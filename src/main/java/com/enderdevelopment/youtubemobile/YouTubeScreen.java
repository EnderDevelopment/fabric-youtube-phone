package com.enderdevelopment.youtubemobile;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Util;

import java.net.URI;
import java.util.List;

public final class YouTubeScreen extends Screen {
    private static final int CARD_HEIGHT = 56;
    private static final int PAGE_SIZE = 5;
    private static final List<Video> VIDEOS = List.of(
            new Video("Minecraft 1.21 Survival Guide", "https://www.youtube.com/results?search_query=Minecraft+1.21+survival+guide"),
            new Video("Building a Cozy Minecraft Starter House", "https://www.youtube.com/results?search_query=Minecraft+cozy+starter+house"),
            new Video("Minecraft Redstone Tutorials", "https://www.youtube.com/results?search_query=Minecraft+redstone+tutorial"),
            new Video("Minecraft World Tour", "https://www.youtube.com/results?search_query=Minecraft+world+tour"),
            new Video("Minecraft Update News", "https://www.youtube.com/results?search_query=Minecraft+update+news"),
            new Video("Minecraft Adventure Maps", "https://www.youtube.com/results?search_query=Minecraft+adventure+maps"),
            new Video("Minecraft Creative Building Ideas", "https://www.youtube.com/results?search_query=Minecraft+creative+building+ideas"),
            new Video("Minecraft Tips and Tricks", "https://www.youtube.com/results?search_query=Minecraft+tips+and+tricks")
    );

    private int page;
    private int selected;
    private Button previousButton;
    private Button nextButton;

    public YouTubeScreen() {
        super(Component.literal("YouTube Mobile"));
    }

    @Override
    protected void init() {
        int left = Math.max(12, (width - 420) / 2);
        int top = 54;
        int controlsY = top + PAGE_SIZE * CARD_HEIGHT + 12;
        previousButton = addRenderableWidget(Button.builder(Component.literal("<"), button -> previousPage())
                .bounds(left, controlsY, 40, 22).build());
        nextButton = addRenderableWidget(Button.builder(Component.literal(">"), button -> nextPage())
                .bounds(left + 380, controlsY, 40, 22).build());
        updatePageButtons();
    }

    private void previousPage() {
        if (page > 0) {
            page--;
            selected = 0;
            updatePageButtons();
        }
    }

    private void nextPage() {
        if ((page + 1) * PAGE_SIZE < VIDEOS.size()) {
            page++;
            selected = 0;
            updatePageButtons();
        }
    }

    private void updatePageButtons() {
        if (previousButton != null) {
            previousButton.active = page > 0;
            nextButton.active = (page + 1) * PAGE_SIZE < VIDEOS.size();
        }
    }

    private void openSelected() {
        int index = page * PAGE_SIZE + selected;
        if (index >= 0 && index < VIDEOS.size()) {
            try {
                Util.getPlatform().openUri(URI.create(VIDEOS.get(index).url()));
            } catch (RuntimeException ignored) {
                if (minecraft.player != null) {
                    minecraft.player.displayClientMessage(Component.literal("Could not open the video link."), false);
                }
            }
        }
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (event.key() == InputConstants.KEY_ESCAPE) {
            onClose();
            return true;
        }
        if (event.key() == InputConstants.KEY_UP) {
            selected = Math.max(0, selected - 1);
            return true;
        }
        if (event.key() == InputConstants.KEY_DOWN) {
            selected = Math.min(Math.min(PAGE_SIZE, VIDEOS.size() - page * PAGE_SIZE) - 1, selected + 1);
            return true;
        }
        if (event.key() == InputConstants.KEY_LEFT) {
            previousPage();
            return true;
        }
        if (event.key() == InputConstants.KEY_RIGHT) {
            nextPage();
            return true;
        }
        if (event.key() == InputConstants.KEY_RETURN || event.key() == InputConstants.KEY_SPACE) {
            openSelected();
            return true;
        }
        return super.keyPressed(event);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (scrollY > 0) {
            selected = Math.max(0, selected - 1);
        } else if (scrollY < 0) {
            selected = Math.min(Math.min(PAGE_SIZE, VIDEOS.size() - page * PAGE_SIZE) - 1, selected + 1);
        }
        return true;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics, mouseX, mouseY, partialTick);
        int panelLeft = Math.max(8, (width - 440) / 2);
        int panelRight = panelLeft + 440;
        int top = 20;
        graphics.fill(panelLeft, top, panelRight, Math.min(height - 12, top + 410), 0xff151923);
        graphics.fill(panelLeft, top, panelRight, top + 34, 0xffe62117);
        graphics.drawString(font, "YouTube", panelLeft + 16, top + 12, 0xffffffff);
        graphics.drawString(font, "Y  open/close", panelRight - 96, top + 12, 0xffffd9d9);
        graphics.drawString(font, "HOME  •  PAGE " + (page + 1), panelLeft + 20, top + 43, 0xffbfc7d5);

        int listTop = top + 54;
        int start = page * PAGE_SIZE;
        for (int row = 0; row < PAGE_SIZE && start + row < VIDEOS.size(); row++) {
            int y = listTop + row * CARD_HEIGHT;
            boolean active = row == selected;
            graphics.fill(panelLeft + 12, y, panelRight - 12, y + 48, active ? 0xff343d52 : 0xff222735);
            graphics.fill(panelLeft + 20, y + 8, panelLeft + 82, y + 40, active ? 0xffe62117 : 0xff39445b);
            graphics.drawCenteredString(font, "▶", panelLeft + 51, y + 19, 0xffffffff);
            graphics.drawString(font, VIDEOS.get(start + row).title(), panelLeft + 94, y + 14, 0xffffffff);
            graphics.drawString(font, "Press ENTER to open in browser", panelLeft + 94, y + 30, 0xffaeb8c9);
        }
        graphics.drawCenteredString(font, Component.literal("↑ ↓ / scroll select    ← → pages    ENTER open    ESC close"), width / 2, height - 22, 0xffd2d7e0);
        super.render(graphics, mouseX, mouseY, partialTick);
    }

    private record Video(String title, String url) {
    }
}
