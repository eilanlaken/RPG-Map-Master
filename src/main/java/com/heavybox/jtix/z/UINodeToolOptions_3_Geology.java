package com.heavybox.jtix.z;

import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.userinterface.UIGraphics;
import com.heavybox.jtix.userinterface.UIGroup;
import com.heavybox.jtix.userinterface.UIText;

public class UINodeToolOptions_3_Geology extends UIGroup {

    public UINodeToolOptions_3_Geology() {
        setLayoutVertical();
        useScrollbar = true;
        active = false; // deactivated by default
        width = 400;
        widthFitContent = false;
        heightFitContent = true;

        // TITLE
        UIGraphics titleBackground = new UIGraphics(360, 30);
        titleBackground.color = Color.valueOf("0075FF").toFloatBits();
        UIText titleTool = new UIText("Nature Tool");
        titleTool.size = 21;
        titleBackground.childAdd(titleTool);
        childAdd(titleBackground);
    }

}
