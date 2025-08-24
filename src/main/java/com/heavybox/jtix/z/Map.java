package com.heavybox.jtix.z;

import com.heavybox.jtix.assets.Assets;
import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.graphics.*;
import org.lwjgl.opengl.GL11;

public class Map {

    // Terrain layer
    public final Camera terrainMaskCamera = new Camera(Camera.Mode.ORTHOGRAPHIC, 1920, 1080, 1, 0, 100, 75);
    private String terrainVertexShaderSrc = Assets.getFileContent("assets/shaders/terrain-mask.vert");
    private String terrainFragmentShaderSrc = Assets.getFileContent("assets/shaders/terrain-mask.frag");
    private Shader terrainShader = new Shader(terrainVertexShaderSrc, terrainFragmentShaderSrc);
    private FrameBuffer layer0TerrainMask = new FrameBuffer(1920, 1080);;
    private FrameBuffer layer0TerrainBlendMap = new FrameBuffer(1920, 1080);
    private FrameBuffer layer0 = new FrameBuffer(1920, 1080);
    private Texture terrainGrass;
    private Texture terrainWater;
    private Texture terrainSteepness;
    private Texture terrainRoad;

    // Ground layer (wheat fields)
    public MapLayer_3 layer3;

    // Decorations layer

    // Text layer

    public FrameBuffer mapFinal = new FrameBuffer(1920, 1080);

    private int commandsIndex = 0;
    public Array<Command> commandsHistory = new Array<>(true, 10);
    public Array<Command> commandsQueue = new Array<>(true, 10);

    public final Camera camera = new Camera(Camera.Mode.ORTHOGRAPHIC, 1920, 1080, 1, 0, 100, 75);


    private boolean needsRedraw = false;

    public Map(boolean initEmpty) {

        layer3 = new MapLayer_3();

        if (initEmpty) {
            FrameBufferBinder.bind(layer0TerrainMask);
            GL11.glClearColor(0,0,0,0);
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_STENCIL_BUFFER_BIT); // should probably clear the stencil
        } else {
            FrameBufferBinder.bind(layer0TerrainMask);
            GL11.glClearColor(1,1,1,1);
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_STENCIL_BUFFER_BIT); // should probably clear the stencil
        }
    }

    public void addCommand(Command command) {
        commandsQueue.add(command);
    }

    public void update(float delta) {
        // execute command queue
        for (Command command : commandsQueue) {
            executeCommand(command);
        }
        // add all commands in the command queue to history
        commandsHistory.addAll(commandsQueue);
        // clear command queue
        commandsQueue.clear();
    }

    private void executeCommand(Command command) {

    }

    private void undo() {

    }

    private void redo() {

    }

    public void render(Renderer2D renderer2D) {
        layer3.redraw(renderer2D);

        FrameBufferBinder.bind(mapFinal);
        GL11.glClearColor(1.0f,0.0f,0.0f,1);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_STENCIL_BUFFER_BIT); // should probably clear the stencil
        renderer2D.begin(camera);
        // render layer-0
        renderer2D.drawTexture(layer0.getColorAttachment0(), 0, 0, 0, 1,1);
        // render layer-1
        // render layer-2
        // render layer-3
        renderer2D.drawTexture(layer3.getTexture(), 0, 0, 0, 1,1);

        // render layer-4
        renderer2D.end();
        needsRedraw = false;
    }

    public Texture getTexture() {
        return mapFinal.getColorAttachment0();
    }

}
