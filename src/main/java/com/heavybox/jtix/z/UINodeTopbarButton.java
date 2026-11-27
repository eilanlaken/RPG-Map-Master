package com.heavybox.jtix.z;

import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.TextureRegion;
import com.heavybox.jtix.userinterface.UIGraphics;
import com.heavybox.jtix.userinterface.UIGroup;
import com.heavybox.jtix.userinterface.UIText;

public class UINodeTopbarButton extends UIGroup {

    public UINodeTopbarButton(final TextureRegion region, final String text) {
        setLayoutHorizontal();
        useScrollbar = false;
        width = 110;
        childSpacingHorizontal = 12;
        colorBackground = Color.valueOf("#2B2D30");
        widthFitContent = false;
        heightFitContent = true;

        childAdd(new UIGraphics(region));

        UIText nodeText = new UIText(text);
        nodeText.size = 14;
        childAdd(nodeText);

        // do the onclick and on mouse enter / leave etc.

        onMouseEnter(e -> {
            colorBackground = Color.valueOf("#43454A");
        });

        onMouseLeave(e -> {
            colorBackground = Color.valueOf("#2B2D30");
        });
    }

}
