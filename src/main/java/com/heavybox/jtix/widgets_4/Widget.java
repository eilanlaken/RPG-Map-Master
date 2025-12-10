package com.heavybox.jtix.widgets_4;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.graphics.Graphics;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.input.InputLayer;

// a container of nodes
public class Widget implements InputLayer {

    public int zIndex = 1;

    private final Array<Node> nodes = new Array<>(true, 1);

    public Widget() {}

    public Widget(int zIndex) {
        this.zIndex = zIndex;
    }

    public void addNodes(final Node... nodes) {
        for (Node node : nodes) {
            addNode(node);
        }
    }

    public void addNode(final Node node) {
        if (node == null) throw new WidgetsException("node must not be null.");
        if (node.hasParent()) throw new WidgetsException("Only ROOT Nodes go inside a Widget. Node " + node + " already has a parent.");
        if (nodes.contains(node, true)) throw new WidgetsException("Widget already contains Node node.");

        nodes.add(node);
    }

    public void removeNode(final Node node) {
        if (node == null) throw new WidgetsException("node must not be null.");
        if (!nodes.contains(node, true)) throw new WidgetsException("Node node is not directly contained in the Widget.");

        nodes.removeValue(node, true);
    }

    // TODO: split into fixedUpdate() and frameUpdate()
    public final void update() {
        float delta = Graphics.getDeltaTime();

        for (Node node : nodes) {
            if (node.active) node.update(delta);
        }
    }

    public final void render(Renderer2D renderer2D) {
        for (Node node : nodes) {
            if (node.active) node.render(renderer2D);
        }
    }

    @Override
    public int getLevel() {
        return zIndex;
    }
}
