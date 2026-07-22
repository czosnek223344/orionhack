package com.orionhack.addon.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.util.Hand;
import meteordevelopment.meteorclient.systems.modules.Modules;
import com.orionhack.addon.modules.Settings;
import com.orionhack.addon.utils.TpUtil;

public class MaceKillUtil {
    private static final MinecraftClient mc = MinecraftClient.getInstance();

    private static double MACE_KILL_HEIGHT = 30.0;

    public static void hit(Entity target) {
        if (mc.player == null || target == null) return;

        double x = mc.player.getX();
        double y = mc.player.getY();
        double z = mc.player.getZ();

        double finalHeight = MACE_KILL_HEIGHT;
        Settings settingsModule = Modules.get().get(Settings.class);
        
        if (settingsModule != null && settingsModule.isActive()) {
            finalHeight = settingsModule.maceKillHeight.get();
        }

        TpUtil.teleport(x, y + finalHeight, z);
        
        TpUtil.teleport(x, y, z);

        mc.player.networkHandler.sendPacket(PlayerInteractEntityC2SPacket.attack(target, mc.player.isSneaking()));

        mc.player.swingHand(Hand.MAIN_HAND);
        mc.player.networkHandler.sendPacket(new HandSwingC2SPacket(Hand.MAIN_HAND));
    }

    public static void setMaceKillHeight(double height) {
        MACE_KILL_HEIGHT = height;
    }
}
