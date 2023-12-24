package me.byteful.lib.ocelot.impl;

import java.util.List;
import java.util.Random;
import java.util.Set;
import me.byteful.lib.ocelot.BlockPosition;
import me.byteful.lib.ocelot.ChunkPosition;
import me.byteful.lib.ocelot.OcelotHandler;
import net.minecraft.world.level.chunk.Chunk;
import net.minecraft.world.level.chunk.ChunkSection;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.lighting.LightEngine;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.v1_19_R3.CraftChunk;
import org.bukkit.craftbukkit.v1_19_R3.CraftWorld;


public class NMS_v1_19_R3 implements OcelotHandler {

  private static final Random random = new Random();

  @Override
  public void updateBlockState(BlockState state) {
    final Chunk chunk = (Chunk) state.getChunk();
    final int x = state.getX();
    final int y = state.getY();
    final int z = state.getZ();
    final net.minecraft.core.BlockPosition bp = new net.minecraft.core.BlockPosition(
        x, y, z);
    chunk.i.remove(bp);
    final ChunkSection cs = chunk.b(chunk.e(state.getY()));
    cs.a(state.getX() & 15, state.getY() & 15, state.getZ() & 15);
  }

  @Override
  public void updateBlockState(List<BlockState> states) {
    if (states != null && !states.isEmpty()) {
      BlockState blockState = states.get(random.nextInt(states.size()));
      final Chunk chunk = (Chunk) blockState.getChunk();
      final int x = blockState.getX();
      final int y = blockState.getY();
      final int z = blockState.getZ();
      final net.minecraft.core.BlockPosition bp = new net.minecraft.core.BlockPosition(
          x, y, z);
      chunk.i.remove(bp);
      final ChunkSection cs = chunk.b((chunk.e(blockState.getY())));
      cs.a(blockState.getX() & 15, blockState.getY() & 15, blockState.getZ() & 15);
    }
  }

  @Override
  public void refreshChunk(ChunkPosition chunk, Set<BlockPosition> blocks) {
    final CraftWorld world = (CraftWorld) chunk.world;
    final LightEngine lightEngine = world.getHandle().l_();

    for (BlockPosition blockPosition : blocks) {
      final CraftChunk chunkAt = (CraftChunk) world.getChunkAt(blockPosition.getX(), blockPosition.getZ());

      final net.minecraft.core.BlockPosition bp = new net.minecraft.core.BlockPosition(
          blockPosition.getX(), blockPosition.getY(), blockPosition.getZ());
      chunkAt.getHandle(ChunkStatus.c).a();
      lightEngine.a(bp);
    }
  }
}
