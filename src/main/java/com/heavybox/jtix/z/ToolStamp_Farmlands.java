package com.heavybox.jtix.z;

import com.heavybox.jtix.RPGMapMakerScene;
import com.heavybox.jtix.assets.Assets;
import com.heavybox.jtix.collections.Collections;
import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.graphics.Texture;
import com.heavybox.jtix.input.Input;
import com.heavybox.jtix.input.Keyboard;
import com.heavybox.jtix.input.Mouse;
import com.heavybox.jtix.math.MathUtils;
import com.heavybox.jtix.math.Shape2DPolygon;
import com.heavybox.jtix.math.Vector2;
import org.locationtech.jts.geom.*;
import org.locationtech.jts.triangulate.VoronoiDiagramBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ToolStamp_Farmlands extends Tool {

    private final Texture[] bases = new Texture[5];
    private int baseType = MathUtils.randomUniformInt(0, bases.length);
    private float linesAngle = MathUtils.randomUniformFloat(0, 360);
    private boolean procedural = false;

    public ToolStamp_Farmlands(RPGMapMakerScene scene) {
        super(scene);
        bases[0] = Assets.get("assets/textures-layer-0/farmland_0.png");
        bases[1] = Assets.get("assets/textures-layer-0/farmland_1.png");
        bases[2] = Assets.get("assets/textures-layer-0/farmland_2.png");
        bases[3] = Assets.get("assets/textures-layer-0/farmland_3.png");
        bases[4] = Assets.get("assets/textures-layer-0/farmland_4.png");
        free = true;
        mode = Mode.ADD;
        shape = Shape.POLYGON;
    }

    @Override
    protected String[] getPrefixes() {
        return new String[] {"farmland"};
    }

    @Override
    public void update(float delta) {
        // inputs
        boolean backspaceJustPressed = Input.keyboard.isKeyJustPressed(Keyboard.Key.BACKSPACE);
        boolean pJustPressed = Input.keyboard.isKeyJustPressed(Keyboard.Key.P);
        boolean leftClicked = Input.mouse.isButtonClicked(Mouse.Button.LEFT);

        // brush settings
        if (backspaceJustPressed) {
            mode = Collections.enumNext(mode);
            polygonPoints.clear();
            free = true;
            shape = (mode == Mode.ADD) ? Shape.POLYGON : Shape.POINT;
            return;
        } else if (pJustPressed) {
            procedural = !procedural;
            return;
        }

        if (mode == Mode.SUB) {
            if (leftClicked) {
                CommandTerrainFarmlandSub cmd = new CommandTerrainFarmlandSub(x, y);
                map.addCommand(cmd);
            }
        }

        // if mode == ADD
        if (free) {
            if (leftClicked) {
                Vector2 p = new Vector2(x, y);
                polygonPoints.add(p);
                free = false;
            }
            return;
        }

        if (leftClicked) {
            Vector2 p = new Vector2(x, y);
            polygonPoints.add(p);
            if (polygonPoints.size < 4) {
                return;
            }
            if (Vector2.dst(p, polygonPoints.first()) <= 20) {
                if (!procedural) createFarmland();
                else createFarmlandsProcedural();
                polygonPoints.clear();
                free = true;
            }
        }
    }

    private void createFarmland() {
        CommandTerrainFarmlandAdd cmd = new CommandTerrainFarmlandAdd();
        cmd.polygon = Utils.polygonConvertToFlat(polygonPoints);
        cmd.baseType = baseType;
        cmd.linesAngle = linesAngle;
        map.addCommand(cmd);
        baseType = MathUtils.randomUniformInt(0, bases.length);
        linesAngle = MathUtils.randomUniformFloat(0, 360);
    }

    private void createFarmlandsProcedural() {
        GeometryFactory gf = new GeometryFactory();
        Coordinate[] coords = new Coordinate[polygonPoints.size + 1];
        for (int i = 0; i < polygonPoints.size; i++) {
            Vector2 point = polygonPoints.get(i);
            coords[i] = new Coordinate(point.x, point.y);
        }
        coords[polygonPoints.size] = new Coordinate(polygonPoints.first().x, polygonPoints.first().y);

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
        Shape2DPolygon shape2DPolygon = new Shape2DPolygon(points);

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

    @Override
    public String getHelperText() {
        return super.getHelperText() + " | " +
                "Mode: " + mode + " (BACKSPACE) | " +
                "Procedural? " + procedural + " (P) | " +
                "";
    }

    @Override
    public void renderToolText(Renderer2D renderer2D, float x, float y) {
        if (procedural) renderer2D.drawStringLine("Procedural", 14, true, x + 20, y + 20, 0 , 1, 1);
    }

    @Override
    public void renderToolOverlay(Renderer2D renderer2D, float x, float y) {
        if (mode == Mode.SUB) {
            renderer2D.setColor(Color.RED);
            renderer2D.drawCircleFilled(10,10, x, y, 0, 1,1);
            renderer2D.setColor(Color.WHITE);
            return;
        }

        // ADD wheat fields
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

            if (polygonPoints.size >= 3 && !procedural) {
                // draw farmland
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
        return "Farmlands Stamp";
    }
}
