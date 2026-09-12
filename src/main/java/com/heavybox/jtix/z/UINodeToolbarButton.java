package com.heavybox.jtix.z;

import com.heavybox.jtix.assets.Assets;
import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.TexturePack;
import com.heavybox.jtix.graphics.TextureRegion;
import com.heavybox.jtix.userinterface.*;

public class UINodeToolbarButton extends NodeGroup {

    public final int selectIndex;

    public UINodeToolbarButton(int index, final TextureRegion region, final String text, final String hotkeyText) {
        setLayoutHorizontal();
        width = 250;
        height = 54;
        childSpacingHorizontal = 15;
        colorBackground = Color.valueOf("#2B2D30");
        widthFitContent = false;
        heightFitContent = false;
        childAdd(new NodeGraphics(region));
        childAdd(new NodeText(text));

        NodeText hotkey = new NodeText(hotkeyText);
        hotkey.anchor = Anchor.PARENT_CENTER_RIGHT;
        hotkey.transform.x = -10;
        childAdd(hotkey);
        // do the onclick and on mouse enter / leave etc.

        this.selectIndex = index;

        onMouseEnter(e -> {
            Node parent = super.parent;
            UINodeToolbar toolbar = (UINodeToolbar) parent;
            if (toolbar.selected == selectIndex) return;
            colorBackground = Color.valueOf("#43454A");
        });

        onMouseLeave(e -> {
            Node parent = super.parent;
            UINodeToolbar toolbar = (UINodeToolbar) parent;
            if (toolbar.selected == selectIndex) return;
            colorBackground = Color.valueOf("#2B2D30");
        });

        onMouseClick(e -> {
            Node parent = super.parent;
            UINodeToolbar toolbar = (UINodeToolbar) parent;
            if (toolbar.selected == selectIndex) return;

            toolbar.selectTool(selectIndex);
        });

    }

}
