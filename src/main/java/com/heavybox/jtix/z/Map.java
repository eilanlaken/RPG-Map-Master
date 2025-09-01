package com.heavybox.jtix.z;

import com.heavybox.jtix.assets.Assets;
import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.graphics.*;
import org.lwjgl.opengl.GL11;

public class Map {


    public MapLayer_0 layer0; // Terrain layer (wheat fields)
    public MapLayer_1 layer1; // Ground layer (wheat fields)
    public MapLayer_3 layer3; // Token layer

    // Decorations layer

    // Text layer

    public FrameBuffer mapFinal = new FrameBuffer(1920, 1080);

    private int commandsIndex = 0;
    public Array<Command> commandsHistory = new Array<>(true, 10);
    public Array<Command> commandsQueue = new Array<>(true, 10);

    public final Camera camera = new Camera(Camera.Mode.ORTHOGRAPHIC, 1920, 1080, 1, 0, 100, 75);


    private boolean needsRedraw = false;

    public Map(boolean initEmpty) {

        layer0 = new MapLayer_0();
        layer3 = new MapLayer_3();

//        if (initEmpty) {
//            FrameBufferBinder.bind(layer0TerrainMask);
//            GL11.glClearColor(0,0,0,0);
//            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_STENCIL_BUFFER_BIT); // should probably clear the stencil
//        } else {
//            FrameBufferBinder.bind(layer0TerrainMask);
//            GL11.glClearColor(1,1,1,1);
//            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_STENCIL_BUFFER_BIT); // should probably clear the stencil
//        }
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

    public void getAllTokens(MapToken.Type ofType, Array<MapToken> out) {
        out.clear();
        for (MapToken mapToken : layer3.allTokens) {
            if (mapToken.type == ofType) out.add(mapToken);
        }
    }

    private void executeCommand(Command command) {
        if (command.layer == 0) layer0.executeCommand(command);
        if (command.layer == 3) layer3.executeCommand(command);
    }

    private void undo() {

    }

    private void redo() {

    }

    public void render(Renderer2D renderer2D) {
        layer0.applyChanges(renderer2D);
        layer3.applyChanges(renderer2D); // TODO: use applyChanges

        FrameBufferBinder.bind(mapFinal);
        GL11.glClearColor(1.0f,1.0f,1.0f,1);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_STENCIL_BUFFER_BIT); // should probably clear the stencil
        renderer2D.begin(camera);
        // render layer-0
        renderer2D.drawTexture(layer0.getTexture(), 0, 0, 0, 1,1);
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
