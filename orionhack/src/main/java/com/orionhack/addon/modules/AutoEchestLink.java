package com.orionhack.addon.modules;

import com.orionhack.addon.OrionHack;
import com.orionhack.addon.utils.EChestLinkUtil;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.util.math.Vec3d;

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

    public AutoEchestLink() {
        super(OrionHack.CATEGORY, "auto-echest-link", "Auto opens nearest EC via EChestLinkUtil, keeps GUI open.");
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if (mc.player == null || mc.world == null) return;
        EChestLinkUtil.tick();

        boolean ecGuiOpen = mc.currentScreen != null && mc.currentScreen.getTitle() != null &&
            mc.currentScreen.getTitle().getString().toLowerCase().contains("ender");

        if (!ecGuiOpen && !EChestLinkUtil.isOpening()) {
            Vec3d nearest = EChestLinkUtil.findNearestEC(maxRange.get());
            if (nearest != null) {
                EChestLinkUtil.open(nearest);
            }
        }
    }
}
