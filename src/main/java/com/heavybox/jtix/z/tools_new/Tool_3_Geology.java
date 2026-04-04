package com.heavybox.jtix.z.tools_new;

import com.heavybox.jtix.RPGMapMakerScene;
import com.heavybox.jtix.assets.Assets;
import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.collections.ArrayChar;
import com.heavybox.jtix.collections.ArrayFloat;
import com.heavybox.jtix.collections.Collections;
import com.heavybox.jtix.graphics.*;
import com.heavybox.jtix.input.Input;
import com.heavybox.jtix.input.Keyboard;
import com.heavybox.jtix.input.Mouse;
import com.heavybox.jtix.math.MathUtils;
import com.heavybox.jtix.math.Shape2DPolygon;
import com.heavybox.jtix.math.Vector2;
import com.heavybox.jtix.z.*;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

public class Tool_3_Geology extends Tool_new {

    private final TexturePack atlas;
    private TextureRegion[] currentRegions;

    private Tool.Mode currentMode;
    private Tool.Shape currentShape;
    private float spacing = 1.0f;
    private final Set<Token> tokensToDelete = new HashSet<>();
    private final Array<Token> tokensPreview = new Array<>();
    private final Array<Token> alreadyCreatedTokens = new Array<>();
    private boolean angleFollowPath = false;
    private boolean fillShape = false;

    // point mode

    // circle mode
    private float circle_spreadRadius = 200;

    // line mode
    private boolean line_free = true;
    private final Vector2 line_start = new Vector2();
    private final Vector2 line_end = new Vector2();

    // polygon mode
    private boolean polygon_free = true;
    private final Array<Vector2> polygon_points = new Array<>(true, 10);
    private final ArrayFloat polygon_flatTmp = new ArrayFloat(true, 10);
    private final Vector2 polygon_BottomLeft = new Vector2();
    private final Vector2 polygon_TopRight = new Vector2();
    private Shape2DPolygon polygon_shape;

    // trees specifics
    public Type currentType = Type.BOULDER_PLAIN_BIG;

    public Tool_3_Geology(final RPGMapMakerScene scene) {
        super(scene);
        atlas = Assets.get("assets/texture-packs/layer_3.yml");

        sclX = 1f / 3;
        sclY = 1f / 3;

        currentShape = Tool.Shape.POINT;
        currentMode = Tool.Mode.ADD;
        currentRegions = getRegions();
        refillWithTokens();
    }

    protected int getBatchCountArea(float area) {
        float maxExtent = 0;
        for (TextureRegion r : currentRegions) {
            if (r == null) continue;
            maxExtent = Math.max(maxExtent, r.packedWidth);
        }
        float d = spacing * maxExtent * 0.5f * Math.abs(sclX); // center spacing
        if (d == 0) return 1;
        return (int) (area / (d * d));
    }

    protected int getBatchCountLength(float length) {
        float maxExtent = 0;
        for (TextureRegion r : currentRegions) {
            if (r == null) continue;
            maxExtent = Math.max(maxExtent, r.packedWidth);
        }
        float d = spacing * maxExtent * 0.5f * Math.abs(sclX); // center spacing
        if (d == 0) return 1;
        return (int) (length / d);
    }

    @Override
    void onChangeParameters() {
        refillWithTokens();
        currentRegions = getRegions();
    }

    private void refillWithTokens() {
        if (currentShape == Tool.Shape.POINT)        point_refillWithTokens();
        else if (currentShape == Tool.Shape.CIRCLE)  circle_refillWithTokens();
        else if (currentShape == Tool.Shape.LINE)    line_refillWithTokens();
        else if (currentShape == Tool.Shape.POLYGON) polygon_refillWithTokens();
    }

    private void point_refillWithTokens() {
        tokensPreview.clear();
        Token token = new Token(3, 0, 0, 0, sclX,sclY, getRegions());
        tokensPreview.add(token);
    }

