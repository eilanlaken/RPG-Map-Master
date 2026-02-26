package com.heavybox.jtix.widgets;

import com.heavybox.jtix.collections.Array;

public interface WidgetNodeContainer {

    default void fixedUpdateContainer(float delta) {}

    default float getContentHeight(final Array<WidgetNode> widgets) {
        if (widgets == null || widgets.isEmpty()) return 0;

        float min_y = Float.POSITIVE_INFINITY;
        float max_y = Float.NEGATIVE_INFINITY;
        for (WidgetNode node : widgets) {
            float down = node.offsetY - node.getHeight() * 0.5f;
            float up = node.offsetY + node.getHeight() * 0.5f;
            min_y = Math.min(min_y, down);
            max_y = Math.max(max_y, up);
        }
        return Math.abs(max_y - min_y);
    }

    default float getContentWidth(final Array<WidgetNode> widgets) {
        if (widgets == null || widgets.isEmpty()) return 0;

        float min_x = Float.POSITIVE_INFINITY;
        float max_x = Float.NEGATIVE_INFINITY;
        for (WidgetNode node : widgets) {
            float left = node.offsetX - node.getWidth() * 0.5f;
            float right = node.offsetX + node.getWidth() * 0.5f;
            min_x = Math.min(min_x, left);
            max_x = Math.max(max_x, right);
        }
        return Math.abs(max_x - min_x);
    }

    /*** SUPPORTING ENUMS ***/
    // controls the box sizing
    enum Sizing {
        STATIC  ,  // Hard-coded value in pixels. The size remains constant even if content overflows or fits with extra space.
        DYNAMIC , // The widget box will set its size to completely fit its children.
        VIEWPORT, // the node box size will always size itself according to the viewport. For example, if the window width is 100 and the width is 0.82 -> 82 final width in pixels
        ;
    }

    // controls how it handles overflow children.
    enum Overflow {
        VISIBLE  ,   // does nothing, renders while ignoring the bounds
        HIDDEN   ,    // uses glScissors to clip the content, so only the pixels that land inside the box render. The rest get trimmed.
        ;
    }

}
