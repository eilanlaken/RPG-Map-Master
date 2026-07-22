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

public class SceneInput_4 implements Scene {

    WidgetShapeRectangle panel_1 = new WidgetShapeRectangle(250,250);
    WidgetShapeRectangle p1_child_1 = new WidgetShapeRectangle(80,80);
    WidgetShapeRectangle p1_child_2 = new WidgetShapeRectangle(80,80);

    WidgetShapeRectangle panel_2 = new WidgetShapeRectangle(250,250);
    WidgetShapeRectangle p2_child_1 = new WidgetShapeRectangle(80,80);
    WidgetShapeRectangle p2_child_2 = new WidgetShapeRectangle(80,80);

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

        Widgets.add(panel_1);
        Widgets.add(panel_2);
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
            p1_child_1.transform.x -= 1;
        }
        if (Input.keyboard.isKeyPressed(Keyboard.Key.W)) {
            p1_child_1.transform.y += 1;
        }
        if (Input.keyboard.isKeyPressed(Keyboard.Key.S)) {
            p1_child_1.transform.y -= 1;
        }
        if (Input.keyboard.isKeyPressed(Keyboard.Key.Q)) {
            p1_child_1.transform.deg += 1;
        }


        if (Input.keyboard.isKeyJustPressed(Keyboard.Key.KEY_1)) {
            p1_child_1.inputLayer = 8;
            p1_child_2.inputLayer = 9;
        }

        if (Input.keyboard.isKeyJustPressed(Keyboard.Key.KEY_2)) {
            p1_child_1.inputLayer = 9;
            p1_child_2.inputLayer = 8;
        }

        renderer2D.begin();
        Widgets.render(renderer2D);
        renderer2D.end();
    }

    // TODO: make components that drag window for a professional look and feel
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
