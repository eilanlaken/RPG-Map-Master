package com.heavybox.jtix;

import com.heavybox.jtix.application.Scene;
import com.heavybox.jtix.assets.Assets;
import com.heavybox.jtix.graphics.*;
import com.heavybox.jtix.input.Input;
import com.heavybox.jtix.input.Keyboard;
import com.heavybox.jtix.input.Mouse;
import com.heavybox.jtix.math.Vector3;
import com.heavybox.jtix.physics2d.Body2D;
import org.lwjgl.opengl.GL11;

// contact points polygon vs polygon:
// https://www.youtube.com/watch?v=5gDC1GU3Ivg
public class SceneDemo implements Scene {

    private Renderer2D renderer2D;
    public final Camera camera = new Camera(Camera.Mode.ORTHOGRAPHIC, Graphics.getWindowWidth(), Graphics.getWindowHeight(), 1, 0, 100, 75);

    private Texture terrainGrass;
    private Texture terrainWater;
    private Texture terrainRoad;
    private Texture terrainSteepness;

    public SceneDemo() {
        renderer2D = new Renderer2D();
    }

    @Override
    public void setup() {
        // load async
        Assets.loadTexture("assets/textures-layer-0/terrain-grass_1920x1080.png", Texture.FilterMag.LINEAR, Texture.FilterMin.LINEAR, Texture.Wrap.MIRRORED_REPEAT, Texture.Wrap.MIRRORED_REPEAT, 1);
        Assets.finishLoading();

        terrainGrass = Assets.get("assets/textures-layer-0/terrain-grass_1920x1080.png");
    }

    @Override
    public void finish() {

    }

    @Override
    public void start() {
        camera.update();
    }


    @Override
    public void update() {
        Vector3 screen = new Vector3(Input.mouse.getX(), Input.mouse.getY(), 0);
        camera.unProject(screen);

        if (Input.mouse.isButtonPressed(Mouse.Button.MIDDLE) && !Input.keyboard.isKeyPressed(Keyboard.Key.LEFT_CONTROL)) {
            camera.position.x -= 1.5f * Input.mouse.getXDelta();
            camera.position.y += 1.5f * Input.mouse.getYDelta();
            // TODO: set zoom limits
        } else if (Input.mouse.isButtonPressed(Mouse.Button.MIDDLE) && Input.keyboard.isKeyPressed(Keyboard.Key.LEFT_CONTROL)) {
            camera.zoom += Input.mouse.getYDelta() * 0.05f;
        }

        FrameBufferBinder.bind(null);
        GL11.glClearColor(0.01f,0.01f,0.01f,1);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_STENCIL_BUFFER_BIT); // should probably clear the stencil
        renderer2D.begin(camera);
        renderer2D.drawTexture(terrainGrass, 0, 0, 0, 1, 1);
        renderer2D.end();

        renderer2D.begin();
        renderer2D.end();
    }



}
