package com.heavybox.jtix.application;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.input.InputEventHandler;

public interface Scene extends InputEventHandler {

    /* Scene life-cycle: start() -> update()...[repeat] -> finish() */
    void start();
    void update(); // TODO: refactor update into update(fixedDelta) and render() or frameUpdate(delta)
    void finish();

    /* GLFW Window callbacks. */
    default void windowResized(int width, int height) {}
    default void windowFocused(boolean focus) {}
    default void windowMinimized(boolean minimized) {}
    default void windowMaximized(boolean maximized) {}
    default void windowFilesDraggedAndDropped(Array<String> filePaths) {}

    @Override
    default int getInputLayer() { return Integer.MIN_VALUE; }

}
