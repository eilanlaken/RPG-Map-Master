package com.heavybox.jtix.z;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.heavybox.jtix.assets.Assets;
import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.graphics.*;
import org.lwjgl.opengl.GL11;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;
import java.util.Set;
import java.util.function.Predicate;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;

public class Map {

    public int width; // 1920
    public int height; // 1080

    public MapSurface_0_Terrain surface_0_terrain; // Terrain and farmlands layer
    public MapSurface_1_Tokens mapSurface_1_tokens; // Token layer

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
        surface_0_terrain = new MapSurface_0_Terrain(width, height);
        mapSurface_1_tokens = new MapSurface_1_Tokens(width, height);
        mapFinal = new FrameBuffer(width, height);
    }

    // TODO: create a load constructor.

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

    public void getAllTokensInCircleByCondition(Predicate<Token> filter, float centerX, float centerY, float radius, Set<Token> out) {
        out.clear();
        float r2 = radius * radius;

        for (Token token : mapSurface_1_tokens.allTokens) {
            if (!filter.test(token)) continue;

            float dx = token.getX() - centerX;
            float dy = token.getY() - centerY;

            if (dx * dx + dy * dy <= r2) {
                out.add(token);
            }
        }
    }

    public void getAllTokensInCircleByEnumValue(Enum<?> type, float centerX, float centerY, float radius, Set<Token> out) {
        float r2 = radius * radius;
        for (Token token : mapSurface_1_tokens.allTokens) {
            if (token.tokenType != type) continue;

            float dx = token.getX() - centerX;
            float dy = token.getY() - centerY;

            if (dx * dx + dy * dy <= r2) {
                out.add(token);
            }
        }
    }

    public void getAllTokensInCircleByEnumClass(Class<? extends Enum<?>> type, float centerX, float centerY, float radius, Set<Token> out) {
        float r2 = radius * radius;
        for (Token token : mapSurface_1_tokens.allTokens) {
            if (token.tokenType.getClass() != type) continue;

            float dx = token.getX() - centerX;
            float dy = token.getY() - centerY;

            if (dx * dx + dy * dy <= r2) {
                out.add(token);
            }
        }
    }

    public void getAllTokensByRegion(TextureRegion region, Array<Token> out) {
        out.clear();
        for (Token token : mapSurface_1_tokens.allTokens) {
            if (token.regions[0] == region) out.add(token);
        }
    }

    public void getAllTokensByType(Enum<?> type, Array<Token> out) {
        out.clear();
        for (Token token : mapSurface_1_tokens.allTokens) {
            if (token.tokenType == type) out.add(token);
        }
    }

    private void executeCommand(Command command) {
        if (command.getSurface() == Command.Surface.TERRAIN) surface_0_terrain.executeCommand(command);
        if (command.getSurface() == Command.Surface.TOKENS)  mapSurface_1_tokens.executeCommand(command);
    }

    // TODO
    public void undo() {
        if (commandsHistory.isEmpty()) return;

    }

    public void redo() {
        System.out.println("redo");
    }

    public void render(Renderer2D renderer2D) {
        surface_0_terrain.applyChanges(renderer2D);
        mapSurface_1_tokens.applyChanges(renderer2D);

        Graphics.bindFrameBuffer(mapFinal);
        GL11.glClearColor(1.0f,1.0f,1.0f,1);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_STENCIL_BUFFER_BIT); // should probably clear the stencil
        renderer2D.begin(camera);
        renderer2D.blendingSet(GL_ONE, GL_ONE_MINUS_SRC_ALPHA); // TODO <- this fixes the dark artifacts.

        // render layer-0
        renderer2D.drawTexture(surface_0_terrain.getTexture(), 0, 0, 0, 1,1);
        // render layer-3
        renderer2D.drawTexture(mapSurface_1_tokens.getTexture(), 0, 0, 0, 1,1);

        // render layer-4
        renderer2D.end();
        needsRedraw = false;
    }

    public Texture getTexture() {
        return mapFinal.getDefaultColorAttachment();
    }

    // TODO: delete
    @Deprecated public void exportLayerAsImage(int layer) {
        Texture texture;
        if (layer == 0) texture = surface_0_terrain.getTexture();
        else if (layer == 3) texture = mapSurface_1_tokens.getTexture();
        else texture = mapSurface_1_tokens.getTexture();

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

    public void exportTerrainAsImage(final String path) {
        Assets.saveImage(path, surface_0_terrain.getTexture());
    }

    public JsonElement serializeCommandsHistory() {
        JsonObject json = new JsonObject();
        JsonArray commands = new JsonArray();
        for (Command command : commandsHistory) commands.add(command.serialize());
        json.add("commands", commands);
        return json;
    }

    public JsonElement serializeTokens() {
        JsonObject json = new JsonObject();
        JsonArray tokens = new JsonArray();
        for (Token token : mapSurface_1_tokens.allTokens) tokens.add(token.serialize());
        json.add("tokens", tokens);
        return json;
    }

}
