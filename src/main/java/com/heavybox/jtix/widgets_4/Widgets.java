package com.heavybox.jtix.widgets_4;

import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Font;
import com.heavybox.jtix.graphics.Graphics;
import com.heavybox.jtix.input.Input;

public final class Widgets {

    /*** some global flags ***/
    public static boolean debugMode = true; // TODO: use this when rendering: render regions if true.

    /*** global theme ***/
    // text
    public static Font    themeTextFont                              = null;
    public static Color   themeTextColor                             = Color.WHITE.clone();
    public static int     themeTextSize                              = 18;
    public static float   themeTextLineHeight                        = 1.2f;
    public static boolean themeTextAntialiasing                      = true;
    public static boolean themeTextWrapEnabled                       = true;
    // containers
    public static boolean themeContainerBoxBackgroundVisible         = true;
    public static Color   themeContainerBoxBackgroundColor           = Color.valueOf("#227BFF");
    public static int     themeContainerBoxPaddingTop                = 40;
    public static int     themeContainerBoxPaddingBottom             = 10;
    public static int     themeContainerBoxPaddingLeft               = 10;
    public static int     themeContainerBoxPaddingRight              = 10;
    public static int     themeContainerBoxChildSpacingVertical      = 5;
    public static int     themeContainerBoxChildSpacingHorizontal    = 5;
    public static int     themeContainerBoxCornerRadiusTopLeft       = 0;
    public static int     themeContainerBoxCornerRadiusTopRight      = 0;
    public static int     themeContainerBoxCornerRadiusBottomRight   = 0;
    public static int     themeContainerBoxCornerRadiusBottomLeft    = 0;
    public static int     themeContainerBoxCornerSegmentsTopLeft     = 10;
    public static int     themeContainerBoxCornerSegmentsTopRight    = 10;
    public static int     themeContainerBoxCornerSegmentsBottomRight = 10;
    public static int     themeContainerBoxCornerSegmentsBottomLeft  = 10;
    public static int     themeContainerBoxBorderSize                = 4;
    public static Color   themeContainerBoxBorderColor               = Color.RED.clone();
    // scrollbars
    public static boolean themeScrollbarDrawForwardButton            = true;
    public static boolean themeScrollbarDrawBackwardButton           = true;
    // images
    // input - text fields
    // input - checkbox
    // input - radio button

    /*** input device state */
    private static float pointerXPrev = 0;
    private static float pointerYPrev = 0;
    private static float pointerX     = 0;
    private static float pointerY     = 0;

    private Widgets() {}

    public static void update() {
        float windowHalfWidth = Graphics.getWindowWidth() * 0.5f;
        float windowHalfHeight = Graphics.getWindowHeight() * 0.5f;
        Widgets.pointerXPrev = Widgets.pointerX;
        Widgets.pointerYPrev = Widgets.pointerY;
        Widgets.pointerX = Input.mouse.getX() - windowHalfWidth;
        Widgets.pointerY = windowHalfHeight - Input.mouse.getY();
    }

    public static float getPointerX() {
        return pointerX;
    }
    public static float getPointerY() { return pointerY; }
    public static float getPointerXPrev() { return pointerXPrev; }
    public static float getPointerYPrev() { return pointerYPrev; }

    public static void useGlobalTheme() {

    }


}
