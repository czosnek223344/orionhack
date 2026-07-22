package com.orionhack.addon.utils;

import com.orionhack.addon.modules.Settings;
import net.minecraft.entity.Entity;
import net.minecraft.util.Hand;
import static meteordevelopment.meteorclient.MeteorClient.mc;

public class MaceKillUtil {

    public static void executeMaceKill(Entity target) {
        if (!Settings.maceKillEnabled) {
            return;
        }

        if (mc.player == null || target == null) {
            return;
        }

        double pX = mc.player.getX();
        double pY = mc.player.getY();
        double pZ = mc.player.getZ();

        double tX = target.getX();
        double tY = target.getY();
        double tZ = target.getZ();

        double tpHeight = Settings.maceHeightAmount;

        TpUtil.teleport(pX, pY + tpHeight, pZ);

        mc.player.fallDistance = (float) tpHeight;

        TpUtil.teleport(tX, tY, tZ);

        mc.interactionManager.attackEntity(mc.player, target);
        mc.player.swingHand(Hand.MAIN_HAND);
    }
}
