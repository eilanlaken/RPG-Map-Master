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

import java.util.Comparator;

// This will be a basic brush.
// Other brushes will extend it
public class ToolTokensTrees extends Tool {

    private final TexturePack atlas;
    private final TextureRegion region;
    private final Array<Token> tokensPreview = new Array<>();
    private final Array<Token> alreadyCreatedTokens = new Array<>();

    public boolean addTrunk = true;
    public boolean addLeaves = true;
    public boolean addFruit = false;
    public Type currentType = Type.TREE_CIRCULAR;

    public ToolTokensTrees(RPGMapMakerScene scene) {
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
                spawnTokens(true);
                refillCircleWithTokens();
            }
            return;
        }

        if (brushMode == BrushMode.LINE && free) {
            if (leftClicked) {
                lineStart.x = x;
                lineStart.y = y;
                refillLineWithTokens();
                free = false;
            }
            return;
        }
        if (brushMode == BrushMode.LINE && !free) {
            if (mouseMoved) refillLineWithTokens();
            else if (leftClicked) {
                spawnTokens(false);
                tokensPreview.clear();
                free = true;
            }
            return;
        }

        if (brushMode == BrushMode.POLYGON && free) {
            if (leftClicked) {
                polygonPoints.add(new Vector2(x, y));
                free = false;
            }
            return;
        }
        if (brushMode == BrushMode.POLYGON && !free) {
            if (mouseMoved && polygonPoints.size >= 2) refillPolygonWithTokens();
            else if (leftClicked) {
                refillPolygonWithTokens();

                Vector2 p = new Vector2(x, y); // need to test intersections etc.
                polygonPoints.add(p);
                if (polygonPoints.size < 4) {
                    return;
                }

                if (Vector2.dst(p, polygonPoints.first()) <= 20) {
                    spawnTokens(false);
                    tokensPreview.clear();
                    polygonPoints.clear();
                    free = true;
                }

            }
            return;
        }
    }

    private void spawnTokens(boolean useBrushOffset) {
        map.getAllTokens(currentType, alreadyCreatedTokens);
        System.out.println(alreadyCreatedTokens.size);

        float offsetX = useBrushOffset ? x : 0;
        float offsetY = useBrushOffset ? y : 0;

        for (Token token : tokensPreview) {
            Vector2 position = new Vector2(token.x + x, token.y + y);
            float minDistance = Float.POSITIVE_INFINITY;
            for (Token mapToken : alreadyCreatedTokens) {
                float distanceSquared = Vector2.dst2(position.x, position.y, mapToken.x, mapToken.y);
                minDistance = Math.min(distanceSquared, minDistance);
            }
            minDistance = (float) Math.sqrt(minDistance);
            if (minDistance < Tool.MINIMUM_TOKEN_SPACING * region.originalWidth) continue;

            CommandTokenCreate createToken = new CommandTokenCreate(
                    3,
                    token.x + offsetX, token.y + offsetY, token.deg, sclX, sclY, true,
                    token.regions
            );
            createToken.tokenType = currentType;
            createToken.sourceTool = this.getClass();
            createToken.tint = token.tint;
            map.addCommand(createToken);
        }
    }

    @Override
    public void renderToolOverlay(Renderer2D renderer2D, float x, float y) {
        // render tool overlay when brush mode is set to points
        if (brushMode == BrushMode.POINT && free) {
            for (Token token : tokensPreview) {
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
            for (Token token : tokensPreview) {
                token.render(renderer2D);
            }

            float dx = x - lineStart.x;
            float dy = y - lineStart.y;
            float angle = MathUtils.radiansToDegrees * MathUtils.atan2(dy, dx);
            renderer2D.setColor(1,0,0,0.2f);
            renderer2D.drawCircleFilled(Math.max(spreadRadius, 5), 10, 180, lineStart.x, lineStart.y, angle + 90,1,1);
            renderer2D.drawLineFilled(lineStart.x, lineStart.y, x, y, 2 * Math.max(spreadRadius, 5));
            renderer2D.drawCircleFilled(Math.max(spreadRadius, 5), 10, 180, x, y, angle - 90,1,1);
        }

        // render tool overlay when brush mode is set to polygons
        if (brushMode == BrushMode.POLYGON && free) {
            renderer2D.setColor(Color.RED);
            renderer2D.drawCircleFilled(10,5, x, y, 0, 1,1);
            renderer2D.setColor(Color.WHITE);
        }
        if (brushMode == BrushMode.POLYGON && !free) {
            if (polygonPoints.isEmpty()) return;

            for (Token token : tokensPreview) {
                token.render(renderer2D);
            }

            renderer2D.setColor(Color.BLACK);
            renderer2D.drawCircleBorder(15, 5, 10, polygonPoints.first().x, polygonPoints.first().y, 0,1,1);
            renderer2D.setColor(Color.RED);
            for (int i = 0; i < polygonPoints.size; i++) {
                Vector2 p = polygonPoints.get(i);
                renderer2D.drawCircleFilled(5, 5, p.x, p.y, 0, 1, 1);
            }
            for (int i = 0; i < polygonPoints.size - 1; i++) {
                Vector2 p1 = polygonPoints.get(i);
                Vector2 p2 = polygonPoints.get(i + 1);
                renderer2D.drawLineThin(p1.x, p1.y, p2.x, p2.y);
            }
            renderer2D.drawLineThin(polygonPoints.last().x, polygonPoints.last().y, x, y);
            renderer2D.drawLineThin(x, y, polygonPoints.first().x, polygonPoints.first().y);
            renderer2D.setColor(Color.WHITE);
        }

        renderer2D.setColor(Color.WHITE); // just to be sure, reset color back to white.
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

            Token token = new Token(3, offsetX, offsetY, 0, 1,1, getRegions());
            //token.tint = Color.randomOpaque();
            tokensPreview.add(token);
        }

        tokensPreview.sort(Comparator.comparingInt(o -> -(int) o.y));
    }

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

        float degTilt =  tokensAngleMatchLine ? MathUtils.atan2(dy, dx) * MathUtils.radiansToDegrees - 90 : 0;

        // first half circle (at lineStar)
        for (int i = 0; i < batchCount; i++) {
            float radius = MathUtils.randomUniformFloat(0,1) * spreadRadius; // distance from center
            float angle  = angleOffset + i * half_slice + MathUtils.randomUniformFloat(0,1);
            float offsetX = MathUtils.cosRad(angle) * radius;
            float offsetY = MathUtils.sinRad(angle) * radius;
            Token token = new Token(3, lineStart.x + offsetX, lineStart.y + offsetY, degTilt, 1,1, getRegions());
            //token.tint = Color.randomOpaque();
            tokensPreview.add(token);
        }

        // fill rectangular area
        float width = Vector2.dst(lineStart.x, lineStart.y, x, y);
        Vector2 norm = new Vector2(dx, dy).nor();
        Vector2 prep = new Vector2(norm).rotate90(1);
        Vector2 offset = new Vector2();
        for (int i = 0; i < rectBatchCount; i++) {
            float normScale = MathUtils.randomUniformFloat(0, width);
            float prepScale = MathUtils.randomUniformFloat(-spreadRadius, spreadRadius);
            offset.set(norm.x * normScale, norm.y * normScale);
            offset.add(prep.x * prepScale, prep.y * prepScale);
            Token token = new Token(3, lineStart.x + offset.x, lineStart.y + offset.y, degTilt, 1,1, getRegions());
            //token.tint = Color.randomOpaque();
            tokensPreview.add(token);
        }

        // second half circle (at lineEnd)
        for (int i = 0; i < batchCount; i++) {
            float radius = MathUtils.randomUniformFloat(0,1) * spreadRadius; // distance from center
            float angle  = angleOffset + i * half_slice + MathUtils.randomUniformFloat(0,1) + MathUtils.PI;
            float offsetX = MathUtils.cosRad(angle) * radius;
            float offsetY = MathUtils.sinRad(angle) * radius;
            Token token = new Token(3, x + offsetX, y + offsetY, degTilt, 1,1, getRegions());
            //token.tint = Color.randomOpaque();
            tokensPreview.add(token);
        }

        tokensPreview.sort(Comparator.comparingInt(o -> -(int) o.y));
    }

    private void refillPolygonWithTokens() {
        if (polygonPoints.size < 2) return;
        tokensPreview.clear();

        Vector2 p1 = polygonPoints.get(polygonPoints.size - 1);
        Vector2 p2 = new Vector2(x, y);
        Vector2 intersection = new Vector2();
        int intersectionIndex = -1;
        for (int i = 0; i < polygonPoints.size - 3; i++) {
            Vector2 a = polygonPoints.get(i);
            Vector2 b = polygonPoints.get(i + 1);
            int result = MathUtils.segmentsIntersection(a, b, p1, p2, intersection);
            if (result != 0 && !intersection.equals(b)) continue;
            intersectionIndex = i + 1;
            break;
        }

        // poly points is the formed polygon.
        // if there is an intersection, then it is the polygon starting from the intersection point all the way to the mouse.
        // if there is no intersection, then it is the polygon starting at 0 to the mouse.
        Array<Vector2> polyPoints = new Array<>(true, 5);
        if (intersectionIndex == -1) { // no intersection
            for (Vector2 point : polygonPoints) polyPoints.add(point);
            polyPoints.add(new Vector2(x, y));
        } else {
            for (int i = intersectionIndex; i < polygonPoints.size - 1; i++) {
                polyPoints.add(polygonPoints.get(i));
            }
            polyPoints.add(intersection);
        }

        // calculate bounding box
        Vector2 bottomLeftCorner = new Vector2(Float.MAX_VALUE, Float.MAX_VALUE);
        Vector2 topRightCorner = new Vector2(Float.MIN_VALUE, Float.MIN_VALUE);
        for (Vector2 v : polyPoints) {
            if (v.x < bottomLeftCorner.x) bottomLeftCorner.x = v.x;
            if (v.y < bottomLeftCorner.y) bottomLeftCorner.y = v.y;
            if (v.x > topRightCorner.x) topRightCorner.x = v.x;
            if (v.y > topRightCorner.y) topRightCorner.y = v.y;
        }

        float stepSizePixels_x = spreadRadius; // token width * 0.5
        float stepSizePixels_y = 25; // token height * 0.5
        for (float rect_x = bottomLeftCorner.x; rect_x < topRightCorner.x; rect_x += stepSizePixels_x) {
            for (float rect_y = bottomLeftCorner.y; rect_y < topRightCorner.y; rect_y += stepSizePixels_y) {
                boolean contained = MathUtils.polygonContainsPoint(polyPoints, rect_x, rect_y);
                if (contained) {
                    Token token = new Token(3, rect_x + MathUtils.randomUniformFloat(-5,5), rect_y  + MathUtils.randomUniformFloat(-5,5), 0, 1,1, atlas.getRegion("assets/textures-layer-3/debug_rect.png"));
                    //token.tint = Color.randomOpaque();
                    tokensPreview.add(token);
                }
            }
        }

        tokensPreview.sort(Comparator.comparingInt(o -> -(int) o.y));
    }

    @Override
    protected TextureRegion[] getRegions() {
        String prefix = "assets/textures-layer-3/" + currentType.name().toLowerCase();
        TextureRegion leaves = atlas.getRegion(prefix + "_" + MathUtils.randomUniformInt(0, 6) + ".png"); // currently, hard coded value "6"
        TextureRegion fruits = atlas.getRegion(prefix + "_fruits_red" + ".png"); // currently,hard coded "red"
        TextureRegion trunk = atlas.getRegion(prefix + "_trunk_" + MathUtils.randomUniformInt(0, 6) + ".png"); // currently, hard coded value "6";

        TextureRegion[] regions = new TextureRegion[3];
        regions[0] = addLeaves ? leaves : null;
        regions[1] = addFruit ? fruits : null;
        regions[2] = addTrunk ? trunk : null;
        return regions;
    }

    @Override
    public void activate() {
        System.out.println("active - " + getName());
        tokensPreview.clear();
        polygonPoints.clear();
        if (brushMode == BrushMode.POINT) {
            refillCircleWithTokens();
        }
    }

    @Override
    public void deactivate() {
        tokensPreview.clear();
        polygonPoints.clear();
    }

    @Override
    public String getName() {
        return "Trees Tool";
    }

    public enum Type {

        TREE_BUSH,
        TREE_CIRCULAR,
        TREE_CONIFER,
        TREE_CYPRESS,
        TREE_GLOBOSE,
        TREE_HIGH,
        TREE_REGULAR,
        TREE_SPARSE,

    }

}
