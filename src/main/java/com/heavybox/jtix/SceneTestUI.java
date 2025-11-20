package com.heavybox.jtix;

import com.heavybox.jtix.application.Scene;
import com.heavybox.jtix.graphics.Camera;
import com.heavybox.jtix.graphics.Graphics;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.widgets_3.NodeInputCheckbox;
import com.heavybox.jtix.widgets_3.NodeInputSlider;
import com.heavybox.jtix.widgets_3.NodeText;

public class SceneTestUI implements Scene {

    // create basic UI elements. 
    public Renderer2D renderer2D = new Renderer2D();
    NodeInputCheckbox checkbox = new NodeInputCheckbox();
    NodeInputSlider slider = new NodeInputSlider();
    NodeText text = new NodeText();
    Camera camera = new Camera(Camera.Mode.ORTHOGRAPHIC, Graphics.getWindowWidth(), Graphics.getWindowHeight(), 1, 0, 100, 1);

    com.heavybox.jtix.widgets.NodeInputCheckbox a = new com.heavybox.jtix.widgets.NodeInputCheckbox();

    @Override
    public void setup() {
        checkbox.setValue(true);
        text.text = "hello";
    }

    @Override
    public void start() {

    }

    @Override
    public void update() {
        checkbox.update(1);
        slider.update(1);

        renderer2D.begin();
        //checkbox.render(renderer2D, 0, 0, 0, 1, 1);
        slider.setValue(2.8f);
        slider.render(renderer2D, 200, 0, 30, 2, 2);
        //text.render(renderer2D, 0, 0, 0, 1, 1);
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
