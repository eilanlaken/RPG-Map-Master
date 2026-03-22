package com.heavybox.jtix.z;

import com.heavybox.jtix.RPGMapMakerScene;
import com.heavybox.jtix.assets.Assets;
import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.collections.ArrayChar;
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
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;

public class ToolBrush_Debug extends Tool {

    private final TexturePack atlas;
    private TextureRegion region;

    private Mode currentMode;
    private Shape currentShape;
    private float spacing = 1;
    private final Array<Token> tokensPreview = new Array<>();
    private final Array<Token> alreadyCreatedTokens = new Array<>();
    private boolean angleFollowPath = true;

    // point mode

    // circle mode
    private float circle_spreadRadius = 200;
    private boolean circle_fill = false;

    // line mode
    private boolean line_free = true;
    private final Vector2 line_start = new Vector2();
    private final Vector2 line_end = new Vector2();

    // polygon mode
    private boolean polygon_free = true;
    private boolean polygon_fill = false;
    private final Array<Vector2> polygon_points = new Array<>(true, 10);

    public ToolBrush_Debug(final RPGMapMakerScene scene) {
        super(scene);
        // TODO:
        //Input.addEventHandler(this);
        atlas = Assets.get("assets/texture-packs/layer_3.yml");
        region = atlas.getRegion("assets/textures-layer-3/debug_rect_0.png");

        selectMode(Mode.ADD);
        selectShape(Shape.POINT);
    }

    private void refillWithTokens() {
        if (currentShape == Shape.POINT)        point_refillWithTokens();
        else if (currentShape == Shape.CIRCLE)  circle_refillWithTokens();
        else if (currentShape == Shape.LINE)    line_refillWithTokens();
        else if (currentShape == Shape.POLYGON) polygon_refillWithTokens();
    }

    private void point_refillWithTokens() {
        tokensPreview.clear();
        Token token = new Token(3, 0, 0, 0, sclX,sclY, getRegions());
        tokensPreview.add(token);
    }

