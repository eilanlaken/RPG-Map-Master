package com.heavybox.jtix;

import com.heavybox.jtix.application.Scene;
import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.collections.ArrayChar;
import com.heavybox.jtix.input.Input;
import com.heavybox.jtix.input.InputEventHandler;
import com.heavybox.jtix.input.Mouse;
import org.jetbrains.annotations.NotNull;

public class SceneInput_2 implements Scene {

    InputEventHandler second = new InputEventHandler() {

        @Override
        public boolean isActive() {
            return false;
        }

        @Override
        public boolean mouseScrolled(float scrollX, float scrollY) {
            System.out.println(scrollX + " , " + scrollY);
            return true;
        }

        @Override
        public boolean keyboardCodepointsTyped(@NotNull ArrayChar codepoints) {
            System.out.println(codepoints.size);
            return true;
        }

        @Override
        public boolean mouseDragged(int mouseX, int mouseY, int deltaMouseX, int deltaMouseY, @NotNull Array<Mouse.Button> buttons) {
            System.out.println(deltaMouseX + " , " + buttons.first());
            return true;
        }

        @Override
        public int getInputLayer() {
            return 1;
        }
    };

    @Override
    public void start() {
        Input.addEventHandler(second);
    }

    @Override
    public void update() {

    }

    @Override
    public boolean isActive() {
        return true;
    }

    @Override
    public void finish() {

    }

}
