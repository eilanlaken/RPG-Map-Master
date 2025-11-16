package com.heavybox.jtix.widgets_3;

import com.heavybox.jtix.collections.Array;

public class Widget {

    public boolean active  = true;
    public Anchor  anchor  = Anchor.CENTER_CENTER;
    public float   anchorX = 0;
    public float   anchorY = 0;

    // an array of root nodes (with no container).
    public Array<Node> nodes = new Array<>(false, 1);

    // anchors ensure spacing between a widget and its parent's edges.
    // when the parent is null, it's the window edges.
    // this is important to make the ui responsive.
    public enum Anchor {
        UPPER_LEFT ,  UPPER_CENTER,  UPPER_RIGHT,
        CENTER_LEFT, CENTER_CENTER, CENTER_RIGHT,
        BOTTOM_LEFT, BOTTOM_CENTER, BOTTOM_RIGHT,
        ;
    }

}
