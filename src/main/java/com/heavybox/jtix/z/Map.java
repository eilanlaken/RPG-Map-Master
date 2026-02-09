package com.heavybox.jtix.z;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.graphics.*;
import org.lwjgl.opengl.GL11;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;

public class Map {

    // TODO: customize width and height
    public int width; // 1920
    public int height; // 1080

    public MapLayerLevel_0 layer0; // Terrain layer (wheat fields)
    public MapLayerLevel_1 layer1; // Ground layer (wheat fields)
    public MapLayerLevel_3 layer3; // Token layer
    public MapLayerLevel_4 layer5; // Token layer

    // Decorations layer

    // Text layer

    public FrameBuffer mapFinal;

    private int commandsIndex = 0;
    public Array<Command> commandsHistory = new Array<>(true, 10);
    public Array<Command> commandsQueue = new Array<>(true, 10);

    private final Camera camera;


    private boolean needsRedraw = false;

    public Map(int width, int height) {
        this.width = width;
        this.height = height;
        camera = new Camera(Camera.Mode.ORTHOGRAPHIC, width, height, 1, 0, 100, 75);
        layer0 = new MapLayerLevel_0(width, height);
        layer1 = new MapLayerLevel_1(width, height);
        layer3 = new MapLayerLevel_3(width, height);
        layer5 = new MapLayerLevel_4(width, height);
        mapFinal = new FrameBuffer(width, height);
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

    public void getAllTokens(Token.Type ofType, Array<Token> out) {
        out.clear();
        for (Token token : layer3.allTokens) {
            if (token.type == ofType) out.add(token);
        }
    }

    public void getAllTokens(final Class<? extends Tool> sourceTool, Array<Token> out) {
        out.clear();
        for (Token token : layer3.allTokens) {
            if (token.sourceTool == sourceTool) out.add(token);
        }
    }

    public void getAllTokens(final TextureRegion withRegion, Array<Token> out) {
        out.clear();
        for (Token token : layer3.allTokens) {
            if (token.regions[0] == withRegion) out.add(token);
        }
    }

    public void getAllTokens(Enum<?> type, Array<Token> out) {
        out.clear();
        for (Token token : layer3.allTokens) {
            if (token.tokenType == type) out.add(token);
        }
    }

    private void executeCommand(Command command) {
        if (command.layer == 0) layer0.executeCommand(command);
        if (command.layer == 1) layer1.executeCommand(command);
        if (command.layer == 3) layer3.executeCommand(command);
        if (command.layer == 5) layer5.executeCommand(command);
    }

    public void undo() {
        if (commandsHistory.isEmpty()) return;
        if (commandsHistory.size == 1) {
            //layer0.clear();
            layer1.clear();
            //layer2.clear();
            layer3.clear();
            //layer4.clear();
            layer5.clear();
            commandsHistory.clear();
            return;
        }

        //layer0.clear();
        layer1.clear();
        //layer2.clear();
        layer3.clear();
        //layer4.clear();
        layer5.clear();
        int lastIndex = commandsHistory.size - 1;
        for (int i = commandsHistory.size - 2; i >= 0; i--) {
            Command cmd = commandsHistory.get(i);
            if (cmd.anchor) {
                lastIndex = i;
                break;
            }
        }
        commandsHistory.truncate(lastIndex);
        for (Command command : commandsHistory) {
            if (command instanceof CommandTerrain) continue;
            if (command instanceof CommandTerrainChangeEnvironment) continue;
            executeCommand(command);
        }
    }

    public void redo() {
        System.out.println("redo");
    }

    public void render(Renderer2D renderer2D) {
        layer0.applyChanges(renderer2D);
        layer1.applyChanges(renderer2D);
        layer3.applyChanges(renderer2D); // TODO: use applyChanges
        layer5.applyChanges(renderer2D); // TODO: use applyChanges

        FrameBufferBinder.bind(mapFinal);
        GL11.glClearColor(1.0f,1.0f,1.0f,1);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_STENCIL_BUFFER_BIT); // should probably clear the stencil
        renderer2D.begin(camera);
        renderer2D.blendingSet(GL_ONE, GL_ONE_MINUS_SRC_ALPHA); // TODO <- this fixes the dark artifacts.

        // render layer-0
        renderer2D.drawTexture(layer0.getTexture(), 0, 0, 0, 1,1);
        renderer2D.drawTexture(layer1.getTexture(), 0, 0, 0, 1,1);
        // render layer-1
        // render layer-2
        // render layer-3
        renderer2D.drawTexture(layer3.getTexture(), 0, 0, 0, 1,1);
        renderer2D.drawTexture(layer5.getTexture(), 0, 0, 0, 1,1);

        // render layer-4
        renderer2D.end();
        needsRedraw = false;
    }

    public Texture getTexture() {
        return mapFinal.getDefaultColorAttachment();
    }

    public void exportLayerAsImage(int layer) {
        Texture texture;
        if (layer == 0) texture = layer0.getTexture();
        else if (layer == 1) texture = layer1.getTexture();
        else if (layer == 3) texture = layer3.getTexture();
        else if (layer == 5) texture = layer5.getTexture();
        else texture = layer3.getTexture();

        ByteBuffer buffer = texture.getPixmapBytes();

        // Create BufferedImage
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int i = (x + (width * y)) * 4;
                int r = buffer.get(i) & 0xFF;
                int g = buffer.get(i + 1) & 0xFF;
                int b = buffer.get(i + 2) & 0xFF;
                int a = buffer.get(i + 3) & 0xFF;

                // Flip vertically, since OpenGL textures start bottom-left
                image.setRGB(x, height - y - 1,
                        ((a & 0xFF) << 24) |
                                ((r & 0xFF) << 16) |
                                ((g & 0xFF) << 8)  |
                                (b & 0xFF));
            }
        }

        try {
            ImageIO.write(image, "png", new File("layer_" + layer + ".png"));
        } catch (Exception e) {

        }
    }

}
