package com.heavybox.jtix.userinterface;

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

public final class UserInterface {

    public static final Comparator<Node> NODE_COMPARATOR = Comparator.comparingInt(a -> a.zIndex);

    /*** tmp Widget helpers ***/
    static final Array<Node>        layoutChildren = new Array<>(true, 10);
    static final Array<Transform2D> layoutOffsets  = new Array<>(true, 10);

    /*** some global flags ***/
    private static int     currentID = 0;
    public  static boolean debugMode = true; // TODO: use this when rendering: render regions if true.

    /*** nodes and input device state management */
    private static Node  inputMouseTarget        = null;
    private static Node  inputMouseDragTarget    = null;
    private static Node  inputMouseDragUnder     = null;
    private static Node  inputMouseDragUnderPrev = null;
    private static Node  inputMouseDownTarget    = null;
    private static Node  inputMouseUpTarget      = null;
    private static float pointerXPrev            = 0;
    private static float pointerYPrev            = 0;
    private static float pointerX                = 0;
    private static float pointerY                = 0;

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
            Node target = findTopmostChildAt(pointerX, pointerY, null);
            inputMouseDownTarget = target;
            if (target == null) return false; // hit an empty space.

            /* travels to the top-most component that *handles* the event - if any. */
            while (target != null) {
                if (target.eventListener.onMouseDown != null || target.eventListenerDefault.onMouseDown != null) break;
                if (target.eventListener.onMouseDoubleClick != null || target.eventListenerDefault.onMouseDoubleClick != null) break;
                if (target.eventListener.onMouseDragStart != null || target.eventListenerDefault.onMouseDragStart != null) break;
                target = target.getParent();
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
            }
            if (target.eventListenerDefault.onMouseDragStart != null) {
                target.eventListenerDefault.onMouseDragStart.handle(mouseDragStart);
            }
            inputMouseDragTarget = target;

