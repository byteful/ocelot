package me.byteful.lib.ocelot;

import java.util.List;
import org.bukkit.block.BlockState;

import java.util.Set;

public interface OcelotHandler {
  void updateBlockState(final BlockState state);
  void updateBlockState(final List<BlockState> states);
  void refreshChunk(final ChunkPosition chunk, final Set<BlockPosition> blocks);
}