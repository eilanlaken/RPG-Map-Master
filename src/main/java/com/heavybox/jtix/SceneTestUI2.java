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
    WidgetShapeLine line = new WidgetShapeLine(300,22, Color.RED);
    WidgetShapeRectangle rect1 = new WidgetShapeRectangle(250,150,Color.YELLOW) {
        @Override
        public boolean maskChildren() {
            return true;
        }
    };
    WidgetShapeRectangle rect2 = new WidgetShapeRectangle(120,100,Color.RED){
        @Override
        public boolean maskChildren() {
            return true;
        }
    };
    WidgetShapeRectangle rect3 = new WidgetShapeRectangle(50,50,Color.BLUE){
        @Override
        public boolean maskChildren() {
            return true;
        }
    };

    WidgetImage image = new WidgetImage("assets/engine-tests/simpleImage.png");

    WidgetContainer container = new WidgetContainer();

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
        line.type = WidgetShape.Type.BORDER;

        image.width = 100;
        image.height = 100;
        image.border = true;

        container.boxWidthSizing = WidgetContainer.Sizing.STATIC;
        container.boxHeightSizing = WidgetContainer.Sizing.STATIC;
        container.boxWidth = 200;
        container.boxHeight = 400;
        container.boxPaddingBottom = 0;
        container.boxPaddingTop = 0;

        container.addChild(image);

        image.transform.x = 100;

        rect1.addChild(rect2);
        rect2.addChild(rect3);

        rect1.onMouseClick = (e) -> {
            System.out.println("rect 1");
            return false;
        };
        rect2.onMouseClick = (e) -> {
            System.out.println("rect 2");
            return false;
        };
        rect3.onMouseClick = (e) -> {
            System.out.println("rect 3");
            return false;
        };
        //rect2.addChild(rect3);


    }

    @Override
    public void start() {

    }

    @Override
    public void update() {
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

        //widgetText.update(1);
        container.update(1);
        rect1.update(1);
        if (Input.keyboard.isKeyPressed(Keyboard.Key.W)) {
            ///widgetText.localTransform.deg += 2;
        }

        GL11.glClearColor(0.01f,0.01f,0.01f,1);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_STENCIL_BUFFER_BIT); // should probably clear the stencil

        renderer2D.begin();
        //container.render(renderer2D);
        rect1.render(renderer2D);
        //rect2.render(renderer2D);
        //rect3.render(renderer2D);
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
