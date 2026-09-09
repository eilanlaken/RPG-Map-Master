package com.heavybox.jtix.z;

import com.heavybox.jtix.assets.Assets;
import com.heavybox.jtix.graphics.TexturePack;
import com.heavybox.jtix.userinterface.NodeGroup;

public class UINodeToolbar extends NodeGroup {

    // buttons
    UINodeToolbarButton terrain;

    public UINodeToolbar() {
        this.heightFitContent = true;
        this.widthFitContent = false;
        this.width = 300;
        setLayoutVertical();

        TexturePack uiIconsPack = Assets.get("assets/texture-packs/user-interface.yml");
        terrain = new UINodeToolbarButton(uiIconsPack.getRegion("assets/user-interface/toolbar-icon-terrain.png"), "Terrain", "1");
        childAdd(terrain);
    }

}
