package com.heavybox.jtix.widgets_2;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.math.Transform2D;

public interface Layout {

    default boolean includes(final Widget child) {
        if (child == null) return false;
        return child.active && child.anchor != null;
    }

    default void setChildTransformOffset(final Array<Widget> childrenLayout, final Array<Transform2D> out) {
        for (Transform2D offset : out) offset.idt();
    }

}
