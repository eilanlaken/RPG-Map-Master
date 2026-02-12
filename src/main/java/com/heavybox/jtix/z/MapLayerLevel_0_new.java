package com.heavybox.jtix.z;

import com.heavybox.jtix.assets.Assets;
import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.graphics.*;
import org.lwjgl.opengl.GL11;

import static org.lwjgl.opengl.GL11.*;

public class MapLayerLevel_0_new implements MapLayerLevel {

    public int width, height;

    private final FrameBuffer layer0; // <- final composite layer image
    private final FrameBuffer ground;
    private final FrameBuffer wheatFields;
    private final FrameBuffer liquid;
    private final FrameBuffer blendMap;

    public final Camera camera;

    private int groundBaseTextureIndex = 0;
    private int liquidBaseTextureIndex = 2;
    private float uvScaleFactorGround = 1; // TODO
    private float uvScaleFactorLiquid = 1; // TODO
    private final Texture[] terrainGrounds = new Texture[6];
    private final Texture[] terrainLiquids = new Texture[3];
    private final Texture[] bases = new Texture[5];

    private final Texture terrainSteepness;

    public Texture brushAdd;
    public Texture brushSub;

    private Shader groundShader;
    private Shader liquidShader;
    private Shader terrainShader;

    private boolean changed = true;

    private final Array<CommandTerrainTerraform_new> commandsBlendMap = new Array<>(true, 100);
    private final Array<CommandTerrainTerraform_new> commandsGround = new Array<>(true, 100);
    private final Array<CommandTerrainTerraform_new> commandsLiquid = new Array<>(true, 100);
    private final Array<CommandTerrainWheatFieldCreate> commandsWheatFields = new Array<>(true, 100);

    public MapLayerLevel_0_new(int width, int height) {
        this.width = width;
        this.height = height;

        //layer0 = new FrameBuffer(width, height);

        layer0 = FrameBufferBuilder.begin()
                .setWidth(width)
                .setHeight(height)
                .addColorAttachment("attachment_0")
                .end(); // <- draw terrain here
        blendMap = FrameBufferBuilder.begin()
                .setWidth(width)
                .setHeight(height)
                .addColorAttachment("attachment_0")
                .end(); // <- draw blend map here
        ground = FrameBufferBuilder.begin()
                .setWidth(width)
                .setHeight(height)
                .addColorAttachment("attachment_0")
                .end(); // <- draw ground textures (grass, roads, stones, ...) here
        wheatFields = FrameBufferBuilder.begin()
                .setWidth(width)
                .setHeight(height)
                .addColorAttachment("attachment_0")
                .end(); // <- wheat fields go here
        liquid = FrameBufferBuilder.begin()
                .setWidth(width)
                .setHeight(height)
                .addColorAttachment("attachment_0")
                .end(); // <- draw liquid textures (water, deep water, lava, ...) here

        camera = new Camera(Camera.Mode.ORTHOGRAPHIC, width, height, 1, 0, 100, 75);

        terrainGrounds[0] = Assets.get("assets/textures-layer-0/terrain_land_grass_0.jpg");
        terrainGrounds[1] = Assets.get("assets/textures-layer-0/terrain_land_grass_1.jpg");
        terrainGrounds[2] = Assets.get("assets/textures-layer-0/terrain_land_sand_0.jpg");
        terrainGrounds[3] = Assets.get("assets/textures-layer-0/terrain_land_stone_0.jpg");
        terrainGrounds[4] = Assets.get("assets/textures-layer-0/terrain_land_stone_1.jpg");
        terrainGrounds[5] = Assets.get("assets/textures-layer-0/terrain_land_road_0.jpg");

        bases[0] = Assets.get("assets/textures-layer-0/wheat_field_0.png");
        bases[1] = Assets.get("assets/textures-layer-0/wheat_field_1.png");
        bases[2] = Assets.get("assets/textures-layer-0/wheat_field_2.png");
        bases[3] = Assets.get("assets/textures-layer-0/wheat_field_3.png");
        bases[4] = Assets.get("assets/textures-layer-0/wheat_field_4.png");

        terrainLiquids[0] = Assets.get("assets/textures-layer-0/terrain_liquid_water_0.jpg");
        terrainLiquids[1] = Assets.get("assets/textures-layer-0/terrain_liquid_water_1.jpg");
        terrainLiquids[2] = Assets.get("assets/textures-layer-0/terrain_liquid_water_2.jpg");

        terrainSteepness = Assets.get("assets/textures-layer-0/terrain_material_rock.jpg");

        brushSub = Assets.get("assets/tools/terrain-brush-erase.png");
        brushAdd = Assets.get("assets/tools/terrain-brush-draw.png");

        // TODO: revise
        String terrainVertexShaderSrc = Assets.getFileContent("assets/shaders/terrain.vert");
        String terrainFragmentShaderSrc = Assets.getFileContent("assets/shaders/terrain.frag");
        this.terrainShader = new Shader(terrainVertexShaderSrc, terrainFragmentShaderSrc);

        String groundVertexShaderSrc = Assets.getFileContent("assets/shaders/terrain-ground.vert");
        String groundFragmentShaderSrc = Assets.getFileContent("assets/shaders/terrain-ground.frag");
        this.groundShader = new Shader(groundVertexShaderSrc, groundFragmentShaderSrc);

        String liquidVertexShaderSrc = Assets.getFileContent("assets/shaders/terrain-liquid.vert");
        String liquidFragmentShaderSrc = Assets.getFileContent("assets/shaders/terrain-liquid.frag");
        this.liquidShader = new Shader(liquidVertexShaderSrc, liquidFragmentShaderSrc);


        FrameBufferBinder.bind(blendMap);
        GL11.glClearColor(1,1,1,1);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

    }

