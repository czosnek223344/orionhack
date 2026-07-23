package com.orionhack.addon.modules;

import com.orionhack.addon.OrionHack;
import com.orionhack.addon.utils.Blacklist;
import com.orionhack.addon.utils.MaceKillUtil;
import com.orionhack.addon.utils.PredictionUtil;
import com.orionhack.addon.utils.TPUtil;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.EntityDamageS2CPacket;
import net.minecraft.util.math.Vec3d;

public class HitBack extends Module {

    public HitBack() {
        super(OrionHack.CATEGORY, "hitback", "i hate this module");
    }

    @EventHandler
    private void onReceivePacket(PacketEvent.Receive event) {
        if (!(event.packet instanceof EntityDamageS2CPacket damagePacket)) return;

        if (mc.player == null || damagePacket.entityId() != mc.player.getId()) return;

        Entity attacker = mc.world.getEntityById(damagePacket.sourceCauseId());
        if (attacker == null) return;

        boolean isBlacklistedPlayer = attacker instanceof PlayerEntity p && Blacklist.isBlacklisted(p.getName().getString());
        boolean isMob = attacker instanceof MobEntity;

        if (isBlacklistedPlayer || isMob) {
            attackBack(attacker);
        }
    }

    private void attackBack(Entity target) {
        if (target == null) return;

        Vec3d targetPos = (target instanceof PlayerEntity p) 
            ? PredictionUtil.getPredictedPos(p, 2.0) 
            : target.getPos();

        TPUtil.tpTo(targetPos);
        MaceKillUtil.hit(target);
    }
}
