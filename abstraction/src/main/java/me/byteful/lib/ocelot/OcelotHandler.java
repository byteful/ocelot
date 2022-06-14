package me.byteful.lib.ocelot;

import org.bukkit.block.BlockState;

import java.util.Set;

public interface OcelotHandler {
  void updateBlockState(final BlockState state);

  void refreshChunk(final ChunkPosition chunk, final Set<BlockPosition> blocks);
}