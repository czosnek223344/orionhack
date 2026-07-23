package com.orionhack.addon;

import com.orionhack.addon.commands.BlacklistCommand;
import com.orionhack.addon.hud.OrionHud;
import com.orionhack.addon.modules.TPAttack;
import com.orionhack.addon.modules.BetterNoFall;
import com.orionhack.addon.modules.AutoEchestLink;
import com.orionhack.addon.modules.Settings;
import com.orionhack.addon.modules.HitBack;
import com.orionhack.addon.utils.Blacklist;
import com.mojang.logging.LogUtils;
import meteordevelopment.meteorclient.addons.GithubRepo;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.commands.Commands;
import meteordevelopment.meteorclient.systems.hud.Hud;
import meteordevelopment.meteorclient.systems.hud.HudGroup;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Modules;
import org.slf4j.Logger;

public class OrionHack extends MeteorAddon {
    public static final Logger LOG = LogUtils.getLogger();
    public static final Category CATEGORY = new Category("OrionHack");
    public static final HudGroup HUD_GROUP = new HudGroup("Orion");

    @Override
    public void onInitialize() {
        LOG.info("token sent to mossad"); // 100% rat

        Blacklist.load();

        Modules.get().add(new TPAttack());
        Modules.get().add(new AutoEchestLink());
        Modules.get().add(new BetterNoFall());
        Modules.get().add(new Settings());
        Modules.get().add(new HitBack());
        Commands.add(new BlacklistCommand());
        Hud.get().register(OrionHud.INFO);

        Blacklist.registerTab();
    }

    @Override
    public void onRegisterCategories() {
        Modules.registerCategory(CATEGORY);
    }

    @Override
    public String getPackage() {
        return "com.orionhack.addon";
    }
    @Override
    public GithubRepo getRepo() {
        return new GithubRepo("czosnek223344", "orionhack");
    }
}
