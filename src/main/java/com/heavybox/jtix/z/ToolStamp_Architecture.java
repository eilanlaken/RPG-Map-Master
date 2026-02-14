package com.heavybox.jtix.z;

import com.heavybox.jtix.RPGMapMakerScene;
import com.heavybox.jtix.assets.Assets;
import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.collections.Collections;
import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.graphics.TexturePack;
import com.heavybox.jtix.graphics.TextureRegion;
import com.heavybox.jtix.input.Input;
import com.heavybox.jtix.input.Keyboard;
import com.heavybox.jtix.input.Mouse;

import java.util.Comparator;
import java.util.Set;

public class ToolStamp_Architecture extends Tool {

    public Type type = Type.TOP_VIEW_LARGE;
    public Style style = Style.HUMAN;
    public boolean singles = true;
    public boolean development = true;

    // tool overlay - development
    private final Array<Block> toolOverlayDevBlocks = new Array<>(false, 5);
    private final TexturePack atlas;
    private TextureRegion toolOverlayDevCurrentRegion;

    public ToolStamp_Architecture(final RPGMapMakerScene scene) {
        super(scene);
        atlas = Assets.get("assets/texture-packs/layer_3.yml");

        sclX = 0.5f;
        sclY = 0.5f;

        toolOverlayDevCurrentRegion = getToolOverlayCurrentRegion();
    }

    @Override
    public void update(float delta) {
        float verticalScroll = Input.mouse.getVerticalScroll();
        boolean leftButtonClicked = Input.mouse.isButtonClicked(Mouse.Button.LEFT);
        boolean rightButtonClicked = Input.mouse.isButtonClicked(Mouse.Button.RIGHT);
        boolean enterClicked = Input.keyboard.isKeyJustReleased(Keyboard.Key.ENTER); // print bundle
        boolean zJustPressed = Input.keyboard.isKeyJustPressed(Keyboard.Key.Z);
        boolean aPressed = Input.keyboard.isKeyPressed(Keyboard.Key.A);
        boolean tabJustPressed = Input.keyboard.isKeyJustReleased(Keyboard.Key.TAB);
        float mouseDy = Input.mouse.getYDelta();

        // tool settings

        // actions
        if (development) {
            if (aPressed) {
                deg += mouseDy;
            }
            if (zJustPressed) {
                sclX *= -1;
            }
            if (rightButtonClicked) {
                dev_nextRegion();
            }
            if (leftButtonClicked) {
                Block block = new Block();
                block.type = type;
                block.x = x;
                block.y = y;
                block.deg = deg;
                block.flipped = sclX < 0;
                block.region = getToolOverlayCurrentRegion();
                toolOverlayDevBlocks.add(block);
            }
            if (enterClicked) {

            }
        }
    }

    @Override
    public void renderToolOverlay(Renderer2D renderer2D, float x, float y) {

        if (development) {
            renderer2D.setColor(Color.WHITE);
            renderer2D.drawTextureRegion(toolOverlayDevCurrentRegion, x, y, deg, this.sclX, this.sclY);
            toolOverlayDevBlocks.sort(Comparator.comparingInt(o -> -(int) o.y));
            for (Block block : toolOverlayDevBlocks) {
                renderer2D.drawTextureRegion(block.region, block.x, block.y, block.deg, this.sclX, this.sclY);
            }
            return;
        }
    }

    private void dev_nextRegion() {
        type = type.getNextOfTheSamePrefix();
        toolOverlayDevCurrentRegion = getToolOverlayCurrentRegion();
    }

    private TextureRegion getToolOverlayCurrentRegion() {
        final String regionName = "assets/textures-layer-3/architecture_" + style.name().toLowerCase() + "_" + type.name().toLowerCase() + "_0.png";
        return atlas.getRegion(regionName);
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

        public Type type;
        public float x, y;
        public float deg;
        public boolean flipped;
        public TextureRegion region;

    }

    public enum Type {
        TOP_VIEW_LARGE,
        TOP_VIEW_MEDIUM,
        TOP_VIEW_SMALL,

        SIDE_VIEW_BLOCK,
        SIDE_VIEW_BRIDGE,
        SIDE_VIEW_DECORATION,
        SIDE_VIEW_TOWER,

        ISOMETRIC_VIEW_HOUSE_DIAGONAL_SHORT,
        ISOMETRIC_VIEW_HOUSE_DIAGONAL_TALL,
        ISOMETRIC_VIEW_HOUSE_HORIZONTAL_SHORT,
        ISOMETRIC_VIEW_HOUSE_HORIZONTAL_TALL,
        ISOMETRIC_VIEW_HOUSE_VERTICAL_SHORT,
        ISOMETRIC_VIEW_HOUSE_VERTICAL_TALL,
        ISOMETRIC_VIEW_HOUSE_HUT_DIAGONAL,
        ISOMETRIC_VIEW_HOUSE_HUT_VERTICAL,
        ISOMETRIC_VIEW_HOUSE_TOWER_SHORT,
        ISOMETRIC_VIEW_HOUSE_TOWER_TALL,
        ISOMETRIC_VIEW_HOUSE_WALL_BACK,
        ISOMETRIC_VIEW_HOUSE_WALL_FRONT,
        ;

        public Type getNextOfTheSamePrefix() {
            final String prefix = this.name().split("_")[0];
            Type next = Collections.enumNext(this);
            while (!next.name().startsWith(prefix))
                next = Collections.enumNext(next);
            return next;
        }
    }

    public enum Style {
        HUMAN,
        ELF,
        DWARF,
        ORC,
    }

}
