package com.heavybox.jtix.widgets_2;

public final class EventListener {

    /*** input - event handlers ***/
    public OnMouseUp          onMouseUp          = null;
    public OnMouseDown        onMouseDown        = null;
    public OnMouseEnter       onMouseEnter       = null;
    public OnMouseLeave       onMouseLeave       = null;
    public OnMouseClick       onMouseClick       = null;
    public OnMouseDoubleClick onMouseDoubleClick = null;
    public OnMouseScroll      onMouseScroll      = null;
    public OnMouseDrag        onMouseDrag        = null;
    public OnMouseDragStart   onMouseDragStart   = null;
    public OnMouseDragEnd     onMouseDragEnd     = null;
    public OnMouseDragEnter   onMouseDragEnter   = null;
    public OnMouseDragLeave   onMouseDragLeave   = null;
    public OnMouseDragDrop    onMouseDragDrop    = null;
    public OnCodepointsTyped  onCodepointsTyped  = null;
    public OnKeysJustPressed  onKeysJustPressed  = null;
    public OnKeysPressed      onKeysPressed      = null;

    @FunctionalInterface
    public interface OnMouseClick {
        void handle(EventData.MouseClick e);
    }

    @FunctionalInterface
    public interface OnMouseDoubleClick {
        void handle(EventData.MouseDoubleClick e);
    }

    @FunctionalInterface
    public interface OnMouseDown {
        void handle(EventData.MouseDown e);
    }

    @FunctionalInterface
    public interface OnMouseUp {
        void handle(EventData.MouseUp e);
    }

    @FunctionalInterface
    public interface OnMouseEnter {
        void handle(EventData.MouseEnter e);
    }

    @FunctionalInterface
    public interface OnMouseLeave {
        void handle(EventData.MouseLeave e);
    }

    @FunctionalInterface
    public interface OnMouseScroll {
        void handle(EventData.MouseScroll e);
    }

    @FunctionalInterface
    public interface OnMouseDrag {
        void handle(EventData.MouseDrag e);
    }

    @FunctionalInterface
    public interface OnMouseDragStart {
        void handle(EventData.MouseDragStart e);
    }

    @FunctionalInterface
    public interface OnMouseDragEnd {
        void handle(EventData.MouseDragEnd e);
    }

    @FunctionalInterface
    public interface OnMouseDragEnter {
        void handle(EventData.MouseDragEnter e);
    }

    @FunctionalInterface
    public interface OnMouseDragLeave {
        void handle(EventData.MouseDragLeave e);
    }

    @FunctionalInterface
    public interface OnMouseDragDrop {
        void handle(EventData.MouseDragDrop e);
    }

    @FunctionalInterface
    public interface OnCodepointsTyped {
        void handle(EventData.CodepointsTyped e);
    }

    @FunctionalInterface
    public interface OnKeysJustPressed {
        void handle(EventData.KeysJustPressed e);
    }

    @FunctionalInterface
    public interface OnKeysPressed {
        void handle(EventData.KeysPressed e);
    }

}
