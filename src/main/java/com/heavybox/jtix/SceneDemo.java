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
import com.heavybox.jtix.widgets_4.Widget;
import com.heavybox.jtix.widgets_4.Widgets;
import com.heavybox.jtix.z.*;
import org.lwjgl.opengl.GL11;

// contact points polygon vs polygon:
// https://www.youtube.com/watch?v=5gDC1GU3Ivg
public class SceneDemo implements Scene, RPGMapMakerScene {

    public static int width = 2048;
    public static int height = 2048;

    private final Renderer2D renderer2D;
    public final Camera camera = new Camera(Camera.Mode.ORTHOGRAPHIC, Graphics.getWindowWidth(), Graphics.getWindowHeight(), 2, 0, 100, 75);

    // tools - refactor immediately after working version
    public Map map;
    public Tool[] tools = new Tool[10];
    public int activeTool = 0;

    // user-interface
    private final Widget widgetActionsBar = new Widget();
    private final Widget widgetStatisticsBar = new Widget();
    private final Widget widgetTools = new Widget();
    private WidgetNodeToolbar toolbar;
    private WidgetNodeToolSettings toolSettings;

    public SceneDemo() {
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
            //We don't pack layer 4
            // pack ui assets
            ToolsTexturePacker.packTextures("assets/texture-packs", "user-interface", 0, 2, ToolsTexturePacker.TexturePackSize.XX_LARGE_8192, "assets/user-interface", true);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        // load async
        // layer 0
        Assets.loadTexture("assets/textures-layer-0/terrain_land_grass_0.jpg", Texture.FilterMag.LINEAR, Texture.FilterMin.LINEAR_MIPMAP_LINEAR, Texture.Wrap.MIRRORED_REPEAT, Texture.Wrap.MIRRORED_REPEAT, 1);
        Assets.loadTexture("assets/textures-layer-0/terrain_land_grass_1.jpg", Texture.FilterMag.LINEAR, Texture.FilterMin.LINEAR_MIPMAP_LINEAR, Texture.Wrap.MIRRORED_REPEAT, Texture.Wrap.MIRRORED_REPEAT, 1);
        Assets.loadTexture("assets/textures-layer-0/terrain_land_road_0.jpg", Texture.FilterMag.LINEAR, Texture.FilterMin.LINEAR_MIPMAP_LINEAR, Texture.Wrap.MIRRORED_REPEAT, Texture.Wrap.MIRRORED_REPEAT, 1);
        Assets.loadTexture("assets/textures-layer-0/terrain_land_sand_0.jpg", Texture.FilterMag.LINEAR, Texture.FilterMin.LINEAR_MIPMAP_LINEAR, Texture.Wrap.MIRRORED_REPEAT, Texture.Wrap.MIRRORED_REPEAT, 1);
        Assets.loadTexture("assets/textures-layer-0/terrain_land_stone_0.jpg", Texture.FilterMag.LINEAR, Texture.FilterMin.LINEAR_MIPMAP_LINEAR, Texture.Wrap.MIRRORED_REPEAT, Texture.Wrap.MIRRORED_REPEAT, 1);
        Assets.loadTexture("assets/textures-layer-0/terrain_liquid_water_0.jpg", Texture.FilterMag.LINEAR, Texture.FilterMin.LINEAR_MIPMAP_LINEAR, Texture.Wrap.MIRRORED_REPEAT, Texture.Wrap.MIRRORED_REPEAT, 1);
        Assets.loadTexture("assets/textures-layer-0/terrain_liquid_water_1.jpg", Texture.FilterMag.LINEAR, Texture.FilterMin.LINEAR_MIPMAP_LINEAR, Texture.Wrap.MIRRORED_REPEAT, Texture.Wrap.MIRRORED_REPEAT, 1);
        Assets.loadTexture("assets/textures-layer-0/terrain_liquid_water_2.jpg", Texture.FilterMag.LINEAR, Texture.FilterMin.LINEAR_MIPMAP_LINEAR, Texture.Wrap.MIRRORED_REPEAT, Texture.Wrap.MIRRORED_REPEAT, 1);
        Assets.loadTexture("assets/textures-layer-0/terrain_material_rock.jpg", Texture.FilterMag.LINEAR, Texture.FilterMin.LINEAR_MIPMAP_LINEAR, Texture.Wrap.MIRRORED_REPEAT, Texture.Wrap.MIRRORED_REPEAT, 1);

        // layer 1
//        Assets.loadTexture("assets/textures-layer-1/terrain-wheat-field-base_0.png", Texture.FilterMag.LINEAR, Texture.FilterMin.LINEAR_MIPMAP_LINEAR, Texture.Wrap.MIRRORED_REPEAT, Texture.Wrap.MIRRORED_REPEAT, 1);
//        Assets.loadTexture("assets/textures-layer-1/terrain-wheat-field-base_1.png", Texture.FilterMag.LINEAR, Texture.FilterMin.LINEAR_MIPMAP_LINEAR, Texture.Wrap.MIRRORED_REPEAT, Texture.Wrap.MIRRORED_REPEAT, 1);
//        Assets.loadTexture("assets/textures-layer-1/terrain-wheat-field-base_2.png", Texture.FilterMag.LINEAR, Texture.FilterMin.LINEAR_MIPMAP_LINEAR, Texture.Wrap.MIRRORED_REPEAT, Texture.Wrap.MIRRORED_REPEAT, 1);
//        Assets.loadTexture("assets/textures-layer-1/terrain-wheat-field-base_3.png", Texture.FilterMag.LINEAR, Texture.FilterMin.LINEAR_MIPMAP_LINEAR, Texture.Wrap.MIRRORED_REPEAT, Texture.Wrap.MIRRORED_REPEAT, 1);
//        Assets.loadTexture("assets/textures-layer-1/terrain-wheat-field-base_4.png", Texture.FilterMag.LINEAR, Texture.FilterMin.LINEAR_MIPMAP_LINEAR, Texture.Wrap.MIRRORED_REPEAT, Texture.Wrap.MIRRORED_REPEAT, 1);
//        Assets.loadTexture("assets/textures-layer-1/terrain-wheat-field-lines.png", Texture.FilterMag.LINEAR, Texture.FilterMin.LINEAR_MIPMAP_LINEAR, Texture.Wrap.MIRRORED_REPEAT, Texture.Wrap.MIRRORED_REPEAT, 1);
        Assets.loadTexture("assets/textures-layer-1/wheat_field_0.png", Texture.FilterMag.LINEAR, Texture.FilterMin.LINEAR_MIPMAP_LINEAR, Texture.Wrap.MIRRORED_REPEAT, Texture.Wrap.MIRRORED_REPEAT, 1);
        Assets.loadTexture("assets/textures-layer-1/wheat_field_1.png", Texture.FilterMag.LINEAR, Texture.FilterMin.LINEAR_MIPMAP_LINEAR, Texture.Wrap.MIRRORED_REPEAT, Texture.Wrap.MIRRORED_REPEAT, 1);
        Assets.loadTexture("assets/textures-layer-1/wheat_field_2.png", Texture.FilterMag.LINEAR, Texture.FilterMin.LINEAR_MIPMAP_LINEAR, Texture.Wrap.MIRRORED_REPEAT, Texture.Wrap.MIRRORED_REPEAT, 1);
        Assets.loadTexture("assets/textures-layer-1/wheat_field_3.png", Texture.FilterMag.LINEAR, Texture.FilterMin.LINEAR_MIPMAP_LINEAR, Texture.Wrap.MIRRORED_REPEAT, Texture.Wrap.MIRRORED_REPEAT, 1);
        Assets.loadTexture("assets/textures-layer-1/wheat_field_4.png", Texture.FilterMag.LINEAR, Texture.FilterMin.LINEAR_MIPMAP_LINEAR, Texture.Wrap.MIRRORED_REPEAT, Texture.Wrap.MIRRORED_REPEAT, 1);


        // layer 2

        // layer 3
        //Assets.loadTexturePack("assets/texture-packs/layer_3.yml");
        Assets.loadTexturePack("assets/texture-packs/layer_3.yml", Texture.FilterMag.NEAREST, Texture.FilterMin.LINEAR_MIPMAP_LINEAR, 4);

        // layer 5

        // user-interface
        Assets.loadTexturePack("assets/texture-packs/user-interface.yml");

        Assets.finishLoading();

        map = new Map(2048, 2048);
        //map = new Map(1920, 1080);

        tools[0] = new ToolBrushTerrain(this);
        tools[1] = new ToolBrushFields(this);
        tools[2] = new ToolBrushTrees(this);
        tools[3] = new ToolBrushProps(this);
        //tools[4] = new ToolBrushArchitecture(this); // TODO: redo architecture brush
        tools[5] = new ToolBrushRocks(this);
        tools[6] = new ToolBrushDecorations(this);

        // user - interface
        WidgetNodeActionsBar actionsBar = new WidgetNodeActionsBar();
        WidgetNodeStatisticsBar statisticsBar = new WidgetNodeStatisticsBar(this);
        toolbar = new WidgetNodeToolbar(this);
        toolSettings = new WidgetNodeToolSettings();

        widgetStatisticsBar.addNode(statisticsBar);
        widgetActionsBar.addNodes(actionsBar);
        widgetTools.addNodes(toolSettings, toolbar);
    }

