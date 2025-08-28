package com.heavybox.jtix.z;

import com.heavybox.jtix.assets.Assets;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.graphics.TexturePack;
import com.heavybox.jtix.graphics.TextureRegion;

public class ToolStampProps extends Tool {

    public TexturePack layer3;
    public TextureRegion region;

    public ToolStampProps(Map map) {
        super(map);
        layer3 = Assets.get("assets/texture-packs/layer_3.yml");

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

}
