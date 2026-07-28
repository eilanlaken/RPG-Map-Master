package com.heavybox.jtix.widgets_2;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.collections.ArrayChar;
import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Graphics;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.input.Input;
import com.heavybox.jtix.input.InputEventHandler;
import com.heavybox.jtix.input.Keyboard;
import com.heavybox.jtix.input.Mouse;
import com.heavybox.jtix.math.Transform2D;
import com.heavybox.jtix.math.Vector2;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;

public final class Widgets {

    public  static final Comparator<Node> widgetComparator = Comparator.comparingInt(a -> a.zIndex);
    private static final float            WHITE_FLOAT_BITS = Color.WHITE.toFloatBits(); // to reset the color to white before re-rendering components

    /*** tmp Widget helpers ***/
    static final Array<Node>        layoutChildren = new Array<>(true, 10);
    static final Array<Transform2D> layoutOffsets  = new Array<>(true, 10);

    /*** some global flags ***/
    private static int     currentID = 0;
    public  static boolean debugMode = true; // TODO: use this when rendering: render regions if true.

    /*** input device state */
    private static Node inputMouseOnTarget   = null;
    private static Node inputMouseDownTarget = null;
    private static Node inputMouseUpTarget   = null;
    private static Node inputMouseDragTarget = null;

    private static float pointerXPrev = 0;
    private static float pointerYPrev = 0;
    private static float pointerX     = 0;
    private static float pointerY     = 0;

    /*** current scene widgets hierarchy */
    private static final Array<Node> rootWidgets = new Array<>(false, 5);
    private static final Array<Node> toReplace   = new Array<>(false, 1);

