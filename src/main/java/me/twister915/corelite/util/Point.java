package me.twister915.corelite.util;

import lombok.Data;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;

@Data
public final class Point {
    private final double x, y, z;
    private final float pitch, yaw;

    public Location in(World world) {
        return new Location(world, x, y, z, yaw, pitch);
    }

    public double distanceSquared(Point point) {
        return Math.pow(x-point.getX(), 2) + Math.pow(y-point.getY(), 2) + Math.pow(z - point.getZ(), 2);
    }

    public double distance(Point point) {
        return Math.sqrt(distanceSquared(point));
    }

    public static Point of(Location location) {
        return new Point(location.getX(), location.getY(), location.getZ(), location.getPitch(), location.getYaw());
    }

    public static Point of(Block block) {
        return new Point(block.getX(), block.getY(), block.getZ(), 0f, 0f);
    }

    public static Point of(Entity entity) {
        return of(entity.getLocation());
    }
}
