package com.heavybox.jtix.widgets_2;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Graphics;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.input.Input;
import org.jetbrains.annotations.NotNull;

public final class Widgets {

    /*** some global flags ***/
    public  static       boolean debugMode        = true; // TODO: use this when rendering: render regions if true.
    private static final float   WHITE_FLOAT_BITS = Color.WHITE.toFloatBits(); // to reset the color to white before re-rendering components

    /*** input device state */
    private static float pointerXPrev = 0;
    private static float pointerYPrev = 0;
    private static float pointerX     = 0;
    private static float pointerY     = 0;

    /*** current scene widgets */
    private static final Array<Widget> allSceneWidgets         = new Array<>(false, 4);
    private static final Array<Widget> allSceneWidgetsToAdd    = new Array<>(false, 4);
    private static final Array<Widget> allSceneWidgetsToRemove = new Array<>(false, 4);

    private Widgets() {}

    public static void update() {
        float windowHalfWidth = Graphics.getWindowWidth() * 0.5f;
        float windowHalfHeight = Graphics.getWindowHeight() * 0.5f;
        pointerXPrev = pointerX;
        pointerYPrev = pointerY;
        pointerX = Input.mouse.getX() - windowHalfWidth;
        pointerY = windowHalfHeight - Input.mouse.getY();

        /* add all added widgets */
        for (Widget widget : allSceneWidgetsToAdd) {
            Input.addEventHandler(widget);
        }
        allSceneWidgets.addAll(allSceneWidgetsToAdd);
        allSceneWidgetsToAdd.clear();
        /* remove all added widgets */
        for (Widget widget : allSceneWidgetsToRemove) {
            Input.removeEventHandler(widget);
        }
        allSceneWidgets.removeAll(allSceneWidgetsToRemove, true);
        allSceneWidgetsToRemove.clear();

        // iterate over all *root* widget nodes and perform offset updates and logical updates.
        final float delta = Graphics.getDeltaTime();
        for (Widget widget : allSceneWidgets) {
            if (widget.isRoot()) widget.update(delta);
        }
    }

    public static void render(Renderer2D renderer2D) {
        // iterate over all *root* widget nodes and perform renders
        for (Widget widget : allSceneWidgets) {
            renderer2D.setColor(WHITE_FLOAT_BITS);
            if (widget.isRoot()) widget.render(renderer2D);
        }
    }

    public static float getPointerX()     { return pointerX; }
    public static float getPointerY()     { return pointerY; }
    public static float getPointerXPrev() { return pointerXPrev; }
    public static float getPointerYPrev() { return pointerYPrev; }

    public static void add(@NotNull final Widget widget) {
        allSceneWidgetsToAdd.add(widget);
    }

    public static void remove(@NotNull final Widget widget) {
        allSceneWidgetsToRemove.add(widget);
    }

    public static void clear() {
        allSceneWidgets.clear();
        allSceneWidgetsToAdd.clear();
        allSceneWidgetsToRemove.clear();
    }

    static boolean isXAncestorOfY(final Widget X, final Widget Y) {
        if (X == null || Y == null) return false;
        return X == Y.getParent() || isXAncestorOfY(X, Y.getParent());
    }

}
