package com.heavybox.jtix;

import com.heavybox.jtix.application.Scene;
import com.heavybox.jtix.assets.Assets;
import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.graphics.*;
import com.heavybox.jtix.input.Input;
import com.heavybox.jtix.input.Keyboard;
import com.heavybox.jtix.input.Mouse;
import com.heavybox.jtix.math.Vector3;
import com.heavybox.jtix.tools.ToolsTexturePacker;
import com.heavybox.jtix.z.Command;
import com.heavybox.jtix.z.MapToken;
import com.heavybox.jtix.z.Tool;
import com.heavybox.jtix.z.ToolTerrain;
import org.lwjgl.opengl.GL11;

// contact points polygon vs polygon:
// https://www.youtube.com/watch?v=5gDC1GU3Ivg
public class SceneDemo_2 implements Scene {

    private Renderer2D renderer2D;
    public final Camera camera = new Camera(Camera.Mode.ORTHOGRAPHIC, Graphics.getWindowWidth(), Graphics.getWindowHeight(), 1, 0, 100, 75);

    private Texture terrainGrass;
    private Texture terrainWater;
    private Texture terrainSteepness;
    private Texture terrainRoad;

    public FrameBuffer terrainMask;
    public final Camera terrainMaskCamera = new Camera(Camera.Mode.ORTHOGRAPHIC, 1920, 1080, 1, 0, 100, 75);

    // tools - refactor immediately after working version
    public Texture terrainBrushErase;

    public Tool[] tools = new Tool[10];
    public int activeTool = 0;

    public Shader terrainShader;

    public SceneDemo_2() {
        renderer2D = new Renderer2D();
        terrainMask = new FrameBuffer(1920, 1080);
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

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }


        terrainGrass = Assets.get("assets/textures-layer-0/terrain-grass_1920x1080.png");
        terrainWater = Assets.get("assets/textures-layer-0/terrain-water_1920x1080.png");
        terrainSteepness = Assets.get("assets/textures-layer-0/terrain-rock_1920x1080.jpg");
        terrainBrushErase = new Texture("assets/tools/terrain-brush-erase.png");

        String terrainVertexShaderSrc = Assets.getFileContent("assets/shaders/terrain-mask.vert");
        String terrainFragmentShaderSrc = Assets.getFileContent("assets/shaders/terrain-mask.frag");
        this.terrainShader = new Shader(terrainVertexShaderSrc, terrainFragmentShaderSrc);

        tools[0] = new ToolTerrain(null);
    }

    @Override
    public void finish() {

    }

    @Override
    public void start() {
        camera.update();
        terrainMaskCamera.update();

        Vector3 screen = new Vector3();
        screen.set(Input.mouse.getX(), Input.mouse.getY(), 0);
        camera.unProject(screen);

        FrameBufferBinder.bind(terrainMask);
        GL11.glClearColor(1,1,1,1);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_STENCIL_BUFFER_BIT); // should probably clear the stencil
        renderer2D.begin(terrainMaskCamera);
        renderer2D.drawTexture(terrainBrushErase, 0,0,0,1,1);
        renderer2D.drawTexture(terrainBrushErase, 0,0,0,1,1);
        renderer2D.drawTexture(terrainBrushErase, 0,0,0,1,1);
        renderer2D.drawTexture(terrainBrushErase, 0,0,0,1,1);
        renderer2D.drawTexture(terrainBrushErase, 0,0,0,1,1);
        renderer2D.drawTexture(terrainBrushErase, 0,0,0,1,1);
        renderer2D.end();
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
        renderer2D.drawTexture(terrainWater, 0, 0, 0, 1, 1);
        renderer2D.setShader(terrainShader);
        renderer2D.setShaderAttribute("u_texture_mask", terrainMask.getColorAttachment0());
        renderer2D.setShaderAttribute("u_texture_steepness", terrainSteepness);
        renderer2D.drawTexture(terrainGrass, 0, 0, 0, 1, 1);
        //renderer2D.drawTexture(terrainMask.getColorAttachment0(), 0, 0, 0, 1, 1);
        renderer2D.end();

        // draw tools overlay
        renderer2D.begin(camera);
        if (activeTool != -1) tools[activeTool].renderToolOverlay(renderer2D, screen.x, screen.y, 0, 1,1);
        renderer2D.end();

        // draw UI
        renderer2D.begin();
        renderer2D.end();
    }

    @Override
    public void windowResized(int width, int height) {
        camera.viewportWidth = Graphics.getWindowWidth();
        camera.viewportHeight = Graphics.getWindowHeight();
    }

}
