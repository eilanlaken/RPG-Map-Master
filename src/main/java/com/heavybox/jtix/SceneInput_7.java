package com.heavybox.jtix;

import com.heavybox.jtix.application.Application;
import com.heavybox.jtix.application.Scene;
import com.heavybox.jtix.assets.Assets;
import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.graphics.*;
import com.heavybox.jtix.input.Input;
import com.heavybox.jtix.input.Mouse;
import com.heavybox.jtix.tools.ToolsTexturePacker;
import com.heavybox.jtix.tools.ToolsThemeGenerator;
import com.heavybox.jtix.userinterface.Theme;
import com.heavybox.jtix.userinterface.UICheckbox;
import com.heavybox.jtix.userinterface.UISlider;
import com.heavybox.jtix.userinterface.UserInterface;
import com.heavybox.jtix.z.UINodeLayers;
import com.heavybox.jtix.z.UINodeToolOptions;
import com.heavybox.jtix.z.UINodeToolbar;
import com.heavybox.jtix.z.UINodeTopbar;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

// default theme
public class SceneInput_7 implements Scene {

    Renderer2D renderer2D = new Renderer2D();
    UISlider slider = new UISlider();

    @Override
    public void start() {

        try {
            ToolsTexturePacker.packTextures("assets/texture-packs", "user-interface", 0, 2, ToolsTexturePacker.TexturePackSize.XX_LARGE_8192, "assets/user-interface", true);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        try {

            // option
            ToolsThemeGenerator.optionImageOnPath = "assets/user-interface-theme/option-on.png";
            ToolsThemeGenerator.optionImageOffPath = "assets/user-interface-theme/option-off.png";

            // group
            ToolsThemeGenerator.groupColorBackground = Color.valueOf("#1E1F22");
            ToolsThemeGenerator.groupSizeBorder = 0;

            // text
            ToolsThemeGenerator.textSize = 18;
            ToolsThemeGenerator.textAntialiasing = true;

            // checkbox
            ToolsThemeGenerator.checkboxImageCheckedPath = "assets/user-interface-theme/checkbox-checked.png";
            ToolsThemeGenerator.checkboxImageUncheckedPath = "assets/user-interface-theme/checkbox-unchecked.png";

            // slider

            ToolsThemeGenerator.generateTheme("assets/texture-packs", "theme");
        } catch (Exception e) {

        }

        Assets.loadTheme("assets/texture-packs/theme.yml");
        Assets.loadTexturePack("assets/texture-packs/user-interface.yml");
        Assets.finishLoading();

        Theme theme = Assets.get("assets/texture-packs/theme.yml");
        UserInterface.setTheme(theme);
        slider = new UISlider();
        UICheckbox checkbox = new UICheckbox();
        UserInterface.add(checkbox);
    }

    @Override
    public boolean mouseButtonsDown(int mouseX, int mouseY, @NotNull Array<Mouse.Button> buttons) {

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
        //renderer2D.drawTextureRegion(pack.getRegion("assets/user-interface/toolbar-icon-geology.png"), 0, 0, 0, 1,1);
        //renderer2D.drawStringLine("hello world", 20, true, 0,0,0,0,0,1,1);
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
