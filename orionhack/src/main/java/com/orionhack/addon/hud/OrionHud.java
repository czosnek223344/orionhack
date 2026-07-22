package com.orionhack.addon.hud;

import com.orionhack.addon.OrionHack;
import meteordevelopment.meteorclient.systems.hud.HudElement;
import meteordevelopment.meteorclient.systems.hud.HudElementInfo;
import meteordevelopment.meteorclient.systems.hud.HudRenderer;
import meteordevelopment.meteorclient.utils.render.color.Color;

public class OrionHud extends HudElement {
    // java scares me
    public static final HudElementInfo<OrionHud> INFO = new HudElementInfo<>(OrionHack.HUD_GROUP, "orion-hud", "Orion HUD.", OrionHud::new);

    public OrionHud() {
        super(INFO);
    }

    @Override
    public void render(HudRenderer renderer) {
        setSize(renderer.textWidth("Orion", true), renderer.textHeight(true));
        renderer.quad(x, y, getWidth(), getHeight(), Color.RED);
        renderer.text("Orion", x, y, Color.WHITE, true);
    }
}
