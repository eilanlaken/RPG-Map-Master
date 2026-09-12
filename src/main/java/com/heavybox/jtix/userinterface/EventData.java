package com.heavybox.jtix.userinterface;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.collections.ArrayChar;
import com.heavybox.jtix.input.Keyboard;

public abstract class EventData {

    public final Node target;

    EventData(final Node target) {
        this.target = target;
    }

    public static class MouseDown extends EventData {

        public boolean buttonLeft;
        public boolean buttonRight;
        public boolean buttonMiddle;
        public float   mouseLocalX;
        public float   mouseLocalY;

        public MouseDown(Node target, boolean buttonLeft, boolean buttonRight, boolean buttonMiddle, float mouseLocalX, float mouseLocalY) {
            super(target);
            this.buttonLeft = buttonLeft;
            this.buttonRight = buttonRight;
            this.buttonMiddle = buttonMiddle;
            this.mouseLocalX = mouseLocalX;
            this.mouseLocalY = mouseLocalY;
        }

        public MouseDown(Node target) {
            super(target);
        }

    }

    public static class MouseUp extends EventData {

        public boolean buttonLeft;
        public boolean buttonRight;
        public boolean buttonMiddle;
        public float   mouseLocalX;
        public float   mouseLocalY;

        public MouseUp(Node target, boolean buttonLeft, boolean buttonRight, boolean buttonMiddle, float mouseLocalX, float mouseLocalY) {
            super(target);
            this.buttonLeft = buttonLeft;
            this.buttonRight = buttonRight;
            this.buttonMiddle = buttonMiddle;
            this.mouseLocalX = mouseLocalX;
            this.mouseLocalY = mouseLocalY;
        }

    }

    public static class MouseClick extends EventData {

        public boolean buttonLeft;
        public boolean buttonRight;
        public boolean buttonMiddle;
        public float mouseLocalX;
        public float mouseLocalY;

        public MouseClick(Node target, boolean buttonLeft, boolean buttonRight, boolean buttonMiddle, float mouseLocalX, float mouseLocalY) {
            super(target);
            this.buttonLeft = buttonLeft;
            this.buttonRight = buttonRight;
            this.buttonMiddle = buttonMiddle;
            this.mouseLocalX = mouseLocalX;
            this.mouseLocalY = mouseLocalY;
        }

    }

    public static class MouseDoubleClick extends EventData {

        public boolean buttonLeft;
        public boolean buttonRight;
        public boolean buttonMiddle;
        public float mouseLocalX;
        public float mouseLocalY;

        public MouseDoubleClick(Node target, boolean buttonLeft, boolean buttonRight, boolean buttonMiddle, float mouseLocalX, float mouseLocalY) {
            super(target);
            this.buttonLeft = buttonLeft;
            this.buttonRight = buttonRight;
            this.buttonMiddle = buttonMiddle;
            this.mouseLocalX = mouseLocalX;
            this.mouseLocalY = mouseLocalY;
        }

    }

    public static class MouseMiddleClick extends EventData {

        public float mouseLocalX;
        public float mouseLocalY;

        public MouseMiddleClick(Node target, float mouseLocalX, float mouseLocalY) {
            super(target);
            this.mouseLocalX = mouseLocalX;
            this.mouseLocalY = mouseLocalY;
        }

    }

    public static class MouseLeftClickOutside extends EventData {

        public float mouseLocalX;
        public float mouseLocalY;

        public MouseLeftClickOutside(Node target, float mouseLocalX, float mouseLocalY) {
            super(target);
            this.mouseLocalX = mouseLocalX;
            this.mouseLocalY = mouseLocalY;
        }

    }

    public static class MouseRightClickOutside extends EventData {

        public float mouseLocalX;
        public float mouseLocalY;

        public MouseRightClickOutside(Node target, float mouseLocalX, float mouseLocalY) {
            super(target);
            this.mouseLocalX = mouseLocalX;
            this.mouseLocalY = mouseLocalY;
        }

    }

    public static class MouseMiddleClickOutside extends EventData {

        public float mouseLocalX;
        public float mouseLocalY;

        public MouseMiddleClickOutside(Node target, float mouseLocalX, float mouseLocalY) {
            super(target);
            this.mouseLocalX = mouseLocalX;
            this.mouseLocalY = mouseLocalY;
        }

    }

    public static class MouseEnter extends EventData {

        public float mouseLocalXPrev;
        public float mouseLocalYPrev;
        public float mouseLocalX;
        public float mouseLocalY;

        public MouseEnter(Node target, float mouseLocalXPrev, float mouseLocalYPrev, float mouseLocalX, float mouseLocalY) {
            super(target);
            this.mouseLocalXPrev = mouseLocalXPrev;
            this.mouseLocalYPrev = mouseLocalYPrev;
            this.mouseLocalX = mouseLocalX;
            this.mouseLocalY = mouseLocalY;
        }

    }

