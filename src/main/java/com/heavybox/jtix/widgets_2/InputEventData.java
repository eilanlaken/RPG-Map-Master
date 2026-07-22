package com.heavybox.jtix.widgets_2;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.collections.ArrayChar;
import com.heavybox.jtix.input.Keyboard;

public abstract class InputEventData {

    public final Widget target;

    InputEventData(final Widget target) {
        this.target = target;
    }

    public static class MouseDown extends InputEventData {

        public boolean buttonLeft;
        public boolean buttonRight;
        public boolean buttonMiddle;
        public float   mouseLocalX;
        public float   mouseLocalY;

        public MouseDown(Widget target, boolean buttonLeft, boolean buttonRight, boolean buttonMiddle, float mouseLocalX, float mouseLocalY) {
            super(target);
            this.buttonLeft = buttonLeft;
            this.buttonRight = buttonRight;
            this.buttonMiddle = buttonMiddle;
            this.mouseLocalX = mouseLocalX;
            this.mouseLocalY = mouseLocalY;
        }

        public MouseDown(Widget target) {
            super(target);
        }

    }

    public static class MouseUp extends InputEventData {

        public boolean buttonLeft;
        public boolean buttonRight;
        public boolean buttonMiddle;
        public float   mouseLocalX;
        public float   mouseLocalY;

        public MouseUp(Widget target, boolean buttonLeft, boolean buttonRight, boolean buttonMiddle, float mouseLocalX, float mouseLocalY) {
            super(target);
            this.buttonLeft = buttonLeft;
            this.buttonRight = buttonRight;
            this.buttonMiddle = buttonMiddle;
            this.mouseLocalX = mouseLocalX;
            this.mouseLocalY = mouseLocalY;
        }

    }

    public static class MouseClick extends InputEventData {

        public boolean buttonLeft;
        public boolean buttonRight;
        public boolean buttonMiddle;
        public float mouseLocalX;
        public float mouseLocalY;

        public MouseClick(Widget target, boolean buttonLeft, boolean buttonRight, boolean buttonMiddle, float mouseLocalX, float mouseLocalY) {
            super(target);
            this.buttonLeft = buttonLeft;
            this.buttonRight = buttonRight;
            this.buttonMiddle = buttonMiddle;
            this.mouseLocalX = mouseLocalX;
            this.mouseLocalY = mouseLocalY;
        }

    }

    public static class MouseDoubleClick extends InputEventData {

        public boolean buttonLeft;
        public boolean buttonRight;
        public boolean buttonMiddle;
        public float mouseLocalX;
        public float mouseLocalY;

        public MouseDoubleClick(Widget target, boolean buttonLeft, boolean buttonRight, boolean buttonMiddle, float mouseLocalX, float mouseLocalY) {
            super(target);
            this.buttonLeft = buttonLeft;
            this.buttonRight = buttonRight;
            this.buttonMiddle = buttonMiddle;
            this.mouseLocalX = mouseLocalX;
            this.mouseLocalY = mouseLocalY;
        }

    }

    public static class MouseMiddleClick extends InputEventData {

        public float mouseLocalX;
        public float mouseLocalY;

        public MouseMiddleClick(Widget target, float mouseLocalX, float mouseLocalY) {
            super(target);
            this.mouseLocalX = mouseLocalX;
            this.mouseLocalY = mouseLocalY;
        }

    }

    public static class MouseLeftClickOutside extends InputEventData {

        public float mouseLocalX;
        public float mouseLocalY;

        public MouseLeftClickOutside(Widget target, float mouseLocalX, float mouseLocalY) {
            super(target);
            this.mouseLocalX = mouseLocalX;
            this.mouseLocalY = mouseLocalY;
        }

    }

    public static class MouseRightClickOutside extends InputEventData {

        public float mouseLocalX;
        public float mouseLocalY;

        public MouseRightClickOutside(Widget target, float mouseLocalX, float mouseLocalY) {
            super(target);
            this.mouseLocalX = mouseLocalX;
            this.mouseLocalY = mouseLocalY;
        }

    }

    public static class MouseMiddleClickOutside extends InputEventData {

        public float mouseLocalX;
        public float mouseLocalY;

        public MouseMiddleClickOutside(Widget target, float mouseLocalX, float mouseLocalY) {
            super(target);
            this.mouseLocalX = mouseLocalX;
            this.mouseLocalY = mouseLocalY;
        }

    }

    public static class MouseEnter extends InputEventData {

        public float mouseLocalXPrev;
        public float mouseLocalYPrev;
        public float mouseLocalX;
        public float mouseLocalY;

        public MouseEnter(Widget target, float mouseLocalXPrev, float mouseLocalYPrev, float mouseLocalX, float mouseLocalY) {
            super(target);
            this.mouseLocalXPrev = mouseLocalXPrev;
            this.mouseLocalYPrev = mouseLocalYPrev;
            this.mouseLocalX = mouseLocalX;
            this.mouseLocalY = mouseLocalY;
        }

    }

    public static class MouseLeave extends InputEventData {

        public float mouseLocalXPrev;
        public float mouseLocalYPrev;
        public float mouseLocalX;
        public float mouseLocalY;

        public MouseLeave(Widget target, float mouseLocalXPrev, float mouseLocalYPrev, float mouseLocalX, float mouseLocalY) {
            super(target);
            this.mouseLocalXPrev = mouseLocalXPrev;
            this.mouseLocalYPrev = mouseLocalYPrev;
            this.mouseLocalX = mouseLocalX;
            this.mouseLocalY = mouseLocalY;
        }

    }

