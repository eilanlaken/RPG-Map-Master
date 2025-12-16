package com.heavybox.jtix.z;

import com.heavybox.jtix.assets.Assets;
import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.graphics.*;
import org.lwjgl.opengl.GL11;

import static org.lwjgl.opengl.GL11.*;

public class MapLayer_0 implements MapLayer {

    private FrameBuffer layer0 = new FrameBuffer(1920, 1080);

    private FrameBuffer terrainBlendMap = new FrameBuffer(1920, 1080); // <- draw roads here
    private FrameBuffer terrainMask = new FrameBuffer(1920, 1080); // <- draw terrain here
    public final Camera camera = new Camera(Camera.Mode.ORTHOGRAPHIC, 1920, 1080, 1, 0, 100, 75);

    private Texture backgroundMorning;

    private final Texture[] terrainGrounds = new Texture[2];
    private int terrainGroundsIndex = 0;
    @Deprecated private final Texture terrainGrass;

    private final Texture[] terrainLiquids = new Texture[3];
    private int terrainLiquidsIndex = 0;
    @Deprecated private final Texture terrainWater;


    private final Texture terrainSteepness;
    private final Texture terrainStones;
    private final Texture terrainRoad;

    public Texture brushAdd;
    public Texture brushSub;

    private Shader terrainShader;

    private Array<CommandTerrainChangeEnvironment> commandTerrainChangeEnvironments = new Array<>(true, 10);
    private Array<CommandTerrain> commandsTerrainHistory = new Array<>(true, 100);
    private Array<CommandTerrain> commandsQueueTerrainMask = new Array<>(true, 100);
    private Array<CommandTerrain> commandsQueueTerrainBlendMapStone = new Array<>(true, 100);
    private Array<CommandTerrain> commandsQueueTerrainBlendMapRoad = new Array<>(true, 100);

    private boolean changed = true;

    public MapLayer_0() {
        terrainGrass = Assets.get("assets/textures-layer-0/terrain-grass_1920x1080.png");
        terrainGrounds[0] = Assets.get("assets/textures-layer-0/terrain-grass_1920x1080.png");
        terrainGrounds[1] = Assets.get("assets/textures-layer-0/terrain-slate_1920x1080.png");

        terrainWater = Assets.get("assets/textures-layer-0/terrain-water_1920x1080.png");
        terrainLiquids[0] = Assets.get("assets/textures-layer-0/terrain-water_1920x1080.png");
        terrainLiquids[1] = Assets.get("assets/textures-layer-0/terrain-lava_1920x1080.png");
        terrainLiquids[2] = Assets.get("assets/textures-layer-0/terrain-acid_1920x1080.png");

        terrainStones = Assets.get("assets/textures-layer-0/terrain-stones_1920x1080.png");
        terrainRoad = Assets.get("assets/textures-layer-0/terrain-road_1920x1080.png");
        terrainSteepness = Assets.get("assets/textures-layer-0/terrain-rock_1920x1080.jpg");
        brushSub = new Texture("assets/tools/terrain-brush-erase.png");
        brushAdd = new Texture("assets/tools/terrain-brush-draw.png");

        String terrainVertexShaderSrc = Assets.getFileContent("assets/shaders/terrain-mask.vert");
        String terrainFragmentShaderSrc = Assets.getFileContent("assets/shaders/terrain-mask.frag");
        this.terrainShader = new Shader(terrainVertexShaderSrc, terrainFragmentShaderSrc);

        FrameBufferBinder.bind(terrainMask);
        GL11.glClearColor(1,1,1,1);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

        // blendmap frame buffer
        terrainBlendMap = FrameBufferBuilder.begin()
                .setWidth(1920)
                .setHeight(1080)
                .addColorAttachment("attachment_0")
                .addColorAttachment("attachment_1")
                .end();
        FrameBufferBinder.bind(terrainBlendMap);
        //terrainBlendMap.setRenderTargets("attachment_0");
        GL11.glClearColor(0,0,0,1f);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
    }