    /*** input event handling ***/
    private static final InputEventHandler inputEventHandler = new InputEventHandler() {
        @Override
        public int getInputLayer() { return Integer.MAX_VALUE; }

        @Override
        public boolean isActive() { return true; }

        @Override
        public boolean mouseButtonsDown(int mouseX, int mouseY, @NotNull Array<Mouse.Button> buttons) {
            Node target = findTopmostChildAt(pointerX, pointerY);
            inputMouseDownTarget = target;
            if (target == null) return false; // hit an empty space.

            /* travels to the top-most component that *handles* the event - if any. */
            while (target != null) {
                if (target.eventListener.onMouseDown != null || target.eventListenerDefault.onMouseDown != null) break;
                if (target.eventListener.onMouseDoubleClick != null || target.eventListenerDefault.onMouseDoubleClick != null) break;
                if (target.eventListener.onMouseDragStart != null || target.eventListenerDefault.onMouseDragStart != null) break;
                else target = target.getParent();
            }

            if (target == null) return true; // none of the components handle the event.

            Vector2 local = new Vector2(pointerX, pointerY);
            local.transform_TranslateRotateScale(-target.getTransformScreen().x, -target.getTransformScreen().y, -target.getTransformScreen().deg, 1 / target.getTransformScreen().sclX, 1/ target.getTransformScreen().sclY);

            // taking care of on mouse down event
            EventData.MouseDown mouseDown = new EventData.MouseDown(
                    target,
                    buttons.contains(Mouse.Button.LEFT, true),
                    buttons.contains(Mouse.Button.RIGHT, true),
                    buttons.contains(Mouse.Button.MIDDLE, true),
                    local.x,
                    local.y
            );
            if (target.eventListener.onMouseDown != null) {
                target.eventListener.onMouseDown.handle(mouseDown);
            }
            if (target.eventListenerDefault.onMouseDown != null) {
                target.eventListenerDefault.onMouseDown.handle(mouseDown);
            }

            // taking care of potential double click
            final Array<Mouse.Button> doubleClicks = Input.mouse.getButtonsDoubleClicked();
            if (!doubleClicks.isEmpty()) {
                EventData.MouseDoubleClick doubleClick = new EventData.MouseDoubleClick(
                        target,
                        buttons.contains(Mouse.Button.LEFT, true),
                        buttons.contains(Mouse.Button.RIGHT, true),
                        buttons.contains(Mouse.Button.MIDDLE, true),
                        local.x,
                        local.y
                );
                if (target.eventListener.onMouseDoubleClick != null) {
                    target.eventListener.onMouseDoubleClick.handle(doubleClick);
                }
                if (target.eventListenerDefault.onMouseDoubleClick != null) {
                    target.eventListenerDefault.onMouseDoubleClick.handle(doubleClick);
                }
            }

            if (!buttons.contains(Mouse.Button.LEFT, true)) return true;

            // taking care of mouse drag start
            EventData.MouseDragStart mouseDragStart = new EventData.MouseDragStart(
                    target,
                    local.x,
                    local.y
            );
            if (target.eventListener.onMouseDragStart != null) {
                target.eventListener.onMouseDragStart.handle(mouseDragStart);
                inputMouseDragTarget = target;
            }
            if (target.eventListenerDefault.onMouseDragStart != null) {
                target.eventListenerDefault.onMouseDragStart.handle(mouseDragStart);
                inputMouseDragTarget = target;
            }

            return true;
        }

        @Override
        public boolean mouseButtonsUp(int mouseX, int mouseY, @NotNull Array<Mouse.Button> buttons) {
            Node target = findTopmostChildAt(pointerX, pointerY);
            inputMouseUpTarget = target;

            if (target == null) return false; // no target of the component tree was hit.

            /* travels to the top-most component that handles the event. */
            while (target != null) {
                if (target == inputMouseDragTarget) break;
                if (target.eventListener.onMouseUp != null || target.eventListenerDefault.onMouseUp != null) break;
                if (target.eventListener.onMouseClick != null || target.eventListenerDefault.onMouseClick != null) break;
                if (target.eventListener.onMouseDragEnd != null || target.eventListenerDefault.onMouseDragEnd != null) break;
                else target = target.getParent();
            }

            if (target == null) return true; // none of the components handle the event.

            Vector2 local = new Vector2(pointerX, pointerY);
            local.transform_TranslateRotateScale(-target.getTransformScreen().x, -target.getTransformScreen().y, -target.getTransformScreen().deg, 1 / target.getTransformScreen().sclX, 1/ target.getTransformScreen().sclY);

            // taking care of on mouse up event
            EventData.MouseUp mouseUp = new EventData.MouseUp(
                    target,
                    buttons.contains(Mouse.Button.LEFT, true),
                    buttons.contains(Mouse.Button.RIGHT, true),
                    buttons.contains(Mouse.Button.MIDDLE, true),
                    local.x,
                    local.y
            );
            if (target.eventListener.onMouseUp != null) {
                target.eventListener.onMouseUp.handle(mouseUp);
            }
            if (target.eventListenerDefault.onMouseUp != null) {
                target.eventListenerDefault.onMouseUp.handle(mouseUp);
            }

            if (inputMouseDownTarget == inputMouseUpTarget) {
                EventData.MouseClick mouseClick = new EventData.MouseClick(
                        target,
                        buttons.contains(Mouse.Button.LEFT, true),
                        buttons.contains(Mouse.Button.RIGHT, true),
                        buttons.contains(Mouse.Button.MIDDLE, true),
                        local.x,
                        local.y
                );
                if (target.eventListener.onMouseClick != null) {
                    target.eventListener.onMouseClick.handle(mouseClick);
                }
                if (target.eventListenerDefault.onMouseClick != null) {
                    target.eventListenerDefault.onMouseClick.handle(mouseClick);
                }
            }

            if (!buttons.contains(Mouse.Button.LEFT, true)) return true;
            if (inputMouseDragTarget == null) return true;

            /* taking care of mouse drag end event */
            EventData.MouseDragEnd mouseDragEnd = new EventData.MouseDragEnd(
                    inputMouseDragTarget,
                    local.x,
                    local.y
            );
            if (inputMouseDragTarget.eventListener.onMouseDragEnd != null) {
                inputMouseDragTarget.eventListener.onMouseDragEnd.handle(mouseDragEnd);
            }
            if (inputMouseDragTarget.eventListenerDefault.onMouseDragEnd != null) {
                inputMouseDragTarget.eventListenerDefault.onMouseDragEnd.handle(mouseDragEnd);
            }

            /* taking care of mouse drag drop event */
            Node dropTarget = findTopmostChildAtPointerUnderWidget(pointerX, pointerY, inputMouseDragTarget);
            if (dropTarget != null) {
                EventData.MouseDragDrop dragDrop = new EventData.MouseDragDrop(
                        dropTarget,
                        inputMouseDragTarget,
                        local.x,
                        local.y
                );
                if (dropTarget.eventListener.onMouseDragDrop != null) {
                    dropTarget.eventListener.onMouseDragDrop.handle(dragDrop);
                }
                if (dropTarget.eventListenerDefault.onMouseDragDrop != null) {
                    dropTarget.eventListenerDefault.onMouseDragDrop.handle(dragDrop);
                }
            }

            inputMouseDragTarget = null;

            return true;
        }

        /*
        the actual mouse movement is handled inside the update().
        why? because what matters is the movement of the mouse relative to the widget.
        it could be that the widget has MOVED TOWARDS THE MOUSE (while the mouse was stale).
        in that case, mouseMoved() would never be invoked.
        */
        @Override
        public boolean mouseMoved(int mouseX, int mouseY, int deltaMouseX, int deltaMouseY) {
            Node target = findTopmostChildAt(pointerX, pointerY);

            if (inputMouseDragTarget != null) {
                if (inputMouseDragTarget.eventListener.onMouseDrag == null && inputMouseDragTarget.eventListenerDefault.onMouseDrag == null) return true;

                Vector2 local = new Vector2(pointerX, pointerY);
                local.transform_TranslateRotateScale(-inputMouseDragTarget.getTransformScreen().x, -inputMouseDragTarget.getTransformScreen().y, -inputMouseDragTarget.getTransformScreen().deg, 1 / inputMouseDragTarget.getTransformScreen().sclX, 1/ inputMouseDragTarget.getTransformScreen().sclY);
                Vector2 localPrev = new Vector2(pointerXPrev, pointerYPrev);
                localPrev.transform_TranslateRotateScale(-inputMouseDragTarget.getTransformScreen().x, -inputMouseDragTarget.getTransformScreen().y, -inputMouseDragTarget.getTransformScreen().deg, 1 / inputMouseDragTarget.getTransformScreen().sclX, 1/ inputMouseDragTarget.getTransformScreen().sclY);
                EventData.MouseDrag mouseDrag = new EventData.MouseDrag(
                        inputMouseDragTarget,
                        localPrev.x,
                        localPrev.y,
                        local.x,
                        local.y
                );
                if (inputMouseDragTarget.eventListener.onMouseDrag != null) {
                    inputMouseDragTarget.eventListener.onMouseDrag.handle(mouseDrag);
                }
                if (inputMouseDragTarget.eventListenerDefault.onMouseDrag != null) {
                    inputMouseDragTarget.eventListenerDefault.onMouseDrag.handle(mouseDrag);
                }

                return true;
            }

            return target == null;
        }

        @Override
        public boolean mouseScrolled(float scrollX, float scrollY) {
            Node target = findTopmostChildAt(pointerX, pointerY);
            if (target == null) return false; // no target of the component tree was hit.

            /* travels to the top-most component that handles the event. */
            while (target != null) {
                if (target.eventListener.onMouseScroll != null || target.eventListenerDefault.onMouseScroll != null) break;
                else target = target.getParent();
            }
            if (target == null) return true; // none of the components handle the event.

            Vector2 local = new Vector2(pointerX, pointerY);
            local.transform_TranslateRotateScale(-target.getTransformScreen().x, -target.getTransformScreen().y, -target.getTransformScreen().deg, 1 / target.getTransformScreen().sclX, 1/ target.getTransformScreen().sclY);
            EventData.MouseScroll eventData = new EventData.MouseScroll(
                    target,
                    scrollX,
                    scrollY,
                    local.x,
                    local.y
            );
            if (target.eventListener.onMouseScroll != null) {
                target.eventListener.onMouseScroll.handle(eventData);
            }
            if (target.eventListenerDefault.onMouseScroll != null) {
                target.eventListenerDefault.onMouseScroll.handle(eventData);
            }

            return true;
        }

        @Override
        public boolean mouseDragged(int mouseX, int mouseY, int deltaMouseX, int deltaMouseY, @NotNull Array<Mouse.Button> buttons) {
            return InputEventHandler.super.mouseDragged(mouseX, mouseY, deltaMouseX, deltaMouseY, buttons);
        }

        @Override
        public boolean keyboardKeysJustPressed(@NotNull Array<Keyboard.Key> keys) {
            return InputEventHandler.super.keyboardKeysJustPressed(keys);
        }

        @Override
        public boolean keyboardKeysJustReleased(@NotNull Array<Keyboard.Key> keys) {
            return InputEventHandler.super.keyboardKeysJustReleased(keys);
        }

        @Override
        public boolean keyboardCodepointsTyped(@NotNull ArrayChar codepoints) {
            return InputEventHandler.super.keyboardCodepointsTyped(codepoints);
        }
    };


