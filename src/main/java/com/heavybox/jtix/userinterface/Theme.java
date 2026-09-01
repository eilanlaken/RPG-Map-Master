package com.heavybox.jtix.userinterface;

import com.heavybox.jtix.graphics.*;
import com.heavybox.jtix.memory.MemoryResource;

public class Theme implements MemoryResource {

    public TexturePack texturePack = null;

    /* picture */

    /* text */
    public Font    textFont         = null;
    public Color   textColor        = Color.WHITE.clone();
    public int     textSize         = 22;
    public boolean textAntialiasing = true;
    public float   textLineSpacing  = 1.1f;

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

    /* group */
    public Texture groupTextureBackground         = null;
    public Color   groupColorBackground           = Color.WHITE.clone();
    public float   groupPaddingTop                = 10;
    public float   groupPaddingBottom             = 10;
    public float   groupPaddingLeft               = 10;
    public float   groupPaddingRight              = 10;
    public float   groupChildSpacingVertical      = 15;
    public float   groupChildSpacingHorizontal    = 15;
    public float   groupCornerRadiusTopLeft       = 0;
    public float   groupCornerRadiusTopRight      = 50;
    public float   groupCornerRadiusBottomRight   = 0;
    public float   groupCornerRadiusBottomLeft    = 0;
    public int     groupCornerSegmentsTopLeft     = 0;
    public int     groupCornerSegmentsTopRight    = 3;
    public int     groupCornerSegmentsBottomRight = 0;
    public int     groupCornerSegmentsBottomLeft  = 0;
    public float   groupSizeBorder                = 5;
    public Color   groupColorBorder               = Color.RED.clone();

    /* scrollbar */
    public Texture scrollbarImageBar   = null;
    public Texture scrollbarImageThumb = null;
    public Color   scrollbarColorBar   = Color.valueOf("343538");
    public Color   scrollbarColorThumb = Color.valueOf("5c5d5e");
    public float   scrollbarThickness  = 10;

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
