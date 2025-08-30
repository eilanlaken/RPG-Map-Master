package com.heavybox.jtix.z;

import com.heavybox.jtix.assets.Assets;
import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.graphics.*;
import org.lwjgl.opengl.GL11;

import java.util.Comparator;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;

public class MapLayer_3 implements MapLayer {

    // DEBUGGING


    // Tokens layer
    private FrameBuffer layer3 = new FrameBuffer(1920, 1080);;
    public Array<MapToken> allTokens = new Array<>(false, 10);
    private final TexturePack tokensAtlas;
    public final Camera camera = new Camera(Camera.Mode.ORTHOGRAPHIC, 1920, 1080, 1, 0, 100, 75);

    public MapLayer_3() {
        this.tokensAtlas = Assets.get("assets/texture-packs/layer_3.yml");
    }

    @Override
    public void executeCommand(Command command) {
        if (command instanceof CommandTokenCreate) {
            CommandTokenCreate cmd = (CommandTokenCreate) command;
            MapToken mapToken = new MapToken(cmd.layer, cmd.x, cmd.y, cmd.deg, cmd.sclX, cmd.sclY, cmd.regions);
            allTokens.add(mapToken);
            return;
        }

        // remove token

        // change token (move, scale, rotate...)

    }

    @Override
    public void redraw(Renderer2D renderer2D) {
        // sort tokens by y-value.
        FrameBufferBinder.bind(layer3);
        GL11.glClearColor(0,0,0,0);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
        renderer2D.begin(camera);
        renderer2D.setBlending(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ONE_MINUS_SRC_ALPHA);
        allTokens.sort(Comparator.comparingInt(o -> -(int) o.y));
        for (MapToken mapToken : allTokens) {
            mapToken.render(renderer2D);
        }
        renderer2D.end();
    }

    @Override
    public void applyChanges(Renderer2D renderer2D) {
        redraw(renderer2D);
    }

    @Override
    public Texture getTexture() {
        return layer3.getColorAttachment0();
    }


}
