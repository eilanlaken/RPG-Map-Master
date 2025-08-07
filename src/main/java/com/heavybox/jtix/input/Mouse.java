package com.heavybox.jtix.input;

import com.heavybox.jtix.application.Application;
import org.lwjgl.glfw.*;

public class Mouse {

    /* mouse info */
    private int     prevCursorX      = 0;
    private int     prevCursorY      = 0;
    private int     cursorX          = 0;
    private int     cursorY          = 0;
    private int     cursorDeltaX     = 0;
    private int     cursorDeltaY     = 0;
    private boolean cursorHidden     = false;
    private boolean cursorInWindow   = true;
    private boolean cursorEnteredWindow = false;
    private boolean cursorLeftWindow    = false;
    private float   verticalScroll   = 0;
    private float   horizontalScroll = 0;

    /* mouse state */
    private final int[] mouseButtonsPrevStates    = new int[5];
    private final int[] mouseButtonsCurrentStates = new int[5];

    Mouse() {
        GLFW.glfwSetMouseButtonCallback(Application.getWindowHandle(), new GLFWMouseButtonCallback() {
            @Override
            public void invoke(long window, int button, int action, int mods) {
                mouseButtonsPrevStates[button] = mouseButtonsCurrentStates[button];
                mouseButtonsCurrentStates[button] = action;
            }
        });

        GLFW.glfwSetCursorPosCallback(Application.getWindowHandle(), new GLFWCursorPosCallback() {
            @Override
            public void invoke(long window, double xPos, double yPos) {
                prevCursorX = cursorX;
                prevCursorY = cursorY;
                cursorX = (int) xPos;
                cursorY = (int) yPos;
                cursorDeltaX = cursorX - prevCursorX;
                cursorDeltaY = cursorY - prevCursorY;
            }
        });

        GLFW.glfwSetCursorEnterCallback(Application.getWindowHandle(), new GLFWCursorEnterCallback() {
            @Override
            public void invoke(long window, boolean entered) {
                cursorInWindow = entered;
                cursorEnteredWindow = entered;
                cursorLeftWindow = !entered;
            }
        });

        GLFW.glfwSetScrollCallback(Application.getWindowHandle(), new GLFWScrollCallback() {
            @Override
            public void invoke(long window, double xOffset, double yOffset) {
                verticalScroll = (float) yOffset;
                horizontalScroll = (float) xOffset;
            }
        });
    }

    public float getVerticalScroll() {
        return verticalScroll;
    }

    public float getHorizontalScroll() {
        return horizontalScroll;
    }

    public void hideCursor() {
        GLFW.glfwSetInputMode(Application.getWindowHandle(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_HIDDEN);
        cursorHidden = true;
    }

    public void revealCursor() {
        GLFW.glfwSetInputMode(Application.getWindowHandle(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
        cursorHidden = false;
    }

    public boolean isCursorHidden() {
        return cursorHidden;
    }

    public boolean isInsideWindow() {
        return true;
    }

    public int getX() { return cursorX; }

    public int getY() { return cursorY; }

    public int getXPrev() {
        return prevCursorX;
    }

    public int getYPrev() {
        return prevCursorY;
    }

    public int getXDelta() { return cursorDeltaX; }

    public int getYDelta() { return cursorDeltaY; }

    // TODO: test
    public void setCursorPosition(float x, float y) {
        GLFW.glfwSetCursorPos(Application.getWindowHandle(), x, y);
    }

    public boolean isCursorInWindow() {
        return cursorInWindow;
    }

    public boolean moved() {
        return cursorDeltaX != 0 || cursorDeltaY != 0;
    }

    public boolean isButtonPressed(final Button button) {
        return mouseButtonsCurrentStates[button.glfwCode] == GLFW.GLFW_PRESS;
    }

    public boolean isButtonJustPressed(final Button button) {
        return mouseButtonsCurrentStates[button.glfwCode] == GLFW.GLFW_PRESS && mouseButtonsPrevStates[button.glfwCode] != GLFW.GLFW_PRESS;
    }

    public boolean isButtonReleased(final Button button) {
        return mouseButtonsCurrentStates[button.glfwCode] == GLFW.GLFW_RELEASE;
    }

    // TODO: test
    public boolean isButtonJustReleased(final Button button) {
        return mouseButtonsCurrentStates[button.glfwCode] == GLFW.GLFW_RELEASE && mouseButtonsPrevStates[button.glfwCode] == GLFW.GLFW_PRESS;
    }

    public boolean isButtonClicked(final Button button) {
        return mouseButtonsPrevStates[button.glfwCode] == GLFW.GLFW_PRESS && mouseButtonsCurrentStates[button.glfwCode] == GLFW.GLFW_RELEASE;
    }

    public boolean cursorJustEnteredWindow() {
        return cursorEnteredWindow;
    }

    public boolean cursorJustLeftWindow() {
        return cursorLeftWindow;
    }

    void update() {
        /* reset internal state */
        verticalScroll = 0;
        horizontalScroll = 0;
        cursorDeltaX = 0;
        cursorDeltaY = 0;
        mouseButtonsPrevStates[GLFW.GLFW_MOUSE_BUTTON_1] = mouseButtonsCurrentStates[GLFW.GLFW_MOUSE_BUTTON_1];
        mouseButtonsPrevStates[GLFW.GLFW_MOUSE_BUTTON_2] = mouseButtonsCurrentStates[GLFW.GLFW_MOUSE_BUTTON_2];
        mouseButtonsPrevStates[GLFW.GLFW_MOUSE_BUTTON_3] = mouseButtonsCurrentStates[GLFW.GLFW_MOUSE_BUTTON_3];
        mouseButtonsPrevStates[GLFW.GLFW_MOUSE_BUTTON_4] = mouseButtonsCurrentStates[GLFW.GLFW_MOUSE_BUTTON_4];
        mouseButtonsPrevStates[GLFW.GLFW_MOUSE_BUTTON_5] = mouseButtonsCurrentStates[GLFW.GLFW_MOUSE_BUTTON_5];
        cursorEnteredWindow = false;
        cursorLeftWindow = false;
    }

    public enum Button {

        LEFT(GLFW.GLFW_MOUSE_BUTTON_1),
        RIGHT(GLFW.GLFW_MOUSE_BUTTON_2),
        MIDDLE(GLFW.GLFW_MOUSE_BUTTON_3),
        BACK(GLFW.GLFW_MOUSE_BUTTON_4),
        FORWARD(GLFW.GLFW_MOUSE_BUTTON_5)
        ;

        public final int glfwCode;

        Button(final int glfwCode) {
            this.glfwCode = glfwCode;
        }

    }

}
