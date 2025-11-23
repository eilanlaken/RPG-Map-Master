package com.heavybox.jtix.widgets_4;

import com.heavybox.jtix.assets.Assets;
import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.graphics.TextureRegion;

// TODO.
public class WidgetImage extends Widget {

    public TextureRegion region = null;
    public float zoom = 1;
    public float width = 0;
    public float height = 0;
    public Color tint = Color.WHITE.clone();
    public boolean border = false;
    public Color borderColor = Color.WHITE.clone();
    public float borderThickness = 4;

    public WidgetImage(final String src) {
        boolean loaded = Assets.isLoaded(src);
    }

    @Override
    protected void draw(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY) {

    }

    @Override
    protected float getWidth() {
        return 0;
    }

    @Override
    protected float getHeight() {
        return 0;
    }

    @Override
    protected void fixedUpdate(float delta) {

    }

}
