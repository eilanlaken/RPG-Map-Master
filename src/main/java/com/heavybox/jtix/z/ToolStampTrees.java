package com.heavybox.jtix.z;

import com.heavybox.jtix.graphics.Renderer2D;

public class ToolStampTrees extends Tool {

    public float scale = 1;
    public boolean addFruits = false;
    public boolean addTrunk = true;
    public Mode mode = Mode.REGULAR;
    public int batchSize = 1;

    public ToolStampTrees(Map map) {
        super(map);
    }

    @Override
    public void update(float delta) {

    }

    @Override
    public void renderToolOverlay(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY) {

    }

    public enum Mode {
        REGULAR,
        CYPRESS,
    }

}
