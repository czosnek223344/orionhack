package com.orionhack.addon.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

public class PredictionUtil {
    private static final MinecraftClient mc = MinecraftClient.getInstance();

    public static Vec3d getPredictedPos(PlayerEntity target, double ticks) {
        if (target == null) return null;
        Vec3d velocity = target.getVelocity();
        Vec3d currentPos = new Vec3d(target.getX(), target.getY(), target.getZ());
        return currentPos.add(velocity.multiply(ticks));
    }
}
