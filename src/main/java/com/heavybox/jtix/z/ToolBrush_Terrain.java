package com.heavybox.jtix.z;

import com.heavybox.jtix.RPGMapMakerScene;
import com.heavybox.jtix.assets.Assets;
import com.heavybox.jtix.collections.Collections;
import com.heavybox.jtix.graphics.Graphics;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.graphics.Shader;
import com.heavybox.jtix.graphics.Texture;
import com.heavybox.jtix.input.Input;
import com.heavybox.jtix.input.Keyboard;
import com.heavybox.jtix.input.Mouse;
import com.heavybox.jtix.math.MathUtils;
import com.heavybox.jtix.math.Vector2;
import org.jetbrains.annotations.NotNull;

public class ToolBrush_Terrain extends Tool {

    public Target target = Target.GROUND;

    public Texture[] brushesAdd = new Texture[2];
    public Texture[] brushesSub = new Texture[2];

    private final Shader brushShader;

    public int defaultGroundIndex = 0;
    public int defaultLiquidIndex = 0;
    public int groundIndex = 5;
    public int liquidIndex = 1;
    public boolean randomDegree = true;

    private Texture currentBrush;
    public int brushIndex = 1;

    // specific line state
    private final Vector2 lineStart = new Vector2();
    private final Vector2 lineEnd = new Vector2();
    private LineModeState lineModeState = LineModeState.FREE;
    private float lineMouseX;
    private float lineMouseY;

    // ground textures
    private final Texture[] terrainGrounds = new Texture[7];
    private final Texture[] terrainLiquids = new Texture[3];

    public ToolBrush_Terrain(RPGMapMakerScene scene) {
        super(scene);
        brushesAdd[0] = Assets.get("assets/brushes/brush_terrain_add_0.png");
        brushesSub[0] = Assets.get("assets/brushes/brush_terrain_sub_0.png");

        brushesAdd[1] = Assets.get("assets/brushes/brush_terrain_add_1.png");
        brushesSub[1] = Assets.get("assets/brushes/brush_terrain_sub_1.png");

//        brushesAdd[2] = Assets.get("assets/brushes/brush_terrain_add_2.png");
//        brushesSub[2] = Assets.get("assets/brushes/brush_terrain_sub_2.png");

        terrainGrounds[0] = Assets.get("assets/textures-layer-0/terrain_land_grass_0.jpg");
        terrainGrounds[1] = Assets.get("assets/textures-layer-0/terrain_land_grass_1.jpg");
        terrainGrounds[2] = Assets.get("assets/textures-layer-0/terrain_land_sand_0.jpg");
        terrainGrounds[3] = Assets.get("assets/textures-layer-0/terrain_land_stone_0.jpg");
        terrainGrounds[4] = Assets.get("assets/textures-layer-0/terrain_land_stone_1.jpg");
        terrainGrounds[5] = Assets.get("assets/textures-layer-0/terrain_land_road_0.jpg");
        terrainGrounds[6] = Assets.get("assets/textures-layer-0/terrain_land_dirt_0.jpg");

        terrainLiquids[0] = Assets.get("assets/textures-layer-0/terrain_liquid_water_0.jpg");
        terrainLiquids[1] = Assets.get("assets/textures-layer-0/terrain_liquid_water_1.jpg");
        terrainLiquids[2] = Assets.get("assets/textures-layer-0/terrain_liquid_water_0.jpg");

        String groundVertexShaderSrc = Assets.getFileContent("assets/shaders/terrain-brush.vert");
        String groundFragmentShaderSrc = Assets.getFileContent("assets/shaders/terrain-brush.frag");
        this.brushShader = new Shader(groundVertexShaderSrc, groundFragmentShaderSrc);

        sclX = 0.5f;
        sclY = 0.5f;
        shape = Shape.CIRCLE;

        onSetParameter();
    }

