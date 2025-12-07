package com.heavybox.jtix.z;

import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.widgets_4.WidgetContainer;

public class WidgetToolSettings extends WidgetContainer {

    public WidgetToolSettings() {
        layout = Layout.VERTICAL;
        layoutWidthSizing = Sizing.STATIC;
        layoutHeightSizing = Sizing.STATIC;
        layoutOverflowX = Overflow.VISIBLE;
        layoutOverflowY = Overflow.HIDDEN;
        layoutAddScrollbar = true;
        layoutWidth = 250;
        layoutHeight = 650;

        boxBorderSize = 3;
        boxBorderColor = Color.valueOf("e7b524");
        boxBackgroundVisible = true;
        boxBackgroundColor = Color.valueOf("0f0e0a");
        boxChildSpacingVertical = 5;
        boxPaddingTop = 2;
        boxPaddingBottom = 2;
        boxPaddingLeft = 2;
        boxPaddingRight = 2;

        anchor = Anchor.TOP_LEFT;
        anchorX = 42;
        anchorY = 200;

        addChildren();
    }

    private void addChildren() {

    }

}
