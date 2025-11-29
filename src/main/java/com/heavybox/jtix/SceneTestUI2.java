package com.heavybox.jtix;

import com.heavybox.jtix.application.Scene;
import com.heavybox.jtix.graphics.Camera;
import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Graphics;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.input.Input;
import com.heavybox.jtix.input.Keyboard;
import com.heavybox.jtix.input.Mouse;
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
    WidgetShapeLine line = new WidgetShapeLine(300,22, Color.RED);
    WidgetShapeRectangle rect1 = new WidgetShapeRectangle(350,250,Color.YELLOW);
    WidgetShapeRectangle rect2 = new WidgetShapeRectangle(350,250,Color.WHITE);
    WidgetShapeRectangle rect3 = new WidgetShapeRectangle(350,250,Color.GREEN);
    WidgetShapeRectangle rect4 = new WidgetShapeRectangle(350,250,Color.BROWN);

    WidgetImage image = new WidgetImage("assets/engine-tests/simpleImage.png");

    WidgetContainer container = new WidgetContainer();
    WidgetInputScrollbar scrollbar = new WidgetInputScrollbar();

    @Override
    public void setup() {


        widgetText2.transform.x = 200;
        widgetText2.transform.y = 200;

        widgetText.addChild(widgetText2);
        widgetText.anchor = Widget.Anchor.CENTER_LEFT;
        widgetText.anchorX = 200;

        slider.transform.deg = 30;
        slider.transform.x = 100;
        slider.transform.y = 100;

        rect2.type = WidgetShape.Type.FILLED;

        container.layoutWidthSizing = WidgetContainer.Sizing.DYNAMIC;
        container.layoutHeightSizing = WidgetContainer.Sizing.STATIC;
        container.layoutWidth = 200;
        container.layoutHeight = 600;
        container.boxPaddingLeft = 10;
        container.boxPaddingRight = 10;
        container.boxPaddingBottom = 100;

        //container.anchor = Widget.Anchor.CENTER_LEFT;
        container.layoutOverflowY = WidgetContainer.Overflow.SCROLLBAR;
        container.layoutOverflowX = WidgetContainer.Overflow.HIDDEN;

        container.onMouseUp = e -> {
            System.out.println(e.buttonLeft + " | " + e.buttonRight + " | " + e.buttonMiddle);
            return false;
        };
        container.onMouseDown = e -> {
            container.layoutOverflowY = WidgetContainer.Overflow.HIDDEN;
            return false;
        };



        rect1.onMouseLeftClick = (e) -> {
            System.out.println("rect 1");
            return false;
        };
        rect2.onMouseLeftClick = (e) -> {
            System.out.println("rect 2");
            return false;
        };
        rect3.onMouseLeftClick = (e) -> {
            System.out.println("rect 3");
            return false;
        };
        //rect2.addChild(rect3);

        container.layout = WidgetContainer.Layout.VERTICAL;
        container.addChild(rect1);
        container.addChild(rect2);
        container.addChild(rect3);
        container.addChild(rect4);
    }

    @Override
    public void start() {

    }

    @Override
    public void update() {
        //scrollbar.update(1);
        checkbox.update(1);
        if (Input.keyboard.isKeyPressed(Keyboard.Key.W)) {
            rect2.transform.y += 1;
        }
        if (Input.keyboard.isKeyPressed(Keyboard.Key.S)) {
            rect2.transform.y -= 1;
        }
        if (Input.keyboard.isKeyPressed(Keyboard.Key.A)) {
            rect2.transform.x += 1;
        }
        if (Input.keyboard.isKeyPressed(Keyboard.Key.D)) {
            rect2.transform.x -= 1;
        }

        if (Input.keyboard.isKeyPressed(Keyboard.Key.UP)) {
            rect3.transform.y += 1;
        }
        if (Input.keyboard.isKeyPressed(Keyboard.Key.DOWN)) {
            rect3.transform.y -= 1;
        }
        if (Input.keyboard.isKeyPressed(Keyboard.Key.RIGHT)) {
            rect3.transform.x += 1;
        }
        if (Input.keyboard.isKeyPressed(Keyboard.Key.LEFT)) {
            rect3.transform.x -= 1;
        }

        if (Input.keyboard.isKeyJustPressed(Keyboard.Key.G)) {
            container.layoutHeightSizing = WidgetContainer.Sizing.STATIC;
            container.layoutHeight = 20;
        }

        if (Input.mouse.isButtonJustPressed(Mouse.Button.RIGHT)) {
            //container.boxPaddingRight = 60;
        }

        //widgetText.update(1);
        container.update(1);
        //rect1.update(1);
        if (Input.keyboard.isKeyJustPressed(Keyboard.Key.SPACE)) {
            rect3.active = false;
        }

        GL11.glClearColor(0.01f,0.01f,0.01f,1);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_STENCIL_BUFFER_BIT); // should probably clear the stencil

        renderer2D.begin();
        container.render(renderer2D);
        //scrollbar.render(renderer2D);
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
