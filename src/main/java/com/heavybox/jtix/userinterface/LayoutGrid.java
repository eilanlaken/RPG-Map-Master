package com.heavybox.jtix.userinterface;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.math.Transform2D;

public class LayoutGrid implements Layout {

    public boolean fillRows    = true;
    public int     rows        = 4;
    public int     cols        = 6;
    public float   rowsOffset  = 0; // main axis offset
    public float   colsOffset  = 0; // secondary axis offset
    public float   rowsSpacing = 40;
    public float   colsSpacing = 20;
    public boolean uniformGrid = true;

    // TODO: maybe centralize grid cells

    @Override
    public void setChildTransformOffset(Node parent, Array<Node> childrenLayout, Array<Transform2D> out) {
        if (fillRows) fillRows(parent, childrenLayout, out);
        else fillCols(parent, childrenLayout, out);
    }

    private void fillRows(final Node parent, Array<Node> children, Array<Transform2D> out) {
        if (children.isEmpty()) return;

        // calculate cell dimensions
        float cellWidth = 0;
        float cellHeight = 0;
        for (Node child : children) {
            cellWidth = Math.max(cellWidth, child.getWidth());
            cellHeight = Math.max(cellHeight, child.getHeight());
        }

        final float parentWidth = parent.getWidth();
        final float parentHeight = parent.getHeight();

        float position_x = -parentWidth * 0.5f + colsOffset + (uniformGrid ? cellWidth * 0.5f : children.first().getWidth() * 0.5f);
        float position_y = parentHeight * 0.5f + rowsOffset;
        for (int i = 0; i < children.size; i++) {
            Node child = children.get(i);
            Transform2D offset = out.get(i).idt(); // get and reset
            offset.x = position_x;
            offset.y = position_y - cellHeight * 0.5f;
            position_x += (uniformGrid ? cellWidth : child.getWidth()) + colsSpacing;
            if ((i + 1) % cols == 0) {
                position_x = -parentWidth * 0.5f + colsOffset + (uniformGrid ? cellWidth * 0.5f : children.first().getWidth() * 0.5f);
                position_y -= cellHeight + rowsSpacing;
            }
        }

    }

    private void fillCols(final Node parent, Array<Node> children, Array<Transform2D> out) {
        // TODO
    }

}