    // TODO - filter against self. If a token is too close to one already in the circle, don't add it.
    private void circle_refillWithTokens() {
        tokensPreview.clear();
        if (fillShape) {
            int batchCount = getBatchCountArea(MathUtils.PI * circle_spreadRadius * circle_spreadRadius);
            for (int i = 0; i < batchCount; i++) {
                float r = circle_spreadRadius * MathUtils.randomUniformFloat(0,1);
                float angle = MathUtils.randomUniformFloat(0,360);
                float offsetX = MathUtils.cosDeg(angle) * r;
                float offsetY = MathUtils.sinDeg(angle) * r;

                // filter against added tokens
                Vector2 position = new Vector2(offsetX, offsetY);
                float minDistance = Float.POSITIVE_INFINITY;
                for (Token mapToken : tokensPreview) {
                    float distanceSquared = Vector2.dst2(position.x, position.y, mapToken.transform.x, mapToken.transform.y);
                    minDistance = Math.min(distanceSquared, minDistance);
                }
                minDistance = (float) Math.sqrt(minDistance);
                if (minDistance < getMinSpacing()) continue;

                float deg = this.deg + (!angleFollowPath ? 0 : angle + 90);
                Token token = new Token(3, offsetX, offsetY, deg, sclX, sclY, getRegions());
                tokensPreview.add(token);
            }
        } else {
            int batchCount = getBatchCountLength(2 * MathUtils.PI * circle_spreadRadius);
            for (int i = 0; i < batchCount; i++) {
                float angle = (360f / batchCount) * i;
                float offsetX = MathUtils.cosDeg(angle) * circle_spreadRadius;
                float offsetY = MathUtils.sinDeg(angle) * circle_spreadRadius;
                float deg = this.deg + (!angleFollowPath ? 0 : angle + 90);
                Token token = new Token(3, offsetX, offsetY, deg, sclX, sclY, getRegions());
                tokensPreview.add(token);
            }
        }

        if (tokensPreview.size >= 2) tokensPreview.sort(Comparator.comparingInt(o -> -(int) o.transform.y));
    }

    private void line_refillWithTokens() {
        tokensPreview.clear();
        line_end.set(x, y);
        float length = Vector2.dst(line_start, line_end);
        int batchCount = getBatchCountLength(length);
        Vector2 step = new Vector2(line_end.x - line_start.x, line_end.y - line_start.y);
        step.nor();
        step.scl(length / batchCount);
        for (int i = 0; i < batchCount; i++) {
            float deg = this.deg + (!angleFollowPath ? 0 : step.angleDeg()); // calculate deg based on params.
            Token token = new Token(3, line_start.x + step.x * i, line_start.y + step.y * i, deg, sclX, sclY, getRegions());
            tokensPreview.add(token);
        }
        if (tokensPreview.size >= 2) tokensPreview.sort(Comparator.comparingInt(o -> -(int) o.transform.y));
    }

