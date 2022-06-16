package me.byteful.lib.ocelot.impl;

import me.byteful.lib.ocelot.BlockPosition;
import me.byteful.lib.ocelot.ChunkPosition;
import me.byteful.lib.ocelot.OcelotHandler;
import net.minecraft.server.v1_13_R2.Chunk;
import net.minecraft.server.v1_13_R2.ChunkSection;
import net.minecraft.server.v1_13_R2.IBlockData;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.v1_13_R2.CraftChunk;
import org.bukkit.craftbukkit.v1_13_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_13_R2.block.data.CraftBlockData;

import java.util.Set;

public class NMS_v1_13_R2 implements OcelotHandler {

  @Override
  public void updateBlockState(BlockState state) {
    final Chunk chunk = ((CraftChunk) state.getChunk()).getHandle();
    final int y = state.getY();
    final net.minecraft.server.v1_13_R2.BlockPosition bp = new net.minecraft.server.v1_13_R2.BlockPosition(state.getX(), y, state.getZ());
    final IBlockData ibd = ((CraftBlockData) state.getBlockData()).getState();
    chunk.tileEntities.remove(bp);
    ChunkSection cs = chunk.getSections()[y >> 4];
    if (cs == chunk.a()) {
      cs = new ChunkSection(y >> 4 << 4, chunk.world.worldProvider.g());
      chunk.getSections()[y >> 4] = cs;
    }
    cs.setType(state.getX() & 15, y & 15, state.getZ() & 15, ibd);
  }

  @Override
  public void refreshChunk(ChunkPosition chunk, Set<BlockPosition> blocks) {
    final CraftWorld world = (CraftWorld) chunk.world;
    final CraftChunk chunkAt = (CraftChunk) world.getChunkAt(chunk.x, chunk.z);

    chunkAt.getHandle().initLighting();

//    for (BlockPosition pos : blocks) {
//      final net.minecraft.server.v1_13_R2.BlockPosition bp = new net.minecraft.server.v1_13_R2.BlockPosition(pos.getX(), pos.getY(), pos.getZ());
//      chunkAt.getHandle().getType(bp).
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