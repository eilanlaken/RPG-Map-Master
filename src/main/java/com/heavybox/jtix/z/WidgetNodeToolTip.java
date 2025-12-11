package com.heavybox.jtix.z;

import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.widgets_4.WidgetNodeContainerStack;
import com.heavybox.jtix.widgets_4.WidgetNodeText;

public class WidgetNodeToolTip extends WidgetNodeContainerStack {

    public final WidgetNodeText tip = new WidgetNodeText("This is a tool tip");

    public WidgetNodeToolTip() {
        boxBorderSize = 0;
        boxBackgroundVisible = true;
        boxBackgroundColor = Color.BLACK.clone();
        widthSizing = Sizing.DYNAMIC;
        heightSizing = Sizing.DYNAMIC;
        boxPaddingTop = 8;
        boxPaddingBottom = 8;
        boxPaddingLeft = 8;
        boxPaddingRight = 8;
        anchor = Anchor.CURSOR_TOP_LEFT;
        anchorX = 20;
        addChild(tip);
    }

}
