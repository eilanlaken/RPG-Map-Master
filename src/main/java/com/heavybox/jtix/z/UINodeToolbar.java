package com.heavybox.jtix.z;

import com.heavybox.jtix.assets.Assets;
import com.heavybox.jtix.graphics.TexturePack;
import com.heavybox.jtix.userinterface.Anchor;
import com.heavybox.jtix.userinterface.NodeGroup;

public class UINodeToolbar extends NodeGroup {

    // buttons
    private final UINodeToolbarButton terrain;
    private final UINodeToolbarButton nature;
    private final UINodeToolbarButton geology;
    private final UINodeToolbarButton props;
    private final UINodeToolbarButton architecture;
    private final UINodeToolbarButton decorations;
    private final UINodeToolbarButton text;
    private final UINodeToolbarButton procedural;
    private final UINodeToolbarButton select;


    public UINodeToolbar() {
        setLayoutVertical();

        this.heightFitContent = true;
        this.widthFitContent = true;
        this.width = 300;
        this.anchor = Anchor.PARENT_TOP_LEFT;
        this.transform.x = 40;
        this.transform.y = -140;

        TexturePack uiIconsPack = Assets.get("assets/texture-packs/user-interface.yml");

        terrain = new UINodeToolbarButton(uiIconsPack.getRegion("assets/user-interface/toolbar-icon-terrain.png"), "Terrain", "1");
        nature = new UINodeToolbarButton(uiIconsPack.getRegion("assets/user-interface/toolbar-icon-nature.png"), "Nature", "2");
        geology = new UINodeToolbarButton(uiIconsPack.getRegion("assets/user-interface/toolbar-icon-geology.png"), "Geology", "3");
        props = new UINodeToolbarButton(uiIconsPack.getRegion("assets/user-interface/toolbar-icon-props.png"), "Props", "4");
        architecture = new UINodeToolbarButton(uiIconsPack.getRegion("assets/user-interface/toolbar-icon-architecture.png"), "Architecture", "5");
        decorations = new UINodeToolbarButton(uiIconsPack.getRegion("assets/user-interface/toolbar-icon-decorations.png"), "Decorations", "6");
        text = new UINodeToolbarButton(uiIconsPack.getRegion("assets/user-interface/toolbar-icon-text.png"), "Text", "7");
        procedural = new UINodeToolbarButton(uiIconsPack.getRegion("assets/user-interface/toolbar-icon-procedural.png"), "Procedural", "8");
        select = new UINodeToolbarButton(uiIconsPack.getRegion("assets/user-interface/toolbar-icon-select.png"), "Select", "9");


        childAdd(terrain);
        childAdd(nature);
        childAdd(geology);
        childAdd(props);
        childAdd(architecture);
        childAdd(decorations);
        childAdd(text);
        childAdd(procedural);
        childAdd(select);
    }

}
