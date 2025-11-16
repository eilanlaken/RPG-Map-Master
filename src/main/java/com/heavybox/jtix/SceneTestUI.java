package com.heavybox.jtix;

import com.heavybox.jtix.application.Scene;
import com.heavybox.jtix.graphics.Renderer2D;

public class SceneTestUI implements Scene {

    // create basic UI elements. 
    public Renderer2D renderer2D = new Renderer2D();

    @Override
    public void setup() {
    }

    @Override
    public void start() {

    }

    @Override
    public void update() {
        renderer2D.begin();
        renderer2D.end();
    }

    @Override
    public void finish() {

    }

}
