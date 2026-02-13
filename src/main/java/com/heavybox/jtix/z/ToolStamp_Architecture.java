package com.heavybox.jtix.z;

import com.heavybox.jtix.RPGMapMakerScene;
import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.graphics.TextureRegion;

import java.util.Comparator;

public class ToolStamp_Architecture extends Tool {

    public StructureType structureType = StructureType.FLAT_LARGE;
    public Grouping grouping = Grouping.DEVELOPMENT;
    public Style style = Style.HUMAN;

    // tool overlay
    private final Array<Block> toolOverlayPreviewBlocks = new Array<>(false, 5);
    private TextureRegion toolOverlayCurrentRegion;

    public ToolStamp_Architecture(final RPGMapMakerScene scene) {
        super(scene);

        sclX = 0.5f;
        sclY = 0.5f;
    }

    @Override
    public void update(float delta) {
        // tool settings

        // actions
    }

    @Override
    public void renderToolOverlay(Renderer2D renderer2D, float x, float y) {

        if (grouping == Grouping.DEVELOPMENT) {
            renderer2D.setColor(Color.WHITE);
            renderer2D.drawTextureRegion(toolOverlayCurrentRegion, x, y, 0, this.sclX, this.sclY);
            toolOverlayPreviewBlocks.sort(Comparator.comparingInt(o -> -(int) o.y));
            for (Block block : toolOverlayPreviewBlocks) {
                renderer2D.drawTextureRegion(block.region, block.x, block.y, 0, this.sclX, this.sclY);
            }
            return;
        }
    }

    private TextureRegion setToolOverlayCurrentRegion() {
        final String prefix = "assets/textures-layer-3/architecture_";
        final String suffix = ".png";

        return null;
    }

    @Override
    public void activate() {

    }

    @Override
    public void deactivate() {

    }

    @Override
    public String getName() {
        return "Architecture Tool";
    }

    public static class Block {

        public float x, y;
        public boolean flipped;
        public TextureRegion region;

    }

    public enum StructureType {

        CASTLE_BLOCK,
        CASTLE_DECORATION,
        CASTLE_TOWER,

        FLAT_LARGE,
        FLAT_MEDIUM,
        FLAT_SMALL,

        BRIDGE,
        HOUSE_DIAGONAL_SHORT,
        HOUSE_DIAGONAL_TALL,
        HOUSE_HORIZONTAL_SHORT,
        HOUSE_HORIZONTAL_TALL,
        HOUSE_VERTICAL_SHORT,
        HOUSE_VERTICAL_TALL,
        HUT_DIAGONAL,
        HUT_VERTICAL,
        TOWER_SHORT,
        TOWER_TALL,
        WALL_BACK,
        WALL_FRONT,
    }

    public enum Grouping {
        SINGLES,
        BUNDLES,
        DEVELOPMENT,
    }

    public enum Style {
        HUMAN,
        ELF,
        DWARF,
        ORC,
    }

}
