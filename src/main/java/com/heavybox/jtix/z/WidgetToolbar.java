package com.heavybox.jtix.z;

import com.heavybox.jtix.widgets_4.WidgetContainer;

public class WidgetToolbar extends WidgetContainer {

    public WidgetToolbar() {
        layout = Layout.VERTICAL;
        layoutAddScrollbar = false;
        layoutWidthSizing = Sizing.DYNAMIC;
        layoutHeightSizing = Sizing.DYNAMIC;
        layoutOverflowX = Overflow.VISIBLE;
        layoutOverflowY = Overflow.VISIBLE;

        boxBackgroundVisible = false;
        boxBorderSize = 0;
        boxPaddingTop = 0;
        boxPaddingBottom = 0;
        boxPaddingLeft = 0;
        boxPaddingRight = 0;

    }

    @Override
    protected void fixedUpdateContainer(float delta) {

    }
}
