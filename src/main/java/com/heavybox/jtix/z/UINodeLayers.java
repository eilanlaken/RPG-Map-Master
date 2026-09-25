package com.heavybox.jtix.z;

import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.userinterface.Anchor;
import com.heavybox.jtix.userinterface.NodeGraphics;
import com.heavybox.jtix.userinterface.NodeGroup;
import com.heavybox.jtix.userinterface.NodeText;

public class UINodeLayers extends NodeGroup {

    // state management

    public UINodeLayers() {
        setLayoutHorizontal();

        this.heightFitContent = true;
        this.widthFitContent = true;
        this.hideOverflow = false;
        this.anchor = Anchor.PARENT_BOTTOM_CENTER;
        this.transform.x = 0;
        this.transform.y = 30;

        UINodeLayersBlock block_0 = new UINodeLayersBlock(0, Color.WHITE);
        childAdd(block_0);

        UINodeLayersBlock block_1 = new UINodeLayersBlock(1, Color.CLEAR_WHITE);
        block_1.unselect();
        childAdd(block_1);

        NodeGraphics addLayerButton = new NodeGraphics(90,90);
        addLayerButton.color = Color.valueOf("#43454A").toFloatBits();
        NodeText plusSign = new NodeText("+");
        plusSign.size = 32;
        addLayerButton.childAdd(plusSign);
        childAdd(addLayerButton);

        NodeText title = new NodeText("Layers");
        title.size = 18;
        title.anchor = Anchor.PARENT_TOP_CENTER;
        title.transform.y = 25;
        childAdd(title);
    }

    public void createLayer() {}
    public void selectLayer() {}


}
