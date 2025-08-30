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
import com.heavybox.jtix.math.Vector2;

import java.util.Comparator;

public class ToolStampCityBlock extends Tool {

    public final TexturePack layer3;

    // singles mode
    public Array<House> singleHouses = new Array<>();
    private final Array<House> toolOverlay = new Array<>();
    public TextureRegion foundation;
    public TextureRegion foundationOverlay;
    public TextureRegion roof;
    public TextureRegion roofOverlay;
    public BaseType baseType = BaseType.DIAGONAL_BIG_LEFT;
    public RoofType roofType = RoofType.BLUE;
    public int foundationOverlayIndex = 0;
    public int roofOverlayIndex = 0;
    
    // combos mode
    public Array<House> houses = new Array<>(true, 4);
    
    public Mode mode = Mode.SINGLES;
    
    public ToolStampCityBlock(Map map) {
        super(map);
        layer3 = Assets.get("assets/texture-packs/layer_3.yml");
        sclX = 0.5f;
        sclY = 0.5f;
        setRegions();
    }

    @Override
    public void update(float delta) {
        if (mode == Mode.SINGLES) {
            if (Input.keyboard.isKeyJustPressed(Keyboard.Key.LEFT_SHIFT)) {
                baseType = BaseType.values()[(baseType.ordinal() + 1) % BaseType.values().length]; // next
                setRegions();
            } else if (Input.keyboard.isKeyJustPressed(Keyboard.Key.TAB)) {
                baseType = BaseType.values()[(baseType.ordinal() - 1 + BaseType.values().length) % BaseType.values().length]; // prev
                setRegions();
            } else if (Input.keyboard.isKeyJustPressed(Keyboard.Key.Z)) {
                roofType = RoofType.values()[(roofType.ordinal() + 1) % RoofType.values().length]; // next
                setRegions();
            } else if (Input.keyboard.isKeyJustPressed(Keyboard.Key.X)) {
                roofType = RoofType.values()[(roofType.ordinal() - 1 + RoofType.values().length) % RoofType.values().length]; // prev
                setRegions();
            } else if (Input.mouse.isButtonClicked(Mouse.Button.LEFT)) {
                House house = new House();
                house.x = x;
                house.y = y;
                house.baseType = baseType;
                house.roofType = roofType;
                house.foundationOverlayIndex = foundationOverlayIndex;
                house.roofOverlayIndex = roofOverlayIndex;
                singleHouses.add(house);
            } else if (Input.keyboard.isKeyJustPressed(Keyboard.Key.TAB)) {
                if (!singleHouses.isEmpty()) singleHouses.pop();
            } else if (Input.keyboard.isKeyJustPressed(Keyboard.Key.ENTER) && !singleHouses.isEmpty()) {
                // calculate center of mass
                Array<House> houseArray = new Array<>();
                houseArray.addAll(singleHouses);
                houseArray.sort(Comparator.comparingInt(o -> -(int) o.y));
                Vector2 cm = new Vector2();
                for (House house : houseArray) {
                    cm.add(house.x, house.y);
                }
                cm.scl(1f / houseArray.size);
                System.out.println("<combination>");
                for (House house : houseArray) {
                    System.out.println("\t" +
                            "<object " +
                            "baseType=\"" + house.baseType.ordinal() + "\" " +
                            "x=\"" + (house.x - cm.x) + "\" " +
                            "y=\"" + (house.y - cm.y) + "\" " +
                            "roofType=\"" + house.roofType + "\" " +
                            "foundationOverlayIndex=\"" + house.foundationOverlayIndex + "\" " +
                            "roofOverlayIndex=\"" + house.roofOverlayIndex + "\"/>"
                    );
                }
                System.out.println("</combination>");
            }
        } else if (mode == Mode.COMBOS) {
            
        }
    }


    @Override
    public void renderToolOverlay(Renderer2D renderer2D, float x, float y) {
        if (mode == Mode.SINGLES) {
            renderer2D.setColor(Color.WHITE);
            // render current bullet
            int direction = (baseType == BaseType.DIAGONAL_BIG_RIGHT || baseType == BaseType.DIAGONAL_SMALL_RIGHT) ? -1 : 1;
            renderer2D.drawTextureRegion(foundation, x, y, 0, direction * this.sclX, this.sclY);
            renderer2D.drawTextureRegion(foundationOverlay, x, y, 0, direction * this.sclX, this.sclY);
            renderer2D.drawTextureRegion(roof, x, y, 0, direction * this.sclX, this.sclY);
            renderer2D.drawTextureRegion(roofOverlay, x, y, 0, direction * this.sclX, this.sclY);
            // render stamped
            toolOverlay.clear();
            toolOverlay.addAll(singleHouses);
            toolOverlay.sort(Comparator.comparingInt(o -> -(int) o.y));
            for (House house : toolOverlay) {
                house.render(renderer2D);
            }
        } else if (mode == Mode.COMBOS) {
            
        }
    }

