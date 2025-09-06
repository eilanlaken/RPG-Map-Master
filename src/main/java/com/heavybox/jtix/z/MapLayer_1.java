package com.heavybox.jtix.z;

import com.heavybox.jtix.assets.Assets;
import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.graphics.*;
import com.heavybox.jtix.math.MathUtils;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;

public class MapLayer_1 implements MapLayer {

    private FrameBuffer layer1 = new FrameBuffer(1920, 1080);
    public final Camera camera = new Camera(Camera.Mode.ORTHOGRAPHIC, 1920, 1080, 1, 0, 100, 75);

    private final Texture base_0;
    private final Texture lines;
    private Texture harvest;

    private boolean changed = false;

    public Array<CommandCreateWheatField> commandCreateWheatFields = new Array<>(true, 5);
    public Array<CommandCreateWheatField> newWheatFields = new Array<>(true, 5);

    public MapLayer_1() {
        base_0 = Assets.get("assets/textures-layer-1/terrain-wheat-field-base_0.png");
        lines = Assets.get("assets/textures-layer-1/terrain-wheat-field-lines.png");
    }

    @Override
    public void executeCommand(Command command) {
        changed = true;
        System.out.println("hiff");
        if (command instanceof CommandCreateWheatField) {
            System.out.println("hi");
            CommandCreateWheatField cmd = (CommandCreateWheatField) command;
            newWheatFields.add(cmd);
        }
    }

    @Override
    public void redraw(Renderer2D renderer2D) {
        // TODO
    }

    @Override
    public void applyChanges(Renderer2D renderer2D) {
        if (!changed) return;
        FrameBufferBinder.bind(layer1);
        renderer2D.begin(camera);
        renderer2D.setBlending(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ONE_MINUS_SRC_ALPHA);
        for (CommandCreateWheatField cmd : newWheatFields) {
            renderer2D.drawPolygonFilled(cmd.polygon, base_0, 0, 0, 0, 1,1);
            renderer2D.drawPolygonFilled(cmd.polygon, lines, uv -> uv.rotateDeg(cmd.linesAngle),0,0,0,1,1);
        }
        renderer2D.end();
        commandCreateWheatFields.addAll(newWheatFields);
        newWheatFields.clear();
        changed = false;
    }

    @Override
    public Texture getTexture() {
        return layer1.getColorAttachment0();
    }

}
