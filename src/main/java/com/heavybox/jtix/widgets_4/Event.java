package com.heavybox.jtix.widgets_4;

public abstract class Event {

    public static class EventClick extends Event {

        public float mouseLocalX;
        public float mouseLocalY;

    }

    @FunctionalInterface
    public interface ClickListener {
        boolean run(EventClick e);
    }

}
