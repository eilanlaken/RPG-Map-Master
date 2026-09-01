package com.heavybox.jtix;

import com.heavybox.jtix.application.Application;
import com.heavybox.jtix.application.Scene;
import com.heavybox.jtix.assets.Assets;
import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.graphics.*;
import com.heavybox.jtix.input.Input;
import com.heavybox.jtix.input.Keyboard;
import com.heavybox.jtix.input.Mouse;
import com.heavybox.jtix.tools.ToolsTexturePacker;
import com.heavybox.jtix.tools.ToolsThemeGenerator;
import com.heavybox.jtix.userinterface.*;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

public class SceneInput_5 implements Scene {

    NodeGroup panel_1;
    NodeGraphics p1_child_1 = new NodeGraphics(80,80);
    NodeGraphics p1_child_2 = new NodeGraphics(80,80);

    Renderer2D renderer2D = new Renderer2D();
    TexturePack atlas;

    TextureRegion region;
    NodeGraphics graphics;

    NodeScrollbar scrollbar;

    @Override
    public void start() {

        try {
            ToolsTexturePacker.packTextures("assets/texture-packs", "user-interface", 0, 2, ToolsTexturePacker.TexturePackSize.XX_LARGE_8192, "assets/user-interface", true);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        try {
            // scrollbar
            //ToolsThemeGenerator.scrollbarImageBarPath = "assets/user-interface-theme/scrollbar-bar.png";
            //ToolsThemeGenerator.scrollbarImageThumbPath = "assets/user-interface-theme/scrollbar-thumb.png";
            //ToolsThemeGenerator.scrollbarColorBar = Color.RED.clone();
            //ToolsThemeGenerator.scrollbarColorThumb = Color.BLUE.clone();
            //ToolsThemeGenerator.scrollbarThickness = 50;

            // group
            ToolsThemeGenerator.groupTextureBackgroundPath = "assets/user-interface-theme/panel-background.png";

            // text
            ToolsThemeGenerator.textFontPath = "assets/user-interface-theme/SnackerComic_PerosnalUseOnly.ttf";
            ToolsThemeGenerator.textSize = 38;
            ToolsThemeGenerator.textAntialiasing = true;

            // checkbox
            ToolsThemeGenerator.checkboxImageCheckedPath = "assets/user-interface-theme/checkbox-checked.png";
            ToolsThemeGenerator.checkboxImageUncheckedPath = "assets/user-interface-theme/checkbox-unchecked.png";

            // slider
            ToolsThemeGenerator.sliderImageBackgroundPath = "assets/user-interface-theme/slider-background.png";
            ToolsThemeGenerator.sliderImageFillPath = "assets/user-interface-theme/slider-fill.png";
            ToolsThemeGenerator.sliderImageThumbPath = "assets/user-interface-theme/slider-thumb.png";
            ToolsThemeGenerator.sliderLength = 200;

            ToolsThemeGenerator.generateTheme("assets/user-interface-theme", "theme");
        } catch (Exception e) {

        }
        Assets.loadTheme("assets/user-interface-theme/theme.yml");
        Assets.loadTexturePack("assets/texture-packs/user-interface.yml");
        Assets.finishLoading();

        Theme theme = Assets.get("assets/user-interface-theme/theme.yml");
        UserInterface.setTheme(theme);


        atlas = Assets.get("assets/texture-packs/user-interface.yml");
        region = atlas.getRegion("assets/user-interface/debug-mouse.jpg");
        graphics = new NodeGraphics(344,344);

        panel_1 = new NodeGroup();
        scrollbar = new NodeScrollbar();
        UserInterface.add(scrollbar);
    }

    @Override
    public boolean mouseButtonsDown(int mouseX, int mouseY, @NotNull Array<Mouse.Button> buttons) {
        return false;
    }


    @Override
    public void update() {
        //moveWindow();
        UserInterface.update();

        Graphics.bindFrameBuffer(null);
        GL11.glClearColor(0f,0f,0f,1);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_STENCIL_BUFFER_BIT); // should probably clear the stencil

        if (Input.keyboard.isKeyPressed(Keyboard.Key.D)) {
            p1_child_1.transform.x += 1;
        }
        if (Input.keyboard.isKeyPressed(Keyboard.Key.A)) {
            p1_child_1.transform.x -= 1;
        }
        if (Input.keyboard.isKeyPressed(Keyboard.Key.W)) {
            p1_child_1.transform.y += 1;
        }
        if (Input.keyboard.isKeyPressed(Keyboard.Key.S)) {
            p1_child_1.transform.y -= 1;
        }
        if (Input.keyboard.isKeyPressed(Keyboard.Key.Q)) {
            panel_1.transform.deg += 1;
        }


        renderer2D.begin();
        UserInterface.render(renderer2D);
        renderer2D.end();

//        renderer2D.begin();
////        renderer2D.drawTextureRegion(
////                region,
////                0f, 0f,
////                0.5f, 1f,
////                0, 0, 0,
////                1, 1
////        );
//        renderer2D.end();

    }

    // TODO: this is how you can drag an undecorated window.
    boolean draggingWindow = false;
    int dragOffsetX = 0;
    int dragOffsetY = 0;
    private void moveWindow() {
        if (Input.mouse.isButtonJustPressed(Mouse.Button.LEFT)) {
            draggingWindow = true;

            dragOffsetX = Input.mouse.getX();
            dragOffsetY = Input.mouse.getY();
        }

        if (draggingWindow && Input.mouse.isButtonPressed(Mouse.Button.LEFT)) {
            Application.windowSetPosition(
                    Input.mouse.getMonitorX() - dragOffsetX,
                    Input.mouse.getMonitorY() - dragOffsetY
            );
        }

        if (Input.mouse.isButtonJustReleased(Mouse.Button.LEFT)) {
            draggingWindow = false;
        }
    }

    @Override
    public boolean isActive() {
        return true;
    }

    @Override
    public void finish() {

    }

}
