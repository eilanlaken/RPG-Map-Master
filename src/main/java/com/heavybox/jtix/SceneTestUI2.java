package com.heavybox.jtix;

import com.heavybox.jtix.application.Scene;
import com.heavybox.jtix.graphics.Camera;
import com.heavybox.jtix.graphics.Graphics;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.input.Input;
import com.heavybox.jtix.input.Keyboard;
import com.heavybox.jtix.widgets_4.Widget;
import com.heavybox.jtix.widgets_4.WidgetInputSlider;
import com.heavybox.jtix.widgets_4.WidgetText;
import org.lwjgl.opengl.GL11;

public class SceneTestUI2 implements Scene {

    // create basic UI elements. 
    public Renderer2D renderer2D = new Renderer2D();
    Camera camera = new Camera(Camera.Mode.ORTHOGRAPHIC, Graphics.getWindowWidth(), Graphics.getWindowHeight(), 1, 0, 100, 1);

    WidgetText widgetText = new WidgetText("hello");
    WidgetText widgetText2 = new WidgetText("hello2");
    WidgetInputSlider slider = new WidgetInputSlider();

    @Override
    public void setup() {
        widgetText2.localTransform.x = 200;
        widgetText2.localTransform.y = 200;

        widgetText.addChild(widgetText2);
        widgetText.anchor = Widget.Anchor.CENTER_LEFT;
        widgetText.anchorX = 200;

        slider.localTransform.deg = 30;
        slider.localTransform.x = 100;
        slider.localTransform.y = 100;
        slider.onMouseClick = (e) -> { // TODO: remove.
            System.out.println(e.mouseLocalX);
            System.out.println(e.mouseLocalY);
            return false;
        };
        slider.onMouseEnter = (e) -> {
            System.out.println("mouse enter.");
            return false;
        };
        slider.onMouseLeave = (e) -> {
            System.out.println("mouse leave.");
            return false;
        };

    }

    @Override
    public void start() {

    }

    @Override
    public void update() {
        //widgetText.update(1);
        slider.update(1);
        if (Input.keyboard.isKeyPressed(Keyboard.Key.W)) {
            ///widgetText.localTransform.deg += 2;
        }

        GL11.glClearColor(0.01f,0.01f,0.01f,1);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_STENCIL_BUFFER_BIT); // should probably clear the stencil

        renderer2D.begin();
        //widgetText.render(renderer2D);
        slider.render(renderer2D);
        renderer2D.end();
    }

    @Override
    public void finish() {

    }

    @Override
    public void windowResized(int width, int height) {
        camera.viewportWidth = width;
        camera.viewportHeight = height;
        camera.update();
    }

}
