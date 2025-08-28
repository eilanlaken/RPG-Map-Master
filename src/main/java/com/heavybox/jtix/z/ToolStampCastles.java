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

import java.util.Comparator;

public class ToolStampCastles extends Tool {

    public final TexturePack layer3;

    public Mode mode = Mode.SINGLE;

    public Array<CastleBlock> singleCastleBlocks = new Array<>();
    public CastleBlockType type = CastleBlockType.values()[0];
    public int currentSingleIndex = 0;
    public TextureRegion currentRegion;

    public ToolStampCastles(Map map) {
        super(map);
        layer3 = Assets.get("assets/texture-packs/layer_3.yml");
        sclX = 0.5f;
        sclY = 0.5f;
        setRegion();
    }

    @Override
    public void update(float delta) {
        if (mode == Mode.SINGLE) {
            if (Input.keyboard.isKeyJustPressed(Keyboard.Key.Q)) {
                type = CastleBlockType.values()[(type.ordinal() + 1) % CastleBlockType.values().length]; // next
                setRegion();
            } else if (Input.keyboard.isKeyJustPressed(Keyboard.Key.A)) {
                type = CastleBlockType.values()[(type.ordinal() - 1 + CastleBlockType.values().length) % CastleBlockType.values().length];
                setRegion();
            } else if (Input.mouse.isButtonClicked(Mouse.Button.RIGHT)) {
                currentSingleIndex++;
                setRegion();
            } else if (Input.mouse.isButtonClicked(Mouse.Button.LEFT)) {
                CastleBlock castleBlock = new CastleBlock();
                castleBlock.x = x;
                castleBlock.y = y;
                castleBlock.type = type;
                castleBlock.blockIndex = currentSingleIndex;
                singleCastleBlocks.add(castleBlock);
            }
        } else if (mode == Mode.COMBINATION) {

        }
    }

    private void setRegion() {
        currentSingleIndex %= type.amount;
        System.out.println(currentSingleIndex);
        currentRegion = layer3.getRegion("assets/textures-layer-3/" + type.name().toLowerCase() + "_" + currentSingleIndex + ".png");
    }

    @Override
    public void renderToolOverlay(Renderer2D renderer2D, float x, float y, float deg, float sclX, float sclY) {
        if (mode == Mode.SINGLE) {
            renderer2D.setColor(Color.WHITE);
            renderer2D.drawTextureRegion(currentRegion, x, y, 0, this.sclX, this.sclY);
            singleCastleBlocks.sort(Comparator.comparingInt(o -> -(int) o.y));
            for (CastleBlock block : singleCastleBlocks) {
                TextureRegion region = layer3.getRegion("assets/textures-layer-3/" + block.type.name().toLowerCase() + "_" + block.blockIndex + ".png");
                renderer2D.drawTextureRegion(region, block.x, block.y, 0, this.sclX, this.sclY);
            }
        } else if (mode == Mode.COMBINATION) {

        }
    }

    @Override
    public void activate() {
        singleCastleBlocks.clear();
        setRegion();
    }

    @Override
    public void deactivate() {
        singleCastleBlocks.clear();
    }

    public class CastleBlock {

        float x, y;
        CastleBlockType type;
        int blockIndex;

    }

    public enum CastleBlockType {

        CASTLE_BUILDING_DIAGONAL_SHORT_LEFT(6),
        CASTLE_BUILDING_DIAGONAL_SHORT_RIGHT(6),
        CASTLE_BUILDING_DIAGONAL_TALL_LEFT(6),
        CASTLE_BUILDING_DIAGONAL_TALL_RIGHT(6),

        CASTLE_BUILDING_NARROW_SHORT(7),
        CASTLE_BUILDING_NARROW_TALL(7),
        CASTLE_BUILDING_WIDE_SHORT(10),
        CASTLE_BUILDING_WIDE_TALL(10),

        CASTLE_TOWER_SHORT(12),
        CASTLE_TOWER_TALL(12),

        CASTLE_WALL_BACK_LEFT(5),
        CASTLE_WALL_BACK_RIGHT(5),
        CASTLE_WALL_FRONT_LEFT(5),
        CASTLE_WALL_FRONT_RIGHT(5),
        ;

        public final int amount;

        CastleBlockType(final int amount) {
            this.amount = amount;
        }

    }


    public enum Mode {
        SINGLE,
        COMBINATION,
        ;
    }

}