    @Override
    public void executeCommand(Command command) {
        changed = true;

        if (command instanceof CommandTerrainTerraform_new) {
            CommandTerrainTerraform_new cmd = (CommandTerrainTerraform_new) command;
            if (cmd.target == ToolBrush_Terrain.Target.GROUND) commandsGround.add(cmd);
            if (cmd.target == ToolBrush_Terrain.Target.BLEND_MAP) commandsBlendMap.add(cmd);
            return;
        }

        if (command instanceof CommandTerrainWheatFieldCreate) {
            CommandTerrainWheatFieldCreate cmd = (CommandTerrainWheatFieldCreate) command;
            commandsWheatFields.add(cmd);
            return;
        }

    }

    @Override
    public void redraw(Renderer2D renderer2D) {
        // erase terrain mask

    }

    @Override
    public void applyChanges(Renderer2D renderer2D) {
        if (!changed) return;

        // render ground image
        FrameBufferBinder.bind(ground);
        renderer2D.begin(camera);
        renderer2D.blendingSet(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ONE_MINUS_SRC_ALPHA);
        renderer2D.setShader(groundShader);
        for (CommandTerrainTerraform_new cmd : commandsGround) {
            int groundIndex = cmd.groundIndex % terrainGrounds.length;
            Texture groundSrcImg = terrainGrounds[groundIndex];
            renderer2D.setShaderAttribute("u_texture_reveal", groundSrcImg);
            renderer2D.setShaderAttribute("u_width", uvScaleFactorGround * groundSrcImg.width);
            renderer2D.setShaderAttribute("u_height", uvScaleFactorGround * groundSrcImg.height);
            renderer2D.drawTexture(brushAdd,cmd.x,cmd.y,0,cmd.sclX,cmd.sclY);
        }
        renderer2D.end();

        // render liquid image
        FrameBufferBinder.bind(liquid);
        renderer2D.begin(camera);
        renderer2D.blendingSet(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ONE_MINUS_SRC_ALPHA);
        renderer2D.setShader(liquidShader);
        for (CommandTerrainTerraform_new cmd : commandsLiquid) {
            int liquidIndex = cmd.liquidIndex % terrainLiquids.length;
            Texture liquidSrcImg = terrainLiquids[liquidIndex];
            renderer2D.setShaderAttribute("u_texture_reveal", liquidSrcImg);
            renderer2D.setShaderAttribute("u_width", uvScaleFactorLiquid * liquidSrcImg.width);
            renderer2D.setShaderAttribute("u_height", uvScaleFactorLiquid * liquidSrcImg.height);
            renderer2D.drawTexture(brushAdd,cmd.x,cmd.y,0,cmd.sclX,cmd.sclY);
        }
        renderer2D.end();

        FrameBufferBinder.bind(wheatFields);
        renderer2D.begin(camera);
        renderer2D.blendingSet(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ONE_MINUS_SRC_ALPHA);
        for (CommandTerrainWheatFieldCreate cmd : commandsWheatFields) {
            System.out.println("hi");
            renderer2D.setColor(0.396f, 0.263f, 0.129f, 0.4f);
            renderer2D.drawCurveFilled(null, 11.0f, 20, cmd.polygon, 0, 0, 0, 1, 1);
            renderer2D.setColor(Color.WHITE);
            renderer2D.drawPolygonFilled(cmd.polygon, bases[cmd.baseType], uv -> uv.rotateDeg(cmd.linesAngle).scl(2), 0, 0, 0, 1, 1);
        }
        renderer2D.end();

        // render blend map
        FrameBufferBinder.bind(blendMap);
        renderer2D.begin(camera);
        renderer2D.blendingSet(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ONE_MINUS_SRC_ALPHA);
        for (CommandTerrainTerraform_new cmd : commandsBlendMap) {
            Texture brush = cmd.mode == Tool.Mode.ADD ? brushAdd : brushSub;
            renderer2D.drawTexture(brush,cmd.x,cmd.y,0,cmd.sclX,cmd.sclY);
        }
        renderer2D.end();

        // render final image
        FrameBufferBinder.bind(layer0);
        GL11.glClearColor(0,0,0,1);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
        renderer2D.begin(camera);
        renderer2D.blendingSet(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ONE_MINUS_SRC_ALPHA);
        renderer2D.setShader(terrainShader);
        renderer2D.setShaderAttribute("u_width_groundBase", uvScaleFactorGround * terrainGrounds[groundBaseTextureIndex].width);
        renderer2D.setShaderAttribute("u_height_groundBase", uvScaleFactorGround * terrainGrounds[groundBaseTextureIndex].height);
        renderer2D.setShaderAttribute("u_width_liquidBase", uvScaleFactorLiquid * terrainLiquids[liquidBaseTextureIndex].width);
        renderer2D.setShaderAttribute("u_height_liquidBase", uvScaleFactorLiquid * terrainLiquids[liquidBaseTextureIndex].height);
        renderer2D.setShaderAttribute("u_texture_ground_base", terrainGrounds[groundBaseTextureIndex]);
        renderer2D.setShaderAttribute("u_texture_ground", ground.getDefaultColorAttachment());
        renderer2D.setShaderAttribute("u_texture_wheatFields", wheatFields.getDefaultColorAttachment());
        renderer2D.setShaderAttribute("u_texture_liquid_base", terrainLiquids[liquidBaseTextureIndex]);
        renderer2D.setShaderAttribute("u_texture_liquid", liquid.getDefaultColorAttachment());
        renderer2D.setShaderAttribute("u_texture_steepness", terrainSteepness);
        renderer2D.setShaderAttribute("u_blendmap_width", blendMap.width);
        renderer2D.setShaderAttribute("u_blendmap_height", blendMap.height);
        renderer2D.drawTexture(blendMap.getDefaultColorAttachment(), 0,0,0,1,-1);
        renderer2D.setShader(null);
        //renderer2D.drawTexture(wheatFields.getDefaultColorAttachment(), 0,0,0,1,-1);
        renderer2D.end();

//        FrameBufferBinder.bind(layer0);
//        GL11.glClearColor(0,0,0,1);
//        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
//        renderer2D.begin(camera);
//        renderer2D.drawTexture(wheatFields.getDefaultColorAttachment(), 0,0,0,1,-1);
//        renderer2D.end();

        commandsBlendMap.clear();
        commandsGround.clear();
        commandsLiquid.clear();
        commandsWheatFields.clear();
        changed = false;
    }

    @Override
    public void clear() {
        FrameBufferBinder.bind(blendMap);
        GL11.glClearColor(1,1,1,1);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

//        FrameBufferBinder.bind(ground);
//        terrainBlendMap.setRenderTargets("attachment_0");
//        GL11.glClearColor(0,0,0,1f);
//        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
//
//        terrainBlendMap.setRenderTargets("attachment_1");
//        GL11.glClearColor(0,0,0,1f);
//        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

        FrameBufferBinder.bind(layer0);
        GL11.glClearColor(0,0,0,0);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
    }

    @Override
    public Texture getTexture() {
        return layer0.getDefaultColorAttachment(); // for now.
    }
}
