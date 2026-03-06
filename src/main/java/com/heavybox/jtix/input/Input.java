package com.heavybox.jtix.input;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.collections.ArrayChar;
import com.heavybox.jtix.math.MathUtils;

import java.util.Comparator;

public final class Input {

    public static final Keyboard keyboard = new Keyboard();
    public static final Mouse    mouse    = new Mouse();
    public static final Webcam   webcam   = new Webcam();

    private static final Array<InputEventHandler> inputEventHandlers   = new Array<>(true, 3);

    private Input() {}

    public static void update() {
        // sort input layers every frame as the layers index may change.
        inputEventHandlers.sort(Comparator.comparingInt(InputEventHandler::getLayer).reversed());

        // update event handlers - mouse scrolled.
        float scrollX = mouse.getScrollX();
        float scrollY = mouse.getScrollY();
        if (!MathUtils.isZero(scrollX) || !MathUtils.isZero(scrollY)) {
            for (InputEventHandler inputEventHandler : inputEventHandlers) {
                boolean handled = inputEventHandler.mouseScrolled(scrollX, scrollY);
                if (handled) break;
            }
        }

        // update event handlers - mouse on button(s) down
        final Array<Mouse.Button> mouseButtonsJustPressed = mouse.getButtonsJustPressed();
        if (!mouseButtonsJustPressed.isEmpty()) {
            for (InputEventHandler inputEventHandler : inputEventHandlers) {
                boolean handled = inputEventHandler.mouseButtonsDown(mouse.getX(), mouse.getY(), mouseButtonsJustPressed);
                if (handled) break;
            }
        }

        // update event handlers - mouse on button(s) up
        final Array<Mouse.Button> mouseButtonsJustReleased = mouse.getButtonsJustReleased();
        if (!mouseButtonsJustReleased.isEmpty()) {
            for (InputEventHandler inputEventHandler : inputEventHandlers) {
                boolean handled = inputEventHandler.mouseButtonsUp(mouse.getX(), mouse.getY(), mouseButtonsJustReleased);
                if (handled) break;
            }
        }

        // update event handlers - mouse moved
        boolean mouseMoved = mouse.moved();
        if (mouseMoved) {
            for (InputEventHandler inputEventHandler : inputEventHandlers) {
                boolean handled = inputEventHandler.mouseMoved(mouse.getX(), mouse.getY(), mouse.getXDelta(), mouse.getYDelta());
                if (handled) break;
            }
        }

        // update event handlers - mouse dragged
        final Array<Mouse.Button> mouseButtonsPressed = mouse.getButtonsPressed();
        final boolean mouseDragged = mouse.moved() && !mouseButtonsPressed.isEmpty();
        if (mouseDragged) {
            for (InputEventHandler inputEventHandler : inputEventHandlers) {
                boolean handled = inputEventHandler.mouseDragged(mouse.getX(), mouse.getY(), mouse.getXDelta(), mouse.getYDelta(), mouseButtonsPressed);
                if (handled) break;
            }
        }

        // update event handlers - keyboard buttons down
        final Array<Keyboard.Key> keyboardKeysJustPressed = keyboard.getKeysJustDown();
        if (!keyboardKeysJustPressed.isEmpty()) {
            for (InputEventHandler inputEventHandler : inputEventHandlers) {
                boolean handled = inputEventHandler.keyboardKeysJustPressed(keyboardKeysJustPressed);
                if (handled) break;
            }
        }

        // update event handlers - keyboard buttons up
        final Array<Keyboard.Key> keyboardKeysJustReleased = keyboard.getKeysJustUp();
        if (!keyboardKeysJustReleased.isEmpty()) {
            for (InputEventHandler inputEventHandler : inputEventHandlers) {
                boolean handled = inputEventHandler.keyboardKeysJustReleased(keyboardKeysJustReleased);
                if (handled) break;
            }
        }

        // update event handlers - keyboard codepoint typed
        final ArrayChar codepointTyped = keyboard.getCodepointsTyped();
        if (!codepointTyped.isEmpty()) {
            for (InputEventHandler inputEventHandler : inputEventHandlers) {
                boolean handled = inputEventHandler.keyboardCodepointsTyped(codepointTyped);
                if (handled) break;
            }
        }

        keyboard.update();
        mouse.update();
    }

    public static void cleanup() {
        webcam.deleteAll();
        clearEventHandlers();
    }

    public static void addEventHandler(final InputEventHandler inputEventHandler) {
        if (inputEventHandler == null) throw new InputException("event handler cannot be null.");
        if (inputEventHandlers.contains(inputEventHandler,true)) throw new InputException("cannot register the same event handler more than once");
        inputEventHandlers.add(inputEventHandler);
        inputEventHandlers.sort(Comparator.comparingInt(InputEventHandler::getLayer).reversed());
    }

    public static void removeEventHandler(final InputEventHandler inputEventHandler) {
        inputEventHandlers.removeValue(inputEventHandler,true);
        inputEventHandlers.sort(Comparator.comparingInt(InputEventHandler::getLayer)); // order should be maintained but ok.
    }

    public static void clearEventHandlers() {
        inputEventHandlers.clear();
    }

}
