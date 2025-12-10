package com.heavybox.jtix;

import com.heavybox.jtix.application.Scene;
import com.heavybox.jtix.graphics.Camera;
import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Graphics;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.input.Input;
import com.heavybox.jtix.input.InputLayer;
import com.heavybox.jtix.input.Mouse;
import com.heavybox.jtix.widgets_4.Node;
import com.heavybox.jtix.widgets_4.NodeContainer;
import com.heavybox.jtix.widgets_4.NodeShapeRectangle;
import com.heavybox.jtix.widgets_4.Widget;
import org.lwjgl.opengl.GL11;

public class SceneWidgetsInput implements Scene, InputLayer {

    public Renderer2D renderer2D = new Renderer2D();
    Camera camera = new Camera(Camera.Mode.ORTHOGRAPHIC, Graphics.getWindowWidth(), Graphics.getWindowHeight(), 1, 0, 100, 1);


    Widget widget_1 = new Widget();
    Widget widget_2 = new Widget();

    NodeContainer container = new NodeContainer();
    NodeShapeRectangle rectangle = new NodeShapeRectangle(300,200, Color.WHITE);

    @Override
    public void setup() {
        container.layout = NodeContainer.Layout.VERTICAL;
        container.layoutWidthSizing = NodeContainer.Sizing.DYNAMIC;
        container.layoutHeightSizing = NodeContainer.Sizing.STATIC;
        container.layoutWidth = 200;
        container.layoutHeight = 300;
        container.boxPaddingLeft = 10;
        container.boxPaddingRight = 10;
        container.boxPaddingBottom = 50;
        container.boxPaddingTop = 50;
        container.layoutAddScrollbar = true;
        container.layoutOverflowY = NodeContainer.Overflow.HIDDEN;
        container.layoutOverflowX = NodeContainer.Overflow.HIDDEN;
        container.onMouseMiddleClick = e -> {
            System.out.println("in");
        };
        container.onMouseMiddleClickOutside = e -> {
            System.out.println("out");
        };

        widget_1.addNodes(container);

        rectangle.anchor = Node.Anchor.CENTER_RIGHT;
        rectangle.anchorX = 200;

        widget_2.addNode(rectangle);
    }

    @Override
    public void start() {

    }

    @Override
    public void update() {


        if (Input.mouse.isButtonJustPressed(Mouse.Button.RIGHT)) {
            NodeShapeRectangle rect = new NodeShapeRectangle(100,50, Color.random());
            container.addChild(rect);
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
