package com.heavybox.jtix.input;

// TODO
public interface InputLayer {

    int getLevel();

    default boolean mouseButtonDown(Mouse.Button button) {
        return false;
    }

}
