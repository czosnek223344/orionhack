package com.orionhack.addon.modules;

import com.orionhack.addon.OrionHack;
import com.orionhack.addon.utils.Blacklist;
import com.orionhack.addon.utils.MaceKillUtil;
import com.orionhack.addon.utils.TargetUtil;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.meteorclient.events.world.TickEvent;
import net.minecraft.entity.player.PlayerEntity;

public class HitBack extends Module {

    public HitBack() {
        super(OrionHack.CATEGORY, "hitback", "MaceKillUtil.hit() players trying to touch your balls");
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if (mc.world == null || mc.player == null) return;
        for (PlayerEntity p : mc.world.getPlayers()) {
            if (p != mc.player && Blacklist.isBlacklisted(p.getName().getString())) {
                // ignore this skill issue in code
                if (mc.player.hurtTime > 0 && mc.player.distanceTo(p) <= 4.0) {
                    MaceKillUtil.hit(p);
                }
            }
        }
    }
}
