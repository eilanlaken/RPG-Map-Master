package com.heavybox.jtix.z;

import com.heavybox.jtix.RPGMapMakerScene;
import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.graphics.TextureRegion;
import com.heavybox.jtix.input.Input;
import com.heavybox.jtix.input.Keyboard;

public class ToolDebug extends Tool {

    private final Array<MapToken> tokensPreview = new Array<>();

    public ToolDebug(RPGMapMakerScene scene) {
        super(scene);
    }

    @Override
    public void update(float delta) {
        // handle mode switching, clicking actions etc.
        boolean inputLeftShiftJustPressed = Input.keyboard.isKeyJustPressed(Keyboard.Key.LEFT_SHIFT);

        if (brushMode == BrushMode.POINT) {

        } else if (brushMode == BrushMode.LINE) {

        } else if (brushMode == BrushMode.POLYGON) {

        }
    }

    @Override
    public void renderToolOverlay(Renderer2D renderer2D, float x, float y) {
        // render tool overlay when brush mode is set to points
        if (brushMode == BrushMode.POINT) {
            for (MapToken token : tokensPreview) {
                token.renderPreview(renderer2D, x, y);
            }
            renderer2D.setColor(Color.RED);
            renderer2D.drawCircleThin(Math.max(spreadRadius, 5), 10, x, y, 0,1,1);
        }

        // render tool overlay when brush mode is set to lines
        if (brushMode == BrushMode.LINE) {

        }

        // render tool overlay when brush mode is set to polygons
        if (brushMode == BrushMode.POLYGON) {

        }

        renderer2D.setColor(Color.WHITE); // just to be sure, reset color back to white.
    }

    @Override
    public void activate() {
        tokensPreview.clear();

        if (brushMode == BrushMode.POINT) {
            fillCircleWithTokens();
        }
    }

    // TODO
    private void fillCircleWithTokens() {
        tokensPreview.clear();
        for (int i = 0; i < batchCount; i++) {

        }
    }

    // TODO
    private void fillRectangleWithTokens(float size, float angle) {

    }

    // TODO
    private void fillPolygonWithTokens() {

    }

    @Override
    public void deactivate() {
        tokensPreview.clear();
    }

    @Override
    public String getName() {
        return "Debug Tool";
    }

    private static class MapTokenDebug extends MapToken {

        public Color tint;

        public MapTokenDebug(Color tint, float x, float y, float deg, float sclX, float sclY) {
            super(3, x, y, deg, sclX, sclY);
            this.tint = tint;
        }

        @Override
        public void render(Renderer2D renderer2D) {
            renderer2D.setColor(tint);
            renderer2D.drawRectangleFilled(60,30,x,y,deg,sclX,sclY);
        }

        @Override
        public void renderPreview(Renderer2D renderer2D, float toolX, float toolY) {
            renderer2D.setColor(tint);
            renderer2D.drawRectangleFilled(60,30,x + toolX,y + toolY,deg,sclX,sclY);
        }
    }

}
