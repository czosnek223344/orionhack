package com.orionhack.addon.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.Vec3d;

public class TPUtil {
    private static final MinecraftClient mc = MinecraftClient.getInstance();
    private static final double MAX_STEP = 10.0;

    public static void tpTo(Vec3d target) {
        if (mc.player == null || mc.getNetworkHandler() == null) return;

        Vec3d start = new Vec3d(mc.player.getX(), mc.player.getY(), mc.player.getZ());
        double dist = start.distanceTo(target);
        if (dist < 0.05) return;

        int steps = (int) Math.ceil(dist / MAX_STEP);
        double inv = 1.0 / steps;

        for (int i = 1; i <= steps; i++) {
            double t = i * inv;
            Vec3d pos = i == steps ? target : start.lerp(target, t);
            sendPos(pos);
        }

        mc.player.setPosition(target);
        mc.player.setVelocity(Vec3d.ZERO);
    }

    public static void tpTo(Vec3d target, float yaw, float pitch) {
        if (mc.player == null || mc.getNetworkHandler() == null) return;

        Vec3d start = new Vec3d(mc.player.getX(), mc.player.getY(), mc.player.getZ());
        double dist = start.distanceTo(target);

        if (dist < 0.05) {
            sendFull(target, yaw, pitch);
            mc.player.setPosition(target);
            mc.player.setVelocity(Vec3d.ZERO);
            return;
        }

        int steps = (int) Math.ceil(dist / MAX_STEP);
        double inv = 1.0 / steps;

        for (int i = 1; i <= steps; i++) {
            double t = i * inv;
            Vec3d pos = i == steps ? target : start.lerp(target, t);
            sendFull(pos, yaw, pitch);
        }

        mc.player.setPosition(target);
        mc.player.setVelocity(Vec3d.ZERO);
    }

    public static void tpDirect(Vec3d target) {
        if (mc.player == null || mc.getNetworkHandler() == null) return;
        sendPos(target);
        mc.player.setPosition(target);
        mc.player.setVelocity(Vec3d.ZERO);
    }

    private static void sendPos(Vec3d pos) {
        mc.getNetworkHandler().sendPacket(
            new PlayerMoveC2SPacket.PositionAndOnGround(
                pos.x, pos.y, pos.z,
                mc.player.isOnGround(),
                false
            )
        );
    }

    private static void sendFull(Vec3d pos, float yaw, float pitch) {
        mc.getNetworkHandler().sendPacket(
            new PlayerMoveC2SPacket.Full(
                pos.x, pos.y, pos.z,
                yaw, pitch,
                mc.player.isOnGround(),
                false
            )
        );
    }

    public static void preparePath(Vec3d target) {
        tpTo(target);
    }
}
