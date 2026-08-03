package com.orionhack.addon.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class PredictionUtil {
    private static final MinecraftClient mc = MinecraftClient.getInstance();

    // Below this speed we treat the player as stationary (no prediction)
    private static final double MIN_SPEED = 0.05;

    // Hard limit so packet-fly / life-overflow never yeets you across the map
    private static final double MAX_SPEED_CLAMP = 14.0;

    // How much extra we trust vertical motion (people packet-fly up a lot)
    private static final double VERTICAL_BIAS = 0.65;

    public static Vec3d getPredictedPos(PlayerEntity target, double ticks) {
        if (target == null || mc.player == null) return null;

        Vec3d pos = target.getPos();
        Vec3d vel = target.getVelocity();

        double speed = vel.length();

        // Completely still or barely moving → just use current position
        if (speed < MIN_SPEED) {
            return pos;
        }

        // Distance-based latency compensation (farther = more ticks)
        double dist = mc.player.distanceTo(target);
        double latency = MathHelper.clamp(dist * 0.065, 0.0, 5.5);

        // Slight extra when the target is moving toward us (better lead)
        Vec3d toMe = mc.player.getPos().subtract(pos).normalize();
        double closing = vel.normalize().dotProduct(toMe);
        if (closing > 0.35) {
            latency += 0.6;
        }

        double totalTicks = ticks + latency;

        // Clamp insane packet-fly / overflow velocities
        if (speed > MAX_SPEED_CLAMP) {
            vel = vel.normalize().multiply(MAX_SPEED_CLAMP);
        }

        // Separate horizontal / vertical so we can bias Y
        double dx = vel.x * totalTicks;
        double dy = vel.y * totalTicks;
        double dz = vel.z * totalTicks;

        // Extra vertical lead when flying up hard (common on anarchy)
        if (vel.y > 0.25) {
            dy += vel.y * VERTICAL_BIAS * Math.min(totalTicks, 4.0);
        }

        // Tiny gravity compensation when falling so we don't undershoot
        if (vel.y < -0.15) {
            dy -= 0.08 * totalTicks;
        }

        return new Vec3d(
            pos.x + dx,
            pos.y + dy,
            pos.z + dz
        );
    }
}
