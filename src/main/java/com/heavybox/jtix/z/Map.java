package com.heavybox.jtix.z;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.graphics.*;
import org.lwjgl.opengl.GL11;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;
import java.util.Set;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;

public class Map {

    public int width; // 1920
    public int height; // 1080

    public MapLayerLevel_0 layer0; // Terrain layer (wheat fields)
    public MapLayerLevel_1 layer1; // Ground layer (wheat fields)
    public MapLayerLevel_3 layer3; // Token layer
    public MapLayerLevel_4 layer4; // Token layer

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
        layer4 = new MapLayerLevel_4(width, height);
        mapFinal = new FrameBuffer(width, height);
    }

    public void addCommand(Command command) {
        commandsQueue.add(command);
    }

    public void update(float delta) {
        for (Command command : commandsQueue) {
            executeCommand(command);
        }
        commandsHistory.addAll(commandsQueue);
        commandsQueue.clear();
    }

    public void getAllTokensInCircle(Enum<?> type, float centerX, float centerY, float radius, Set<Token> out) {
        float r2 = radius * radius;
        for (Token token : layer3.allTokens) {
            if (token.tokenType != type) continue;

            float dx = token.getX() - centerX;
            float dy = token.getY() - centerY;

            if (dx * dx + dy * dy <= r2) {
                out.add(token);
            }
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
        if (command.layer == 5) layer4.executeCommand(command);
    }

    // TODO
    public void undo() {
        if (commandsHistory.isEmpty()) return;

    }

    public void redo() {
        System.out.println("redo");
    }

    public void render(Renderer2D renderer2D) {
        layer0.applyChanges(renderer2D);
        layer1.applyChanges(renderer2D);
        layer3.applyChanges(renderer2D);
        layer4.applyChanges(renderer2D);

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
        renderer2D.drawTexture(layer4.getTexture(), 0, 0, 0, 1,1);

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
        else if (layer == 5) texture = layer4.getTexture();
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
