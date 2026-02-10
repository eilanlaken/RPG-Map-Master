package com.heavybox.jtix.z;

import com.heavybox.jtix.RPGMapMakerScene;
import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.collections.Collections;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.graphics.TextureRegion;
import com.heavybox.jtix.math.Vector2;

public abstract class Tool {

    public static final float MINIMUM_TOKEN_SPACING = 0.5f;

    // TODO
    public BrushMode brushMode = BrushMode.POINT;
    public boolean free = true;
    public float density = 0.05f; // units per 100 pixels
    public float spreadRadius = 200; // in pixels. convert to sprite width / 2
    public float minimum_spacing = 0.5f;

    public Vector2 lineStart = new Vector2();
    public boolean tokensAngleMatchLine = false;
    public Array<Vector2> polygonPoints = new Array<>(true, 10);

    public float x    = 0;
    public float y    = 0;
    public float deg  = 0;
    public float sclX = 1;
    public float sclY = 1;

    protected Map map;
    protected RPGMapMakerScene scene;

    public Tool(RPGMapMakerScene scene) {
        this.scene = scene;
        this.map = scene.getMap();
    }

    public void switchToNextBrushMode() {
        free = true;
        brushMode = Collections.enumNext(brushMode);
        onSwitchMode();
    }

    protected void onSwitchMode() {}
    protected void onParametersChange() {}

    // TODO
    protected TextureRegion[] getRegions() { return null; }

    protected float getSpacingX() {
        TextureRegion[] regions = getRegions();
        float pixelSpacing = 0;
        for (TextureRegion region : regions) {
            if (region == null) continue;
            pixelSpacing = Math.max(region.packedWidth, pixelSpacing);
        }
        return pixelSpacing * minimum_spacing * Math.abs(sclX);
    }

    protected float getSpacingY() {
        TextureRegion[] regions = getRegions();
        float pixelSpacing = 0;
        for (TextureRegion region : regions) {
            if (region == null) continue;
            pixelSpacing = Math.max(region.packedWidth, pixelSpacing);
        }
        return pixelSpacing * minimum_spacing * Math.abs(sclY);
    }

    protected int getBatchCount(float area) {
        if (area <= 0f || density <= 0f) return 0;
        float densityPerPixel = density / 100f;
        return (int) Math.ceil(area * densityPerPixel);
    }

    public abstract void update(float delta);
    public abstract void renderToolOverlay(Renderer2D renderer2D, float x, float y);
    public abstract void activate();
    public abstract void deactivate();
    public abstract String getName();

    // TODO
    public enum BrushMode {
        POINT,
        LINE,
        POLYGON, // TODO: BUG HERE WHEN CLOSING A POLYGON EXACTLY
        ;
    }

}
