package com.heavybox.jtix;

import com.heavybox.jtix.application.Application;
import com.heavybox.jtix.application.Scene;
import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Graphics;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.input.Input;
import com.heavybox.jtix.input.Keyboard;
import com.heavybox.jtix.input.Mouse;
import com.heavybox.jtix.widgets_2.WidgetShapeRectangle;
import com.heavybox.jtix.widgets_2.Widgets;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

public class SceneInput_3 implements Scene {

    WidgetShapeRectangle parent = new WidgetShapeRectangle(50,50);
    WidgetShapeRectangle child_1 = new WidgetShapeRectangle(50,50);
    WidgetShapeRectangle child_2 = new WidgetShapeRectangle(50,50);
    Renderer2D renderer2D = new Renderer2D();

    @Override
    public void start() {
        //rectangle_1.transform.x = 100;
        //rectangle_1.transform.y = 100;

        child_1.transform.x = 60;
        child_1.transform.y = -60;

        child_2.transform.x = -60;
        child_2.transform.y = -60;


        parent.onMouseDoubleClick(e -> {
            parent.color = Color.randomOpaque().toFloatBits();
            System.out.println("clicked parent");
        });
        child_1.onMouseClick(e -> {
            child_1.color = Color.randomOpaque().toFloatBits();
            System.out.println("clicked child 1");
        });
        child_2.onMouseClick(e -> {
            child_2.color = Color.randomOpaque().toFloatBits();
            System.out.println("clicked child 2");
        });

        parent.onMouseEnter(e -> {
            parent.color = Color.RED.toFloatBits();
        });
        parent.onMouseLeave(e -> {
            parent.color = Color.GREEN.toFloatBits();
        });

        child_1.onMouseEnter(e -> {
            child_1.color = Color.RED.toFloatBits();
        });
        child_1.onMouseLeave(e -> {
            child_1.color = Color.GREEN.toFloatBits();
        });

        child_2.onMouseEnter(e -> {
            child_2.color = Color.RED.toFloatBits();
        });
        child_2.onMouseLeave(e -> {
            child_2.color = Color.YELLOW.toFloatBits();
        });


        parent.connectChild(child_1);
        parent.connectChild(child_2);
        Widgets.add(parent);
    }

    @Override
    public boolean mouseButtonsDown(int mouseX, int mouseY, @NotNull Array<Mouse.Button> buttons) {
        //System.out.println("screen");
        return true;
    }

    boolean draggingWindow = false;
    int dragOffsetX = 0;
    int dragOffsetY = 0;
    @Override
    public void update() {
        // TODO: drag the window using:
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

        System.out.println(Input.mouse.getMonitorX());

        Widgets.update();

        Graphics.bindFrameBuffer(null);
        GL11.glClearColor(0.01f,0.01f,0.01f,1);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_STENCIL_BUFFER_BIT); // should probably clear the stencil

        if (Input.keyboard.isKeyPressed(Keyboard.Key.D)) {
            child_1.transform.x += 1;
        }
        if (Input.keyboard.isKeyPressed(Keyboard.Key.A)) {
            child_1.transform.x -= 1;
        }
        if (Input.keyboard.isKeyPressed(Keyboard.Key.W)) {
            child_1.transform.y += 1;
        }
        if (Input.keyboard.isKeyPressed(Keyboard.Key.S)) {
            child_1.transform.y -= 1;
        }
        if (Input.keyboard.isKeyPressed(Keyboard.Key.Q)) {
            child_1.transform.deg += 1;
        }


        if (Input.keyboard.isKeyJustPressed(Keyboard.Key.KEY_1)) {
            child_1.inputLayer = 8;
            child_2.inputLayer = 9;
        }

        if (Input.keyboard.isKeyJustPressed(Keyboard.Key.KEY_2)) {
            child_1.inputLayer = 9;
            child_2.inputLayer = 8;
        }

        renderer2D.begin();
        Widgets.render(renderer2D);
        renderer2D.end();
    }

    @Override
    public boolean isActive() {
        return true;
    }

    @Override
    public void finish() {

    }

}
