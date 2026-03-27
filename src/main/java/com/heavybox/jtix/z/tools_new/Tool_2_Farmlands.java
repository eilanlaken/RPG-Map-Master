package com.heavybox.jtix.z.tools_new;

import com.heavybox.jtix.RPGMapMakerScene;
import com.heavybox.jtix.assets.Assets;
import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.collections.ArrayChar;
import com.heavybox.jtix.collections.Collections;
import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.graphics.Texture;
import com.heavybox.jtix.input.Input;
import com.heavybox.jtix.input.Keyboard;
import com.heavybox.jtix.input.Mouse;
import com.heavybox.jtix.math.MathUtils;
import com.heavybox.jtix.math.Vector2;
import com.heavybox.jtix.z.CommandTerrainFarmlandAdd;
import com.heavybox.jtix.z.Token;
import com.heavybox.jtix.z.Utils;
import org.jetbrains.annotations.NotNull;

public class Tool_2_Farmlands extends Tool_new {

    private Mode currentMode = Mode.ADD;
    private Shape currentShape = Shape.POINT;

    private final Texture[] bases = new Texture[5];
    private int baseType = MathUtils.randomUniformInt(0, bases.length);
    private float linesAngle = MathUtils.randomUniformFloat(0, 360); //TODO: change to deg

    // point (not applicable for farmlands)

    // line - draws squares
    public Array<Vector2> line_points = new Array<>(true, 10);
    private final Vector2 line_start = new Vector2();
    public boolean line_free = true;
    public float line_width = 100;
    public boolean line_procedural = false;

    // circle
    public Array<Vector2> circle_points = new Array<>(true, 10);
    public float circle_radius = 100;

    // polygon
    public Array<Vector2> polygon_points = new Array<>(true, 10);
    private boolean polygon_procedural = false;
    public boolean polygon_free = true;

    public Tool_2_Farmlands(final RPGMapMakerScene scene) {
        super(scene);
        bases[0] = Assets.get("assets/textures-layer-0/farmland_0.png");
        bases[1] = Assets.get("assets/textures-layer-0/farmland_1.png");
        bases[2] = Assets.get("assets/textures-layer-0/farmland_2.png");
        bases[3] = Assets.get("assets/textures-layer-0/farmland_3.png");
        bases[4] = Assets.get("assets/textures-layer-0/farmland_4.png");
        polygon_free = true;

        currentMode = Mode.ADD;
        currentShape = Shape.LINE;
    }

    private void createFarmland(@NotNull Array<Vector2> points) {
        CommandTerrainFarmlandAdd cmd = new CommandTerrainFarmlandAdd();
        cmd.polygon = Utils.polygonConvertToFlat(points);
        cmd.baseType = baseType;
        cmd.linesAngle = linesAngle;
        map.addCommand(cmd);
        baseType = MathUtils.randomUniformInt(0, bases.length);
        linesAngle = MathUtils.randomUniformFloat(0, 360);

        points.clear();
    }

    @Override
    void onChangeParameters() {

    }

    private void refillPoints() {
        if (currentShape == Shape.LINE) refillPoints_line();
        else if (currentShape == Shape.CIRCLE) refillPoints_circle();
        else if (currentShape == Shape.POLYGON) refillPoints_polygon();
    }

    private void refillPoints_line() {
        line_points.clear();

        Vector2 diff = new Vector2(x - line_start.x, y - line_start.y);
        diff.nor();
        diff.scl(line_width);
        diff.rotate90(1);

        Vector2 a0 = new Vector2(line_start).add(diff);
        Vector2 a1 = new Vector2(line_start).sub(diff);
        Vector2 a2 = new Vector2(x, y).sub(diff);
        Vector2 a3 = new Vector2(x, y).add(diff);

        line_points.add(a0,a1,a2,a3);
    }

    private void refillPoints_circle() {
        circle_points.clear();

    }

    private void refillPoints_polygon() {
        polygon_points.clear();

    }

    @Override
    public void update(float delta) {
        boolean shiftLeftJustPressed = Input.keyboard.isKeyJustPressed(Keyboard.Key.LEFT_SHIFT);
        boolean leftButtonPressed = Input.mouse.isButtonPressed(Mouse.Button.LEFT);
        boolean leftButtonJustPressed = Input.mouse.isButtonJustPressed(Mouse.Button.LEFT);
        boolean leftClick = Input.mouse.isButtonClicked(Mouse.Button.LEFT);
        boolean rightButtonJustPressed = Input.mouse.isButtonJustPressed(Mouse.Button.RIGHT);
        boolean mouseMoved = Input.mouse.moved();
        boolean scrollUp = Input.mouse.getScrollY() > 0;
        boolean scrollDown = Input.mouse.getScrollY() < 0;
        boolean backspaceJustPressed = Input.keyboard.isKeyJustPressed(Keyboard.Key.BACKSPACE);
        boolean plusPressed = Input.keyboard.isKeyPressed(Keyboard.Key.EQUAL);
        boolean minusPressed = Input.keyboard.isKeyPressed(Keyboard.Key.MINUS);
        boolean spaceJustPressed = Input.keyboard.isKeyJustPressed(Keyboard.Key.SPACE);
        boolean zButtonJustPressed = Input.keyboard.isKeyJustPressed(Keyboard.Key.Z);
        boolean aPressed = Input.keyboard.isKeyPressed(Keyboard.Key.A);
        boolean sPressed = Input.keyboard.isKeyPressed(Keyboard.Key.S);
        float dy = Input.mouse.getYDelta();

        // =============  tool settings  ===============
        if (backspaceJustPressed) {
            this.currentMode = Collections.enumNext(this.currentMode);
            reset();
            return;
        }

        // ********* Actions ************
        if (currentShape == Shape.POINT) {
            // ignored
        }

        if (currentShape == Shape.LINE) {
            if (line_free) {
                if (leftClick) {
                    line_start.set(x, y);
                    refillPoints();
                    line_free = false;
                }
            } else {
                if (mouseMoved) refillPoints();
                if (leftClick) {
                    // spawn farmland
                    line_points.add(line_points.first());
                    createFarmland(line_points);
                    line_free = true;
                }
            }
        }

        if (currentShape == Shape.CIRCLE) {

        }

        if (currentShape == Shape.POLYGON) {

        }
    }

