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

}
