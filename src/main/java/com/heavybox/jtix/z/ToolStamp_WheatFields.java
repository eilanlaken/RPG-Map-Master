package com.heavybox.jtix.z;

import com.heavybox.jtix.RPGMapMakerScene;
import com.heavybox.jtix.assets.Assets;
import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.graphics.Texture;
import com.heavybox.jtix.input.Input;
import com.heavybox.jtix.input.Mouse;
import com.heavybox.jtix.math.MathUtils;
import com.heavybox.jtix.math.Vector2;

public class ToolStamp_WheatFields extends Tool {

    private Texture[] bases = new Texture[5];
    private int baseType = MathUtils.randomUniformInt(0, bases.length);
    private float linesAngle = MathUtils.randomUniformFloat(0, 360);

    public ToolStamp_WheatFields(RPGMapMakerScene scene) {
        super(scene);
        bases[0] = Assets.get("assets/textures-layer-0/wheat_field_0.png");
        bases[1] = Assets.get("assets/textures-layer-0/wheat_field_1.png");
        bases[2] = Assets.get("assets/textures-layer-0/wheat_field_2.png");
        bases[3] = Assets.get("assets/textures-layer-0/wheat_field_3.png");
        bases[4] = Assets.get("assets/textures-layer-0/wheat_field_4.png");
        free = true;
        mode = Mode.ADD;
        shape = Shape.POLYGON;
    }

    @Override
    public void update(float delta) {
        boolean mouseMoved = Input.mouse.moved();
        boolean leftClicked = Input.mouse.isButtonClicked(Mouse.Button.LEFT);
        int verticalScroll = (int) Input.mouse.getVerticalScroll();

        baseType += verticalScroll;
        baseType %= bases.length;
        if (baseType < 0) baseType = bases.length - 1;

        if (free) {
            if (leftClicked) {
                Vector2 p = new Vector2(x, y);
                polygonPoints.add(p);
                free = false;
            }
        } else {
            if (leftClicked) {
                Vector2 p = new Vector2(x, y);
                polygonPoints.add(p);
                if (polygonPoints.size < 4) {
                    return;
                }
                if (Vector2.dst(p, polygonPoints.first()) <= 20) {
                    createWheatField();
                    polygonPoints.clear();
                    free = true;
                }
            }
        }
    }

    private void createWheatField() {
        CommandTerrainWheatFieldCreate cmd = new CommandTerrainWheatFieldCreate();
        cmd.polygon = Utils.polygonConvertToFlat(polygonPoints);
        cmd.baseType = baseType;
        cmd.linesAngle = linesAngle;
        map.addCommand(cmd);

        baseType = MathUtils.randomUniformInt(0, bases.length);
        linesAngle = MathUtils.randomUniformFloat(0, 360);
    }

    @Override
    public void renderToolOverlay(Renderer2D renderer2D, float x, float y) {
        if (free) {
            renderer2D.drawCircleFilled(10,5, x, y, 0, 1,1);
            renderer2D.setColor(Color.WHITE);
        } else {
            if (polygonPoints.isEmpty()) return;

            renderer2D.setColor(Color.BLACK);
            renderer2D.drawCircleBorder(15, 5, 10, polygonPoints.first().x, polygonPoints.first().y, 0,1,1);
            renderer2D.setColor(Color.GREEN);
            for (int i = 0; i < polygonPoints.size; i++) {
                Vector2 p = polygonPoints.get(i);
                renderer2D.drawCircleFilled(5, 5, p.x, p.y, 0, 1, 1);
            }
            for (int i = 0; i < polygonPoints.size - 1; i++) {
                Vector2 p1 = polygonPoints.get(i);
                Vector2 p2 = polygonPoints.get(i + 1);
                renderer2D.drawLineThin(p1.x, p1.y, p2.x, p2.y);
            }
            renderer2D.drawLineThin(polygonPoints.last().x, polygonPoints.last().y, x, y);
            renderer2D.drawLineThin(x, y, polygonPoints.first().x, polygonPoints.first().y);

            if (polygonPoints.size >= 3) {
                // draw wheat field
                float[] polygon = Utils.polygonConvertToFlat(polygonPoints);
                renderer2D.setColor(0.3569f, 0.3098f, 0.2275f, 0.4f);
                renderer2D.drawCurveFilled(null, 16.0f, 20, polygon, 0, 0, 0, 1, 1);
                renderer2D.setColor(Color.WHITE);
                renderer2D.drawPolygonFilled(polygon, bases[baseType], uv -> uv.rotateDeg(linesAngle).scl(2), 0, 0, 0, 1, 1);
            }
        }
    }

    @Override
    public void activate() {

    }

    @Override
    public void deactivate() {

    }

    @Override
    public String getName() {
        return "Wheat Fields Stamp";
    }
}
