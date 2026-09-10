package com.heavybox.jtix.z;

import com.heavybox.jtix.assets.Assets;
import com.heavybox.jtix.graphics.TexturePack;
import com.heavybox.jtix.userinterface.Anchor;
import com.heavybox.jtix.userinterface.NodeGroup;

public class UINodeTopbar extends NodeGroup {

    // buttons
    private final UINodeTopbarButton btnNew;
    private final UINodeTopbarButton open;
    private final UINodeTopbarButton save;
    private final UINodeTopbarButton saveAs;
    private final UINodeTopbarButton export;
    private final UINodeTopbarButton undo;
    private final UINodeTopbarButton redo;
    private final UINodeTopbarButton settings;
    private final UINodeTopbarButton help;

    public UINodeTopbar() {
        setLayoutHorizontal();

        this.heightFitContent = true;
        this.widthFitContent = true;
        this.anchor = Anchor.PARENT_TOP_CENTER;
        this.transform.y = -40;

        TexturePack uiIconsPack = Assets.get("assets/texture-packs/user-interface.yml");

        btnNew = new UINodeTopbarButton(uiIconsPack.getRegion("assets/user-interface/topbar-icon-new-map.png"), "New Map");
        open = new UINodeTopbarButton(uiIconsPack.getRegion("assets/user-interface/topbar-icon-open.png"), "Open");
        save = new UINodeTopbarButton(uiIconsPack.getRegion("assets/user-interface/topbar-icon-save.png"), "Save");
        saveAs = new UINodeTopbarButton(uiIconsPack.getRegion("assets/user-interface/topbar-icon-save-as.png"), "Save As");
        export = new UINodeTopbarButton(uiIconsPack.getRegion("assets/user-interface/topbar-icon-export.png"), "Export");
        undo = new UINodeTopbarButton(uiIconsPack.getRegion("assets/user-interface/topbar-icon-undo.png"), "Undo");
        redo = new UINodeTopbarButton(uiIconsPack.getRegion("assets/user-interface/topbar-icon-redo.png"), "Redo");
        settings = new UINodeTopbarButton(uiIconsPack.getRegion("assets/user-interface/topbar-icon-settings.png"), "Settings");
        help = new UINodeTopbarButton(uiIconsPack.getRegion("assets/user-interface/topbar-icon-help.png"), "Help");

        childAdd(btnNew);
        childAdd(open);
        childAdd(save);
        childAdd(saveAs);
        childAdd(export);
        childAdd(undo);
        childAdd(redo);
        childAdd(settings);
        childAdd(help);
    }

}
