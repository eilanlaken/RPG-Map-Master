package com.heavybox.jtix.widgets_4;

import com.heavybox.jtix.collections.ArrayChar;

// TODO: continue with keyboard event listeners, drag and drop events etc.
public abstract class Event {

    public static class EventMouseDown extends Event {

        public boolean buttonLeft   = false;
        public boolean buttonRight  = false;
        public boolean buttonMiddle = false;
        public float   mouseLocalX;
        public float   mouseLocalY;

    }

    public static class EventMouseUp extends Event {

        public boolean buttonLeft   = false;
        public boolean buttonRight  = false;
        public boolean buttonMiddle = false;
        public float   mouseLocalX;
        public float   mouseLocalY;

    }

    public static class EventMouseLeftClick extends Event {

        public float mouseLocalX;
        public float mouseLocalY;

    }

    public static class EventMouseRightClick extends Event {

        public float mouseLocalX;
        public float mouseLocalY;

    }

    public static class EventMouseMiddleClick extends Event {

        public float mouseLocalX;
        public float mouseLocalY;

    }

    public static class EventMouseLeftClickOutside extends Event {

        public float mouseLocalX;
        public float mouseLocalY;

    }

    public static class EventMouseRightClickOutside extends Event {

        public float mouseLocalX;
        public float mouseLocalY;

    }

    public static class EventMouseMiddleClickOutside extends Event {

        public float mouseLocalX;
        public float mouseLocalY;

    }

    public static class EventMouseEnter extends Event {

        public float mouseLocalXPrev;
        public float mouseLocalYPrev;
        public float mouseLocalX;
        public float mouseLocalY;

    }

    public static class EventMouseLeave extends Event {

        public float mouseLocalXPrev;
        public float mouseLocalYPrev;
        public float mouseLocalX;
        public float mouseLocalY;

    }

    public static class EventMouseScroll extends Event {

        public float scrollValue;
        public float mouseLocalX;
        public float mouseLocalY;

    }

    public static class EventMouseDrag extends Event {

        public float mouseLocalXPrev;
        public float mouseLocalYPrev;
        public float mouseLocalX;
        public float mouseLocalY;
        public float mouseLocalDeltaX;
        public float mouseLocalDeltaY;

    }

    public static class EventMouseDragStart extends Event {

        public float mouseLocalX;
        public float mouseLocalY;

    }

    public static class EventMouseDragEnd extends Event {

        public float mouseLocalX;
        public float mouseLocalY;

    }

    public static class EventResize extends Event {

        public float prevWidth;
        public float prevHeight;
        public float newWidth;
        public float newHeight;

    }

    public static class EventChildAdded extends Event {

        public Widget widget;

    }

    public static class EventChildRemoved extends Event {

        public Widget widget;
        public int    index;

    }

    // TODO
//    public static class EventKeyDown extends Event {
//
//        public Keyboard.Key key;
//        public int          codePoint;
//
//    }
//
//    public static class EventKeyUp extends Event {
//
//        public Keyboard.Key key;
//        public int          codePoint;
//
//    }

    public static class EventCodepointTyped extends Event {

        public ArrayChar codePoints;

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
    public interface EventListenerCodepointTyped {
        boolean handle(EventCodepointTyped e);
    }

}
