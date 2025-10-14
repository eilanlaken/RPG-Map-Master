package com.heavybox.jtix.z;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.widgets.Node;
import com.heavybox.jtix.widgets.NodeContainerHorizontal;
import com.heavybox.jtix.widgets.NodeText;

public class WidgetNodeSidebarMenuButton extends NodeContainerHorizontal {

    public static final Color COLOR_UNSELECTED = Color.valueOf("1D1D1D");
    public static final Color COLOR_SELECTED = Color.valueOf("AD1D1D");
    public static final Color TEXT_COLOR_SELECTED = Color.WHITE;
    public static final Color TEXT_COLOR_UNSELECTED = Color.WHITE;

    protected NodeText nameNode;
    protected NodeText hotkeyNode;

    public WidgetNodeSidebarMenuButton(String name, String hotkey) {
        boxHeightSizing = Sizing.DYNAMIC;
        boxWidthSizing = Sizing.STATIC;
        boxWidth = 240;
        boxBorderSize = 0;
        boxPaddingLeft = 10;
        boxPaddingRight = 10;
        boxPaddingTop = 5;
        boxPaddingBottom = 5;
        margin = 15;
        boxBackgroudColor = Color.valueOf("1D1D1D");

        nameNode = new NodeText(name);
        nameNode.size = 22;

        hotkeyNode = new NodeText(hotkey);
        hotkeyNode.size = 15;
        hotkeyNode.color = Color.valueOf("EEEEEE");

        addChild(nameNode);
        addChild(hotkeyNode);
//
//        onClick = () -> {
//            NodeToolBar toolBar = (NodeToolBar) container;
//            toolBar.select(this);
//        };

        onMouseEnter = () -> {
            WidgetNodeSidebarTools toolBar = (WidgetNodeSidebarTools) container;
            if (toolBar.selected == this) return;
            boxBackgroudColor = Color.valueOf("2D2D4D");
        };

        onMouseLeave = () -> {
            WidgetNodeSidebarTools toolBar = (WidgetNodeSidebarTools) container;
            if (toolBar.selected == this) return;
            boxBackgroudColor = Color.valueOf("1D1D1D");
        };
    }

    // I want the last item to stick to the end. I can hard code it.
    @Override
    protected void setChildrenOffset(final Array<Node> children) {
        super.setChildrenOffset(children);
        Node hotkey = children.get(1);
        hotkey.offsetX = (calculateWidth() * 0.5f - boxBorderSize - boxPaddingRight - hotkey.calculateWidth() * 0.5f) * screenSclX;
    }


}
