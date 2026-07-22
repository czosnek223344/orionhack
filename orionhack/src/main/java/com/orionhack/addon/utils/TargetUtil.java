// TargetUtil.java
package com.orionhack.addon.utils;

import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import java.util.List;
import java.util.stream.Collectors;

public class TargetUtil {
    private static final MinecraftClient mc = MinecraftClient.getInstance();

    public static PlayerEntity getClosestBlacklisted(double maxRange) {
        if (mc.world == null || mc.player == null) return null;

        PlayerEntity closest = null;
        double closestDist = Double.MAX_VALUE;

        List<PlayerEntity> blacklistedPlayers = mc.world.getPlayers().stream()
            .filter(p -> Blacklist.isBlacklisted(p.getName().getString()) && p != mc.player)
            .collect(Collectors.toList());

        for (PlayerEntity p : blacklistedPlayers) {
            if (!PlayerUtils.canSeeEntity(p)) continue;

            double dist = mc.player.squaredDistanceTo(p);

            if (dist < closestDist && Math.sqrt(dist) <= maxRange) {
                closestDist = dist;
                closest = p;
            }
        }
        return closest;
    }
}
