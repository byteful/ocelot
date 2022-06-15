package me.byteful.lib.ocelot.impl;

import me.byteful.lib.ocelot.BlockPosition;
import me.byteful.lib.ocelot.ChunkPosition;
import me.byteful.lib.ocelot.OcelotHandler;
import org.bukkit.block.BlockState;

import java.util.Set;

public class BukkitHandler implements OcelotHandler {
  @Override
  public void updateBlockState(BlockState state) {
    state.update(true, false);
  }

  @Override
  public void refreshChunk(ChunkPosition chunk, Set<BlockPosition> blocks) {

  }
}