    @Override
    public void update(float delta) {
        boolean shiftLeftJustPressed = Input.keyboard.isKeyJustPressed(Keyboard.Key.LEFT_SHIFT);
        boolean leftButtonPressed = Input.mouse.isButtonPressed(Mouse.Button.LEFT);
        boolean leftButtonJustPressed = Input.mouse.isButtonJustPressed(Mouse.Button.LEFT);
        boolean leftButtonJustClicked = Input.mouse.isButtonClicked(Mouse.Button.LEFT);
        boolean rightButtonJustPressed = Input.mouse.isButtonJustPressed(Mouse.Button.RIGHT);
        boolean mouseMoved = Input.mouse.moved();
        boolean scrollUp = Input.mouse.getScrollY() > 0;
        boolean scrollDown = Input.mouse.getScrollY() < 0;
        boolean backspaceJustPressed = Input.keyboard.isKeyJustPressed(Keyboard.Key.BACKSPACE);
        boolean plus = Input.keyboard.isKeyJustPressed(Keyboard.Key.EQUAL);
        boolean minus = Input.keyboard.isKeyJustPressed(Keyboard.Key.MINUS);
        boolean spaceJustPressed = Input.keyboard.isKeyJustPressed(Keyboard.Key.SPACE);
        boolean zButtonJustPressed = Input.keyboard.isKeyJustPressed(Keyboard.Key.Z);
        boolean aPressed = Input.keyboard.isKeyPressed(Keyboard.Key.A);
        boolean sPressed = Input.keyboard.isKeyPressed(Keyboard.Key.S);

        if (plus) {
            if (target == Target.GROUND) groundIndex = (groundIndex + 1) % terrainGrounds.length;
            else if (target == Target.LIQUID) liquidIndex = (liquidIndex + 1) % terrainLiquids.length;
            return;
        } else if (minus) {
            if (target == Target.GROUND) {
                groundIndex--;
                if (groundIndex == -1) groundIndex = terrainGrounds.length - 1;
            }
            else if (target == Target.LIQUID) {
                liquidIndex--;
                if (liquidIndex == -1) liquidIndex = terrainLiquids.length - 1;
            }
            return;
        }

        if (spaceJustPressed) {
            brushIndex = (brushIndex + 1) % brushesAdd.length;
            onSetParameter();
            return;
        }

        if (shiftLeftJustPressed) {
            setShape(Collections.enumNext(shape));
            return;
        }

        if (sPressed) {
            sclX *= 1.00f + 0.8f * Graphics.getDeltaTime();
            sclY *= 1.00f + 0.8f * Graphics.getDeltaTime();
        }
        if (aPressed) {
            sclX *= 1.00f - 0.8f * Graphics.getDeltaTime();
            sclY *= 1.00f - 0.8f * Graphics.getDeltaTime();
        }

        // TODO brush settings
        if (zButtonJustPressed) {
            setTarget(Collections.enumNext(target));
            return;
        }

        if (backspaceJustPressed) {
            setMode(Collections.enumNext(mode));
            onSetParameter();
            return;
        }

        if (shape == Shape.POINT) {
            if (leftButtonJustPressed) {
                CommandTerrainAddSub cmd = new CommandTerrainAddSub(x, y, sclX, sclY, false); // TODO: anchor
                cmd.target = target;
                cmd.mode = mode;
                cmd.groundIndex = groundIndex;
                cmd.liquidIndex = liquidIndex;
                cmd.brushIndex = brushIndex;
                map.addCommand(cmd);
                if (randomDegree) deg = MathUtils.randomUniformFloat(0,360);
                else deg = 0;
            }
            return;
        }

        if (shape == Shape.CIRCLE) {
            if (leftButtonJustPressed || (leftButtonPressed && mouseMoved)) {
                CommandTerrainAddSub cmd = new CommandTerrainAddSub(x, y, sclX, sclY, false); // TODO: anchor
                cmd.target = target;
                cmd.mode = mode;
                cmd.groundIndex = groundIndex;
                cmd.liquidIndex = liquidIndex;
                cmd.brushIndex = brushIndex;
                map.addCommand(cmd);
                if (randomDegree) deg = MathUtils.randomUniformFloat(0, 360);
                else deg = 0;
            }
            return;
        }

        if (shape == Shape.LINE) {
            if (lineModeState == LineModeState.FREE) {
                if (leftButtonJustClicked) {
                    lineStart.set(x, y);
                    lineModeState = LineModeState.CREATING_LINE;
                }
                return;
            }
            if (lineModeState == LineModeState.CREATING_LINE) {
                if (rightButtonJustPressed) { // cancel
                    lineModeState = LineModeState.FREE;
                    return;
                }
                if (leftButtonJustClicked) {
                    lineEnd.set(x, y);
                    lineModeState = LineModeState.DRAWING_ALONG_CREATED_LINE;
                }
            }
            if (lineModeState == LineModeState.DRAWING_ALONG_CREATED_LINE) {
                // snap mouse back to line
                // TODO: this does not work for upright lines.
                // better would be to parametrize the curve.
                lineMouseX = MathUtils.clampFloat(x, lineStart.x, lineEnd.x);
                float slope = (lineEnd.y - lineStart.y) / (lineEnd.x - lineStart.x);
                float n = lineEnd.y - slope * lineEnd.x;
                lineMouseY = slope * lineMouseX + n;

                if (rightButtonJustPressed) { // cancel
                    lineModeState = LineModeState.FREE;
                    return;
                }

                if (leftButtonJustPressed || (leftButtonPressed && mouseMoved)) {
                    CommandTerrainAddSub cmd = new CommandTerrainAddSub(lineMouseX, lineMouseY, sclX, sclY, false); // TODO: anchor
                    cmd.target = target;
                    cmd.mode = mode;
                    cmd.groundIndex = groundIndex;
                    cmd.liquidIndex = liquidIndex;
                    cmd.brushIndex = brushIndex;
                    map.addCommand(cmd);
                    if (randomDegree) deg = MathUtils.randomUniformFloat(0, 360);
                    else deg = 0;
                }

            }
            return;
        }

        // TODO: later
        if (shape == Shape.POLYGON) {

            return;
        }
    }

