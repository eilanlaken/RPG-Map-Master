package com.heavybox.jtix.input;

import com.heavybox.jtix.application.Application;
import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.collections.Tuple3;
import com.heavybox.jtix.math.Vector3;
import org.lwjgl.glfw.*;

// TODO: add double-click detection with sensitivity parameters
public final class Mouse {

    /*** static constants ***/
    private static final Button[] ALL_BUTTONS        = Button.values();
    private static final float    doubleClickTimeSec = 0.5f;
    private static final float    doubleClickPixels  = 4;

    /*** mouse info ***/
    private int     prevCursorX         = 0;
    private int     prevCursorY         = 0;
    private int     cursorX             = 0;
    private int     cursorY             = 0;
    private int     cursorDeltaX        = 0;
    private int     cursorDeltaY        = 0;
    private boolean cursorHidden        = false;
    private boolean cursorInWindow      = true;
    private boolean cursorEnteredWindow = false;
    private boolean cursorLeftWindow    = false;
    private float   scrollY = 0;
    private float   scrollX = 0;

    /*** mouse button state trackers ***/
    private final int[]     mouseButtonsPrevStates    = new int[5];
    private final int[]     mouseButtonsCurrentStates = new int[5];
    private final Vector3[] doubleClickTracker        = new Vector3[ALL_BUTTONS.length];
    private final boolean[] doubleClickButtons        = new boolean[ALL_BUTTONS.length];

    /*** mouse frame buttons pressed, just-pressed, released, just-released ***/
    private final Array<Button> buttonsPressed           = new Array<>(false, 5);
    private       boolean       buttonsPressedDirty      = true;
    private final Array<Button> buttonsJustPressed       = new Array<>(false, 5);
    private       boolean       buttonsJustPressedDirty  = true;
    private final Array<Button> buttonsJustReleased      = new Array<>(false, 5);
    private       boolean       buttonsJustReleasedDirty = true;

    Mouse() {
        GLFW.glfwSetMouseButtonCallback(Application.getWindowHandle(), new GLFWMouseButtonCallback() {
            @Override
            public void invoke(long window, int button, int action, int mods) {
                mouseButtonsPrevStates[button] = mouseButtonsCurrentStates[button];
                mouseButtonsCurrentStates[button] = action;

                /* handle double clicks */
                if (action == GLFW.GLFW_PRESS) {
                    Vector3 state = doubleClickTracker[button];
                    if (state == null) {
                        doubleClickTracker[button] = new Vector3();
                        doubleClickTracker[button].x = cursorX;
                        doubleClickTracker[button].y = cursorY;
                        doubleClickTracker[button].z = System.nanoTime();
                    } else {
                        final float now = System.nanoTime();
                        float dt = (now - doubleClickTracker[button].z) / 1_000_000_000f;
                        float dx = Math.abs(cursorX - doubleClickTracker[button].x);
                        float dy = Math.abs(cursorY - doubleClickTracker[button].y);
                        boolean doubleClicked = dt < 0.5f && dx < 8 && dy < 8;
                        doubleClickButtons[button] = doubleClicked;
                        if (doubleClicked) doubleClickTracker[button] = null;
                        else {
                            doubleClickTracker[button].x = cursorX;
                            doubleClickTracker[button].y = cursorY;
                            doubleClickTracker[button].z = now;
                        }
                    }
                }

                return; // end
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
                scrollY = (float) yOffset;
                scrollX = (float) xOffset;
            }
        });
    }

    public float getScrollY() {
        return scrollY;
    }

    public float getScrollX() {
        return scrollX;
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
        return cursorDeltaX != 0 || cursorDeltaY != 0 || cursorLeftWindow || cursorEnteredWindow;
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

    public boolean isButtonJustReleased(final Button button) {
        return mouseButtonsCurrentStates[button.glfwCode] == GLFW.GLFW_RELEASE && mouseButtonsPrevStates[button.glfwCode] == GLFW.GLFW_PRESS;
    }

    public boolean isButtonClicked(final Button button) {
        return mouseButtonsPrevStates[button.glfwCode] == GLFW.GLFW_PRESS && mouseButtonsCurrentStates[button.glfwCode] == GLFW.GLFW_RELEASE;
    }

    // TODO
    public boolean isButtonDoubleClicked(final Button button) {
        return doubleClickButtons[button.ordinal()];
    }

    public boolean cursorJustEnteredWindow() {
        return cursorEnteredWindow;
    }

    public boolean cursorJustLeftWindow() {
        return cursorLeftWindow;
    }

    public Array<Button> getButtonsPressed() {
        if (!buttonsPressedDirty) return buttonsPressed;

        for (Button button : ALL_BUTTONS) {
            if (isButtonPressed(button)) buttonsPressed.add(button);
        }
        buttonsPressedDirty = false;
        return buttonsPressed;
    }

    public Array<Button> getButtonsJustPressed() {
        if (!buttonsJustPressedDirty) return buttonsJustPressed;

        for (Button button : ALL_BUTTONS) {
            if (isButtonJustPressed(button)) buttonsJustPressed.add(button);
        }
        buttonsJustPressedDirty = false;
        return buttonsJustPressed;
    }

    public Array<Button> getButtonsJustReleased() {
        if (!buttonsJustReleasedDirty) return buttonsJustReleased;

        for (Button button : ALL_BUTTONS) {
            if (isButtonJustReleased(button)) buttonsJustReleased.add(button);
        }
        buttonsJustReleasedDirty = false;
        return buttonsJustReleased;
    }

    void update() {
        /* reset internal state */
        scrollY = 0;
        scrollX = 0;
        cursorDeltaX = 0;
        cursorDeltaY = 0;
        mouseButtonsPrevStates[GLFW.GLFW_MOUSE_BUTTON_1] = mouseButtonsCurrentStates[GLFW.GLFW_MOUSE_BUTTON_1];
        mouseButtonsPrevStates[GLFW.GLFW_MOUSE_BUTTON_2] = mouseButtonsCurrentStates[GLFW.GLFW_MOUSE_BUTTON_2];
        mouseButtonsPrevStates[GLFW.GLFW_MOUSE_BUTTON_3] = mouseButtonsCurrentStates[GLFW.GLFW_MOUSE_BUTTON_3];
        mouseButtonsPrevStates[GLFW.GLFW_MOUSE_BUTTON_4] = mouseButtonsCurrentStates[GLFW.GLFW_MOUSE_BUTTON_4];
        mouseButtonsPrevStates[GLFW.GLFW_MOUSE_BUTTON_5] = mouseButtonsCurrentStates[GLFW.GLFW_MOUSE_BUTTON_5];

        for (int i = 0; i < doubleClickButtons.length; i++) {
            doubleClickButtons[i] = false;
        }

        buttonsPressed.clear();
        buttonsPressedDirty = true;
        buttonsJustPressed.clear();
        buttonsJustPressedDirty = true;
        buttonsJustReleased.clear();
        buttonsJustReleasedDirty = true;

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
