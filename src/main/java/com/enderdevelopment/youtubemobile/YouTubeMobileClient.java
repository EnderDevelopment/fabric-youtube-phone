package com.enderdevelopment.youtubemobile;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

public final class YouTubeMobileClient implements ClientModInitializer {
    private static final KeyMapping OPEN_APP = KeyBindingHelper.registerKeyBinding(new KeyMapping(
            "key.youtube_mobile.open", InputConstants.Type.KEYSYM, InputConstants.KEY_Y,
            KeyMapping.Category.MISC));

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (OPEN_APP.consumeClick()) {
                if (client.screen instanceof YouTubeScreen) {
                    client.setScreen(null);
                } else {
                    client.setScreen(new YouTubeScreen());
                }
            }
        });
    }
}
