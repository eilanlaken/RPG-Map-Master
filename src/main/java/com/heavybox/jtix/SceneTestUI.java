package com.heavybox.jtix;

import com.heavybox.jtix.application.Scene;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.widgets_2.Widget;
import com.heavybox.jtix.widgets_2.WidgetText;

public class SceneTestUI implements Scene {

    // create basic UI elements. 
    public WidgetText text = new WidgetText();
    public Renderer2D renderer2D = new Renderer2D();

    @Override
    public void setup() {
        text.text = "hello";
    }

    @Override
    public void start() {

    }

    @Override
    public void update() {
        renderer2D.begin();
        text.render(renderer2D);
        renderer2D.end();
    }

    @Override
    public void finish() {

    }

}
