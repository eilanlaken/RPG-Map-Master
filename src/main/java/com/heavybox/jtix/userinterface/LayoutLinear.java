package com.heavybox.jtix.userinterface;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.math.Transform2D;
import com.heavybox.jtix.widgets.WidgetNode;

public class LayoutLinear implements Layout {

    public boolean vertical            = true;
    public float   primaryAxisOffset   = 0;
    public float   secondaryAxisOffset = 0;
    public float   childSpacing        = 20;

    @Override
    public void setChildTransformOffset(Node parent, Array<Node> childrenLayout, Array<Transform2D> out) {
        // TODO
        if (vertical) { // vertical layout
            float sclY = 1; // ??
            float position_y = parent.getHeight() * 0.5f - primaryAxisOffset;
            for (int i = 0; i < childrenLayout.size; i++) {
                Node child = childrenLayout.get(i);
                Transform2D offset = out.get(i);
                float child_height = child.getHeight() * sclY;
                offset.x = secondaryAxisOffset;
                offset.y = position_y - child_height * 0.5f;
                position_y -= child_height + childSpacing * sclY;
            }
            return;
        }

        // horizontal layout
    }

}
