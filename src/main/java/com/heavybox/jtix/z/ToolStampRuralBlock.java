package com.heavybox.jtix.z;

import com.heavybox.jtix.assets.Assets;
import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.graphics.TexturePack;
import com.heavybox.jtix.graphics.TextureRegion;
import com.heavybox.jtix.input.Input;
import com.heavybox.jtix.input.Mouse;

public class ToolStampRuralBlock extends Tool {

    public final TexturePack layer3;
    private final Array<TextureRegion> regions = new Array<>(10);
    private final Array<String> hutsRegionNames = new Array<>(true,10);
    private final Array<String> propsRegionNames = new Array<>(true,10);

    private Mode mode = Mode.SINGLES_HUTS;

    // singles huts
    private int singlesHutsIndex = 0;

    // singles props

    // combos

    public ToolStampRuralBlock(Map map) {
        super(map);
        layer3 = Assets.get("assets/texture-packs/layer_3.yml");
        sclX = 0.5f;
        sclY = 0.5f;
        for (String name : layer3.namedRegions.keySet()) {
            if (name.startsWith("assets/textures-layer-3/hut")) hutsRegionNames.add(name);
            if (name.startsWith("assets/textures-layer-3/prop")) propsRegionNames.add(name);
        }
        setRegions();
    }

    private void setRegions() {
        regions.clear();
        if (mode == Mode.SINGLES_HUTS) {
            regions.add(layer3.getRegion(hutsRegionNames.get(singlesHutsIndex)));
        }
    }

    @Override
    public void update(float delta) {
        if (mode == Mode.SINGLES_HUTS) {
            if (Input.mouse.isButtonClicked(Mouse.Button.RIGHT)) {
                singlesHutsIndex++;
                singlesHutsIndex %= hutsRegionNames.size;
                setRegions();
            }
        } else if (mode == Mode.SINGLES_PROPS) {

        } else if (mode == Mode.COMBOS) {

        }
    }

    @Override
    public void renderToolOverlay(Renderer2D renderer2D, float x, float y) {
        renderer2D.setColor(Color.WHITE);
        for (TextureRegion region : regions) {
            renderer2D.drawTextureRegion(region, x, y, this.deg, this.sclX, this.sclY);
        }
    }

    @Override
    public void activate() {
        setRegions();
    }

    @Override
    public void deactivate() {
        regions.clear();
    }

    private enum Hut {
        DIAGONAL_BIG_LEFT,
        DIAGONAL_BIG_RIGHT,
        DIAGONAL_SMALL_LEFT,
        DIAGONAL_SMALL_RIGHT,
        HORIZONTAL_BIG,
        HORIZONTAL_SMALL,
        VERTICAL_SMALL,
        VERTICAL_MEDIUM,
        VERTICAL_LARGE,
        VERTICAL_GIANT,
        ;
    }

    private enum Mode {
        SINGLES_HUTS,
        SINGLES_PROPS,
        COMBOS,
        ;
    }

}
