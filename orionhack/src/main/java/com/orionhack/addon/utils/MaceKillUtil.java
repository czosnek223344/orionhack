package com.orionhack.addon.modules;

import com.orionhack.addon.OrionHack;
import com.orionhack.addon.utils.Blacklist;
import com.orionhack.addon.utils.MaceKillUtil;
import com.orionhack.addon.utils.PredictionUtil;
import com.orionhack.addon.utils.TPUtil;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

public class HitBack extends Module {

    private Entity lastAttacker = null;

    public HitBack() {
        super(OrionHack.CATEGORY, "hitback", "MaceKillUtil.hit() players trying to touch your balls");
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if (mc.player == null || mc.world == null) return;

        if (mc.player.hurtTime > 0) {
            Entity attacker = mc.player.getAttacker();
            if (attacker != null && attacker != lastAttacker) {
                boolean isBlacklistedPlayer = attacker instanceof PlayerEntity p && Blacklist.isBlacklisted(p.getName().getString());
                boolean isMob = attacker instanceof MobEntity;

                if (isBlacklistedPlayer || isMob) {
                    attackBack(attacker);
                    lastAttacker = attacker;
                }
            }
        } else {
            lastAttacker = null;
        }
    }

    private void attackBack(Entity target) {
        if (target == null) return;

        Vec3d targetPos;
        if (target instanceof PlayerEntity player) {
            targetPos = PredictionUtil.getPredictedPos(player, 2.0);
        } else {
            targetPos = target.getPos();
        }

        TPUtil.tpTo(targetPos);
        MaceKillUtil.hit(target);
    }
}