            return true;
        }

        @Override
        public boolean mouseButtonsUp(int mouseX, int mouseY, @NotNull Array<Mouse.Button> buttons) {
            Node target = findTopmostChildAt(pointerX, pointerY, null);
            inputMouseUpTarget = target;

            if (target == null) return false; // no target of the component tree was hit.

            /* travels to the top-most component that handles the event. */
            while (target != null) {
                if (target == inputMouseDragTarget) break;
                if (target.eventListener.onMouseUp != null || target.eventListenerDefault.onMouseUp != null) break;
                if (target.eventListener.onMouseClick != null || target.eventListenerDefault.onMouseClick != null) break;
                if (target.eventListener.onMouseDragEnd != null || target.eventListenerDefault.onMouseDragEnd != null) break;
                target = target.getParent();
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
            Node dropTarget = findTopmostChildAt(pointerX, pointerY, inputMouseDragTarget);
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

        @Override
        public boolean mouseMoved(int mouseX, int mouseY, int deltaMouseX, int deltaMouseY) {
            Node inputMouseTargetPrev = inputMouseTarget;
            inputMouseTarget = findTopmostChildAt(pointerX, pointerY, null);

            if (inputMouseTarget == inputMouseTargetPrev) return inputMouseTarget != null;

            /* handle possibly leaving prev target */
            if (inputMouseTargetPrev != null) {
                Node leaveEventTarget = inputMouseTargetPrev;
                while (leaveEventTarget != null) {
                    if (leaveEventTarget.eventListener.onMouseLeave != null) break;
                    if (leaveEventTarget.eventListenerDefault.onMouseLeave != null) break;
                    leaveEventTarget = leaveEventTarget.parent;
                }

                // left prev subtree
                boolean leftSubtree = leaveEventTarget != null && !isXAncestorOfY(leaveEventTarget, inputMouseTarget);
                boolean triggerOnMouseLeave = leftSubtree && leaveEventTarget.eventListener.onMouseLeave != null;
                boolean triggerOnMouseLeaveDefault = leftSubtree && leaveEventTarget.eventListenerDefault.onMouseLeave != null;
                if (triggerOnMouseLeave) {
                    Vector2 local = new Vector2(pointerX, pointerY);
                    Vector2 localPrevFrame = new Vector2(pointerXPrev, pointerYPrev);
                    local.transform_TranslateRotateScale(-leaveEventTarget.getTransformScreen().x, -leaveEventTarget.getTransformScreen().y, -leaveEventTarget.getTransformScreen().deg, 1 / leaveEventTarget.getTransformScreen().sclX, 1/ leaveEventTarget.getTransformScreen().sclY);
                    localPrevFrame.transform_TranslateRotateScale(-leaveEventTarget.getTransformScreen().x, -leaveEventTarget.getTransformScreen().y, -leaveEventTarget.getTransformScreen().deg, 1 / leaveEventTarget.getTransformScreen().sclX, 1/ leaveEventTarget.getTransformScreen().sclY);
                    EventData.MouseLeave mouseLeave = new EventData.MouseLeave(leaveEventTarget, localPrevFrame.x, localPrevFrame.y, local.x, local.y);
                    leaveEventTarget.eventListener.onMouseLeave.handle(mouseLeave);
                }
                if (triggerOnMouseLeaveDefault) {
                    Vector2 local = new Vector2(pointerX, pointerY);
                    Vector2 localPrevFrame = new Vector2(pointerXPrev, pointerYPrev);
                    local.transform_TranslateRotateScale(-leaveEventTarget.getTransformScreen().x, -leaveEventTarget.getTransformScreen().y, -leaveEventTarget.getTransformScreen().deg, 1 / leaveEventTarget.getTransformScreen().sclX, 1/ leaveEventTarget.getTransformScreen().sclY);
                    localPrevFrame.transform_TranslateRotateScale(-leaveEventTarget.getTransformScreen().x, -leaveEventTarget.getTransformScreen().y, -leaveEventTarget.getTransformScreen().deg, 1 / leaveEventTarget.getTransformScreen().sclX, 1/ leaveEventTarget.getTransformScreen().sclY);
                    EventData.MouseLeave mouseLeave = new EventData.MouseLeave(leaveEventTarget, localPrevFrame.x, localPrevFrame.y, local.x, local.y);
                    leaveEventTarget.eventListenerDefault.onMouseLeave.handle(mouseLeave);
                }
            }

            /* handle possibly entering current target */
            if (inputMouseTarget != null) {
                Node enterEventTarget = inputMouseTarget;
                while (enterEventTarget != null) {
                    if (enterEventTarget.eventListener.onMouseEnter != null) break;
                    if (enterEventTarget.eventListenerDefault.onMouseEnter != null) break;
                    enterEventTarget = enterEventTarget.parent;
                }

                boolean enteredSubtree = enterEventTarget != null && !isXAncestorOfY(enterEventTarget, inputMouseTargetPrev);
                boolean triggerOnMouseEnter = enteredSubtree && enterEventTarget.eventListener.onMouseEnter != null;
                boolean triggerOnMouseEnterDefault = enteredSubtree && enterEventTarget.eventListenerDefault.onMouseEnter != null;
                // enter current subtree
                if (triggerOnMouseEnter) {
                    Vector2 local = new Vector2(pointerX, pointerY);
                    Vector2 localPrevFrame = new Vector2(pointerXPrev, pointerYPrev);
                    local.transform_TranslateRotateScale(-enterEventTarget.getTransformScreen().x, -enterEventTarget.getTransformScreen().y, -enterEventTarget.getTransformScreen().deg, 1 / enterEventTarget.getTransformScreen().sclX, 1/ enterEventTarget.getTransformScreen().sclY);
                    localPrevFrame.transform_TranslateRotateScale(-enterEventTarget.getTransformScreen().x, -enterEventTarget.getTransformScreen().y, -enterEventTarget.getTransformScreen().deg, 1 / enterEventTarget.getTransformScreen().sclX, 1/ enterEventTarget.getTransformScreen().sclY);
                    EventData.MouseEnter mouseEnter = new EventData.MouseEnter(enterEventTarget, localPrevFrame.x, localPrevFrame.y, local.x, local.y);
                    enterEventTarget.eventListener.onMouseEnter.handle(mouseEnter);
                }
                if (triggerOnMouseEnterDefault) {
                    Vector2 local = new Vector2(pointerX, pointerY);
                    Vector2 localPrevFrame = new Vector2(pointerXPrev, pointerYPrev);
                    local.transform_TranslateRotateScale(-enterEventTarget.getTransformScreen().x, -enterEventTarget.getTransformScreen().y, -enterEventTarget.getTransformScreen().deg, 1 / enterEventTarget.getTransformScreen().sclX, 1/ enterEventTarget.getTransformScreen().sclY);
                    localPrevFrame.transform_TranslateRotateScale(-enterEventTarget.getTransformScreen().x, -enterEventTarget.getTransformScreen().y, -enterEventTarget.getTransformScreen().deg, 1 / enterEventTarget.getTransformScreen().sclX, 1/ enterEventTarget.getTransformScreen().sclY);
                    EventData.MouseEnter mouseEnter = new EventData.MouseEnter(enterEventTarget, localPrevFrame.x, localPrevFrame.y, local.x, local.y);
                    enterEventTarget.eventListenerDefault.onMouseEnter.handle(mouseEnter);
                }
            }

            return inputMouseTarget != null;
        }

        @Override
        public boolean mouseScrolled(float scrollX, float scrollY) {
            Node target = findTopmostChildAt(pointerX, pointerY, null);
            if (target == null) return false; // no target of the component tree was hit.

            /* travels to the top-most component that handles the event. */
            while (target != null) {
                if (target.eventListener.onMouseScroll != null || target.eventListenerDefault.onMouseScroll != null) break;
                target = target.getParent();
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
            Node mouseOver = findTopmostChildAt(pointerX, pointerY, null);
            if (inputMouseDragTarget == null) return mouseOver != null;

            boolean triggerOnMouseDrag = inputMouseDragTarget.eventListener.onMouseDrag != null;
            boolean triggerOnMouseDragDefault = inputMouseDragTarget.eventListenerDefault.onMouseDrag != null;
            if (triggerOnMouseDrag || triggerOnMouseDragDefault) {
                Vector2 local = new Vector2(pointerX, pointerY);
                Vector2 localPrevFrame = new Vector2(pointerXPrev, pointerYPrev);
                local.transform_TranslateRotateScale(-inputMouseDragTarget.getTransformScreen().x, -inputMouseDragTarget.getTransformScreen().y, -inputMouseDragTarget.getTransformScreen().deg, 1 / inputMouseDragTarget.getTransformScreen().sclX, 1 / inputMouseDragTarget.getTransformScreen().sclY);
                localPrevFrame.transform_TranslateRotateScale(-inputMouseDragTarget.getTransformScreen().x, -inputMouseDragTarget.getTransformScreen().y, -inputMouseDragTarget.getTransformScreen().deg, 1 / inputMouseDragTarget.getTransformScreen().sclX, 1 / inputMouseDragTarget.getTransformScreen().sclY);
                EventData.MouseDrag mouseDrag = new EventData.MouseDrag(
                        inputMouseDragTarget,
                        localPrevFrame.x,
                        localPrevFrame.y,
                        local.x,
                        local.y
                );
                if (triggerOnMouseDrag) inputMouseDragTarget.eventListener.onMouseDrag.handle(mouseDrag);
                if (triggerOnMouseDragDefault) inputMouseDragTarget.eventListenerDefault.onMouseDrag.handle(mouseDrag);
            }

            inputMouseDragUnderPrev = inputMouseDragUnder;
            inputMouseDragUnder = findTopmostChildAt(pointerX, pointerY, inputMouseDragTarget);
            if (inputMouseDragUnder == inputMouseDragUnderPrev) return mouseOver != null;

            /* handle drag leave event */
            if (inputMouseDragUnderPrev != null) {
                Node dragLeaveEventTarget = inputMouseDragUnderPrev;
                while (dragLeaveEventTarget != null) {
                    if (dragLeaveEventTarget.eventListener.onMouseDragLeave != null) break;
                    if (dragLeaveEventTarget.eventListenerDefault.onMouseDragLeave != null) break;
                    dragLeaveEventTarget = dragLeaveEventTarget.parent;
                }

                // left prev subtree
                boolean dragLeftSubtree = dragLeaveEventTarget != null && !isXAncestorOfY(dragLeaveEventTarget, inputMouseDragUnder);
                boolean triggerOnMouseDragLeave = dragLeftSubtree && dragLeaveEventTarget.eventListener.onMouseDragLeave != null;
                boolean triggerOnMouseDragLeaveDefault = dragLeftSubtree && dragLeaveEventTarget.eventListenerDefault.onMouseDragLeave != null;
                if (triggerOnMouseDragLeave) {
                    Vector2 local = new Vector2(pointerX, pointerY);
                    Vector2 localPrevFrame = new Vector2(pointerXPrev, pointerYPrev);
                    local.transform_TranslateRotateScale(-dragLeaveEventTarget.getTransformScreen().x, -dragLeaveEventTarget.getTransformScreen().y, -dragLeaveEventTarget.getTransformScreen().deg, 1 / dragLeaveEventTarget.getTransformScreen().sclX, 1/ dragLeaveEventTarget.getTransformScreen().sclY);
                    localPrevFrame.transform_TranslateRotateScale(-dragLeaveEventTarget.getTransformScreen().x, -dragLeaveEventTarget.getTransformScreen().y, -dragLeaveEventTarget.getTransformScreen().deg, 1 / dragLeaveEventTarget.getTransformScreen().sclX, 1/ dragLeaveEventTarget.getTransformScreen().sclY);
                    EventData.MouseDragLeave mouseDragLeave = new EventData.MouseDragLeave(dragLeaveEventTarget, inputMouseDragTarget, localPrevFrame.x, localPrevFrame.y, local.x, local.y);
                    dragLeaveEventTarget.eventListener.onMouseDragLeave.handle(mouseDragLeave);
                }
                if (triggerOnMouseDragLeaveDefault) {
                    Vector2 local = new Vector2(pointerX, pointerY);
                    Vector2 localPrevFrame = new Vector2(pointerXPrev, pointerYPrev);
                    local.transform_TranslateRotateScale(-dragLeaveEventTarget.getTransformScreen().x, -dragLeaveEventTarget.getTransformScreen().y, -dragLeaveEventTarget.getTransformScreen().deg, 1 / dragLeaveEventTarget.getTransformScreen().sclX, 1/ dragLeaveEventTarget.getTransformScreen().sclY);
                    localPrevFrame.transform_TranslateRotateScale(-dragLeaveEventTarget.getTransformScreen().x, -dragLeaveEventTarget.getTransformScreen().y, -dragLeaveEventTarget.getTransformScreen().deg, 1 / dragLeaveEventTarget.getTransformScreen().sclX, 1/ dragLeaveEventTarget.getTransformScreen().sclY);
                    EventData.MouseDragLeave mouseDragLeave = new EventData.MouseDragLeave(dragLeaveEventTarget, inputMouseDragTarget, localPrevFrame.x, localPrevFrame.y, local.x, local.y);
                    dragLeaveEventTarget.eventListenerDefault.onMouseDragLeave.handle(mouseDragLeave);
                }
            }

            /* handle possible drag enter */
            if (inputMouseDragUnder != null) {
                Node dragEnterEventTarget = inputMouseDragUnder;
                while (dragEnterEventTarget != null) {
                    if (dragEnterEventTarget.eventListener.onMouseDragEnter != null) break;
                    if (dragEnterEventTarget.eventListenerDefault.onMouseDragEnter != null) break;
                    dragEnterEventTarget = dragEnterEventTarget.parent;
                }

                boolean dragEnteredSubtree = dragEnterEventTarget != null && !isXAncestorOfY(dragEnterEventTarget, inputMouseDragUnderPrev);
                boolean triggerOnMouseDragEnter = dragEnteredSubtree && dragEnterEventTarget.eventListener.onMouseDragEnter != null;
                boolean triggerOnMouseDragEnterDefault = dragEnteredSubtree && dragEnterEventTarget.eventListenerDefault.onMouseDragEnter != null;
                // enter current subtree
                if (triggerOnMouseDragEnter) {
                    Vector2 local = new Vector2(pointerX, pointerY);
                    Vector2 localPrevFrame = new Vector2(pointerXPrev, pointerYPrev);
                    local.transform_TranslateRotateScale(-dragEnterEventTarget.getTransformScreen().x, -dragEnterEventTarget.getTransformScreen().y, -dragEnterEventTarget.getTransformScreen().deg, 1 / dragEnterEventTarget.getTransformScreen().sclX, 1/ dragEnterEventTarget.getTransformScreen().sclY);
                    localPrevFrame.transform_TranslateRotateScale(-dragEnterEventTarget.getTransformScreen().x, -dragEnterEventTarget.getTransformScreen().y, -dragEnterEventTarget.getTransformScreen().deg, 1 / dragEnterEventTarget.getTransformScreen().sclX, 1/ dragEnterEventTarget.getTransformScreen().sclY);
                    EventData.MouseDragEnter mouseDragEnter = new EventData.MouseDragEnter(dragEnterEventTarget, inputMouseDragTarget, localPrevFrame.x, localPrevFrame.y, local.x, local.y);
                    dragEnterEventTarget.eventListener.onMouseDragEnter.handle(mouseDragEnter);
                }
                if (triggerOnMouseDragEnterDefault) {
                    Vector2 local = new Vector2(pointerX, pointerY);
                    Vector2 localPrevFrame = new Vector2(pointerXPrev, pointerYPrev);
                    local.transform_TranslateRotateScale(-dragEnterEventTarget.getTransformScreen().x, -dragEnterEventTarget.getTransformScreen().y, -dragEnterEventTarget.getTransformScreen().deg, 1 / dragEnterEventTarget.getTransformScreen().sclX, 1/ dragEnterEventTarget.getTransformScreen().sclY);
                    localPrevFrame.transform_TranslateRotateScale(-dragEnterEventTarget.getTransformScreen().x, -dragEnterEventTarget.getTransformScreen().y, -dragEnterEventTarget.getTransformScreen().deg, 1 / dragEnterEventTarget.getTransformScreen().sclX, 1/ dragEnterEventTarget.getTransformScreen().sclY);
                    EventData.MouseDragEnter mouseDragEnter = new EventData.MouseDragEnter(dragEnterEventTarget, inputMouseDragTarget, localPrevFrame.x, localPrevFrame.y, local.x, local.y);
                    dragEnterEventTarget.eventListenerDefault.onMouseDragEnter.handle(mouseDragEnter);
                }
            }

            return mouseOver != null;
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


    private UserInterface() {}

    public static float getPointerX()     { return pointerX; }
    public static float getPointerY()     { return pointerY; }
    public static float getPointerXPrev() { return pointerXPrev; }
    public static float getPointerYPrev() { return pointerYPrev; }

    private static Node findTopmostChildAt(float pointerX, float pointerY, final Node excluded) {
        for (int i = rootWidgets.size - 1; i >= 0; i--) {
            Node topmost = rootWidgets.get(i).findTopmostChildAt(pointerX, pointerY, excluded);
            if (topmost != null) return topmost;
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
        rootWidgets.sort(NODE_COMPARATOR);

        final float delta = Graphics.getDeltaTime();
        for (Node node : rootWidgets) {
            if (!node.isRoot()) continue;
            if (!node.isActive()) continue;
            node.update(delta);
        }
    }

    public static void render(Renderer2D renderer2D) {
        // iterate over all *root* widget nodes and perform renders
        rootWidgets.sort(NODE_COMPARATOR);
        for (Node node : rootWidgets) {
            if (!node.isRoot()) continue; // to be extra sure.
            if (!node.isActive()) continue; // to be extra sure.
            renderer2D.setColor(Color.WHITE_FLOAT);
            node.render(renderer2D);
        }
    }

    public static void add(final Node node) {
        Node root = node.getRoot();
        if (rootWidgets.contains(root, true)) return;

        rootWidgets.add(root);
    }

    public static void remove(@NotNull final Node node) {
        node.disconnectFromParent();
        rootWidgets.removeValue(node, true);
    }

    public static void cleanup() {
        inputMouseDragTarget = null;
        inputMouseTarget = null;
        inputMouseDownTarget = null;
        inputMouseDragUnderPrev = null;
        inputMouseDragUnder = null;
        inputMouseUpTarget = null;
        layoutChildren.clear();
        layoutOffsets.clear();
        Input.unregisterEventHandler(inputEventHandler);
        rootWidgets.clear();
        currentID = 0;
    }

    /* package private methods */

    static boolean isXAncestorOfY(final Node X, final Node Y) {
        if (X == null || Y == null) return false;
        if (X == Y) return true;

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
