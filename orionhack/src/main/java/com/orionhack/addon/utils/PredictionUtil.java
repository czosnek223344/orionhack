package com.orionhack.addon.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

public class PredictionUtil {
    private static final MinecraftClient mc = MinecraftClient.getInstance();

    public static Vec3d getPredictedPos(PlayerEntity target, double ticks) {
        if (target == null || mc.player == null) return null;

        double deltaX = target.getX() - target.prevX;
        double deltaY = target.getY() - target.prevY;
        double deltaZ = target.getZ() - target.prevZ;

        double distance = mc.player.squaredDistanceTo(target);
        double latencyComp = Math.sqrt(distance) * 0.12;
        double totalTicks = ticks + latencyComp;

        double speed = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);

        if (speed > 0.0) {
            double maxDesyncClamp = 8.0;
            if (speed > maxDesyncClamp) {
                double scale = maxDesyncClamp / speed;
                deltaX *= scale;
                deltaY *= scale;
                deltaZ *= scale;
            }
        }

        double predictedX = target.getX() + (deltaX * totalTicks);
        double predictedY = target.getY() + (deltaY * totalTicks);
        double predictedZ = target.getZ() + (deltaZ * totalTicks);

        return new Vec3d(predictedX, predictedY, predictedZ);
    }
}
