package com.heavybox.jtix.widgets_2;

import com.heavybox.jtix.widgets.Event;

public class InputEventListener {

    /*** input - event handlers ***/
    public MouseUp                 onMouseUp                 = null;
    public MouseDown               onMouseDown               = null;
    public MouseEnter              onMouseEnter              = null;
    public MouseLeave              onMouseLeave              = null;
    public MouseLeftClick          onMouseLeftClick          = null;
    public MouseRightClick         onMouseRightClick         = null;
    public MouseMiddleClick        onMouseMiddleClick        = null;
    public MouseLeftClickOutside   onMouseLeftClickOutside   = null;
    public MouseRightClickOutside  onMouseRightClickOutside  = null;
    public MouseMiddleClickOutside onMouseMiddleClickOutside = null;
    public MouseScroll             onMouseScroll             = null;
    public MouseDrag               onMouseDrag               = null;
    public MouseDragStart          onMouseDragStart          = null;
    public MouseDragEnd            onMouseDragEnd            = null;
    public CodepointsTyped         onCodepointsTyped         = null;
    public KeysJustPressed         onKeysJustPressed         = null;
    public KeysPressed             onKeysPressed             = null;

    @FunctionalInterface
    public interface MouseLeftClick {
        void handle(InputEventData.MouseLeftClick e);
    }

    @FunctionalInterface
    public interface MouseRightClick {
        void handle(InputEventData.MouseRightClick e);
    }

    @FunctionalInterface
    public interface MouseMiddleClick {
        void handle(InputEventData.MouseMiddleClick e);
    }

    @FunctionalInterface
    public interface MouseLeftClickOutside {
        void handle(InputEventData.MouseLeftClickOutside e);
    }

    @FunctionalInterface
    public interface MouseRightClickOutside {
        void handle(InputEventData.MouseRightClickOutside e);
    }

    @FunctionalInterface
    public interface MouseMiddleClickOutside {
        void handle(InputEventData.MouseMiddleClickOutside e);
    }

    @FunctionalInterface
    public interface MouseDown {
        void handle(InputEventData.MouseDown e);
    }

    @FunctionalInterface
    public interface MouseUp {
        void handle(InputEventData.MouseUp e);
    }

    @FunctionalInterface
    public interface MouseEnter {
        void handle(InputEventData.MouseEnter e);
    }

    @FunctionalInterface
    public interface MouseLeave {
        void handle(InputEventData.MouseLeave e);
    }

    @FunctionalInterface
    public interface MouseScroll {
        void handle(InputEventData.MouseScroll e);
    }

    @FunctionalInterface
    public interface MouseDrag {
        void handle(InputEventData.MouseDrag e);
    }

    @FunctionalInterface
    public interface MouseDragStart {
        void handle(InputEventData.MouseDragStart e);
    }

    @FunctionalInterface
    public interface MouseDragEnd {
        void handle(InputEventData.MouseDragEnd e);
    }

    @FunctionalInterface
    public interface CodepointsTyped {
        void handle(InputEventData.CodepointsTyped e);
    }

    @FunctionalInterface
    public interface KeysJustPressed {
        void handle(InputEventData.KeysJustPressed e);
    }

    @FunctionalInterface
    public interface KeysPressed {
        void handle(InputEventData.KeysPressed e);
    }

}
