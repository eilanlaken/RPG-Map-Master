package com.heavybox.jtix.userinterface;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.math.Transform2D;
import com.heavybox.jtix.widgets.WidgetNode;

public class LayoutLinear implements Layout {

    public boolean vertical            = true;
    public float   primaryAxisOffset   = 0;
    public float   secondaryAxisOffset = 0;
    public float   childSpacing        = 20;

    public LayoutLinear(boolean vertical) {
        this.vertical = vertical;
    }

    public LayoutLinear() {
        this(true);
    }

    @Override
    public void setChildTransformOffset(Node parent, Array<Node> childrenLayout, Array<Transform2D> out) {
        // TODO
        if (vertical) { // vertical layout
            float position_y = parent.getHeight() * 0.5f - primaryAxisOffset;
            for (int i = 0; i < childrenLayout.size; i++) {
                Node child = childrenLayout.get(i);
                Transform2D offset = out.get(i).idt(); // get and reset
                float child_height = child.getHeight();
                offset.x = secondaryAxisOffset;
                offset.y = position_y - child_height * 0.5f;
                position_y -= child_height + childSpacing;
            }
            return;
        }

        // horizontal layout
        float position_x = -parent.getWidth() * 0.5f + primaryAxisOffset;
        for (int i = 0; i < childrenLayout.size; i++) {
            Node child = childrenLayout.get(i);
            Transform2D offset = out.get(i).idt(); // get and reset
            float child_width = child.getWidth();
            offset.x = position_x + child_width * 0.5f;;
            offset.y = secondaryAxisOffset;
            position_x += child_width + childSpacing;
        }
    }

}
