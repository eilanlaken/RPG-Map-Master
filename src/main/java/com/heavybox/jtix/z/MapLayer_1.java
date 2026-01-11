package com.heavybox.jtix.z;

import com.heavybox.jtix.assets.Assets;
import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.graphics.*;
import org.lwjgl.opengl.GL11;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;

public class MapLayer_1 implements MapLayer {

    private FrameBuffer layer1 = new FrameBuffer(1920, 1080);
    public final Camera camera = new Camera(Camera.Mode.ORTHOGRAPHIC, 1920, 1080, 1, 0, 100, 75);

    private final Texture[] bases = new Texture[5];
    private final Texture lines;

    private boolean changed = false;

    public Array<CommandCreateWheatField> commandCreateWheatFields = new Array<>(true, 5);
    public Array<CommandCreateWheatField> newWheatFields = new Array<>(true, 5);

    public MapLayer_1() {
        bases[0] = Assets.get("assets/textures-layer-1/terrain-wheat-field-base_0.png");
        bases[1] = Assets.get("assets/textures-layer-1/terrain-wheat-field-base_1.png");
        bases[2] = Assets.get("assets/textures-layer-1/terrain-wheat-field-base_2.png");
        bases[3] = Assets.get("assets/textures-layer-1/terrain-wheat-field-base_3.png");
        bases[4] = Assets.get("assets/textures-layer-1/terrain-wheat-field-base_4.png");
        lines = Assets.get("assets/textures-layer-1/terrain-wheat-field-lines.png");
    }

    @Override
    public void executeCommand(Command command) {
        changed = true;
        if (command instanceof CommandCreateWheatField) {
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
            float[] borderPolygon = new float[cmd.polygon.length + 2];
            System.arraycopy(cmd.polygon, 0, borderPolygon, 0, cmd.polygon.length);
            borderPolygon[borderPolygon.length - 2] = cmd.polygon[0];
            borderPolygon[borderPolygon.length - 1] = cmd.polygon[1];
            renderer2D.setColor(0.396f, 0.263f, 0.129f, 0.3f);
            renderer2D.drawCurveFilled(null, 5.0f, 20, borderPolygon, 0,0,0,1,1);
            renderer2D.setColor(Color.WHITE);
            renderer2D.drawPolygonFilled(cmd.polygon, bases[cmd.baseType], 0, 0, 0, 1,1);
            renderer2D.drawPolygonFilled(cmd.polygon, lines, uv -> uv.rotateDeg(cmd.linesAngle),0,0,0,1,1);
            //renderer2D.setColor(0.396f, 0.665f, 0.129f, 0.7f);
            //renderer2D.drawPolygonFilled(cmd.polygon, harvestPart, uv -> uv.rotateDeg(cmd.linesAngle),0,0,0,1,1);
            //renderer2D.setColor(Color.WHITE);
        }
        renderer2D.end();
        commandCreateWheatFields.addAll(newWheatFields);
        newWheatFields.clear();
        changed = false;
    }

    @Override
    public void clear() {
        FrameBufferBinder.bind(layer1);
        GL11.glClearColor(0,0,0,0);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
        changed = true;
    }

    @Override
    public Texture getTexture() {
        return layer1.getDefaultColorAttachment();
    }

}
