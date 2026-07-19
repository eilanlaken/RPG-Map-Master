package com.heavybox.jtix;

import com.heavybox.jtix.application.Scene;
import com.heavybox.jtix.collections.Array;
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

    WidgetShapeRectangle rectangle_1 = new WidgetShapeRectangle(50,50);
    WidgetShapeRectangle rectangle_2 = new WidgetShapeRectangle(50,50);
    WidgetShapeRectangle rectangle_3 = new WidgetShapeRectangle(50,50);
    WidgetShapeRectangle rectangle_4 = new WidgetShapeRectangle(50,50);
    Renderer2D renderer2D = new Renderer2D();

    @Override
    public void start() {
        rectangle_1.transform.x = 100;
        rectangle_1.transform.y = 100;

        rectangle_2.transform.x = 60;
        rectangle_2.transform.y = -60;

        rectangle_3.transform.x = -60;
        rectangle_3.transform.y = -60;

        rectangle_4.transform.x = -60;
        rectangle_4.transform.y = -60;


        rectangle_1.onMouseDown(e -> {
            System.out.println("clicked rect 1");
        });

        rectangle_2.onMouseDown(e -> {
            System.out.println("clicked rect 2");
        });

        rectangle_1.connectChild(rectangle_2);
        rectangle_1.connectChild(rectangle_3);
        Widgets.add(rectangle_1);
    }

    @Override
    public boolean mouseButtonsDown(int mouseX, int mouseY, @NotNull Array<Mouse.Button> buttons) {
        System.out.println("screen");
        return true;
    }



    @Override
    public void update() {
        Widgets.update();

        Graphics.bindFrameBuffer(null);
        GL11.glClearColor(0.01f,0.01f,0.01f,1);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_STENCIL_BUFFER_BIT); // should probably clear the stencil

        if (Input.keyboard.isKeyPressed(Keyboard.Key.D)) {
            rectangle_4.transform.x += 1;
        }
        if (Input.keyboard.isKeyPressed(Keyboard.Key.A)) {
            rectangle_4.transform.x -= 1;
        }
        if (Input.keyboard.isKeyPressed(Keyboard.Key.W)) {
            rectangle_4.transform.y += 1;
        }
        if (Input.keyboard.isKeyPressed(Keyboard.Key.S)) {
            rectangle_4.transform.y -= 1;
        }
        if (Input.keyboard.isKeyPressed(Keyboard.Key.Q)) {
            rectangle_4.transform.deg += 1;
        }


        if (Input.keyboard.isKeyJustPressed(Keyboard.Key.KEY_1)) {
            Widgets.remove(rectangle_3);
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