    @Override
    public void finish() {

    }

    @Override
    public void start() {
        camera.update();
        // TODO: replace with static Tools
        Tools.initTools(this);
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

        // undo-redo
        boolean left_ctrl_pressed = Input.keyboard.isKeyPressed(Keyboard.Key.LEFT_CONTROL);
        boolean left_ctrl_just_pressed = Input.keyboard.isKeyJustReleased(Keyboard.Key.LEFT_CONTROL);
        boolean left_shift_pressed = Input.keyboard.isKeyPressed(Keyboard.Key.LEFT_SHIFT);
        boolean z_just_pressed = Input.keyboard.isKeyJustReleased(Keyboard.Key.Z);
        if (left_ctrl_pressed && left_shift_pressed && z_just_pressed) {
            map.redo();
            return;
        } else if (left_ctrl_pressed && z_just_pressed) {
            map.undo();
            return;
        } else if (left_ctrl_just_pressed && z_just_pressed) {
            map.undo();
            return;
        }

//        // handle keyboard input
//        if (Input.keyboard.isKeyJustReleased(Keyboard.Key.KEY_1) && activeTool != 0) {
//            tools[activeTool].deactivate();
//            activeTool = 0; // terrain tool
//            tools[activeTool].activate();
//        } else if (Input.keyboard.isKeyJustReleased(Keyboard.Key.KEY_2) && activeTool != 1) {
//            tools[activeTool].deactivate();
//            activeTool = 1; // tree tool
//            tools[activeTool].activate();
//        } else if (Input.keyboard.isKeyJustReleased(Keyboard.Key.KEY_3) && activeTool != 2) {
//            tools[activeTool].deactivate();
//            activeTool = 2; //
//            tools[activeTool].activate();
//        } else if (Input.keyboard.isKeyJustReleased(Keyboard.Key.KEY_4) && activeTool != 3) {
//            tools[activeTool].deactivate();
//            activeTool = 3; //
//            tools[activeTool].activate();
//        } else if (Input.keyboard.isKeyJustReleased(Keyboard.Key.KEY_5) && activeTool != 4) {
//            tools[activeTool].deactivate();
//            activeTool = 4; //
//            tools[activeTool].activate();
//        } else if (Input.keyboard.isKeyJustReleased(Keyboard.Key.KEY_6) && activeTool != 5) {
//            tools[activeTool].deactivate();
//            activeTool = 5; //
//            tools[activeTool].activate();
//        } else if (Input.keyboard.isKeyJustReleased(Keyboard.Key.KEY_7) && activeTool != 6) {
//            tools[activeTool].deactivate();
//            activeTool = 6; //
//            tools[activeTool].activate();
//        } else if (Input.keyboard.isKeyJustReleased(Keyboard.Key.KEY_8) && activeTool != 7) {
//            tools[activeTool].deactivate();
//            activeTool = 7; //
//            tools[activeTool].activate();
//        }
//        tools[activeTool].x = screen.x;
//        tools[activeTool].y = screen.y;
//        tools[activeTool].update(Graphics.getDeltaTime());

        // export placeholder
        if (Input.keyboard.isKeyJustReleased(Keyboard.Key.KP_0)) {
            map.exportLayerAsImage(0);
        } else if (Input.keyboard.isKeyJustReleased(Keyboard.Key.KP_1)) {
            map.exportLayerAsImage(1);
        } else if (Input.keyboard.isKeyJustReleased(Keyboard.Key.KP_2)) {
            map.exportLayerAsImage(2);
        } else if (Input.keyboard.isKeyJustReleased(Keyboard.Key.KP_3)) {
            map.exportLayerAsImage(3);
        } else if (Input.keyboard.isKeyJustReleased(Keyboard.Key.KP_4)) {
            //map.saveLayerAsImage(4);
        } else if (Input.keyboard.isKeyJustReleased(Keyboard.Key.KP_5)) {
            map.exportLayerAsImage(5);
        }

        map.update(delta);
        map.render(renderer2D);
        FrameBufferBinder.bind(null);
        GL11.glClearColor(0.01f,0.01f,0.01f,1);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_STENCIL_BUFFER_BIT); // should probably clear the stencil
        renderer2D.begin(camera);
        renderer2D.drawTexture(map.getTexture(), 0, 0, 0, 1, 1);
        renderer2D.end();

        // update tools
        Tools.update();
        // draw tools overlay
        renderer2D.begin(camera);
        //if (activeTool != -1) tools[activeTool].renderToolOverlay(renderer2D, screen.x, screen.y);
        Tools.render(renderer2D, screen.x, screen.y);
        renderer2D.end();

        // update user interface
        widgetActionsBar.update();
        widgetStatisticsBar.update();
        widgetTools.update();

        // render user interface
        renderer2D.begin();
//        widgetActionsBar.render(renderer2D);
//        widgetStatisticsBar.render(renderer2D);
//        widgetTools.render(renderer2D);
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

    @Override
    public void windowFilesDraggedAndDropped(Array<String> filePaths) {
        // TODO:
        /*
        This will create a new brush with the assets as the tokens.
         */
    }

    @Override
    public Map getMap() {
        return map;
    }

    @Override
    public Camera getCamera() {
        return camera;
    }

    @Override
    public void undo() {

    }

    @Override
    public void redo() {

    }

    @Override
    public void saveAs(String path) {

    }

    @Override
    public void exportAs(String path) {

    }

    @Override
    public void load(String path) {

    }

}
