package com.heavybox.jtix.z;

import com.heavybox.jtix.assets.Assets;
import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.graphics.TexturePack;
import com.heavybox.jtix.graphics.TextureRegion;
import com.heavybox.jtix.input.Input;
import com.heavybox.jtix.input.Mouse;

public class ToolStampPlants extends Tool {

    public boolean addFruits = false;
    public boolean addTrunk = true;
    public Mode mode = Mode.REGULAR;
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
        if (Input.mouse.isButtonClicked(Mouse.Button.LEFT)) {
            System.out.println("add tree");
            // TODO: improve.
            TextureRegion base = mode == Mode.REGULAR ? layer3.getRegion("assets/textures-layer-3/tree_regular_1.png") : layer3.getRegion("assets/textures-layer-3/tree-cypress_1.png");
            TextureRegion trunk = addTrunk ? layer3.getRegion("assets/textures-layer-3/tree_regular_trunk_1.png") : null;
            TextureRegion fruits = addFruits ? layer3.getRegion("assets/textures-layer-3/tree_regular_fruits.png") : null;

            CommandTokenCreate createTree = new CommandTokenCreate(
                    3,
                    x,y,deg,sclX,sclY,true,
                    base, trunk, fruits
            );
            map.addCommand(createTree);
        }
        if (Input.mouse.isButtonClicked(Mouse.Button.RIGHT)) {
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
        REGULAR,
        CYPRESS,
        DENSE,
        SPARSE,
        ;
    }

}
