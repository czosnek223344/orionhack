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
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

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
    private boolean guiHidden = false;
    private boolean guiWasOpen = false;
    private boolean lastKeyState = false;
    private HandledScreen<?> savedScreen = null;
    private int savedSyncId = -1;
    private World lastWorld = null;

    public AutoEchestLink() {
        super(OrionHack.CATEGORY, "auto-echest-link", "Auto opens nearest EC and hides it so you can move.");
    }

    @Override
    public void onDeactivate() {
        resetStuff();
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if (mc.player == null || mc.world == null) return;
        EChestLinkUtil.tick();

        boolean isEchestScreen = mc.currentScreen instanceof HandledScreen &&
            mc.currentScreen.getTitle() != null &&
            mc.currentScreen.getTitle().getString().toLowerCase().contains("ender");

        if (isEchestScreen && savedScreen == null && !guiWasOpen && !guiHidden) {
            savedScreen = (HandledScreen<?>) mc.currentScreen;
            if (mc.player.currentScreenHandler != null) {
                savedSyncId = mc.player.currentScreenHandler.syncId;
            }
            
            if (hideGui.get()) {
                mc.setScreen(null);
                guiHidden = true;
            } else {
                guiHidden = false;
            }
            
            guiWasOpen = true;
            isLinked = true;
            lastWorld = mc.world;
            info("EChest linked! Press your toggle key to open/close the GUI.");
        }

        boolean keyDown = toggleKey.get().isPressed();
        boolean keyJustPressed = keyDown && !lastKeyState;
        lastKeyState = keyDown;

        if (keyJustPressed && savedScreen != null) {
            if (guiHidden) {
                mc.setScreen(savedScreen);
                guiHidden = false;
            } else {
                mc.setScreen(null);
                guiHidden = true;
            }
        }

        if (getMaceKey.get().isPressed()) {
            EChestLinkUtil.get("mace");
        }

        if (savedScreen != null && mc.currentScreen == null && !guiHidden && guiWasOpen) {
            resetStuff();
            error("Ender Chest GUI closed. EChest link broken.");
            return;
        }

        if (savedScreen != null && savedSyncId != -1) {
            boolean handlerValid = mc.player.currentScreenHandler != null &&
                    mc.player.currentScreenHandler.syncId == savedSyncId;
            if (!handlerValid) {
                resetStuff();
                error("Ender chest handler invalid. EChest link broken.");
                return;
            }
        }

        if (savedScreen != null && mc.world != lastWorld) {
            resetStuff();
            lastWorld = mc.world;
            error("World changed. EChest link broken.");
            return;
        }

        lastWorld = mc.world;

        if (!isLinked && !EChestLinkUtil.isOpening() && savedScreen == null) {
            Vec3d ecPos = EChestLinkUtil.findNearestEC(maxRange.get());
            if (ecPos != null) {
                EChestLinkUtil.open(ecPos);
            }
        }
    }

    private void resetStuff() {
        guiHidden = false;
        guiWasOpen = false;
        lastKeyState = false;
        isLinked = false;
        if (savedSyncId != -1 && mc.player != null && mc.getNetworkHandler() != null) {
            mc.getNetworkHandler().sendPacket(new CloseHandledScreenC2SPacket(savedSyncId));
            savedSyncId = -1;
        }
        savedScreen = null;
    }
}