    public static class MouseLeave extends EventData {

        public float mouseLocalXPrev;
        public float mouseLocalYPrev;
        public float mouseLocalX;
        public float mouseLocalY;

        public MouseLeave(Node target, float mouseLocalXPrev, float mouseLocalYPrev, float mouseLocalX, float mouseLocalY) {
            super(target);
            this.mouseLocalXPrev = mouseLocalXPrev;
            this.mouseLocalYPrev = mouseLocalYPrev;
            this.mouseLocalX = mouseLocalX;
            this.mouseLocalY = mouseLocalY;
        }

    }

    public static class MouseScroll extends EventData {

        public float scrollX;
        public float scrollY;
        public float mouseLocalX;
        public float mouseLocalY;

        public MouseScroll(Node target, float scrollX, float scrollY, float mouseLocalX, float mouseLocalY) {
            super(target);
            this.scrollX = scrollX;
            this.scrollY = scrollY;
            this.mouseLocalX = mouseLocalX;
            this.mouseLocalY = mouseLocalY;
        }
    }

    public static class MouseDrag extends EventData {

        public float mouseLocalXPrev;
        public float mouseLocalYPrev;
        public float mouseLocalX;
        public float mouseLocalY;

        public MouseDrag(Node target, float mouseLocalXPrev, float mouseLocalYPrev, float mouseLocalX, float mouseLocalY) {
            super(target);
            this.mouseLocalXPrev = mouseLocalXPrev;
            this.mouseLocalYPrev = mouseLocalYPrev;
            this.mouseLocalX = mouseLocalX;
            this.mouseLocalY = mouseLocalY;
        }
    }

    public static class MouseDragStart extends EventData {

        public float mouseLocalX;
        public float mouseLocalY;

        public MouseDragStart(Node target, float mouseLocalX, float mouseLocalY) {
            super(target);
            this.mouseLocalX = mouseLocalX;
            this.mouseLocalY = mouseLocalY;
        }
    }

    public static class MouseDragEnd extends EventData {

        public float mouseLocalX;
        public float mouseLocalY;

        public MouseDragEnd(Node target, float mouseLocalX, float mouseLocalY) {
            super(target);
            this.mouseLocalX = mouseLocalX;
            this.mouseLocalY = mouseLocalY;
        }

    }

    public static class MouseDragEnter extends EventData {

        public Node dragged;
        public float  mouseLocalXPrev;
        public float  mouseLocalYPrev;
        public float  mouseLocalX;
        public float  mouseLocalY;

        public MouseDragEnter(Node target, Node dragged, float mouseLocalXPrev, float mouseLocalYPrev, float mouseLocalX, float mouseLocalY) {
            super(target);
            this.dragged = dragged;
            this.mouseLocalXPrev = mouseLocalXPrev;
            this.mouseLocalYPrev = mouseLocalYPrev;
            this.mouseLocalX = mouseLocalX;
            this.mouseLocalY = mouseLocalY;
        }
    }

    public static class MouseDragLeave extends EventData {

        public Node dragged;
        public float  mouseLocalXPrev;
        public float  mouseLocalYPrev;
        public float  mouseLocalX;
        public float  mouseLocalY;

        public MouseDragLeave(Node target, Node dragged, float mouseLocalXPrev, float mouseLocalYPrev, float mouseLocalX, float mouseLocalY) {
            super(target);
            this.dragged = dragged;
            this.mouseLocalXPrev = mouseLocalXPrev;
            this.mouseLocalYPrev = mouseLocalYPrev;
            this.mouseLocalX = mouseLocalX;
            this.mouseLocalY = mouseLocalY;
        }
    }

    public static class MouseDragDrop extends EventData {

        public Node dragged;
        public float  mouseLocalX;
        public float  mouseLocalY;

        public MouseDragDrop(Node target, Node dragged, float mouseLocalX, float mouseLocalY) {
            super(target);
            this.dragged = dragged;
            this.mouseLocalX = mouseLocalX;
            this.mouseLocalY = mouseLocalY;
        }

    }

    public static class CodepointsTyped extends EventData {

        public ArrayChar codePoints;

        public CodepointsTyped(Node target, char ...chars) {
            super(target);
            this.codePoints = new ArrayChar(true, 1);
            if (chars == null) return;
            for (char aChar : chars) {
                this.codePoints.add(aChar);
            }
        }

    }

    public static class KeysJustPressed extends EventData {

        public Array<Keyboard.Key> keys;

        public KeysJustPressed(Node target, Keyboard.Key ...keys) {
            super(target);
            this.keys = new Array<>(true, 1);
            this.keys.addAll(keys);
        }

    }

    public static class KeysJustReleased extends EventData {

        public Array<Keyboard.Key> keys;

        public KeysJustReleased(Node target, Keyboard.Key ...keys) {
            super(target);
            this.keys = new Array<>(true, 1);
            this.keys.addAll(keys);
        }

    }

}
