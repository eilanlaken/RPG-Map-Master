package com.heavybox.jtix.widgets_2;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.widgets.NodeContainer;
import com.heavybox.jtix.widgets_2.WidgetsException;

/*
Follows CSS' box model, more or less -

draw():
 -----------------------------------------------------------
|                           border                         |
|          --------------------------------------          |
|         |               padding top            |         |
|         |          p0----------------p1        |         |
|         |  padding  |                | padding |         |
| border  |   left    |                |  right  |  border |
|         |           |    content     |         |         |
|         |           |    render()    |         |         |
|         |           |                |         |         |
|         |          p3----------------p2        |         |
|         |             padding bottom           |         |
|          --------------------------------------          |
|                           border                         |
 -----------------------------------------------------------

 */
public abstract class Widget {

    /* hierarchy properties */
    protected Widget parent = null;
    protected Array<Widget> children = new Array<>(true, 1);

    /* positioning */
    protected Layout layout = Layout.STACK;
    protected float offsetX = 0; // set by parent
    protected float offsetY = 0; // set by parent
    protected Transform localTransform = new Transform();
    protected Transform worldTransform = new Transform(); // calculated.

    /* box properties */
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

    public final void addChild(Widget widget) {
        if (widget == null) throw new WidgetsException(Widget.class.getSimpleName() + " element cannot be null.");
        if (widget == this) throw new WidgetsException("Trying to parent a " + Widget.class.getSimpleName() + " to itself.");
        if (widget.parent != null) widget.parent.removeChild(widget);

        children.add(widget);
        widget.parent = this;
    }

    public final void removeChild(Widget widget) {
        if (widget == null) throw new WidgetsException(Widget.class.getSimpleName() + " element cannot be null.");
        if (!children.contains(widget, true)) throw new WidgetsException(Widget.class.getSimpleName() + " does not contain the element " + widget + " as a child so it cannot be removed.");

        children.removeValue(widget,true);
        widget.parent = null;
    }

    public enum Layout {

        STACK,
        VERTICAL,
        HORIZONTAL,
        ;

    }

    public enum Sizing {

        STATIC,  // Hard-coded value in pixels. The size remains constant even if content overflows or fits with extra space.
        DYNAMIC, // The widget box will set its size to completely fit its children.
        ;

    }

    /* controls how it renders the contents of the widget that overflow the box */
    public enum Overflow {
        VISIBLE,    // does nothing, renders while ignoring the bounds
        HIDDEN,      // uses glScissors to clip the content, so only the pixels that land inside the box render. The rest get trimmed.
        SCROLLBAR, // trims the content and adds scrollbars
    }

    // anchors ensure spacing between a widget and its parent's edges.
    // when the parent is null, it's the window edges.
    // this is important to make the ui responsive.
    public enum Anchor {
        TOP_LEFT,    TOP_CENTER,    TOP_RIGHT,
        CENTER_LEFT, CENTER_CENTER, CENTER_RIGHT,
        BOTTOM_LEFT, BOTTOM_CENTER, BOTTOM_RIGHT,
        ;
    }

}
