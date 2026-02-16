package com.heavybox.jtix.z;

import com.heavybox.jtix.RPGMapMakerScene;
import com.heavybox.jtix.assets.Assets;
import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.collections.Collections;
import com.heavybox.jtix.graphics.*;
import com.heavybox.jtix.input.Input;
import com.heavybox.jtix.input.Keyboard;
import com.heavybox.jtix.input.Mouse;
import com.heavybox.jtix.math.MathUtils;
import com.heavybox.jtix.math.Vector2;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

// TODO: determine spacing based on brush
public class ToolBrush_Nature extends Tool {

    private final TexturePack atlas;
    private final Array<Token> tokensPreview = new Array<>();
    private final Array<Token> alreadyCreatedTokens = new Array<>();
    private final Set<Token> tokensToDelete = new HashSet<>();

    public boolean addTrunk = true;
    public boolean addLeaves = true;
    public boolean addFruit = false;
    public Type currentType = Type.TREE_CIRCULAR;

    public ToolBrush_Nature(RPGMapMakerScene scene) {
        super(scene);
        atlas = Assets.get("assets/texture-packs/layer_3.yml");

        sclX = 0.5f;
        sclY = 0.5f;

        sclX = 1f / 6;
        sclY = 1f / 6;
    }

    @Override
    protected void onSetShape() {
        tokensPreview.clear();
        alreadyCreatedTokens.clear();
        onSetParameter();
    }

    // TODO
    @Override
    protected void onSetMode() {
        if (mode == Mode.SUB) {
            setShape(Shape.CIRCLE);
        }
    }

    @Override
    protected void onSetParameter() {
        tokensPreview.clear();
        if (shape == Shape.CIRCLE) refillCircleWithTokens();
        if (shape == Shape.LINE) refillLineWithTokens();
        if (shape == Shape.POLYGON) refillPolygonWithTokens();
    }

    @Override
    public void update(float delta) {
        // handle mode switching, clicking actions etc.
        // TODO: take input layers into account
        boolean leftShiftJustPressed = Input.keyboard.isKeyJustPressed(Keyboard.Key.LEFT_SHIFT);
        boolean mouseMoved = Input.mouse.moved();
        boolean leftPressedAndMoved = Input.mouse.isButtonPressed(Mouse.Button.LEFT) && mouseMoved;
        boolean leftClicked = Input.mouse.isButtonClicked(Mouse.Button.LEFT);
        boolean rightClicked = Input.mouse.isButtonClicked(Mouse.Button.RIGHT);
        boolean sKeyPressed = Input.keyboard.isKeyPressed(Keyboard.Key.S);
        boolean spaceKeyPressed = Input.keyboard.isKeyJustPressed(Keyboard.Key.SPACE);
        float dy = Input.mouse.getYDelta();

        // brush settings - TODO

        if (leftShiftJustPressed) {
            setShape(Collections.enumNext(shape));
        }

        if (spaceKeyPressed) {
            mode = Collections.enumNext(mode);
            onSetMode();
        }

        if (rightClicked) {
            currentType = Collections.enumNext(currentType);
            onSetParameter();
            return;
        }

        if (Input.keyboard.isKeyJustPressed(Keyboard.Key.EQUAL)) {
            setScale(sclX * 2, sclY * 2);
            return;
        } else if (Input.keyboard.isKeyJustPressed(Keyboard.Key.MINUS)) {
            setScale(sclX * 0.5f, sclY * 0.5f);
            return;
        } else if (Input.keyboard.isKeyJustPressed(Keyboard.Key.BACKSPACE)) {
            setScale(sclX * -1.0f, sclY);
            return;
        }

        if (sKeyPressed && dy != 0) {
            float deltaSpreadRadius = -dy / 1000 * Graphics.getWindowHeight();
            setSpreadRadius(spreadRadius + deltaSpreadRadius);
            return;
        }

        // brush actions
        if (mode == Mode.SUB) {

            tokensToDelete.clear();
            if (leftClicked || leftPressedAndMoved) {
                map.getAllTokensInCircle(currentType, x, y, spreadRadius * sclX, tokensToDelete);
                deleteTokens();
            }

            return;
        }

        if (mode == Mode.ADD) {
            if (shape == Shape.CIRCLE) {
                if (leftClicked || leftPressedAndMoved) {
                    spawnTokens(true);
                    refillCircleWithTokens();
                    return;
                }
                return;
            }

            if (shape == Shape.LINE && free) {
                if (leftClicked) {
                    lineStart.x = x;
                    lineStart.y = y;
                    refillLineWithTokens();
                    free = false;
                }
                return;
            }
            if (shape == Shape.LINE && !free) {
                if (mouseMoved) refillLineWithTokens();
                else if (leftClicked) {
                    spawnTokens(false);
                    tokensPreview.clear();
                    free = true;
                }
                return;
            }

            if (shape == Shape.POLYGON && free) {
                if (leftClicked) {
                    polygonPoints.add(new Vector2(x, y));
                    free = false;
                }
                return;
            }
            if (shape == Shape.POLYGON && !free) {
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
            }
        }
    }

