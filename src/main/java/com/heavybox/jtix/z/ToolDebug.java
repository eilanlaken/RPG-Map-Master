package com.heavybox.jtix.z;

import com.heavybox.jtix.RPGMapMakerScene;
import com.heavybox.jtix.assets.Assets;
import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.graphics.TexturePack;
import com.heavybox.jtix.graphics.TextureRegion;
import com.heavybox.jtix.input.Input;
import com.heavybox.jtix.input.Keyboard;
import com.heavybox.jtix.input.Mouse;
import com.heavybox.jtix.math.MathUtils;
import com.heavybox.jtix.math.Vector2;

public class ToolDebug extends Tool {

    private final TexturePack atlas;
    private final TextureRegion region;
    private final Array<MapToken> tokensPreview = new Array<>();
    private final Array<MapToken> alreadyCreatedTokens = new Array<>();

    public ToolDebug(RPGMapMakerScene scene) {
        super(scene);
        atlas = Assets.get("assets/texture-packs/layer_3.yml");
        region = atlas.getRegion("assets/textures-layer-3/debug_rect.png");
    }

    @Override
    public void update(float delta) {
        // handle mode switching, clicking actions etc.
        // TODO: take input layers into account
        boolean inputLeftShiftJustPressed = Input.keyboard.isKeyJustPressed(Keyboard.Key.LEFT_SHIFT);
        boolean leftPressedAndMoved = Input.mouse.isButtonPressed(Mouse.Button.LEFT) && Input.mouse.moved();
        boolean leftClicked = Input.mouse.isButtonClicked(Mouse.Button.LEFT);

        if (brushMode == BrushMode.POINT) {
            if (leftClicked || leftPressedAndMoved) {
                spawnTokens();
            }
            return;
        }

        if (brushMode == BrushMode.LINE) {

            return;
        }

        if (brushMode == BrushMode.POLYGON) {

            return;
        }
    }

    private void spawnTokens() {
        map.getAllTokens(region, alreadyCreatedTokens);

        for (MapToken token : tokensPreview) {
            // TODO: consider density
            Vector2 position = new Vector2(token.x + x, token.y + y);
            float minDistance = Float.POSITIVE_INFINITY;
            for (MapToken mapToken : alreadyCreatedTokens) {
                float distanceSquared = Vector2.dst2(position.x, position.y, mapToken.x, mapToken.y);
                minDistance = Math.min(distanceSquared, minDistance);
            }
            minDistance = (float) Math.sqrt(minDistance);
            if (minDistance < Tool.MINIMUM_TOKEN_SPACING * region.originalWidth) continue;

            CommandTokenCreate createToken = new CommandTokenCreate(
                    3,
                    token.x + x, token.y + y, deg, sclX, sclY, true,
                    atlas.getRegion("assets/textures-layer-3/debug_rect.png")
            );
            createToken.sourceTool = this.getClass();
            createToken.tint = token.tint;
            map.addCommand(createToken);
        }
        refillCircleWithTokens();
    }

    @Override
    public void renderToolOverlay(Renderer2D renderer2D, float x, float y) {
        // render tool overlay when brush mode is set to points
        if (brushMode == BrushMode.POINT && free) {
            for (MapToken token : tokensPreview) {
                token.renderPreview(renderer2D, x, y);
            }
            renderer2D.setColor(Color.RED);
            renderer2D.drawCircleThin(Math.max(spreadRadius, 5), 10, x, y, 0,1,1);
        }
        if (brushMode == BrushMode.POINT && !free) {

        }

        // render tool overlay when brush mode is set to lines
        if (brushMode == BrushMode.LINE && free) {

        }
        if (brushMode == BrushMode.LINE && !free) {

        }

        // render tool overlay when brush mode is set to polygons
        if (brushMode == BrushMode.POLYGON && free) {

        }
        if (brushMode == BrushMode.POLYGON && !free) {

        }

        renderer2D.setColor(Color.WHITE); // just to be sure, reset color back to white.
    }

    @Override
    public void activate() {
        System.out.println("active");
        tokensPreview.clear();

        if (brushMode == BrushMode.POINT) {
            refillCircleWithTokens();
        }
    }

    // TODO - filter against self. If a token is too close to one already in the circle, don't add it.
    private void refillCircleWithTokens() {
        tokensPreview.clear();
        float slice = 2.0f * MathUtils.PI / batchCount;

        for (int i = 0; i < batchCount; i++) {
            float radius = MathUtils.randomUniformFloat(0,1) * spreadRadius; // distance from center
            float angle  = i * slice + MathUtils.randomUniformFloat(0,1) * slice;

            float offsetX = MathUtils.cosRad(angle) * radius;
            float offsetY = MathUtils.sinRad(angle) * radius;

            MapToken token = new MapToken(3, offsetX, offsetY, 0, 1,1, atlas.getRegion("assets/textures-layer-3/debug_rect.png"));
            token.tint = Color.randomOpaque();
            tokensPreview.add(token);
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

//    private static class MapTokenDebug extends MapToken {
//
//        public Color tint;
//
//        public MapTokenDebug(Color tint, float x, float y, float deg, float sclX, float sclY) {
//            super(3, x, y, deg, sclX, sclY);
//            this.tint = tint;
//        }
//
//        @Override
//        public void render(Renderer2D renderer2D) {
//            renderer2D.setColor(tint);
//            renderer2D.drawRectangleFilled(60,30,x,y,deg,sclX,sclY);
//        }
//
//        @Override
//        public void renderPreview(Renderer2D renderer2D, float toolX, float toolY) {
//            renderer2D.setColor(tint);
//            renderer2D.drawRectangleFilled(60,30,x + toolX,y + toolY,deg,sclX,sclY);
//        }
//
//    }

}