    private void polygon_refillWithTokens() {
        tokensPreview.clear();
        if (polygon_points.isEmpty()) return;

        if (fillShape) {
            if (polygon_points.size <= 1) return;

            Utils.polygonConvertToArrayFloat(polygon_flatTmp, polygon_points);
            polygon_flatTmp.add(x,y);
            Utils.polygonCalculateBoundingBox(polygon_flatTmp, polygon_BottomLeft, polygon_TopRight);
            float width  = polygon_TopRight.x - polygon_BottomLeft.x;
            float height = polygon_TopRight.y - polygon_BottomLeft.y;
            float area = Math.abs(width * height);
            float ratio = width / height;
            int batchCount = getBatchCountArea(area);

            float cellArea = area / batchCount;
            float step = (float)Math.sqrt(cellArea);


            float minX = polygon_BottomLeft.x;
            float minY = polygon_BottomLeft.y;

            float maxX = polygon_TopRight.x;
            float maxY = polygon_TopRight.y;

            float startX = minX + step * 0.5f;
            float startY = minY + step * 0.5f;
            float posY = startY;

            if (polygon_shape == null) {
                polygon_shape = new Shape2DPolygon(polygon_flatTmp);
            } else {
                polygon_shape.setPoints(polygon_flatTmp);
            }
            Vector2 field = new Vector2();

            while (posY <= maxY) {
                float posX = startX;
                while (posX <= maxX) {
                    if (MathUtils.polygonContainsPoint(polygon_flatTmp, posX, posY)) {
                        field.set(posX, posY);
                        float angle = deg + (angleFollowPath ? Utils.getDirectionRough(field, polygon_shape) : 0);
                        Token token = new Token(3, posX, posY, angle, sclX, sclY, getRegions());
                        tokensPreview.add(token);
                    }
                    posX += step;
                }
                posY += step;
            }

        } else {
            for (int i = 0; i < polygon_points.size - 1; i++) {
                Vector2 start = polygon_points.get(i);
                Vector2 end = polygon_points.get(i+1);
                float length = Vector2.dst(start, end);
                int batchCount = getBatchCountLength(length);
                Vector2 step = new Vector2(end.x - start.x, end.y - start.y);
                step.nor();
                step.scl(length / batchCount);
                for (int j = 0; j < batchCount; j++) {
                    float deg = this.deg + (!angleFollowPath ? 0 : step.angleDeg()); // calculate deg based on params.
                    Token token = new Token(3, start.x + step.x * j, start.y + step.y * j, deg, sclX, sclY, getRegions());
                    tokensPreview.add(token);
                }
            }
            // add last line segment
            Vector2 start = polygon_points.last();
            Vector2 end = new Vector2(x,y);
            float length = Vector2.dst(start, end);
            int batchCount = getBatchCountLength(length);
            Vector2 step = new Vector2(end.x - start.x, end.y - start.y);
            step.nor();
            step.scl(length / batchCount);
            for (int j = 0; j < batchCount; j++) {
                float deg = this.deg + (!angleFollowPath ? 0 : step.angleDeg()); // calculate deg based on params.
                Token token = new Token(3, start.x + step.x * j, start.y + step.y * j, deg, sclX, sclY, getRegions());
                tokensPreview.add(token);
            }
        }

        if (tokensPreview.size >= 2) tokensPreview.sort(Comparator.comparingInt(o -> -(int) o.transform.y));
    }

    protected TextureRegion[] getRegions() {
        String name = "assets/textures-layer-3/geology_" + currentType.name().toLowerCase() + "_" + MathUtils.randomUniformInt(0,6) + ".png";
        TextureRegion[] regions = new TextureRegion[1];
        regions[0] = atlas.getRegion(name);
        return regions;
    }

    private void deleteTokens() {
        for (Token token : tokensToDelete) {
            CommandTokenDelete cmd = new CommandTokenDelete(token.tokenType, token.layer, token.transform.x, token.transform.y, false);
            map.addCommand(cmd);
        }
        tokensToDelete.clear();
    }

    protected float getMinSpacing() {
        TextureRegion[] regions = getRegions();
        float pixelSpacing = 0;
        for (TextureRegion region : regions) {
            if (region == null) continue;
            pixelSpacing = Math.max(region.packedWidth, pixelSpacing);
        }
        return pixelSpacing * 0.5f * Math.abs(sclX);
    }

