package me.byteful.lib.ocelot.impl;

import me.byteful.lib.ocelot.BlockPosition;
import me.byteful.lib.ocelot.ChunkPosition;
import me.byteful.lib.ocelot.OcelotHandler;
import net.minecraft.network.protocol.game.ClientboundLevelChunkWithLightPacket;
import net.minecraft.network.protocol.game.PacketPlayOutUnloadChunk;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.chunk.Chunk;
import net.minecraft.world.level.lighting.LightEngine;
import org.bukkit.Bukkit;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.v1_18_R2.CraftChunk;
import org.bukkit.craftbukkit.v1_18_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_18_R2.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.Set;

public class NMS_v1_18_R2 implements OcelotHandler {

  @Override
  public void updateBlockState(BlockState state) {
    final Chunk chunk = ((CraftChunk) state.getChunk()).getHandle();
    final net.minecraft.core.BlockPosition bp = new net.minecraft.core.BlockPosition(state.getX(), state.getY(), state.getZ());
    final IBlockData ibd = ((CraftBlockData) state.getBlockData()).getState();
    chunk.i.remove(bp);
    chunk.a(bp, ibd, false);
    chunk.q.k().a(bp);
  }

  @Override
  public void refreshChunk(ChunkPosition chunk, Set<BlockPosition> blocks) {
    final CraftWorld world = (CraftWorld) chunk.world;
    final LightEngine engine = world.getHandle().l_();
    final CraftChunk chunkAt = (CraftChunk) world.getChunkAt(chunk.x, chunk.z);

    for (BlockPosition pos : blocks) {
      engine.a(new net.minecraft.core.BlockPosition(pos.getX(), pos.getY(), pos.getZ()));
    }

    final PacketPlayOutUnloadChunk unload = new PacketPlayOutUnloadChunk(chunk.x, chunk.z);
    final ClientboundLevelChunkWithLightPacket load = new ClientboundLevelChunkWithLightPacket(chunkAt.getHandle(), engine, null, null, true);

    for (Player p : Bukkit.getOnlinePlayers()) {
      final EntityPlayer ep = ((CraftPlayer) p).getHandle();
      final int dist = Bukkit.getViewDistance() + 1;
      final ChunkPosition playerPos = new ChunkPosition(p.getLocation().getChunk());
      if (chunk.distance(playerPos) > dist) {
        continue;
      }
      ep.b.a(unload);
      ep.b.a(load);
    }
  }
}