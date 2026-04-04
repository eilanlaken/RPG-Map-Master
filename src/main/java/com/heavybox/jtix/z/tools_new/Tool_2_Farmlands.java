package com.heavybox.jtix.z.tools_new;

import com.heavybox.jtix.RPGMapMakerScene;
import com.heavybox.jtix.assets.Assets;
import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.collections.ArrayChar;
import com.heavybox.jtix.collections.ArrayFloat;
import com.heavybox.jtix.collections.Collections;
import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Graphics;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.graphics.Texture;
import com.heavybox.jtix.input.Input;
import com.heavybox.jtix.input.Keyboard;
import com.heavybox.jtix.input.Mouse;
import com.heavybox.jtix.math.MathUtils;
import com.heavybox.jtix.math.Shape2DPolygon;
import com.heavybox.jtix.math.Vector2;
import com.heavybox.jtix.z.CommandTerrainFarmlandAdd;
import com.heavybox.jtix.z.CommandTerrainFarmlandSub;
import com.heavybox.jtix.z.Utils;
import org.jetbrains.annotations.NotNull;
import org.locationtech.jts.geom.*;
import org.locationtech.jts.triangulate.VoronoiDiagramBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Deprecated
public class Tool_2_Farmlands extends Tool_new {

    private Mode currentMode = Mode.ADD;
    private Shape currentShape = Shape.POINT;

    private final Texture[] bases = new Texture[5];
    private int baseType = MathUtils.randomUniformInt(0, bases.length);
    private float linesAngle = MathUtils.randomUniformFloat(0, 360); //TODO: change to deg
    public boolean procedural = true;
    public float size = 100;

    // point (not applicable for farmlands)

    // line - draws squares
    public Array<Vector2> line_points = new Array<>(true, 10);
    private final Vector2 line_start = new Vector2();
    public boolean line_free = true;

    // circle
    public Array<Vector2> circle_points = new Array<>(true, 10);
    public int circle_refinement = 25;
    public float circle_radius = 100;

    // polygon
    public Array<Vector2> polygon_points = new Array<>(true, 10);
    public final ArrayFloat polygon_procedural = new ArrayFloat(true, 8);
    public boolean polygon_free = true;

