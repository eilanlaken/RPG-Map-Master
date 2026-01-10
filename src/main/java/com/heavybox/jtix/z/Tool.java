package com.heavybox.jtix.z;

import com.heavybox.jtix.RPGMapMakerScene;
import com.heavybox.jtix.collections.Collections;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.math.Vector2;

public abstract class Tool {

    public static final float MINIMUM_TOKEN_SPACING = 0.5f;

    // TODO
    public BrushMode brushMode = BrushMode.POINT;
    public boolean free = true;
    public int batchCount = 33;
    public float spreadRadius = 155; // in pixels
    public Vector2 lineStart = new Vector2();
    public Vector2 lineEnd = new Vector2();

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
        onSwitchMode();
        free = true;
        brushMode = Collections.enumNext(brushMode);
    }

    protected void onSwitchMode() {}

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
