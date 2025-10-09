package com.heavybox.jtix.z;

import com.heavybox.jtix.assets.Assets;
import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.graphics.*;
import com.heavybox.jtix.input.Input;
import com.heavybox.jtix.input.Keyboard;
import com.heavybox.jtix.input.Mouse;
import com.heavybox.jtix.math.MathUtils;
import com.heavybox.jtix.math.Vector2;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ToolStampTrees extends Tool {

    public static final int TREE_DENSITY = 68; // The minimal distance between trees
    public final TexturePack layer3;
    public static final String[] FRUIT_COLORS = {"red", "orange", "green"};
    public static final String[] TREE_COLORS = {"green", "red", "yellow"};

    public Mode mode = Mode.REGULAR;
    public int batchSize = 10;
    public float addLeavesProbability = 1.0f; // TODO
    public float addTrunkProbability = 1.0f;
    public float addFruitsProbability = 0.5f;
    public Set<String> fruitColors = new HashSet<>();
    public Set<String> treeColors = new HashSet<>();

    private final Array<Vector2> positions = new Array<>(false, 10);
    private final Array<MapToken> trees = new Array<>();

    public ToolStampTrees(Map map) {
        super(map);
        sclX = 0.25f;
        sclY = 0.25f;
        layer3 = Assets.get("assets/texture-packs/layer_3.yml");
        fruitColors.add(FRUIT_COLORS[0]);
        fruitColors.add(FRUIT_COLORS[1]);
        fruitColors.add(FRUIT_COLORS[2]);

        treeColors.add(TREE_COLORS[0]); // only green
    }

    @Override
    public void update(float delta) {
        // tool settings - mode
        if (Input.mouse.getVerticalScroll() > 0 && Input.keyboard.isKeyPressed(Keyboard.Key.LEFT_CONTROL)) {
            mode = Mode.values()[(mode.ordinal() + 1) % Mode.values().length]; // next
            System.out.println(mode);
            return;
        } else if (Input.mouse.getVerticalScroll() < 0 && Input.keyboard.isKeyPressed(Keyboard.Key.LEFT_CONTROL)) {
            mode = Mode.values()[(mode.ordinal() - 1 + Mode.values().length) % Mode.values().length];
            System.out.println(mode);
            return;
        }
        // tool settings - tree colors
        boolean treeColorsModified = Input.keyboard.isKeyJustPressed(Keyboard.Key.Q)
                || Input.keyboard.isKeyJustPressed(Keyboard.Key.W)
                || Input.keyboard.isKeyJustPressed(Keyboard.Key.E);
        if (treeColorsModified) {
            int index = 0;
            if (Input.keyboard.isKeyJustPressed(Keyboard.Key.Q)) index = 0;
            else if (Input.keyboard.isKeyJustPressed(Keyboard.Key.W)) index = 1;
            else if (Input.keyboard.isKeyJustPressed(Keyboard.Key.E)) index = 2;
            String color_q = TREE_COLORS[index];
            if (treeColors.contains(color_q) && treeColors.size() > 1) treeColors.remove(color_q);
            else treeColors.add(color_q);
            System.out.println("Tree colors: " + String.join(", ", treeColors));
            return;
        }

        // tool settings - fruits color
        boolean fruitColorsModified = Input.keyboard.isKeyJustPressed(Keyboard.Key.A)
                || Input.keyboard.isKeyJustPressed(Keyboard.Key.S)
                || Input.keyboard.isKeyJustPressed(Keyboard.Key.D);
        if (fruitColorsModified) {
            int index = 0;
            if (Input.keyboard.isKeyJustPressed(Keyboard.Key.A)) index = 0;
            else if (Input.keyboard.isKeyJustPressed(Keyboard.Key.S)) index = 1;
            else if (Input.keyboard.isKeyJustPressed(Keyboard.Key.D)) index = 2;
            String color_q = FRUIT_COLORS[index];
            if (fruitColors.contains(color_q) && fruitColors.size() > 1) fruitColors.remove(color_q);
            else fruitColors.add(color_q);
            System.out.println("Fruit colors: " + String.join(", ", fruitColors));
            return;
        }

        // tool settings - scale
        float deltaScale = Input.mouse.isButtonPressed(Mouse.Button.RIGHT) && Input.keyboard.isKeyPressed(Keyboard.Key.Z) ? -Input.mouse.getYDelta() / (Graphics.getWindowHeight() * 0.3f) : 0;
        sclX += deltaScale;
        sclY += deltaScale;
        sclX = MathUtils.clampFloat(sclX, 0.25f, 4);
        sclY = MathUtils.clampFloat(sclY, 0.25f, 4);
        if (deltaScale != 0) {
            System.out.println("Scale: " + sclX);
            return;
        }

        // tool settings - trunk probability
        float deltaAddTrunkProbability = Input.mouse.isButtonPressed(Mouse.Button.RIGHT) && Input.keyboard.isKeyPressed(Keyboard.Key.X) ? -Input.mouse.getYDelta() / (Graphics.getWindowHeight() * 0.3f) : 0;
        addTrunkProbability += deltaAddTrunkProbability;
        addTrunkProbability = MathUtils.clampFloat(addTrunkProbability, 0, 1);
        if (deltaAddTrunkProbability != 0) {
            System.out.println("Add Trunk P: " + addTrunkProbability);
            return;
        }

        // tool settings - fruits probability
        float deltaAddFruitProbability = Input.mouse.isButtonPressed(Mouse.Button.RIGHT) && Input.keyboard.isKeyPressed(Keyboard.Key.C) ? -Input.mouse.getYDelta() / (Graphics.getWindowHeight() * 0.3f) : 0;
        addFruitsProbability += deltaAddFruitProbability;
        addFruitsProbability = MathUtils.clampFloat(addFruitsProbability, 0, 1);
        if (deltaAddFruitProbability != 0) {
            System.out.println("Add Fruits P: " + addFruitsProbability);
            return;
        }

        // tool settings - batch size
        int batchSizeDelta = Input.keyboard.isKeyPressed(Keyboard.Key.LEFT_SHIFT) ? (int) Input.mouse.getVerticalScroll() : 0;
        batchSize += batchSizeDelta;
        batchSize = MathUtils.clampInt(batchSize, 1, 20);
        if (batchSizeDelta != 0) {
            System.out.println("Batch size: " + batchSize);
            return;
        }

        boolean leftPressedAndMoved = Input.mouse.isButtonPressed(Mouse.Button.LEFT) && Input.mouse.moved();
        if (Input.mouse.isButtonClicked(Mouse.Button.LEFT) || leftPressedAndMoved) {

            setPositions();

            if (mode == Mode.REGULAR) {
                for (Vector2 position : positions) {
                    float x = position.x;
                    float y = position.y;
                    List<String> treeColorsList = new ArrayList<>(treeColors);
                    String treeColor = treeColorsList.get(MathUtils.randomUniformInt(0, treeColorsList.size()));
                    TextureRegion base = layer3.getRegion("assets/textures-layer-3/tree_" + mode.name().toLowerCase() + "_" + treeColor + "_" + MathUtils.randomUniformInt(0, 6) + ".png");
                    boolean addTrunk = MathUtils.randomUniformFloat(0, 1) < addTrunkProbability;
                    TextureRegion trunk = addTrunk ? layer3.getRegion("assets/textures-layer-3/tree_" + mode.name().toLowerCase() + "_trunk_" + MathUtils.randomUniformInt(1, 6) + ".png") : null;
                    boolean addFruits = MathUtils.randomUniformFloat(0, 1) < addFruitsProbability;
                    List<String> fruitColorsList = new ArrayList<>(fruitColors);
                    String fruitColor = fruitColorsList.get(MathUtils.randomUniformInt(0, fruitColorsList.size()));
                    TextureRegion fruits = addFruits ? layer3.getRegion("assets/textures-layer-3/tree_" + mode.name().toLowerCase() + "_fruits_" + fruitColor + ".png") : null;
                    CommandTokenCreate createPlant = new CommandTokenCreate(
                            3,
                            x, y, deg, sclX, sclY, true,
                            base, trunk, fruits
                    );
                    createPlant.type = MapToken.Type.TREE;
                    map.addCommand(createPlant);
                }
                return;
            }

            if (mode == Mode.CYPRESS) {
                for (Vector2 position : positions) {
                    float x = position.x;
                    float y = position.y;
                    TextureRegion base = layer3.getRegion("assets/textures-layer-3/tree_" + mode.name().toLowerCase() + "_" + MathUtils.randomUniformInt(0, 6) + ".png");
                    boolean addTrunk = MathUtils.randomUniformFloat(0, 1) < addTrunkProbability;
                    TextureRegion trunk = addTrunk ? layer3.getRegion("assets/textures-layer-3/tree_" + mode.name().toLowerCase() + "_trunk_" + MathUtils.randomUniformInt(1, 6) + ".png") : null;
                    boolean addFruits = MathUtils.randomUniformFloat(0, 1) < addFruitsProbability;
                    List<String> fruitColorsList = new ArrayList<>(fruitColors);
                    String fruitColor = fruitColorsList.get(MathUtils.randomUniformInt(0, fruitColorsList.size()));
                    TextureRegion fruits = addFruits ? layer3.getRegion("assets/textures-layer-3/tree_" + mode.name().toLowerCase() + "_fruits_" + fruitColor + ".png") : null;
                    CommandTokenCreate createPlant = new CommandTokenCreate(
                            3,
                            x, y, deg, sclX, sclY, true,
                            base, trunk, fruits
                    );
                    createPlant.type = MapToken.Type.TREE;
                    map.addCommand(createPlant);
                }
                return;
            }

            if (mode == Mode.DENSE || mode == Mode.SPARSE) {
                for (Vector2 position : positions) {
                    float x = position.x;
                    float y = position.y;
                    TextureRegion base = layer3.getRegion("assets/textures-layer-3/tree_" + mode.name().toLowerCase() + "_" + MathUtils.randomUniformInt(0, 6) + ".png");
                    boolean addFruits = MathUtils.randomUniformFloat(0, 1) < addFruitsProbability;
                    List<String> fruitColorsList = new ArrayList<>(fruitColors);
                    String fruitColor = fruitColorsList.get(MathUtils.randomUniformInt(0, fruitColorsList.size()));
                    TextureRegion fruits = addFruits ? layer3.getRegion("assets/textures-layer-3/tree_" + mode.name().toLowerCase() + "_fruits_" + fruitColor + ".png") : null;
                    CommandTokenCreate createPlant = new CommandTokenCreate(
                            3,
                            x, y, deg, 2 * sclX, 2 * sclY, true,
                            base, fruits
                    );
                    createPlant.type = MapToken.Type.TREE;
                    map.addCommand(createPlant);
                }
                return;
            }

            if (mode == Mode.BUSH) {
                for (Vector2 position : positions) {
                    float x = position.x;
                    float y = position.y;
                    TextureRegion base = layer3.getRegion("assets/textures-layer-3/tree_bush_" + MathUtils.randomUniformInt(0, 6) + ".png");
                    boolean addFruits = MathUtils.randomUniformFloat(0, 1) < addFruitsProbability;
                    List<String> fruitColorsList = new ArrayList<>(fruitColors);
                    String fruitColor = fruitColorsList.get(MathUtils.randomUniformInt(0, fruitColorsList.size()));
                    TextureRegion fruits = addFruits ? layer3.getRegion("assets/textures-layer-3/tree_" + mode.name().toLowerCase() + "_fruits_" + fruitColor + ".png") : null;
                    CommandTokenCreate createPlant = new CommandTokenCreate(
                            3,
                            x, y, deg, sclX, sclY, true, base, fruits
                    );
                    createPlant.type = MapToken.Type.TREE;
                    map.addCommand(createPlant);
                    return;
                }
            }
        }
    }



    // TODO: consider the scale.
    private void setPositions() {
        positions.clear();
        final float spacing = TREE_DENSITY * sclX;

        float r = (float) Math.sqrt(batchSize / (2 * MathUtils.PI)) * spacing; // circle radius
        for (int i = 0; i < batchSize; i++) { // scatter inside circle
            Vector2 position = new Vector2(x + MathUtils.randomUniformFloat(-r,r), y + MathUtils.randomUniformFloat(-r,r));
            positions.add(position);
        }

        Array<Vector2> filtered = new Array<>();
        for (Vector2 position : positions) { // first filter against self
            boolean add = true;
            for (Vector2 p : filtered) {
                add &= position.dst(p) >= spacing;
            }
            if (add) filtered.add(position);
        }

        positions.clear();
        map.getAllTokens(MapToken.Type.TREE, trees);
        for (Vector2 position : filtered) { // second filter against all trees tokens previously added
            boolean add = true;
            for (MapToken tree : trees) {
                Vector2 p = new Vector2(tree.x, tree.y);
                add &= position.dst(p) >= spacing;
            }
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
        REGULAR,
        CYPRESS,
        DENSE,
        SPARSE,
        BUSH,
        ;
    }

}
