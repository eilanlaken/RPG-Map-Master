package com.heavybox.jtix.widgets_2;

public class InputEventListener {

    /*** input - event handlers ***/
    public OnMouseUp                 onMouseUp                 = null;
    public OnMouseDown               onMouseDown               = null;
    public OnMouseEnter              onMouseEnter              = null;
    public OnMouseLeave              onMouseLeave              = null;
    public OnMouseLeftClick          onMouseLeftClick          = null;
    public OnMouseRightClick         onMouseRightClick         = null;
    public OnMouseMiddleClick        onMouseMiddleClick        = null;
    public OnMouseLeftClickOutside   onMouseLeftClickOutside   = null;
    public OnMouseRightClickOutside  onMouseRightClickOutside  = null;
    public OnMouseMiddleClickOutside onMouseMiddleClickOutside = null;
    public OnMouseScroll             onMouseScroll             = null;
    public OnMouseDrag               onMouseDrag               = null;
    public OnMouseDragStart          onMouseDragStart          = null;
    public OnMouseDragEnd            onMouseDragEnd            = null;
    public OnCodepointsTyped         onCodepointsTyped         = null;
    public OnKeysJustPressed         onKeysJustPressed         = null;
    public OnKeysPressed             onKeysPressed             = null;

    @FunctionalInterface
    public interface OnMouseLeftClick {
        void handle(InputEventData.MouseLeftClick e);
    }

    @FunctionalInterface
    public interface OnMouseRightClick {
        void handle(InputEventData.MouseRightClick e);
    }

    @FunctionalInterface
    public interface OnMouseMiddleClick {
        void handle(InputEventData.MouseMiddleClick e);
    }

    @FunctionalInterface
    public interface OnMouseLeftClickOutside {
        void handle(InputEventData.MouseLeftClickOutside e);
    }

    @FunctionalInterface
    public interface OnMouseRightClickOutside {
        void handle(InputEventData.MouseRightClickOutside e);
    }

    @FunctionalInterface
    public interface OnMouseMiddleClickOutside {
        void handle(InputEventData.MouseMiddleClickOutside e);
    }

    @FunctionalInterface
    public interface OnMouseDown {
        void handle(InputEventData.MouseDown e);
    }

    @FunctionalInterface
    public interface OnMouseUp {
        void handle(InputEventData.MouseUp e);
    }

    @FunctionalInterface
    public interface OnMouseEnter {
        void handle(InputEventData.MouseEnter e);
    }

    @FunctionalInterface
    public interface OnMouseLeave {
        void handle(InputEventData.MouseLeave e);
    }

    @FunctionalInterface
    public interface OnMouseScroll {
        void handle(InputEventData.MouseScroll e);
    }

    @FunctionalInterface
    public interface OnMouseDrag {
        void handle(InputEventData.MouseDrag e);
    }

    @FunctionalInterface
    public interface OnMouseDragStart {
        void handle(InputEventData.MouseDragStart e);
    }

    @FunctionalInterface
    public interface OnMouseDragEnd {
        void handle(InputEventData.MouseDragEnd e);
    }

    @FunctionalInterface
    public interface OnCodepointsTyped {
        void handle(InputEventData.CodepointsTyped e);
    }

    @FunctionalInterface
    public interface OnKeysJustPressed {
        void handle(InputEventData.KeysJustPressed e);
    }

    @FunctionalInterface
    public interface OnKeysPressed {
        void handle(InputEventData.KeysPressed e);
    }

}
