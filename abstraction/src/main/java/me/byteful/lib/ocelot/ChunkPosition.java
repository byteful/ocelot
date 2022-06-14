package me.byteful.lib.ocelot;

import org.bukkit.Chunk;
import org.bukkit.World;

import java.util.Objects;

public final class ChunkPosition {
  public World world;
  public int x;
  public int z;

  public ChunkPosition(World world, int x, int z) {
    this.world = world;
    this.x = x;
    this.z = z;
  }

  public ChunkPosition(Chunk chunk) {
    this(chunk.getWorld(), chunk.getX(), chunk.getZ());
  }

  public Chunk getChunk() {
    return world.getChunkAt(x, z);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof ChunkPosition)) return false;
    ChunkPosition that = (ChunkPosition) o;
    return x == that.x && z == that.z && world.equals(that.world);
  }

  public int distance(ChunkPosition other) {
    return Math.max(Math.abs(other.x - x), Math.abs(other.z - z));
  }

  @Override
  public int hashCode() {
    return Objects.hash(world, x, z);
  }

  @Override
  public String toString() {
    return "ChunkPosition{" +
      "world=" + world +
      ", x=" + x +
      ", z=" + z +
      '}';
  }
}