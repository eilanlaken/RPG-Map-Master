package com.heavybox.jtix.userinterface;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.math.Transform2D;

public interface Layout {

    default void setChildTransformOffset(UI parent, final Array<UI> childrenLayout, final Array<Transform2D> out) {
        for (Transform2D offset : out) offset.idt();
    }

}
