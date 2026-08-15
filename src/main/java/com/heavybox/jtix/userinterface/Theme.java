package com.heavybox.jtix.userinterface;

import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Font;
import com.heavybox.jtix.graphics.TexturePack;
import com.heavybox.jtix.graphics.TextureRegion;
import com.heavybox.jtix.memory.MemoryResource;

public class Theme implements MemoryResource {

    public TexturePack texturePack = null;

    /* picture */

    /* text */
    public Font    textFont         = null;
    public Color   textColor        = Color.WHITE.clone();
    public int     textSize         = 22;
    public boolean textAntialiasing = true;
    public float   lineSpacing      = 1.1f;

    /* slider */
    public TextureRegion sliderImageBackground = null;
    public TextureRegion sliderImageFill       = null;
    public TextureRegion sliderImageThumb      = null;
    public Color         sliderColorBackground = Color.GRAY.clone();
    public Color         sliderColorFill       = Color.valueOf("0075FF");
    public Color         sliderColorThumb      = Color.valueOf("0075FF");
    public float         sliderLength          = 200;
    public float         sliderThumbSize       = 18;
    public float         sliderThickness       = 7.5f;

    /* container */

    /* scrollbar */

    /* checkbox */
    public TextureRegion checkboxImageChecked             = null;
    public TextureRegion checkboxImageUnchecked           = null;
    public Color         checkboxColorBorderChecked       = Color.valueOf("0075FF");
    public Color         checkboxColorBorderUnchecked     = Color.valueOf("767676");
    public Color         checkboxColorCheckmarkBackground = Color.valueOf("0075FF");
    public Color         checkboxColorCheckmark           = Color.valueOf("FFFFFF");
    public float         checkboxSize                     = 27;
    public float         checkboxSizeBorder               = 5;

    @Override
    public void delete() {
        if (texturePack != null) texturePack.delete();
        if (textFont != null) textFont.delete();
    }

}
