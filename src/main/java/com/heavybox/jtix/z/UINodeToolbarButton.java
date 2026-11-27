package com.heavybox.jtix.z;

import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.TextureRegion;
import com.heavybox.jtix.userinterface.*;

public class UINodeToolbarButton extends UIGroup {

    public final int selectIndex;

    public UINodeToolbarButton(int index, final TextureRegion region, final String text, final String hotkeyText) {
        setLayoutHorizontal();
        width = 250;
        height = 54;
        childSpacingHorizontal = 15;
        colorBackground = Color.valueOf("#2B2D30");
        widthFitContent = false;
        heightFitContent = false;
        childAdd(new UIGraphics(region));
        childAdd(new UIText(text));

        UIText hotkey = new UIText(hotkeyText);
        hotkey.anchor = Anchor.PARENT_CENTER_RIGHT;
        hotkey.transform.x = -10;
        childAdd(hotkey);
        // do the onclick and on mouse enter / leave etc.

        this.selectIndex = index;

        onMouseEnter(e -> {
            UI parent = super.parent;
            UINodeToolbar toolbar = (UINodeToolbar) parent;
            if (toolbar.selected == selectIndex) return;
            colorBackground = Color.valueOf("#43454A");
        });

        onMouseLeave(e -> {
            UI parent = super.parent;
            UINodeToolbar toolbar = (UINodeToolbar) parent;
            if (toolbar.selected == selectIndex) return;
            colorBackground = Color.valueOf("#2B2D30");
        });

        onMouseClick(e -> {
            UI parent = super.parent;
            UINodeToolbar toolbar = (UINodeToolbar) parent;
            if (toolbar.selected == selectIndex) return;

            toolbar.selectTool(selectIndex);
        });

    }

}
