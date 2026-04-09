package com.heavybox.jtix.z.tools_new;

import com.heavybox.jtix.RPGMapMakerScene;
import com.heavybox.jtix.assets.Assets;
import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.collections.ArrayChar;
import com.heavybox.jtix.collections.Collections;
import com.heavybox.jtix.graphics.*;
import com.heavybox.jtix.input.Input;
import com.heavybox.jtix.input.Keyboard;
import com.heavybox.jtix.input.Mouse;
import com.heavybox.jtix.math.Vector2;
import com.heavybox.jtix.z.Utils;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;

public class Tool_5_DEV_Architecture extends Tool_new {

    public Type type = Type.SIDE_VIEW_BLOCK_BIG;


    private final TexturePack atlas;
    private TextureRegion toolOverlayDevCurrentRegion;
    private final Array<Block> toolOverlayDevBlocks = new Array<>(false, 5);


    public Tool_5_DEV_Architecture(RPGMapMakerScene scene) {
        super(scene);
        atlas = Assets.get("assets/texture-packs/layer_3.yml");

        toolOverlayDevCurrentRegion = getToolOverlayCurrentRegion();
    }

    private TextureRegion getToolOverlayCurrentRegion() {
        final String regionName = "assets/textures-layer-3/architecture_dwarf_" + type.name().toLowerCase() + "_0.png";
        return atlas.getRegion(regionName);
    }

    @Override
    void onChangeParameters() {

    }

    @Override
    public void update(float delta) {
        boolean leftButtonClicked = Input.mouse.isButtonClicked(Mouse.Button.LEFT);
        boolean enterClicked = Input.keyboard.isKeyJustReleased(Keyboard.Key.ENTER); // print bundle
        boolean zJustPressed = Input.keyboard.isKeyJustPressed(Keyboard.Key.Z);
        boolean xJustPressed = Input.keyboard.isKeyJustPressed(Keyboard.Key.X);
        boolean rJustPressed = Input.keyboard.isKeyJustPressed(Keyboard.Key.R);
        boolean aPressed = Input.keyboard.isKeyPressed(Keyboard.Key.A);
        boolean sPressed = Input.keyboard.isKeyPressed(Keyboard.Key.S);
        boolean spaceJustPressed = Input.keyboard.isKeyJustPressed(Keyboard.Key.SPACE);
        boolean rightClick = Input.mouse.isButtonClicked(Mouse.Button.RIGHT);
        boolean plusPressed = Input.keyboard.isKeyPressed(Keyboard.Key.EQUAL);
        boolean minusPressed = Input.keyboard.isKeyPressed(Keyboard.Key.MINUS);

        if (rJustPressed) { // reset
            deg = 0;
            sclX = 1;
            sclY = 1;
            return;
        }

        if (aPressed) {
            deg += 30 * Graphics.getDeltaTime();
            deg %= 360;
        }
        if (sPressed) {
            deg -= 30 * Graphics.getDeltaTime();
            deg %= 360;
        }
        if (spaceJustPressed) {
            deg += 90;
            deg %= 360;
        }
        if (rightClick) {
            sclX *= -1;
        }
        if (plusPressed) {
            sclX *= 1.00f + 0.8f * Graphics.getDeltaTime();
            sclY *= 1.00f + 0.8f * Graphics.getDeltaTime();
            return;
        }
        if (minusPressed) {
            sclX *= 1.00f - 0.8f * Graphics.getDeltaTime();
            sclY *= 1.00f - 0.8f * Graphics.getDeltaTime();
            return;
        }

        if (zJustPressed) {
            type = type.getNextOfTheSamePrefix();
            toolOverlayDevCurrentRegion = getToolOverlayCurrentRegion();
        } else if (xJustPressed) {
            type = type.getPrevOfTheSamePrefix();
            toolOverlayDevCurrentRegion = getToolOverlayCurrentRegion();
        }
        if (leftButtonClicked) {
            Block block = new Block();
            block.type = type;
            block.x = x;
            block.y = y;
            block.deg = deg;
            block.sclX = this.sclX;
            block.sclY = this.sclY;
            block.region = getToolOverlayCurrentRegion();
            toolOverlayDevBlocks.add(block);
        }
        if (enterClicked && !toolOverlayDevBlocks.isEmpty()) {
            Array<Block> blocks = new Array<>();
            blocks.addAll(this.toolOverlayDevBlocks);
            blocks.sort(Comparator.comparingInt(o -> -(int) o.y));
            // calculate center of mass
            Vector2 cm = new Vector2();
            for (Block block : blocks) {
                cm.add(block.x, block.y);
            }
            cm.scl(1f / blocks.size);
            final String prefix = blocks.first().type.name().split("_")[0];
            System.out.println("<bundle prefix=\"" + prefix + "\">");
            for (Block block : blocks) {
                System.out.println("\t" +
                        "<block type=\"" + block.type.name() +
                        "\" sclX=\"" + block.sclX +
                        "\" sclY=\"" + block.sclY +
                        "\" x=\"" + (block.x - cm.x) +
                        "\" y=\"" + (block.y - cm.y) +
                        "\" deg=\"" + block.deg +
                        "\"/>"
                );            }
            System.out.println("</bundle>");
        }

    }

