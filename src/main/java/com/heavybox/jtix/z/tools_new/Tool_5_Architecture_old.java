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
import com.heavybox.jtix.math.MathUtils;
import com.heavybox.jtix.math.Vector2;
import com.heavybox.jtix.z.*;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class Tool_5_Architecture_old extends Tool_new {

    private Shader shader;
    private final TexturePack atlas;

    // global state
    private Tool.Mode currentMode;
    private Tool.Shape currentShape;
    private View currentView;

    private float spacing = 1.0f;
    private Race race = Race.DWARF;
    private final Set<Token> tokensToDelete = new HashSet<>();
    private final Array<Token> tokensPreview = new Array<>();
    private final Array<Token> alreadyCreatedTokens = new Array<>();
    private boolean angleFollowPath = true;
    private boolean fillShape = false;
    private boolean procedural = true;

    // point mode
    private boolean dragged = false;
    private final Vector2 point_lastSpawnPoint = new Vector2();

    // line mode
    private boolean line_free = true;
    private final Vector2 line_start = new Vector2();
    private final Vector2 line_end = new Vector2();

    // circle mode
    private float circle_spreadRadius = 200;

    // polygon mode

    public Tool_5_Architecture_old(final RPGMapMakerScene scene) {
        super(scene);
        atlas = Assets.get("assets/texture-packs/layer_3.yml");
        sclX = 1f / 3;
        sclY = 1f / 3;
        // group assets

        currentMode = Tool.Mode.ADD;
        currentShape = Tool.Shape.POINT;
        currentView = View.ISOMETRIC_VIEW;

        Utils.countVariations(atlas, "assets/textures-layer-3/architecture_dwarf_top_view_tower");

        String vertex = Assets.getFileContent("assets/shaders/default-shader.vert");
        String fragment = Assets.getFileContent("assets/shaders/default-shader.frag");
        this.shader = new Shader(vertex, fragment);
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
        if (currentView == View.ISOMETRIC_VIEW) return 38 * spacing * Math.abs(sclX);
        return 38 * spacing * Math.abs(sclX); // TODO: for now, this is hard-coded.
    }

    private TextureRegion getRegion() {

        return null;
    }

    private TextureRegion getRegion_tower() {
        if (currentView == View.SIDE_VIEW) {
            String race = this.race.name().toLowerCase();
            String prefix = "assets/textures-layer-3/architecture_" + race + "_" + View.SIDE_VIEW.name().toLowerCase() + "_";
            String middle = "tower_" + (MathUtils.randomUniformInt(0, 2) == 1 ? "big" : "small");
            int variations = Utils.countVariations(atlas, prefix + middle);
            String suffix = "_" + MathUtils.randomUniformInt(0, variations) + ".png";
            return atlas.getRegion(prefix + middle + suffix);
        }

        if (currentView == View.ISOMETRIC_VIEW) {
            String race = this.race.name().toLowerCase();
            String prefix = "assets/textures-layer-3/architecture_" + race + "_" + View.ISOMETRIC_VIEW.name().toLowerCase() + "_";
            boolean tall = MathUtils.randomUniformInt(0,2) == 1;
            String middle = "tower_" + (tall ? "tall" : "short");
            String suffix = "_" + MathUtils.randomUniformInt(0,6) + ".png";
            return atlas.getRegion(prefix + middle + suffix);
        }

        return null;
    }

    private TextureRegion getRegion_sideBlock() {
        String race = this.race.name().toLowerCase();
        String prefix = "assets/textures-layer-3/architecture_" + race + "_" + View.SIDE_VIEW.name().toLowerCase() + "_";
        String middle = "block_" + (MathUtils.randomUniformInt(0, 2) == 1 ? "big" : "small");
        String suffix = "_" + MathUtils.randomUniformInt(0, Utils.countVariations(atlas, prefix + middle)) + ".png";
        return atlas.getRegion(prefix + middle + suffix);
    }

    private TextureRegion getRegion_isometricHouse(int angleIndex) {
        String race = this.race.name().toLowerCase();
        String prefix = "assets/textures-layer-3/architecture_" + race + "_" + View.ISOMETRIC_VIEW.name().toLowerCase() + "_";

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

    private void spawnToken(TextureRegion region, float x, float y, float deg, float sclX, float sclY) {
        CommandTokenCreate createToken = new CommandTokenCreate(
                3,
                x, y,
                deg,
                sclX, sclY,
                false,
                region
        );

        createToken.tokenType = currentView;
        map.addCommand(createToken);
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

        Color tint = new Color();
        tint.r = 1 + MathUtils.randomUniformFloat(-0.05f, 0.0f);
        tint.g = 1 + MathUtils.randomUniformFloat(-0.05f, 0.0f);
        tint.b = 1 + MathUtils.randomUniformFloat(-0.05f, 0.0f);
        tint.a = 1;
        createToken.tint = tint;
        createToken.shader = shader;
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
        boolean zJustPressed = Input.keyboard.isKeyJustPressed(Keyboard.Key.Z);

        if (zJustPressed) {
            this.currentView = Collections.enumNext(currentView);
            return;
        }

        if (currentMode == Tool.Mode.SUB) {
            // TODO
            return;
        }

        // add tokens
        if (currentShape == Tool.Shape.POINT) {
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
                if (currentView == View.ISOMETRIC_VIEW) {
                    float angleDeg = getDiscreteAngle(dir.angleDeg());
                    int angleIndex = getDiscreteAngleIndex(dir.angleDeg());
                    TextureRegion region = getRegion_isometricHouse(angleIndex);
                    spawnToken(region, MathUtils.randomUniformFloat(-2.5f, 2.5f), flipX(angleIndex));
                } else if (currentView == View.SIDE_VIEW) {
                    TextureRegion region = getRegion_sideBlock();
                    spawnToken(region, 0, MathUtils.randomUniformInt(0,2) == 1);
                }
                point_lastSpawnPoint.set(x, y);
                return;
            } else if (leftJustUp) {
                if (!dragged) {
                    TextureRegion region = getRegion_tower();
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
