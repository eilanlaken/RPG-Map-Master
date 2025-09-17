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
import com.heavybox.jtix.math.Vector3;

public class ToolStampPlants extends Tool {

    public static final int TREE_DENSITY = 17; // The minimal distance between trees
    public float addFruitsProbability = 0.5f;
    public boolean addTrunk = true;
    public Mode mode = Mode.TREE_REGULAR;
    public int batchSize = 10;
    public TexturePack layer3;
    public Color fruitsColor = Color.RED;

    private final Array<Vector2> positions = new Array<>(false, 10);
    private final Array<MapToken> trees = new Array<>();

    public ToolStampPlants(Map map) {
        super(map);
        sclX = 0.25f;
        sclY = 0.25f;
        layer3 = Assets.get("assets/texture-packs/layer_3.yml");
    }

    @Override
    public void update(float delta) {
        boolean leftPressedAndMoved = Input.mouse.isButtonPressed(Mouse.Button.LEFT) && Input.mouse.moved();

        if (Input.keyboard.isKeyJustPressed(Keyboard.Key.TAB)) {
            mode = Mode.values()[(mode.ordinal() + 1) % Mode.values().length];
            System.out.println(mode);
        } else if (Input.mouse.isButtonClicked(Mouse.Button.LEFT) || leftPressedAndMoved) {
            setPositions();
            if (mode == Mode.TREE_REGULAR || mode == Mode.TREE_CYPRESS) {
                for (Vector2 position : positions) {
                    float x = position.x;
                    float y = position.y;
                    TextureRegion base =
                            mode == Mode.TREE_REGULAR ?
                                    layer3.getRegion("assets/textures-layer-3/tree_regular_" + MathUtils.randomUniformInt(1, 7) + ".png")
                                    :
                                    layer3.getRegion("assets/textures-layer-3/tree_cypress_" + MathUtils.randomUniformInt(1, 7) + ".png");
                    TextureRegion trunk = addTrunk ? layer3.getRegion("assets/textures-layer-3/tree_regular_trunk_" + MathUtils.randomUniformInt(1, 11) + ".png") : null;
                    boolean addFruits = MathUtils.randomUniformFloat(0, 1) < addFruitsProbability;
                    TextureRegion fruits = addFruits ? // if addFruits, add regular or cypress fruits. Else, ignore.
                            (mode == Mode.TREE_REGULAR ? layer3.getRegion("assets/textures-layer-3/tree_regular_fruits.png")
                                    : layer3.getRegion("assets/textures-layer-3/tree_cypress_fruits.png")) : null;
                    CommandTokenCreate createPlant = new CommandTokenCreate(
                            3,
                            x, y, deg, sclX, sclY, true,
                            base, trunk, fruits
                    );
                    createPlant.type = MapToken.Type.TREE;
                    map.addCommand(createPlant);
                }
            } else if (mode == Mode.TREE_DENSE || mode == Mode.TREE_SPARSE) {
                TextureRegion base = mode == Mode.TREE_DENSE ?
                        layer3.getRegion("assets/textures-layer-3/tree_dense_" + MathUtils.randomUniformInt(1,7) + ".png")
                        :
                        layer3.getRegion("assets/textures-layer-3/tree_sparse_" + MathUtils.randomUniformInt(1, 7) + ".png");
                boolean addFruits = MathUtils.randomUniformFloat(0, 1) < addFruitsProbability;
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
                TextureRegion base = layer3.getRegion("assets/textures-layer-3/tree_bush_" + MathUtils.randomUniformInt(1,7) + ".png");
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

    private void setPositions() {
        positions.clear();
        float r = (float) Math.sqrt(batchSize / (2 * MathUtils.PI)) * TREE_DENSITY;
        for (int i = 0; i < batchSize; i++) {
            Vector2 position = new Vector2(x + MathUtils.randomUniformFloat(-r,r), y + MathUtils.randomUniformFloat(-r,r));
            positions.add(position);
        }
        Array<Vector2> filtered = new Array<>();
        for (Vector2 position : positions) {
            boolean add = true;
            for (Vector2 p : filtered) {
                add &= position.dst(p) >= TREE_DENSITY;
            }
            if (add) filtered.add(position);
        }
        positions.clear();
        map.getAllTokens(MapToken.Type.TREE, trees);
        for (Vector2 position : filtered) {
            boolean add = true;
            for (MapToken tree : trees) {
                Vector2 p = new Vector2(tree.x, tree.y);
                add &= position.dst(p) >= TREE_DENSITY;
            }
            if (!add) System.out.println("heee");
            if (add) positions.add(position);
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
