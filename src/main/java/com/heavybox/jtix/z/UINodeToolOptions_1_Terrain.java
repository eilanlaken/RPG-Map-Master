package com.heavybox.jtix.z;

import com.heavybox.jtix.userinterface.NodeGroup;
import com.heavybox.jtix.userinterface.NodeText;

public class UINodeToolOptions_1_Terrain extends NodeGroup {

    public UINodeToolOptions_1_Terrain() {
        setLayoutVertical();
        useScrollbar = true;
        active = true; // deactivated by default
        width = 300;
        widthFitContent = false;
        heightFitContent = true;

        childAdd(new NodeText("Terrain Tool"));


    }

}
