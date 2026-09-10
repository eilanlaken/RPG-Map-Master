package com.heavybox.jtix.userinterface;

import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.graphics.TextureRegion;

// TODO
public class NodeOption extends Node {

    /* state */
    public  int     groupID;
    public  Object  value;
    private boolean on;

    /* rendering */
    public TextureRegion imageOn     = UserInterface.getTheme().optionImageOn;
    public TextureRegion imageOff    = UserInterface.getTheme().optionImageOff;
    public Color         colorBorder = UserInterface.getTheme().optionColorBorder;
    public Color         colorFill   = UserInterface.getTheme().optionColorFill;
    public float         size        = UserInterface.getTheme().optionSize;

    public NodeOption(final int groupID, final Object value) {
        this.value = value;
        this.groupID = groupID;
        this.on = true;

        onMouseClickDefault(e -> {
            setOn();
        });
    }

    @Override
    protected void onParentChange() {
        setOn();
    }

    // TODO: handle the case where this is a root node.
    private void setOn() {
        this.on = true;

        Node root = getRoot();
        setOthersOff(root);
    }

    private void setOthersOff(final Node node) {
        if (node == null) return;
        if (node instanceof NodeOption) {
            NodeOption option = (NodeOption) node;
            if (option != this && option.groupID == this.groupID) option.on = false;
        }

        for (Node child : node.getChildren()) {
            setOthersOff(child);
        }
    }

    public boolean isOn() { return on; }

    @Override
    protected void draw(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY) {
        final float radius = size * 0.5f;
        if (on) {
            if (imageOn == null) {
                renderer2D.setColor(colorBorder);
                renderer2D.drawCircleBorder(radius, radius * 0.1f, 20, x, y, deg, sclX, sclY);
                renderer2D.setColor(colorFill);
                renderer2D.drawCircleFilled(radius * 0.72f, 20, x, y, deg, sclX, sclY);
            } else {
                renderer2D.drawTextureRegion(imageOn, x, y, deg, sclX, sclY);
            }
            return;
        }

        if (imageOff == null) {
            renderer2D.setColor(colorFill);
            renderer2D.drawCircleBorder(radius, radius * 0.1f, 20, x, y, deg, sclX, sclY);
        } else {
            renderer2D.drawTextureRegion(imageOff, x, y, deg, sclX, sclY);
        }
    }

    @Override
    public float getWidth() {
        if (on) return imageOn != null ? imageOn.packedWidth : size;
        return imageOff != null ? imageOff.packedWidth : size;
    }

    @Override
    public float getHeight() {
        if (on) return imageOn != null ? imageOn.packedHeight : size;
        return imageOff != null ? imageOff.packedHeight : size;
    }

}
