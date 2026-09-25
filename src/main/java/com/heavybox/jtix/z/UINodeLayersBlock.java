package com.heavybox.jtix.z;

import com.heavybox.jtix.assets.Assets;
import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Texture;
import com.heavybox.jtix.userinterface.Anchor;
import com.heavybox.jtix.userinterface.NodeGraphics;
import com.heavybox.jtix.userinterface.NodeGroup;
import com.heavybox.jtix.userinterface.NodeText;

public class UINodeLayersBlock extends NodeGroup {

    private int index;

    private NodeText textIndex;
    private NodeGraphics selectionIndicator;
    private NodeGraphics layerImage;

    public UINodeLayersBlock(int index, Color color) {
        setLayoutDefault();
        width = 90;
        height = 100;
        widthFitContent = false;
        heightFitContent = false;
        hideOverflow = true;
        paddingBottom = 0;
        paddingLeft = 0;
        paddingRight = 0;
        paddingTop = 0;

        this.selectionIndicator = new NodeGraphics(90,90);
        this.selectionIndicator.color = Color.CLEAR_WHITE.toFloatBits();
        this.selectionIndicator.color = Color.valueOf("0075FF").toFloatBits();
        this.layerImage = new NodeGraphics(75,75);
        Texture image = Assets.get("assets/textures-layer-0/terrain_land_grass_0.jpg");
        this.layerImage.color = color.toFloatBits();
        this.layerImage.setImage(image);

        this.index = index;
        this.textIndex = new NodeText("" + index);
        this.textIndex.anchor = Anchor.PARENT_TOP_LEFT;
        this.textIndex.transform.x = 16;
        this.textIndex.transform.y = -18;

        childAdd(selectionIndicator);
        childAdd(layerImage);
        childAdd(textIndex);
    }

    public void unselect() {
        selectionIndicator.color = Color.valueOf("#43454A").toFloatBits();
    }

}
