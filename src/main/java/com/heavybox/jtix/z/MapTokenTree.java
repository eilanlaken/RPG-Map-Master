package com.heavybox.jtix.z;

import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.graphics.TexturePack;
import com.heavybox.jtix.graphics.TextureRegion;

public class MapTokenTree extends MapToken {

    private TextureRegion regionTrunk;
    private TextureRegion regionBody;
    private TextureRegion regionFruits;

    public MapTokenTree(TexturePack layer_3, float x, float y, float sclX, float sclY) {
        super(3, x, y, 0, sclX, sclY);
    }

    @Override
    public void render(Renderer2D renderer2D) {

    }
}
