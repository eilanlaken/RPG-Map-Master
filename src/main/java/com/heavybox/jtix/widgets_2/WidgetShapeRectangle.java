package com.heavybox.jtix.widgets_2;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.math.Transform2D;

public final class WidgetShapeRectangle extends Widget implements WidgetShape {

    public Widget parent = null;
    public int inputLayerIndex = 1;
    public boolean active = true;
    public Array<Widget> children = null;
    public Transform2D transformLocal = new Transform2D();
    public Transform2D transformGlobal = new Transform2D(); // calculated

    @Override
    public int getInputLayer() {
        if (parent == null) return inputLayerIndex;
        return parent.getInputLayer() + inputLayerIndex;
    }

    @Override
    public boolean isActive() {
        return true;
    }

}
