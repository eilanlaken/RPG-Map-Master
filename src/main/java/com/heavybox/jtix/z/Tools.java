package com.heavybox.jtix.z;

import com.heavybox.jtix.RPGMapMakerScene;
import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.graphics.Graphics;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.input.Input;
import com.heavybox.jtix.input.Keyboard;
import com.heavybox.jtix.math.Vector3;

public final class Tools {

    private Tools() {}

    public static Array<Tool> tools = new Array<>(true, 10);
    public static int activeToolIndex = 0;

    public static ToolBrush_Terrain toolBrushTerrain;
    public static ToolStamp_Farmlands toolStampFarmlands; // TODO: unify farmlands with terrain
    public static ToolBrush_Nature toolBrushNature;
    public static ToolStamp_Architecture toolStampArchitecture;
    public static ToolBrush_Geology toolBrushGeology;
    public static ToolBrush_Props toolBrushProps;
    public static ToolStamp_Procedural toolStampProcedural;

    private static RPGMapMakerScene scene;

    public static void initTools(final RPGMapMakerScene scene) {
        Tools.scene = scene;

        toolBrushTerrain = new ToolBrush_Terrain(scene);
        toolBrushNature = new ToolBrush_Nature(scene);
        toolStampFarmlands = new ToolStamp_Farmlands(scene);
        toolStampArchitecture = new ToolStamp_Architecture(scene);
        toolBrushGeology = new ToolBrush_Geology(scene);
        toolBrushProps = new ToolBrush_Props(scene);
        toolStampProcedural = new ToolStamp_Procedural(scene);

        tools.add(toolBrushTerrain);
        tools.add(toolBrushNature);
        tools.add(toolStampFarmlands);
        tools.add(toolStampArchitecture);
        tools.add(toolBrushGeology);
        tools.add(toolBrushProps);
        tools.add(toolStampProcedural);

        selectTool(0);
    }

    public static void selectTool(int index) {
        tools.get(activeToolIndex).deactivate();
        activeToolIndex = index % tools.size;
        tools.get(activeToolIndex).activate();
    }

    public static void update() {
        Vector3 screen = new Vector3(Input.mouse.getX(), Input.mouse.getY(), 0);
        scene.getCamera().unProject(screen);

        Tool activeTool = tools.get(activeToolIndex);
        activeTool.x = screen.x;
        activeTool.y = screen.y;
        activeTool.update(Graphics.getDeltaTime());

        if (Input.keyboard.isKeyJustPressed(Keyboard.Key.KEY_1)) {
            selectTool(0);
        } else if (Input.keyboard.isKeyJustPressed(Keyboard.Key.KEY_2)) {
            selectTool(1);
        } else if (Input.keyboard.isKeyJustPressed(Keyboard.Key.KEY_3)) {
            selectTool(2);
        } else if (Input.keyboard.isKeyJustPressed(Keyboard.Key.KEY_4)) {
            selectTool(3);
        } else if (Input.keyboard.isKeyJustPressed(Keyboard.Key.KEY_5)) {
            selectTool(4);
        }
    }

    public static void render(Renderer2D renderer2D, float x, float y) {
        tools.get(activeToolIndex).renderToolOverlay(renderer2D, x, y);
    }

}
