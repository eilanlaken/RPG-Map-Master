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
import com.heavybox.jtix.z.CommandTokenCreate;
import com.heavybox.jtix.z.Token;
import com.heavybox.jtix.z.Tool;
import com.heavybox.jtix.z.Utils;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

public class Tool_5_Architecture extends Tool_new {

    private final TexturePack atlas;

    // global state
    private Tool.Mode currentMode;
    private Tool.Shape currentShape;
    private View currentView;

    private float spacing = 1.0f;
    private Race race = Race.HUMAN;
    private final Set<Token> tokensToDelete = new HashSet<>();
    private final Array<Token> tokensPreview = new Array<>();
    private final Array<Token> alreadyCreatedTokens = new Array<>();
    private boolean angleFollowPath = true;
    private boolean fillShape = false;
    private boolean procedural = true;

    // point mode
    private boolean dragged = false;
    private final Vector2 point_lastSpawnPoint = new Vector2();

    // line mode
    private boolean line_free = true;
    private final Vector2 line_start = new Vector2();
    private final Vector2 line_end = new Vector2();

    // circle mode
    private float circle_spreadRadius = 200;

    // polygon mode
    private boolean polygon_fill = false;
    private boolean polygon_free = true;
    private final Array<Vector2> polygon_points = new Array<>(true, 10);
    private final ArrayFloat polygon_flatTmp = new ArrayFloat(true, 10);
    private final Vector2 polygon_BottomLeft = new Vector2();
    private final Vector2 polygon_TopRight = new Vector2();
    private Shape2DPolygon polygon_shape;

    public Tool_5_Architecture(final RPGMapMakerScene scene) {
        super(scene);
        atlas = Assets.get("assets/texture-packs/layer_3.yml");
        sclX = 1f / 3;
        sclY = 1f / 3;
        // group assets

        currentMode = Tool.Mode.ADD;
        currentShape = Tool.Shape.POINT;
        currentView = View.ISOMETRIC_VIEW;
    }

    private float getDiscreteAngle(float angle) {
        // for isometric view
        final float RANGE = 22.5f;
        for (int i = 0; i < 8; i++) {
            float deg = i * 45;
            float dist = MathUtils.shortestAngularDistanceDeg(deg, angle);
            if (dist <= RANGE) {
                return deg;
            }
        }

        return 0;
    }

    private int getDiscreteAngleIndex(float angle) {
        // for isometric view
        final float RANGE = 22.5f;
        int index = 0;
        for (int i = 0; i < 8; i++) {
            float deg = i * 45;
            float dist = MathUtils.shortestAngularDistanceDeg(deg, angle);
            if (dist <= RANGE) {
                index = i;
                break;
            }
        }

        index = (index + 2) % 8; // advance by 90 degrees
        return index;
    }

    private boolean flipX(int angleIndex) {
        return angleIndex == 1 || angleIndex == 5;
    }

    protected float getProceduralSpacing() {
        if (currentView == View.ISOMETRIC_VIEW) return 38 * spacing * Math.abs(sclX);
        return 38 * spacing * Math.abs(sclX); // TODO: for now, this is hard-coded.
    }

    private TextureRegion getRegion() {

        return null;
    }

    private TextureRegion getRegion_tower() {
        if (currentView == View.ISOMETRIC_VIEW) {
            String race = this.race.name().toLowerCase();
            String prefix = "assets/textures-layer-3/architecture_" + race + "_" + View.ISOMETRIC_VIEW.name().toLowerCase() + "_";
            boolean tall = MathUtils.randomUniformInt(0,2) == 1;
            String middle = "tower_" + (tall ? "tall" : "short");
            int variations = Utils.countVariations(atlas, prefix + middle);
            String suffix = "_" + MathUtils.randomUniformInt(0,variations) + ".png";
            return atlas.getRegion(prefix + middle + suffix);
        }

        return null;
    }

    private TextureRegion getRegion_bridge() {
        if (currentView == View.ISOMETRIC_VIEW) {
            String race = this.race.name().toLowerCase();
            String prefix = "assets/textures-layer-3/architecture_" + race + "_" + View.ISOMETRIC_VIEW.name().toLowerCase() + "_";
            boolean tall = MathUtils.randomUniformInt(0,2) == 1;
            String middle = "bridge";
            int variations = Utils.countVariations(atlas, prefix + middle);
            String suffix = "_" + MathUtils.randomUniformInt(0,variations) + ".png";
            return atlas.getRegion(prefix + middle + suffix);
        }

        return null;
    }

