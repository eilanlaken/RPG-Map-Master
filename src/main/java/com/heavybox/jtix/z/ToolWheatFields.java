package com.heavybox.jtix.z;

import com.heavybox.jtix.assets.Assets;
import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.graphics.Texture;
import com.heavybox.jtix.input.Input;
import com.heavybox.jtix.input.Mouse;
import com.heavybox.jtix.math.MathUtils;
import com.heavybox.jtix.math.Vector2;

public class ToolWheatFields extends Tool {

    public State state = State.FREE;

    private Array<Vector2> points = new Array<>(true, 10);
    private float[] polygon;
    private int fieldType = 0; // 0...4
    private float linesAngle = MathUtils.randomUniformFloat(0, 360);
    private boolean addLines = true;
    private int harvestType = 0; // 0 = none, 1, 2

    private Texture base_0;
    private Texture lines;
    private Texture harvest;
    private Color harvestTint;

    public ToolWheatFields(Map map) {
        super(map);
        base_0 = Assets.get("assets/textures-layer-1/terrain-wheat-field-base_0.png");
        lines = Assets.get("assets/textures-layer-1/terrain-wheat-field-lines.png");
    }

    @Override
    public void update(float delta) {
        if (state == State.FREE) {
            if (Input.mouse.isButtonClicked(Mouse.Button.LEFT)) {
                Vector2 p = new Vector2(x, y); // need to test intersections etc.
                points.add(p);
                state = State.DRAW_POLYGON;
            }
        } else if (state == State.DRAW_POLYGON) {
            if (Input.mouse.isButtonClicked(Mouse.Button.RIGHT)) { // cancel
                points.clear();
                state = State.FREE;
            }
            else if (Input.mouse.isButtonClicked(Mouse.Button.LEFT)) {
                Vector2 p = new Vector2(x, y); // need to test intersections etc.
                if (points.size < 3) {
                    points.add(p);
                } else {
                    points.add(p);
                    // last added segment
                    Vector2 p1 = points.get(points.size - 2);
                    Vector2 p2 = points.get(points.size - 1);
                    Vector2 intersection = new Vector2();
                    int intersectionIndex = -1;
                    for (int i = 0; i < points.size - 3; i++) {
                        Vector2 a = points.get(i);
                        Vector2 b = points.get(i + 1);
                        int result = MathUtils.segmentsIntersection(a, b, p1, p2, intersection);
                        if (result != 0 && !intersection.equals(b)) continue;
                        System.out.println(i);
                        intersectionIndex = i + 1;
                        break;
                    }
                    if (intersectionIndex != -1) {
                        Array<Vector2> polyPoints = new Array<>(true, 5);
                        for (int i = intersectionIndex; i < points.size - 1; i++) {
                            polyPoints.add(points.get(i));
                        }
                        polyPoints.add(intersection);
                        points.clear();
                        points.addAll(polyPoints);
                        points.pack();
                        polygon = new float[points.size * 2];
                        for (int i = 0; i < points.size; i++) {
                            polygon[i * 2]     = points.get(i).x;
                            polygon[i * 2 + 1] = points.get(i).y;
                        }
                        // no lines, no harvest
                        if (!addLines && harvestType == 0) {
                            // immediately add createField command, no need to set angle

                            state = State.FREE;
                        } else {
                            state = State.SET_ANGLE;
                            linesAngle = MathUtils.randomUniformFloat(0,360);
                        }
                    }
                }
            }
        } else if (state == State.SET_ANGLE) {

        }
    }

    @Override
    public void renderToolOverlay(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY) {
        if (state == State.FREE) {
            renderer2D.setColor(Color.RED);
            renderer2D.drawCircleFilled(5,5, x, y, 0, 1,1);
            renderer2D.setColor(Color.WHITE);
        } else if (state == State.DRAW_POLYGON) {
            if (points.isEmpty()) return;
            renderer2D.setColor(Color.RED);
            for (int i = 0; i < points.size; i++) {
                Vector2 p = points.get(i);
                renderer2D.drawCircleFilled(5, 5, p.x, p.y, 0, 1, 1);
            }
            for (int i = 0; i < points.size - 1; i++) {
                Vector2 p1 = points.get(i);
                Vector2 p2 = points.get(i + 1);
                renderer2D.drawLineThin(p1.x, p1.y, p2.x, p2.y);
            }
            renderer2D.drawLineThin(points.last().x, points.last().y, x, y);
            renderer2D.setColor(Color.WHITE);
        } else if (state == State.SET_ANGLE) {
            renderer2D.drawPolygonFilled(polygon, base_0, 0, 0, 0, 1,1);
            renderer2D.drawPolygonFilled(polygon, lines, uv -> uv.rotateDeg(linesAngle),0,0,0,1,1);
        }
    }

    @Override
    public void activate() {
        points.clear();
        state = State.FREE;
    }

    @Override
    public void deactivate() {
        points.clear();
        state = State.FREE;
    }

    private enum State {
        FREE,
        DRAW_POLYGON,
        SET_ANGLE,
        ;
    }

}