    private void spawnTokens(boolean useBrushOffset, boolean maintainMinSpacing) {
        map.getAllTokensByType(currentType, alreadyCreatedTokens);

        float offsetX = useBrushOffset ? x : 0;
        float offsetY = useBrushOffset ? y : 0;

        for (Token token : tokensPreview) {
            Vector2 position = new Vector2(token.transform.x + x, token.transform.y + y);
            float minDistance = Float.POSITIVE_INFINITY;
            for (Token mapToken : alreadyCreatedTokens) {
                float distanceSquared = Vector2.dst2(position.x, position.y, mapToken.transform.x, mapToken.transform.y);
                minDistance = Math.min(distanceSquared, minDistance);
            }
            minDistance = (float) Math.sqrt(minDistance);
            if (minDistance < getMinSpacing() && maintainMinSpacing) continue;

            CommandTokenCreate createToken = new CommandTokenCreate(
                    3,
                    token.transform.x + offsetX, token.transform.y + offsetY,
                    token.transform.deg,
                    token.transform.sclX, token.transform.sclY, true,
                    token.regions
            );

            createToken.tokenType = currentType;
            createToken.tint = token.tint;
            map.addCommand(createToken);
        }
    }

    @Override
    public void update(float delta) {
        // =============  input parameters  ===============
        boolean backspaceJustPressed = Input.keyboard.isKeyJustPressed(Keyboard.Key.BACKSPACE);
        boolean leftShiftJustPressed = Input.keyboard.isKeyJustPressed(Keyboard.Key.LEFT_SHIFT);
        boolean leftClicked = Input.mouse.isButtonClicked(Mouse.Button.LEFT);
        boolean rightClicked = Input.mouse.isButtonClicked(Mouse.Button.RIGHT);
        boolean mouseMoved = Input.mouse.moved();
        boolean leftPressedAndMoved = Input.mouse.isButtonPressed(Mouse.Button.LEFT) && mouseMoved;
        boolean plusJustPressed = Input.keyboard.isKeyJustPressed(Keyboard.Key.EQUAL);
        boolean minusJustPressed = Input.keyboard.isKeyJustPressed(Keyboard.Key.MINUS);
        boolean tabJustPressed = Input.keyboard.isKeyJustPressed(Keyboard.Key.TAB);
        boolean sPressed = Input.keyboard.isKeyPressed(Keyboard.Key.S);
        boolean aPressed = Input.keyboard.isKeyPressed(Keyboard.Key.A);
        boolean dPressed = Input.keyboard.isKeyPressed(Keyboard.Key.D);
        float dy = Input.mouse.getYDelta();
        boolean zJustPressed = Input.keyboard.isKeyJustPressed(Keyboard.Key.Z);
        boolean xJustPressed = Input.keyboard.isKeyJustPressed(Keyboard.Key.X);

        // =============  tool settings  ===============
        if (leftShiftJustPressed) {
            this.currentShape = Collections.enumNext(this.currentShape);
            onChangeParameters();
            return;
        }

        if (tabJustPressed) {
            this.angleFollowPath = !angleFollowPath;
            onChangeParameters();
            return;
        }

        if (dPressed && dy != 0) {
            float deltaSpacing = dy > 0 ? 0.01f : -0.01f;
            spacing *= (1.0f + deltaSpacing);
            onChangeParameters();
            return;
        }

        if (sPressed && dy != 0) {
            float deltaSpreadRadius = -dy / 1000 * Graphics.getWindowHeight();
            circle_spreadRadius += deltaSpreadRadius;
            circle_spreadRadius = Math.max(0, circle_spreadRadius);
            onChangeParameters();
            return;
        }

        if (aPressed && dy != 0) {
            float deltaDeg = -dy / 1000 * Graphics.getWindowHeight();
            deg += deltaDeg;
            onChangeParameters();
            return;
        }

        if (backspaceJustPressed) {
            this.currentMode = Collections.enumNext(this.currentMode);
            onChangeParameters();
        }

        if (rightClicked) {
            this.fillShape = !fillShape;
            onChangeParameters();
        }

        if (plusJustPressed) {
            sclX *= 2;
            sclY *= 2;
            onChangeParameters();
            return;
        } else if (minusJustPressed) {
            sclX *= 0.5f;
            sclY *= 0.5f;
            onChangeParameters();
            return;
        }

        if (zJustPressed) {
            currentType = Collections.enumNext(currentType);
            onChangeParameters();
            return;
        } else if (xJustPressed) {
            currentType = Collections.enumPrev(currentType);
            onChangeParameters();
            return;
        }

        // =============  actions  ===============

        if (currentMode == Tool.Mode.SUB) {
            if (leftClicked || leftPressedAndMoved) {
                tokensToDelete.clear();
                float radius = Math.abs(circle_spreadRadius * sclX);
                radius = Math.max(radius, 10);
                map.getAllTokensInCircleByType(ToolBrush_Debug.Type.DEBUG_RECT, x, y, radius, tokensToDelete);
                deleteTokens();
            }
            return;
        }

        // add tokens
        if (currentShape == Tool.Shape.POINT) {
            if (leftClicked) {
                spawnTokens(true, false);
                refillWithTokens();
                return;
            }
        }


        if (currentShape == Tool.Shape.LINE) {
            if (line_free) {
                if (leftClicked) {
                    line_start.set(x, y);
                    refillWithTokens();
                    line_free = false;
                }
            } else {
                if (mouseMoved) refillWithTokens();
                if (leftClicked) {
                    spawnTokens(false, false);
                    tokensPreview.clear();
                    line_free = true;
                }
            }
        }

        if (currentShape == Tool.Shape.CIRCLE) {
            if (leftClicked || leftPressedAndMoved) {
                spawnTokens(true, true);
                refillWithTokens();
                return;
            }
            return;
        }

        if (currentShape == Tool.Shape.POLYGON) {
            if (polygon_free) {
                if (leftClicked) {
                    polygon_points.add(new Vector2(x, y));
                    polygon_free = false;
                }
                return;
            } else {
                if (mouseMoved) refillWithTokens();
                else if (leftClicked) {
                    Vector2 p = new Vector2(x, y); // need to test intersections etc.

                    if (polygon_points.size <= 2) {
                        polygon_points.add(p);
                        return;
                    }

                    if (Vector2.dst(p, polygon_points.first()) <= 20) {
                        spawnTokens(false, false);
                        tokensPreview.clear();
                        polygon_points.clear();
                        polygon_free = true;
                    } else {
                        polygon_points.add(p);
                    }
                }
            }
        }
    }

