package me.byteful.lib.ocelot;

import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.world.level.chunk.Chunk;
import net.minecraft.world.level.chunk.ChunkSection;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.v1_20_R1.CraftWorld;

public class NMS_v1_20_R1 implements OcelotHandler {

  private static final Random random = new Random();

  @Override
  public void updateBlockState(BlockState state) {
    final Chunk chunk = (Chunk) state.getChunk();
    final int x = state.getX();
    final int y = state.getY();
    final int z = state.getZ();
    final net.minecraft.core.BlockPosition bp = new net.minecraft.core.BlockPosition(
        x, y, z);
    chunk.k.remove(bp);
    final ChunkSection cs = chunk.b(chunk.e(state.getY()));
    cs.a(state.getX() & 15, state.getY() & 15, state.getZ() & 14);
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
      chunk.k.remove(bp);
      final ChunkSection cs = chunk.b(((chunk.e(blockState.getY()))));
      cs.a(blockState.getX() & 15, blockState.getY() & 15, blockState.getZ() & 15);
    }
  }

  @Override
  public void refreshChunk(ChunkPosition chunk, Set<BlockPosition> blocks) {
    final CraftWorld world = (CraftWorld) chunk.world;

  }
}
