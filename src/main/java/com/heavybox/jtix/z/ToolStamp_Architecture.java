package com.heavybox.jtix.z;

import com.heavybox.jtix.RPGMapMakerScene;
import com.heavybox.jtix.assets.Assets;
import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.collections.Collections;
import com.heavybox.jtix.graphics.*;
import com.heavybox.jtix.input.Input;
import com.heavybox.jtix.input.Keyboard;
import com.heavybox.jtix.input.Mouse;
import com.heavybox.jtix.math.MathUtils;
import com.heavybox.jtix.math.Vector2;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

public class ToolStamp_Architecture extends Tool {

    private static final Array<Bundle> BUNDLES_TOP_VIEW       = new Array<>(true, 10);
    private static final Array<Bundle> BUNDLES_SIDE_VIEW      = new Array<>(true, 10);
    private static final Array<Bundle> BUNDLES_ISOMETRIC_VIEW = new Array<>(true, 10);

    /* bundles initialization block */
    static {
        try {
            File file = new File("assets/data/architecture.xml");
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(file);
            doc.getDocumentElement().normalize();

            NodeList bundlesList = doc.getElementsByTagName("bundle");

            List<Element> bundles_top_view = new ArrayList<>();
            List<Element> bundles_side_view = new ArrayList<>();
            List<Element> bundles_isometric_view = new ArrayList<>();
            for (int i = 0; i < bundlesList.getLength(); i++) {
                Element e = (Element) bundlesList.item(i);
                if ("TOP".equals(e.getAttribute("prefix"))) {
                    bundles_top_view.add(e);
                }
                if ("SIDE".equals(e.getAttribute("prefix"))) {
                    bundles_side_view.add(e);
                }
                if ("ISOMETRIC".equals(e.getAttribute("prefix"))) {
                    bundles_isometric_view.add(e);
                }
            }

            for (Element bundleElement : bundles_top_view) {
                NodeList blocks = bundleElement.getElementsByTagName("block");
                Bundle bundle = new Bundle();
                bundle.blocks = new Block[blocks.getLength()];
                for (int i = 0; i < blocks.getLength(); i++) {
                    Element blockElement = (Element) blocks.item(i);
                    float x = Float.parseFloat(blockElement.getAttribute("x"));
                    float y = Float.parseFloat(blockElement.getAttribute("y"));
                    float deg = Float.parseFloat(blockElement.getAttribute("deg"));
                    boolean flipped = Boolean.parseBoolean(blockElement.getAttribute("flipped"));
                    Type type = Type.valueOf(blockElement.getAttribute("type"));
                    bundle.blocks[i] = new Block();
                    bundle.blocks[i].x = x;
                    bundle.blocks[i].y = y;
                    bundle.blocks[i].deg = deg;
                    bundle.blocks[i].flipped = flipped;
                    bundle.blocks[i].type = type;
                }

                BUNDLES_TOP_VIEW.add(bundle);
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public Type type = Type.TOP_VIEW_LARGE;
    public Style style = Style.HUMAN;
    public boolean singles = false;
    public boolean development = false;

    // tool overlay - development
    private final Array<Block> toolOverlayDevBlocks = new Array<>(false, 5);
    private final TexturePack atlas;
    private TextureRegion toolOverlayDevCurrentRegion;

    // tool overlay - non-development
    private int bundleTopViewIndex = MathUtils.randomUniformInt(0, BUNDLES_TOP_VIEW.size);

    public ToolStamp_Architecture(final RPGMapMakerScene scene) {
        super(scene);
        atlas = Assets.get("assets/texture-packs/layer_3.yml");

        sclX = 1f;
        sclY = 1f;

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
        boolean qPressed = Input.keyboard.isKeyPressed(Keyboard.Key.Q);
        boolean sPressed = Input.keyboard.isKeyPressed(Keyboard.Key.S);
        boolean dPressed = Input.keyboard.isKeyPressed(Keyboard.Key.D);
        boolean wPressed = Input.keyboard.isKeyPressed(Keyboard.Key.W);
        boolean tabJustPressed = Input.keyboard.isKeyJustReleased(Keyboard.Key.TAB);
        float mouseDy = Input.mouse.getYDelta();

        // tool settings
        if (qPressed) {
            deg += 180 * Graphics.getDeltaTime();
        }
        if (wPressed) {
            deg -= 180 * Graphics.getDeltaTime();
        }
        if (sPressed) {
            sclX *= 1.00f + 0.8f * Graphics.getDeltaTime();
            sclY *= 1.00f + 0.8f * Graphics.getDeltaTime();
        }
        if (aPressed) {
            sclX *= 1.00f - 0.8f * Graphics.getDeltaTime();
            sclY *= 1.00f - 0.8f * Graphics.getDeltaTime();
        }
        if (zJustPressed) {
            sclX *= -1;
        }

        if (!development) {
            if (leftButtonClicked) {

                for (Block block : BUNDLES_TOP_VIEW.get(bundleTopViewIndex).blocks) {
                    Vector2 offset = new Vector2(block.x, block.y);
                    offset.rotateDeg(deg);
                    offset.scl(sclX, sclY);
                    CommandTokenCreate cmd = new CommandTokenCreate(3, x + offset.x, y + offset.y, block.deg + deg, block.flipped ? -sclX : sclX, sclY, false, getToolOverlayBlockRegion(block));
                    cmd.tokenType = type;
                    map.addCommand(cmd);
                    bundleTopViewIndex = MathUtils.randomUniformInt(0, BUNDLES_TOP_VIEW.size);
                }
            }

        }

        // actions
        if (development) {

            if (rightButtonClicked) {
                dev_nextRegion();
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
                    System.out.println("\t" + "<block type=\"" + block.type.name() + "\" flipped=\"" + (block.sclX < 0) + "\" x=\"" + (block.x - cm.x) + "\" y=\"" + (block.y - cm.y) + "\" deg=\"" + block.deg + "\"/>");
                }
                System.out.println("</bundle>");
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
                renderer2D.drawTextureRegion(block.region, block.x, block.y, block.deg, block.sclX, block.sclY);
            }
            return;
        }

        if (!singles) { // && bundles top view
            Block[] blocks = BUNDLES_TOP_VIEW.get(bundleTopViewIndex).blocks;
            for (Block block : blocks) {
                Vector2 toBlock = new Vector2(block.x, block.y);
                toBlock.scl(Math.abs(sclX), Math.abs(sclY));
                toBlock.rotateDeg(deg);
                renderer2D.drawTextureRegion(getToolOverlayBlockRegion(block), x + toBlock.x, y + toBlock.y, block.deg + deg, block.flipped ? -sclX : sclX, sclY);
            }
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

    private TextureRegion getToolOverlayBlockRegion(Block block) {
        final String regionName = "assets/textures-layer-3/architecture_" + style.name().toLowerCase() + "_" + block.type.name().toLowerCase() + "_0.png";
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
