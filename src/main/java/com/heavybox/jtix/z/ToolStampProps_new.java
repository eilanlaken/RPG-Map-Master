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
        // tool settings - mode
        if (Input.mouse.getVerticalScroll() > 0) {
            mode = Mode.values()[(mode.ordinal() + 1) % Mode.values().length]; // next
            changeMod();
            return;
        } else if (Input.mouse.getVerticalScroll() < 0) {
            mode = Mode.values()[(mode.ordinal() - 1 + Mode.values().length) % Mode.values().length];
            changeMod();
            return;
        }

        boolean leftButtonClicked = Input.mouse.isButtonClicked(Mouse.Button.LEFT);
        boolean rightButtonClicked = Input.mouse.isButtonClicked(Mouse.Button.RIGHT);

        if (Input.mouse.isButtonClicked(Mouse.Button.LEFT)) {
            CommandTokenCreate createProp = new CommandTokenCreate(
                    3,
                    x, y, deg, sclX, sclY, true,
                    region
            );
            createProp.type = MapToken.Type.TREE;
            map.addCommand(createProp);
        }
    }

    @Override
    public void renderToolOverlay(Renderer2D renderer2D, float x, float y) {
        if (mode.singles) {
            renderer2D.setColor(Color.WHITE);
            renderer2D.drawTextureRegion(region, x, y, 0, this.sclX, this.sclY);
            return;
        }

    }

    private void changeMod() {
        System.out.println(mode);

        if (mode.singles) {
            singleIndex = MathUtils.randomUniformInt(0, 6);
            region = layer3.getRegion("assets/textures-layer-3/prop_" + mode.name().toLowerCase() + "_" + singleIndex + ".png");
        } else {
            points.clear();
        }
    }

    @Override
    public void activate() {
        changeMod();
    }

    @Override
    public void deactivate() {

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
