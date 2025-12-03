package com.heavybox.jtix;

import com.heavybox.jtix.application.Scene;
import com.heavybox.jtix.collections.ArrayFloat;
import com.heavybox.jtix.graphics.Camera;
import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Graphics;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.math.MathUtils;
import com.heavybox.jtix.math.Vector2;
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
    WidgetShapeRectangle rect1 = new WidgetShapeRectangle(350,250,Color.YELLOW);
    WidgetShapeRectangle rect2 = new WidgetShapeRectangle(350,250,Color.WHITE);
    WidgetShapeRectangle rect3 = new WidgetShapeRectangle(350,250,Color.GREEN);
    WidgetShapeRectangle rect4 = new WidgetShapeRectangle(350,250,Color.BROWN);

    WidgetImage image = new WidgetImage("assets/engine-tests/simpleImage.png");

    WidgetContainer container = new WidgetContainer();

    WidgetInputTextField textField = new WidgetInputTextField();

    WidgetInputScrollbar scrollbar = new WidgetInputScrollbar();

    ArrayFloat out = new ArrayFloat();

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
        container.layoutOverflowY = WidgetContainer.Overflow.HIDDEN;
        container.layoutOverflowX = WidgetContainer.Overflow.HIDDEN;

        container.onMouseMiddleClick = e -> {
            System.out.println("in");
            return false;
        };

        container.onMouseMiddleClickOutside = e -> {
            System.out.println("out");
            return false;
        };

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
        float delta = Graphics.getDeltaTime();
        //scrollbar.update(delta);

//        //scrollbar.update(1);
//        checkbox.update(1);
//        if (Input.keyboard.isKeyPressed(Keyboard.Key.W)) {
//            rect2.transform.y += 1;
//        }
//        if (Input.keyboard.isKeyPressed(Keyboard.Key.S)) {
//            rect2.transform.y -= 1;
//        }
//        if (Input.keyboard.isKeyPressed(Keyboard.Key.A)) {
//            rect2.transform.x += 1;
//        }
//        if (Input.keyboard.isKeyPressed(Keyboard.Key.D)) {
//            rect2.transform.x -= 1;
//        }
//
//        if (Input.keyboard.isKeyPressed(Keyboard.Key.UP)) {
//            rect3.transform.y += 1;
//        }
//        if (Input.keyboard.isKeyPressed(Keyboard.Key.DOWN)) {
//            rect3.transform.y -= 1;
//        }
//        if (Input.keyboard.isKeyPressed(Keyboard.Key.RIGHT)) {
//            rect3.transform.x += 1;
//        }
//        if (Input.keyboard.isKeyPressed(Keyboard.Key.LEFT)) {
//            rect3.transform.x -= 1;
//        }
//
//        if (Input.keyboard.isKeyJustReleased(Keyboard.Key.G)) {
//            container.layoutHeightSizing = WidgetContainer.Sizing.STATIC;
//            container.layoutHeight = 20;
//        }
//
//        if (Input.mouse.isButtonJustPressed(Mouse.Button.RIGHT)) {
//            //container.boxPaddingRight = 60;
//        }
//
//        //widgetText.update(1);
        //container.update(delta);
//        //rect1.update(1);
//        if (Input.keyboard.isKeyJustReleased(Keyboard.Key.SPACE)) {
//            rect3.active = false;
//        }

        //textField.update(delta);
        container.update(delta);

        GL11.glClearColor(0.01f,0.01f,0.01f,1);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_STENCIL_BUFFER_BIT); // should probably clear the stencil


        MathUtils.curveBezierGetPoints(out, 30, new Vector2(0,0), new Vector2(200,200), new Vector2(400,0));
        Vector2[] points = new Vector2[out.size / 2];
        for (int i = 0; i < points.length; i++) {
            points[i] = new Vector2();
            points[i].x = out.get(2 * i);
            points[i].y = out.get(2 * i + 1);
        }

        renderer2D.begin();
        renderer2D.drawCurveThin(points);
        //scrollbar.render(renderer2D);
        //container.render(renderer2D);

        //scrollbar.render(renderer2D);
        //textField.render(renderer2D);
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
