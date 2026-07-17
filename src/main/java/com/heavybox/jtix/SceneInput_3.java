package com.heavybox.jtix;

import com.heavybox.jtix.application.Scene;
import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.collections.ArrayChar;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.input.Input;
import com.heavybox.jtix.input.InputEventHandler;
import com.heavybox.jtix.input.Mouse;
import com.heavybox.jtix.widgets_2.WidgetShapeRectangle;
import com.heavybox.jtix.widgets_2.Widgets;
import org.jetbrains.annotations.NotNull;

public class SceneInput_3 implements Scene {

    WidgetShapeRectangle rectangle = new WidgetShapeRectangle(200,100);
    Renderer2D renderer2D = new Renderer2D();

    @Override
    public void start() {
        rectangle.inputEventListener.onMouseDown = e -> {
            System.out.println("clicked rect");
        };
        Widgets.add(rectangle);
    }

    @Override
    public boolean mouseButtonsDown(int mouseX, int mouseY, @NotNull Array<Mouse.Button> buttons) {
        System.out.println("screen");
        return true;
    }

    @Override
    public void update() {
        Widgets.update();

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
