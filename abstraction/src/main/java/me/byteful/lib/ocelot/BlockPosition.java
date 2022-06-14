package me.byteful.lib.ocelot;

import org.bukkit.Location;
import org.bukkit.block.Block;

import java.util.Objects;

public final class BlockPosition {
  private final int x, y, z;

  public BlockPosition(int x, int y, int z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }

  public BlockPosition(Block block) {
    final Location l = block.getLocation();
    this.x = l.getBlockX();
    this.y = l.getBlockY();
    this.z = l.getBlockZ();
  }

  public int getX() {
    return x;
  }

  public int getY() {
    return y;
  }

  public int getZ() {
    return z;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    BlockPosition that = (BlockPosition) o;
    return x == that.x && y == that.y && z == that.z;
  }

  @Override
  public int hashCode() {
    return Objects.hash(x, y, z);
  }

  @Override
  public String toString() {
    return "BlockPosition{" +
      "x=" + x +
      ", y=" + y +
      ", z=" + z +
      '}';
  }
}