    private TextureRegion getRegion_isometricHouse(int angleIndex) {
        String race = this.race.name().toLowerCase();
        String prefix = "assets/textures-layer-3/architecture_" + race + "_" + View.ISOMETRIC_VIEW.name().toLowerCase() + "_";

        String middle = "";
        if (angleIndex == 0 || angleIndex == 4) {
            boolean tall = MathUtils.randomUniformInt(0,2) == 1;
            middle = "house_horizontal_" + (tall ? "tall" : "short");
        }
        if (angleIndex == 1 || angleIndex == 5) {
            int type = MathUtils.randomUniformInt(0,3);
            if (type == 0) middle = "house_diagonal_short";
            if (type == 1) middle = "house_diagonal_tall";
            if (type == 2) middle = "hut_diagonal";
        }
        if (angleIndex == 2 || angleIndex == 6) {
            int type = MathUtils.randomUniformInt(0,3);
            if (type == 0) middle = "house_vertical_short";
            if (type == 1) middle = "house_vertical_tall";
            if (type == 2) middle = "hut_vertical";
        }
        if (angleIndex == 3 || angleIndex == 7) {
            int type = MathUtils.randomUniformInt(0,3);
            if (type == 0) middle = "house_diagonal_short";
            if (type == 1) middle = "house_diagonal_tall";
            if (type == 2) middle = "hut_diagonal";
        }

        int variations = Utils.countVariations(atlas, prefix + middle);
        String suffix = "_" + MathUtils.randomUniformInt(0,variations) + ".png";

        return atlas.getRegion(prefix + middle + suffix);
    }

    private void spawnToken(TextureRegion region, float x, float y, float deg, float sclX, float sclY) {
        CommandTokenCreate createToken = new CommandTokenCreate(
                scene.getActiveLayerIndex(),
                x, y,
                deg,
                sclX, sclY,
                false,
                region
        );

        createToken.tokenType = currentView;
        map.addCommand(createToken);
    }

    private void spawnToken(TextureRegion region, float deg, boolean flipX) {
        CommandTokenCreate createToken = new CommandTokenCreate(
                scene.getActiveLayerIndex(),
                x, y,
                deg,
                flipX ? -sclX : sclX,
                sclY,
                false,
                region
        );

        Color tint = new Color();
        tint.r = 1 + MathUtils.randomUniformFloat(-0.05f, 0.0f);
        tint.g = 1 + MathUtils.randomUniformFloat(-0.05f, 0.0f);
        tint.b = 1 + MathUtils.randomUniformFloat(-0.05f, 0.0f);
        tint.a = 1;
        createToken.tint = tint;
        createToken.tokenType = currentView;
        map.addCommand(createToken);
    }

    @Override
    void onChangeParameters() {
        refillWithTokens();
    }

    private void refillWithTokens() {
        if (currentShape == Tool.Shape.POINT)        point_refillWithTokens();
        else if (currentShape == Tool.Shape.CIRCLE)  circle_refillWithTokens();
        else if (currentShape == Tool.Shape.LINE)    line_refillWithTokens();
        else if (currentShape == Tool.Shape.POLYGON) polygon_refillWithTokens();
    }

    private void point_refillWithTokens() {}

    private void line_refillWithTokens() {
        tokensPreview.clear();
        if (line_free) return;
        line_end.set(x, y);
        float length = Vector2.dst(line_start, line_end);
        int batchCount = getBatchCountLength(length);
        Vector2 step = new Vector2(line_end.x - line_start.x, line_end.y - line_start.y);
        step.nor();
        step.scl(length / batchCount);
        for (int i = 0; i < batchCount; i++) {
            float deg = this.deg + (!angleFollowPath ? 0 : step.angleDeg()); // calculate deg based on params.
            int angleIndex = getDiscreteAngleIndex(deg);
            TextureRegion region = getRegion_isometricHouse(angleIndex);
            Token token = new Token(scene.getActiveLayerIndex(), line_start.x + step.x * i, line_start.y + step.y * i, 0, flipX(angleIndex) ? -sclX : sclX, sclY, region);
            tokensPreview.add(token);
        }
        if (tokensPreview.size >= 2) tokensPreview.sort(Comparator.comparingInt(o -> -(int) o.transform.y));
    }

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
                if (minDistance < 100) continue;

