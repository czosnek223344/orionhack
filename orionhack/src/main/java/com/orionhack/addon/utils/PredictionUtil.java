package com.orionhack.addon.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class PredictionUtil {
    private static final MinecraftClient mc = MinecraftClient.getInstance();

    private static final double MIN_SPEED = 0.05;
    private static final double MAX_SPEED_CLAMP = 14.0;
    private static final double VERTICAL_BIAS = 0.65;

    public static Vec3d getPredictedPos(PlayerEntity target, double ticks) {
        if (target == null || mc.player == null) return null;

        double tx = target.getX();
        double ty = target.getY();
        double tz = target.getZ();

        Vec3d vel = target.getVelocity();
        double speed = vel.length();

        // Almost still → no prediction
        if (speed < MIN_SPEED) {
            return new Vec3d(tx, ty, tz);
        }

        // Distance + closing latency
        double dist = mc.player.distanceTo(target);
        double latency = MathHelper.clamp(dist * 0.065, 0.0, 5.5);

        double dxToMe = mc.player.getX() - tx;
        double dyToMe = mc.player.getY() - ty;
        double dzToMe = mc.player.getZ() - tz;
        double len = Math.sqrt(dxToMe * dxToMe + dyToMe * dyToMe + dzToMe * dzToMe);
        if (len > 0.001) {
            double nx = dxToMe / len;
            double ny = dyToMe / len;
            double nz = dzToMe / len;
            double closing = (vel.x * nx + vel.y * ny + vel.z * nz);
            if (closing > 0.35) latency += 0.6;
        }

        double totalTicks = ticks + latency;

        // Clamp packet-fly / overflow speeds
        if (speed > MAX_SPEED_CLAMP) {
            double scale = MAX_SPEED_CLAMP / speed;
            vel = new Vec3d(vel.x * scale, vel.y * scale, vel.z * scale);
        }

        double dx = vel.x * totalTicks;
        double dy = vel.y * totalTicks;
        double dz = vel.z * totalTicks;

        // Vertical bias for rising packet-fly
        if (vel.y > 0.25) {
            dy += vel.y * VERTICAL_BIAS * Math.min(totalTicks, 4.0);
        }

        // Small gravity compensation when falling
        if (vel.y < -0.15) {
            dy -= 0.08 * totalTicks;
        }

        return new Vec3d(tx + dx, ty + dy, tz + dz);
    }
}
