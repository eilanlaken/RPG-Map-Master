package com.heavybox.jtix.z;

import com.heavybox.jtix.assets.Assets;
import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.graphics.*;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;

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
        layer1 = new MapLayer_1();
        layer3 = new MapLayer_3();
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
        System.out.println("jj + " + command.layer);
        if (command.layer == 0) layer0.executeCommand(command);
        if (command.layer == 1) layer1.executeCommand(command);
        if (command.layer == 3) layer3.executeCommand(command);
    }

    private void undo() {

    }

    private void redo() {

    }

    public void render(Renderer2D renderer2D) {
        layer0.applyChanges(renderer2D);
        layer1.applyChanges(renderer2D);
        layer3.applyChanges(renderer2D); // TODO: use applyChanges

        FrameBufferBinder.bind(mapFinal);
        GL11.glClearColor(1.0f,1.0f,1.0f,1);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_STENCIL_BUFFER_BIT); // should probably clear the stencil
        renderer2D.begin(camera);
        // render layer-0
        renderer2D.drawTexture(layer0.getTexture(), 0, 0, 0, 1,1);
        renderer2D.drawTexture(layer1.getTexture(), 0, 0, 0, 1,1);
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

    public void saveLayerAsImage(int layer) {
        Texture texture = layer3.getTexture();

        ByteBuffer buffer = texture.getPixmapBytes();

        // Create BufferedImage
        BufferedImage image = new BufferedImage(1920, 1080, BufferedImage.TYPE_INT_ARGB);

        for (int y = 0; y < 1080; y++) {
            for (int x = 0; x < 1920; x++) {
                int i = (x + (1920 * y)) * 4;
                int r = buffer.get(i) & 0xFF;
                int g = buffer.get(i + 1) & 0xFF;
                int b = buffer.get(i + 2) & 0xFF;
                int a = buffer.get(i + 3) & 0xFF;

                // Flip vertically, since OpenGL textures start bottom-left
                image.setRGB(x, 1080 - y - 1,
                        ((a & 0xFF) << 24) |
                                ((r & 0xFF) << 16) |
                                ((g & 0xFF) << 8)  |
                                (b & 0xFF));
            }
        }

        try {
            ImageIO.write(image, "png", new File("layer333.png"));
        } catch (Exception e) {

        }
    }

}
