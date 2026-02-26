package com.heavybox.jtix.z;

import com.heavybox.jtix.assets.Assets;
import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.TexturePack;
import com.heavybox.jtix.widgets.WidgetNodeContainerHorizontal;
import com.heavybox.jtix.widgets.WidgetNodeImage;

public class WidgetNodeActionsBar extends WidgetNodeContainerHorizontal {

    public static final int BUTTON_SIZE = 44;

    public WidgetNodeToolTip toolTip = new WidgetNodeToolTip();

    // probably get a reference to the scene or map or whatever.
    public WidgetNodeActionsBar() {
        layoutOverflowX = Overflow.VISIBLE;
        layoutOverflowY = Overflow.VISIBLE;
        layoutAddScrollbar = false;
        layoutWidthSizing = Sizing.DYNAMIC;
        layoutHeightSizing = Sizing.DYNAMIC;
        boxBorderSize = 3;
        boxBorderColor = Color.valueOf("e7b524");
        boxBackgroundVisible = true;
        boxBackgroundColor = Color.valueOf("0f0e0a");
        boxChildSpacing = 5;
        boxPaddingTop = 0;
        boxPaddingBottom = 0;
        boxPaddingLeft = 0;
        boxPaddingRight = 0;

        anchor = Anchor.PARENT_TOP_LEFT;
        anchorX = 50;
        anchorY = 50;

        toolTip.anchor = Anchor.CURSOR_TOP_LEFT;
        toolTip.anchorX = 20;

        addChildren();
    }

    private void addChildren() {
        TexturePack ui = Assets.get("assets/texture-packs/user-interface.yml");

        WidgetNodeImage actionBarNew = new WidgetNodeImage(ui.getRegion("assets/user-interface/action-bar-new.png"));
        actionBarNew.boxBackgroundVisible = true;
        actionBarNew.boxBackgroundColor = Color.valueOf("101010");
        actionBarNew.boxBorderSize = 0;
        actionBarNew.imgWidth = BUTTON_SIZE;
        actionBarNew.imgHeight = BUTTON_SIZE;
        actionBarNew.onMouseEnter = e -> {
            actionBarNew.boxBackgroundColor = Color.valueOf("2a2a2a");
            toolTip.tip.text = "Create New Project...";
        };
        actionBarNew.onMouseLeave = e -> {
            actionBarNew.boxBackgroundColor = Color.valueOf("101010");
        };

        WidgetNodeImage actionBarLoad = new WidgetNodeImage(ui.getRegion("assets/user-interface/action-bar-load.png"));
        actionBarLoad.boxBackgroundVisible = true;
        actionBarLoad.boxBackgroundColor = Color.valueOf("101010");
        actionBarLoad.boxBorderSize = 0;
        actionBarLoad.imgWidth = BUTTON_SIZE;
        actionBarLoad.imgHeight = BUTTON_SIZE;
        actionBarLoad.onMouseLeftClick = e -> {

        };
        actionBarLoad.onMouseEnter = e -> {
            actionBarLoad.boxBackgroundColor = Color.valueOf("2a2a2a");
            toolTip.tip.text = "Load Project...";
        };
        actionBarLoad.onMouseLeave = e -> {
            actionBarLoad.boxBackgroundColor = Color.valueOf("101010");
        };

        WidgetNodeImage actionBarSaveAs = new WidgetNodeImage(ui.getRegion("assets/user-interface/action-bar-save-as.png"));
        actionBarSaveAs.boxBackgroundVisible = true;
        actionBarSaveAs.boxBackgroundColor = Color.valueOf("101010");
        actionBarSaveAs.boxBorderSize = 0;
        actionBarSaveAs.imgWidth = BUTTON_SIZE;
        actionBarSaveAs.imgHeight = BUTTON_SIZE;
        actionBarSaveAs.onMouseEnter = e -> {
            actionBarSaveAs.boxBackgroundColor = Color.valueOf("2a2a2a");
            toolTip.tip.text = "Save As...";
        };
        actionBarSaveAs.onMouseLeave = e -> {
            actionBarSaveAs.boxBackgroundColor = Color.valueOf("101010");
        };

        WidgetNodeImage actionBarExport = new WidgetNodeImage(ui.getRegion("assets/user-interface/action-bar-export.png"));
        actionBarExport.boxBackgroundVisible = true;
        actionBarExport.boxBackgroundColor = Color.valueOf("101010");
        actionBarExport.boxBorderSize = 0;
        actionBarExport.imgWidth = BUTTON_SIZE;
        actionBarExport.imgHeight = BUTTON_SIZE;
        actionBarExport.onMouseEnter = e -> {
            actionBarExport.boxBackgroundColor = Color.valueOf("2a2a2a");
            toolTip.tip.text = "Export Map...";
        };
        actionBarExport.onMouseLeave = e -> {
            actionBarExport.boxBackgroundColor = Color.valueOf("101010");
        };

        WidgetNodeImage actionBarUndo = new WidgetNodeImage(ui.getRegion("assets/user-interface/action-bar-undo.png"));
        actionBarUndo.boxBackgroundVisible = true;
        actionBarUndo.boxBackgroundColor = Color.valueOf("101010");
        actionBarUndo.boxBorderSize = 0;
        actionBarUndo.imgWidth = BUTTON_SIZE;
        actionBarUndo.imgHeight = BUTTON_SIZE;
        actionBarUndo.onMouseLeftClick = e -> {
        };
        actionBarUndo.onMouseEnter = e -> {
            actionBarUndo.boxBackgroundColor = Color.valueOf("2a2a2a");
            toolTip.tip.text = "Undo";
        };
        actionBarUndo.onMouseLeave = e -> {
            actionBarUndo.boxBackgroundColor = Color.valueOf("101010");
        };

        WidgetNodeImage actionBarRedo = new WidgetNodeImage(ui.getRegion("assets/user-interface/action-bar-redo.png"));
        actionBarRedo.boxBackgroundVisible = true;
        actionBarRedo.boxBackgroundColor = Color.valueOf("101010");
        actionBarRedo.boxBorderSize = 0;
        actionBarRedo.imgWidth = BUTTON_SIZE;
        actionBarRedo.imgHeight = BUTTON_SIZE;
        actionBarRedo.onMouseLeftClick = e -> {
        };
        actionBarRedo.onMouseEnter = e -> {
            actionBarRedo.boxBackgroundColor = Color.valueOf("2a2a2a");
            toolTip.tip.text = "Redo";
        };
        actionBarRedo.onMouseLeave = e -> {
            actionBarRedo.boxBackgroundColor = Color.valueOf("101010");
        };



        addChild(actionBarNew);
        addChild(actionBarLoad);
        addChild(actionBarSaveAs);
        addChild(actionBarExport);
        addChild(actionBarUndo);
        addChild(actionBarRedo);

        // tooltip
        addChild(toolTip);
        toolTip.hidden = true;
        //toolTip.text = "hello";

        onMouseEnter = e -> {
            toolTip.hidden = false;

        };
        onMouseLeave = e -> {
            toolTip.hidden = true;

        };
    }

}
