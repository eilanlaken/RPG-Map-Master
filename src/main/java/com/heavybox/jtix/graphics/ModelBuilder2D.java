package com.heavybox.jtix.graphics;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

// TODO
public final class ModelBuilder2D {

    private static float color = Color.WHITE_FLOAT;

    public static void begin() {
        ModelBuilder2D.color = Color.WHITE_FLOAT;
    }

    public static void setColor(final Color color) {
        if (color == null) {
            ModelBuilder2D.color = Color.WHITE_FLOAT;
            return;
        }
        ModelBuilder2D.color = color.toFloatBits();
    }

    public static void addTexture(@Nullable Texture texture, float x, float y, float deg, float sclX, float sclY) {

    }

    public static void addTextureRegion(@NotNull TextureRegion region, float x, float y, float deg, float sclX, float sclY) {

    }

    public static void addCircleFilled() {

    }

    public static void end() {

    }


}
