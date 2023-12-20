package me.byteful.lib.ocelot;

import io.papermc.lib.PaperLib;
import me.byteful.lib.ocelot.bucket.Bucket;
import me.byteful.lib.ocelot.bucket.BucketPartition;
import me.byteful.lib.ocelot.bucket.factory.BucketFactory;
import me.byteful.lib.ocelot.bucket.partitioning.PartitioningStrategies;
import me.byteful.lib.ocelot.impl.BukkitHandler;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class OcelotAPI {
  private static final OcelotHandler HANDLER;
  private static final Map<ChunkPosition, Set<BlockPosition>> BLOCKS = new HashMap<>();
  private static final Bucket<BlockState> BUCKET = BucketFactory.newHashSetBucket(20, PartitioningStrategies.lowestSize());
  private static final int MID_VERSION = getMidVersion();

  static {
    // Load OcelotHandler for whatever NMS version is available. If no NMS support is found, use BukkitHandler.

    Bukkit.getLogger().info("[Ocelot] Detected NMS version: " + getNMSVersion());

    OcelotHandler localHandler;
    try {
      Class<?> clazz = Class.forName("me.byteful.lib.ocelot.impl.NMS_" + getNMSVersion());
      localHandler = clazz.asSubclass(OcelotHandler.class).getConstructor().newInstance();
    } catch (Exception e) {
      Bukkit.getLogger().warning("[Ocelot] NMS version " + getNMSVersion() + " is not supported by Ocelot. Defaulting to Bukkit methods...");
      localHandler = new BukkitHandler();
    }
    HANDLER = localHandler;

    Bukkit.getScheduler().scheduleSyncRepeatingTask(JavaPlugin.getProvidingPlugin(OcelotAPI.class), () -> {
      final BucketPartition<BlockState> part = BUCKET.asCycle().next();
      for (BlockState state : part) {
        HANDLER.updateBlockState(state);
        final ChunkPosition c = new ChunkPosition(state.getChunk());
        if (!BLOCKS.containsKey(c)) {
          BLOCKS.put(c, new HashSet<>());
        }
        BLOCKS.get(c).add(new BlockPosition(state.getBlock()));
      }
    }, 1L, 1L);
  }

  private static String getNMSVersion() {
    final String v = Bukkit.getServer().getClass().getPackage().getName();

    return v.substring(v.lastIndexOf('.') + 1);
  }

  private static int getMidVersion() {
    Pattern pattern = Pattern.compile("1\\.([0-9]+)");
    Matcher matcher = pattern.matcher(Bukkit.getBukkitVersion());
    matcher.find();
    return Integer.parseInt(matcher.group(1));
  }

  private static int getMinHeight(World world) {
    if (MID_VERSION >= 17) {
      return world.getMinHeight();
    }

    return 0;
  }

  private static boolean isInBounds(Block block) {
    if (block == null) {
      return false;
    }
    final int y = block.getLocation().getBlockY();

    return y >= getMinHeight(block.getWorld()) && y <= block.getWorld().getMaxHeight();
  }

  public static CompletableFuture<Void> updateBlock(final Block block, final Material type) {
    if (!isInBounds(block)) {
      throw new IllegalArgumentException("Block has to be in bounds! (Y: " + block.getLocation().getBlockY() + ")");
    }
    final BlockState state = block.getState();
    state.setType(type);

    return updateBlockState(state);
  }

  public static CompletableFuture<Void> updateBlock(final Block block, final MaterialData data) {
    if (!isInBounds(block)) {
      throw new IllegalArgumentException("Block has to be in bounds! (Y: " + block.getLocation().getBlockY() + ")");
    }
    final BlockState state = block.getState();
    state.setData(data);

    return updateBlockState(state);
  }

  public static CompletableFuture<Void> updateBlock(final Block block, final BlockData data) {
    if (!isInBounds(block)) {
      throw new IllegalArgumentException("Block has to be in bounds! (Y: " + block.getLocation().getBlockY() + ")");
    }
    final BlockState state = block.getState();
    state.setBlockData(data);

    return updateBlockState(state);
  }

  public static CompletableFuture<Void> updateBlocks(final Collection<Block> blocks, final Material type, boolean fast) {
    final Set<BlockState> states = new HashSet<>();
    for (Block block : blocks) {
      if (!isInBounds(block)) {
        continue;
      }
      final BlockState state = block.getState();
      state.setType(type);
      states.add(state);
    }

    return updateBlockStates(states, fast);
  }

  public static CompletableFuture<Void> updateBlocks(final Collection<Block> blocks, final MaterialData data, boolean fast) {
    final Set<BlockState> states = new HashSet<>();
    for (Block block : blocks) {
      if (!isInBounds(block)) {
        continue;
      }
      final BlockState state = block.getState();
      state.setData(data);
      states.add(state);
    }

    return updateBlockStates(states, fast);
  }

  public static CompletableFuture<Void> updateBlocks(final Collection<Block> blocks, final BlockData data, boolean fast) {
    final Set<BlockState> states = new HashSet<>();
    for (Block block : blocks) {
      if (!isInBounds(block)) {
        continue;
      }
      final BlockState state = block.getState();
      state.setBlockData(data);
      states.add(state);
    }

    return updateBlockStates(states, fast);
  }

  public static CompletableFuture<Void> updateBlockStates(final Collection<BlockState> states, boolean fast) {
    final Set<CompletableFuture<Void>> futures = new HashSet<>();

    if (states.size() <= 20) {
      states.forEach(state -> futures.add(updateBlockState(state)));

      return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    }

    final CompletableFuture<Void> future = new CompletableFuture<>();
    final Bucket<BlockState> bucket = BucketFactory.newHashSetBucket(20, PartitioningStrategies.lowestSize());
    bucket.addAll(states.stream().filter(state -> state != null && isInBounds(state.getBlock())).collect(Collectors.toSet()));

    if (fast) {
      bucket.forEach(blockState -> {
        //        HANDLER.updateBlockState(state);
        HANDLER.updateBlockState(blockState);
      });
    }
    new BukkitRunnable() {
      long blockCounter = 0L;

      @Override
      public void run() {
        if (bucket.isEmpty() || blockCounter >= states.size()) {
          bucket.clear();
          cancel();
          CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new)).thenRun(() -> future.complete(null));

          return;
        }

        final BucketPartition<BlockState> part = bucket.asCycle().next();
        for (BlockState state : part) {
          futures.add(updateBlockState(state));
          blockCounter++;
        }
      }
    }.runTaskTimer(JavaPlugin.getProvidingPlugin(OcelotAPI.class), 1L, 1L);

    return future;
