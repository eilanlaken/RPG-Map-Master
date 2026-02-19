package com.heavybox.jtix.z;

import com.heavybox.jtix.RPGMapMakerScene;
import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.math.Shape2DPolygon;
import org.locationtech.jts.geom.*;
import org.locationtech.jts.triangulate.VoronoiDiagramBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/*
TODO: implement this:
https://maps.probabletrain.com/#/
https://maps.probabletrain.com/#/algorithmoverview
https://www.youtube.com/watch?v=TznowSvHJrU
https://www.youtube.com/watch?v=3G5d8ob_Lfo

2 methods: tensor fields and voronoi diagrams / noise.

Tensor field:
- select a polygon area
- add "stress points"
- calculate streamlines
- generate graph
- generate polygons by starting at a node and always making a right turn
- fill polygons: either with architecture or with farmland

Voronoi: https://hackaday.com/2017/05/28/procedurally-generating-random-medieval-cities/
- subdivide an area

 */
public class ToolStamp_Procedural extends Tool {

    Polygon polygon;
    Shape2DPolygon shape2DPolygon;
    Shape2DPolygon[] shape2DSubPolygons;

    // if mode == polygon, use voronoi
    // if mode == circle, fill houses along circle

    public ToolStamp_Procedural(final RPGMapMakerScene scene) {
        super(scene);

        // DEMO
        GeometryFactory gf = new GeometryFactory();
        Coordinate[] coords = new Coordinate[] {
                new Coordinate(0, 0),
                new Coordinate(1000, 0),
                new Coordinate(1000, 800),
                new Coordinate(400, 1200),
                new Coordinate(0, 800),
                new Coordinate(0, 0)   // close the ring
        };
        LinearRing shell = gf.createLinearRing(coords);
        polygon = gf.createPolygon(shell, null);
        if (!polygon.isValid()) {
            System.out.println("Invalid polygon");
        }

        float[] points = new float[polygon.getCoordinates().length * 2];
        for (int i = 0; i < polygon.getCoordinates().length; i++) {
            points[2 * i] = (float) polygon.getCoordinates()[i].x;
            points[2 * i + 1] = (float) polygon.getCoordinates()[i].y;
        }
        shape2DPolygon = new Shape2DPolygon(points);


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

        shape2DSubPolygons = new Shape2DPolygon[subPolygons.size()];

        for (int i = 0; i < subPolygons.size(); i++) {
            Polygon p = subPolygons.get(i);
            float[] points_sub = new float[p.getCoordinates().length * 2];
            for (int j = 0; j < p.getCoordinates().length; j++) {
                points_sub[2 * j] = (float) p.getCoordinates()[j].x;
                points_sub[2 * j + 1] = (float) p.getCoordinates()[j].y;
            }
            shape2DSubPolygons[i] = new Shape2DPolygon(points_sub);
        }
    }

    @Override
    public void update(float delta) {

    }

    @Override
    public void renderToolOverlay(Renderer2D renderer2D, float x, float y) {
        renderer2D.setColor(Color.RED);
        renderer2D.drawPolygonThin(shape2DPolygon.points, false, 0,0,0,1,1);
        renderer2D.setColor(Color.WHITE);
        for (int i = 0; i < shape2DSubPolygons.length; i++) {
            renderer2D.drawPolygonThin(shape2DSubPolygons[i].points, false, 0,0,0,1,1);
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
        return "Procedural Brush";
    }

}
