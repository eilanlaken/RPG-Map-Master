package com.heavybox.jtix;

import com.google.gson.*;
import com.heavybox.jtix.application.Scene;
import com.heavybox.jtix.assets.Assets;
import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.graphics.*;
import com.heavybox.jtix.input.Input;
import com.heavybox.jtix.input.Keyboard;
import com.heavybox.jtix.input.Mouse;
import com.heavybox.jtix.math.Vector3;
import com.heavybox.jtix.tools.ToolsTexturePacker;
import com.heavybox.jtix.widgets.Widget;
import com.heavybox.jtix.z.*;
import com.heavybox.jtix.z.tools_new.*;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

// contact points polygon vs polygon:
// https://www.youtube.com/watch?v=5gDC1GU3Ivg
public class SceneDemo implements Scene, RPGMapMakerScene {

    // dev save and load
    public final String saveFile = null;// "C:\\Users\\eilan\\OneDrive\\Desktop\\Heavy Box Games\\projects\\RPG Map Master\\public\\maps\\map_num.map";

    public static int width = 2880;
    public static int height = 2880; //1620;

    private final Renderer2D renderer2D;
    public final Camera camera = new Camera(Camera.Mode.ORTHOGRAPHIC, Graphics.getWindowWidth(), Graphics.getWindowHeight(), 2, 0, 100, 75);

    // tools - refactor immediately after working version
    public Map map;
    public final Tool_new[] tools = new Tool_new[8];
    public int activeToolIndex = 4;
    public int activeLayerIndex = 1;

    // user-interface
    private final Widget widgetHelpBar = new Widget();

    /*
    private final Widget widgetActionsBar = new Widget();
    private final Widget widgetStatisticsBar = new Widget();
    private final Widget widgetTools = new Widget();
    private WidgetNodeToolbar toolbar;
    private WidgetNodeToolSettings toolSettings;
    */

    public SceneDemo() {
        renderer2D = new Renderer2D();
    }

