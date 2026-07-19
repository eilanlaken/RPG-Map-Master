package com.heavybox.jtix.widgets_2;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Graphics;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.input.Input;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;

// TODO: find a way to assign an input event handler for all the ROOT nodes.
public final class Widgets {

    private static int sceneCreatedWidgets = 0;

    /*** some global flags ***/
    public  static       boolean debugMode        = true; // TODO: use this when rendering: render regions if true.
    private static final float   WHITE_FLOAT_BITS = Color.WHITE.toFloatBits(); // to reset the color to white before re-rendering components

    /*** input device state */
    private static float pointerXPrev = 0;
    private static float pointerYPrev = 0;
    private static float pointerX     = 0;
    private static float pointerY     = 0;

    /*** current scene widgets hierarchy */
    private static final Array<Widget> rootWidgets = new Array<>(false, 5);
    private static final Array<Widget> toReplace = new Array<>(false, 1);
    private static final Comparator<Widget> widgetComparator = Comparator.comparingInt(a -> a.inputLayer);

    /*** input event handling ***/

    private Widgets() {}

    public static float getPointerX()     { return pointerX; }
    public static float getPointerY()     { return pointerY; }
    public static float getPointerXPrev() { return pointerXPrev; }
    public static float getPointerYPrev() { return pointerYPrev; }

    public static void update() {
        float windowHalfWidth = Graphics.getWindowWidth() * 0.5f;
        float windowHalfHeight = Graphics.getWindowHeight() * 0.5f;
        pointerXPrev = pointerX;
        pointerYPrev = pointerY;
        pointerX = Input.mouse.getX() - windowHalfWidth;
        pointerY = windowHalfHeight - Input.mouse.getY();

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
        sceneCreatedWidgets = 0;
    }

    static boolean isXAncestorOfY(final Widget X, final Widget Y) {
        Widget current = Y.getParent();
        while (current != null) {
            if (current == X) return true;
            current = current.getParent();
        }
        return false;
    }

    static int getID() {
        final int id = sceneCreatedWidgets;
        sceneCreatedWidgets++;
        return id;
    }

}
