package com.heavybox.jtix.z;

import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.userinterface.NodeGraphics;
import com.heavybox.jtix.userinterface.NodeGroup;
import com.heavybox.jtix.userinterface.NodeText;

public class UINodeToolOptions_2_Nature extends NodeGroup {

    public UINodeToolOptions_2_Nature() {
        setLayoutVertical();
        useScrollbar = true;
        active = true; // deactivated by default
        width = 400;
        widthFitContent = false;
        heightFitContent = true;

        // TITLE
        NodeGraphics titleBackground = new NodeGraphics(360, 30);
        titleBackground.color = Color.valueOf("0075FF").toFloatBits();
        NodeText titleTool = new NodeText("Nature Tool");
        titleTool.size = 21;
        titleBackground.childAdd(titleTool);
        childAdd(titleBackground);
    }

}
