package com.heavybox.jtix.widgets_3;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.collections.Tuple2;
import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.math.Vector2;

/*
Roughly follows CSS' box model -

draw():
 -----------------------------------------------------------
|                           border                         |
|          --------------------------------------          |
|         |               padding top            |         |
|         |          p0----------------p1        |         |
|         |  padding  |                | padding |         |
| border  |   left    |                |  right  |  border |
|         |           |    render      |         |         |
|         |           |   children()   |         |         |
|         |           |                |         |         |
|         |          p3----------------p2        |         |
|         |             padding bottom           |         |
|          --------------------------------------          |
|                           border                         |
 -----------------------------------------------------------

 */
public class NodeContainer extends Node {

    /* state */
    private final Array<Node>    children        = new Array<>(true, 1);
    private       float          scrollOffsetX   = 0;
    private       float          scrollOffsetY   = 0;

    /* box properties */
    public Layout    boxLayout                    = Layout.STACK;
    public Sizing    boxWidthSizing               = Sizing.DYNAMIC;
    public float     boxWidthMin                  = 0;
    public float     boxWidthMax                  = Float.POSITIVE_INFINITY;
    public float     boxWidth                     = 1;
    public Sizing    boxHeightSizing              = Sizing.DYNAMIC;
    public float     boxHeightMin                 = 0;
    public float     boxHeightMax                 = Float.POSITIVE_INFINITY;
    public float     boxHeight                    = 1;
    public Overflow  boxContentOverflowX          = Overflow.HIDDEN;
    public Overflow  boxContentOverflowY          = Overflow.HIDDEN;
    public Color     boxBackgroudColor            = Color.valueOf("#007BFF");
    public boolean   boxBackgroundEnabled         = true;
    public int       boxPaddingTop                = 80;
    public int       boxPaddingBottom             = 20;
    public int       boxPaddingLeft               = 0;
    public int       boxPaddingRight              = 0;
    public int       boxCornerRadiusTopLeft       = 0;
    public int       boxCornerRadiusTopRight      = 0;
    public int       boxCornerRadiusBottomRight   = 0;
    public int       boxCornerRadiusBottomLeft    = 0;
    public int       boxCornerSegmentsTopLeft     = 10;
    public int       boxCornerSegmentsTopRight    = 10;
    public int       boxCornerSegmentsBottomRight = 10;
    public int       boxCornerSegmentsBottomLeft  = 10;
    public int       boxBorderSize                = 8;
    public Color     boxBorderColor               = Color.RED.clone();

    /*** HIERARCHY OPERATIONS ***/

    public final void addChild(Node node) {
        if (node == null) throw new WidgetsException(Widget.class.getSimpleName() + " element cannot be null.");
        if (node == this) throw new WidgetsException("Trying to parent a " + Widget.class.getSimpleName() + " to itself.");
        if (node.container != null) node.container.removeChild(node);

        children.add(node);
        node.container = this;
    }

    public final void removeChild(Node node) {
        if (node == null) throw new WidgetsException(Widget.class.getSimpleName() + " element cannot be null.");
        if (!children.contains(node, true)) throw new WidgetsException(Widget.class.getSimpleName() + " does not contain the element " + node + " as a child so it cannot be removed.");

        int index = children.removeValue(node,true);
        node.container = null;
    }

    @Override
    protected void fixedUpdate(float delta) {

    }

    @Override
    protected void frameUpdate(float delta) {

    }

    /*** UPDATE ***/


    protected final void setChildrenGlobalTransform() {

    }

    public final void setChildrenOffsets() {
        if (boxLayout == Layout.STACK) {

            return;
        }

        if (boxLayout == Layout.VERTICAL) {

            return;
        }

        if (boxLayout == Layout.HORIZONTAL) {

            return;
        }

        if (boxLayout == Layout.CUSTOM) {

            return;
        }
    }

    // can override to create custom layout positioning.
    protected void setChildrenOffsets(final Array<Node> offsets) {

    }

    /*** RENDERING ***/

    // TODO
    @Override
    protected void render(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY) {

    }

    @Override
    public float getWidth() {
        return 0;
    }

    @Override
    public float getHeight() {
        return 0;
    }

    /*** SUPPORTING ENUMS ***/

    // controls the children's layout
    public enum Layout {
        STACK, // stack on top of each-other at the center
        VERTICAL, // place elements from top to bottom, while taking box model into account (padding, border, ...)
        HORIZONTAL, // place elements from left to right, while taking box model into account (padding, border, ...)
        CUSTOM
        ;
    }

    // controls the box sizing
    public enum Sizing {
        STATIC  ,  // Hard-coded value in pixels. The size remains constant even if content overflows or fits with extra space.
        DYNAMIC , // The widget box will set its size to completely fit its children.
        VIEWPORT, // the node box size will always size itself according to the viewport. For example, if the window width is 100 and the width is 82 -> 82 final width in pixels
        ;
    }

    // controls how it handles overflow children.
    public enum Overflow {
        VISIBLE  ,   // does nothing, renders while ignoring the bounds
        HIDDEN   ,    // uses glScissors to clip the content, so only the pixels that land inside the box render. The rest get trimmed.
        SCROLLBAR, // trims the content and adds scrollbars
        ;
    }

}