    @Override
    public void renderToolOverlay(Renderer2D renderer2D, float x, float y) {
        if (currentMode == Mode.SUB) {

            return;
        }

        // add farmlands
        if (currentShape == Shape.POINT) {
            // ignored
        }

        if (currentShape == Shape.LINE) {
            if (line_free) {
                renderer2D.setColor(Color.BLUE);
                renderer2D.drawCircleThin(Math.max(12, 5), 10, x, y, 0,1,1);
                renderer2D.setColor(Color.WHITE);
            } else {
                if (line_points.isEmpty()) return;
                if (MathUtils.polygonArea(line_points) == 0) return;
                // draw the surrounding polygon
                renderer2D.setColor(Color.BLUE);
                renderer2D.drawCircleThin(Math.max(12, 5), 10, line_start.x, line_start.y, 0,1,1);
                renderer2D.drawCircleThin(Math.max(12, 5), 10, x, y, 0,1,1);
                renderer2D.setColor(Color.YELLOW);
                renderer2D.drawLineThin(line_points.get(0).x, line_points.get(0).y, line_points.get(1).x, line_points.get(1).y);
                renderer2D.drawLineThin(line_points.get(1).x, line_points.get(1).y, line_points.get(2).x, line_points.get(2).y);
                renderer2D.drawLineThin(line_points.get(2).x, line_points.get(2).y, line_points.get(3).x, line_points.get(3).y);
                renderer2D.drawLineThin(line_points.get(3).x, line_points.get(3).y, line_points.get(0).x, line_points.get(0).y);
                // draw the wheat field
                if (!line_procedural) {
                    // draw farmland
                    float[] polygon = Utils.polygonConvertToFlat(line_points);
                    renderer2D.setColor(0.3569f, 0.3098f, 0.2275f, 0.4f);
                    renderer2D.drawCurveFilled(null, 16.0f, 20, polygon, 0, 0, 0, 1, 1);
                    renderer2D.setColor(Color.WHITE);
                    renderer2D.drawPolygonFilled(polygon, bases[baseType], uv -> uv.rotateDeg(linesAngle).scl(2), 0, 0, 0, 1, 1);
                }
            }
            return;
        }

        if (currentShape == Shape.CIRCLE) {

        }

        if (currentShape == Shape.POLYGON) {

        }
    }

    @Override
    public void renderToolText(Renderer2D renderer2D, float x, float y) {
        if (currentShape == Shape.POINT) {
            renderer2D.drawStringLine("Point shapes not applicable for farmlands", 12, true, x, y, 0, 1, 1);
        }
    }

    @Override
    String getHelperText() {
        return "";
    }

    private void reset() {
        line_points.clear();
        polygon_points.clear();
        line_free = true;
        polygon_free = true;
        circle_points.clear();
    }

    @Override
    public void activate() {
        reset();
    }

    @Override
    public void deactivate() {
        reset();
    }

    // later

    @Override
    public int getLayer() {
        return 0;
    }

    @Override
    public boolean active() {
        return active;
    }

    @Override
    public boolean mouseButtonsDown(int mouseX, int mouseY, @NotNull Array<Mouse.Button> buttons) {
        return false;
    }

    @Override
    public boolean mouseButtonsUp(int mouseX, int mouseY, @NotNull Array<Mouse.Button> buttons) {
        return false;
    }

    @Override
    public boolean mouseMoved(int mouseX, int mouseY, int deltaMouseX, int deltaMouseY) {
        return false;
    }

    @Override
    public boolean mouseScrolled(float scrollX, float scrollY) {
        return false;
    }

    @Override
    public boolean mouseDragged(int mouseX, int mouseY, int deltaMouseX, int deltaMouseY, @NotNull Array<Mouse.Button> buttons) {
        return false;
    }

    @Override
    public boolean keyboardKeysJustPressed(@NotNull Array<Keyboard.Key> keys) {
        return false;
    }

    @Override
    public boolean keyboardKeysJustReleased(@NotNull Array<Keyboard.Key> keys) {
        return false;
    }

    @Override
    public boolean keyboardCodepointsTyped(@NotNull ArrayChar codepoints) {
        return false;
    }

}
