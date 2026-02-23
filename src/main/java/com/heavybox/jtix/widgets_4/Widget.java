package com.heavybox.jtix.widgets_4;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.graphics.Graphics;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.input.InputLayer;

// a container of nodes
public class Widget implements InputLayer {

    /*** programmer set user-data for in game logic ***/
    public Object  userData = null;
    public int     zIndex   = 1;
    public boolean active   = true;

    private final Array<WidgetNode> nodes = new Array<>(true, 1);

    public Widget() {
        this(1);
    }

    public Widget(int zIndex) {
        this.zIndex = zIndex;
        // maybe register itself as input layer.
    }

    public final void addNodes(final WidgetNode... nodes) {
        for (WidgetNode node : nodes) {
            if (node == null) throw new WidgetsException("node must not be null.");
            if (node.hasParent()) throw new WidgetsException("Only ROOT Nodes go inside a Widget. Node " + node + " already has a parent.");
            if (this.nodes.contains(node, true)) throw new WidgetsException("Widget already contains Node node.");

            this.nodes.add(node);
            node.setWidget(this);
        }
    }

    @Deprecated
    public final void addNode(final WidgetNode node) {
        if (node == null) throw new WidgetsException("node must not be null.");
        if (node.hasParent()) throw new WidgetsException("Only ROOT Nodes go inside a Widget. Node " + node + " already has a parent.");
        if (nodes.contains(node, true)) throw new WidgetsException("Widget already contains Node node.");

        nodes.add(node);
        node.setWidget(this);
    }

    public final void removeNode(final WidgetNode node) {
        if (node == null) throw new WidgetsException("node must not be null.");
        if (!nodes.contains(node, true)) throw new WidgetsException("Node node is not directly contained in the Widget.");

        nodes.removeValue(node, true);
        node.setWidget(null);
    }

    public final void show() {
        if (this.active) return;

        active = true;
        // TODO: maybe register from input layers
    }

    public final void hide() {
        if (!active) return;

        active = false;
        // TODO: maybe unregister from input layers
    }

    // TODO: split into fixedUpdate() and frameUpdate()
    public final void update() {
        if (!active) return;

        float delta = Graphics.getDeltaTime();
        for (WidgetNode node : nodes) {
            if (node.active) node.update(delta);
        }
    }

    public final void render(Renderer2D renderer2D) {
        if (!active) return;

        for (WidgetNode node : nodes) {
            if (node.active) node.render(renderer2D);
        }
    }

    @Override
    public int getLevel() {
        return zIndex;
    }
}
