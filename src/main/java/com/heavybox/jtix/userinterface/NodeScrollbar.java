package com.heavybox.jtix.userinterface;

import com.heavybox.jtix.collections.ArrayFloat;
import com.heavybox.jtix.collections.ArrayInt;
import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.graphics.Texture;
import com.heavybox.jtix.math.MathUtils;
import com.heavybox.jtix.math.Vector2;

// TODO
public class NodeScrollbar extends Node {

    /* state */
    public float   scrollSpeed = 100; // pixels per ? TODO
    public boolean vertical  = true;
    public float   value     = 0.9f;
    public float   length    = 300;
    public float   thumbSize = 0.2f; // percentage

    /* theme */
    public Texture imageBar   = UserInterface.getTheme().scrollbarImageBar;
    public Texture imageThumb = UserInterface.getTheme().scrollbarImageThumb;
    public Color   colorBar   = UserInterface.getTheme().scrollbarColorBar.clone();
    public Color   colorThumb = UserInterface.getTheme().scrollbarColorThumb.clone();
    public float   thickness  = UserInterface.getTheme().scrollbarThickness;

    /* internal params for rendering */
    private final ArrayFloat polygonBar   = new ArrayFloat(true, 8);
    private final ArrayFloat polygonThumb = new ArrayFloat(true, 8);
    private final ArrayInt   indices      = new ArrayInt(true, 0,1,3,3,1,2);

    public NodeScrollbar() {
        onMouseDragStartDefault(e -> {
            value = 0.5f - e.mouseLocalY / length;
            value = MathUtils.clampFloat(value, 0, 1);
        });

        onMouseDragDefault(e -> {
            value = value - (e.mouseLocalY - e.mouseLocalYPrev) / length;
            value = MathUtils.clampFloat(value, 0, 1);
        });
    }

    @Override
    protected final void draw(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY) {
        if (imageBar != null || imageThumb != null) setPolygonShapes();

        drawBar(renderer2D, x, y, deg, sclX, sclY);

        value = MathUtils.clampFloat(value, 0, 1);
        float offset_x = 0;
        float offset_y = (0.5f - value) * (length - thumbSize * length);
        Vector2 offset_transformed = new Vector2(offset_x, offset_y);
        offset_transformed.scl(sclX, sclY);
        offset_transformed.rotateDeg(deg);
        drawThumb(renderer2D, x + offset_transformed.x, y + offset_transformed.y, deg, sclX, sclY);
    }

    protected void drawBar(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY) {
        if (imageBar != null) {
            renderer2D.drawPolygonFilled(imageBar, polygonBar, indices, x, y, deg, sclX, sclY);
            return;
        }

        renderer2D.setColor(colorBar);
        renderer2D.drawRectangleFilled(thickness, length, x, y, deg, sclX, sclY);
    }

    protected void drawThumb(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY) {
        if (imageThumb != null) {
            renderer2D.drawPolygonFilled(imageThumb, polygonThumb, indices, x, y, deg, sclX, sclY);
            return;
        }

        final float thumbLength = thumbSize * length;
        renderer2D.setColor(colorThumb);
        renderer2D.setColor(1,1,1,0.3f);
        renderer2D.drawRectangleFilled(thickness, thumbLength, x, y, deg, sclX, sclY);
    }

    @Override
    protected final void onFixedUpdate(float delta) {
        fixedUpdateScrollbar(delta);
    }

    private void setPolygonShapes() {
        // bar
        polygonBar.clear();
        float widthHalfBar = thickness * 0.5f;
        float heightHalfBar = length * 0.5f;
        polygonBar.add(-widthHalfBar, -heightHalfBar);
        polygonBar.add( widthHalfBar, -heightHalfBar);
        polygonBar.add( widthHalfBar,  heightHalfBar);
        polygonBar.add(-widthHalfBar,  heightHalfBar);
        // thumb
        polygonThumb.clear();
        float widthHalfThumb = thickness * 0.5f;
        float heightHalfThumb = length * thumbSize * 0.5f;
        polygonThumb.add(-widthHalfThumb, -heightHalfThumb);
        polygonThumb.add( widthHalfThumb, -heightHalfThumb);
        polygonThumb.add( widthHalfThumb,  heightHalfThumb);
        polygonThumb.add(-widthHalfThumb,  heightHalfThumb);
    }

    protected void fixedUpdateScrollbar(float delta) {}

    @Override
    protected float getWidth() {
        return vertical ? thickness : length;
    }

    @Override
    protected float getHeight() {
        return vertical ? length : thickness;
    }

}
