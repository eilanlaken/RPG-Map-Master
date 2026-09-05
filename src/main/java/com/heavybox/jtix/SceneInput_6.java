package com.heavybox.jtix;

import com.heavybox.jtix.application.Application;
import com.heavybox.jtix.application.Scene;
import com.heavybox.jtix.assets.Assets;
import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Graphics;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.graphics.TexturePack;
import com.heavybox.jtix.input.Input;
import com.heavybox.jtix.input.Keyboard;
import com.heavybox.jtix.input.Mouse;
import com.heavybox.jtix.math.MathUtils;
import com.heavybox.jtix.tools.ToolsTexturePacker;
import com.heavybox.jtix.tools.ToolsThemeGenerator;
import com.heavybox.jtix.userinterface.*;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

public class SceneInput_6 implements Scene {

    NodeGroup panel_1;
    Renderer2D renderer2D = new Renderer2D();
    TexturePack atlas;


    @Override
    public void start() {

        try {
            ToolsTexturePacker.packTextures("assets/texture-packs", "user-interface", 0, 2, ToolsTexturePacker.TexturePackSize.XX_LARGE_8192, "assets/user-interface", true);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        try {

            // group
            ToolsThemeGenerator.groupColorBackground = Color.valueOf("#2B2B2B");
            ToolsThemeGenerator.groupSizeBorder = 0;

            // text
            ToolsThemeGenerator.textSize = 38;
            ToolsThemeGenerator.textAntialiasing = true;

            // checkbox

            // slider

            ToolsThemeGenerator.generateTheme("assets/user-interface-theme", "theme");
        } catch (Exception e) {

        }
        Assets.loadTheme("assets/user-interface-theme/theme.yml");
        Assets.loadTexturePack("assets/texture-packs/user-interface.yml");
        Assets.finishLoading();

        Theme theme = Assets.get("assets/user-interface-theme/theme.yml");
        UserInterface.setTheme(theme);


        atlas = Assets.get("assets/texture-packs/user-interface.yml");

        panel_1 = new NodeGroup();
        panel_1.setLayoutGrid(true, 5,5, true);

        panel_1.widthFitContent = false;
        panel_1.heightFitContent = false;
        UserInterface.add(panel_1);
    }

    @Override
    public boolean mouseButtonsDown(int mouseX, int mouseY, @NotNull Array<Mouse.Button> buttons) {
        NodeGraphics child = new NodeGraphics(MathUtils.randomUniformFloat(50,150), MathUtils.randomUniformFloat(50,150));
        child.color = Color.randomOpaque().toFloatBits();
        panel_1.childAdd(child);
        return true;
    }


    @Override
    public void update() {
        //moveWindow();
        UserInterface.update();

        Graphics.bindFrameBuffer(null);
        GL11.glClearColor(0f,0f,0f,1);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_STENCIL_BUFFER_BIT); // should probably clear the stencil


        renderer2D.begin();
        UserInterface.render(renderer2D);
        renderer2D.end();


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