                float deg = this.deg + (angle + 90);
                int angleIndex = getDiscreteAngleIndex(deg);
                TextureRegion region = getRegion_isometricHouse(angleIndex);
                Token token = new Token(scene.getActiveLayerIndex(), offsetX, offsetY, 0, flipX(angleIndex) ? -sclX : sclX, sclY, region);
                tokensPreview.add(token);
            }
        } else {
            int batchCount = getBatchCountLength(2 * MathUtils.PI * circle_spreadRadius);
            for (int i = 0; i < batchCount; i++) {
                float angle = (360f / batchCount) * i;
                float offsetX = MathUtils.cosDeg(angle) * circle_spreadRadius;
                float offsetY = MathUtils.sinDeg(angle) * circle_spreadRadius;
                float deg = this.deg + (angle + 90);
                int angleIndex = getDiscreteAngleIndex(deg);
                TextureRegion region = getRegion_isometricHouse(angleIndex);
                Token token = new Token(scene.getActiveLayerIndex(), offsetX, offsetY, 0, flipX(angleIndex) ? -sclX : sclX, sclY, region);
                tokensPreview.add(token);
            }
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
            float step = (float)Math.sqrt(cellArea) * 1;

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
                        int angleIndex = getDiscreteAngleIndex(angle);
                        TextureRegion region = getRegion_isometricHouse(angleIndex);
                        Token token = new Token(scene.getActiveLayerIndex(), posX, posY, 0, flipX(angleIndex) ? -sclX : sclX, sclY, region);
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
                    float deg = this.deg + step.angleDeg(); // calculate deg based on params.
                    int angleIndex = getDiscreteAngleIndex(deg);
                    TextureRegion region = getRegion_isometricHouse(angleIndex);
                    Token token = new Token(scene.getActiveLayerIndex(), start.x + step.x * j, start.y + step.y * j, 0, flipX(angleIndex) ? -sclX : sclX, sclY, region);
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
                float deg = this.deg + step.angleDeg(); // calculate deg based on params.
                int angleIndex = getDiscreteAngleIndex(deg);
                TextureRegion region = getRegion_isometricHouse(angleIndex);
                Token token = new Token(scene.getActiveLayerIndex(), start.x + step.x * j, start.y + step.y * j, 0, flipX(angleIndex) ? -sclX : sclX, sclY, region);
                tokensPreview.add(token);
            }
        }

        if (tokensPreview.size >= 2) tokensPreview.sort(Comparator.comparingInt(o -> -(int) o.transform.y));
    }

    protected int getBatchCountLength(float length) {
        float maxExtent = 100;
        float d = spacing * maxExtent * 0.5f * Math.abs(sclX); // center spacing
        if (d == 0) return 1;
        return (int) (length / d);
    }

    protected int getBatchCountArea(float area) {
        float maxExtent = 100;
        float d = spacing * maxExtent * 0.5f * Math.abs(sclX); // center spacing
        if (d == 0) return 1;
        return (int) (area / (d * d));
    }

    private void spawnTokens(boolean useBrushOffset, boolean maintainMinSpacing) {
        map.getAllTokensByType(ArchitectureEnum.ARCHITECTURE_ENUM, alreadyCreatedTokens);

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
            if (minDistance < 40 * Math.abs(sclX) && maintainMinSpacing) continue;

            CommandTokenCreate createToken = new CommandTokenCreate(
                    scene.getActiveLayerIndex(),
                    token.transform.x + offsetX, token.transform.y + offsetY,
                    token.transform.deg,
                    token.transform.sclX, token.transform.sclY, true,
                    token.regions
            );

            createToken.tokenType = ArchitectureEnum.ARCHITECTURE_ENUM;
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
        boolean leftJustDown = Input.mouse.isButtonJustPressed(Mouse.Button.LEFT);
        boolean leftJustUp = Input.mouse.isButtonJustReleased(Mouse.Button.LEFT);
        boolean leftPressed = Input.mouse.isButtonPressed(Mouse.Button.LEFT);
        boolean mouseMoved = Input.mouse.moved();
        boolean leftPressedAndMoved = Input.mouse.isButtonPressed(Mouse.Button.LEFT) && mouseMoved;
        boolean plusJustPressed = Input.keyboard.isKeyJustPressed(Keyboard.Key.EQUAL);
        boolean minusJustPressed = Input.keyboard.isKeyJustPressed(Keyboard.Key.MINUS);
        boolean sPressed = Input.keyboard.isKeyPressed(Keyboard.Key.S);
        boolean aPressed = Input.keyboard.isKeyPressed(Keyboard.Key.A);
        boolean dPressed = Input.keyboard.isKeyPressed(Keyboard.Key.D);
        boolean zJustPressed = Input.keyboard.isKeyJustPressed(Keyboard.Key.Z);

        if (zJustPressed) {
            this.currentView = Collections.enumNext(currentView);
            return;
        }

        if (currentMode == Tool.Mode.SUB) {
            // TODO
            return;
        }

        if (leftShiftJustPressed) {
            this.currentShape = Collections.enumNext(this.currentShape);
            onChangeParameters();
            return;
        }

        // add tokens
        if (currentShape == Tool.Shape.POINT) {
            if (rightClicked) {
                TextureRegion region = getRegion_bridge();
                spawnToken(region, 0, false);
                return;
            }
            if (leftJustDown) {
                point_lastSpawnPoint.set(x, y);
                dragged = false;
                return;
            } else if (leftPressed && mouseMoved) {
                dragged = true;
                Vector2 current = new Vector2(x, y);
                float dst = Vector2.dst(current, point_lastSpawnPoint);
                if (dst < getProceduralSpacing()) return;

                // spawn token and reset anchor
                Vector2 dir = new Vector2(x - point_lastSpawnPoint.x, y - point_lastSpawnPoint.y);
                if (currentView == View.ISOMETRIC_VIEW) {
                    float angleDeg = getDiscreteAngle(dir.angleDeg());
                    int angleIndex = getDiscreteAngleIndex(dir.angleDeg());
                    TextureRegion region = getRegion_isometricHouse(angleIndex);
                    spawnToken(region, MathUtils.randomUniformFloat(-2.5f, 2.5f), flipX(angleIndex));
                }
                point_lastSpawnPoint.set(x, y);
                return;
            } else if (leftJustUp) {
                if (!dragged) {
                    TextureRegion region = getRegion_tower();
                    spawnToken(region, 0, MathUtils.randomUniformInt(0,2) == 1);
                }
            }
        }

        // TODO
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
                    spawnTokens(false, true);
                    tokensPreview.clear();
                    line_free = true;
                }
            }
        }

        // TODO (with fill-shape true / false)
        if (currentShape == Tool.Shape.CIRCLE) {
            if (leftClicked || leftPressedAndMoved) {
                spawnTokens(true, true);
                refillWithTokens();
                return;
            }
            return;
        }

        // TODO (with fill-shape true / false)
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

            return;
        }

        if (currentShape == Tool.Shape.POINT) {

        }

        if (currentShape == Tool.Shape.LINE) {
            if (line_free) {
                renderer2D.setColor(Color.BLUE);
                renderer2D.drawCircleThin(Math.max(12, 5), 10, x, y, 0,1,1);
                renderer2D.setColor(Color.WHITE);
//                for (TextureRegion region : currentRegions) {
//                    if (region == null) continue;
//                    renderer2D.drawTextureRegion(region, x, y, deg, sclX, sclY);
//                }
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
                //renderer2D.drawTextureRegion(region, x,y,deg,sclX,sclY);
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

    }

    @Override
    public void deactivate() {

    }


    // later

    @Override
    public int getInputLayer() {
        return 0;
    }

    @Override
    public boolean isActive() {
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

    public enum View {
        TOP_VIEW,
        ISOMETRIC_VIEW
    }

    public enum Race {
        HUMAN,
        ELF,
        DWARF,
    }

    public static class Bundle {
        public Block[] blocks;
    }

    public static class Block {

        public Type type;
        public float x, y;
        public float sclX;
        public float sclY;
        public float deg;

    }

    public enum Type {
        TOP_VIEW_HOUSE_LARGE,
        TOP_VIEW_HOUSE_MEDIUM,
        TOP_VIEW_HOUSE_SMALL,
        TOP_VIEW_TOWER,
        TOP_VIEW_WALL,

        ISOMETRIC_VIEW_HOUSE_DIAGONAL_SHORT,
        ISOMETRIC_VIEW_HOUSE_DIAGONAL_TALL,
        ISOMETRIC_VIEW_HOUSE_HORIZONTAL_SHORT,
        ISOMETRIC_VIEW_HOUSE_HORIZONTAL_TALL,
        ISOMETRIC_VIEW_HOUSE_VERTICAL_SHORT,
        ISOMETRIC_VIEW_HOUSE_VERTICAL_TALL,
        ISOMETRIC_VIEW_HUT_DIAGONAL,
        ISOMETRIC_VIEW_HUT_VERTICAL,
        ISOMETRIC_VIEW_TOWER_SHORT,
        ISOMETRIC_VIEW_TOWER_TALL,
        ISOMETRIC_VIEW_BRIDGE,
        ;

        public Type getNextView() {
            final String prefix = this.name().split("_")[0];

            Type next = Collections.enumNext(this);
            while (next.name().startsWith(prefix))
                next = Collections.enumNext(next);
            return next;
        }

        public Type getNextOfTheSamePrefix() {
            final String prefix = this.name().split("_")[0];

            Type next = Collections.enumNext(this);
            while (!next.name().startsWith(prefix))
                next = Collections.enumNext(next);
            return next;
        }

        public Type getPrevOfTheSamePrefix() {
            final String prefix = this.name().split("_")[0];
            Type prev = Collections.enumPrev(this);
            while (!prev.name().startsWith(prefix))
                prev = Collections.enumPrev(prev);
            return prev;
        }

        public String getView() {
            if (name().startsWith("TOP")) return "TOP";
            if (name().startsWith("SIDE")) return "SIDE";
            if (name().startsWith("ISOMETRIC")) return "ISOMETRIC";
            return null;
        }

    }

    public enum ArchitectureEnum {
        ARCHITECTURE_ENUM;
    }

}