    @Override
    public void renderToolOverlay(Renderer2D renderer2D, float x, float y) {
        if (currentMode == Tool.Mode.SUB) {
            float radius = Math.abs(circle_spreadRadius * sclX);
            renderer2D.setColor(Color.RED);
            renderer2D.drawCircleThin(Math.max(radius, 10), 10, x, y, 0,1,1);
            return;
        }

        // for adding tokens
        if (currentShape == Tool.Shape.POINT) {
            renderer2D.setColor(Color.GREEN);
            renderer2D.drawCircleFilled(8, 10, x, y, 0,1,1);
            renderer2D.setColor(Color.WHITE);
            for (Token token : tokensPreview) {
                token.renderPreview(renderer2D, x, y);
            }
            return;
        }

        if (currentShape == Tool.Shape.LINE) {
            if (line_free) {
                renderer2D.setColor(Color.BLUE);
                renderer2D.drawCircleThin(Math.max(12, 5), 10, x, y, 0,1,1);
                renderer2D.setColor(Color.WHITE);
                for (TextureRegion region : currentRegions) {
                    if (region == null) continue;
                    renderer2D.drawTextureRegion(region, x, y, deg, sclX, sclY);
                }
            } else {
                renderer2D.setColor(Color.BLUE);
                renderer2D.drawCircleThin(Math.max(12, 5), 10, line_start.x, line_start.y, 0,1,1);
                renderer2D.drawLineThin(line_start.x, line_start.y, x, y);
                renderer2D.drawCircleThin(Math.max(12, 5), 10, x, y, 0,1,1);
                for (Token token : tokensPreview) {
                    token.render(renderer2D);
                }
            }
            return;
        }

        if (currentShape == Tool.Shape.CIRCLE) {
            float radius = Math.abs(circle_spreadRadius);
            renderer2D.setColor(Color.GREEN);
            renderer2D.drawCircleThin(Math.max(radius, 5), 10, x, y, 0,1,1);
            renderer2D.setColor(Color.WHITE);
            for (Token token : tokensPreview) {
                token.renderPreview(renderer2D, x, y);
            }
            return;
        }

        if (currentShape == Tool.Shape.POLYGON) {
            for (Token token : tokensPreview) {
                token.render(renderer2D);
            }
            if (polygon_free) {
                renderer2D.setColor(Color.PURPLE);
                renderer2D.drawCircleThin(Math.max(12, 5), 10, x, y, 0,1,1);
                renderer2D.setColor(Color.WHITE);
                if (currentRegions != null) {
                    for (TextureRegion region : currentRegions) {
                        if (region == null) continue;
                        renderer2D.drawTextureRegion(region, x, y, deg, sclX, sclY);
                    }
                }
            } else if (!polygon_points.isEmpty()) {
                renderer2D.setColor(Color.PURPLE);
                renderer2D.drawCircleBorder(15, 5, 10, polygon_points.first().x, polygon_points.first().y, 0,1,1);
                for (int i = 0; i < polygon_points.size; i++) {
                    Vector2 p = polygon_points.get(i);
                    renderer2D.drawCircleFilled(5, 5, p.x, p.y, 0, 1, 1);
                }
                renderer2D.setColor(Color.YELLOW);
                for (int i = 0; i < polygon_points.size - 1; i++) {
                    Vector2 p1 = polygon_points.get(i);
                    Vector2 p2 = polygon_points.get(i + 1);
                    renderer2D.drawLineThin(p1.x, p1.y, p2.x, p2.y);
                }
                renderer2D.drawLineThin(polygon_points.last().x, polygon_points.last().y, x, y);
                renderer2D.drawLineThin(x, y, polygon_points.first().x, polygon_points.first().y);
            }
            return;
        }

        renderer2D.setColor(Color.WHITE);
    }

