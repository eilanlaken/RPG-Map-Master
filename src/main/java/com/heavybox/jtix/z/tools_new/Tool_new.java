package com.heavybox.jtix.z.tools_new;

import com.heavybox.jtix.RPGMapMakerScene;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.input.InputEventHandler;
import com.heavybox.jtix.z.Map;

public abstract class Tool_new implements InputEventHandler {

    // meta
    protected final Map map;
    protected final RPGMapMakerScene scene;
    public boolean active = false;

    // brush transform
    public float x    = 0;
    public float y    = 0;
    public float deg  = 0;
    public float sclX = 1;
    public float sclY = 1;

    public Tool_new(RPGMapMakerScene scene) {
        this.scene = scene;
        this.map = scene.getMap();
    }

    public final String getName() {return getClass().getSimpleName();};

    abstract void onChangeParameters();
    public abstract void update(float delta);
    public abstract void renderToolOverlay(Renderer2D renderer2D, float x, float y);
    public abstract void renderToolText(Renderer2D renderer2D, float x, float y);
    abstract String getHelperText();

    public abstract void activate();
    public abstract void deactivate();

    public enum Shape {
        POINT,
        LINE,
        CIRCLE,
        POLYGON,
        ;
    }

    public enum Mode {
        ADD,
        SUB,
        ;
    }

}
