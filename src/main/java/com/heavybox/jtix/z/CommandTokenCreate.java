package com.heavybox.jtix.z;

import com.heavybox.jtix.graphics.TextureRegion;

public class CommandTokenCreate extends Command {

    public TextureRegion[] regions;

    public CommandTokenCreate(int layer, float x, float y, float deg, float sclX, float sclY, boolean anchor, TextureRegion... regions) {
        super(layer, x, y, deg, sclX, sclY, anchor);
        this.regions = regions;
    }

}
