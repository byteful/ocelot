package me.byteful.lib.ocelot;

import io.papermc.lib.PaperLib;
import me.byteful.lib.ocelot.impl.BukkitHandler;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public final class OcelotAPI {
  private static final OcelotHandler HANDLER;
  private static final Map<ChunkPosition, Set<BlockPosition>> BLOCKS = new HashMap<>();

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
  }

  private static String getNMSVersion() {
    final String v = Bukkit.getServer().getClass().getPackage().getName();

    return v.substring(v.lastIndexOf('.') + 1);
  }

  public static CompletableFuture<Void> updateBlock(final Block block, final Material type) {
    final BlockState state = block.getState();
    state.setType(type);

    return updateBlockState(state);
  }

  public static CompletableFuture<Void> updateBlock(final Block block, final MaterialData data) {
    final BlockState state = block.getState();
    state.setData(data);

    return updateBlockState(state);
  }

  public static CompletableFuture<Void> updateBlock(final Block block, final BlockData data) {
    final BlockState state = block.getState();
    state.setBlockData(data);

    return updateBlockState(state);
  }

  public static CompletableFuture<Void> updateBlocks(final Collection<Block> blocks, final Material type) {
    final Set<BlockState> states = new HashSet<>();
    for (Block block : blocks) {
      final BlockState state = block.getState();
      state.setType(type);
      states.add(state);
    }

    return updateBlockStates(states);
  }

  public static CompletableFuture<Void> updateBlocks(final Collection<Block> blocks, final MaterialData data) {
    final Set<BlockState> states = new HashSet<>();
    for (Block block : blocks) {
      final BlockState state = block.getState();
      state.setData(data);
      states.add(state);
    }

    return updateBlockStates(states);
  }

  public static CompletableFuture<Void> updateBlocks(final Collection<Block> blocks, final BlockData data) {
    final Set<BlockState> states = new HashSet<>();
    for (Block block : blocks) {
      final BlockState state = block.getState();
      state.setBlockData(data);
      states.add(state);
    }

    return updateBlockStates(states);
  }

  public static CompletableFuture<Void> updateBlockStates(final Collection<BlockState> states) {
    final BlockState[] statesArray = states.toArray(new BlockState[0]);
    final CompletableFuture[] array = new CompletableFuture[states.size()];

    for (int i = 0; i < states.size(); i++) {
      array[i] = updateBlockState(statesArray[i]);
    }

    return CompletableFuture.allOf(array);
  }

  public static CompletableFuture<Void> updateBlockState(final BlockState state) {
    return PaperLib.getChunkAtAsync(state.getLocation()).thenAccept(chunk -> {
      Runnable run = () -> {
        HANDLER.updateBlockState(state);
        final ChunkPosition c = new ChunkPosition(chunk);
        if (!BLOCKS.containsKey(c)) {
          BLOCKS.put(c, new HashSet<>());
        }
        BLOCKS.get(c).add(new BlockPosition(state.getBlock()));
      };

      if (Bukkit.isPrimaryThread()) {
        run.run();
      } else {
        Bukkit.getScheduler().runTask(JavaPlugin.getProvidingPlugin(OcelotAPI.class), run);
        System.out.println("[Ocelot] updateBlockState was ran async. Fixing automatically...");
      }
    });
  }

  public static void refreshChunks() {
    BLOCKS.forEach(HANDLER::refreshChunk);
    BLOCKS.clear();
  }
}