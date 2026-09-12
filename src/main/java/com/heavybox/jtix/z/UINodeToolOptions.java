package com.heavybox.jtix.z;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.userinterface.Anchor;
import com.heavybox.jtix.userinterface.Node;
import com.heavybox.jtix.userinterface.NodeGroup;

public class UINodeToolOptions extends NodeGroup {

    private final Array<Node> allTools = new Array<>(true, 9);

    private final UINodeToolOptions_1_Terrain options_1_terrain;
    private final UINodeToolOptions_2_Nature  options_2_nature;

    public UINodeToolOptions() {
        setLayoutDefault();
        heightFitContent = true;
        widthFitContent = true;
        colorBackground = Color.CLEAR_BLACK.clone();
        paddingBottom = 20;
        paddingTop = 20;
        paddingLeft = 0;
        paddingRight = 0;
        anchor = Anchor.PARENT_TOP_RIGHT;
        transform.x = -40;
        transform.y = -140;


        options_1_terrain = new UINodeToolOptions_1_Terrain();
        options_2_nature = new UINodeToolOptions_2_Nature();
        allTools.add(options_1_terrain);
        allTools.add(options_2_nature);
        childAdd(options_1_terrain);
        childAdd(options_2_nature);
    }

    public void setActive(int index) {
        for (int i = 0; i < allTools.size; i++) {
            allTools.get(i).active = i == index;
        }
    }

}
