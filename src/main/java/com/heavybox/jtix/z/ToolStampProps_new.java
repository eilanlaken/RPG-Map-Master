package com.heavybox.jtix.z;

import com.heavybox.jtix.assets.Assets;
import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.graphics.TexturePack;
import com.heavybox.jtix.graphics.TextureRegion;
import com.heavybox.jtix.input.Input;
import com.heavybox.jtix.input.Keyboard;
import com.heavybox.jtix.input.Mouse;
import com.heavybox.jtix.math.MathUtils;
import com.heavybox.jtix.math.Vector2;

import javax.swing.plaf.synth.Region;

public class ToolStampProps_new extends Tool {

    public final TexturePack layer3;

    // tool overlay
    public TextureRegion region;
    public int singleIndex = 0;
    public Array<Vector2> points = new Array<>();
    public boolean polygonModeFree = true;

    private Mode mode = Mode.values()[0];

    public ToolStampProps_new(Map map) {
        super(map);
        sclX = 0.25f;
        sclY = 0.25f;
        layer3 = Assets.get("assets/texture-packs/layer_3.yml");
    }

    @Override
    public void update(float delta) {
        // input
        float verticalScroll = Input.mouse.getVerticalScroll();
        boolean leftButtonClicked = Input.mouse.isButtonClicked(Mouse.Button.LEFT);
        boolean rightButtonClicked = Input.mouse.isButtonClicked(Mouse.Button.RIGHT);

        // tool settings - mode
        if (verticalScroll > 0) {
            mode = Mode.values()[(mode.ordinal() + 1) % Mode.values().length]; // next
            changeMod();
            return;
        } else if (verticalScroll < 0) {
            mode = Mode.values()[(mode.ordinal() - 1 + Mode.values().length) % Mode.values().length];
            changeMod();
            return;
        }



        if (mode.singles && leftButtonClicked) {
            CommandTokenCreate createProp = new CommandTokenCreate(
                    3,
                    x, y, deg, sclX, sclY, true,
                    region
            );
            createProp.type = MapToken.Type.PROP;
            map.addCommand(createProp);
            singleIndex = MathUtils.randomUniformInt(0,6);
            region = layer3.getRegion("assets/textures-layer-3/prop_" + mode.name().toLowerCase() + "_" + singleIndex + ".png");
        }

        if (!mode.singles && polygonModeFree && leftButtonClicked) {
            Vector2 p = new Vector2(x, y);
            points.add(p);
            polygonModeFree = false;
        }

        if (!mode.singles && !polygonModeFree && leftButtonClicked) {
            Vector2 p = new Vector2(x, y); // need to test intersections etc.
            points.add(p);
            if (points.size < 4) return;

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
                intersectionIndex = i + 1;
                break;
            }
            if (intersectionIndex != -1) {
                Array<Vector2> polyPoints = new Array<>(true, 5);
                for (int i = intersectionIndex; i < points.size - 1; i++) {
                    polyPoints.add(points.get(i));
                }
                polyPoints.add(intersection);
                float[] polyPointsFlat = new float[polyPoints.size * 2];
                for (int i = 0; i < polyPoints.size; i++) {
                    polyPointsFlat[i * 2]     = points.get(i).x;
                    polyPointsFlat[i * 2 + 1] = points.get(i).y;
                }

                // calculate bounding box
                Vector2 bottomLeftCorner = new Vector2(Float.MAX_VALUE, Float.MAX_VALUE);
                Vector2 topRightCorner = new Vector2(Float.MIN_VALUE, Float.MIN_VALUE);
                for (Vector2 v : polyPoints) {
                    if (v.x < bottomLeftCorner.x) bottomLeftCorner.x = v.x;
                    if (v.y < bottomLeftCorner.y) bottomLeftCorner.y = v.y;
                    if (v.x > topRightCorner.x) topRightCorner.x = v.x;
                    if (v.y > topRightCorner.y) topRightCorner.y = v.y;
                }

                float width = topRightCorner.x - bottomLeftCorner.x;
                float height = topRightCorner.y - bottomLeftCorner.y;
                float stepSizePixels = 10;
                Array<Vector2> gridPoints = new Array<>();
                for (float rect_x = bottomLeftCorner.x; rect_x < topRightCorner.x; rect_x += stepSizePixels) {
                    for (float rect_y = bottomLeftCorner.y; rect_y < topRightCorner.y; rect_y += stepSizePixels) {
                        boolean contained = MathUtils.polygonContainsPoint(polyPointsFlat, rect_x, rect_y);
                        if (contained) gridPoints.add(new Vector2(rect_x + MathUtils.randomUniformFloat(-2,2), rect_y  + MathUtils.randomUniformFloat(-2,2)));
                    }
                }

                for (Vector2 position : gridPoints) {
                    CommandTokenCreate createProp = new CommandTokenCreate(
                            3,
                            position.x, position.y, deg, sclX, sclY, false,
                            getRegion()
                    );
                    createProp.type = MapToken.Type.PROP;
                    map.addCommand(createProp);
                }

                polygonModeFree = true;
            }

        }

    }

    private float getSpacing() {
        if (mode == Mode.CHOPPED_TRUNK) return 10;
        if (mode == Mode.FLOWER_DAISY || mode == Mode.FLOWER_TULIP || mode == Mode.FLOWER_SUNFLOWER || mode == Mode.FLOWER_SCORPION)
            return 6;

        return 10; // default
    }

    private TextureRegion getRegion() {
        if (mode == Mode.CHOPPED_TRUNK) {
            return layer3.getRegion("assets/textures-layer-3/prop_chopped_trunk.png");
        }

        if (mode == Mode.FLOWER_DAISY || mode == Mode.FLOWER_TULIP || mode == Mode.FLOWER_SUNFLOWER || mode == Mode.FLOWER_SCORPION) {
            int index = MathUtils.randomUniformInt(0,6);
            return layer3.getRegion("assets/textures-layer-3/prop_" + mode.name().toLowerCase() + "_" + index + ".png");
        }


        return null;
    }

    @Override
    public void renderToolOverlay(Renderer2D renderer2D, float x, float y) {
        if (mode.singles) {
            renderer2D.setColor(Color.WHITE);
            renderer2D.drawTextureRegion(region, x, y, 0, this.sclX, this.sclY);
            return;
        }

        if (polygonModeFree) {
            renderer2D.setColor(Color.RED);
            renderer2D.drawCircleFilled(5,5, x, y, 0, 1,1);
            renderer2D.setColor(Color.WHITE);
        } else {
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
        }
    }

    private void changeMod() {
        System.out.println(mode);

        if (mode.singles) {
            singleIndex = MathUtils.randomUniformInt(0, 6);
            region = layer3.getRegion("assets/textures-layer-3/prop_" + mode.name().toLowerCase() + "_" + singleIndex + ".png");
        } else {
            points.clear();
            polygonModeFree = true;
        }
    }

    @Override
    public void activate() {
        changeMod();
    }

    @Override
    public void deactivate() {
        points.clear();
        polygonModeFree = true;
    }

    public enum Mode {
        BARRELS(true),
        BOXES(true),
        BRIDGE(false), // along path
        CHOPPED_TRUNK(false), // polygon scatter
        FENCE(false), // along path
        FLOWER_DAISY(false), // polygon
        FLOWER_SCORPION(false), // polygon
        FLOWER_SUNFLOWER(false), // polygon
        FLOWER_TULIP(false), // polygon
        HUT(false), // polygon
        LODGE(true),
        PILE(true),
        PILLAR_STONE_SHORT(true),
        PILLAR_STONE_TALL(true),
        ROAD_SIGNS(true),
        SACK(true),
        SCARECROW(true),
        STRAW(true),
        TOWER(true),
        WINDMILL(true),
        ;

        public boolean singles;

        Mode(boolean singles) {
            this.singles = singles;
        }

    }

}
