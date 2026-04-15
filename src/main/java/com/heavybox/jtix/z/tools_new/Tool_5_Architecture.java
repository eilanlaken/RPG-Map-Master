package com.heavybox.jtix.z.tools_new;

import com.heavybox.jtix.RPGMapMakerScene;
import com.heavybox.jtix.assets.Assets;
import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.collections.ArrayChar;
import com.heavybox.jtix.collections.Collections;
import com.heavybox.jtix.collections.Tuple2;
import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.graphics.TexturePack;
import com.heavybox.jtix.graphics.TextureRegion;
import com.heavybox.jtix.input.Input;
import com.heavybox.jtix.input.Keyboard;
import com.heavybox.jtix.input.Mouse;
import com.heavybox.jtix.math.MathUtils;
import com.heavybox.jtix.math.Vector2;
import com.heavybox.jtix.z.CommandTokenCreate;
import com.heavybox.jtix.z.Token;
import com.heavybox.jtix.z.Tool;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Tool_5_Architecture extends Tool_new {

    private static final Array<Bundle> BUNDLES_TOP_VIEW       = new Array<>(true, 10);
    private static final Array<Bundle> BUNDLES_SIDE_VIEW      = new Array<>(true, 10);
    private static final Array<Bundle> BUNDLES_ISOMETRIC_VIEW = new Array<>(true, 10);

    static {
        try {
            File file = new File("assets/data/architecture-bundles.xml");
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
                    float sclX = Float.parseFloat(blockElement.getAttribute("sclX"));
                    float sclY = Float.parseFloat(blockElement.getAttribute("sclY"));
                    Type type = Type.valueOf(blockElement.getAttribute("type"));
                    bundle.blocks[i] = new Block();
                    bundle.blocks[i].x = x;
                    bundle.blocks[i].y = y;
                    bundle.blocks[i].deg = deg;
                    bundle.blocks[i].sclX = sclX;
                    bundle.blocks[i].sclY = sclY;
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
                    float sclX = Float.parseFloat(blockElement.getAttribute("sclX"));
                    float sclY = Float.parseFloat(blockElement.getAttribute("sclY"));
                    Type type = Type.valueOf(blockElement.getAttribute("type"));
                    bundle.blocks[i] = new Block();
                    bundle.blocks[i].x = x;
                    bundle.blocks[i].y = y;
                    bundle.blocks[i].deg = deg;
                    bundle.blocks[i].sclX = sclX;
                    bundle.blocks[i].sclY = sclY;
                    bundle.blocks[i].type = type;
                }

                BUNDLES_ISOMETRIC_VIEW.add(bundle);
            }

            for (Element bundleElement : bundles_side_view) {
                NodeList blocks = bundleElement.getElementsByTagName("block");
                Bundle bundle = new Bundle();
                bundle.blocks = new Block[blocks.getLength()];
                for (int i = 0; i < blocks.getLength(); i++) {
                    Element blockElement = (Element) blocks.item(i);
                    float x = Float.parseFloat(blockElement.getAttribute("x"));
                    float y = Float.parseFloat(blockElement.getAttribute("y"));
                    float deg = Float.parseFloat(blockElement.getAttribute("deg"));
                    float sclX = Float.parseFloat(blockElement.getAttribute("sclX"));
                    float sclY = Float.parseFloat(blockElement.getAttribute("sclY"));
                    Type type = Type.valueOf(blockElement.getAttribute("type"));
                    bundle.blocks[i] = new Block();
                    bundle.blocks[i].x = x;
                    bundle.blocks[i].y = y;
                    bundle.blocks[i].deg = deg;
                    bundle.blocks[i].sclX = sclX;
                    bundle.blocks[i].sclY = sclY;
                    bundle.blocks[i].type = type;
                }

                BUNDLES_SIDE_VIEW.add(bundle);
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private final TexturePack atlas;

    // global state
    private Tool.Mode currentMode;
    private Tool.Shape currentShape;
    private View currentView;

    private float spacing = 1.0f;
    private Race race = Race.ELF;
    private final Set<Token> tokensToDelete = new HashSet<>();
    private final Array<Token> tokensPreview = new Array<>();
    private final Array<Token> alreadyCreatedTokens = new Array<>();
    private boolean angleFollowPath = true;
    private boolean fillShape = false;
    private boolean procedural = true;
    private boolean bundleModeOn = false;

    // point mode
    private boolean dragged = false;
    private final Vector2 point_lastSpawnPoint = new Vector2();
    private final Array<Tuple2<Vector2, Float>> point_debugTokens = new Array<>();

    // line mode

    // circle mode

    // polygon mode

    public Tool_5_Architecture(final RPGMapMakerScene scene) {
        super(scene);
        atlas = Assets.get("assets/texture-packs/layer_3.yml");
        sclX = 1f / 3;
        sclY = 1f / 3;
        // group assets

        currentMode = Tool.Mode.ADD;
        currentShape = Tool.Shape.POINT;
        currentView = View.ISOMETRIC_VIEW;
    }

    // this is the heart of the architecture tool
    private void setTokenParams(float mouseAngleDeg) {

        // for isometric view
        final float RANGE = 22.5f;
        int index = 0;
        for (int i = 0; i < 8; i++) {
            float deg = i * 45;
            float dist = MathUtils.shortestAngularDistanceDeg(deg, mouseAngleDeg);
            if (dist <= RANGE) {
                index = i;
                break;
            }
        }

        index = (index + 2) % 8; // advance by 90 degrees
        if (index >= 4) index -= 4; // limit to [0,3] range (4 options).

        // now, consider each of the 8 quadrants
        if (index == 0) { // horizontal

        } else if (index == 1) { // diagonal left

        } else if (index == 2) { // vertical

        } else { // index == 3 // diagonal right

        }
    }

    private float getDiscreteAngle(float angle) {
        // for isometric view
        final float RANGE = 22.5f;
        for (int i = 0; i < 8; i++) {
            float deg = i * 45;
            float dist = MathUtils.shortestAngularDistanceDeg(deg, angle);
            if (dist <= RANGE) {
                return deg;
            }
        }

        return 0;
    }

    private int getDiscreteAngleIndex(float angle) {
        // for isometric view
        final float RANGE = 22.5f;
        int index = 0;
        for (int i = 0; i < 8; i++) {
            float deg = i * 45;
            float dist = MathUtils.shortestAngularDistanceDeg(deg, angle);
            if (dist <= RANGE) {
                index = i;
                break;
            }
        }

        index = (index + 2) % 8; // advance by 90 degrees
        return index;
    }

    private boolean flipX(int angleIndex) {
        return angleIndex == 1 || angleIndex == 5;
    }

    protected float getProceduralSpacing() {
        return 38 * spacing * Math.abs(sclX); // TODO: for now, this is hard-coded.
    }

    private TextureRegion getRegion_isometricTower() {
        String prefix = "assets/textures-layer-3/architecture_" + race.name().toLowerCase() + "_" + currentView.name().toLowerCase() + "_";
        boolean tall = MathUtils.randomUniformInt(0,2) == 1;
        String middle = "tower_" + (tall ? "tall" : "short");
        String suffix = "_" + MathUtils.randomUniformInt(0,6) + ".png";
        return atlas.getRegion(prefix + middle + suffix);
    }

    private TextureRegion getRegion_isometricHouse(int angleIndex) {
        String prefix = "assets/textures-layer-3/architecture_" + race.name().toLowerCase() + "_" + currentView.name().toLowerCase() + "_";

        String middle = "";
        if (angleIndex == 0 || angleIndex == 4) {
            boolean tall = MathUtils.randomUniformInt(0,2) == 1;
            middle = "house_horizontal_" + (tall ? "tall" : "short");
        }
        if (angleIndex == 1 || angleIndex == 5) {
            int type = MathUtils.randomUniformInt(0,3);
            if (type == 0) middle = "house_diagonal_short";
            if (type == 1) middle = "house_diagonal_tall";
            if (type == 2) middle = "hut_diagonal";
        }
        if (angleIndex == 2 || angleIndex == 6) {
            int type = MathUtils.randomUniformInt(0,3);
            if (type == 0) middle = "house_vertical_short";
            if (type == 1) middle = "house_vertical_tall";
            if (type == 2) middle = "hut_vertical";
        }
        if (angleIndex == 3 || angleIndex == 7) {
            int type = MathUtils.randomUniformInt(0,3);
            if (type == 0) middle = "house_diagonal_short";
            if (type == 1) middle = "house_diagonal_tall";
            if (type == 2) middle = "hut_diagonal";
        }

        String suffix = "_" + MathUtils.randomUniformInt(0,6) + ".png";

        return atlas.getRegion(prefix + middle + suffix);
    }

    private void spawnToken(TextureRegion region, float deg, boolean flipX) {

        CommandTokenCreate createToken = new CommandTokenCreate(
                3,
                x, y,
                deg,
                flipX ? -sclX : sclX,
                sclY,
                false,
                region
        );

        createToken.tokenType = currentView;
        map.addCommand(createToken);
    }

    @Override
    void onChangeParameters() {

    }

    @Override
    public void update(float delta) {
        // =============  input parameters  ===============
        boolean backspaceJustPressed = Input.keyboard.isKeyJustPressed(Keyboard.Key.BACKSPACE);
        boolean leftShiftJustPressed = Input.keyboard.isKeyJustPressed(Keyboard.Key.LEFT_SHIFT);
        boolean leftClicked = Input.mouse.isButtonClicked(Mouse.Button.LEFT);
        boolean leftJustDown = Input.mouse.isButtonJustPressed(Mouse.Button.LEFT);
        boolean leftJustUp = Input.mouse.isButtonJustReleased(Mouse.Button.LEFT);
        boolean leftPressed = Input.mouse.isButtonPressed(Mouse.Button.LEFT);
        boolean mouseMoved = Input.mouse.moved();
        boolean leftPressedAndMoved = Input.mouse.isButtonPressed(Mouse.Button.LEFT) && mouseMoved;
        boolean plusJustPressed = Input.keyboard.isKeyJustPressed(Keyboard.Key.EQUAL);
        boolean minusJustPressed = Input.keyboard.isKeyJustPressed(Keyboard.Key.MINUS);
        boolean sPressed = Input.keyboard.isKeyPressed(Keyboard.Key.S);
        boolean aPressed = Input.keyboard.isKeyPressed(Keyboard.Key.A);
        boolean dPressed = Input.keyboard.isKeyPressed(Keyboard.Key.D);

        if (currentMode == Tool.Mode.SUB) {
            // TODO
            return;
        }

        // add tokens
        if (currentShape == Tool.Shape.POINT && currentView == View.ISOMETRIC_VIEW && !bundleModeOn) {
            if (leftJustDown) {
                point_lastSpawnPoint.set(x, y);
                dragged = false;
                return;
            } else if (leftPressed && mouseMoved) {
                dragged = true;
                Vector2 current = new Vector2(x, y);
                float dst = Vector2.dst(current, point_lastSpawnPoint);
                if (dst < getProceduralSpacing()) return;

                // spawn token and reset anchor
                Vector2 dir = new Vector2(x - point_lastSpawnPoint.x, y - point_lastSpawnPoint.y);
                float angleDeg = getDiscreteAngle(dir.angleDeg());
                int angleIndex = getDiscreteAngleIndex(dir.angleDeg());
                TextureRegion region = getRegion_isometricHouse(angleIndex);
                spawnToken(region, MathUtils.randomUniformFloat(-5,5), flipX(angleIndex));
                point_lastSpawnPoint.set(x, y);
                return;
            } else if (leftJustUp) {
                if (!dragged) {
                    TextureRegion region = getRegion_isometricTower();
                    spawnToken(region, 0, MathUtils.randomUniformInt(0,2) == 1);
                }
            }
        }

        if (currentShape == Tool.Shape.LINE) {

        }

        if (currentShape == Tool.Shape.CIRCLE) {

        }

        if (currentShape == Tool.Shape.POLYGON) {

        }

    }

    @Override
    public void renderToolOverlay(Renderer2D renderer2D, float x, float y) {
        if (currentMode == Tool.Mode.SUB) {

            return;
        }

        if (currentShape == Tool.Shape.POINT) {
            for (Tuple2<Vector2, Float> debugToken : point_debugTokens) {
                renderer2D.setColor(Color.randomOpaque());
                renderer2D.drawRectangleFilled(38, 100, debugToken.t1.x, debugToken.t1.y, debugToken.t2, 1, 1);
            }
        }

        if (currentShape == Tool.Shape.LINE) {

        }

        if (currentShape == Tool.Shape.CIRCLE) {

        }

        if (currentShape == Tool.Shape.POLYGON) {

        }

        renderer2D.setColor(Color.WHITE);
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

    public enum View {
        TOP_VIEW,
        SIDE_VIEW,
        ISOMETRIC_VIEW
    }

    public enum Race {
        HUMAN,
        ELF,
        DWARF,
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

}
