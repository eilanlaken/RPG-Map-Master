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

    // point mode

    // circle mode
    private float circle_spreadRadius = 200;
    private boolean circle_fill = false;

    // line mode
    private Vector2 line_start = new Vector2();
    private Vector2 line_end = new Vector2();

    // polygon mode
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

    private void point_refillWithTokens() {
        tokensPreview.clear();
        Token token = new Token(3, 0, 0, 0, sclX,sclY, getRegions());
        tokensPreview.add(token);
    }

    // TODO - filter against self. If a token is too close to one already in the circle, don't add it.
    private void circle_refillWithTokens() {
        tokensPreview.clear();
        float radius = circle_spreadRadius * sclX;

        if (circle_fill) {
            int batchCount = getBatchCountArea(MathUtils.PI * radius * radius);
            for (int i = 0; i < batchCount; i++) {
                float r = radius * MathUtils.randomUniformFloat(0,1);
                float deg = MathUtils.randomUniformFloat(0,360);
                float offsetX = MathUtils.cosDeg(deg) * r;
                float offsetY = MathUtils.sinDeg(deg) * r;
                Token token = new Token(3, offsetX, offsetY, 0, sclX, sclY, getRegions());
                token.tint = Color.randomOpaque();
                tokensPreview.add(token);
            }
        } else {
            int batchCount = getBatchCountLength(2 * MathUtils.PI * radius);
            for (int i = 0; i < batchCount; i++) {
                float angle = (360f / batchCount) * i;
                float offsetX = MathUtils.cosDeg(angle) * radius;
                float offsetY = MathUtils.sinDeg(angle) * radius;
                float deg = 0; // calculate deg based on params.
                Token token = new Token(3, offsetX, offsetY, deg, sclX, sclY, getRegions());
                token.tint = Color.randomOpaque();
                tokensPreview.add(token);
            }
        }

        tokensPreview.sort(Comparator.comparingInt(o -> -(int) o.minY));
    }

    protected int getBatchCountArea(float area) {
        float d = spacing * region.packedWidth * 0.5f * Math.abs(sclX); // center spacing
        return (int) (area / (d * d));
    }

    protected int getBatchCountLength(float length) {
        float d = spacing * region.packedWidth * 0.5f * Math.abs(sclX); // center spacing
        return (int) (length / d);
    }

    private void line_refillWithTokens() {
        tokensPreview.clear();
        //...
        tokensPreview.sort(Comparator.comparingInt(o -> -(int) o.minY));
    }

    private void polygon_refillWithTokens() {
        tokensPreview.clear();
        //...
        tokensPreview.sort(Comparator.comparingInt(o -> -(int) o.minY));
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
                point_refillWithTokens();
                return;
            }
        }

        if (currentShape == Shape.CIRCLE) {
            if (leftClicked || leftPressedAndMoved) {
                spawnTokens(true, true);
                circle_refillWithTokens();
                return;
            }
            return;
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
            System.out.println(tokensPreview.size);

            for (Token token : tokensPreview) {
                token.renderPreview(renderer2D, x, y);
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

        if (currentShape == Shape.LINE) {

            return;
        }



        if (currentShape == Shape.POLYGON) {

            return;
        }
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
