package com.heavybox.jtix.z;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.graphics.*;
import org.lwjgl.opengl.GL11;

import static org.lwjgl.opengl.GL11.*;

public class MapLayer_5 implements MapLayer {

    // DEBUGGING
    private boolean changed = false;

    // Tokens layer
    private FrameBuffer layer5;
    public Array<MapToken> allTokens = new Array<>(false, 10); // TODO: maybe refactor to be member of Map
    public final Camera camera;

    public MapLayer_5(int width, int height) {
        layer5 = new FrameBuffer(width, height);
        camera = new Camera(Camera.Mode.ORTHOGRAPHIC, width, height, 1, 0, 100, 75);
    }

    @Override
    public void executeCommand(Command command) {
        changed = true;

        if (command instanceof CommandTokenCreate) {
            CommandTokenCreate cmd = (CommandTokenCreate) command;
            MapToken mapToken = new MapToken(cmd.layer, cmd.x, cmd.y, cmd.deg, cmd.sclX, cmd.sclY, cmd.regions);
            mapToken.type = cmd.type;
            allTokens.add(mapToken);
            return;
        }

        // remove token

        // change token (move, scale, rotate...)

    }



    @Override
    public void redraw(Renderer2D renderer2D) {
        // sort tokens by y-value.
        FrameBufferBinder.bind(layer5);
        GL11.glClearColor(0,0,0,0);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
        renderer2D.begin(camera);
        renderer2D.setBlending(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ONE_MINUS_SRC_ALPHA);
        //allTokens.sort(Comparator.comparingInt(o -> -(int) o.y));
        for (MapToken mapToken : allTokens) {
            mapToken.render(renderer2D);
        }
        renderer2D.end();
    }

    @Override
    public void applyChanges(Renderer2D renderer2D) {
        if (!changed) return;
        redraw(renderer2D);

        changed = false;
    }

    @Override
    public void clear() {
        allTokens.clear();

        FrameBufferBinder.bind(layer5);
        GL11.glClearColor(0,0,0,0);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
    }

    @Override
    public Texture getTexture() {
        return layer5.getDefaultColorAttachment();
    }

}