    @Override
    public void renderToolText(Renderer2D renderer2D, float x, float y) {

    }

    @Override
    String getHelperText() {
        return "";
    }

    @Override
    public void activate() {
        // TODO
        refillWithTokens();
    }

    @Override
    public void deactivate() {
        // TODO
    }

    // later

    @Override
    public int getLayer() {
        return 0;
    }

    @Override
    public boolean active() {
        return active;
    }

    @Override
    public boolean mouseButtonsDown(int mouseX, int mouseY, @NotNull Array<Mouse.Button> buttons) {
        return false;
    }

    @Override
    public boolean mouseButtonsUp(int mouseX, int mouseY, @NotNull Array<Mouse.Button> buttons) {
        return false;
    }

    @Override
    public boolean mouseMoved(int mouseX, int mouseY, int deltaMouseX, int deltaMouseY) {
        return false;
    }

    @Override
    public boolean mouseScrolled(float scrollX, float scrollY) {
        return false;
    }

    @Override
    public boolean mouseDragged(int mouseX, int mouseY, int deltaMouseX, int deltaMouseY, @NotNull Array<Mouse.Button> buttons) {
        return false;
    }

    @Override
    public boolean keyboardKeysJustPressed(@NotNull Array<Keyboard.Key> keys) {
        return false;
    }

    @Override
    public boolean keyboardKeysJustReleased(@NotNull Array<Keyboard.Key> keys) {
        return false;
    }

    @Override
    public boolean keyboardCodepointsTyped(@NotNull ArrayChar codepoints) {
        return false;
    }

    public enum Type {

        BOULDER_PLAIN_BIG,
        BOULDER_PLAIN_SMALL,

        HILLS_BROWN,
        HILLS_GREEN,

    }

}
