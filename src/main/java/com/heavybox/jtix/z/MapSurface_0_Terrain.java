package com.heavybox.jtix.z;

import com.heavybox.jtix.assets.Assets;
import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.graphics.*;
import com.heavybox.jtix.z.tools_new.Tool_1_Terrain;
import com.heavybox.jtix.z.tools_new.Tool_new;
import org.lwjgl.opengl.GL11;

import static org.lwjgl.opengl.GL11.*;

public class MapSurface_0_Terrain implements MapSurface {

    public int width, height;

    private final FrameBuffer layer0; // <- final composite layer image
    private final FrameBuffer ground;
    private final FrameBuffer liquid;
    private final FrameBuffer blendMap;

    public final Camera camera;

    private int groundBaseTextureIndex = 0;
    private int liquidBaseTextureIndex = 2;
    private float uvScaleFactorGround = 1; // TODO
    private float uvScaleFactorLiquid = 1; // TODO
    private final Texture[] terrainGrounds = new Texture[9];
    private final Texture[] terrainLiquids = new Texture[3];

    private final Texture terrainSteepness;

    public Texture[] brushesAdd = new Texture[9];
    public Texture[] brushesSub = new Texture[9];

    private final Shader shader_brush;
    private final Shader shader_terrain;

    private boolean changed = true;

    private final Array<CommandTerrain> commandsBlendMap = new Array<>(true, 100);
    private final Array<CommandTerrain> commandsGround = new Array<>(true, 100);
    private final Array<CommandTerrain> commandsLiquid = new Array<>(true, 100);

    public MapSurface_0_Terrain(int width, int height) {
        this.width = width;
        this.height = height;

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
        terrainGrounds[5] = Assets.get("assets/textures-layer-0/terrain_land_dirt_0.jpg");
        terrainGrounds[6] = Assets.get("assets/textures-layer-0/terrain_land_dirt_1.jpg");
        terrainGrounds[7] = Assets.get("assets/textures-layer-0/terrain_land_road_0.jpg");
        terrainGrounds[8] = Assets.get("assets/textures-layer-0/terrain_land_rocks_0.jpg");

        terrainLiquids[0] = Assets.get("assets/textures-layer-0/terrain_liquid_water_0.jpg");
        terrainLiquids[1] = Assets.get("assets/textures-layer-0/terrain_liquid_water_1.jpg");
        terrainLiquids[2] = Assets.get("assets/textures-layer-0/terrain_liquid_water_2.jpg");

        terrainSteepness = Assets.get("assets/textures-layer-0/terrain_material_rock.jpg");

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

        String terrainVertexShaderSrc = Assets.getFileContent("assets/shaders/terrain.vert");
        String terrainFragmentShaderSrc = Assets.getFileContent("assets/shaders/terrain.frag");
        this.shader_terrain = new Shader(terrainVertexShaderSrc, terrainFragmentShaderSrc);

        String groundVertexShaderSrc = Assets.getFileContent("assets/shaders/terrain-brush.vert");
        String groundFragmentShaderSrc = Assets.getFileContent("assets/shaders/terrain-brush.frag");
        this.shader_brush = new Shader(groundVertexShaderSrc, groundFragmentShaderSrc);

        Graphics.bindFrameBuffer(blendMap);
        GL11.glClearColor(1,1,1,1);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

    }

