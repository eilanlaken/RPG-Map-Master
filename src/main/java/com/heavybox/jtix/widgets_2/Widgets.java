package com.heavybox.jtix.widgets_2;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Graphics;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.input.Input;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;

public final class Widgets {

    public  static final Comparator<Widget> widgetComparator = Comparator.comparingInt(a -> a.inputLayer);
    private static final float              WHITE_FLOAT_BITS = Color.WHITE.toFloatBits(); // to reset the color to white before re-rendering components

    /*** some global flags ***/
    private static int     currentID = 0;
    public  static boolean debugMode = true; // TODO: use this when rendering: render regions if true.

    /*** input device state */
    private static float pointerXPrevValue = 0;
    private static float pointerYPrevValue = 0;
    private static float pointerXPrevFrame = 0;
    private static float pointerYPrevFrame = 0;
    private static float pointerX          = 0;
    private static float pointerY          = 0;

    /*** current scene widgets hierarchy */
    private static final Array<Widget> rootWidgets = new Array<>(false, 5);
    private static final Array<Widget> toReplace = new Array<>(false, 1);

    /*** input event handling ***/

    private Widgets() {}

    public static float getPointerX()     { return pointerX; }
    public static float getPointerY()     { return pointerY; }
    public static float getPointerXPrevValue() { return pointerXPrevValue; }
    public static float getPointerYPrevValue() { return pointerYPrevValue; }
    public static float getPointerXPrevFrame() { return pointerXPrevFrame; }
    public static float getPointerYPrevFrame() { return pointerYPrevFrame; }

    public static void update() {
        float windowHalfWidth = Graphics.getWindowWidth() * 0.5f;
        float windowHalfHeight = Graphics.getWindowHeight() * 0.5f;
        float newPointerX = Input.mouse.getX() - windowHalfWidth;
        float newPointerY = windowHalfHeight - Input.mouse.getY();
        // Previous frame.
        pointerXPrevFrame = pointerX;
        pointerYPrevFrame = pointerY;
        // Previous value.
        if (newPointerX != pointerX) pointerXPrevValue = pointerX;
        if (newPointerY != pointerY) pointerYPrevValue = pointerY;
        pointerX = newPointerX;
        pointerY = newPointerY;

        // consolidate root nodes
        toReplace.clear();
        for (Widget widget : rootWidgets) {
            if (!widget.isRoot()) toReplace.add(widget);
        }
        for (Widget widget : toReplace) {
            Widget newRoot = widget.getRoot();
            rootWidgets.replaceFirst(widget, newRoot, true);
            Input.removeEventHandler(widget);
            Input.addEventHandler(newRoot);
        }

        final float delta = Graphics.getDeltaTime();
        for (Widget widget : rootWidgets) {
            if (!widget.isRoot()) continue;
            if (!widget.isActive()) continue;
            widget.update(delta);
        }
    }

    public static void render(Renderer2D renderer2D) {
        // iterate over all *root* widget nodes and perform renders
        rootWidgets.sort(widgetComparator);
        for (Widget widget : rootWidgets) {
            if (!widget.isRoot()) continue; // to be extra sure.
            if (!widget.isActive()) continue;
            renderer2D.setColor(WHITE_FLOAT_BITS);
            widget.render(renderer2D);
        }
    }

    public static void add(final Widget widget) {
        Widget root = widget.getRoot();
        if (rootWidgets.contains(root, true)) return;

        rootWidgets.add(root);
        Input.addEventHandler(root);
    }

    public static void remove(@NotNull final Widget widget) {
        if (widget.isRoot()) {
            rootWidgets.removeValue(widget, true);
            Input.removeEventHandler(widget);
            return;
        }

        Widget parent = widget.parent;
        parent.children.removeValue(widget, true);
    }

    public static void clear() {
        rootWidgets.clear();
        currentID = 0;
    }

    static boolean isXAncestorOfY(final Widget X, final Widget Y) {
        if (X == null || Y == null) return false;

        Widget current = Y.getParent();
        while (current != null) {
            if (current == X) return true;
            current = current.getParent();
        }
        return false;
    }

    static int getID() {
        return currentID++;
    }

}
