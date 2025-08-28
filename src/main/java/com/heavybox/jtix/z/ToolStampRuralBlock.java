package com.heavybox.jtix.z;

import com.heavybox.jtix.assets.Assets;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.graphics.TexturePack;

public class ToolStampRuralBlock extends Tool {

    public final TexturePack layer3;

    public ToolStampRuralBlock(Map map) {
        super(map);
        layer3 = Assets.get("assets/texture-packs/layer_3.yml");
        sclX = 0.5f;
        sclY = 0.5f;
        setRegions();
    }

    private void setRegions() {

    }

    @Override
    public void update(float delta) {

    }

    @Override
    public void renderToolOverlay(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY) {

    }

    @Override
    public void activate() {

    }

    @Override
    public void deactivate() {

    }

    private enum Hut {
        DIAGONAL_BIG_LEFT,
        DIAGONAL_BIG_RIGHT,
        DIAGONAL_SMALL_LEFT,
        DIAGONAL_SMALL_RIGHT,
        HORIZONTAL_BIG,
        HORIZONTAL_SMALL,
        VERTICAL_SMALL,
        VERTICAL_MEDIUM,
        VERTICAL_LARGE,
        VERTICAL_GIANT,
        ;
    }

    private enum Mode {
        SINGLES_HUTS,
        SINGLES_PROPS,
        COMBOS,
        ;
    }

}
