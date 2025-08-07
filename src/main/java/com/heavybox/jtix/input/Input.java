package com.heavybox.jtix.input;

import com.heavybox.jtix.collections.Array;

public final class Input {

    public static final Keyboard keyboard = new Keyboard();
    public static final Mouse    mouse    = new Mouse();
    public static final Webcam   webcam   = new Webcam();

    // TODO:
    private static InputHandler inputHandler = null;

    private Input() {}

    public static void update() {
        keyboard.update();
        mouse.update();
    }

    public static void setInputHandler(final InputHandler handler) {
        Input.inputHandler = handler;
    }

    public static void cleanup() {
        webcam.deleteAll();
    }

}
