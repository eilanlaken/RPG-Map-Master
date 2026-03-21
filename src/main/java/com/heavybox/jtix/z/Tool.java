package com.heavybox.jtix.z;

import com.heavybox.jtix.RPGMapMakerScene;
import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.collections.Collections;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.graphics.TextureRegion;
import com.heavybox.jtix.input.InputEventHandler;
import com.heavybox.jtix.math.Vector2;

public abstract class Tool implements InputEventHandler {

    // references
    protected final Map map;
    protected final RPGMapMakerScene scene;

    private boolean active = false;

    // modes
    public Mode mode = Mode.ADD;
    public Shape shape = Shape.CIRCLE;
    public boolean free = true;
    public float density = 0.15f; // units per 100 pixels
    public float spreadRadius = 200; // in pixels. convert to sprite width / 2
    public float minimum_spacing = 0.5f;
    public Vector2 lineStart = new Vector2();
    public Vector2 lineEnd = new Vector2();
    public boolean tokensAngleMatchLine = false;
    public Array<Vector2> polygonPoints = new Array<>(true, 10);

    // brush transform
    public float x    = 0;
    public float y    = 0;
    public float deg  = 0;
    public float sclX = 1;
    public float sclY = 1;

    public Tool(RPGMapMakerScene scene) {
        this.scene = scene;
        this.map = scene.getMap();
    }

    protected String[] getPrefixes() {
        return null;
    }

    public void setShape(Shape shape) {
        this.shape = shape;
        onSetShape();
    }

    public void switchToNextBrushShape() {
        shape = Collections.enumNext(shape);
        onSetShape();
    }

    protected void setMode(Mode mode) {
        this.mode = mode;
        onSetMode();
    }

    protected void onSetShape() {
        this.free = true;
    }

    protected void onSetParameter() {}
    protected void onSetMode() {}

    // TODO - should be abstract
    protected TextureRegion[] getRegions() { return null; }

    protected float getMinSpacing() {
        TextureRegion[] regions = getRegions();
        float pixelSpacing = 0;
        for (TextureRegion region : regions) {
            if (region == null) continue;
            pixelSpacing = Math.max(region.packedWidth, pixelSpacing);
        }
        return pixelSpacing * minimum_spacing * Math.abs(sclX);
    }

    protected int getBatchCount(float area) {
        if (area <= 0f || density <= 0f) return 1;
        float densityPerPixel = density / 100f;
        return Math.max(1, (int) Math.ceil(area * densityPerPixel));
    }

    public void setDensity(float density) {
        this.density += density;
        onSetParameter();
    }

    public void setSpreadRadius(float spreadRadius) {
        this.spreadRadius = spreadRadius;
        onSetParameter();
    }

    public void setScale(float sclX, float sclY) {
        this.sclX = sclX;
        this.sclY = sclY;
        onSetParameter();
    }

    public abstract void update(float delta);
    public abstract void renderToolOverlay(Renderer2D renderer2D, float x, float y);
    public abstract void renderToolText(Renderer2D renderer2D, float x, float y);
    public String getHelperText() {return getName();}

    public void activate() {
        this.active = true;
        onActivate();
    }

    public void deactivate() {
        this.active = false;
        onDeactivate();
    }

    public abstract void onActivate();
    public abstract void onDeactivate();
    public abstract String getName();

    @Override
    public boolean active() { return active; }

    // TODO
    public enum Shape {
        POINT,
        LINE,
        CIRCLE,
        POLYGON, // TODO: BUG HERE WHEN CLOSING A POLYGON EXACTLY
        ;
    }

    public enum Mode {
        ADD,
        SUB,
        ;
    }

}
