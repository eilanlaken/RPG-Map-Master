package com.heavybox.jtix;

import com.heavybox.jtix.application.Scene;
import com.heavybox.jtix.graphics.Camera;
import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Graphics;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.input.Input;
import com.heavybox.jtix.input.InputLayer;
import com.heavybox.jtix.input.Mouse;
import com.heavybox.jtix.widgets.*;
import org.lwjgl.opengl.GL11;

public class SceneNewContainers implements Scene, InputLayer {

    public Renderer2D renderer2D = new Renderer2D();
    Camera camera = new Camera(Camera.Mode.ORTHOGRAPHIC, Graphics.getWindowWidth(), Graphics.getWindowHeight(), 1, 0, 100, 1);


    Widget widget_1 = new Widget();
    Widget widget_2 = new Widget();

    WidgetNodeContainerStack container = new WidgetNodeContainerStack();
    WidgetNodeShapeRectangle rectangle = new WidgetNodeShapeRectangle(300,200, Color.WHITE);

    @Override
    public void setup() {
        container.boxPaddingLeft = 10;
        container.boxPaddingRight = 10;
        container.boxPaddingBottom = 50;
        container.boxPaddingTop = 50;
        container.overflowX = WidgetNodeContainer.Overflow.VISIBLE;
        container.overflowY = WidgetNodeContainer.Overflow.VISIBLE;
        container.onMouseMiddleClick = e -> {
            System.out.println("in");
        };
        container.onMouseMiddleClickOutside = e -> {
            System.out.println("out");
        };

        container.anchor = WidgetNode.Anchor.PARENT_BOTTOM_LEFT;

        widget_1.addNodes(container);

        rectangle.anchor = WidgetNode.Anchor.PARENT_CENTER_RIGHT;
        rectangle.anchorX = 200;

        widget_2.addNode(rectangle);
    }

    @Override
    public void start() {

    }

    private WidgetNode currentRect;

    @Override
    public void update() {


        if (Input.mouse.isButtonJustPressed(Mouse.Button.RIGHT)) {
            WidgetNodeShapeRectangle rect = new WidgetNodeShapeRectangle(100,50, Color.random());
            container.addChild(rect);
            this.currentRect = rect;
        } else if (Input.mouse.isButtonJustPressed(Mouse.Button.LEFT)) {
            currentRect.anchor = WidgetNode.Anchor.CURSOR_BOTTOM_RIGHT;
            currentRect.anchorX = 50;
            currentRect.anchorY = 50;
        }

        widget_1.update();
        widget_2.update();

        GL11.glClearColor(0.01f,0.01f,0.01f,1);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_STENCIL_BUFFER_BIT); // should probably clear the stencil

        renderer2D.begin();
        widget_1.render(renderer2D);
        widget_2.render(renderer2D);
        renderer2D.end();
    }

    @Override
    public void finish() {

    }

    @Override
    public int getLevel() {
        return 0;
    }

    @Override
    public boolean mouseButtonDown(Mouse.Button button) {

        return true;
    }
}