    @Override
    public void renderToolOverlay(Renderer2D renderer2D, float x, float y) {
        renderer2D.setColor(Color.WHITE);
        renderer2D.drawTextureRegion(toolOverlayDevCurrentRegion, x, y, deg, this.sclX, this.sclY);
        toolOverlayDevBlocks.sort(Comparator.comparingInt(o -> -(int) o.y));
        for (Block block : toolOverlayDevBlocks) {
            renderer2D.drawTextureRegion(block.region, block.x, block.y, block.deg, block.sclX, block.sclY);
        }
    }

    @Override
    public void renderToolText(Renderer2D renderer2D, float x, float y) {

    }

    @Override
    String getHelperText() {
        return "";
    }

    @Override
    public void activate() {

    }

    @Override
    public void deactivate() {

    }

    public static class Bundle {

        public Block[] blocks;

    }

    public static class Block {

        public Type type;
        public float x, y;
        public float sclX;
        public float sclY;
        public float deg;
        public boolean flipped;
        public TextureRegion region;

    }

    public enum Type {
        TOP_VIEW_HOUSE_LARGE,
        TOP_VIEW_HOUSE_MEDIUM,
        TOP_VIEW_HOUSE_SMALL,
        TOP_VIEW_TOWER,
        TOP_VIEW_WALL,

        SIDE_VIEW_BLOCK_SMALL,
        SIDE_VIEW_BLOCK_BIG,
        SIDE_VIEW_BRIDGE,
        SIDE_VIEW_TOWER_SMALL,
        SIDE_VIEW_TOWER_BIG,

        ISOMETRIC_VIEW_HOUSE_DIAGONAL_SHORT,
        ISOMETRIC_VIEW_HOUSE_DIAGONAL_TALL,
        ISOMETRIC_VIEW_HOUSE_HORIZONTAL_SHORT,
        ISOMETRIC_VIEW_HOUSE_HORIZONTAL_TALL,
        ISOMETRIC_VIEW_HOUSE_VERTICAL_SHORT,
        ISOMETRIC_VIEW_HOUSE_VERTICAL_TALL,
        ISOMETRIC_VIEW_HUT_DIAGONAL,
        ISOMETRIC_VIEW_HUT_VERTICAL,
        ISOMETRIC_VIEW_TOWER_SHORT,
        ISOMETRIC_VIEW_TOWER_TALL,
        ISOMETRIC_VIEW_WALL_BACK,
        ISOMETRIC_VIEW_WALL_FRONT,
        ;

        public Type getNextView() {
            final String prefix = this.name().split("_")[0];

            Type next = Collections.enumNext(this);
            while (next.name().startsWith(prefix))
                next = Collections.enumNext(next);
            return next;
        }

        public Type getNextOfTheSamePrefix() {
            final String prefix = this.name().split("_")[0];

            Type next = Collections.enumNext(this);
            while (!next.name().startsWith(prefix))
                next = Collections.enumNext(next);
            return next;
        }

        public Type getPrevOfTheSamePrefix() {
            final String prefix = this.name().split("_")[0];
            Type prev = Collections.enumPrev(this);
            while (!prev.name().startsWith(prefix))
                prev = Collections.enumPrev(prev);
            return prev;
        }

        public String getView() {
            if (name().startsWith("TOP")) return "TOP";
            if (name().startsWith("SIDE")) return "SIDE";
            if (name().startsWith("ISOMETRIC")) return "ISOMETRIC";
            return null;
        }

    }

    // later

    @Override
    public int getLayer() {
        return 0;
    }

    @Override
    public boolean active() {
        return active;
    }

    @Override
    public boolean mouseButtonsDown(int mouseX, int mouseY, @NotNull Array<Mouse.Button> buttons) {
        return false;
    }

    @Override
    public boolean mouseButtonsUp(int mouseX, int mouseY, @NotNull Array<Mouse.Button> buttons) {
        return false;
    }

    @Override
    public boolean mouseMoved(int mouseX, int mouseY, int deltaMouseX, int deltaMouseY) {
        return false;
    }

    @Override
    public boolean mouseScrolled(float scrollX, float scrollY) {
        return false;
    }

    @Override
    public boolean mouseDragged(int mouseX, int mouseY, int deltaMouseX, int deltaMouseY, @NotNull Array<Mouse.Button> buttons) {
        return false;
    }

    @Override
    public boolean keyboardKeysJustPressed(@NotNull Array<Keyboard.Key> keys) {
        return false;
    }

    @Override
    public boolean keyboardKeysJustReleased(@NotNull Array<Keyboard.Key> keys) {
        return false;
    }

    @Override
    public boolean keyboardCodepointsTyped(@NotNull ArrayChar codepoints) {
        return false;
    }

}
