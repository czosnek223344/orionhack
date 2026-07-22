package com.orionhack.addon.modules;

import com.orionhack.addon.OrionHack;
import com.orionhack.addon.utils.MaceKillUtil;
import com.orionhack.addon.utils.TPUtil;
import com.orionhack.addon.utils.TargetUtil;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;

public class Settings extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    public final Setting<Double> maceKillHeight = sgGeneral.add(new DoubleSetting.Builder()
        .name("mace-kill-height")
        .description("height for mace kill.")
        .defaultValue(30.0)
        .min(0.0)
        .max(50.0)
        .sliderMin(0.0)
        .sliderMax(50.0)
        .build()
    );

    public Settings() {
        super(OrionHack.CATEGORY, "settings", "Addon settings.");
    }

    @Override
    public void onActivate() {
        MaceKillUtil.setMaceKillHeight(maceKillHeight.get());
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        MaceKillUtil.setMaceKillHeight(maceKillHeight.get());
    }
} // you have found secret backdoor, be scared
