package com.orionhack.addon.modules;

import com.orionhack.addon.OrionHack;
import com.orionhack.addon.utils.EChestLinkUtil;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.KeybindSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.meteorclient.utils.misc.Keybind;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.InputUtil;

public class AutoEchestLink extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Double> maxRange = sgGeneral.add(new DoubleSetting.Builder()
        .name("max-range")
        .description("Max range for EC search")
        .defaultValue(200.0)
        .range(0, 200)
        .sliderRange(0, 200)
        .build()
    );

    private final Setting<Boolean> hideGui = sgGeneral.add(new BoolSetting.Builder()
        .name("hide-gui")
        .description("Hide GUI after link")
        .defaultValue(true)
        .build()
    );

    private final Setting<Keybind> toggleKey = sgGeneral.add(new KeybindSetting.Builder()
        .name("toggle-key")
        .description("Key to show/hide EC GUI (H)")
        .defaultValue(Keybind.fromKey(InputUtil.GLFW_KEY_H))
        .build()
    );

    private final Setting<Keybind> getMaceKey = sgGeneral.add(new KeybindSetting.Builder()
        .name("get-mace-key")
        .description("Key to take mace (N)")
        .defaultValue(Keybind.fromKey(InputUtil.GLFW_KEY_N))
        .build()
    );

    private boolean isLinked = false;

    public AutoEchestLink() {
        super(OrionHack.CATEGORY, "auto-echest-link", "Auto opens nearest EC via EChestLinkUtil.");
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if (mc.player == null || mc.world == null) return;
        EChestLinkUtil.tick();

        boolean ecGuiOpen = mc.currentScreen instanceof HandledScreen &&
            mc.currentScreen.getTitle() != null &&
            mc.currentScreen.getTitle().getString().toLowerCase().contains("ender");

        if (toggleKey.get().isPressed()) {
            if (ecGuiOpen) {
                mc.setScreen(null);
            } else if (isLinked) {
                // Re-show GUI if needed
            }
        }

        if (getMaceKey.get().isPressed()) {
            EChestLinkUtil.get("mace");
        }

        if (hideGui.get() && ecGuiOpen && !isLinked) {
            mc.setScreen(null);
            isLinked = true;
            info("echest linked");
        }

        if (!isLinked && !EChestLinkUtil.isOpening()) {
            EChestLinkUtil.open(EChestLinkUtil.findNearestEC(maxRange.get()));
        }
    }
}
