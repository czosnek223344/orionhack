package com.orionhack.addon.utils;

import com.orionhack.addon.gui.BlacklistTab;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.gui.tabs.Tabs;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

public class Blacklist {
    private static final Set<String> blacklisted = new HashSet<>();
    // shit making folder in meteor client folder
    private static final File FOLDER = new File(MeteorClient.FOLDER, "orionhack");
    private static final File FILE = new File(FOLDER, "blacklist.txt");

    public static void add(String name) {
        blacklisted.add(name.toLowerCase());
        save();
    }

    public static void remove(String name) {
        blacklisted.remove(name.toLowerCase());
        save();
    }

    public static boolean isBlacklisted(String name) {
        return blacklisted.contains(name.toLowerCase());
    }

    public static Set<String> getList() {
        return new HashSet<>(blacklisted);
    }

    // loads the file
    public static void load() {
        if (!FILE.exists()) return;
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    blacklisted.add(line.trim().toLowerCase());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // saves the .txt
    public static void save() {
        if (!FOLDER.exists()) FOLDER.mkdirs();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE))) {
            for (String name : blacklisted) {
                writer.write(name);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // blacklist tab
    public static void registerTab() {
        Tabs.get().add(new BlacklistTab());
    }
}