    @Override
    public void start() {
        try {
            ToolsTexturePacker.packTextures("assets/texture-packs", "layer_3", 0, 2, ToolsTexturePacker.TexturePackSize.XX_LARGE_8192, "assets/textures-layer-3", true);
            ToolsTexturePacker.packTextures("assets/texture-packs", "user-interface", 0, 2, ToolsTexturePacker.TexturePackSize.XX_LARGE_8192, "assets/user-interface", true);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        // load async
        // brushes - add
        Assets.loadTexture("assets/brushes/brush_terrain_add_0.png", Texture.FilterMag.LINEAR, Texture.FilterMin.LINEAR_MIPMAP_LINEAR, Texture.Wrap.CLAMP_TO_EDGE, Texture.Wrap.CLAMP_TO_EDGE, 1);
        Assets.loadTexture("assets/brushes/brush_terrain_add_1.png", Texture.FilterMag.LINEAR, Texture.FilterMin.LINEAR_MIPMAP_LINEAR, Texture.Wrap.CLAMP_TO_EDGE, Texture.Wrap.CLAMP_TO_EDGE, 1);
        Assets.loadTexture("assets/brushes/brush_terrain_add_2.png", Texture.FilterMag.LINEAR, Texture.FilterMin.LINEAR_MIPMAP_LINEAR, Texture.Wrap.CLAMP_TO_EDGE, Texture.Wrap.CLAMP_TO_EDGE, 1);
        Assets.loadTexture("assets/brushes/brush_terrain_add_3.png", Texture.FilterMag.LINEAR, Texture.FilterMin.LINEAR_MIPMAP_LINEAR, Texture.Wrap.CLAMP_TO_EDGE, Texture.Wrap.CLAMP_TO_EDGE, 1);
        Assets.loadTexture("assets/brushes/brush_terrain_add_4.png", Texture.FilterMag.LINEAR, Texture.FilterMin.LINEAR_MIPMAP_LINEAR, Texture.Wrap.CLAMP_TO_EDGE, Texture.Wrap.CLAMP_TO_EDGE, 1);
        Assets.loadTexture("assets/brushes/brush_terrain_add_5.png", Texture.FilterMag.LINEAR, Texture.FilterMin.LINEAR_MIPMAP_LINEAR, Texture.Wrap.CLAMP_TO_EDGE, Texture.Wrap.CLAMP_TO_EDGE, 1);
        Assets.loadTexture("assets/brushes/brush_terrain_add_6.png", Texture.FilterMag.LINEAR, Texture.FilterMin.LINEAR_MIPMAP_LINEAR, Texture.Wrap.CLAMP_TO_EDGE, Texture.Wrap.CLAMP_TO_EDGE, 1);
        Assets.loadTexture("assets/brushes/brush_terrain_add_7.png", Texture.FilterMag.LINEAR, Texture.FilterMin.LINEAR_MIPMAP_LINEAR, Texture.Wrap.CLAMP_TO_EDGE, Texture.Wrap.CLAMP_TO_EDGE, 1);
        Assets.loadTexture("assets/brushes/brush_terrain_add_8.png", Texture.FilterMag.LINEAR, Texture.FilterMin.LINEAR_MIPMAP_LINEAR, Texture.Wrap.CLAMP_TO_EDGE, Texture.Wrap.CLAMP_TO_EDGE, 1);
        // brushes - sub
        Assets.loadTexture("assets/brushes/brush_terrain_sub_0.png", Texture.FilterMag.LINEAR, Texture.FilterMin.LINEAR_MIPMAP_LINEAR, Texture.Wrap.CLAMP_TO_EDGE, Texture.Wrap.CLAMP_TO_EDGE, 1);
        Assets.loadTexture("assets/brushes/brush_terrain_sub_1.png", Texture.FilterMag.LINEAR, Texture.FilterMin.LINEAR_MIPMAP_LINEAR, Texture.Wrap.CLAMP_TO_EDGE, Texture.Wrap.CLAMP_TO_EDGE, 1);
        Assets.loadTexture("assets/brushes/brush_terrain_sub_2.png", Texture.FilterMag.LINEAR, Texture.FilterMin.LINEAR_MIPMAP_LINEAR, Texture.Wrap.CLAMP_TO_EDGE, Texture.Wrap.CLAMP_TO_EDGE, 1);
        Assets.loadTexture("assets/brushes/brush_terrain_sub_3.png", Texture.FilterMag.LINEAR, Texture.FilterMin.LINEAR_MIPMAP_LINEAR, Texture.Wrap.CLAMP_TO_EDGE, Texture.Wrap.CLAMP_TO_EDGE, 1);
        Assets.loadTexture("assets/brushes/brush_terrain_sub_4.png", Texture.FilterMag.LINEAR, Texture.FilterMin.LINEAR_MIPMAP_LINEAR, Texture.Wrap.CLAMP_TO_EDGE, Texture.Wrap.CLAMP_TO_EDGE, 1);
        Assets.loadTexture("assets/brushes/brush_terrain_sub_5.png", Texture.FilterMag.LINEAR, Texture.FilterMin.LINEAR_MIPMAP_LINEAR, Texture.Wrap.CLAMP_TO_EDGE, Texture.Wrap.CLAMP_TO_EDGE, 1);
        Assets.loadTexture("assets/brushes/brush_terrain_sub_6.png", Texture.FilterMag.LINEAR, Texture.FilterMin.LINEAR_MIPMAP_LINEAR, Texture.Wrap.CLAMP_TO_EDGE, Texture.Wrap.CLAMP_TO_EDGE, 1);
        Assets.loadTexture("assets/brushes/brush_terrain_sub_7.png", Texture.FilterMag.LINEAR, Texture.FilterMin.LINEAR_MIPMAP_LINEAR, Texture.Wrap.CLAMP_TO_EDGE, Texture.Wrap.CLAMP_TO_EDGE, 1);
        Assets.loadTexture("assets/brushes/brush_terrain_sub_8.png", Texture.FilterMag.LINEAR, Texture.FilterMin.LINEAR_MIPMAP_LINEAR, Texture.Wrap.CLAMP_TO_EDGE, Texture.Wrap.CLAMP_TO_EDGE, 1);

        // layer 0 - terrain textures
        Assets.loadTexture("assets/textures-layer-0/terrain_land_dirt_0.jpg", Texture.FilterMag.LINEAR, Texture.FilterMin.LINEAR_MIPMAP_LINEAR, Texture.Wrap.MIRRORED_REPEAT, Texture.Wrap.MIRRORED_REPEAT, 1);
        Assets.loadTexture("assets/textures-layer-0/terrain_land_dirt_1.jpg", Texture.FilterMag.LINEAR, Texture.FilterMin.LINEAR_MIPMAP_LINEAR, Texture.Wrap.MIRRORED_REPEAT, Texture.Wrap.MIRRORED_REPEAT, 1);
        Assets.loadTexture("assets/textures-layer-0/terrain_land_grass_0.jpg", Texture.FilterMag.LINEAR, Texture.FilterMin.LINEAR_MIPMAP_LINEAR, Texture.Wrap.MIRRORED_REPEAT, Texture.Wrap.MIRRORED_REPEAT, 1);
        Assets.loadTexture("assets/textures-layer-0/terrain_land_grass_1.jpg", Texture.FilterMag.LINEAR, Texture.FilterMin.LINEAR_MIPMAP_LINEAR, Texture.Wrap.MIRRORED_REPEAT, Texture.Wrap.MIRRORED_REPEAT, 1);
        Assets.loadTexture("assets/textures-layer-0/terrain_land_road_0.jpg", Texture.FilterMag.LINEAR, Texture.FilterMin.LINEAR_MIPMAP_LINEAR, Texture.Wrap.MIRRORED_REPEAT, Texture.Wrap.MIRRORED_REPEAT, 1);
        Assets.loadTexture("assets/textures-layer-0/terrain_land_rocks_0.jpg", Texture.FilterMag.LINEAR, Texture.FilterMin.LINEAR_MIPMAP_LINEAR, Texture.Wrap.MIRRORED_REPEAT, Texture.Wrap.MIRRORED_REPEAT, 1);
        Assets.loadTexture("assets/textures-layer-0/terrain_land_sand_0.jpg", Texture.FilterMag.LINEAR, Texture.FilterMin.LINEAR_MIPMAP_LINEAR, Texture.Wrap.MIRRORED_REPEAT, Texture.Wrap.MIRRORED_REPEAT, 1);
        Assets.loadTexture("assets/textures-layer-0/terrain_land_stone_0.jpg", Texture.FilterMag.LINEAR, Texture.FilterMin.LINEAR_MIPMAP_LINEAR, Texture.Wrap.MIRRORED_REPEAT, Texture.Wrap.MIRRORED_REPEAT, 1);
        Assets.loadTexture("assets/textures-layer-0/terrain_land_stone_1.jpg", Texture.FilterMag.LINEAR, Texture.FilterMin.LINEAR_MIPMAP_LINEAR, Texture.Wrap.MIRRORED_REPEAT, Texture.Wrap.MIRRORED_REPEAT, 1);
        Assets.loadTexture("assets/textures-layer-0/terrain_liquid_water_0.jpg", Texture.FilterMag.LINEAR, Texture.FilterMin.LINEAR_MIPMAP_LINEAR, Texture.Wrap.MIRRORED_REPEAT, Texture.Wrap.MIRRORED_REPEAT, 1);
        Assets.loadTexture("assets/textures-layer-0/terrain_liquid_water_1.jpg", Texture.FilterMag.LINEAR, Texture.FilterMin.LINEAR_MIPMAP_LINEAR, Texture.Wrap.MIRRORED_REPEAT, Texture.Wrap.MIRRORED_REPEAT, 1);
        Assets.loadTexture("assets/textures-layer-0/terrain_liquid_water_2.jpg", Texture.FilterMag.LINEAR, Texture.FilterMin.LINEAR_MIPMAP_LINEAR, Texture.Wrap.MIRRORED_REPEAT, Texture.Wrap.MIRRORED_REPEAT, 1);
        Assets.loadTexture("assets/textures-layer-0/terrain_material_rock.jpg", Texture.FilterMag.LINEAR, Texture.FilterMin.LINEAR_MIPMAP_LINEAR, Texture.Wrap.MIRRORED_REPEAT, Texture.Wrap.MIRRORED_REPEAT, 1);
        // layer 3
        Assets.loadTexturePack("assets/texture-packs/layer_3.yml", Texture.FilterMag.NEAREST, Texture.FilterMin.LINEAR_MIPMAP_LINEAR, 4);

        // user-interface
        Assets.loadTexturePack("assets/texture-packs/user-interface.yml");

        Assets.finishLoading();

        // load saved file
        SaveLoadData loadData = null;
        try {
            loadData = load();
        } catch (Exception e) {
            System.out.println(e);
        }
        //map = new Map(width, height);
        if (loadData == null) {
            map = new Map(width, height);
        } else {
            map = new Map(width, height, loadData.ground, loadData.liquid, loadData.blendMap, loadData.tokens, renderer2D);
        }

        // user - interface
        /*
        WidgetNodeActionsBar actionsBar = new WidgetNodeActionsBar();
        WidgetNodeStatisticsBar statisticsBar = new WidgetNodeStatisticsBar(this);
        toolbar = new WidgetNodeToolbar(this);
        toolSettings = new WidgetNodeToolSettings();
        widgetStatisticsBar.addNode(statisticsBar);
        widgetActionsBar.addNodes(actionsBar);
        widgetTools.addNodes(toolSettings, toolbar);
        */
        WidgetNodeHelpBar helpBar = new WidgetNodeHelpBar();
        widgetHelpBar.addNodes(helpBar);
        camera.update();

        //Tools.initTools(this);
        // init tools
        tools[0] = new Tool_1_Terrain(this);
        tools[1] = new Tool_2_Nature(this);
        tools[2] = new Tool_3_Geology(this);
        tools[3] = new Tool_4_Props(this);
        tools[4] = new Tool_5_Architecture(this);
        tools[5] = new Tool_6_Decorations(this);
        tools[activeToolIndex].activate();
    }

    private SaveLoadData load() throws IOException {
        if (saveFile == null) return null;

        SaveLoadData data = new SaveLoadData();
        try (ZipFile zip = new ZipFile(saveFile)) {
            data.ground = new Texture(
                    readImage(zip, "ground.png"),
                    Texture.FilterMag.NEAREST,
                    Texture.FilterMin.NEAREST,
                    Texture.Wrap.CLAMP_TO_EDGE,
                    Texture.Wrap.CLAMP_TO_EDGE,
                    1
            );

            data.liquid = new Texture(
                    readImage(zip, "liquid.png"),
                    Texture.FilterMag.NEAREST,
                    Texture.FilterMin.NEAREST,
                    Texture.Wrap.CLAMP_TO_EDGE,
                    Texture.Wrap.CLAMP_TO_EDGE,
                    1
            );

            data.blendMap = new Texture(
                    readImage(zip, "blendMap.png"),
                    Texture.FilterMag.NEAREST,
                    Texture.FilterMin.NEAREST,
                    Texture.Wrap.CLAMP_TO_EDGE,
                    Texture.Wrap.CLAMP_TO_EDGE,
                    1
            );

            ZipEntry entry = zip.getEntry("tokens.json");
            if (entry == null)
                throw new IOException("Missing entry: tokens.json");

            try (InputStream in = zip.getInputStream(entry); Reader reader = new InputStreamReader(in, StandardCharsets.UTF_8)) {
                JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
                JsonArray tokensJson = json.getAsJsonArray("tokens");
                data.tokens = new Array<>(tokensJson.size());
                for (JsonElement element : tokensJson) {
                    data.tokens.add(Token.deserialize(element.getAsJsonObject()));
                }
            } catch (Exception e) {
                System.out.println(e.getStackTrace());
            }
        }

        return data;
    }

    private BufferedImage readImage(ZipFile zip, String name) throws IOException {
        ZipEntry entry = zip.getEntry(name);
        if (entry == null) throw new IOException("Missing entry: " + name);
        try (InputStream in = zip.getInputStream(entry)) {
            return ImageIO.read(in);
        }
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
        Graphics.bindFrameBuffer(null);
        GL11.glClearColor(0.01f,0.01f,0.01f,1);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_STENCIL_BUFFER_BIT); // should probably clear the stencil
        renderer2D.begin(camera);
        renderer2D.drawTexture(map.getTexture(), 0, 0, 0, 1, 1);
        renderer2D.end();

        // update tools
        //Tools.update();
        // update tools
        screen.set(Input.mouse.getX(), Input.mouse.getY(), 0);
        camera.unProject(screen);
        Tool_new activeTool = tools[activeToolIndex];
        activeTool.x = screen.x;
        activeTool.y = screen.y;
        activeTool.update(Graphics.getDeltaTime());
        // do a select tool logic

        // draw tools overlay
        renderer2D.begin(camera);
        //Tools.renderToolOverlay(renderer2D, screen.x, screen.y);
        tools[activeToolIndex].renderToolOverlay(renderer2D, screen.x, screen.y);
        renderer2D.setShader(null);
        renderer2D.end();

        renderer2D.begin();
        tools[activeToolIndex].renderToolText(renderer2D, Input.mouse.getX() - Graphics.getWindowWidth() * 0.5f, Graphics.getWindowHeight() * 0.5f - Input.mouse.getY());
        renderer2D.end();
//        renderer2D.begin();
//        Tools.renderToolText(renderer2D, Input.mouse.getX() - Graphics.getWindowWidth() * 0.5f, Graphics.getWindowHeight() * 0.5f - Input.mouse.getY());
//        renderer2D.end();

        // update user interface
        /*
        widgetActionsBar.update();
        widgetStatisticsBar.update();
        widgetTools.update();
        */

        //widgetHelpBar.update();
        // render user interface
        renderer2D.begin();
        widgetHelpBar.render(renderer2D);
//        widgetActionsBar.render(renderer2D);
//        widgetStatisticsBar.render(renderer2D);
//        widgetTools.render(renderer2D);
        renderer2D.end();

    }