//    final BlockState[] statesArray = states.toArray(new BlockState[0]);
//    final CompletableFuture[] array = new CompletableFuture[states.size()];
//
//    for (int i = 0; i < states.size(); i++) {
//      array[i] = updateBlockState(statesArray[i]);
//    }
//
//    return CompletableFuture.allOf(array);
  }

  public static CompletableFuture<Void> updateBlockState(final BlockState state) {
    if (!isInBounds(state.getBlock())) {
      throw new IllegalArgumentException("Block has to be in bounds! (Y: " + state.getY() + ")");
    }
    final CompletableFuture<Void> future = new CompletableFuture<>();
    PaperLib.getChunkAtAsync(state.getLocation()).thenAccept(chunk -> {
      Runnable run = () -> {
        HANDLER.updateBlockState(state);
        final ChunkPosition c = new ChunkPosition(chunk);
        if (!BLOCKS.containsKey(c)) {
          BLOCKS.put(c, new HashSet<>());
        }
        BLOCKS.get(c).add(new BlockPosition(state.getBlock()));
        future.complete(null);
      };

      if (Bukkit.isPrimaryThread()) {
        run.run();
      } else {
        Bukkit.getScheduler().runTask(JavaPlugin.getProvidingPlugin(OcelotAPI.class), run);
      }
    });

    return future;
  }

  public static CompletableFuture<Void> refreshChunks() {
    final int total = BLOCKS.size();

    if (total <= 20) {
      return CompletableFuture.runAsync(() -> {
        BLOCKS.forEach(HANDLER::refreshChunk);
        BLOCKS.clear();
      });
    }

    final Bucket<Map.Entry<ChunkPosition, Set<BlockPosition>>> bucket = BucketFactory.newHashSetBucket(20, PartitioningStrategies.lowestSize());
    bucket.addAll(BLOCKS.entrySet());
    BLOCKS.clear();

    final CompletableFuture<Void> future = new CompletableFuture<>();

    new BukkitRunnable() {
      long chunkCounter = 0L;

      @Override
      public void run() {
        if (bucket.isEmpty() || chunkCounter >= total) {
          bucket.clear();
          cancel();
          future.complete(null);

          return;
        }

        final BucketPartition<Map.Entry<ChunkPosition, Set<BlockPosition>>> part = bucket.asCycle().next();
        for (Map.Entry<ChunkPosition, Set<BlockPosition>> data : part) {
          HANDLER.refreshChunk(data.getKey(), data.getValue());
          chunkCounter++;
        }
      }
    }.runTaskTimer(JavaPlugin.getProvidingPlugin(OcelotAPI.class), 1L, 1L);

    return future;
  }
}