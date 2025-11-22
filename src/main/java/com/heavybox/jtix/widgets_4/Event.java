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

    @FunctionalInterface
    public interface EventListenerMouseClick {
        boolean run(EventMouseClick e);
    }

    @FunctionalInterface
    public interface EventListenerMouseDown {
        boolean run(EventMouseDown e);
    }

    @FunctionalInterface
    public interface EventListenerMouseUp {
        boolean run(EventMouseUp e);
    }

    @FunctionalInterface
    public interface EventListenerMouseEnter {
        boolean run(EventMouseEnter e);
    }

    @FunctionalInterface
    public interface EventListenerMouseLeave {
        boolean run(EventMouseLeave e);
    }

}