    public static class MouseScroll extends InputEventData {

        public float scrollX;
        public float scrollY;
        public float mouseLocalX;
        public float mouseLocalY;

        public MouseScroll(Widget target, float scrollX, float scrollY, float mouseLocalX, float mouseLocalY) {
            super(target);
            this.scrollX = scrollX;
            this.scrollY = scrollY;
            this.mouseLocalX = mouseLocalX;
            this.mouseLocalY = mouseLocalY;
        }
    }

    public static class MouseDrag extends InputEventData {

        public float mouseLocalXPrev;
        public float mouseLocalYPrev;
        public float mouseLocalX;
        public float mouseLocalY;
        public float mouseLocalDeltaX;
        public float mouseLocalDeltaY;

        public MouseDrag(Widget target, float mouseLocalXPrev, float mouseLocalYPrev, float mouseLocalX, float mouseLocalY, float mouseLocalDeltaX, float mouseLocalDeltaY) {
            super(target);
            this.mouseLocalXPrev = mouseLocalXPrev;
            this.mouseLocalYPrev = mouseLocalYPrev;
            this.mouseLocalX = mouseLocalX;
            this.mouseLocalY = mouseLocalY;
            this.mouseLocalDeltaX = mouseLocalDeltaX;
            this.mouseLocalDeltaY = mouseLocalDeltaY;
        }
    }

    public static class MouseDragStart extends InputEventData {

        public float mouseLocalX;
        public float mouseLocalY;

        public MouseDragStart(Widget target, float mouseLocalX, float mouseLocalY) {
            super(target);
            this.mouseLocalX = mouseLocalX;
            this.mouseLocalY = mouseLocalY;
        }
    }

    public static class MouseDragEnd extends InputEventData {

        public float mouseLocalX;
        public float mouseLocalY;

        public MouseDragEnd(Widget target, float mouseLocalX, float mouseLocalY) {
            super(target);
            this.mouseLocalX = mouseLocalX;
            this.mouseLocalY = mouseLocalY;
        }

    }

    public static class MouseDragEnter extends InputEventData {

        public Widget draggable;
        public float  mouseLocalXPrev;
        public float  mouseLocalYPrev;
        public float  mouseLocalX;
        public float  mouseLocalY;
        public float  mouseLocalDeltaX;
        public float  mouseLocalDeltaY;

        public MouseDragEnter(Widget target, Widget draggable, float mouseLocalXPrev, float mouseLocalYPrev, float mouseLocalX, float mouseLocalY, float mouseLocalDeltaX, float mouseLocalDeltaY) {
            super(target);
            this.draggable = draggable;
            this.mouseLocalXPrev = mouseLocalXPrev;
            this.mouseLocalYPrev = mouseLocalYPrev;
            this.mouseLocalX = mouseLocalX;
            this.mouseLocalY = mouseLocalY;
            this.mouseLocalDeltaX = mouseLocalDeltaX;
            this.mouseLocalDeltaY = mouseLocalDeltaY;
        }
    }

    public static class MouseDragLeave extends InputEventData {

        public Widget draggable;
        public float  mouseLocalXPrev;
        public float  mouseLocalYPrev;
        public float  mouseLocalX;
        public float  mouseLocalY;
        public float  mouseLocalDeltaX;
        public float  mouseLocalDeltaY;

        public MouseDragLeave(Widget target, Widget draggable, float mouseLocalXPrev, float mouseLocalYPrev, float mouseLocalX, float mouseLocalY, float mouseLocalDeltaX, float mouseLocalDeltaY) {
            super(target);
            this.draggable = draggable;
            this.mouseLocalXPrev = mouseLocalXPrev;
            this.mouseLocalYPrev = mouseLocalYPrev;
            this.mouseLocalX = mouseLocalX;
            this.mouseLocalY = mouseLocalY;
            this.mouseLocalDeltaX = mouseLocalDeltaX;
            this.mouseLocalDeltaY = mouseLocalDeltaY;
        }
    }

    public static class MouseDragDrop extends InputEventData {

        public Widget draggable;
        public float  mouseLocalX;
        public float  mouseLocalY;

        public MouseDragDrop(Widget target, Widget draggable, float mouseLocalX, float mouseLocalY) {
            super(target);
            this.draggable = draggable;
            this.mouseLocalX = mouseLocalX;
            this.mouseLocalY = mouseLocalY;
        }

    }

    public static class CodepointsTyped extends InputEventData {

        public ArrayChar codePoints;

        public CodepointsTyped(Widget target, char ...chars) {
            super(target);
            this.codePoints = new ArrayChar(true, 1);
            if (chars == null) return;
            for (char aChar : chars) {
                this.codePoints.add(aChar);
            }
        }

    }

    public static class KeysJustPressed extends InputEventData {

        public Array<Keyboard.Key> keys;

        public KeysJustPressed(Widget target, Keyboard.Key ...keys) {
            super(target);
            this.keys = new Array<>(true, 1);
            this.keys.addAll(keys);
        }

    }

    public static class KeysPressed extends InputEventData {

        public Array<Keyboard.Key> keys;

        public KeysPressed(Widget target, Keyboard.Key ...keys) {
            super(target);
            this.keys = new Array<>(true, 1);
            this.keys.addAll(keys);
        }

    }

}
