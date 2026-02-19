package com.heavybox.jtix.z;

import org.locationtech.jts.geom.*;
import org.locationtech.jts.triangulate.VoronoiDiagramBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DevTests {

    public static void run() {
        try {

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static void runJTSDemo() {
        GeometryFactory gf = new GeometryFactory();
        Coordinate[] coords = new Coordinate[] {
                new Coordinate(0, 0),
                new Coordinate(10, 0),
                new Coordinate(10, 8),
                new Coordinate(4, 12),
                new Coordinate(0, 8),
                new Coordinate(0, 0)   // close the ring
        };
        LinearRing shell = gf.createLinearRing(coords);
        Polygon polygon = gf.createPolygon(shell, null);
        if (!polygon.isValid()) {
            System.out.println("Invalid polygon");
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

    }

}
