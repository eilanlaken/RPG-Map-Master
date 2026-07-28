package com.heavybox.jtix;

import com.heavybox.jtix.application.Application;
import com.heavybox.jtix.application.Scene;
import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.graphics.Graphics;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.input.Input;
import com.heavybox.jtix.input.Keyboard;
import com.heavybox.jtix.input.Mouse;
import com.heavybox.jtix.widgets_2.NodeShape;
import com.heavybox.jtix.widgets_2.Widgets;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

public class SceneInput_4 implements Scene {

    NodeShape panel_1 = new NodeShape(250,250);
    NodeShape p1_child_1 = new NodeShape(80,80);
    NodeShape p1_child_2 = new NodeShape(80,80);

    NodeShape panel_2 = new NodeShape(250,250);
    NodeShape p2_child_1 = new NodeShape(80,80);
    NodeShape p2_child_2 = new NodeShape(80,80);

    NodeShape rect = new NodeShape(55);

    Renderer2D renderer2D = new Renderer2D();

    @Override
    public void start() {
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

        panel_1.onMouseDoubleClick(e -> {
            System.out.println(e.target + " dbl");
        });
        p1_child_1.onMouseEnter(e -> {
        });
        p1_child_1.onMouseLeave(e -> {
        });
        // drag and drop
        p1_child_1.onMouseDragStart(e -> {
            e.target.disconnectFromParent();
            e.target.zIndex = 100;
            e.target.transform.x = Widgets.getPointerX();
            e.target.transform.y = Widgets.getPointerY();
        });
        p1_child_1.onMouseDrag(e -> {
            e.target.transform.x = Widgets.getPointerX();
            e.target.transform.y = Widgets.getPointerY();
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


        Widgets.add(panel_1);
        Widgets.add(panel_2);
        Widgets.add(rect);
    }

    @Override
    public boolean mouseButtonsDown(int mouseX, int mouseY, @NotNull Array<Mouse.Button> buttons) {
        //System.out.println("screen");
        return true;
    }


    @Override
    public void update() {
        //moveWindow();
        Widgets.update();

        Graphics.bindFrameBuffer(null);
        GL11.glClearColor(0.01f,0.01f,0.01f,1);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_STENCIL_BUFFER_BIT); // should probably clear the stencil

        if (Input.keyboard.isKeyPressed(Keyboard.Key.D)) {
            p1_child_1.transform.x += 1;
        }
        if (Input.keyboard.isKeyPressed(Keyboard.Key.A)) {
            panel_1.transform.x -= 1;
        }
        if (Input.keyboard.isKeyPressed(Keyboard.Key.W)) {
            panel_1.transform.y += 1;
        }
        if (Input.keyboard.isKeyPressed(Keyboard.Key.S)) {
            panel_1.transform.y -= 1;
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
        Widgets.render(renderer2D);
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
