package com.heavybox.jtix.widgets;

import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Font;

// represents a global UI theme of an application.
// styles can be inline to override this global theme.
public final class Theme {

    // TODO: make static
    public static Font       textFont                     = null;
    public static Color      textColor                    = Color.WHITE.clone();
    public static int        textSize                     = 18;
    public static float      textLineHeight               = 1.2f;
    public static boolean    textAntialiasing             = true;
    public static boolean    textWrapEnabled              = true;

    // TODO: Add Themes for common supported primitives: image, text, text-field, checkbox, radio-button, ...


    // TODO: scrollbars
    public static ScrollbarRenderer scrollbarRenderer = getDefaultScrollbarRenderer();

    // equivalent to CSS text alignment.
    // TODO: later.
    public enum TextWrap {
        NONE,
        CENTER,
        LEFT,
        RIGHT,
        JUSTIFY,
        // START, // language sensitive ltr or rtl
        // END
    }

    public static abstract class ScrollbarRenderer {

        protected abstract void render(Node node, float barWidth, float barHeight, float scrollProgressPercentage, float visiblePortionPercentage, float x, float y, float deg, float sclX, float sclY);

    }

    private static ScrollbarRenderer getDefaultScrollbarRenderer() {
        return new ScrollbarRenderer() {
            @Override
            protected void render(Node node, float barWidth, float barHeight, float progress, float viewPortion, float x, float y, float deg, float sclX, float sclY) {

            }
        };
    }

}
