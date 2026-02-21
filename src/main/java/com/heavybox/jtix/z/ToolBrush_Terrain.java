package com.heavybox.jtix.z;

import com.heavybox.jtix.RPGMapMakerScene;
import com.heavybox.jtix.assets.Assets;
import com.heavybox.jtix.collections.Collections;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.graphics.Shader;
import com.heavybox.jtix.graphics.Texture;
import com.heavybox.jtix.input.Input;
import com.heavybox.jtix.input.Keyboard;
import com.heavybox.jtix.input.Mouse;

public class ToolBrush_Terrain extends Tool {

    public Target target = Target.GROUND;

    public Texture[] brushAdd = new Texture[3];
    public Texture[] brushSub = new Texture[3];

    private Shader brushShader;

    public int defaultGroundIndex = 0;
    public int defaultLiquidIndex = 0;
    public int groundIndex = 5;
    public int liquidIndex = 1;

    private Texture currentBrush;
    public int brushIndex = 1;

    // ground textures
    private final Texture[] terrainGrounds = new Texture[6];

    public ToolBrush_Terrain(RPGMapMakerScene scene) {
        super(scene);
        brushAdd[0] = Assets.get("assets/brushes/brush_terrain_add_0.png");
        brushSub[0] = Assets.get("assets/brushes/brush_terrain_sub_0.png");

        brushAdd[1] = Assets.get("assets/brushes/brush_terrain_add_1.png");
        brushSub[1] = Assets.get("assets/brushes/brush_terrain_sub_1.png");

        //brushAdd[2] = Assets.get("assets/brushes/brush_terrain_add_2.png");
        //brushSub[2] = Assets.get("assets/brushes/brush_terrain_sub_2.png");

        terrainGrounds[0] = Assets.get("assets/textures-layer-0/terrain_land_grass_0.jpg");
        terrainGrounds[1] = Assets.get("assets/textures-layer-0/terrain_land_grass_1.jpg");
        terrainGrounds[2] = Assets.get("assets/textures-layer-0/terrain_land_sand_0.jpg");
        terrainGrounds[3] = Assets.get("assets/textures-layer-0/terrain_land_stone_0.jpg");
        terrainGrounds[4] = Assets.get("assets/textures-layer-0/terrain_land_stone_1.jpg");
        terrainGrounds[5] = Assets.get("assets/textures-layer-0/terrain_land_road_0.jpg");

        String groundVertexShaderSrc = Assets.getFileContent("assets/shaders/terrain-brush.vert");
        String groundFragmentShaderSrc = Assets.getFileContent("assets/shaders/terrain-brush.frag");
        this.brushShader = new Shader(groundVertexShaderSrc, groundFragmentShaderSrc);

        sclX = 0.5f;
        sclY = 0.5f;

        onSetParameter();
    }

    @Override
    public void update(float delta) {
        boolean leftButtonPressed = Input.mouse.isButtonPressed(Mouse.Button.LEFT);
        boolean leftButtonJustPressed = Input.mouse.isButtonJustPressed(Mouse.Button.LEFT);
        boolean rightButtonJustPressed = Input.mouse.isButtonJustPressed(Mouse.Button.RIGHT);
        boolean mouseMoved = Input.mouse.moved();
        boolean scrollUp = Input.mouse.getVerticalScroll() > 0;
        boolean scrollDown = Input.mouse.getVerticalScroll() < 0;
        boolean spaceJustPressed = Input.keyboard.isKeyJustPressed(Keyboard.Key.SPACE);

        // TODO brush settings
        if (rightButtonJustPressed) {
            setTarget(Collections.enumNext(target));
            return;
        }

        if (spaceJustPressed) {
            setMode(Collections.enumNext(mode));
            return;
        }

        if (leftButtonJustPressed || (leftButtonPressed && mouseMoved)) {
            CommandTerrainAddSub cmd = new CommandTerrainAddSub(x, y, sclX, sclY, false); // TODO: anchor
            cmd.target = target;
            cmd.mode = mode;
            cmd.groundIndex = groundIndex;
            cmd.liquidIndex = liquidIndex;
            cmd.brushIndex = brushIndex;
            map.addCommand(cmd);
        }
    }

    @Override
    protected void onSetParameter() {
        // calculate current texture
        Texture[] brushes = mode == Mode.ADD ? brushAdd : brushSub;
        currentBrush = brushes[brushIndex];
    }

    public void setTarget(Target target) {
        this.target = target;
        System.out.println(target);
        onSetParameter();
    }

    @Override
    protected void onSetMode() {
        System.out.println(mode);
    }

    @Override
    public void renderToolOverlay(Renderer2D renderer2D, float x, float y) {
        renderer2D.setShader(brushShader);
        groundIndex = groundIndex % terrainGrounds.length;
        Texture groundSrcImg = terrainGrounds[groundIndex];
        renderer2D.setShaderAttribute("u_texture_reveal", groundSrcImg);
        renderer2D.setShaderAttribute("u_width", groundSrcImg.width);
        renderer2D.setShaderAttribute("u_height", groundSrcImg.height);
        renderer2D.drawTexture(currentBrush, x, y, deg, sclX, sclY);

        renderer2D.setShader(null);
    }

    @Override
    public void activate() {

    }

    @Override
    public void deactivate() {

    }

    @Override
    public String getName() {
        return "Tool - Terrain";
    }

    public enum Target {
        GROUND,
        LIQUID,
        BLEND_MAP
    }

}
