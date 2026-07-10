package com.heavybox.jtix.z;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.graphics.*;
import com.heavybox.jtix.math.MathUtils;
import org.lwjgl.opengl.GL11;

import java.util.Comparator;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;

public class MapSurface_1_Tokens implements MapSurface {

    // DEBUGGING
    private boolean changed = false;

    // Tokens layer
    private final FrameBuffer layer3;
    public final Array<Token> allTokens = new Array<>(false, 10); // TODO: maybe refactor to be member of Map
    public final Camera camera;

    public MapSurface_1_Tokens(int width, int height) {
        layer3 = new FrameBuffer(width, height);
        camera = new Camera(Camera.Mode.ORTHOGRAPHIC, width, height, 1, 0, 100, 75);
    }

    @Override
    public void executeCommand(Command command) {
        changed = true;

        // create token
        if (command instanceof CommandTokenCreate) {
            CommandTokenCreate cmd = (CommandTokenCreate) command;
            Token token = new Token(cmd.layer, cmd.x, cmd.y, cmd.deg, cmd.sclX, cmd.sclY, cmd.regions);
            token.shader = cmd.shader;
            token.tokenType = cmd.tokenType;
            token.tint = cmd.tint.equals(Color.WHITE) ? Color.WHITE : cmd.tint.clone();
            allTokens.add(token);
            return;
        }

        // remove token
        if (command instanceof CommandTokenDelete) {
            CommandTokenDelete cmd = (CommandTokenDelete) command;
            // find token based on position and type
            for (int i = 0; i < allTokens.size; i++) {
                Token token = allTokens.get(i);
                if (cmd.type != token.tokenType) continue;
                if (!MathUtils.floatsEqual(cmd.x, token.getX())) continue;
                if (!MathUtils.floatsEqual(cmd.y, token.getY())) continue;
                allTokens.removeIndex(i);
            }
        }


        // change token (move, scale, rotate...)

    }



    @Override
    public void redraw(Renderer2D renderer2D) {
        // sort tokens by y-value.
        Graphics.bindFrameBuffer(layer3);
        GL11.glClearColor(0,0,0,0);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
        renderer2D.begin(camera);
        renderer2D.blendingSet(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ONE_MINUS_SRC_ALPHA);
        //allTokens.sort(Comparator.comparingInt(o -> -(int) o.transform.y));
        allTokens.sort(Comparator
                .comparingInt((Token o) -> o.layer) // sort by layers first
                .thenComparingInt(o -> -(int) o.transform.y) // sort by transform
        );
        for (Token token : allTokens) {
            token.render(renderer2D);
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

        Graphics.bindFrameBuffer(layer3);
        GL11.glClearColor(0,0,0,0);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
    }

    @Override
    public int getOrderIndex() {
        return 1;
    }

    @Override
    public Texture getTexture() {
        return layer3.getDefaultColorAttachment();
    }

}
