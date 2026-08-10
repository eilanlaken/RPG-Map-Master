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
import com.heavybox.jtix.tools.ToolsThemeGenerator;
import com.heavybox.jtix.tools.ToolsThemeGenerator_z;
import com.heavybox.jtix.userinterface.*;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

public class SceneInput_4 implements Scene {

    NodeShape panel_1 = new NodeShape(250,250);
    NodeShape p1_child_1 = new NodeShape(80,80);
    NodeShape p1_child_2 = new NodeShape(80,80);

    NodeShape panel_2 = new NodeShape(250,250);
    NodeShape p2_child_1 = new NodeShape(80,80);
    NodeShape p2_child_2 = new NodeShape(80,80);

    NodeShape shape = new NodeShape(55);
    NodeText text = new NodeText("hello text");
    NodePicture picture;

    NodeSlider slider = new NodeSlider();
    NodeCheckbox checkbox;

    Renderer2D renderer2D = new Renderer2D();
    TexturePack atlas;

    @Override
    public void start() {
        try {
            ToolsThemeGenerator.checkboxColorCheckmarkBackground = Color.CHARTREUSE;
            ToolsThemeGenerator.checkboxImageCheckedPath = "assets/user-interface-theme/checkbox-checked.png";
            ToolsThemeGenerator.checkboxImageUncheckedPath = "assets/user-interface-theme/checkbox-unchecked.png";
            ToolsThemeGenerator.generateTheme("assets/user-interface-theme", "theme");
        } catch (Exception e) {

        }
        Assets.loadTheme("assets/user-interface-theme/theme.yml");
        Assets.loadTexturePack("assets/texture-packs/user-interface.yml");
        Assets.finishLoading();

        Theme theme = Assets.get("assets/user-interface-theme/theme.yml");
        UserInterface.setTheme(theme);

        checkbox = new NodeCheckbox();

        atlas = Assets.get("assets/texture-packs/user-interface.yml");

        picture = new NodePicture(atlas.getRegion("assets/user-interface/toolbar-icon-nature.png"));

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

        shape.setToCircleArc(55,22,30);
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


        UserInterface.add(panel_1);
        UserInterface.add(panel_2);
        //UserInterface.add(shape);
        //UserInterface.add(picture);
        //UserInterface.add(slider);
        UserInterface.add(checkbox);
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
        GL11.glClearColor(0.01f,0.01f,0.01f,1);
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
