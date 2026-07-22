package com.orionhack.addon.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.Vec3d;

public class TPUtil {
    private static final MinecraftClient mc = MinecraftClient.getInstance();

    public static void tpTo(Vec3d target) {
        if (mc.player == null) return;

        Vec3d start = new Vec3d(mc.player.getX(), mc.player.getY(), mc.player.getZ());
        double dx = target.x - start.x;
        double dy = target.y - start.y;
        double dz = target.z - start.z;
        double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);

        if (dist <= 0.1) return;

        int steps = (int) Math.ceil(dist / 8.5);
        double stepX = dx / steps;
        double stepY = dy / steps;
        double stepZ = dz / steps;

        Vec3d current = start;

        for (int i = 0; i < steps; i++) {
            current = current.add(stepX, stepY, stepZ);
            sendPositionPacket(current);
        }

        sendPositionPacket(target);
        mc.player.setPosition(target);
    }

    private static void sendPositionPacket(Vec3d pos) {
        mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(
            pos.x, pos.y, pos.z, mc.player.isOnGround(), false
        ));
    }

    public static void preparePath(Vec3d target) {
        tpTo(target);
    }
}
