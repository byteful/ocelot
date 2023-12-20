package me.byteful.lib.ocelot.impl;

import java.util.Set;
import me.byteful.lib.ocelot.BlockPosition;
import me.byteful.lib.ocelot.ChunkPosition;
import me.byteful.lib.ocelot.OcelotHandler;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.chunk.Chunk;
import net.minecraft.world.level.chunk.ChunkSection;
import net.minecraft.world.level.lighting.LightEngine;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.v1_19_R1.CraftChunk;
import org.bukkit.craftbukkit.v1_19_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R1.block.data.CraftBlockData;

public class NMS_v1_19_R2 implements OcelotHandler {

  @Override
  public void updateBlockState(BlockState state) {
    final Chunk chunk = ((CraftChunk) state.getChunk()).getHandle();
    final net.minecraft.core.BlockPosition blockPosition = new net.minecraft.core.BlockPosition(
        state.getX(), state.getY(), state.getZ());
    final IBlockData iBlockData = ((CraftBlockData) state.getBlockData()).getState();
    chunk.i.remove(blockPosition);
    final ChunkSection chunkSection = chunk.b(chunk.e(state.getY()));
    chunkSection.a(state.getX() & 15, state.getY() & 15, state.getZ() & 15, iBlockData);
  }

  @Override
  public void refreshChunk(ChunkPosition chunk, Set<BlockPosition> blocks) {
    final CraftWorld world = (CraftWorld) chunk.world;
    final LightEngine engine = world.getHandle().l_();
    final CraftChunk chunkAt = (CraftChunk) world.getChunkAt(chunk.x, chunk.z);

    for (BlockPosition blockPosition : blocks) {
      final net.minecraft.core.BlockPosition bp = new net.minecraft.core.BlockPosition(
          blockPosition.getX(), blockPosition.getY(), blockPosition.getZ());
      chunkAt.getHandle().q.k().a(bp);
      engine.a(bp);
    }
  }
}