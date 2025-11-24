package com.heavybox.jtix;

import com.heavybox.jtix.application.Scene;
import com.heavybox.jtix.graphics.Camera;
import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Graphics;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.input.Input;
import com.heavybox.jtix.input.Keyboard;
import com.heavybox.jtix.widgets_4.*;
import org.lwjgl.opengl.GL11;

public class SceneTestUI2 implements Scene {

    // create basic UI elements. 
    public Renderer2D renderer2D = new Renderer2D();
    Camera camera = new Camera(Camera.Mode.ORTHOGRAPHIC, Graphics.getWindowWidth(), Graphics.getWindowHeight(), 1, 0, 100, 1);

    WidgetText widgetText = new WidgetText("hello");
    WidgetText widgetText2 = new WidgetText("hello2");
    WidgetInputSlider slider = new WidgetInputSlider();
    WidgetInputCheckbox checkbox = new WidgetInputCheckbox();
    WidgetShapeCircle circle = new WidgetShapeCircle(30,22, Color.WHITE);
    WidgetShapeLine line = new WidgetShapeLine(300,22, Color.RED);
    WidgetShapeRectangle rect = new WidgetShapeRectangle(250,150,Color.YELLOW);

    WidgetImage image = new WidgetImage("assets/engine-tests/simpleImage.png");

    WidgetContainer container = new WidgetContainer();

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

        circle.type = WidgetShape.Type.OUTLINE;
        line.type = WidgetShape.Type.BORDER;

        //image.width = 100;
        //image.height = 100;
        image.border = true;

        container.boxWidthSizing = WidgetContainer.Sizing.STATIC;
        container.boxHeightSizing = WidgetContainer.Sizing.STATIC;
        container.boxWidth = 200;
        container.boxHeight = 400;

    }

    @Override
    public void start() {

    }

    @Override
    public void update() {
        //widgetText.update(1);
        container.update(1);
        if (Input.keyboard.isKeyPressed(Keyboard.Key.W)) {
            ///widgetText.localTransform.deg += 2;
        }

        GL11.glClearColor(0.01f,0.01f,0.01f,1);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_STENCIL_BUFFER_BIT); // should probably clear the stencil

        renderer2D.begin();
        container.render(renderer2D);
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
