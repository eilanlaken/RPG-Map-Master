package com.heavybox.jtix.z;

import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.widgets.NodeContainerHorizontal;
import com.heavybox.jtix.widgets.NodeText;

public class WidgetNodeTopMenuButton extends NodeContainerHorizontal {

    public static final int SIZE = 12;

    public WidgetNodeTopMenuButton(String name) {
        boxPaddingLeft = 4;
        boxPaddingRight = 4;
        boxPaddingTop = 1;
        boxPaddingBottom = 1;
        boxBackgroudColor = Color.valueOf("1D1D1D");
        boxWidthSizing = Sizing.DYNAMIC;
        boxHeightSizing = Sizing.DYNAMIC;
        boxBorderSize = 0;
        NodeText text = new NodeText(name);
        text.size = SIZE;
        addChild(text);
    }

}