    @Override
    public void executeCommand(Command command) {
        changed = true;

        if (command instanceof CommandTerrain) {
            CommandTerrain cmd = (CommandTerrain) command;
            if (cmd.target == Tool_1_Terrain.Target.GROUND) commandsGround.add(cmd);
            if (cmd.target == Tool_1_Terrain.Target.LIQUID) commandsLiquid.add(cmd);
            if (cmd.target == Tool_1_Terrain.Target.BLEND_MAP) commandsBlendMap.add(cmd);
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
        Graphics.bindFrameBuffer(ground);
        renderer2D.begin(camera);
        renderer2D.blendingSet(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ONE_MINUS_SRC_ALPHA);
        renderer2D.setShader(shader_brush);
        for (CommandTerrain cmd : commandsGround) {
            int groundIndex = cmd.groundIndex % terrainGrounds.length;
            Texture groundSrcImg = terrainGrounds[groundIndex];
            renderer2D.setShaderAttribute("u_texture_reveal", groundSrcImg);
            renderer2D.setShaderAttribute("u_width", uvScaleFactorGround * groundSrcImg.width);
            renderer2D.setShaderAttribute("u_height", uvScaleFactorGround * groundSrcImg.height);
            renderer2D.setColor(cmd.tint);
            renderer2D.drawTexture(brushesAdd[cmd.brushIndex], cmd.x, cmd.y, cmd.deg, cmd.sclX, cmd.sclY);
        }
        renderer2D.end();


        // render liquid image
        Graphics.bindFrameBuffer(liquid);
        renderer2D.begin(camera);
        renderer2D.blendingSet(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ONE_MINUS_SRC_ALPHA);
        renderer2D.setShader(shader_brush);
        for (CommandTerrain cmd : commandsLiquid) {
            int liquidIndex = cmd.liquidIndex % terrainLiquids.length;
            Texture liquidSrcImg = terrainLiquids[liquidIndex];
            renderer2D.setShaderAttribute("u_texture_reveal", liquidSrcImg);
            renderer2D.setShaderAttribute("u_width", uvScaleFactorLiquid * liquidSrcImg.width);
            renderer2D.setShaderAttribute("u_height", uvScaleFactorLiquid * liquidSrcImg.height);
            renderer2D.setColor(cmd.tint);
            renderer2D.drawTexture(brushesAdd[cmd.brushIndex],cmd.x,cmd.y,cmd.deg,cmd.sclX,cmd.sclY);
        }
        renderer2D.end();

        // render blend map
        Graphics.bindFrameBuffer(blendMap);
        renderer2D.begin(camera);
        renderer2D.blendingSet(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ONE_MINUS_SRC_ALPHA);
        for (CommandTerrain cmd : commandsBlendMap) {
            Texture brush = cmd.mode == Tool_new.Mode.ADD ? brushesAdd[cmd.brushIndex] : brushesSub[cmd.brushIndex];
            renderer2D.setColor(cmd.tint);
            renderer2D.drawTexture(brush,cmd.x,cmd.y,cmd.deg,cmd.sclX,cmd.sclY);
        }
        renderer2D.end();

        // render final image
        Graphics.bindFrameBuffer(layer0);
        GL11.glClearColor(0,0,0,1);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
        renderer2D.begin(camera);
        renderer2D.blendingSet(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ONE_MINUS_SRC_ALPHA);
        renderer2D.setShader(shader_terrain);
        renderer2D.setShaderAttribute("u_width_groundBase", uvScaleFactorGround * terrainGrounds[groundBaseTextureIndex].width);
        renderer2D.setShaderAttribute("u_height_groundBase", uvScaleFactorGround * terrainGrounds[groundBaseTextureIndex].height);
        renderer2D.setShaderAttribute("u_width_liquidBase", uvScaleFactorLiquid * terrainLiquids[liquidBaseTextureIndex].width);
        renderer2D.setShaderAttribute("u_height_liquidBase", uvScaleFactorLiquid * terrainLiquids[liquidBaseTextureIndex].height);
        renderer2D.setShaderAttribute("u_texture_ground_base", terrainGrounds[groundBaseTextureIndex]);
        renderer2D.setShaderAttribute("u_texture_ground", ground.getDefaultColorAttachment());
        renderer2D.setShaderAttribute("u_texture_liquid_base", terrainLiquids[liquidBaseTextureIndex]);
        renderer2D.setShaderAttribute("u_texture_liquid", liquid.getDefaultColorAttachment());
        renderer2D.setShaderAttribute("u_texture_steepness", terrainSteepness);
        renderer2D.setShaderAttribute("u_blendmap_width", blendMap.width);
        renderer2D.setShaderAttribute("u_blendmap_height", blendMap.height);
        renderer2D.drawTexture(blendMap.getDefaultColorAttachment(), 0,0,0,1,-1);
        renderer2D.setShader(null);
        renderer2D.end();

        commandsBlendMap.clear();
        commandsGround.clear();
        commandsLiquid.clear();
        changed = false;
    }

    @Override
    public void clear() {
        Graphics.bindFrameBuffer(blendMap);
        GL11.glClearColor(1,1,1,1);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

        Graphics.bindFrameBuffer(layer0);
        GL11.glClearColor(0,0,0,0);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
    }

    @Override
    public int getOrderIndex() {
        return 0;
    }

    @Override
    public Texture getTexture() {
        return layer0.getDefaultColorAttachment(); // for now.
    }

    public Texture getGroundTexture() {
        return ground.getDefaultColorAttachment();
    }

    public Texture getLiquidTexture() {
        return liquid.getDefaultColorAttachment();
    }

    public Texture getBlendMapTexture() {
        return blendMap.getDefaultColorAttachment();
    }

}
