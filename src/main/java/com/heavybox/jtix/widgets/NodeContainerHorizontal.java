package com.heavybox.jtix.widgets;

import com.heavybox.jtix.collections.Array;

public class NodeContainerHorizontal extends NodeContainer {

    public float margin = 5;

    public NodeContainerHorizontal() {
        contentOverflowX = Overflow.SCROLLBAR;
        contentOverflowY = Overflow.HIDDEN;
    }

    @Override
    protected void setChildrenOffset(final Array<Node> children) {
        float position_x = -(calculateWidth() * 0.5f - boxBorderSize - boxPaddingLeft) * screenSclX;
        for (Node child : children) {
            float child_width = child.calculateWidth() * screenSclX;
            child.offsetX = position_x + child_width * 0.5f;
            child.offsetY = boxPaddingBottom - (boxPaddingBottom + boxPaddingTop) * 0.5f;
            position_x += child_width + margin * screenSclX;
        }
    }

    @Override
    protected float getContentWidth(final Array<Node> children) {
        float width = 0;
        for (Node child : children) {
            width += child.calculateWidth();
        }
        width += Math.max(0f, margin * (children.size - 1));
        return width;
    }

}
