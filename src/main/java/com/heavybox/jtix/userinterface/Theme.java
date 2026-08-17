package com.heavybox.jtix.userinterface;

import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Font;
import com.heavybox.jtix.graphics.TexturePack;
import com.heavybox.jtix.graphics.TextureRegion;
import com.heavybox.jtix.memory.MemoryResource;
import com.heavybox.jtix.widgets.Widgets;

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

    /* panel */
    public Color panelColorBackground           = Color.valueOf("#227BFF");
    public float panelPaddingTop                = 10;
    public float panelPaddingBottom             = 10;
    public float panelPaddingLeft               = 10;
    public float panelPaddingRight              = 10;
    public float panelChildSpacingVertical      = 15;
    public float panelChildSpacingHorizontal    = 15;
    public float panelCornerRadiusTopLeft       = 0;
    public float panelCornerRadiusTopRight      = 50;
    public float panelCornerRadiusBottomRight   = 0;
    public float panelCornerRadiusBottomLeft    = 0;
    public int   panelCornerSegmentsTopLeft     = 0;
    public int   panelCornerSegmentsTopRight    = 3;
    public int   panelCornerSegmentsBottomRight = 0;
    public int   panelCornerSegmentsBottomLeft  = 0;
    public float panelSizeBorder                = 0;
    public Color panelColorBorder               = Color.RED.clone();

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