    private void setRegions() {
        String prefix = "assets/textures-layer-3/" + switch (baseType) {
            case DIAGONAL_BIG_LEFT, DIAGONAL_BIG_RIGHT -> "house-diagonal-big-";
            case DIAGONAL_SMALL_LEFT, DIAGONAL_SMALL_RIGHT -> "house-diagonal-small-";
            case HORIZONTAL_BIG -> "house-horizontal-big-";
            case HORIZONTAL_SMALL -> "house-horizontal-small-";
            case VERTICAL_BIG -> "house-vertical-big-";
            case VERTICAL_SMALL -> "house-vertical-small-";
        };
        String suffix = ".png";
        foundation = layer3.getRegion(prefix + "foundation" + suffix);
        foundationOverlay = layer3.getRegion(prefix + "foundation-overlay_" + foundationOverlayIndex + suffix);
        roof = layer3.getRegion(prefix + "roof-base_" + roofType.name().toLowerCase() + suffix);
        roofOverlay = layer3.getRegion(prefix + "roof-overlay_" + roofOverlayIndex + suffix);
    }
    
    @Override
    public void activate() {
        houses.clear();
        setRegions();
    }

    @Override
    public void deactivate() {
        houses.clear();
    }

    public static class Combination {
        public House[] houses;
    }

    public class House {
        float x, y;
        BaseType baseType;
        RoofType roofType;
        int foundationOverlayIndex;
        int roofOverlayIndex;

        void render(Renderer2D renderer2D) {
            int direction = (baseType == BaseType.DIAGONAL_BIG_RIGHT || baseType == BaseType.DIAGONAL_SMALL_RIGHT) ? -1 : 1;
            String prefix = "assets/textures-layer-3/" + switch (baseType) {
                case DIAGONAL_BIG_LEFT, DIAGONAL_BIG_RIGHT -> "house-diagonal-big-";
                case DIAGONAL_SMALL_LEFT, DIAGONAL_SMALL_RIGHT -> "house-diagonal-small-";
                case HORIZONTAL_BIG -> "house-horizontal-big-";
                case HORIZONTAL_SMALL -> "house-horizontal-small-";
                case VERTICAL_BIG -> "house-vertical-big-";
                case VERTICAL_SMALL -> "house-vertical-small-";
            };
            String suffix = ".png";
            TextureRegion foundation = layer3.getRegion(prefix + "foundation" + suffix);
            TextureRegion foundationOverlay = layer3.getRegion(prefix + "foundation-overlay_" + foundationOverlayIndex + suffix);
            TextureRegion roof = layer3.getRegion(prefix + "roof-base_" + roofType.name().toLowerCase() + suffix);
            TextureRegion roofOverlay = layer3.getRegion(prefix + "roof-overlay_" + roofOverlayIndex + suffix);
            renderer2D.drawTextureRegion(foundation, x, y, 0, direction * sclX, sclY);
            renderer2D.drawTextureRegion(foundationOverlay, x, y, 0, direction * sclX, sclY);
            renderer2D.drawTextureRegion(roof, x, y, 0, direction * sclX, sclY);
            renderer2D.drawTextureRegion(roofOverlay, x, y, 0, direction * sclX, sclY);
        }

    }
    
    public enum BaseType {

        DIAGONAL_BIG_LEFT,
        DIAGONAL_BIG_RIGHT,

        DIAGONAL_SMALL_LEFT,
        DIAGONAL_SMALL_RIGHT,

        HORIZONTAL_BIG,
        HORIZONTAL_SMALL,

        VERTICAL_BIG,
        VERTICAL_SMALL,
        ;
    }

    public enum RoofType {
        BLUE,
        GREY,
        PURPLE,
        RED,
        YELLOW,
        ;
    }

    public enum Mode {

        SINGLES,
        COMBOS,
        ;

    }

}
