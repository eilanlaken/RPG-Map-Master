package com.heavybox.jtix.z;

import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.TextureRegion;

import java.util.Arrays;
import java.util.Objects;

public class CommandTokenCreate extends Command {

    public Class<? extends Tool> sourceTool;

    public Color tint = Color.WHITE;
    public Token.Type type = Token.Type.UNSPECIFIED;
    public TextureRegion[] regions;

    public CommandTokenCreate(int layer, float x, float y, float deg, float sclX, float sclY, boolean anchor, TextureRegion... regions) {
        super(layer, x, y, deg, sclX, sclY, anchor);
        this.regions = Arrays.stream(regions).filter(Objects::nonNull).toArray(TextureRegion[]::new);
    }

}