    @Override
    protected void onSetParameter() {
        // calculate current texture
        Texture[] brushes = mode == Mode.ADD ? brushesAdd : brushesSub;
        currentBrush = brushes[brushIndex];
    }

    public void setTarget(Target target) {
        this.target = target;
        onSetParameter();
    }

    @Override
    protected void onSetMode() {
        System.out.println(mode);
    }

    @Override
    public String getHelperText() {
        return  super.getHelperText() + " | " +
                "Shape: " + shape.name() + "(LSHIFT) | " +
                "Mode: " + mode.name() + " (BACKSPACE) |" +
                "Brush Texture: " + mode.name() + " (SPACE) |" +
                "Target: " + target.name() + " (Z) |" +
                ((target == Target.GROUND) ? "Ground Index " + groundIndex + " (-+) |" : "") +
                ((target == Target.LIQUID) ? "Liquid Index " + liquidIndex + " (-+) |" : "") +
                "Scale: " + String.format("%.2f", sclX) + " (a & s) |";
    }

    @Override
    public void renderToolText(Renderer2D renderer2D, float x, float y) {
        renderer2D.drawStringLine("Currently Drawing on " + target.name(), 12, true, x + 50, y + 50, 0, 1,1);
    }

    @Override
    public void renderToolOverlay(@NotNull Renderer2D renderer2D, float x, float y) {

        if (shape == Shape.POINT) {
            drawBrushPrediction(renderer2D, x, y);
        }

        if (shape == Shape.CIRCLE) {
            drawBrushPrediction(renderer2D, x, y);
        }

        if (shape == Shape.LINE) {
            renderer2D.setShader(null);
            if (lineModeState == LineModeState.FREE) {
                renderer2D.drawCircleFilled(20,10,x,y,0,1,1);
                return;
            }
            if (lineModeState == LineModeState.CREATING_LINE) {
                renderer2D.drawCircleFilled(20,10,lineStart.x,lineStart.y,0,1,1);
                renderer2D.drawLineThin(lineStart.x, lineStart.y, x, y);
                return;
            }
            if (lineModeState == LineModeState.DRAWING_ALONG_CREATED_LINE) {
                renderer2D.drawCircleFilled(20,10,lineStart.x,lineStart.y,0,1,1);
                renderer2D.drawLineThin(lineStart.x, lineStart.y, lineEnd.x, lineEnd.y);
                renderer2D.drawCircleFilled(20,10,lineEnd.x,lineEnd.y,0,1,1);
                drawBrushPrediction(renderer2D, lineMouseX, lineMouseY);
                return;
            }
        }

        if (shape == Shape.POLYGON) {
            // ignore for now.
        }

        renderer2D.setShader(null);
    }

    private void drawBrushPrediction(Renderer2D renderer2D, float x, float y) {
        groundIndex = groundIndex % terrainGrounds.length;
        liquidIndex = liquidIndex % terrainGrounds.length;
        if (target == Target.GROUND) {
            renderer2D.setShader(brushShader);
            Texture groundSrcImg = terrainGrounds[groundIndex];
            renderer2D.setShaderAttribute("u_texture_reveal", groundSrcImg);
            renderer2D.setShaderAttribute("u_width", groundSrcImg.width);
            renderer2D.setShaderAttribute("u_height", groundSrcImg.height);
            renderer2D.drawTexture(currentBrush, x, y, deg, sclX, sclY);
        } else if (target == Target.LIQUID) {
            renderer2D.setShader(brushShader);
            Texture liquidSrcImg = terrainLiquids[liquidIndex];
            renderer2D.setShaderAttribute("u_texture_reveal", liquidSrcImg);
            renderer2D.setShaderAttribute("u_width", liquidSrcImg.width);
            renderer2D.setShaderAttribute("u_height", liquidSrcImg.height);
            renderer2D.drawTexture(currentBrush, x, y, deg, sclX, sclY);
        } else { // target == blend map
            renderer2D.drawTexture(currentBrush, x, y, deg, sclX, sclY);
        }
        renderer2D.setShader(null);
    }

    @Override
    protected void onSetShape() {
        lineModeState = LineModeState.FREE;
    }

    @Override
    public void activate() {
        System.out.println("Active Tool - " + getName());
    }

    @Override
    public void deactivate() {}

    @Override
    public String getName() {
        return "Terrain Brush";
    }

    public enum Target {
        GROUND,
        LIQUID,
        BLEND_MAP
    }

    private enum LineModeState {
        FREE,
        CREATING_LINE,
        DRAWING_ALONG_CREATED_LINE
    }

}
