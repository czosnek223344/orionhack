package com.orionhack.addon.modules;

import com.orionhack.addon.OrionHack;
import com.orionhack.addon.utils.TPUtil;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.KeybindSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.misc.Keybind;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

public class Move extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Double> blocks = sgGeneral.add(new DoubleSetting.Builder()
        .name("blocks")
        .description("Vertical blocks to move. Positive = up, negative = down.")
        .defaultValue(10.0)
        .range(-400.0, 400.0)
        .sliderRange(-400.0, 400.0)
        .build()
    );

    private final Setting<Keybind> moveKey = sgGeneral.add(new KeybindSetting.Builder()
        .name("move-key")
        .description("Key to trigger the vertical TP.")
        .defaultValue(Keybind.fromKey(GLFW.GLFW_KEY_M))
        .build()
    );

    private boolean wasPressed = false;

    public Move() {
        super(OrionHack.CATEGORY, "move", "TP using TPUtil on keybind, enjoy.");
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if (mc.player == null) return;

        boolean pressed = moveKey.get().isPressed();

        if (pressed && !wasPressed) {
            double y = mc.player.getY() + blocks.get();
            Vec3d target = new Vec3d(mc.player.getX(), y, mc.player.getZ());
            TPUtil.tpTo(target);
        }

        wasPressed = pressed;
    }
}