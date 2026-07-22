package com.orionhack.addon.modules;

import com.orionhack.addon.OrionHack;
import com.orionhack.addon.utils.MaceKillUtil;
import com.orionhack.addon.utils.TPUtil;
import com.orionhack.addon.utils.TargetUtil;
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

public class TPAttack extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Double> maxRange = sgGeneral.add(new DoubleSetting.Builder()
        .name("max-range")
        .description("Max attack range")
        .defaultValue(50.0)
        .range(0.0, 200.0)
        .sliderRange(0.0, 200.0)
        .build()
    );

    private final Setting<Keybind> attackKey = sgGeneral.add(new KeybindSetting.Builder()
        .name("attack-trigger")
        .description("The key or mouse button to trigger the attack.")
        .defaultValue(Keybind.fromButton(GLFW.GLFW_MOUSE_BUTTON_RIGHT))
        .build()
    );

    private final Setting<Keybind> autoAttackKey = sgGeneral.add(new KeybindSetting.Builder()
        .name("auto-attack-trigger")
        .description("The key to toggle auto attack.")
        .defaultValue(Keybind.fromKey(GLFW.GLFW_KEY_J))
        .build()
    );

    private final Setting<Double> autoAttackDelay = sgGeneral.add(new DoubleSetting.Builder()
        .name("auto-attack-delay")
        .description("Delay between auto attacks in ms.")
        .defaultValue(100.0)
        .range(1.0, 1000.0)
        .sliderRange(1.0, 1000.0)
        .build()
    );

    private boolean wasPressed = false;
    private boolean autoAttackWasPressed = false;
    private boolean autoAttackActive = false;
    private long lastAttackTime = 0;

    public TPAttack() {
        super(OrionHack.CATEGORY, "tp-attack", "Idk how this ai slop even works.");
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        boolean isPressed = attackKey.get().isPressed();

        if (isPressed && !wasPressed) {
            attackTarget();
        }

        wasPressed = isPressed;

        boolean isAutoAttackPressed = autoAttackKey.get().isPressed();

        if (isAutoAttackPressed && !autoAttackWasPressed) {
            autoAttackActive = !autoAttackActive;
        }

        autoAttackWasPressed = isAutoAttackPressed;

        if (autoAttackActive) {
            if (System.currentTimeMillis() - lastAttackTime >= autoAttackDelay.get()) {
                attackTarget();
                lastAttackTime = System.currentTimeMillis();
            }
        }
    }

    public void attackTarget() {
        var target = TargetUtil.getClosestBlacklisted(maxRange.get());
        if (target == null) return;

        Vec3d targetPos = target.getBoundingBox().getCenter();

        TPUtil.tpTo(targetPos);
        MaceKillUtil.hit(target);
    }
}
