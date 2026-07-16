package com.heavybox.jtix.widgets_2;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.graphics.Graphics;
import com.heavybox.jtix.input.Input;
import org.jetbrains.annotations.NotNull;

public final class Widgets {

    /*** some global flags ***/
    public static boolean debugMode = true; // TODO: use this when rendering: render regions if true.

    /*** input device state */
    private static float pointerXPrev = 0;
    private static float pointerYPrev = 0;
    private static float pointerX     = 0;
    private static float pointerY     = 0;

    static final Array<Widget> allWidgets         = new Array<>(false, 4);
    static final Array<Widget> allWidgetsToAdd    = new Array<>(false, 4);
    static final Array<Widget> allWidgetsToRemove = new Array<>(false, 4);

    private Widgets() {}

    public static void update() {
        float windowHalfWidth = Graphics.getWindowWidth() * 0.5f;
        float windowHalfHeight = Graphics.getWindowHeight() * 0.5f;
        pointerXPrev = pointerX;
        pointerYPrev = pointerY;
        pointerX = Input.mouse.getX() - windowHalfWidth;
        pointerY = windowHalfHeight - Input.mouse.getY();

        // do widget updates here.
        allWidgets.addAll(allWidgetsToAdd);
        allWidgets.removeAll(allWidgetsToRemove, true);
        allWidgetsToAdd.clear();
        allWidgetsToRemove.clear();

        // iterate over all *root* widget nodes and perform offset updates and logical updates.
    }

    public static void render() {
        // iterate over all *root* widget nodes and perform renders
    }

    public static float getPointerX()     { return pointerX; }
    public static float getPointerY()     { return pointerY; }
    public static float getPointerXPrev() { return pointerXPrev; }
    public static float getPointerYPrev() { return pointerYPrev; }

    public static void registerWidget(@NotNull Widget widget) {
        allWidgetsToAdd.add(widget);
    }

    public static void clear() {
        allWidgets.clear();
        allWidgetsToAdd.clear();
        allWidgetsToRemove.clear();
    }

}
