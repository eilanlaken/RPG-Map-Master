package com.heavybox.jtix.z;

import com.heavybox.jtix.assets.Assets;
import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.graphics.TexturePack;
import com.heavybox.jtix.graphics.TextureRegion;
import com.heavybox.jtix.input.Input;
import com.heavybox.jtix.input.Keyboard;
import com.heavybox.jtix.input.Mouse;
import com.heavybox.jtix.math.MathUtils;

public class ToolStampPlants extends Tool {

    public boolean addFruits = false;
    public boolean addTrunk = true;
    public Mode mode = Mode.TREE_REGULAR;
    public int batchSize = 1;
    public TexturePack layer3;
    public Color fruitsColor = Color.RED;

    public ToolStampPlants(Map map) {
        super(map);
        sclX = 0.25f;
        sclY = 0.25f;
        layer3 = Assets.get("assets/texture-packs/layer_3.yml");
    }

    @Override
    public void update(float delta) {
        if (Input.keyboard.isKeyJustPressed(Keyboard.Key.TAB)) {
            mode = Mode.values()[(mode.ordinal() + 1) % Mode.values().length];
            System.out.println(mode);
        } else if (Input.mouse.isButtonClicked(Mouse.Button.LEFT)) {
            if (mode == Mode.TREE_REGULAR || mode == Mode.TREE_CYPRESS) {
                TextureRegion base =
                        mode == Mode.TREE_REGULAR ?
                                layer3.getRegion("assets/textures-layer-3/tree_regular_" + MathUtils.randomUniformInt(1,7) + ".png")
                                :
                                layer3.getRegion("assets/textures-layer-3/tree_cypress_" + MathUtils.randomUniformInt(1, 7) + ".png");
                TextureRegion trunk = addTrunk ? layer3.getRegion("assets/textures-layer-3/tree_regular_trunk_" + MathUtils.randomUniformInt(1,11) + ".png") : null;
                TextureRegion fruits = addFruits ? // if addFruits, add regular or cypress fruits. Else, ignore.
                        (mode == Mode.TREE_REGULAR ? layer3.getRegion("assets/textures-layer-3/tree_regular_fruits.png")
                                : layer3.getRegion("assets/textures-layer-3/tree_cypress_fruits.png")) : null;
                CommandTokenCreate createPlant = new CommandTokenCreate(
                        3,
                        x, y, deg, sclX, sclY, true,
                        base, trunk, fruits
                );
                map.addCommand(createPlant);
            } else if (mode == Mode.TREE_DENSE || mode == Mode.TREE_SPARSE) {
                TextureRegion base = mode == Mode.TREE_DENSE ?
                        layer3.getRegion("assets/textures-layer-3/tree_dense_" + MathUtils.randomUniformInt(1,7) + ".png")
                        :
                        layer3.getRegion("assets/textures-layer-3/tree_sparse_" + MathUtils.randomUniformInt(1, 7) + ".png");
                TextureRegion fruits = addFruits ? // if addFruits, add regular or cypress fruits. Else, ignore.
                        (mode == Mode.TREE_DENSE ? layer3.getRegion("assets/textures-layer-3/tree_dense_fruits.png")
                                : layer3.getRegion("assets/textures-layer-3/tree_sparse_fruits.png")) : null;
                CommandTokenCreate createPlant = new CommandTokenCreate(
                        3,
                        x, y, deg, 2 * sclX, 2 * sclY, true,
                        base, fruits
                );
                map.addCommand(createPlant);
            } else if (mode == Mode.BUSHES) {
                TextureRegion base = layer3.getRegion("assets/textures-layer-3/bush_" + MathUtils.randomUniformInt(1,7) + ".png");
                CommandTokenCreate createPlant = new CommandTokenCreate(
                        3,
                        x, y, deg, sclX, sclY, true, base
                );
                map.addCommand(createPlant);
            } else if (mode == Mode.FLOWERS) {
                TextureRegion base = layer3.getRegion("assets/textures-layer-3/flower_" + MathUtils.randomUniformInt(1,5) + ".png");
                CommandTokenCreate createPlant = new CommandTokenCreate(
                        3,
                        x, y, deg, 4 * sclX, 4 * sclY, true, base
                );
                map.addCommand(createPlant);
            }
        } else if (Input.mouse.isButtonClicked(Mouse.Button.RIGHT)) {
            // randomize tree
        }
    }

    @Override
    public void renderToolOverlay(Renderer2D renderer2D, float x, float y) {
        renderer2D.setColor(Color.GREEN);
        renderer2D.drawCircleThin(20,30, x, y, deg, sclX, sclY);
        renderer2D.setColor(Color.WHITE);
    }

    @Override
    public void activate() {

    }

    @Override
    public void deactivate() {

    }

    public enum Mode {
        TREE_REGULAR,
        TREE_CYPRESS,
        TREE_DENSE,
        TREE_SPARSE,

        BUSHES,

        FLOWERS,
        ;
    }

}
