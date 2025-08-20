package com.heavybox.jtix.z;

import com.heavybox.jtix.graphics.Renderer2D;

public class ToolTerrain extends Tool {

    public Mode mode = Mode.SUB_LAND;

    public ToolTerrain(Map map) {
        super(map);
    }

    @Override
    public void update(float delta) {

    }

    @Override
    public void renderToolOverlay(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY) {
        renderer2D.drawCircleThin(20,30, x, y, deg, sclX, sclY);
    }

    public enum Mode {

        ADD_LAND,
        SUB_LAND,
        ADD_ROAD,
        SUB_ROAD

    }

}
