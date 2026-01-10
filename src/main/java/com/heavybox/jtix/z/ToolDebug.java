package com.heavybox.jtix.z;

import com.heavybox.jtix.RPGMapMakerScene;
import com.heavybox.jtix.assets.Assets;
import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.collections.Collections;
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
    protected void onSwitchMode() {
        tokensPreview.clear();
        alreadyCreatedTokens.clear();
    }

    @Override
    public void update(float delta) {
        // handle mode switching, clicking actions etc.
        // TODO: take input layers into account
        boolean leftShiftJustPressed = Input.keyboard.isKeyJustPressed(Keyboard.Key.LEFT_SHIFT);
        boolean mouseMoved = Input.mouse.moved();
        boolean leftPressedAndMoved = Input.mouse.isButtonPressed(Mouse.Button.LEFT) && mouseMoved;
        boolean leftClicked = Input.mouse.isButtonClicked(Mouse.Button.LEFT);

        if (brushMode == BrushMode.POINT) {
            if (leftClicked || leftPressedAndMoved) {
                spawnTokens();
            }
            return;
        }

        if (brushMode == BrushMode.LINE && free) {
            if (leftClicked) {
                lineStart.x = x;
                lineStart.y = y;
                free = false;
            }
            return;
        }
        if (brushMode == BrushMode.LINE && !free) {
            if (mouseMoved) refillLineWithTokens();
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
            renderer2D.setColor(Color.RED);
            renderer2D.drawCircleThin(Math.max(spreadRadius, 5), 10, x, y, 0,1,1);
        }
        if (brushMode == BrushMode.LINE && !free) {
            float dx = x - lineStart.x;
            float dy = y - lineStart.y;
            float angle = MathUtils.radiansToDegrees * MathUtils.atan2(dy, dx);
            renderer2D.setColor(1,0,0,0.2f);
            renderer2D.drawCircleFilled(Math.max(spreadRadius, 5), 10, 180, lineStart.x, lineStart.y, angle + 90,1,1);
            renderer2D.drawLineFilled(lineStart.x, lineStart.y, x, y, 2 * Math.max(spreadRadius, 5));
            renderer2D.drawCircleFilled(Math.max(spreadRadius, 5), 10, 180, x, y, angle - 90,1,1);

            for (MapToken token : tokensPreview) {
                token.render(renderer2D);
            }
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

        float dx = x - lineStart.x;
        float dy = y - lineStart.y;
        float angleOffset = MathUtils.radiansToDegrees * MathUtils.atan2(dy, dx);
        for (int i = 0; i < batchCount; i++) {
            float radius = MathUtils.randomUniformFloat(0,1) * spreadRadius; // distance from center
            float angle  = i * slice + MathUtils.randomUniformFloat(0,1) * slice + angleOffset;

            float offsetX = MathUtils.cosRad(angle) * radius;
            float offsetY = MathUtils.sinRad(angle) * radius;

            MapToken token = new MapToken(3, offsetX, offsetY, 0, 1,1, atlas.getRegion("assets/textures-layer-3/debug_rect.png"));
            token.tint = Color.randomOpaque();
            tokensPreview.add(token);
        }
    }

    // TODO
    private void refillLineWithTokens() {
        tokensPreview.clear();

        // first, let's estimate the size of the batch.
        float rectArea = 2 * spreadRadius * Vector2.dst(lineStart.x, lineStart.y, x, y);
        float factor = rectArea / (MathUtils.PI * spreadRadius * spreadRadius);
        int rectBatchCount = (int) (factor * batchCount);

        float dx = x - lineStart.x;
        float dy = y - lineStart.y;
        float angleOffset = MathUtils.atan2(dy, dx) + MathUtils.PI_HALF;
        float half_slice = MathUtils.PI / batchCount;

        // first half circle (at lineStar)
        for (int i = 0; i < batchCount; i++) {
            float radius = MathUtils.randomUniformFloat(0,1) * spreadRadius; // distance from center
            float angle  = angleOffset + i * half_slice + MathUtils.randomUniformFloat(0,1);

            float offsetX = MathUtils.cosRad(angle) * radius;
            float offsetY = MathUtils.sinRad(angle) * radius;

            MapToken token = new MapToken(3, lineStart.x + offsetX, lineStart.y + offsetY, 0, 1,1, atlas.getRegion("assets/textures-layer-3/debug_rect.png"));
            token.tint = Color.randomOpaque();
            tokensPreview.add(token);
        }

        // fill rectangular area
        float width = Vector2.dst(lineStart.x, lineStart.y, x, y);
        float degTilt = MathUtils.atan2(dy, dx) * MathUtils.radiansToDegrees;
        Vector2 norm = new Vector2(dx, dy).nor();
        Vector2 prep = new Vector2(norm).rotate90(1);
        Vector2 offset = new Vector2();
        for (int i = 0; i < rectBatchCount; i++) {
            float normScale = MathUtils.randomUniformFloat(0, width);
            float prepScale = MathUtils.randomUniformFloat(-spreadRadius, spreadRadius);
            offset.set(norm.x * normScale, norm.y * normScale);
            offset.add(prep.x * prepScale, prep.y * prepScale);
            MapToken token = new MapToken(3, lineStart.x + offset.x, lineStart.y + offset.y, 0, 1,1, atlas.getRegion("assets/textures-layer-3/debug_rect.png"));
            token.tint = Color.randomOpaque();
            tokensPreview.add(token);
        }

        // second half circle (at lineEnd)
        for (int i = 0; i < batchCount; i++) {
            float radius = MathUtils.randomUniformFloat(0,1) * spreadRadius; // distance from center
            float angle  = angleOffset + i * half_slice + MathUtils.randomUniformFloat(0,1) + MathUtils.PI;

            float offsetX = MathUtils.cosRad(angle) * radius;
            float offsetY = MathUtils.sinRad(angle) * radius;

            MapToken token = new MapToken(3, x + offsetX, y + offsetY, 0, 1,1, atlas.getRegion("assets/textures-layer-3/debug_rect.png"));
            token.tint = Color.randomOpaque();
            tokensPreview.add(token);
        }
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
