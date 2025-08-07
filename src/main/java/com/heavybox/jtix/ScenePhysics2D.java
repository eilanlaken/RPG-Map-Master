package com.heavybox.jtix;

import com.heavybox.jtix.application.Scene;
import com.heavybox.jtix.graphics.*;
import com.heavybox.jtix.input.Input;
import com.heavybox.jtix.input.Keyboard;
import com.heavybox.jtix.input.Mouse;
import com.heavybox.jtix.math.Vector3;
import com.heavybox.jtix.physics2d.Body2D;
import com.heavybox.jtix.physics2d.World2D;
import org.lwjgl.opengl.GL11;

// contact points polygon vs polygon:
// https://www.youtube.com/watch?v=5gDC1GU3Ivg
public class ScenePhysics2D implements Scene {

    private Renderer2D renderer2DOld;
    private Camera camera;
    private World2D world = new World2D();

    private Body2D body_a;
    private Body2D body_b;

    Texture texture;

    public ScenePhysics2D() {
        renderer2DOld = new Renderer2D();
    }

    @Override
    public void setup() {
        texture = new Texture("assets/cardAFittingEnd.png");
    }

    @Override
    public void finish() {

    }

    @Override
    public void start() {
        camera = new Camera(Camera.Mode.ORTHOGRAPHIC, 640f/32, 480f/32, 1, 0, 100, 75);
        camera.update();

        world.createBodyRectangle(null, Body2D.MotionType.STATIC,
                0, -5,0,
                0f,0f,0,
                1000, 1, 1, 0.8f, false, 1,
                10, 0.5f, 0, 0, 0);

        world.setGravity(0,-10);
    }


    @Override
    public void update() {
        world.update(Graphics.getDeltaTime());
        Vector3 screen = new Vector3(Input.mouse.getX(), Input.mouse.getY(), 0);
        camera.unProject(screen);

        if (Input.mouse.isButtonClicked(Mouse.Button.LEFT)) {

            body_a = world.createBodyCircle(null, Body2D.MotionType.NEWTONIAN,
                    screen.x, screen.y, 0,
                    0f, 0f, 0,
                    1, 1, 1,0.2f, false, 1,
                    0.5f);
        }

        if (Input.mouse.isButtonClicked(Mouse.Button.RIGHT)) {
            body_b = world.createBodyCircle(null, Body2D.MotionType.NEWTONIAN,
                    screen.x, screen.y, 0,
                    0f, 0f, 0,
                    1, 1, 1,0.2f, false, 1,
                    0.5f);
        }


        if (Input.keyboard.isKeyJustPressed(Keyboard.Key.S)) {
            //world.createConstraintDistance(body_a, body_b, 4);
        }

        if (Input.keyboard.isKeyPressed(Keyboard.Key.R)) {
            //body_a.applyForce(1,0, body_a.shape.x(), body_a.shape.y() + 0.2f);
        }

        if (Input.keyboard.isKeyJustPressed(Keyboard.Key.SPACE)) {
            //world.createConstraintWeld(body_a, body_b, new Vector2(1,0));
        }

        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glClearColor(0,0,0,1);

        renderer2DOld.begin(camera);
        world.render(renderer2DOld);
        renderer2DOld.end();

        renderer2DOld.begin();
        renderer2DOld.drawTexture(texture, 0, 0, 0, 1,1);
        renderer2DOld.end();
    }



}
