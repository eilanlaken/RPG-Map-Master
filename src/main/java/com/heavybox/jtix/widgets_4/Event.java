package com.heavybox.jtix.widgets_4;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.collections.ArrayChar;
import com.heavybox.jtix.input.Keyboard;

// TODO: continue with keyboard event listeners, drag and drop events etc.
// TODO: add global transform to all events. Example use: you want to shoot sparkles onMouseEnter, and you need to know where the widget is.
public abstract class Event {

    public final float widgetX;
    public final float widgetY;
    public final float widgetDeg;
    public final float widgetSclX;
    public final float widgetSclY;

    Event(final Transform global) {
        this.widgetX = global.x;
        this.widgetY = global.y;
        this.widgetDeg = global.deg;
        this.widgetSclX = global.sclX;
        this.widgetSclY = global.sclY;
    }

    public static class EventMouseDown extends Event {

        EventMouseDown(final Transform global) {
            super(global);
        }

        public boolean buttonLeft   = false;
        public boolean buttonRight  = false;
        public boolean buttonMiddle = false;
        public float   mouseLocalX;
        public float   mouseLocalY;

    }

    public static class EventMouseUp extends Event {

        EventMouseUp(final Transform global) {
            super(global);
        }

        public boolean buttonLeft   = false;
        public boolean buttonRight  = false;
        public boolean buttonMiddle = false;
        public float   mouseLocalX;
        public float   mouseLocalY;

    }

    public static class EventMouseLeftClick extends Event {

        EventMouseLeftClick(final Transform global) {
            super(global);
        }

        public float mouseLocalX;
        public float mouseLocalY;

    }

    public static class EventMouseRightClick extends Event {

        EventMouseRightClick(final Transform global) {
            super(global);
        }

        public float mouseLocalX;
        public float mouseLocalY;

    }

    public static class EventMouseMiddleClick extends Event {

        EventMouseMiddleClick(final Transform global) {
            super(global);
        }

        public float mouseLocalX;
        public float mouseLocalY;

    }

    public static class EventMouseLeftClickOutside extends Event {

        EventMouseLeftClickOutside(final Transform global) {
            super(global);
        }

        public float mouseLocalX;
        public float mouseLocalY;

    }

    public static class EventMouseRightClickOutside extends Event {

        EventMouseRightClickOutside(final Transform global) {
            super(global);
        }

        public float mouseLocalX;
        public float mouseLocalY;

    }

    public static class EventMouseMiddleClickOutside extends Event {

        EventMouseMiddleClickOutside(final Transform global) {
            super(global);
        }

        public float mouseLocalX;
        public float mouseLocalY;

    }

    public static class EventMouseEnter extends Event {

        EventMouseEnter(final Transform global) {
            super(global);
        }

        public float mouseLocalXPrev;
        public float mouseLocalYPrev;
        public float mouseLocalX;
        public float mouseLocalY;

    }

    public static class EventMouseLeave extends Event {

        EventMouseLeave(final Transform global) {
            super(global);
        }

        public float mouseLocalXPrev;
        public float mouseLocalYPrev;
        public float mouseLocalX;
        public float mouseLocalY;

    }

    public static class EventMouseScroll extends Event {

        EventMouseScroll(final Transform global) {
            super(global);
        }

        public float scrollValue;
        public float mouseLocalX;
        public float mouseLocalY;

    }

    public static class EventMouseDrag extends Event {

        EventMouseDrag(final Transform global) {
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

        EventMouseDragStart(final Transform global) {
            super(global);
        }

        public float mouseLocalX;
        public float mouseLocalY;

    }

    public static class EventMouseDragEnd extends Event {

        EventMouseDragEnd(final Transform global) {
            super(global);
        }

        public float mouseLocalX;
        public float mouseLocalY;

    }

    public static class EventResize extends Event {

        EventResize(final Transform global) {
            super(global);
        }

        public float prevWidth;
        public float prevHeight;
        public float newWidth;
        public float newHeight;

    }

    public static class EventChildAdded extends Event {

        EventChildAdded(final Transform global) {
            super(global);
        }

        public Widget widget;

    }

    public static class EventChildRemoved extends Event {

        EventChildRemoved(final Transform global) {
            super(global);
        }

        public Widget widget;
        public int    index;

    }

    public static class EventCodepointsTyped extends Event {

        EventCodepointsTyped(final Transform global) {
            super(global);
        }

        public ArrayChar codePoints;

    }

    public static class EventKeysJustPressed extends Event {

        EventKeysJustPressed(final Transform global) {
            super(global);
        }

        public Array<Keyboard.Key> keys = new Array<>(true, 1);

    }

    // TODO: not sure if this is the way to handle it.
    public static class EventKeysPressed extends Event {

        EventKeysPressed(final Transform global) {
            super(global);
        }

        public Array<Keyboard.Key> keys = new Array<>(true, 1);

    }


    @FunctionalInterface
    public interface EventListenerMouseLeftClick {
        boolean handle(EventMouseLeftClick e);
    }

    @FunctionalInterface
    public interface EventListenerMouseRightClick {
        boolean handle(EventMouseRightClick e);
    }

    @FunctionalInterface
    public interface EventListenerMouseMiddleClick {
        boolean handle(EventMouseMiddleClick e);
    }

    @FunctionalInterface
    public interface EventListenerMouseLeftClickOutside {
        boolean handle(EventMouseLeftClickOutside e);
    }

    @FunctionalInterface
    public interface EventListenerMouseRightClickOutside {
        boolean handle(EventMouseRightClickOutside e);
    }

    @FunctionalInterface
    public interface EventListenerMouseMiddleClickOutside {
        boolean handle(EventMouseMiddleClickOutside e);
    }

    @FunctionalInterface
    public interface EventListenerMouseDown {
        boolean handle(EventMouseDown e);
    }

    @FunctionalInterface
    public interface EventListenerMouseUp {
        boolean handle(EventMouseUp e);
    }

    @FunctionalInterface
    public interface EventListenerMouseEnter {
        boolean handle(EventMouseEnter e);
    }

    @FunctionalInterface
    public interface EventListenerMouseLeave {
        boolean handle(EventMouseLeave e);
    }

    @FunctionalInterface
    public interface EventListenerMouseScroll {
        boolean handle(EventMouseScroll e);
    }

    @FunctionalInterface
    public interface EventListenerMouseDrag {
        boolean handle(EventMouseDrag e);
    }

    @FunctionalInterface
    public interface EventListenerMouseDragStart {
        boolean handle(EventMouseDragStart e);
    }

    @FunctionalInterface
    public interface EventListenerMouseDragEnd {
        boolean handle(EventMouseDragEnd e);
    }

    @FunctionalInterface
    public interface EventListenerResize {
        boolean handle(EventResize e);
    }

    @FunctionalInterface
    public interface EventListenerChildAdded {
        boolean handle(EventChildAdded e);
    }

    @FunctionalInterface
    public interface EventListenerChildRemoved {
        boolean handle(EventChildRemoved e);
    }

    @FunctionalInterface
    public interface EventListenerCodepointsTyped {
        boolean handle(EventCodepointsTyped e);
    }

    @FunctionalInterface
    public interface EventListenerKeysJustPressed {
        boolean handle(EventKeysJustPressed e);
    }

    @FunctionalInterface
    public interface EventListenerKeysPressed {
        boolean handle(EventKeysPressed e);
    }

}