    private void deleteTokens() {
        for (Token token : tokensToDelete) {
            CommandTokenDelete cmd = new CommandTokenDelete(token.tokenType, token.layer, token.transforms[0].x, token.transforms[0].y, false);
            map.addCommand(cmd);
        }
        tokensToDelete.clear();
    }

    private void spawnTokens(boolean useBrushOffset) {
        map.getAllTokens(currentType, alreadyCreatedTokens);

        float offsetX = useBrushOffset ? x : 0;
        float offsetY = useBrushOffset ? y : 0;

        for (Token token : tokensPreview) {
            Vector2 position = new Vector2(token.transforms[0].x + x, token.transforms[0].y + y);
            float minDistance = Float.POSITIVE_INFINITY;
            for (Token mapToken : alreadyCreatedTokens) {
                float distanceSquared = Vector2.dst2(position.x, position.y, mapToken.transforms[0].x, mapToken.transforms[0].y);
                minDistance = Math.min(distanceSquared, minDistance);
            }
            minDistance = (float) Math.sqrt(minDistance);
            if (minDistance < getSpacingX()) continue;

            CommandTokenCreate createToken = new CommandTokenCreate(
                    3,
                    token.transforms[0].x + offsetX, token.transforms[0].y + offsetY,
                    token.transforms[0].deg,
                    token.transforms[0].sclX, token.transforms[0].sclY, true,
                    token.regions
            );
            createToken.tokenType = currentType;
            createToken.tint = token.tint;
            map.addCommand(createToken);
        }
    }

    @Override
    public void renderToolOverlay(Renderer2D renderer2D, float x, float y) {
        float radius = Math.abs(spreadRadius * sclX);

        Color color = mode == Mode.ADD ? Color.GREEN : Color.RED;
        boolean renderPreviewTokens = mode == Mode.ADD;

        // render tool overlay when brush mode is set to points
        if (shape == Shape.CIRCLE && free) {
            if (renderPreviewTokens) {
                for (Token token : tokensPreview) {
                    token.renderPreview(renderer2D, x, y);
                }
            }
            renderer2D.setColor(color);
            renderer2D.drawCircleThin(Math.max(radius, 5), 10, x, y, 0,1,1);
        }
        if (shape == Shape.CIRCLE && !free) {

        }

        // render tool overlay when brush mode is set to lines
        if (shape == Shape.LINE && free) {
            renderer2D.setColor(color);
            renderer2D.drawCircleThin(Math.max(radius, 5), 10, x, y, 0,1,1);
        }
        if (shape == Shape.LINE && !free) {
            if (renderPreviewTokens) {
                for (Token token : tokensPreview) {
                    token.render(renderer2D);
                }
            }

            float dx = x - lineStart.x;
            float dy = y - lineStart.y;
            float angle = MathUtils.radiansToDegrees * MathUtils.atan2(dy, dx);
            renderer2D.setColor(1,0,0,0.2f);
            renderer2D.drawCircleFilled(Math.max(radius, 5), 10, 180, lineStart.x, lineStart.y, angle + 90,1,1);
            renderer2D.drawLineFilled(lineStart.x, lineStart.y, x, y, 2 * Math.max(radius, 5));
            renderer2D.drawCircleFilled(Math.max(radius, 5), 10, 180, x, y, angle - 90,1,1);
        }

        // render tool overlay when brush mode is set to polygons
        if (shape == Shape.POLYGON && free) {
            renderer2D.setColor(color);
            renderer2D.drawCircleFilled(10,5, x, y, 0, 1,1);
            renderer2D.setColor(Color.WHITE);
        }
        if (shape == Shape.POLYGON && !free) {
            if (polygonPoints.isEmpty()) return;

            if (renderPreviewTokens) {
                for (Token token : tokensPreview) {
                    token.render(renderer2D);
                }
            }

            renderer2D.setColor(Color.BLACK);
            renderer2D.drawCircleBorder(15, 5, 10, polygonPoints.first().x, polygonPoints.first().y, 0,1,1);
            renderer2D.setColor(color);
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
        }

        renderer2D.setColor(Color.WHITE); // just to be sure, reset color back to white.
    }

