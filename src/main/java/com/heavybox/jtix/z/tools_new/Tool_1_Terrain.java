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

public class Tool_1_Terrain extends Tool_new {

    // resources - terrain and brush textures, shaders
    public final Texture[] brushesAdd = new Texture[9];
    public final Texture[] brushesSub = new Texture[9];
    private final Texture[] terrainGrounds = new Texture[11];
    private final Texture[] terrainLiquids = new Texture[4];
    private final Shader brushShader;

    // state
    private Mode currentMode = Mode.ADD;
    private Shape currentShape = Shape.POINT;
    public Target target = Target.GROUND;
    private Texture currentBrush;
    public int brushIndex = 1;
    public int defaultGroundIndex = 0;
    public int defaultLiquidIndex = 0;
    public int groundIndex = 5;
    public int liquidIndex = 1;
    public boolean randomDegree = false;
    private boolean angleFollowPath = true;
    private boolean limitDrawingToTarget = true;
    private Color tint = Color.WHITE.clone();

    // point mode
    private final Vector2 point_lastSpawned = new Vector2();

    // circle mode
    private float circle_spreadRadius = 400;

    // line mode
    private boolean line_free = true;
    private final Vector2 line_start = new Vector2();
    private final Vector2 line_end = new Vector2();

    // polygon mode
    private boolean polygon_fill = true;
    private boolean polygon_free = true;
    private final Array<Vector2> polygon_points = new Array<>(true, 10);
    private final ArrayFloat polygon_flatTmp = new ArrayFloat(true, 10);
    private final Vector2 polygon_BottomLeft = new Vector2();
    private final Vector2 polygon_TopRight = new Vector2();
    private Shape2DPolygon polygon_shape;

    public Tool_1_Terrain(RPGMapMakerScene scene) {
        super(scene);

        // init brush stuff
        String groundVertexShaderSrc = Assets.getFileContent("assets/shaders/terrain-brush.vert");
        String groundFragmentShaderSrc = Assets.getFileContent("assets/shaders/terrain-brush.frag");
        this.brushShader = new Shader(groundVertexShaderSrc, groundFragmentShaderSrc);
        brushesAdd[0] = Assets.get("assets/brushes/brush_terrain_add_0.png");
        brushesAdd[1] = Assets.get("assets/brushes/brush_terrain_add_1.png");
        brushesAdd[2] = Assets.get("assets/brushes/brush_terrain_add_2.png");
        brushesAdd[3] = Assets.get("assets/brushes/brush_terrain_add_3.png");
        brushesAdd[4] = Assets.get("assets/brushes/brush_terrain_add_4.png");
        brushesAdd[5] = Assets.get("assets/brushes/brush_terrain_add_5.png");
        brushesAdd[6] = Assets.get("assets/brushes/brush_terrain_add_6.png");
        brushesAdd[7] = Assets.get("assets/brushes/brush_terrain_add_7.png");
        brushesAdd[8] = Assets.get("assets/brushes/brush_terrain_add_8.png");

        brushesSub[0] = Assets.get("assets/brushes/brush_terrain_sub_0.png");
        brushesSub[1] = Assets.get("assets/brushes/brush_terrain_sub_1.png");
        brushesSub[2] = Assets.get("assets/brushes/brush_terrain_sub_2.png");
        brushesSub[3] = Assets.get("assets/brushes/brush_terrain_sub_3.png");
        brushesSub[4] = Assets.get("assets/brushes/brush_terrain_sub_4.png");
        brushesSub[5] = Assets.get("assets/brushes/brush_terrain_sub_5.png");
        brushesSub[6] = Assets.get("assets/brushes/brush_terrain_sub_6.png");
        brushesSub[7] = Assets.get("assets/brushes/brush_terrain_sub_7.png");
        brushesSub[8] = Assets.get("assets/brushes/brush_terrain_sub_8.png");

        // init terrain stuff
        terrainGrounds[0] = Assets.get("assets/textures-layer-0/terrain_land_grass_0.jpg");
        terrainGrounds[1] = Assets.get("assets/textures-layer-0/terrain_land_grass_1.jpg");
        terrainGrounds[2] = Assets.get("assets/textures-layer-0/terrain_land_sand_0.jpg");
        terrainGrounds[3] = Assets.get("assets/textures-layer-0/terrain_land_stone_0.jpg");
        terrainGrounds[4] = Assets.get("assets/textures-layer-0/terrain_land_stone_1.jpg");
        terrainGrounds[5] = Assets.get("assets/textures-layer-0/terrain_land_dirt_0.jpg");
        terrainGrounds[6] = Assets.get("assets/textures-layer-0/terrain_land_dirt_1.jpg");
        terrainGrounds[7] = Assets.get("assets/textures-layer-0/terrain_land_road_0.jpg");
        terrainGrounds[8] = Assets.get("assets/textures-layer-0/terrain_land_rocks_0.jpg");
        terrainGrounds[9] = Assets.get("assets/textures-layer-0/terrain_land_parchment_0.jpg");
        terrainGrounds[10] = Assets.get("assets/textures-layer-0/terrain_land_parchment_1.jpg");

        terrainLiquids[0] = Assets.get("assets/textures-layer-0/terrain_liquid_water_0.jpg");
        terrainLiquids[1] = Assets.get("assets/textures-layer-0/terrain_liquid_water_1.jpg");
        terrainLiquids[2] = Assets.get("assets/textures-layer-0/terrain_liquid_water_2.jpg");
        terrainLiquids[3] = Assets.get("assets/textures-layer-0/terrain_liquid_water_3.jpg");

        // init
        sclX = 0.25f;
        sclY = 0.25f;
        Texture[] brushes = currentMode == Mode.ADD ? brushesAdd : brushesSub;
        currentBrush = brushes[brushIndex];
    }