    public Tool_2_Farmlands(final RPGMapMakerScene scene) {
        super(scene);
        bases[0] = Assets.get("assets/textures-layer-0/farmland_0.png");
        bases[1] = Assets.get("assets/textures-layer-0/farmland_1.png");
        bases[2] = Assets.get("assets/textures-layer-0/farmland_2.png");
        bases[3] = Assets.get("assets/textures-layer-0/farmland_3.png");
        bases[4] = Assets.get("assets/textures-layer-0/farmland_4.png");
        polygon_free = true;
        line_free = true;
        currentMode = Mode.ADD;
        currentShape = Shape.POLYGON;
        Utils.polygon_generateRandom(size, size, polygon_procedural);
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

    private void createFarmland(@NotNull ArrayFloat points) {
        CommandTerrainFarmlandAdd cmd = new CommandTerrainFarmlandAdd();
        float[] copy = points.pack();
        cmd.polygon = Arrays.copyOf(copy, copy.length);
        cmd.baseType = baseType;
        cmd.linesAngle = linesAngle;
        map.addCommand(cmd);
        baseType = MathUtils.randomUniformInt(0, bases.length);
        linesAngle = MathUtils.randomUniformFloat(0, 360);
        points.clear();
    }

    private void createFarmlandsLineProcedural() {
        float line_width = this.size * 2 * sclY;
        Vector2 segment = new Vector2(x - line_start.x, y - line_start.y);
        float angle = segment.angleDeg(); // later, rotate by angle
        float length = segment.len();

        // subdivide the segment
        ArrayFloat segments = new ArrayFloat(true, 4);
        int n = Math.max(1, Math.round(length / line_width));
        float min = 0.75f;
        float max = 1.25f;
        float[] lens = new float[n];
        float sum = 0f;
        for (int i = 0; i < n; i++) {
            float factor = MathUtils.randomUniformFloat(min, max); // uniform or gaussian centered at 1
            lens[i] = line_width * factor;
            sum += lens[i];
        }
        float scale = length / sum;
        for (int i = 0; i < n; i++) {
            lens[i] *= scale;
        }
        float acc = 0f;
        for (int i = 0; i < n; i++) {
            acc += lens[i];
            segments.add(acc);
        }

        // create rect for the segments
        Array<Rect> rects = new Array<>(true, segments.size);
        float prev = 0f;
        for (int i = 0; i < segments.size; i++) {
            float curr = segments.get(i);
            float segLength = curr - prev;
            float midX = (prev + curr) * 0.5f;

            float heightFactor = MathUtils.randomUniformFloat(0.8f, 1.2f);
            float h = line_width * heightFactor;

            boolean coinFlip = MathUtils.randomUniformBoolean();
            float maxOffset = line_width * 0.055f;

            if (coinFlip) {
                // single rect
                Rect r = new Rect();
                r.width = segLength;
                r.height = h;

                float dx = MathUtils.randomUniformFloat(-maxOffset, maxOffset);
                float dy = MathUtils.randomUniformFloat(-maxOffset, maxOffset);
                r.center = new Vector2(midX + dx, 0f + dy);
                rects.add(r);
            } else {
                // split into 2 stacked rects
                float halfH = h * 0.5f;
                float offsetY = halfH * 0.5f; // center offset

                Rect r1 = new Rect();
                r1.width = segLength;
                r1.height = halfH;
                float dx1 = MathUtils.randomUniformFloat(-maxOffset, maxOffset);
                float dy1 = MathUtils.randomUniformFloat(-maxOffset, 0);
                r1.center = new Vector2(midX + dx1, +offsetY + dy1);

                Rect r2 = new Rect();
                r2.width = segLength;
                r2.height = halfH;
                float dx2 = MathUtils.randomUniformFloat(-maxOffset, maxOffset);
                float dy2 = MathUtils.randomUniformFloat(0, maxOffset);
                r2.center = new Vector2(midX + dx2, -offsetY + dy2);

                rects.add(r1);
                rects.add(r2);
            }
            prev = curr;
        }

        for (Rect rect : rects) {
            Array<Vector2> rect_polygon = rect.getPolygonPoints(line_start,angle);
            createFarmland(rect_polygon);
        }
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
        diff.scl(size * sclY);
        diff.rotate90(1);

        Vector2 a0 = new Vector2(line_start).add(diff);
        Vector2 a1 = new Vector2(line_start).sub(diff);
        Vector2 a2 = new Vector2(x, y).sub(diff);
        Vector2 a3 = new Vector2(x, y).add(diff);

        line_points.add(a0,a1,a2,a3);
    }

    private void refillPoints_circle() {
        circle_points.clear();

        for (int i = 0; i < circle_refinement; i++) {
            Vector2 arm = new Vector2(circle_radius, 0);
            arm.rotateDeg(i * (360f / circle_refinement));
            arm.add(x, y);
            circle_points.add(arm);
        }
        circle_points.add(new Vector2(circle_radius + x, 0 + y));
    }

    private void refillPoints_polygon() {
        if (!procedural) return;

        if (polygon_procedural.isEmpty()) {
            //Utils.polygon_generateRandom(size, size, polygon_procedural);
            return;
        }

        // transform points
    }

    @Override
    public void update(float delta) {
        boolean shiftLeftJustPressed = Input.keyboard.isKeyJustPressed(Keyboard.Key.LEFT_SHIFT);
        boolean leftClick = Input.mouse.isButtonClicked(Mouse.Button.LEFT);
        boolean rightClick = Input.mouse.isButtonJustPressed(Mouse.Button.RIGHT);
        boolean mouseMoved = Input.mouse.moved();
        boolean scrollUp = Input.mouse.getScrollY() > 0;
        boolean scrollDown = Input.mouse.getScrollY() < 0;
        boolean backspaceJustPressed = Input.keyboard.isKeyJustPressed(Keyboard.Key.BACKSPACE);
        boolean pJustPressed = Input.keyboard.isKeyJustPressed(Keyboard.Key.P);
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

        if (aPressed && dy != 0) {
            float deltaDeg = -dy / 1000 * Graphics.getWindowHeight();
            deg += deltaDeg;
            refillPoints();
            return;
        }

        if (shiftLeftJustPressed) {
            this.currentShape = Collections.enumNext(this.currentShape);
            reset();
            return;
        }

        if (pJustPressed) {
            this.procedural = !this.procedural;
            reset();
            return;
        }

        if (plusPressed) {
            sclX *= 1.00f + 0.8f * Graphics.getDeltaTime();
            sclY *= 1.00f + 0.8f * Graphics.getDeltaTime();
            circle_radius *= 1.00f + 0.8f * Graphics.getDeltaTime();
            refillPoints();
            return;
        }
        if (minusPressed) {
            sclX *= 1.00f - 0.8f * Graphics.getDeltaTime();
            sclY *= 1.00f - 0.8f * Graphics.getDeltaTime();
            circle_radius *= 1.00f - 0.8f * Graphics.getDeltaTime();
            refillPoints();
            return;
        }

        // ********* Actions ************
        if (currentMode == Mode.SUB) {
            if (leftClick) {
                CommandTerrainFarmlandSub cmd = new CommandTerrainFarmlandSub(x, y);
                map.addCommand(cmd);
            }
            return;
        }

        if (currentShape == Shape.POINT) {
            // ignored
        }

        if (currentShape == Shape.LINE) {
            if (line_free) {
                if (leftClick) {
                    line_start.set(x, y);
                    refillPoints();
                    line_free = false;
                    return;
                }
            } else {
                if (mouseMoved) refillPoints();
                if (leftClick && !procedural) {
                    // spawn farmland
                    line_points.add(line_points.first());
                    createFarmland(line_points);
                    line_free = true;
                    return;
                } else if (leftClick && procedural) {
                    // procedurally generate farmlands
                    createFarmlandsLineProcedural();
                    line_free = true;
                    return;
                }
            }
        }

        if (currentShape == Shape.CIRCLE) {
            if (mouseMoved) refillPoints();
            if (leftClick) {
                createFarmland(circle_points);
            }
            return;
        }

        if (currentShape == Shape.POLYGON) {
            if (procedural) {
                if (rightClick) {
                    Utils.polygon_generateRandom(this.size * sclX, this.size * sclY, polygon_procedural);
                    return;
                }
                if (leftClick) {
                    // transform first
                    Vector2 point = new Vector2();
                    for (int i = 0; i < polygon_procedural.size / 2; i++) {
                        point.x = polygon_procedural.get(2 * i);
                        point.y = polygon_procedural.get(2 * i + 1);
                        point.transform_ScaleRotateTranslate(x,y,deg,sclX,sclY);
                        polygon_procedural.set(2 * i, point.x);
                        polygon_procedural.set(2 * i + 1, point.y);
                    }
                    polygon_procedural.add(polygon_procedural.get(0), polygon_procedural.get(1));
                    createFarmland(polygon_procedural);
                    Utils.polygon_generateRandom(this.size, this.size, polygon_procedural);
                }
            }

            if (!procedural) {
                if (polygon_free) {
                    if (leftClick) {
                        polygon_points.add(new Vector2(x, y));
                        polygon_free = false;
                    }
                } else {
                    if (mouseMoved) refillPoints();
                    else if (leftClick) {
                        Vector2 p = new Vector2(x, y); // need to test intersections etc.
                        if (polygon_points.size <= 2) {
                            polygon_points.add(p);
                            return;
                        }
                        if (Vector2.dst(p, polygon_points.first()) <= 20) {
                            polygon_points.add(new Vector2(polygon_points.first()));
                            createFarmland(polygon_points);
                            polygon_points.clear();
                            polygon_free = true;
                        } else {
                            polygon_points.add(p);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void renderToolOverlay(Renderer2D renderer2D, float x, float y) {
        if (currentMode == Mode.SUB) {
            renderer2D.setColor(Color.RED);
            renderer2D.drawCircleFilled(10,10, x, y, 0, 1,1);
            renderer2D.setColor(Color.WHITE);
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
                renderer2D.drawRectangleThin(size * 2, size * 2, x, y, 0,sclX,sclY);
                renderer2D.drawRectangleThin(size * 2, size * 2, x, y, 45,sclX,sclY);
                renderer2D.setColor(Color.WHITE);
            } else {
                if (line_points.isEmpty()) return;
                if (MathUtils.polygonArea(line_points) == 0) return;
                // draw the surrounding polygon
                renderer2D.setColor(Color.BLUE);
                renderer2D.drawCircleThin(12, 10, line_start.x, line_start.y, 0,1,1);
                renderer2D.drawCircleThin(12, 10, x, y, 0,1,1);
                renderer2D.setColor(Color.YELLOW);
                renderer2D.drawLineThin(line_points.get(0).x, line_points.get(0).y, line_points.get(1).x, line_points.get(1).y);
                renderer2D.drawLineThin(line_points.get(1).x, line_points.get(1).y, line_points.get(2).x, line_points.get(2).y);
                renderer2D.drawLineThin(line_points.get(2).x, line_points.get(2).y, line_points.get(3).x, line_points.get(3).y);
                renderer2D.drawLineThin(line_points.get(3).x, line_points.get(3).y, line_points.get(0).x, line_points.get(0).y);
                // draw the wheat field
                if (!procedural) {
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
            renderer2D.setColor(Color.BLUE);
            renderer2D.drawCircleFilled(12, 10, x, y, 0,1,1);
            renderer2D.setColor(Color.MAGENTA);
            renderer2D.drawCircleThin(circle_radius, circle_refinement, x, y, 0, 1, 1);
            if (!procedural) {
                if (circle_points.isEmpty()) refillPoints();
                float[] polygon = Utils.polygonConvertToFlat(circle_points);
                renderer2D.setColor(0.3569f, 0.3098f, 0.2275f, 0.4f);
                renderer2D.drawCurveFilled(null, 16.0f, 20, polygon, 0, 0, 0, 1, 1);
                renderer2D.setColor(Color.WHITE);
                renderer2D.drawPolygonFilled(polygon, bases[baseType], uv -> uv.rotateDeg(linesAngle).scl(2), 0, 0, 0, 1, 1);
            }
        }

        if (currentShape == Shape.POLYGON) {
            if (procedural) {
                renderer2D.setColor(Color.BLACK);
                if (!polygon_procedural.isEmpty()) {
                    renderer2D.drawPolygonThin(polygon_procedural, false, x, y, deg, sclX, sclY);
                }
            }
            if (!procedural) {
                renderer2D.setColor(Color.BLACK);
                renderer2D.drawCircleFilled(15, 5, x, y, 0, 1, 1);
                if (polygon_points.isEmpty()) return;

                renderer2D.setColor(Color.BLACK);
                renderer2D.drawCircleBorder(15, 5, 10, polygon_points.first().x, polygon_points.first().y, 0, 1, 1);
                renderer2D.setColor(Color.GREEN);
                for (int i = 0; i < polygon_points.size; i++) {
                    Vector2 p = polygon_points.get(i);
                    renderer2D.drawCircleFilled(5, 5, p.x, p.y, 0, 1, 1);
                }
                for (int i = 0; i < polygon_points.size - 1; i++) {
                    Vector2 p1 = polygon_points.get(i);
                    Vector2 p2 = polygon_points.get(i + 1);
                    renderer2D.drawLineThin(p1.x, p1.y, p2.x, p2.y);
                }
                renderer2D.drawLineThin(polygon_points.last().x, polygon_points.last().y, x, y);
                renderer2D.drawLineThin(x, y, polygon_points.first().x, polygon_points.first().y);

                if (polygon_points.size >= 3) {
                    // draw farmland
                    float[] polygon = Utils.polygonConvertToFlat(polygon_points);
                    renderer2D.setColor(0.3569f, 0.3098f, 0.2275f, 0.4f);
                    renderer2D.drawCurveFilled(null, 16.0f, 20, polygon, 0, 0, 0, 1, 1);
                    renderer2D.setColor(Color.WHITE);
                    renderer2D.drawPolygonFilled(polygon, bases[baseType], uv -> uv.rotateDeg(linesAngle).scl(2), 0, 0, 0, 1, 1);
                }
            }
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

    private class Rect {

        public float width;
        public float height;
        public Vector2 center;

        public Array<Vector2> getPolygonPoints(Vector2 origin, float angle) {
            Array<Vector2> points = new Array<>(true, 4);
            float hw = width * 0.5f;
            float hh = height * 0.5f;

            Vector2 a0 = new Vector2(-hw, -hh).add(center).rotateDeg(angle).add(origin);
            Vector2 a1 = new Vector2(hw, -hh).add(center).rotateDeg(angle).add(origin);
            Vector2 a2 = new Vector2(hw,  hh).add(center).rotateDeg(angle).add(origin);
            Vector2 a3 = new Vector2(-hw,  hh).add(center).rotateDeg(angle).add(origin);
            Vector2 a4 = new Vector2(-hw, -hh).add(center).rotateDeg(angle).add(origin);

            points.add(a0, a1, a2, a3);
            points.add(a4);
            return points;
        }

    }

    private void createFarmlandsProcedural_old(@NotNull Array<Vector2> envelopPolygon) {
        GeometryFactory gf = new GeometryFactory();
        Coordinate[] coords = new Coordinate[envelopPolygon.size + 1];
        for (int i = 0; i < envelopPolygon.size; i++) {
            Vector2 point = envelopPolygon.get(i);
            coords[i] = new Coordinate(point.x, point.y);
        }
        coords[envelopPolygon.size] = new Coordinate(envelopPolygon.first().x, envelopPolygon.first().y);

        LinearRing shell = gf.createLinearRing(coords);
        Polygon polygon = gf.createPolygon(shell, null);
        if (!polygon.isValid()) {
            System.out.println("Invalid polygon");
        }

        float[] points = new float[polygon.getCoordinates().length * 2];
        for (int i = 0; i < polygon.getCoordinates().length; i++) {
            points[2 * i] = (float) polygon.getCoordinates()[i].x;
            points[2 * i + 1] = (float) polygon.getCoordinates()[i].y;
        }

        List<Coordinate> seeds = new ArrayList<>();
        Envelope env = polygon.getEnvelopeInternal();
        Random rand = new Random(1234); // deterministic
        int seedCount = 20;
        while (seeds.size() < seedCount) {
            double x = env.getMinX() + rand.nextDouble() * env.getWidth();
            double y = env.getMinY() + rand.nextDouble() * env.getHeight();

            Point p = gf.createPoint(new Coordinate(x, y));
            if (polygon.contains(p)) {
                seeds.add(p.getCoordinate());
            }
        }

        MultiPoint sites = gf.createMultiPointFromCoords(seeds.toArray(new Coordinate[0]));
        VoronoiDiagramBuilder builder = new VoronoiDiagramBuilder();
        builder.setSites(sites);
        builder.setClipEnvelope(env); // bounding box only
        Geometry diagram = builder.getDiagram(gf);

        // clip each polygon to parent
        List<Polygon> subPolygons = new ArrayList<>();
        for (int i = 0; i < diagram.getNumGeometries(); i++) {
            Geometry cell = diagram.getGeometryN(i);
            Geometry clipped = cell.intersection(polygon);

            if (clipped instanceof Polygon) {
                subPolygons.add((Polygon) clipped);
            } else if (clipped instanceof MultiPolygon) {
                MultiPolygon mp = (MultiPolygon) clipped;
                for (int j = 0; j < mp.getNumGeometries(); j++) {
                    subPolygons.add((Polygon) mp.getGeometryN(j));
                }
            }
        }

        Shape2DPolygon[] shape2DSubPolygons = new Shape2DPolygon[subPolygons.size()];
        for (int i = 0; i < subPolygons.size(); i++) {
            Polygon p = subPolygons.get(i);
            float[] points_sub = new float[p.getCoordinates().length * 2];
            for (int j = 0; j < p.getCoordinates().length; j++) {
                points_sub[2 * j] = (float) p.getCoordinates()[j].x;
                points_sub[2 * j + 1] = (float) p.getCoordinates()[j].y;
            }
            shape2DSubPolygons[i] = new Shape2DPolygon(points_sub);
        }

        for (Shape2DPolygon subPolygon : shape2DSubPolygons) {
            CommandTerrainFarmlandAdd cmd = new CommandTerrainFarmlandAdd();
            cmd.polygon = subPolygon.points.pack();
            cmd.baseType = baseType;
            cmd.linesAngle = linesAngle;
            map.addCommand(cmd);
            baseType = MathUtils.randomUniformInt(0, bases.length);
            linesAngle = MathUtils.randomUniformFloat(0, 360);
        }
    }

}
