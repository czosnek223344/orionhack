package com.orionhack.addon.gui;

import com.orionhack.addon.utils.Blacklist;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.tabs.Tab;
import meteordevelopment.meteorclient.gui.tabs.WindowTabScreen;
import meteordevelopment.meteorclient.gui.widgets.containers.WTable;
import meteordevelopment.meteorclient.gui.widgets.input.WTextBox;
import meteordevelopment.meteorclient.gui.widgets.pressable.WMinus;
import meteordevelopment.meteorclient.gui.widgets.pressable.WPlus;

import java.util.ArrayList;
import java.util.List;

public class BlacklistScreen extends WindowTabScreen {
    public BlacklistScreen(GuiTheme theme, Tab tab) {
        super(theme, tab);
    }

    @Override
    public void initWidgets() {
        // Sets up a table for organizing the input field and list
        WTable table = add(theme.table()).expandX().widget();

        // Adding a new blacklisted player
        WTextBox nameBox = table.add(theme.textBox("")).minWidth(200).expandX().widget();
        nameBox.setFocused(true);

        WPlus addBtn = table.add(theme.plus()).widget();
        addBtn.action = () -> {
            String name = nameBox.get().trim();
            if (!name.isEmpty()) {
                Blacklist.add(name);
                nameBox.set("");
                reload();
            }
        };

        table.row();

        // Listing currently blacklisted players dynamically
        List<String> players = new ArrayList<>(Blacklist.getList());
        for (String p : players) {
            table.add(theme.label(p)).expandX();
            WMinus removeBtn = table.add(theme.minus()).widget();
            removeBtn.action = () -> {
                Blacklist.remove(p);
                reload();
            };
            table.row();
        }
    }
}
