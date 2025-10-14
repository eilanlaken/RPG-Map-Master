package com.heavybox.jtix.z;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.widgets.NodeContainerVertical;
import com.heavybox.jtix.widgets.NodeText;

public class WidgetNodeSidebarTools extends NodeContainerVertical {

    // state
    public WidgetNodeSidebarMenuButton selected = null;
    public Array<WidgetNodeSidebarMenuButton> menuItems = new Array<>();

    WidgetNodeSidebarTools() {
        boxWidthSizing = Sizing.DYNAMIC;
        boxHeightSizing = Sizing.DYNAMIC;
        boxBorderSize = 0;
        boxPaddingTop = 10;
        margin = 0;
        boxPaddingBottom = 5;
        boxBackgroudColor = Color.valueOf("1D1D1D");
        boxBackgroundEnabled = true;

        NodeText title = new NodeText("Tools");
        title.size = 14;
        WidgetNodeSidebarMenuButton select = new WidgetNodeSidebarMenuButton("Terrain", "press 1");
        WidgetNodeSidebarMenuButton move = new WidgetNodeSidebarMenuButton("Trees", "press 2");
        WidgetNodeSidebarMenuButton terrain = new WidgetNodeSidebarMenuButton("Props", "press 3");
        WidgetNodeSidebarMenuButton brush = new WidgetNodeSidebarMenuButton("Architecture", "press 4");
        WidgetNodeSidebarMenuButton path = new WidgetNodeSidebarMenuButton("Mountains", "press 5");
        WidgetNodeSidebarMenuButton text = new WidgetNodeSidebarMenuButton("Text", "press 6");
        WidgetNodeSidebarMenuButton decorations = new WidgetNodeSidebarMenuButton("Decorations", "press 7");
        WidgetNodeSidebarMenuButton export = new WidgetNodeSidebarMenuButton("Export", "press F1");

        menuItems.add(select);
        menuItems.add(move);
        menuItems.add(terrain);
        menuItems.add(brush);
        menuItems.add(path);
        menuItems.add(text);
        menuItems.add(decorations);
        menuItems.add(export);

        addChild(title);
        addChild(select);
        addChild(move);
        addChild(terrain);
        addChild(brush);
        addChild(path);
        addChild(text);
        addChild(decorations);
        addChild(export);

        //addChild(new WidgetNodeSidebarToolOptions("Active Tool", 75));
    }

    protected void select(WidgetNodeSidebarMenuButton item) {
        selected = item;
        item.boxBackgroudColor = WidgetNodeSidebarMenuButton.COLOR_SELECTED;
        for (WidgetNodeSidebarMenuButton menuItem : menuItems) {
            if (selected == menuItem) continue;
            menuItem.boxBackgroudColor = WidgetNodeSidebarMenuButton.COLOR_UNSELECTED;
        }
    }

}
