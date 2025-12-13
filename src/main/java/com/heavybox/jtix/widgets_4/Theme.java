package com.heavybox.jtix.widgets_4;

import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Font;
import com.heavybox.jtix.graphics.TexturePack;
import com.heavybox.jtix.graphics.TextureRegion;
import com.heavybox.jtix.memory.MemoryResource;

public final class Theme implements MemoryResource {

    public TexturePack texturePack;
    public Font font;

    // do checkbox first.
    public TextureRegion themeCheckboxImageUnchecked           = null;
    public TextureRegion themeCheckboxImageChecked             = null;
    public Color         themeCheckboxBorderColorUnchecked     = Color.valueOf("767676");
    public Color         themeCheckboxBorderColorChecked       = Color.valueOf("0075FF");
    public Color         themeCheckboxBackgroundColorCheckmark = Color.valueOf("0075FF");
    public Color         themeCheckboxColorCheckmark           = Color.valueOf("FFFFFF");

    public Theme() {}

    public Theme(final String yaml) {

    }

    @Override
    public void delete() {
        if (texturePack != null) texturePack.delete();
        if (font != null) font.delete();
    }

}
