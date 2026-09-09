package com.heavybox.jtix.z;

import com.heavybox.jtix.widgets.WidgetNodeContainerHorizontal;
import com.heavybox.jtix.widgets.WidgetNodeText;
import com.heavybox.jtix.z.Tools;

@Deprecated public class WidgetNodeHelpBar extends WidgetNodeContainerHorizontal {

    private final WidgetNodeText helperText = new WidgetNodeText("i am a helper");

    public WidgetNodeHelpBar() {
        layoutOverflowX = Overflow.VISIBLE;
        layoutOverflowY = Overflow.VISIBLE;
        layoutAddScrollbar = false;
        layoutWidthSizing = Sizing.DYNAMIC;
        layoutHeightSizing = Sizing.DYNAMIC;
        boxBackgroundVisible = false;
        boxBorderSize = 0;
        boxPaddingTop = 0;
        boxPaddingBottom = 0;
        boxPaddingLeft = 0;
        boxPaddingRight = 0;
        boxChildSpacing = 5;
        anchor = Anchor.PARENT_TOP_CENTER;
        anchorX = 50;
        anchorY = 50;

        helperText.size = 12;
        addChild(helperText);
    }

    @Override
    public void fixedUpdateContainer(float delta) {
        helperText.text = Tools.getActiveTool().getHelperText();
    }

}
