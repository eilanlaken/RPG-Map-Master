package com.heavybox.jtix;

import com.heavybox.jtix.application.Application;
import com.heavybox.jtix.application.Scene;
import com.heavybox.jtix.assets.Assets;
import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.collections.ArrayFloat;
import com.heavybox.jtix.graphics.*;
import com.heavybox.jtix.input.Input;
import com.heavybox.jtix.input.Keyboard;
import com.heavybox.jtix.input.Mouse;
import com.heavybox.jtix.tools.ToolsTexturePacker;
import com.heavybox.jtix.tools.ToolsThemeGenerator;
import com.heavybox.jtix.userinterface.*;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

public class SceneInput_4 implements Scene {

    NodeGraphics panel_1 = new NodeGraphics(250,250);
    NodeGraphics p1_child_1 = new NodeGraphics(80,80);
    NodeGraphics p1_child_2 = new NodeGraphics(80,80);

    NodeGraphics panel_2 = new NodeGraphics(250,250);
    NodeGraphics p2_child_1 = new NodeGraphics(80,80);
    NodeGraphics p2_child_2 = new NodeGraphics(80,80);

    NodeGraphics shape = new NodeGraphics(55);
    NodeText text = new NodeText("hello text");
    NodeGraphics picture;

    NodeSlider slider;
    NodeCheckbox checkbox;

    Renderer2D renderer2D = new Renderer2D();
    TexturePack atlas;

    TextureRegion region;
    NodeGraphics graphics;

    @Override
    public void start() {

        try {
            ToolsTexturePacker.packTextures("assets/texture-packs", "user-interface", 0, 2, ToolsTexturePacker.TexturePackSize.XX_LARGE_8192, "assets/user-interface", true);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        try {
            //ToolsThemeGenerator.checkboxColorCheckmarkBackground = Color.CHARTREUSE;
            //ToolsThemeGenerator.checkboxColorBorderUnchecked = Color.RED;
            ToolsThemeGenerator.checkboxImageCheckedPath = "assets/user-interface-theme/checkbox-checked.png";
            ToolsThemeGenerator.checkboxImageUncheckedPath = "assets/user-interface-theme/checkbox-unchecked.png";

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

        slider = new NodeSlider();
        checkbox = new NodeCheckbox();

        atlas = Assets.get("assets/texture-packs/user-interface.yml");
        region = atlas.getRegion("assets/user-interface/debug-mouse.jpg");
        graphics = new NodeGraphics(344,344);

        picture = new NodeGraphics(atlas.getRegion("assets/user-interface/toolbar-icon-nature.png"));

        panel_1.transform.x = -300;
        panel_1.transform.y = 0;
        p1_child_1.transform.x = 0;
        p1_child_1.transform.y = 60;
        p1_child_2.transform.x = 0;
        p1_child_2.transform.y = -60;
        panel_1.connectChild(p1_child_1);
        panel_1.connectChild(p1_child_2);

        panel_2.transform.x = 300;
        panel_2.transform.y = 0;
        p2_child_1.transform.x = 0;
        p2_child_1.transform.y = 60;
        p2_child_2.transform.x = 0;
        p2_child_2.transform.y = -60;
        panel_2.connectChild(p2_child_1);
        panel_2.connectChild(p2_child_2);

        shape.setShapeToCircleArc(55,22,30);
        shape.connectChild(text);

        panel_1.onMouseLeave(e -> {
            System.out.println("panel_1 leave");
        });
        p1_child_1.onMouseLeave(e -> {
            System.out.println("c1 leave");
        });
        p1_child_2.onMouseLeave(e -> {
            System.out.println("c2 leave");
        });

        panel_1.onMouseEnter(e -> {
            System.out.println("panel_1 enter");
        });
        p1_child_1.onMouseEnter(e -> {
            System.out.println("c1 enter");
        });
        p1_child_2.onMouseEnter(e -> {
            System.out.println("c2 enter");
        });

        // drag and drop
        p1_child_1.onMouseDragStart(e -> {
            e.target.disconnectFromParent();
            e.target.zIndex = 100;
            e.target.transform.x = UserInterface.getPointerX();
            e.target.transform.y = UserInterface.getPointerY();
        });
        p1_child_1.onMouseDrag(e -> {
            e.target.transform.x = UserInterface.getPointerX();
            e.target.transform.y = UserInterface.getPointerY();
        });
        p1_child_1.onMouseDragEnter(e -> {
            System.out.println("error enter");
        });
        p1_child_1.onMouseDragDrop(e -> {
            System.out.println("error drop");
        });

        panel_2.onMouseDragEnter(e -> {
            System.out.println("enter: " + e.dragged);
        });
        panel_2.onMouseDragLeave(e -> {
            System.out.println("leave: " + e.dragged);
        });
        panel_2.onMouseDragDrop(e -> {
            System.out.println("dropped: " + e.dragged);
        });


        graphics.setShapeToRectangleRoundCorners(region.originalWidth, region.originalHeight, 30, 30);

  //      UserInterface.add(panel_1);
//        UserInterface.add(panel_2);
        //UserInterface.add(shape);
        //UserInterface.add(picture);
        //slider.transform.deg = 90;
        UserInterface.add(slider);
        //UserInterface.add(graphics);

        //UserInterface.add(checkbox);
    }

    @Override
    public boolean mouseButtonsDown(int mouseX, int mouseY, @NotNull Array<Mouse.Button> buttons) {
        //System.out.println("screen");
        text.text = "abs asdakl asdasldkj asd \nasdkals \nasdalkj ";
        return false;
    }


    @Override
    public void update() {
        //moveWindow();
        UserInterface.update();

        Graphics.bindFrameBuffer(null);
        GL11.glClearColor(1f,1f,1f,1);
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


        if (Input.keyboard.isKeyJustPressed(Keyboard.Key.KEY_1)) {
            panel_1.zIndex = 8;
            panel_2.zIndex = 9;
        }

        if (Input.keyboard.isKeyJustPressed(Keyboard.Key.KEY_2)) {
            panel_1.zIndex = 9;
            panel_2.zIndex = 8;
        }

        renderer2D.begin();
        UserInterface.render(renderer2D);
        renderer2D.end();

        renderer2D.begin();
//        renderer2D.drawTextureRegion(
//                region,
//                0f, 0f,
//                0.5f, 1f,
//                0, 0, 0,
//                1, 1
//        );
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
