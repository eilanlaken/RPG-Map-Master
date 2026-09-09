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
    public boolean vertical  = true;
    public float   value     = 0f;
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

    public NodeScrollbar(boolean vertical) {
        this.vertical = vertical;

        onMouseDragStartDefault(e -> {
            value = vertical ? 0.5f - e.mouseLocalY / length : 0.5f + e.mouseLocalX / length;
            value = MathUtils.clampFloat(value, 0, 1);
        });

        onMouseDragDefault(e -> {
            value = vertical ? value - (e.mouseLocalY - e.mouseLocalYPrev) / length : value + (e.mouseLocalX - e.mouseLocalXPrev) / length;
            value = MathUtils.clampFloat(value, 0, 1);
        });
    }

    public NodeScrollbar() {
        this(true);
    }

    @Override
    protected final void draw(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY) {
        if (imageBar != null || imageThumb != null) setPolygonShapes();

        if (imageBar != null) {
            renderer2D.drawPolygonFilled(imageBar, polygonBar, indices, x, y, vertical ? deg : deg + 90, sclX, sclY);
        } else {
            renderer2D.setColor(colorBar);
            float width = vertical ? thickness : length;
            float height = vertical ? length : thickness;
            renderer2D.drawRectangleFilled(width, height, x, y, deg, sclX, sclY);
        }

        value = MathUtils.clampFloat(value, 0, 1);
        float offset_x = vertical ? 0 : (0.5f - value) * (-length + thumbSize * length);
        float offset_y = vertical ? (0.5f - value) * (length - thumbSize * length) : 0;
        Vector2 offset_transformed = new Vector2(offset_x, offset_y);
        offset_transformed.scl(sclX, sclY);
        offset_transformed.rotateDeg(deg);
        float thumbX = x + offset_transformed.x;
        float thumbY = y + offset_transformed.y;
        if (imageThumb != null) {
            renderer2D.drawPolygonFilled(imageThumb, polygonThumb, indices, thumbX, thumbY, vertical ? deg : deg + 90, sclX, sclY);
        } else {
            final float thumbLength = thumbSize * length;
            float width = vertical ? thickness : thumbLength;
            float height = vertical ? thumbLength : thickness;
            renderer2D.setColor(colorThumb);
            renderer2D.drawRectangleFilled(width, height, thumbX, thumbY, deg, sclX, sclY);
        }
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
    public final float getWidth() {
        return vertical ? thickness : length;
    }

    @Override
    public final float getHeight() {
        return vertical ? length : thickness;
    }

}
