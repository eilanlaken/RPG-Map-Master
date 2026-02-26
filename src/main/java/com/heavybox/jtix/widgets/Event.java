package com.heavybox.jtix.widgets;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.collections.ArrayChar;
import com.heavybox.jtix.input.Keyboard;
import com.heavybox.jtix.math.Transform2D;

public abstract class Event {

    public final float nodeX;
    public final float nodeY;
    public final float nodeDeg;
    public final float nodeSclX;
    public final float nodeSclY;

    Event(final Transform2D global) {
        this.nodeX = global.x;
        this.nodeY = global.y;
        this.nodeDeg = global.deg;
        this.nodeSclX = global.sclX;
        this.nodeSclY = global.sclY;
    }

    public static class EventMouseDown extends Event {

        EventMouseDown(final Transform2D global) {
            super(global);
        }

        public boolean buttonLeft   = false;
        public boolean buttonRight  = false;
        public boolean buttonMiddle = false;
        public float   mouseLocalX;
        public float   mouseLocalY;

    }

    public static class EventMouseUp extends Event {

        EventMouseUp(final Transform2D global) {
            super(global);
        }

        public boolean buttonLeft   = false;
        public boolean buttonRight  = false;
        public boolean buttonMiddle = false;
        public float   mouseLocalX;
        public float   mouseLocalY;

    }

    public static class EventMouseLeftClick extends Event {

        EventMouseLeftClick(final Transform2D global) {
            super(global);
        }

        public float mouseLocalX;
        public float mouseLocalY;

    }

    public static class EventMouseRightClick extends Event {

        EventMouseRightClick(final Transform2D global) {
            super(global);
        }

        public float mouseLocalX;
        public float mouseLocalY;

    }

    public static class EventMouseMiddleClick extends Event {

        EventMouseMiddleClick(final Transform2D global) {
            super(global);
        }

        public float mouseLocalX;
        public float mouseLocalY;

    }

    public static class EventMouseLeftClickOutside extends Event {

        EventMouseLeftClickOutside(final Transform2D global) {
            super(global);
        }

        public float mouseLocalX;
        public float mouseLocalY;

    }

    public static class EventMouseRightClickOutside extends Event {

        EventMouseRightClickOutside(final Transform2D global) {
            super(global);
        }

        public float mouseLocalX;
        public float mouseLocalY;

    }

    public static class EventMouseMiddleClickOutside extends Event {

        EventMouseMiddleClickOutside(final Transform2D global) {
            super(global);
        }

        public float mouseLocalX;
        public float mouseLocalY;

    }

    public static class EventMouseEnter extends Event {

        EventMouseEnter(final Transform2D global) {
            super(global);
        }

        public float mouseLocalXPrev;
        public float mouseLocalYPrev;
        public float mouseLocalX;
        public float mouseLocalY;

    }

    public static class EventMouseLeave extends Event {

        EventMouseLeave(final Transform2D global) {
            super(global);
        }

        public float mouseLocalXPrev;
        public float mouseLocalYPrev;
        public float mouseLocalX;
        public float mouseLocalY;

    }

    public static class EventMouseScroll extends Event {

        EventMouseScroll(final Transform2D global) {
            super(global);
        }

        public float scrollValue;
        public float mouseLocalX;
        public float mouseLocalY;

    }

    public static class EventMouseDrag extends Event {

        EventMouseDrag(final Transform2D global) {
            super(global);
        }

        public float mouseLocalXPrev;
        public float mouseLocalYPrev;
        public float mouseLocalX;
        public float mouseLocalY;
        public float mouseLocalDeltaX;
        public float mouseLocalDeltaY;

    }

    public static class EventMouseDragStart extends Event {

        EventMouseDragStart(final Transform2D global) {
            super(global);
        }

        public float mouseLocalX;
        public float mouseLocalY;

    }

    public static class EventMouseDragEnd extends Event {

        EventMouseDragEnd(final Transform2D global) {
            super(global);
        }

        public float mouseLocalX;
        public float mouseLocalY;

    }

    public static class EventResize extends Event {

        EventResize(final Transform2D global) {
            super(global);
        }

        public float prevWidth;
        public float prevHeight;
        public float newWidth;
        public float newHeight;

    }

    public static class EventChildAdded extends Event {

        EventChildAdded(final Transform2D global) {
            super(global);
        }

        public WidgetNode node;

    }

    public static class EventChildRemoved extends Event {

        EventChildRemoved(final Transform2D global) {
            super(global);
        }

        public WidgetNode node;
        public int        index;

    }

    public static class EventCodepointsTyped extends Event {

        EventCodepointsTyped(final Transform2D global) {
            super(global);
        }

        public ArrayChar codePoints;

    }

    public static class EventKeysJustPressed extends Event {

        EventKeysJustPressed(final Transform2D global) {
            super(global);
        }

        public Array<Keyboard.Key> keys = new Array<>(true, 1);

    }

    public static class EventKeysPressed extends Event {

        EventKeysPressed(final Transform2D global) {
            super(global);
        }

        public Array<Keyboard.Key> keys = new Array<>(true, 1);

    }


    @FunctionalInterface
    public interface EventListenerMouseLeftClick {
        void handle(EventMouseLeftClick e);
    }

    @FunctionalInterface
    public interface EventListenerMouseRightClick {
        void handle(EventMouseRightClick e);
    }

    @FunctionalInterface
    public interface EventListenerMouseMiddleClick {
        void handle(EventMouseMiddleClick e);
    }

    @FunctionalInterface
    public interface EventListenerMouseLeftClickOutside {
        void handle(EventMouseLeftClickOutside e);
    }

    @FunctionalInterface
    public interface EventListenerMouseRightClickOutside {
        void handle(EventMouseRightClickOutside e);
    }

    @FunctionalInterface
    public interface EventListenerMouseMiddleClickOutside {
        void handle(EventMouseMiddleClickOutside e);
    }

    @FunctionalInterface
    public interface EventListenerMouseDown {
        void handle(EventMouseDown e);
    }

    @FunctionalInterface
    public interface EventListenerMouseUp {
        void handle(EventMouseUp e);
    }

    @FunctionalInterface
    public interface EventListenerMouseEnter {
        void handle(EventMouseEnter e);
    }

    @FunctionalInterface
    public interface EventListenerMouseLeave {
        void handle(EventMouseLeave e);
    }

    @FunctionalInterface
    public interface EventListenerMouseScroll {
        void handle(EventMouseScroll e);
    }

    @FunctionalInterface
    public interface EventListenerMouseDrag {
        void handle(EventMouseDrag e);
    }

    @FunctionalInterface
    public interface EventListenerMouseDragStart {
        void handle(EventMouseDragStart e);
    }

    @FunctionalInterface
    public interface EventListenerMouseDragEnd {
        void handle(EventMouseDragEnd e);
    }

    @FunctionalInterface
    public interface EventListenerResize {
        void handle(EventResize e);
    }

    @FunctionalInterface
    public interface EventListenerChildAdded {
        void handle(EventChildAdded e);
    }

    @FunctionalInterface
    public interface EventListenerChildRemoved {
        void handle(EventChildRemoved e);
    }

    @FunctionalInterface
    public interface EventListenerCodepointsTyped {
        void handle(EventCodepointsTyped e);
    }

    @FunctionalInterface
    public interface EventListenerKeysJustPressed {
        void handle(EventKeysJustPressed e);
    }

    @FunctionalInterface
    public interface EventListenerKeysPressed {
        void handle(EventKeysPressed e);
    }

}
