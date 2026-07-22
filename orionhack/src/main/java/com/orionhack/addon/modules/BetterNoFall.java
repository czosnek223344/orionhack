package com.orionhack.addon.modules;

import com.orionhack.addon.OrionHack;
import com.orionhack.addon.mixin.CustomPlayerMoveAccessor;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

public class BetterNoFall extends Module {

    public BetterNoFall() {
        super(OrionHack.CATEGORY, "better-no-fall", "Better no fall damage by forcing on-ground to false in movement packets.");
    }

    @EventHandler
    private void onSendPacket(PacketEvent.Send event) {
        Packet<?> packet = event.packet;
        if (packet instanceof PlayerMoveC2SPacket) {
            PlayerMoveC2SPacket packet2 = (PlayerMoveC2SPacket) packet;
            ((CustomPlayerMoveAccessor) packet2).setOnGround(false);
        }
    }
}
