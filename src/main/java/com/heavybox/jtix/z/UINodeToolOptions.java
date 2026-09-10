package com.heavybox.jtix.z;

import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.userinterface.Anchor;
import com.heavybox.jtix.userinterface.NodeGroup;

public class UINodeToolOptions extends NodeGroup {

    private final UINodeToolOptions_1_Terrain options_1_terrain;

    public UINodeToolOptions() {
        setLayoutDefault();
        heightFitContent = true;
        widthFitContent = true;

        colorBackground = Color.CLEAR_BLACK.clone();
        paddingBottom = 0;
        paddingTop = 0;
        paddingLeft = 0;
        paddingRight = 0;

        anchor = Anchor.PARENT_TOP_RIGHT;
        transform.x = -40;
        transform.y = -140;

        options_1_terrain = new UINodeToolOptions_1_Terrain();

        childAdd(options_1_terrain);
    }

}
