package com.heavybox.jtix.widgets_2;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.collections.ArrayChar;
import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Graphics;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.input.Input;
import com.heavybox.jtix.input.InputEventHandler;
import com.heavybox.jtix.input.Keyboard;
import com.heavybox.jtix.input.Mouse;
import com.heavybox.jtix.math.Transform2D;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;

public final class Widgets {

    public  static final Comparator<Widget> widgetComparator = Comparator.comparingInt(a -> a.zIndex);
    private static final float              WHITE_FLOAT_BITS = Color.WHITE.toFloatBits(); // to reset the color to white before re-rendering components

    /*** tmp Widget helpers ***/
    static final Array<Widget>      layoutChildren = new Array<>(true, 10);
    static final Array<Transform2D> layoutOffsets  = new Array<>(true, 10);

    /*** some global flags ***/
    private static int     currentID = 0;
    public  static boolean debugMode = true; // TODO: use this when rendering: render regions if true.

    /*** input device state */
    private static Widget inputMouseDownTarget = null;
    private static Widget inputMouseUpTarget   = null;

    private static float pointerXPrev = 0;
    private static float pointerYPrev = 0;
    private static float pointerX     = 0;
    private static float pointerY     = 0;

    /*** current scene widgets hierarchy */
    private static final Array<Widget> rootWidgets = new Array<>(false, 5);
    private static final Array<Widget> toReplace   = new Array<>(false, 1);

    /*** input event handling ***/
    private static final InputEventHandler inputEventHandler = new InputEventHandler() {
        @Override
        public int getInputLayer() { return Integer.MAX_VALUE; }

        @Override
        public boolean isActive() { return true; }

        @Override
        public boolean mouseButtonsDown(int mouseX, int mouseY, @NotNull Array<Mouse.Button> buttons) {
            return InputEventHandler.super.mouseButtonsDown(mouseX, mouseY, buttons);
        }

        @Override
        public boolean mouseButtonsUp(int mouseX, int mouseY, @NotNull Array<Mouse.Button> buttons) {
            return InputEventHandler.super.mouseButtonsUp(mouseX, mouseY, buttons);
        }

        @Override
        public boolean mouseMoved(int mouseX, int mouseY, int deltaMouseX, int deltaMouseY) {
            return InputEventHandler.super.mouseMoved(mouseX, mouseY, deltaMouseX, deltaMouseY);
        }

        @Override
        public boolean mouseScrolled(float scrollX, float scrollY) {
            return InputEventHandler.super.mouseScrolled(scrollX, scrollY);
        }

        @Override
        public boolean mouseDragged(int mouseX, int mouseY, int deltaMouseX, int deltaMouseY, @NotNull Array<Mouse.Button> buttons) {
            return InputEventHandler.super.mouseDragged(mouseX, mouseY, deltaMouseX, deltaMouseY, buttons);
        }

        @Override
        public boolean keyboardKeysJustPressed(@NotNull Array<Keyboard.Key> keys) {
            return InputEventHandler.super.keyboardKeysJustPressed(keys);
        }

        @Override
        public boolean keyboardKeysJustReleased(@NotNull Array<Keyboard.Key> keys) {
            return InputEventHandler.super.keyboardKeysJustReleased(keys);
        }

        @Override
        public boolean keyboardCodepointsTyped(@NotNull ArrayChar codepoints) {
            return InputEventHandler.super.keyboardCodepointsTyped(codepoints);
        }
    };


    private Widgets() {}

    public static float getPointerX()     { return pointerX; }
    public static float getPointerY()     { return pointerY; }
    public static float getPointerXPrev() { return pointerXPrev; }
    public static float getPointerYPrev() { return pointerYPrev; }

    public static void update() {
        Input.registerEventHandler(inputEventHandler);
        layoutChildren.clear();
        layoutOffsets.clear();

        pointerXPrev = pointerX;
        pointerYPrev = pointerY;
        pointerX = Input.mouse.getX() - Graphics.getWindowWidth() * 0.5f;
        pointerY = Graphics.getWindowHeight() * 0.5f - Input.mouse.getY();

        // consolidate root nodes
        toReplace.clear();
        for (Widget widget : rootWidgets) {
            if (!widget.isRoot()) toReplace.add(widget);
        }
        for (Widget widget : toReplace) {
            Widget newRoot = widget.getRoot();
            if (!rootWidgets.contains(newRoot, true)) rootWidgets.replaceFirst(widget, newRoot, true);
            else rootWidgets.removeValue(widget, true);
        }
        rootWidgets.sort(widgetComparator);

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
            if (!widget.isActive()) continue; // to be extra sure.
            renderer2D.setColor(WHITE_FLOAT_BITS);
            widget.render(renderer2D);
        }
    }

    public static void add(final Widget widget) {
        Widget root = widget.getRoot();
        if (rootWidgets.contains(root, true)) return;

        rootWidgets.add(root);
    }

    public static void remove(@NotNull final Widget widget) {
        if (widget.isRoot()) {
            rootWidgets.removeValue(widget, true);
            return;
        }

        Widget parent = widget.parent;
        parent.children.removeValue(widget, true);
        widget.parent = null; // severe the connection completely
    }

    public static void clear() {
        // TODO: reset state: widget dragged etc.
        inputMouseDownTarget = null;
        inputMouseUpTarget = null;
        layoutChildren.clear();
        layoutOffsets.clear();
        Input.unregisterEventHandler(inputEventHandler);
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
