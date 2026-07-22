// MaceKillUtil.java
package com.orionhack.addon.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.Hand;

public class MaceKillUtil {
    private static final MinecraftClient mc = MinecraftClient.getInstance();

    private static double MACE_KILL_HEIGHT = 30.0;

    public static void hit(Entity target) {
        if (mc.player == null || target == null) return;

        double x = mc.player.getX();
        double y = mc.player.getY();
        double z = mc.player.getZ();

        mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(
            x, y + MACE_KILL_HEIGHT, z, false, false
        ));

        mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(
            x, y, z, false, false
        ));

        mc.player.networkHandler.sendPacket(PlayerInteractEntityC2SPacket.attack(target, mc.player.isSneaking()));

        mc.player.swingHand(Hand.MAIN_HAND);
        mc.player.networkHandler.sendPacket(new HandSwingC2SPacket(Hand.MAIN_HAND));
    }

    public static void setMaceKillHeight(double height) {
        MACE_KILL_HEIGHT = height;
    }
}