    private Widgets() {}

    public static float getPointerX()     { return pointerX; }
    public static float getPointerY()     { return pointerY; }
    public static float getPointerXPrev() { return pointerXPrev; }
    public static float getPointerYPrev() { return pointerYPrev; }

    private static Node findTopmostChildAt(float pointerX, float pointerY) {
        for (int i = rootWidgets.size - 1; i >= 0; i--) {
            Node topmost = rootWidgets.get(i).findTopmostChildAt(pointerX, pointerY);
            if (topmost != null) return topmost;
        }
        return null;
    }

    private static Node findTopmostChildAtPointerUnderWidget(float pointerX, float pointerY, final Node top) {
        for (int i = rootWidgets.size - 1; i >= 0; i--) {
            Node topmost = rootWidgets.get(i).findTopmostChildAt(pointerX, pointerY);
            if (topmost != null && topmost != top) return topmost;
        }
        return null;
    }

    public static void update() {
        Input.registerEventHandler(inputEventHandler);
        layoutChildren.clear();
        layoutOffsets.clear();

        pointerXPrev = pointerX;
        pointerYPrev = pointerY;
        pointerX = Input.mouse.getX() - Graphics.getWindowWidth() * 0.5f;
        pointerY = Graphics.getWindowHeight() * 0.5f - Input.mouse.getY();

        // consolidate root nodes
        toReplace.clear();
        for (Node node : rootWidgets) {
            if (!node.isRoot()) toReplace.add(node);
        }
        for (Node node : toReplace) {
            Node newRoot = node.getRoot();
            if (!rootWidgets.contains(newRoot, true)) rootWidgets.replaceFirst(node, newRoot, true);
            else rootWidgets.removeValue(node, true);
        }
        rootWidgets.sort(widgetComparator);

        final float delta = Graphics.getDeltaTime();
        for (Node node : rootWidgets) {
            if (!node.isRoot()) continue;
            if (!node.isActive()) continue;
            node.update(delta);
        }

        inputMouseOnTarget = findTopmostChildAt(pointerX, pointerY);
    }

