package com.heavybox.jtix.input;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.collections.ArrayChar;
import org.jetbrains.annotations.NotNull;

public interface InputEventHandler {

    int     getInputLayer();
    boolean isActive();

    default boolean mouseButtonsDown(int mouseX, int mouseY, @NotNull final Array<Mouse.Button> buttons) { return false; }
    default boolean mouseButtonsUp  (int mouseX, int mouseY, @NotNull final Array<Mouse.Button> buttons) { return false;}
    default boolean mouseMoved      (int mouseX, int mouseY, int deltaMouseX, int deltaMouseY) { return false; }
    default boolean mouseScrolled   (float scrollX, float scrollY) {
        return false;
    }
    default boolean mouseDragged    (int mouseX, int mouseY, int deltaMouseX, int deltaMouseY, @NotNull final Array<Mouse.Button> buttons) { return false;}

    default boolean keyboardKeysJustPressed (@NotNull final Array<Keyboard.Key> keys) {
        return false;
    }
    default boolean keyboardKeysJustReleased(@NotNull final Array<Keyboard.Key> keys) {
        return false;
    }
    default boolean keyboardCodepointsTyped (@NotNull final ArrayChar codepoints) {
        return false;
    }

}
