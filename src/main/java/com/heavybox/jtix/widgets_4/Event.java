package com.heavybox.jtix.widgets_4;

import com.heavybox.jtix.input.Mouse;

// TODO: continue with keyboard event listeners, drag and drop events etc.
public abstract class Event {

    public static class EventMouseClick extends Event {

        public Mouse.Button button;
        public float        mouseLocalX;
        public float        mouseLocalY;

    }

    public static class EventMouseDown extends Event {

        public Mouse.Button button;
        public float        mouseLocalX;
        public float        mouseLocalY;

    }

    public static class EventMouseUp extends Event {

        public Mouse.Button button;
        public float        mouseLocalX;
        public float        mouseLocalY;

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

    @FunctionalInterface
    public interface EventListenerMouseClick {
        boolean handle(EventMouseClick e);
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

}
