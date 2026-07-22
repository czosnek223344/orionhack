package com.orionhack.addon.gui;

import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.tabs.Tab;
import meteordevelopment.meteorclient.gui.tabs.TabScreen;
import net.minecraft.client.gui.screen.Screen;

public class BlacklistTab extends Tab {
    public BlacklistTab() {
        super("Blacklist");
    }

    @Override
    public TabScreen createScreen(GuiTheme theme) {
        return new BlacklistScreen(theme, this);
    }

    @Override
    public boolean isScreen(Screen screen) {
        return screen instanceof BlacklistScreen;
    }
}
