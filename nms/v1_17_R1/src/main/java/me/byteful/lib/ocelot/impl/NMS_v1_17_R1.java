package me.byteful.lib.ocelot.impl;

import me.byteful.lib.ocelot.BlockPosition;
import me.byteful.lib.ocelot.ChunkPosition;
import me.byteful.lib.ocelot.OcelotHandler;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.chunk.Chunk;
import net.minecraft.world.level.chunk.ChunkSection;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.v1_17_R1.CraftChunk;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.block.data.CraftBlockData;

import java.util.Set;

public class NMS_v1_17_R1 implements OcelotHandler {

  @Override
  public void updateBlockState(BlockState state) {
    final Chunk chunk = ((CraftChunk) state.getChunk()).getHandle();
    final int y = state.getY();
    final net.minecraft.core.BlockPosition bp = new net.minecraft.core.BlockPosition(state.getX(), y, state.getZ());
    final IBlockData ibd = ((CraftBlockData) state.getBlockData()).getState();
    chunk.l.remove(bp);
    ChunkSection cs = chunk.getSections()[y >> 4];
    if (cs == chunk.a()) {
      cs = new ChunkSection(y >> 4 << 4);
      chunk.getSections()[y >> 4] = cs;
    }
    cs.setType(state.getX() & 15, y & 15, state.getZ() & 15, ibd);
  }

  @Override
  public void refreshChunk(ChunkPosition chunk, Set<BlockPosition> blocks) {
    final CraftWorld world = (CraftWorld) chunk.world;
//    final LightEngine engine = world.getHandle().k_();
    final CraftChunk chunkAt = (CraftChunk) world.getChunkAt(chunk.x, chunk.z);

//    for (BlockPosition pos : blocks) {
//      final net.minecraft.core.BlockPosition bp = new net.minecraft.core.BlockPosition(pos.getX(), pos.getY(), pos.getZ());
//      chunkAt.getHandle().i.getChunkProvider()..a(bp);
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