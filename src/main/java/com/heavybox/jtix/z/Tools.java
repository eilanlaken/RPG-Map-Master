package com.heavybox.jtix.z;

import com.heavybox.jtix.RPGMapMakerScene;
import com.heavybox.jtix.collections.Array;

public final class Tools {

    private Tools() {}

    public static Array<Tool> tools = new Array<>(true, 10);
    public static int activeToolIndex = 0;

    public static ToolBrushTerrain toolBrushTerrain;
    public static ToolBrushFields toolBrushFields;
    public static ToolBrushTrees toolBrushTrees;
    public static ToolBrushProps toolBrushProps;
    public static ToolBrushArchitecture toolBrushArchitecture;
    public static ToolBrushRocks toolBrushRocks;
    public static ToolBrushDecorations toolBrushDecorations;

    static void initTools(final RPGMapMakerScene scene) {

    }

    public static void update() {
        
    }

}
