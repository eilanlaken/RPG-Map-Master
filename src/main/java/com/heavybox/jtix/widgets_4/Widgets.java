package com.heavybox.jtix.widgets_4;

import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Font;
import com.heavybox.jtix.graphics.Graphics;
import com.heavybox.jtix.input.Input;

public final class Widgets {

    public static boolean debugMode = true;

    /*** global theme ***/
    public static Font    themeTextFont         = null;
    public static Color   themeTextColor        = Color.WHITE.clone();
    public static int     themeTextSize         = 18;
    public static float   themeTextLineHeight   = 1.2f;
    public static boolean themeTextAntialiasing = true;
    public static boolean themeTextWrapEnabled  = true;

    private static float pointerX = 0;
    private static float pointerY = 0;

    private Widgets() {}

    public static void update() {
        float windowHalfWidth = Graphics.getWindowWidth() * 0.5f;
        float windowHalfHeight = Graphics.getWindowHeight() * 0.5f;
        Widgets.pointerX = Input.mouse.getX() - windowHalfWidth;
        Widgets.pointerY = windowHalfHeight - Input.mouse.getY();
    }

    public static float getPointerX() {
        return pointerX;
    }

    public static float getPointerY() {
        return pointerY;
    }
}