    public void selectTool(int index) {
        tools[activeToolIndex].deactivate();
        activeToolIndex = index % tools.length;
        tools[activeToolIndex].activate();
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
    public void saveAs(String path) { // TODO: revise.
        try {
            map.save(path);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        System.out.println("Saved to: " + path);
    }

    @Override
    public void exportAs(String path) {

    }

    @Override
    public void load(String path) {

    }

    @Override
    public int getActiveLayerIndex() {
        return activeLayerIndex;
    }

    @Override
    public boolean isActive() {
        return true;
    }

    @Override
    public void finish() {}

    // **** Input handler ****

    @Override
    public boolean keyboardKeysJustPressed(@NotNull Array<Keyboard.Key> keys) {
        if (keys.contains(Keyboard.Key.END, true)) {
            saveAs(saveFile);
            return true;
        }
        if (keys.contains(Keyboard.Key.PAGE_UP, true)) {
            activeLayerIndex++;
            System.out.println("Layer: " + activeLayerIndex);
            return true;
        } else if (keys.contains(Keyboard.Key.PAGE_DOWN, true)) {
            activeLayerIndex = Math.max(1, --activeLayerIndex);
            System.out.println("Layer: " + activeLayerIndex);
            return true;
        }
        if (keys.contains(Keyboard.Key.KEY_1, true)) {
            selectTool(0);
        } else if (keys.contains(Keyboard.Key.KEY_2, true)) {
            selectTool(1);
        } else if (keys.contains(Keyboard.Key.KEY_3, true)) {
            selectTool(2);
        } else if (keys.contains(Keyboard.Key.KEY_4, true)) {
            selectTool(3);
        } else if (keys.contains(Keyboard.Key.KEY_5, true)) {
            selectTool(4);
        } else if (keys.contains(Keyboard.Key.KEY_6, true)) {
            selectTool(5);
        }
        return true;
    }
}
