package com.heavybox.jtix.z.tools_new;

import com.heavybox.jtix.RPGMapMakerScene;
import com.heavybox.jtix.assets.Assets;
import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.collections.ArrayChar;
import com.heavybox.jtix.collections.ArrayFloat;
import com.heavybox.jtix.collections.Collections;
import com.heavybox.jtix.graphics.Graphics;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.graphics.Shader;
import com.heavybox.jtix.graphics.Texture;
import com.heavybox.jtix.input.Input;
import com.heavybox.jtix.input.Keyboard;
import com.heavybox.jtix.input.Mouse;
import com.heavybox.jtix.math.MathUtils;
import com.heavybox.jtix.math.Shape2DPolygon;
import com.heavybox.jtix.math.Vector2;
import com.heavybox.jtix.z.*;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class Tool_1_Terrain extends Tool_new {

    // brush
    public Target target = Target.GROUND;
    private final Shader brushShader;
    public final Texture[] brushesAdd = new Texture[2];
    public final Texture[] brushesSub = new Texture[2];
    private Texture currentBrush;
    public int brushIndex = 1;

    // terrain textures
    private final Texture[] terrainGrounds = new Texture[8];
    private final Texture[] terrainLiquids = new Texture[3];
    public int defaultGroundIndex = 0;
    public int defaultLiquidIndex = 0;
    public int groundIndex = 5;
    public int liquidIndex = 1;
    public boolean randomDegree = true;

    private Mode currentMode = Mode.ADD;
    private Shape currentShape = Shape.POINT;
    private final Set<Token> tokensToDelete = new HashSet<>();
    private final Array<Token> tokensPreview = new Array<>();
    private final Array<Token> alreadyCreatedTokens = new Array<>();
    private boolean angleFollowPath = true;

    // point mode

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
        brushesSub[0] = Assets.get("assets/brushes/brush_terrain_sub_0.png");
        brushesAdd[1] = Assets.get("assets/brushes/brush_terrain_add_1.png");
        brushesSub[1] = Assets.get("assets/brushes/brush_terrain_sub_1.png");

        // init terrain stuff
        terrainGrounds[0] = Assets.get("assets/textures-layer-0/terrain_land_grass_0.jpg");
        terrainGrounds[1] = Assets.get("assets/textures-layer-0/terrain_land_grass_1.jpg");
        terrainGrounds[2] = Assets.get("assets/textures-layer-0/terrain_land_sand_0.jpg");
        terrainGrounds[3] = Assets.get("assets/textures-layer-0/terrain_land_stone_0.jpg");
        terrainGrounds[4] = Assets.get("assets/textures-layer-0/terrain_land_stone_1.jpg");
        terrainGrounds[5] = Assets.get("assets/textures-layer-0/terrain_land_stone_2.jpg");
        terrainGrounds[6] = Assets.get("assets/textures-layer-0/terrain_land_dirt_0.jpg");
        terrainGrounds[7] = Assets.get("assets/textures-layer-0/terrain_land_road_0.jpg");
        terrainLiquids[0] = Assets.get("assets/textures-layer-0/terrain_liquid_water_0.jpg");
        terrainLiquids[1] = Assets.get("assets/textures-layer-0/terrain_liquid_water_1.jpg");
        terrainLiquids[2] = Assets.get("assets/textures-layer-0/terrain_liquid_water_0.jpg");

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

        // =============  tool settings  ===============
        if (backspaceJustPressed) {
            this.currentMode = Collections.enumNext(this.currentMode);
            onChangeParameters();
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

    }

    private void spawnTerrainCommand(float x, float y, float deg) {
        CommandTerrain cmd = new CommandTerrain(x, y, deg, sclX, sclY, false); // TODO: anchor
        cmd.target = target;
        cmd.mode = currentMode;
        cmd.groundIndex = groundIndex;
        cmd.liquidIndex = liquidIndex;
        cmd.brushIndex = brushIndex;
        map.addCommand(cmd);
        if (randomDegree) deg = MathUtils.randomUniformFloat(0, 360);
        else deg = 0;
    }

    @Override
    public void renderToolText(Renderer2D renderer2D, float x, float y) {

    }

    @Override
    public void renderToolOverlay(Renderer2D renderer2D, float x, float y) {
        if (currentShape == Shape.POINT) {
            drawBrushPrediction(renderer2D, x, y);
        }

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
    public void onChangeParameters() {
        Texture[] brushes = currentMode == Mode.ADD ? brushesAdd : brushesSub;
        currentBrush = brushes[brushIndex];
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

    public enum Target {
        GROUND,
        LIQUID,
        BLEND_MAP
    }

}