    @Override
    public void executeCommand(Command command) {
        changed = true;

        if (command instanceof CommandTerrain) {
            CommandTerrain cmd = (CommandTerrain) command;
            if (cmd.target == ToolBrushTerrain.Target.TERRAIN) commandsQueueTerrainMask.add(cmd);
            if (cmd.target == ToolBrushTerrain.Target.FOREGROUND_STONE) commandsQueueTerrainBlendMapStone.add(cmd);
            if (cmd.target == ToolBrushTerrain.Target.FOREGROUND_ROAD) commandsQueueTerrainBlendMapRoad.add(cmd);
            commandsTerrainHistory.add(cmd);
            return;
        }

        if (command instanceof CommandTerrainChangeEnvironment) {
            CommandTerrainChangeEnvironment cmd = (CommandTerrainChangeEnvironment) command;
            if (cmd.type == CommandTerrainChangeEnvironment.Type.SELECT_NEXT_GROUND) {
                terrainGroundsIndex++;
                terrainGroundsIndex %= terrainGrounds.length;
            } else if (cmd.type == CommandTerrainChangeEnvironment.Type.SELECT_NEXT_LIQUID) {
                terrainLiquidsIndex++;
                terrainLiquidsIndex %= terrainLiquids.length;
            }
            commandTerrainChangeEnvironments.add(cmd);
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

        FrameBufferBinder.bind(terrainBlendMap);
        terrainBlendMap.setRenderTargets("attachment_0");
        renderer2D.begin(camera);
        renderer2D.setBlending(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ONE_MINUS_SRC_ALPHA);
        for (CommandTerrain cmd : commandsQueueTerrainBlendMapStone) {
            Texture texture = cmd.mode == ToolBrushTerrain.Mode.ADD ? brushAdd : brushSub;
            renderer2D.drawTexture(texture, cmd.x, cmd.y, 0, cmd.sclX, cmd.sclY);
        }
        renderer2D.end();

        terrainBlendMap.setRenderTargets("attachment_1");
        renderer2D.begin(camera);
        renderer2D.setBlending(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ONE_MINUS_SRC_ALPHA);
        for (CommandTerrain cmd : commandsQueueTerrainBlendMapRoad) {
            Texture texture = cmd.mode == ToolBrushTerrain.Mode.ADD ? brushAdd : brushSub;
            renderer2D.drawTexture(texture, cmd.x, cmd.y, 0, cmd.sclX, cmd.sclY);
        }
        renderer2D.end();


        // update terrain mask
        FrameBufferBinder.bind(terrainMask);
        renderer2D.begin(camera);
        renderer2D.setBlending(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ONE_MINUS_SRC_ALPHA);
        for (CommandTerrain cmd : commandsQueueTerrainMask) {
            Texture texture = cmd.mode == ToolBrushTerrain.Mode.ADD ? brushAdd : brushSub;
            renderer2D.drawTexture(texture, cmd.x, cmd.y, 0, cmd.sclX, cmd.sclY);
        }
        renderer2D.end();

        FrameBufferBinder.bind(layer0);
        GL11.glClearColor(0,0,0,1);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
        renderer2D.begin(camera);
        renderer2D.setBlending(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ONE_MINUS_SRC_ALPHA);
        renderer2D.drawTexture(terrainLiquids[terrainLiquidsIndex], 0, 0, 0, 1, -1);
        renderer2D.setShader(terrainShader);
        renderer2D.setShaderAttribute("u_texture_map_0", terrainBlendMap.getColorAttachment("attachment_0"));
        renderer2D.setShaderAttribute("u_texture_0", terrainStones);
        renderer2D.setShaderAttribute("u_texture_map_1", terrainBlendMap.getColorAttachment("attachment_1"));
        renderer2D.setShaderAttribute("u_texture_1", terrainRoad);

        renderer2D.setShaderAttribute("u_texture_mask", terrainMask.getColorAttachment0());
        renderer2D.setShaderAttribute("u_texture_steepness", terrainSteepness);
        renderer2D.drawTexture(terrainGrounds[terrainGroundsIndex], 0, 0, 0, 1, -1);

        renderer2D.setShader(null);
        renderer2D.setColor(1,1,1,0.2f);
        renderer2D.drawTexture(terrainBlendMap.getColorAttachment0(), 0,0,0,1,-1);

        renderer2D.end();

        commandsQueueTerrainMask.clear();
        commandsQueueTerrainBlendMapStone.clear();
        commandsQueueTerrainBlendMapRoad.clear();

        changed = false;
    }

    @Override
    public void clear() {
        FrameBufferBinder.bind(terrainMask);
        GL11.glClearColor(1,1,1,1);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

        FrameBufferBinder.bind(terrainBlendMap);
        terrainBlendMap.setRenderTargets("attachment_0");
        GL11.glClearColor(0,0,0,1f);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

        terrainBlendMap.setRenderTargets("attachment_1");
        GL11.glClearColor(0,0,0,1f);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

        FrameBufferBinder.bind(layer0);
        GL11.glClearColor(0,0,0,0);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
    }

    @Override
    public Texture getTexture() {
        //return terrainBlendMap.getColorAttachment("attachment_0");
        //return terrainBlendMap.getColorAttachment0();
        return layer0.getColorAttachment0(); // for now.
    }
}
