package com.heavybox.jtix.z;

import com.heavybox.jtix.widgets.Widget;

public class WidgetSidebar extends Widget {

    public WidgetSidebar() {
        WidgetNodeSidebarTools tools = new WidgetNodeSidebarTools();
        addNode(tools);
    }

}
