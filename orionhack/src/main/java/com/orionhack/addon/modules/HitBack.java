package com.orionhack.addon.modules;

import com.orionhack.addon.OrionHack;
import com.orionhack.addon.utils.Blacklist;
import com.orionhack.addon.utils.MaceKillUtil;
import com.orionhack.addon.utils.PredictionUtil;
import com.orionhack.addon.utils.TPUtil;
import meteordevelopment.meteorclient.events.entity.EntityDamageEvent;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;

public class HitBack extends Module {

    public HitBack() {
        super(OrionHack.CATEGORY, "hitback", "MaceKillUtil.hit() players trying to touch your balls");
    }

    @EventHandler
    private void onDamage(EntityDamageEvent event) {
        if (mc.player == null || event.entity != mc.player) return;

        Entity attacker = event.source.getAttacker();
        if (attacker == null) return;

        boolean isBlacklistedPlayer = attacker instanceof PlayerEntity p && Blacklist.isBlacklisted(p.getName().getString());
        boolean isMob = attacker instanceof MobEntity;

        if (isBlacklistedPlayer || isMob) {
            attackBack(attacker);
        }
    }

    private void attackBack(Entity target) {
        if (target == null) return;

        var targetPos = PredictionUtil.getPredictedPos(target, 2.0);

        TPUtil.tpTo(targetPos);
        MaceKillUtil.hit(target);
    }
}