    // TODO - filter against self. If a token is too close to one already in the circle, don't add it.
    private void circle_refillWithTokens() {
        tokensPreview.clear();

        if (circle_fill) {
            int batchCount = getBatchCountArea(MathUtils.PI * circle_spreadRadius * circle_spreadRadius);
            for (int i = 0; i < batchCount; i++) {
                float r = circle_spreadRadius * MathUtils.randomUniformFloat(0,1);
                float angle = MathUtils.randomUniformFloat(0,360);
                float offsetX = MathUtils.cosDeg(angle) * r;
                float offsetY = MathUtils.sinDeg(angle) * r;
                float deg = this.deg + (!angleFollowPath ? 0 : angle + 90);
                Token token = new Token(3, offsetX, offsetY, deg, sclX, sclY, getRegions());
                token.tint = Color.randomOpaque();
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
                token.tint = Color.randomOpaque();
                tokensPreview.add(token);
            }
        }

        if (tokensPreview.size >= 2) tokensPreview.sort(Comparator.comparingInt(o -> -(int) o.minY));
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
            token.tint = Color.randomOpaque();
            tokensPreview.add(token);
        }
        if (tokensPreview.size >= 2) tokensPreview.sort(Comparator.comparingInt(o -> -(int) o.minY));
    }

    private void polygon_refillWithTokens() {
        tokensPreview.clear();

        if (polygon_fill) {

        } else {
            for (int i = 0; i < polygon_points.size - 1; i++) {
                Vector2 start = polygon_points.get(i);
                Vector2 end = polygon_points.get(i+1);
                float length = Vector2.dst(start, end);
                int batchCount = getBatchCountLength(length);
                Vector2 step = new Vector2(end.x - start.x, end.y - start.y);
                step.nor();
                step.scl(length / batchCount);
                for (int j = 0; j < batchCount - 1; j++) {
                    float deg = this.deg + (!angleFollowPath ? 0 : step.angleDeg()); // calculate deg based on params.
                    Token token = new Token(3, start.x + step.x * j, start.y + step.y * j, deg, sclX, sclY, getRegions());
                    token.tint = Color.randomOpaque();
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
            for (int j = 0; j < batchCount - 1; j++) {
                float deg = this.deg + (!angleFollowPath ? 0 : step.angleDeg()); // calculate deg based on params.
                Token token = new Token(3, start.x + step.x * j, start.y + step.y * j, deg, sclX, sclY, getRegions());
                token.tint = Color.randomOpaque();
                tokensPreview.add(token);
            }
        }

        if (tokensPreview.size >= 2) tokensPreview.sort(Comparator.comparingInt(o -> -(int) o.minY));
    }

    protected int getBatchCountArea(float area) {
        float d = spacing * region.packedWidth * 0.5f * Math.abs(sclX); // center spacing
        if (d == 0) return 1;
        return (int) (area / (d * d));
    }

    protected int getBatchCountLength(float length) {
        float d = spacing * region.packedWidth * 0.5f * Math.abs(sclX); // center spacing
        if (d == 0) return 1;
        return (int) (length / d);
    }

    private void spawnTokens(boolean useBrushOffset, boolean maintainMinSpacing) {
        map.getAllTokensByRegion(region, alreadyCreatedTokens);

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
            if (minDistance < getMinSpacing() && maintainMinSpacing) continue;

            CommandTokenCreate createToken = new CommandTokenCreate(
                    3,
                    token.transforms[0].x + offsetX, token.transforms[0].y + offsetY,
                    token.transforms[0].deg,
                    token.transforms[0].sclX, token.transforms[0].sclY, true,
                    token.regions
            );
            createToken.tokenType = null;
            createToken.tint = token.tint;
            map.addCommand(createToken);
        }
    }

    @Override
    public void update(float delta) {
        boolean backspaceJustPressed = Input.keyboard.isKeyJustPressed(Keyboard.Key.BACKSPACE);
        boolean leftShiftJustPressed = Input.keyboard.isKeyJustPressed(Keyboard.Key.LEFT_SHIFT);
        boolean leftClicked = Input.mouse.isButtonClicked(Mouse.Button.LEFT);
        boolean mouseMoved = Input.mouse.moved();
        boolean leftPressedAndMoved = Input.mouse.isButtonPressed(Mouse.Button.LEFT) && mouseMoved;

        // tool settings
        if (leftShiftJustPressed) {
            selectShape(Collections.enumNext(this.currentShape));
            return;
        }

        if (backspaceJustPressed) {
            selectMode(Collections.enumNext(this.currentMode));
        }

        if (currentMode == Mode.SUB) {

            return;
        }

        // add tokens
        if (currentShape == Shape.POINT) {
            if (leftClicked) {
                spawnTokens(true, false);
                refillWithTokens();
                return;
            }
        }


        if (currentShape == Shape.LINE) {
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

        if (currentShape == Shape.CIRCLE) {
            if (leftClicked || leftPressedAndMoved) {
                spawnTokens(true, true);
                refillWithTokens();
                return;
            }
            return;
        }

        if (currentShape == Shape.POLYGON) {
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
                        spawnTokens(false, true);
                        tokensPreview.clear();
                        polygon_points.clear();
                        polygon_free = true;
                    }

                    polygon_points.add(p);
                }
            }
        }

    }

    @Override
    public void renderToolOverlay(Renderer2D renderer2D, float x, float y) {
        if (currentMode == Mode.SUB) {

            return;
        }

        // for adding tokens
        if (currentShape == Shape.POINT) {
            renderer2D.setColor(Color.GREEN);
            renderer2D.drawCircleFilled(8, 10, x, y, 0,1,1);
            renderer2D.setColor(Color.WHITE);
            for (Token token : tokensPreview) {
                token.renderPreview(renderer2D, x, y);
            }
            return;
        }

        if (currentShape == Shape.LINE) {
            if (line_free) {
                renderer2D.setColor(Color.BLUE);
                renderer2D.drawCircleThin(Math.max(12, 5), 10, x, y, 0,1,1);
                renderer2D.setColor(Color.WHITE);
                renderer2D.drawTextureRegion(region, x,y,deg,sclX,sclY);
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

        if (currentShape == Shape.CIRCLE) {
            float radius = Math.abs(circle_spreadRadius * sclX);
            renderer2D.setColor(Color.GREEN);
            renderer2D.drawCircleThin(Math.max(radius, 5), 10, x, y, 0,1,1);
            renderer2D.setColor(Color.WHITE);
            for (Token token : tokensPreview) {
                token.renderPreview(renderer2D, x, y);
            }
            return;
        }

        if (currentShape == Shape.POLYGON) {
            if (polygon_free) {
                renderer2D.setColor(Color.PURPLE);
                renderer2D.drawCircleThin(Math.max(12, 5), 10, x, y, 0,1,1);
                renderer2D.setColor(Color.WHITE);
                renderer2D.drawTextureRegion(region, x,y,deg,sclX,sclY);
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
    public void onActivate() {
        //tokensPreview.clear();
    }

    @Override
    public void onDeactivate() {
        tokensPreview.clear();
    }

    private void selectMode(Mode newCurrentMode) {
        if (newCurrentMode == Mode.ADD) {

        } else {

        }

        this.currentMode = newCurrentMode;
    }

    private void selectShape(Shape newCurrentShape) {
        if (newCurrentShape == Shape.POINT) {
            point_refillWithTokens();
            System.out.println("ok");
        }

        if (newCurrentShape == Shape.CIRCLE) {
            circle_refillWithTokens();
        }

        if (newCurrentShape == Shape.LINE) {
            line_refillWithTokens();
        }

        this.currentShape = newCurrentShape;
    }

    @Override
    protected TextureRegion[] getRegions() {
        return new TextureRegion[] {region};
    }

    @Override
    public String getName() {
        return "";
    }

    @Override
    public int getLayer() {
        return 2;
    }

    // **************** TODO ****************** ////////////
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
        if (keys.contains(Keyboard.Key.LEFT_SHIFT, true)) {
            // change shape
            return true;
        }

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
}
