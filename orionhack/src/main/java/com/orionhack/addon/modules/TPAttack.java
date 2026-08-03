package com.orionhack.addon.modules;

import com.orionhack.addon.OrionHack;
import com.orionhack.addon.utils.EChestLinkUtil;
import com.orionhack.addon.utils.MaceKillUtil;
import com.orionhack.addon.utils.PredictionUtil;
import com.orionhack.addon.utils.TPUtil;
import com.orionhack.addon.utils.TargetUtil;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.utils.misc.Keybind;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.item.Items;
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

    public enum MaceMode {
        AlwaysSwitch,
        EchestIfLinked
    }

    private final Setting<MaceMode> maceMode = sgGeneral.add(new EnumSetting.Builder<MaceMode>()
        .name("mace-mode")
        .description("AlwaysSwitch = normal hotbar swap. EchestIfLinked = use EChestLinkUtil get/hide when AutoEchestLink is active.")
        .defaultValue(MaceMode.AlwaysSwitch)
        .build()
    );

    private final Setting<Boolean> swapBack = sgGeneral.add(new BoolSetting.Builder()
        .name("swap-back")
        .description("Swap back to previous hotbar slot after attack (AlwaysSwitch mode).")
        .defaultValue(true)
        .build()
    );

    private boolean wasPressed = false;
    private boolean autoAttackWasPressed = false;
    private boolean autoAttackActive = false;
    private long lastAttackTime = 0;

    public TPAttack() {
        super(OrionHack.CATEGORY, "tp-attack", "TPattack - 100% rat");
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

        boolean usedEchest = false;

        if (maceMode.get() == MaceMode.EchestIfLinked && isEchestLinked()) {
            EChestLinkUtil.get("mace");
            usedEchest = true;
        } else {
            FindItemResult mace = InvUtils.findInHotbar(Items.MACE);
            if (mace.found()) {
                InvUtils.swap(mace.slot(), swapBack.get());
            }
        }

        Vec3d targetPos = PredictionUtil.getPredictedPos(target, 2.0);
        TPUtil.tpTo(targetPos);
        MaceKillUtil.hit(target);

        if (usedEchest) {
            EChestLinkUtil.hide("mace");
        } else if (swapBack.get()) {
            InvUtils.swapBack();
        }
    }

    private boolean isEchestLinked() {
        AutoEchestLink link = Modules.get().get(AutoEchestLink.class);
        return link != null && link.isActive();
    }
}
