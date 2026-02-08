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

    public static ToolDebug toolDebug;

    private static RPGMapMakerScene scene;

    public static void initTools(final RPGMapMakerScene scene) {
        Tools.scene = scene;
        toolDebug = new ToolDebug(scene);

        tools.add(toolDebug);
        selectTool(0);
    }

    public static void selectTool(int index) {
        tools.get(activeToolIndex).deactivate();
        activeToolIndex = index % tools.size;
        tools.get(activeToolIndex).activate();
    }

    public static void update() {
        boolean leftShiftJustPressed = Input.keyboard.isKeyJustPressed(Keyboard.Key.LEFT_SHIFT);

        Vector3 screen = new Vector3(Input.mouse.getX(), Input.mouse.getY(), 0);
        scene.getCamera().unProject(screen);

        Tool activeTool = tools.get(activeToolIndex);
        activeTool.x = screen.x;
        activeTool.y = screen.y;
        activeTool.update(Graphics.getDeltaTime());

        if (leftShiftJustPressed) activeTool.switchToNextBrushMode();
    }

    public static void render(Renderer2D renderer2D, float x, float y) {
        tools.get(activeToolIndex).renderToolOverlay(renderer2D, x, y);
    }

}