    @Override
    public void activate() {
        this.active = true;
    }

    @Override
    public void deactivate() {
        this.active = false;
    }

    @Override
    public String getHelperText() {
        return "";
    }

    @Override
    public void update(float delta) {
        System.out.println(groundIndex);

        boolean shiftLeftJustPressed = Input.keyboard.isKeyJustPressed(Keyboard.Key.LEFT_SHIFT);
        boolean leftButtonPressed = Input.mouse.isButtonPressed(Mouse.Button.LEFT);
        boolean leftButtonJustPressed = Input.mouse.isButtonJustPressed(Mouse.Button.LEFT);
        boolean leftClick = Input.mouse.isButtonClicked(Mouse.Button.LEFT);
        boolean rightButtonJustPressed = Input.mouse.isButtonJustPressed(Mouse.Button.RIGHT);
        boolean mouseMoved = Input.mouse.moved();
        boolean scrollUp = Input.mouse.getScrollY() > 0;
        boolean scrollDown = Input.mouse.getScrollY() < 0;
        boolean backspaceJustPressed = Input.keyboard.isKeyJustPressed(Keyboard.Key.BACKSPACE);
        boolean plusPressed = Input.keyboard.isKeyPressed(Keyboard.Key.EQUAL);
        boolean minusPressed = Input.keyboard.isKeyPressed(Keyboard.Key.MINUS);
        boolean spaceJustPressed = Input.keyboard.isKeyJustPressed(Keyboard.Key.SPACE);
        boolean zButtonJustPressed = Input.keyboard.isKeyJustPressed(Keyboard.Key.Z);
        boolean aPressed = Input.keyboard.isKeyPressed(Keyboard.Key.A);
        boolean sPressed = Input.keyboard.isKeyPressed(Keyboard.Key.S);
        boolean lPressed = Input.keyboard.isKeyPressed(Keyboard.Key.L);
        boolean qPressed = Input.keyboard.isKeyPressed(Keyboard.Key.Q);
        float dy = Input.mouse.getYDelta();

        // =============  tool settings  ===============
        if (backspaceJustPressed) {
            this.currentMode = Collections.enumNext(this.currentMode);
            onChangeParameters();
            return;
        }
        if (spaceJustPressed) {
            brushIndex = (brushIndex + 1) % brushesAdd.length;
            onChangeParameters();
            return;
        }
        if (lPressed) {
            limitDrawingToTarget = !limitDrawingToTarget;
            return;
        }
        if (shiftLeftJustPressed) {
            currentShape = Collections.enumNext(this.currentShape);
            onChangeParameters();
            return;
        }

        if (plusPressed) {
            sclX *= 1.00f + 0.8f * Graphics.getDeltaTime();
            sclY *= 1.00f + 0.8f * Graphics.getDeltaTime();
            onChangeParameters();
            return;
        }
        if (minusPressed) {
            sclX *= 1.00f - 0.8f * Graphics.getDeltaTime();
            sclY *= 1.00f - 0.8f * Graphics.getDeltaTime();
            onChangeParameters();
            return;
        }

        if (rightButtonJustPressed) {
            target = Collections.enumNext(target);
            onChangeParameters();
            return;
        }
        if (scrollUp) {
            if (target == Target.GROUND) groundIndex = (groundIndex + 1) % terrainGrounds.length;
            else if (target == Target.LIQUID) liquidIndex = (liquidIndex + 1) % terrainLiquids.length;
            onChangeParameters();
            return;
        }
        if (scrollDown) {
            if (target == Target.GROUND) groundIndex = (groundIndex - 1 + terrainGrounds.length) % terrainGrounds.length;
            else if (target == Target.LIQUID) liquidIndex = (liquidIndex - 1 + terrainLiquids.length) % terrainLiquids.length;
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
        if (qPressed && dy != 0) {
            float deltaAlpha = -dy / Graphics.getWindowHeight();
            tint.a += deltaAlpha;
            tint.a = MathUtils.clampFloat(tint.a, 0, 1);
            System.out.println(tint.a);
            onChangeParameters();
            return;
        }


        // ********* Actions ************

        if (currentShape == Shape.POINT) {
            if (leftButtonJustPressed || (leftButtonPressed && mouseMoved)) {
                spawnTerrainCommand(x,y);
            }
            return;
        }

        if (currentShape == Shape.LINE) {
            if (line_free) {
                if (leftButtonJustPressed) {
                    line_start.set(x, y);
                    line_free = false;
                }
            } else {
                if (leftButtonJustPressed) {
                    // spawn
                    Vector2 step = new Vector2(x - line_start.x, y - line_start.y);
                    float length = step.len();
                    float stepSize = currentBrush.width * sclX * 0.25f;
                    int count = (int) (length / stepSize);
                    step.nor();
                    step.scl(stepSize);
                    for (int i = 0; i < count; i++) {
                        spawnTerrainCommand(line_start.x + i * step.x, line_start.y + i * step.y, angleFollowPath ? step.angleDeg() : this.deg);
                        spawnTerrainCommand(line_start.x + i * step.x, line_start.y + i * step.y, angleFollowPath ? step.angleDeg() : this.deg);
                        spawnTerrainCommand(line_start.x + i * step.x, line_start.y + i * step.y, angleFollowPath ? step.angleDeg() : this.deg);
                        spawnTerrainCommand(line_start.x + i * step.x, line_start.y + i * step.y, angleFollowPath ? step.angleDeg() : this.deg);
                    }
                    line_free = true;
                }
            }
        }

        if (currentShape == Shape.CIRCLE) {
            if (leftClick) {
                for (int angleDeg = 0; angleDeg < 360; angleDeg += 2) {
                    double rad = Math.toRadians(angleDeg);
                    float px = x + (float)(Math.cos(rad) * circle_spreadRadius);
                    float py = y + (float)(Math.sin(rad) * circle_spreadRadius);
                    spawnTerrainCommand(px, py, angleFollowPath ? angleDeg + 90 : this.deg);
                }
            }
            return;
        }

    }


    private void spawnTerrainCommand(float x, float y) {
        spawnTerrainCommand(x, y, this.deg);
    }

    private void spawnTerrainCommand(float x, float y, float deg) {
        CommandTerrain cmd = new CommandTerrain(x, y, deg, sclX, sclY, false); // TODO: anchor
        cmd.target = target;
        cmd.mode = currentMode;
        cmd.groundIndex = groundIndex;
        cmd.liquidIndex = liquidIndex;
        cmd.brushIndex = brushIndex;
        cmd.tint = tint.toFloatBits();
        map.addCommand(cmd);

        if (!limitDrawingToTarget && target != Target.BLEND_MAP) {
            CommandTerrain cmdBlend = new CommandTerrain(x, y, deg, sclX, sclY, false); // TODO: anchor
            cmdBlend.target = Target.BLEND_MAP;
            cmdBlend.mode = target == Target.GROUND ? Mode.ADD : Mode.SUB;
            cmdBlend.groundIndex = groundIndex;
            cmdBlend.liquidIndex = liquidIndex;
            cmdBlend.brushIndex = brushIndex;
            cmdBlend.tint = tint.toFloatBits();
            map.addCommand(cmdBlend);
        }

        if (randomDegree) this.deg = MathUtils.randomUniformFloat(0, 360);
    }

    @Override
    public void renderToolText(Renderer2D renderer2D, float x, float y) {
        if (currentShape == Shape.POLYGON) {
            renderer2D.drawStringLine("Polygon shapes not supported currently", 12, true, x, y, 0, 1, 1);
        }
    }

    @Override
    public void renderToolOverlay(Renderer2D renderer2D, float x, float y) {
        if (currentShape == Shape.POINT) {
            drawBrushPrediction(renderer2D, x, y);
        }

        if (currentShape == Shape.LINE) {
            if (line_free) {
                renderer2D.setColor(Color.BLUE);
                renderer2D.drawCircleThin(Math.max(12, 5), 10, x, y, 0,1,1);
                renderer2D.setColor(Color.WHITE);
                drawBrushPrediction(renderer2D, x, y);
            } else {
                renderer2D.setColor(Color.BLUE);
                renderer2D.drawCircleThin(Math.max(12, 5), 10, line_start.x, line_start.y, 0,1,1);
                renderer2D.drawLineThin(line_start.x, line_start.y, x, y);
                renderer2D.drawCircleThin(Math.max(12, 5), 10, x, y, 0,1,1);
                renderer2D.setColor(Color.WHITE);
                Vector2 step = new Vector2(x - line_start.x, y - line_start.y);
                float length = step.len();
                float stepSize = currentBrush.width * sclX * 0.25f;
                int count = (int) (length / stepSize);
                step.nor();
                step.scl(stepSize);
                for (int i = 0; i < count; i++) {
                    drawBrushPrediction(renderer2D, line_start.x + i * step.x, line_start.y + i * step.y, angleFollowPath ? step.angleDeg() : this.deg);
                }
            }
            return;
        }

        if (currentShape == Shape.CIRCLE) {
            renderer2D.setColor(Color.GREEN);
            renderer2D.drawCircleThin(circle_spreadRadius, 15, x, y, 0, 1, 1);
            renderer2D.setColor(Color.WHITE);
            for (int angleDeg = 0; angleDeg < 360; angleDeg += 2) {
                double rad = Math.toRadians(angleDeg);
                float px = x + (float)(Math.cos(rad) * circle_spreadRadius);
                float py = y + (float)(Math.sin(rad) * circle_spreadRadius);
                drawBrushPrediction(renderer2D, px, py, angleFollowPath ? angleDeg + 90 : this.deg);
            }
        }

        renderer2D.setShader(null);
    }

    private void drawBrushPrediction(Renderer2D renderer2D, float x, float y) {
        drawBrushPrediction(renderer2D, x, y, this.deg);
    }

    private void drawBrushPrediction(Renderer2D renderer2D, float x, float y, float deg) {
        groundIndex = groundIndex % terrainGrounds.length;
        liquidIndex = liquidIndex % terrainGrounds.length;
        if (target == Target.GROUND) {
            renderer2D.setShader(brushShader);
            Texture groundSrcImg = terrainGrounds[groundIndex];
            renderer2D.setShaderUniform("u_texture_reveal", groundSrcImg);
            renderer2D.setShaderUniform("u_width", groundSrcImg.width);
            renderer2D.setShaderUniform("u_height", groundSrcImg.height);
            renderer2D.drawTexture(currentBrush, x, y, deg, sclX, sclY);
        } else if (target == Target.LIQUID) {
            renderer2D.setShader(brushShader);
            Texture liquidSrcImg = terrainLiquids[liquidIndex];
            renderer2D.setShaderUniform("u_texture_reveal", liquidSrcImg);
            renderer2D.setShaderUniform("u_width", liquidSrcImg.width);
            renderer2D.setShaderUniform("u_height", liquidSrcImg.height);
            renderer2D.drawTexture(currentBrush, x, y, deg, sclX, sclY);
        } else { // target == blend map
            renderer2D.drawTexture(currentBrush, x, y, deg, sclX, sclY);
        }
    }

    @Override
    public void onChangeParameters() {
        Texture[] brushes = currentMode == Mode.ADD ? brushesAdd : brushesSub;
        currentBrush = brushes[brushIndex];
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

    public enum Target {
        GROUND,
        LIQUID,
        BLEND_MAP
    }

}