    // TODO - filter against self. If a token is too close to one already in the circle, don't add it.
    private void refillCircleWithTokens() {
        tokensPreview.clear();
        float r = spreadRadius * sclX;
        int batchCount = getBatchCount(MathUtils.PI * r * r);
        float slice = 2.0f * MathUtils.PI / batchCount;

        float dx = x - lineStart.x;
        float dy = y - lineStart.y;
        float angleOffset = MathUtils.radiansToDegrees * MathUtils.atan2(dy, dx);
        for (int i = 0; i < batchCount; i++) {
            float radius = MathUtils.randomUniformFloat(0,1) * spreadRadius * sclX; // distance from center
            float angle  = i * slice + MathUtils.randomUniformFloat(0,1) * slice + angleOffset;

            float offsetX = MathUtils.cosRad(angle) * radius;
            float offsetY = MathUtils.sinRad(angle) * radius;

            Token token = new Token(3, offsetX, offsetY, 0, sclX,sclY, getRegions());
            //token.tint = Color.randomOpaque();
            tokensPreview.add(token);
        }

        tokensPreview.sort(Comparator.comparingInt(o -> -(int) o.minY));
    }

    private void refillLineWithTokens() {
        tokensPreview.clear();

        // first, let's estimate the size of the batch.
        float rectArea = 2 * spreadRadius * sclX * Vector2.dst(lineStart.x, lineStart.y, x, y);
        float factor = rectArea / (MathUtils.PI * spreadRadius * spreadRadius);
        //int rectBatchCount = (int) (factor * batchCount);
        int rectBatchCount = getBatchCount(rectArea);
        float r = spreadRadius * sclX;
        int halfCircleBatchCount = getBatchCount(MathUtils.PI * r * r) / 2;

        float dx = x - lineStart.x;
        float dy = y - lineStart.y;
        float angleOffset = MathUtils.atan2(dy, dx) + MathUtils.PI_HALF;
        float half_slice = MathUtils.PI / halfCircleBatchCount;

        float degTilt =  tokensAngleMatchLine ? MathUtils.atan2(dy, dx) * MathUtils.radiansToDegrees - 90 : 0;

        // first half circle (at lineStar)
        for (int i = 0; i < halfCircleBatchCount; i++) {
            float radius = MathUtils.randomUniformFloat(0,1) * spreadRadius * sclX; // distance from center
            float angle  = angleOffset + i * half_slice + MathUtils.randomUniformFloat(0,1);
            float offsetX = MathUtils.cosRad(angle) * radius;
            float offsetY = MathUtils.sinRad(angle) * radius;
            Token token = new Token(3, lineStart.x + offsetX, lineStart.y + offsetY, degTilt, sclX,sclY, getRegions());
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
            float prepScale = MathUtils.randomUniformFloat(-spreadRadius, spreadRadius) * sclX;
            offset.set(norm.x * normScale, norm.y * normScale);
            offset.add(prep.x * prepScale, prep.y * prepScale);
            Token token = new Token(3, lineStart.x + offset.x, lineStart.y + offset.y, degTilt, sclX,sclY, getRegions());
            //token.tint = Color.randomOpaque();
            tokensPreview.add(token);
        }

        // second half circle (at lineEnd)
        for (int i = 0; i < halfCircleBatchCount; i++) {
            float radius = MathUtils.randomUniformFloat(0,1) * spreadRadius * sclX; // distance from center
            float angle  = angleOffset + i * half_slice + MathUtils.randomUniformFloat(0,1) + MathUtils.PI;
            float offsetX = MathUtils.cosRad(angle) * radius;
            float offsetY = MathUtils.sinRad(angle) * radius;
            Token token = new Token(3, x + offsetX, y + offsetY, degTilt, sclX,sclY, getRegions());
            tokensPreview.add(token);
        }

        tokensPreview.sort(Comparator.comparingInt(o -> -(int) o.minY));
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

        float minX = Float.POSITIVE_INFINITY;
        float minY = Float.POSITIVE_INFINITY;
        float maxX = Float.NEGATIVE_INFINITY;
        float maxY = Float.NEGATIVE_INFINITY;
        for (Vector2 v : polyPoints) {
            if (v.x < minX) minX = v.x;
            if (v.y < minY) minY = v.y;
            if (v.x > maxX) maxX = v.x;
            if (v.y > maxY) maxY = v.y;
        }
        Vector2 bottomLeftCorner  = new Vector2(minX, minY);
        Vector2 bottomRightCorner = new Vector2(maxX, minY);
        Vector2 topRightCorner    = new Vector2(maxX, maxY);
        Vector2 topLeftCorner     = new Vector2(minX, maxY);

        float bounding_box_area = Math.abs(topRightCorner.x - topLeftCorner.x) * Math.abs(topRightCorner.y - bottomRightCorner.y);
        int batchCount = getBatchCount(bounding_box_area);
        float w = Math.abs(topRightCorner.x - topLeftCorner.x);
        float h = Math.abs(topRightCorner.y - bottomRightCorner.y);
        float aspect = w / h;
        int cols = Math.max(1, (int)Math.round(Math.sqrt(batchCount * aspect)));
        int rows = Math.max(1, (int) Math.ceil((float) batchCount / cols));
        float stepX = w / cols;
        float stepY = h / rows;

        for (float rect_x = bottomLeftCorner.x; rect_x < topRightCorner.x; rect_x += stepX) {
            for (float rect_y = bottomLeftCorner.y; rect_y < topRightCorner.y; rect_y += stepY) {
                boolean contained = MathUtils.polygonContainsPoint(polyPoints, rect_x, rect_y);
                if (contained) {
                    float spacing = getSpacingX();
                    float randomOffset_x = MathUtils.randomUniformFloat(-spacing, spacing);
                    float randomOffset_y = MathUtils.randomUniformFloat(-spacing, spacing);;
                    System.out.println(randomOffset_x);
                    Token token = new Token(3, rect_x + randomOffset_x, rect_y  + randomOffset_y, 0, sclX,sclY, getRegions());
                    //token.tint = Color.randomOpaque();
                    tokensPreview.add(token);
                }
            }
        }

        tokensPreview.sort(Comparator.comparingInt(o -> -(int) o.minY));
    }

