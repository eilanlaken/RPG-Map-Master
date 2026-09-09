package com.heavybox.jtix.z;

import com.heavybox.jtix.assets.Assets;
import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.TexturePack;
import com.heavybox.jtix.graphics.TextureRegion;
import com.heavybox.jtix.userinterface.Anchor;
import com.heavybox.jtix.userinterface.NodeGraphics;
import com.heavybox.jtix.userinterface.NodeGroup;
import com.heavybox.jtix.userinterface.NodeText;

public class UINodeToolbarButton extends NodeGroup {


    public UINodeToolbarButton(final TextureRegion region, final String text, final String hotkeyText) {
        setLayoutHorizontal();
        width = 250;
        childSpacingHorizontal = 15;
        colorBackground = Color.RED.clone();
        widthFitContent = false;
        heightFitContent = true;
        childAdd(new NodeGraphics(region));
        childAdd(new NodeText(text));

        NodeText hotkey = new NodeText(hotkeyText);
        hotkey.anchor = Anchor.PARENT_CENTER_RIGHT;
        childAdd(hotkey);
        // do the onclick and on mouse enter / leave etc.
    }

}
