package com.orionhack.addon.modules;

import com.orionhack.addon.OrionHack;
import com.orionhack.addon.utils.TPUtil;
import com.orionhack.addon.utils.TargetUtil;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;

public class Settings extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    public final Setting<Double> maceKillHeight = sgGeneral.add(new DoubleSetting.Builder()
        .name("mace-kill-height")
        .description("height for mace kill attack.")
        .defaultValue(20.0)
        .min(0.0)
        .max(30.0)
        .sliderMin(0.0)
        .sliderMax(30.0)
        .build()
    );

    public Settings() {
        super(OrionHack.CATEGORY, "settings", "Addon settings.");
    }
}