    @Override
    protected TextureRegion[] getRegions() {
        String prefix = "assets/textures-layer-3/" + currentType.name().toLowerCase();

        if (currentType.name().startsWith("PLANT_FLOWER")) {
            return new TextureRegion[] {atlas.getRegion(prefix + "_" + MathUtils.randomUniformInt(0, 6) + ".png")};
        }

        TextureRegion leaves = null;
        try {
           leaves = atlas.getRegion(prefix + "_" + MathUtils.randomUniformInt(0, 6) + ".png"); // currently, hard coded value "6"
        } catch (Exception ignored) {}

        TextureRegion fruits = null;
        try {
            fruits = atlas.getRegion(prefix + "_fruits_red" + ".png"); // currently,hard coded "red"
        } catch (Exception ignored) {

        }

        TextureRegion trunk = null;
        try {
            trunk = atlas.getRegion(prefix + "_trunk_" + MathUtils.randomUniformInt(0, 6) + ".png"); // currently, hard coded value "6";
        } catch (Exception ignored) {

        }

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
        if (shape == Shape.CIRCLE) {
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

        PLANT_FLOWER_DAISY,
        PLANT_FLOWER_ROSE,
        PLANT_FLOWER_SCORPION,
        PLANT_FLOWER_SUNFLOWER,
        PLANT_FLOWER_TULIP,

    }

}