    public static void render(Renderer2D renderer2D) {
        // iterate over all *root* widget nodes and perform renders
        rootWidgets.sort(widgetComparator);
        for (Node node : rootWidgets) {
            if (!node.isRoot()) continue; // to be extra sure.
            if (!node.isActive()) continue; // to be extra sure.
            renderer2D.setColor(WHITE_FLOAT_BITS);
            node.render(renderer2D);
        }
    }

    public static void add(final Node node) {
        Node root = node.getRoot();
        if (rootWidgets.contains(root, true)) return;

        rootWidgets.add(root);
    }

    public static void remove(@NotNull final Node node) {
        if (node.isRoot()) {
            rootWidgets.removeValue(node, true);
            return;
        }

        Node parent = node.parent;
        parent.children.removeValue(node, true);
        node.parent = null; // severe the connection completely
    }

    public static void clear() {
        inputMouseDragTarget = null;
        inputMouseOnTarget = null;
        inputMouseDownTarget = null;
        inputMouseUpTarget = null;
        layoutChildren.clear();
        layoutOffsets.clear();
        Input.unregisterEventHandler(inputEventHandler);
        rootWidgets.clear();
        currentID = 0;
    }

    /* package private methods */
    static Node getInputMouseDragTarget() {
        return inputMouseDragTarget;
    }

    static Node getInputMouseOnTarget() {
        return inputMouseOnTarget;
    }

    static boolean isXAncestorOfY(final Node X, final Node Y) {
        if (X == null || Y == null) return false;

        Node current = Y.getParent();
        while (current != null) {
            if (current == X) return true;
            current = current.getParent();
        }
        return false;
    }

    static int getID() {
        return currentID++;
    }

}
