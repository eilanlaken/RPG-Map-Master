package com.heavybox.jtix;

import com.heavybox.jtix.application.Scene;
import com.heavybox.jtix.assets.Assets;
import com.heavybox.jtix.graphics.*;
import com.heavybox.jtix.input.Input;
import com.heavybox.jtix.input.Keyboard;
import com.heavybox.jtix.input.Mouse;
import com.heavybox.jtix.math.Vector3;
import com.heavybox.jtix.tools.ToolsTexturePacker;
import com.heavybox.jtix.z.*;
import org.lwjgl.opengl.GL11;

// contact points polygon vs polygon:
// https://www.youtube.com/watch?v=5gDC1GU3Ivg
public class SceneDemo_4 implements Scene {

    private Renderer2D renderer2D;
    public final Camera camera = new Camera(Camera.Mode.ORTHOGRAPHIC, Graphics.getWindowWidth(), Graphics.getWindowHeight(), 2, 0, 100, 75);

    // tools - refactor immediately after working version
    public Map map;
    public Tool[] tools = new Tool[10];
    public int activeTool = 0;

    public SceneDemo_4() {
        renderer2D = new Renderer2D();
    }

    @Override
    public void setup() {
        try {
            // We don't pack layer 0
            // We don't pack layer 1
            // pack layer 2
            ToolsTexturePacker.packTextures("assets/texture-packs", "layer_2", 0, 2, ToolsTexturePacker.TexturePackSize.XX_LARGE_8192, "assets/textures-layer-2", true);
            // pack layer 3
            ToolsTexturePacker.packTextures("assets/texture-packs", "layer_3", 0, 2, ToolsTexturePacker.TexturePackSize.XX_LARGE_8192, "assets/textures-layer-3", true);
            // pack layer 4
            ToolsTexturePacker.packTextures("assets/texture-packs", "layer_4", 0, 2, ToolsTexturePacker.TexturePackSize.XX_LARGE_8192, "assets/textures-layer-4", true);
            // pack layer 5 (decorations)
            ToolsTexturePacker.packTextures("assets/texture-packs", "layer_5", 0, 2, ToolsTexturePacker.TexturePackSize.XX_LARGE_8192, "assets/textures-layer-5", true);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        // load async
        // layer 0
        Assets.loadTexture("assets/textures-layer-0/terrain-grass_1920x1080.png", Texture.FilterMag.LINEAR, Texture.FilterMin.LINEAR_MIPMAP_LINEAR, Texture.Wrap.MIRRORED_REPEAT, Texture.Wrap.MIRRORED_REPEAT, 1);
        Assets.loadTexture("assets/textures-layer-0/terrain-water_1920x1080.png", Texture.FilterMag.LINEAR, Texture.FilterMin.LINEAR_MIPMAP_LINEAR, Texture.Wrap.MIRRORED_REPEAT, Texture.Wrap.MIRRORED_REPEAT, 1);
        Assets.loadTexture("assets/textures-layer-0/terrain-rock_1920x1080.jpg", Texture.FilterMag.LINEAR, Texture.FilterMin.LINEAR_MIPMAP_LINEAR, Texture.Wrap.MIRRORED_REPEAT, Texture.Wrap.MIRRORED_REPEAT, 1);
        Assets.loadTexturePack("assets/texture-packs/layer_3.yml");
        // layer 1
        Assets.loadTexture("assets/textures-layer-1/terrain-wheat-field-base_0.png", Texture.FilterMag.LINEAR, Texture.FilterMin.LINEAR_MIPMAP_LINEAR, Texture.Wrap.MIRRORED_REPEAT, Texture.Wrap.MIRRORED_REPEAT, 1);
        Assets.loadTexture("assets/textures-layer-1/terrain-wheat-field-base_1.png", Texture.FilterMag.LINEAR, Texture.FilterMin.LINEAR_MIPMAP_LINEAR, Texture.Wrap.MIRRORED_REPEAT, Texture.Wrap.MIRRORED_REPEAT, 1);
        Assets.loadTexture("assets/textures-layer-1/terrain-wheat-field-base_2.png", Texture.FilterMag.LINEAR, Texture.FilterMin.LINEAR_MIPMAP_LINEAR, Texture.Wrap.MIRRORED_REPEAT, Texture.Wrap.MIRRORED_REPEAT, 1);
        Assets.loadTexture("assets/textures-layer-1/terrain-wheat-field-base_3.png", Texture.FilterMag.LINEAR, Texture.FilterMin.LINEAR_MIPMAP_LINEAR, Texture.Wrap.MIRRORED_REPEAT, Texture.Wrap.MIRRORED_REPEAT, 1);
        Assets.loadTexture("assets/textures-layer-1/terrain-wheat-field-base_4.png", Texture.FilterMag.LINEAR, Texture.FilterMin.LINEAR_MIPMAP_LINEAR, Texture.Wrap.MIRRORED_REPEAT, Texture.Wrap.MIRRORED_REPEAT, 1);
        Assets.loadTexture("assets/textures-layer-1/terrain-wheat-field-harvest-full.png", Texture.FilterMag.LINEAR, Texture.FilterMin.LINEAR_MIPMAP_LINEAR, Texture.Wrap.MIRRORED_REPEAT, Texture.Wrap.MIRRORED_REPEAT, 1);
        Assets.loadTexture("assets/textures-layer-1/terrain-wheat-field-harvest-part.png", Texture.FilterMag.LINEAR, Texture.FilterMin.LINEAR_MIPMAP_LINEAR, Texture.Wrap.MIRRORED_REPEAT, Texture.Wrap.MIRRORED_REPEAT, 1);
        Assets.loadTexture("assets/textures-layer-1/terrain-wheat-field-lines.png", Texture.FilterMag.LINEAR, Texture.FilterMin.LINEAR_MIPMAP_LINEAR, Texture.Wrap.MIRRORED_REPEAT, Texture.Wrap.MIRRORED_REPEAT, 1);
        Assets.finishLoading();

        map = new Map(false);

        tools[0] = new ToolTerrain(map);
        tools[1] = new ToolStampPlants(map);
        tools[2] = new ToolStampGround(map);
        tools[3] = new ToolWheatFields(map);
        tools[4] = new ToolStampCastles(map);
        tools[5] = new ToolStampCityBlock(map);
        tools[6] = new ToolStampRuralBlock(map);
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
        float delta = Graphics.getDeltaTime();
        // handle mouse input and camera movement
        Vector3 screen = new Vector3(Input.mouse.getX(), Input.mouse.getY(), 0);
        camera.unProject(screen);
        if (Input.mouse.isButtonPressed(Mouse.Button.MIDDLE) && !Input.keyboard.isKeyPressed(Keyboard.Key.LEFT_CONTROL)) {
            camera.position.x -= 1.5f * Input.mouse.getXDelta();
            camera.position.y += 1.5f * Input.mouse.getYDelta();
            // TODO: set zoom limits
        } else if (Input.mouse.isButtonPressed(Mouse.Button.MIDDLE) && Input.keyboard.isKeyPressed(Keyboard.Key.LEFT_CONTROL)) {
            camera.zoom += Input.mouse.getYDelta() * 0.05f;
        }
        //System.out.println(screen);
        // handle keyboard input
        if (Input.keyboard.isKeyJustPressed(Keyboard.Key.KEY_1) && activeTool != 0) {
            tools[activeTool].deactivate();
            activeTool = 0; // terrain tool
            tools[activeTool].activate();
        } else if (Input.keyboard.isKeyJustPressed(Keyboard.Key.KEY_2) && activeTool != 1) {
            tools[activeTool].deactivate();
            activeTool = 1; // terrain tool
            tools[activeTool].activate();
        } else if (Input.keyboard.isKeyJustPressed(Keyboard.Key.KEY_3) && activeTool != 2) {
            tools[activeTool].deactivate();
            activeTool = 2; // terrain tool
            tools[activeTool].activate();
        } else if (Input.keyboard.isKeyJustPressed(Keyboard.Key.KEY_4) && activeTool != 3) {
            tools[activeTool].deactivate();
            activeTool = 3; // terrain tool
            tools[activeTool].activate();
        } else if (Input.keyboard.isKeyJustPressed(Keyboard.Key.KEY_5) && activeTool != 4) {
            tools[activeTool].deactivate();
            activeTool = 4; // terrain tool
            tools[activeTool].activate();
        } else if (Input.keyboard.isKeyJustPressed(Keyboard.Key.KEY_6) && activeTool != 5) {
            tools[activeTool].deactivate();
            activeTool = 5; // terrain tool
            tools[activeTool].activate();
        } else if (Input.keyboard.isKeyJustPressed(Keyboard.Key.KEY_7) && activeTool != 6) {
            tools[activeTool].deactivate();
            activeTool = 6; // terrain tool
            tools[activeTool].activate();
        }
        tools[activeTool].x = screen.x;
        tools[activeTool].y = screen.y;
        tools[activeTool].update(Graphics.getDeltaTime());

        map.update(delta);
        map.render(renderer2D);

        FrameBufferBinder.bind(null);
        GL11.glClearColor(0.01f,0.01f,0.01f,1);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_STENCIL_BUFFER_BIT); // should probably clear the stencil
        renderer2D.begin(camera);
        renderer2D.drawTexture(map.getTexture(), 0, 0, 0, 1, 1);
        renderer2D.end();

        // draw tools overlay
        renderer2D.begin(camera);
        if (activeTool != -1) tools[activeTool].renderToolOverlay(renderer2D, screen.x, screen.y);
        renderer2D.end();

        // draw UI
        renderer2D.begin();
        renderer2D.end();

    }

    private void handleInput() {

    }

    @Override
    public void windowResized(int width, int height) {
        camera.viewportWidth = width;
        camera.viewportHeight = height;
        camera.update();
    }

}
