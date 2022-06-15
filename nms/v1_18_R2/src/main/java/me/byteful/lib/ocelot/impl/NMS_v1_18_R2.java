package me.byteful.lib.ocelot.impl;

import me.byteful.lib.ocelot.BlockPosition;
import me.byteful.lib.ocelot.ChunkPosition;
import me.byteful.lib.ocelot.OcelotHandler;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.chunk.Chunk;
import net.minecraft.world.level.chunk.ChunkSection;
import net.minecraft.world.level.lighting.LightEngine;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.v1_18_R2.CraftChunk;
import org.bukkit.craftbukkit.v1_18_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_18_R2.block.data.CraftBlockData;

import java.util.Set;

public class NMS_v1_18_R2 implements OcelotHandler {

  @Override
  public void updateBlockState(BlockState state) {
    final Chunk chunk = ((CraftChunk) state.getChunk()).getHandle();
    final net.minecraft.core.BlockPosition bp = new net.minecraft.core.BlockPosition(state.getX(), state.getY(), state.getZ());
    final IBlockData ibd = ((CraftBlockData) state.getBlockData()).getState();
    chunk.i.remove(bp);
    final ChunkSection cs = chunk.b(chunk.e(state.getY()));
    cs.a(state.getX() & 15, state.getY() & 15, state.getZ() & 15, ibd);
    //chunk.q.k().a(bp);
  }

  @Override
  public void refreshChunk(ChunkPosition chunk, Set<BlockPosition> blocks) {
    final CraftWorld world = (CraftWorld) chunk.world;
    final LightEngine engine = world.getHandle().l_();
    final CraftChunk chunkAt = (CraftChunk) world.getChunkAt(chunk.x, chunk.z);

    for (BlockPosition pos : blocks) {
      final net.minecraft.core.BlockPosition bp = new net.minecraft.core.BlockPosition(pos.getX(), pos.getY(), pos.getZ());
      chunkAt.getHandle().q.k().a(bp);
      engine.a(bp);
    }

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