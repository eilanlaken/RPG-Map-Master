package com.heavybox.jtix.z;

import com.heavybox.jtix.RPGMapMakerScene;
import com.heavybox.jtix.assets.Assets;
import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.collections.ArrayFloat;
import com.heavybox.jtix.collections.Collections;
import com.heavybox.jtix.graphics.*;
import com.heavybox.jtix.input.Input;
import com.heavybox.jtix.input.Keyboard;
import com.heavybox.jtix.input.Mouse;
import com.heavybox.jtix.math.MathUtils;
import com.heavybox.jtix.math.Shape2DPolygon;
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

            for (Element bundleElement : bundles_isometric_view) {
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

                BUNDLES_ISOMETRIC_VIEW.add(bundle);
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public Type type = Type.ISOMETRIC_VIEW_HOUSE_DIAGONAL_SHORT;
    public Race race = Race.HUMAN;
    public boolean singles = false;
    public boolean development = false;

    // tool overlay - development
    private final Array<Block> toolOverlayDevBlocks = new Array<>(false, 5);
    private final TexturePack atlas;
    private TextureRegion toolOverlayDevCurrentRegion;

    // tool overlay - non-development
    private int bundleTopViewIndex = MathUtils.randomUniformInt(0, BUNDLES_TOP_VIEW.size);
    private int bundleIsometricViewIndex = MathUtils.randomUniformInt(0, BUNDLES_ISOMETRIC_VIEW.size);
    private int bundleSideViewIndex = MathUtils.randomUniformInt(0, Math.max(1, BUNDLES_SIDE_VIEW.size));

    public StampMode stampMode = StampMode.POLYGON_TOP_VIEW;

    // point side view singles
    private final Block sideViewCurrentBlock = new Block();
    private int sideViewIndex = MathUtils.randomUniformInt(0,6);
    private Type sideViewType = Type.SIDE_VIEW_BLOCK;

    // line top view
    private Array<Block> lineBlocks = new Array<>(false, 10);
    private final Vector2 linePointStart = new Vector2();
    private boolean lineFree = true;

    // polygon top view
    private boolean polygonFree = false;
    private boolean polygonDrawing = false;
    private boolean polygonDone = true;
    private final Array<Vector2> polygonPoints = new Array<>(true, 10);
    private Shape2DPolygon polygonShape = new Shape2DPolygon(0,0,  400,0,  400,300,  0,300);

    public ToolStamp_Architecture(final RPGMapMakerScene scene) {
        super(scene);
        atlas = Assets.get("assets/texture-packs/layer_3.yml");

        sclX = 2f / 3;
        sclY = 2f / 3;

        shape = Shape.POINT;
        mode = Mode.ADD;

        toolOverlayDevCurrentRegion = getToolOverlayCurrentRegion();
    }

    @Override
    public void update(float delta) {
        float verticalScroll = Input.mouse.getScrollY();
        boolean leftButtonClicked = Input.mouse.isButtonClicked(Mouse.Button.LEFT);
        boolean rightButtonClicked = Input.mouse.isButtonClicked(Mouse.Button.RIGHT);
        boolean enterClicked = Input.keyboard.isKeyJustReleased(Keyboard.Key.ENTER); // print bundle
        boolean zJustPressed = Input.keyboard.isKeyJustPressed(Keyboard.Key.Z);
        boolean xJustPressed = Input.keyboard.isKeyJustPressed(Keyboard.Key.X);
        boolean aPressed = Input.keyboard.isKeyPressed(Keyboard.Key.A);
        boolean qPressed = Input.keyboard.isKeyPressed(Keyboard.Key.Q);
        boolean sPressed = Input.keyboard.isKeyPressed(Keyboard.Key.S);
        boolean dPressed = Input.keyboard.isKeyPressed(Keyboard.Key.D);
        boolean wPressed = Input.keyboard.isKeyPressed(Keyboard.Key.W);
        boolean tabJustPressed = Input.keyboard.isKeyJustReleased(Keyboard.Key.TAB);
        boolean leftShiftJustPressed = Input.keyboard.isKeyJustReleased(Keyboard.Key.LEFT_SHIFT);
        boolean rightShiftJustPressed = Input.keyboard.isKeyJustReleased(Keyboard.Key.RIGHT_SHIFT);
        boolean backspaceJustPressed = Input.keyboard.isKeyJustReleased(Keyboard.Key.BACKSPACE);
        boolean plusJustPressed = Input.keyboard.isKeyJustPressed(Keyboard.Key.EQUAL);
        boolean minusJustPressed = Input.keyboard.isKeyJustPressed(Keyboard.Key.MINUS);
        boolean nJustPressed = Input.keyboard.isKeyJustPressed(Keyboard.Key.N);
        boolean mJustPressed = Input.keyboard.isKeyJustPressed(Keyboard.Key.M);
        float mouseDy = Input.mouse.getYDelta();

        // tool settings - transform
        if (qPressed) {
            deg += 180 * Graphics.getDeltaTime();
        }
        if (wPressed) {
            deg -= 180 * Graphics.getDeltaTime();
        }
        if (plusJustPressed) {
            sclX *= 2;
            sclY *= 2;
        }
        if (minusJustPressed) {
            sclX *= 0.5f;
            sclY *= 0.5f;
        }
        if (tabJustPressed) {
            sclX *= -1;
        }

        // tool settings - parameters
        if (leftShiftJustPressed) {
            stampMode = Collections.enumNext(stampMode);
            onSetParameter();
            return;
        } else if (rightShiftJustPressed) {
            stampMode = Collections.enumPrev(stampMode);
            onSetParameter();
            return;
        }
        if (backspaceJustPressed) {
            mode = Collections.enumNext(mode);
            onSetParameter();
            return;
        }
        if (rightButtonClicked) {
            type = type.getNextView();
            sideViewType = sideViewType.getNextOfTheSamePrefix();
            onSetParameter();
            return;
        }
        if (verticalScroll > 0) {
            race = Collections.enumNext(race);
            onSetParameter();
            return;
        } else if (verticalScroll < 0) {
            race = Collections.enumPrev(race);
            onSetParameter();
            return;
        }

        if (development) {
            if (zJustPressed) {
                dev_nextRegion();
            } else if (xJustPressed) {
                dev_prevRegion();
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

            return;
        }

        if (mode == Mode.SUB) { // TODO: move to eraser edit tool

            return;
        }

        if (stampMode == StampMode.POINT_SINGLE_SIDE_VIEW) {
            if (nJustPressed) {
                sideViewIndex++;
                sideViewIndex %= getRegionCount(race, sideViewType);
                onSetParameter();
            } else if (mJustPressed) {
                sideViewIndex--;
                if (sideViewIndex < 0) sideViewIndex = getRegionCount(race, sideViewType) - 1;
                onSetParameter();
            } else if (leftButtonClicked) {
                Vector2 offset = new Vector2(sideViewCurrentBlock.x, sideViewCurrentBlock.y);
                offset.rotateDeg(deg);
                float sclXCorrected = sclX * 1.2f;
                float sclYCorrected = sclY * 1.2f;
                offset.scl(sclXCorrected, sclYCorrected);
                CommandTokenCreate cmd = new CommandTokenCreate(3, x + offset.x, y + offset.y, sideViewCurrentBlock.deg + deg, sideViewCurrentBlock.flipped ? -sclXCorrected : sclXCorrected, sclYCorrected, false, getCurrentParametersRegion_pointSideView());
                cmd.tokenType = type;
                map.addCommand(cmd);
            }
        }

        if (stampMode == StampMode.POINT_BUNDLE_ISOMETRIC_VIEW) {
            if (leftButtonClicked) {
                Block[] blocks = BUNDLES_ISOMETRIC_VIEW.get(bundleIsometricViewIndex).blocks;
                for (Block block : blocks) {
                    emitCreateBlockCommand(block);
                }
            }
        }

        if (stampMode == StampMode.POINT_BUNDLE_TOP_VIEW) {
            if (leftButtonClicked) {
                Block[] blocks = BUNDLES_TOP_VIEW.get(bundleTopViewIndex).blocks;
                for (Block block : blocks) {
                    emitCreateBlockCommand(block);
                }
            }
        }

        if (stampMode == StampMode.CIRCLE_TOP_VIEW) {

        }

        if (stampMode == StampMode.CIRCLE_TOP_VIEW_PROCEDURAL) {

        }

        if (stampMode == StampMode.LINE_TOP_VIEW) {
            if (lineFree) {
                if (leftButtonClicked) {
                    linePointStart.set(x,y);
                    lineFree = false;
                }
            } else {
                if (leftButtonClicked) {

                }
            }
        }

        if (stampMode == StampMode.POLYGON_TOP_VIEW) {
            if (polygonFree) {
                if (leftButtonClicked) {
                    polygonPoints.add(new Vector2(x, y));
                    polygonFree = false;
                    polygonDrawing = true;
                }
                return;
            }

            if (polygonDrawing) {
                if (leftButtonClicked) {
                    Vector2 p = new Vector2(x, y); // need to test intersections etc.
                    polygonPoints.add(p);
                    if (polygonPoints.size < 4) return;
                    if (Vector2.dst(p, polygonPoints.first()) <= 20) {
                        polygonDrawing = false;
                        polygonDone = true;
                        polygonShape = new Shape2DPolygon(polygonPoints);
                    }
                }
            }

        }

        if (stampMode == StampMode.POLYGON_TOP_VIEW_PROCEDURAL) {

        }

    }

    private void emitCreateBlockCommand(Block block) {
        Vector2 offset = new Vector2(block.x, block.y);
        offset.rotateDeg(deg);
        offset.scl(sclX, sclY);
        CommandTokenCreate cmd = new CommandTokenCreate(3, x + offset.x, y + offset.y, block.deg + deg, block.flipped ? -sclX : sclX, sclY, false, getBlockRegion(block));
        cmd.tokenType = type;
        map.addCommand(cmd);
        randomizeIndex();
    }

    @Override
    protected void onSetParameter() {

    }

    @Override
    public String getHelperText() {
        return super.getHelperText() + " |" +
                " Shape: " + shape + " (SHIFT) | " +
                " Add / Sub: " + mode + " (BACKSPACE) | " +
                " Mode: " + stampMode + " (-+) | " +
                " Race: " + race + " (scroll) | " +
                " View: " + type.getView() + " (V) | "
                ;
    }

    @Override
    public void renderToolText(Renderer2D renderer2D, float x, float y) {

    }

    @Override
    public void renderToolOverlay(Renderer2D renderer2D, float x, float y) {
        // internal development tool
        if (development) {
            render_tool_overlay_dev(renderer2D);
            return;
        }

        // delete architecture from the map
        if (mode == Mode.SUB) {
            renderer2D.setColor(Color.RED);
            renderer2D.drawCircleFilled(8, 10, x, y, 0, 1, 1);
            return;
        }

        // not development
        if (stampMode == StampMode.POINT_SINGLE_SIDE_VIEW) {
            // current block
            Vector2 toBlock = new Vector2(sideViewCurrentBlock.x, sideViewCurrentBlock.y);
            float sclXCorrected = sclX * 1.2f;
            float sclYCorrected = sclY * 1.2f;
            toBlock.scl(sclXCorrected, sclYCorrected);
            toBlock.rotateDeg(deg);
            renderer2D.drawTextureRegion(getCurrentParametersRegion_pointSideView(), x + toBlock.x, y + toBlock.y, sideViewCurrentBlock.deg + deg, sideViewCurrentBlock.flipped ? -sclXCorrected : sclXCorrected, sclYCorrected);
            return;
        }

        if (stampMode == StampMode.POINT_BUNDLE_ISOMETRIC_VIEW) {
            renderBlocks(renderer2D);
            return;
        }

        if (stampMode == StampMode.POINT_BUNDLE_TOP_VIEW) {
            renderBlocks(renderer2D);
            return;
        }

        if (stampMode == StampMode.CIRCLE_TOP_VIEW) {}
        if (stampMode == StampMode.CIRCLE_TOP_VIEW_PROCEDURAL) {}

        if (stampMode == StampMode.LINE_TOP_VIEW) {
            if (lineFree) {
                renderer2D.setColor(Color.WHITE);
                renderer2D.drawCircleThin(Math.max(10, 5), 10, x, y, 0, 1, 1);
            } else {
                for (Block block : lineBlocks) {

                }
                renderer2D.setColor(1,0,0,1f);
                renderer2D.drawLineThin(lineStart.x, lineStart.y, x, y);
                renderer2D.setColor(Color.WHITE);
            }
        }

        if (stampMode == StampMode.POLYGON_TOP_VIEW) {
            if (polygonFree) {
                renderer2D.setColor(Color.RED);
                renderer2D.drawCircleFilled(10,5, x, y, 0, 1,1);
                renderer2D.setColor(Color.WHITE);
                return;
            }

            if (polygonDrawing) {
                if (polygonPoints.isEmpty()) return;

                renderer2D.setColor(Color.BLACK);
                renderer2D.drawCircleBorder(15, 5, 10, polygonPoints.first().x, polygonPoints.first().y, 0, 1, 1);
                renderer2D.setColor(Color.RED);
                for (int i = 0; i < polygonPoints.size; i++) {
                    Vector2 p = polygonPoints.get(i);
                    renderer2D.drawCircleFilled(5, 5, p.x, p.y, 0, 1, 1);
                }
                for (int i = 0; i < polygonPoints.size - 1; i++) {
                    Vector2 p1 = polygonPoints.get(i);
                    Vector2 p2 = polygonPoints.get(i + 1);
                    renderer2D.drawLineThin(p1.x, p1.y, p2.x, p2.y);
                }
                renderer2D.drawLineThin(polygonPoints.last().x, polygonPoints.last().y, x, y);
                renderer2D.drawLineThin(x, y, polygonPoints.first().x, polygonPoints.first().y);
            }

            if (polygonDone) {
                renderer2D.setColor(Color.RED);
                // TODO: just for testing
                    polygonPoints.clear();
                    polygonPoints.add(new Vector2(0,0));
                    polygonPoints.add(new Vector2(400,0));
                    polygonPoints.add(new Vector2(400,300));
                    polygonPoints.add(new Vector2(0,300));
                    polygonPoints.add(new Vector2(0,0));


                for (int i = 0; i < polygonPoints.size - 1; i++) {
                    Vector2 p1 = polygonPoints.get(i);
                    Vector2 p2 = polygonPoints.get(i + 1);
                    renderer2D.drawLineThin(p1.x, p1.y, p2.x, p2.y);
                }
                Vector2 field = new Vector2(x, y);
                float angle = Utils.getDirection(field, polygonShape);
                Vector2 arrow = new Vector2(1,0).rotateDeg(angle).scl(100);
                renderer2D.setColor(Color.WHITE);
                if (Input.mouse.moved()) {
                    ArrayFloat distances = Utils.getDirectionVector(x, y, polygonShape, new Vector2());
                    System.out.println(distances);
                }
                renderer2D.drawLineThin(x, y, x + arrow.x, y + arrow.y);
            }
        }

        if (stampMode == StampMode.POLYGON_TOP_VIEW_PROCEDURAL) {

        }

    }

    private void refillLineBlocks() {
        lineBlocks.clear();

    }

    private void renderBlocks(Renderer2D renderer2D) {
        Block[] blocks = getBundleBlocks();
        for (Block block : blocks) {
            Vector2 toBlock = new Vector2(block.x, block.y);
            toBlock.scl(Math.abs(sclX), Math.abs(sclY));
            toBlock.rotateDeg(deg);
            renderer2D.drawTextureRegion(getToolOverlayBlockRegion(block), x + toBlock.x, y + toBlock.y, block.deg + deg, block.flipped ? -sclX : sclX, sclY);
        }
    }

    private void render_tool_overlay_dev(Renderer2D renderer2D) {
        renderer2D.setColor(Color.WHITE);
        renderer2D.drawTextureRegion(toolOverlayDevCurrentRegion, x, y, deg, this.sclX, this.sclY);
        toolOverlayDevBlocks.sort(Comparator.comparingInt(o -> -(int) o.y));
        for (Block block : toolOverlayDevBlocks) {
            renderer2D.drawTextureRegion(block.region, block.x, block.y, block.deg, block.sclX, block.sclY);
        }
    }

    private void randomizeIndex() {
        bundleTopViewIndex = MathUtils.randomUniformInt(0, Math.max(1, BUNDLES_TOP_VIEW.size));
        bundleIsometricViewIndex = MathUtils.randomUniformInt(0, Math.max(1, BUNDLES_ISOMETRIC_VIEW.size));
        bundleSideViewIndex = MathUtils.randomUniformInt(0, Math.max(1, BUNDLES_SIDE_VIEW.size));
    }

    private Block[] getCurrentBundleBlocks() {
        if (type.name().startsWith("SIDE")) return BUNDLES_SIDE_VIEW.get(bundleSideViewIndex).blocks;
        if (type.name().startsWith("ISO")) return BUNDLES_ISOMETRIC_VIEW.get(bundleIsometricViewIndex).blocks;
        if (type.name().startsWith("TOP")) return BUNDLES_TOP_VIEW.get(bundleTopViewIndex).blocks;

        return BUNDLES_TOP_VIEW.get(bundleTopViewIndex).blocks;
    }

    private Block[] getBundleBlocks() {
        if (stampMode == StampMode.POINT_BUNDLE_ISOMETRIC_VIEW) return BUNDLES_ISOMETRIC_VIEW.get(bundleIsometricViewIndex).blocks;
        if (stampMode == StampMode.POINT_BUNDLE_TOP_VIEW) return BUNDLES_TOP_VIEW.get(bundleTopViewIndex).blocks;

        return null;
    }

    private void dev_nextRegion() {
        type = type.getNextOfTheSamePrefix();
        toolOverlayDevCurrentRegion = getToolOverlayCurrentRegion();
    }

    private void dev_prevRegion() {
        type = type.getPrevOfTheSamePrefix();
        toolOverlayDevCurrentRegion = getToolOverlayCurrentRegion();
    }

    private TextureRegion getToolOverlayCurrentRegion() {
        final String regionName = "assets/textures-layer-3/architecture_" + race.name().toLowerCase() + "_" + type.name().toLowerCase() + "_0.png";
        return atlas.getRegion(regionName);
    }

    private TextureRegion getBlockRegion(Block block) {
        final String regionName = "assets/textures-layer-3/architecture_" + race.name().toLowerCase() + "_" + block.type.name().toLowerCase() + "_" + MathUtils.randomUniformInt(0,6) + ".png";
        return atlas.getRegion(regionName);
    }

    private TextureRegion getToolOverlayBlockRegion(Block block) {
        final String regionName = "assets/textures-layer-3/architecture_" + race.name().toLowerCase() + "_" + block.type.name().toLowerCase() + "_0.png";
        return atlas.getRegion(regionName);
    }

    private TextureRegion getCurrentParametersRegion_pointSideView() {
        sideViewIndex %= getRegionCount(race, sideViewType);
        final String regionName = "assets/textures-layer-3/architecture_" + race.name().toLowerCase() + "_" + sideViewType.name().toLowerCase() + "_" + sideViewIndex + ".png";
        return atlas.getRegion(regionName);
    }

    // TODO: refactor into a global static method of Tools.java
    private int getRegionCount(Race race, Type type) {
        int count = 0;
        while (count < 100) {
            final String regionName = "assets/textures-layer-3/architecture_" + race.name().toLowerCase() + "_" + type.name().toLowerCase() + "_" + count + ".png";
            if (atlas.contains(regionName)) {
                count++;
            } else {
                break;
            }
        }
        return count;
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
        SIDE_VIEW_TOWER,

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

    public enum StampMode {
        POINT_SINGLE_SIDE_VIEW,
        POINT_BUNDLE_ISOMETRIC_VIEW,
        POINT_BUNDLE_TOP_VIEW,

        CIRCLE_TOP_VIEW,
        CIRCLE_TOP_VIEW_PROCEDURAL,

        LINE_TOP_VIEW,

        POLYGON_TOP_VIEW,
        POLYGON_TOP_VIEW_PROCEDURAL,
    }

    public enum Race {
        HUMAN,
        ELF,
        DWARF,
        ORC,
    }

}
