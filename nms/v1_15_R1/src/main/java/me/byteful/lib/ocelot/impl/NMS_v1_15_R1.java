package me.byteful.lib.ocelot.impl;

import java.util.List;
import java.util.Random;
import me.byteful.lib.ocelot.BlockPosition;
import me.byteful.lib.ocelot.ChunkPosition;
import me.byteful.lib.ocelot.OcelotHandler;
import net.minecraft.server.v1_15_R1.Chunk;
import net.minecraft.server.v1_15_R1.ChunkSection;
import net.minecraft.server.v1_15_R1.IBlockData;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.v1_15_R1.CraftChunk;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_15_R1.block.data.CraftBlockData;

import java.util.Set;

public class NMS_v1_15_R1 implements OcelotHandler {

  private static final Random random = new Random();

  @Override
  public void updateBlockState(BlockState state) {
    final Chunk chunk = ((CraftChunk) state.getChunk()).getHandle();
    final int y = state.getY();
    final net.minecraft.server.v1_15_R1.BlockPosition bp = new net.minecraft.server.v1_15_R1.BlockPosition(state.getX(), y, state.getZ());
    final IBlockData ibd = ((CraftBlockData) state.getBlockData()).getState();
    chunk.tileEntities.remove(bp);
    ChunkSection cs = chunk.getSections()[y >> 4];
    if (cs == chunk.a()) {
      cs = new ChunkSection(y >> 4 << 4);
      chunk.getSections()[y >> 4] = cs;
    }
    cs.setType(state.getX() & 15, y & 15, state.getZ() & 15, ibd);
  }

  @Override
  public void updateBlockState(List<BlockState> states) {
    if (states != null && !states.isEmpty()) {
      BlockState blockState = states.get(random.nextInt(states.size()));
      final Chunk chunk = ((CraftChunk) blockState.getChunk()).getHandle();
      final int x = blockState.getX();
      final int y = blockState.getY();
      final int z = blockState.getZ();
      final net.minecraft.server.v1_15_R1.BlockPosition bp = new net.minecraft.server.v1_15_R1.BlockPosition(
          x, y, z);
      final IBlockData ibd = ((CraftBlockData) blockState.getBlockData()).getState();
      chunk.tileEntities.remove(bp);
      ChunkSection cs = chunk.getSections()[y >> 4];
      if (cs == chunk.a()) {
        cs = new ChunkSection(y >> 4 << 4);
        chunk.getSections()[y >> 4] = cs;
      }
      cs.setType(blockState.getX() & 15, blockState.getY() % 15, blockState.getZ() % 15, ibd);
    }
  }

  @Override
  public void refreshChunk(ChunkPosition chunk, Set<BlockPosition> blocks) {
    final CraftWorld world = (CraftWorld) chunk.world;
//    final LightEngine engine = world.getHandle().l_();
    final CraftChunk chunkAt = (CraftChunk) world.getChunkAt(chunk.x, chunk.z);

//    for (BlockPosition pos : blocks) {
//      final net.minecraft.core.BlockPosition bp = new net.minecraft.core.BlockPosition(pos.getX(), pos.getY(), pos.getZ());
//      chunkAt.getHandle().q.k().a(bp);
//      engine.a(bp);
//    }

//    final PacketPlayOutUnloadChunk unload = new PacketPlayOutUnloadChunk(chunk.x, chunk.z);
//
//    for (Player p : Bukkit.getOnlinePlayers()) {
//      final EntityPlayer ep = ((CraftPlayer) p).getHandle();
//      final int dist = Bukkit.getViewDistance() + 1;
//      final ChunkPosition playerPos = new ChunkPosition(p.getLocation().getChunk());
//      if (chunk.distance(playerPos) > dist) {
//        continue;
//      }
//      ep.b.a(unload);
//    }
    world.refreshChunk(chunk.x, chunk.z);
  }
}