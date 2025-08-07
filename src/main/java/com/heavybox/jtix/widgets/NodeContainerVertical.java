package com.heavybox.jtix.widgets;

import com.heavybox.jtix.collections.Array;

public class NodeContainerVertical extends NodeContainer {

    public float margin = 5;

    public NodeContainerVertical() {
        contentOverflowX = Overflow.HIDDEN;
        contentOverflowY = Overflow.SCROLLBAR;
    }

    @Override
    protected void setChildrenOffset(final Array<Node> children) {
        float position_y = (calculateHeight() * 0.5f - boxBorderSize - boxPaddingTop) * screenSclY;
        for (Node child : children) {
            float child_height = child.calculateHeight() * screenSclY;
            child.offsetX = boxPaddingLeft - (boxPaddingLeft + boxPaddingRight) * 0.5f;
            child.offsetY = position_y - child_height * 0.5f;
            position_y -= child_height + margin * screenSclY;
        }
    }

    @Override
    protected float getContentHeight(final Array<Node> children) {
        float height = 0;
        for (Node child : children) {
            height += child.calculateHeight();
        }
        height += Math.max(0f, margin * (children.size - 1));
        return height;
    }

}
