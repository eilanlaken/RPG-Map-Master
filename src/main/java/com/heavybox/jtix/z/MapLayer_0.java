package com.heavybox.jtix.z;

import com.heavybox.jtix.assets.Assets;
import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.graphics.*;
import org.lwjgl.opengl.GL11;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL14.glBlendFuncSeparate;

public class MapLayer_0 implements MapLayer {

    private FrameBuffer layer0 = new FrameBuffer(1920, 1080);;
    private FrameBuffer terrainBlendMap = new FrameBuffer(1920, 1080); // <- draw roads here
    private FrameBuffer terrainMask = new FrameBuffer(1920, 1080); // <- draw terrain here
    public final Camera camera = new Camera(Camera.Mode.ORTHOGRAPHIC, 1920, 1080, 1, 0, 100, 75);

    private Texture terrainGrass;
    private Texture terrainWater;
    private Texture terrainSteepness;
    private Texture terrainRoad;

    public Texture terrainBrushSub;
    public Texture terrainBrushAdd;

    private Shader terrainShader;

    private Array<CommandTerrain> commandsHistory = new Array<>(true, 100);
    private Array<CommandTerrain> commandsQueueTerrainMask = new Array<>(true, 100);
    private Array<CommandTerrain> commandsQueueTerrainBlendMap = new Array<>(true, 100);

    private boolean changed = true;

    public MapLayer_0() {
        terrainGrass = Assets.get("assets/textures-layer-0/terrain-grass_1920x1080.png");
        terrainWater = Assets.get("assets/textures-layer-0/terrain-water_1920x1080.png");
        terrainSteepness = Assets.get("assets/textures-layer-0/terrain-rock_1920x1080.jpg");
        terrainBrushSub = new Texture("assets/tools/terrain-brush-erase.png");
        terrainBrushAdd = new Texture("assets/tools/terrain-brush-draw.png");

        String terrainVertexShaderSrc = Assets.getFileContent("assets/shaders/terrain-mask.vert");
        String terrainFragmentShaderSrc = Assets.getFileContent("assets/shaders/terrain-mask.frag");
        this.terrainShader = new Shader(terrainVertexShaderSrc, terrainFragmentShaderSrc);

        FrameBufferBinder.bind(terrainMask);
        GL11.glClearColor(1,1,1,1);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
    }

    @Override
    public void executeCommand(Command command) {
        changed = true;

        if (!(command instanceof CommandTerrain)) return;
        CommandTerrain cmd = (CommandTerrain) command;
        if (cmd.mode == ToolTerrain.Mode.ADD_LAND || cmd.mode == ToolTerrain.Mode.SUB_LAND) commandsQueueTerrainMask.add(cmd);
        if (cmd.mode == ToolTerrain.Mode.ADD_ROAD || cmd.mode == ToolTerrain.Mode.SUB_ROAD) commandsQueueTerrainBlendMap.add(cmd);
        commandsHistory.add(cmd);
    }

    @Override
    public void redraw(Renderer2D renderer2D) {
        // erase terrain mask

    }

    @Override
    public void applyChanges(Renderer2D renderer2D) {
        if (!changed) return;

        // update terrain mask
        FrameBufferBinder.bind(terrainMask);
        renderer2D.begin(camera);
        renderer2D.setBlending(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ONE_MINUS_SRC_ALPHA);
        for (CommandTerrain cmd : commandsQueueTerrainMask) {
            Texture texture = cmd.mode == ToolTerrain.Mode.ADD_LAND ? terrainBrushAdd : terrainBrushSub;
            renderer2D.drawTexture(texture, cmd.x, cmd.y, 0, cmd.sclX, cmd.sclY);
        }
        renderer2D.end();

        FrameBufferBinder.bind(layer0);
        GL11.glClearColor(0,0,0,1);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT); // should probably clear the stencil
        renderer2D.begin(camera);
        renderer2D.setBlending(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ONE_MINUS_SRC_ALPHA);
        renderer2D.drawTexture(terrainWater, 0, 0, 0, 1, -1);
        renderer2D.setShader(terrainShader);
        renderer2D.setShaderAttribute("u_texture_mask", terrainMask.getColorAttachment0());
        renderer2D.setShaderAttribute("u_texture_steepness", terrainSteepness);
        renderer2D.drawTexture(terrainGrass, 0, 0, 0, 1, -1);
        renderer2D.end();

        commandsQueueTerrainMask.clear();
        commandsQueueTerrainBlendMap.clear();

        changed = false;
    }

    @Override
    public Texture getTexture() {
        return layer0.getColorAttachment0(); // for now.
    }
}
