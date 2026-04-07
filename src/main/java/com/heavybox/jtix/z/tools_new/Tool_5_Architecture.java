package com.heavybox.jtix.z.tools_new;

import com.heavybox.jtix.RPGMapMakerScene;
import com.heavybox.jtix.assets.Assets;
import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.collections.ArrayChar;
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

import java.util.HashSet;
import java.util.Set;

public class Tool_5_Architecture extends Tool_new {

    private final TexturePack atlas;

    // global state
    private Tool.Mode currentMode;
    private Tool.Shape currentShape;
    private View currentView;

    private float spacing = 1.0f;
    private Race race = Race.HUMAN;
    private final Set<Token> tokensToDelete = new HashSet<>();
    private final Array<Token> tokensPreview = new Array<>();
    private final Array<Token> alreadyCreatedTokens = new Array<>();
    private boolean angleFollowPath = true;
    private boolean fillShape = false;
    private boolean procedural = true;
    private boolean bundleMode = true;

    // point mode
    private float point_currentDragDistance = 0;
    private final Vector2 point_lastDragPoint = new Vector2();
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
    // TODO: move this logic to update() when using point mode.
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

    private TextureRegion getRegion(int angleIndex) {
        String prefix = "assets/textures-layer-3/architecture_" + race.name().toLowerCase() + "_" + currentView.name().toLowerCase() + "_";

        String middle = "";
        if (angleIndex == 0 || angleIndex == 4) {
            middle = "house_horizontal_short";
        }
        if (angleIndex == 1 || angleIndex == 5) {
            middle = "house_diagonal_short";
        }
        if (angleIndex == 2 || angleIndex == 6) {
            middle = "house_vertical_short";
        }
        if (angleIndex == 3 || angleIndex == 7) {
            middle = "house_diagonal_short";
        }

        String suffix = "_" + MathUtils.randomUniformInt(0,6) + ".png";

        return atlas.getRegion(prefix + middle + suffix);
    }

    private void spawnToken(TextureRegion region, float deg, boolean flipX) {

        CommandTokenCreate createToken = new CommandTokenCreate(
                3,
                x, y,
                deg,
                flipX ? -sclX : sclX, sclY,
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
        if (currentShape == Tool.Shape.POINT) {
            if (leftJustDown) {
                point_lastDragPoint.set(x, y);
                return;
            } else if (leftJustUp) {

                return;
            } else if (leftPressed && mouseMoved) {
                Vector2 current = new Vector2(x, y);
                float dst = Vector2.dst(current, point_lastDragPoint);
                if (dst < getProceduralSpacing()) return;

                // spawn token and reset anchor
                Vector2 dir = new Vector2(x - point_lastDragPoint.x, y - point_lastDragPoint.y);
                float angleDeg = getDiscreteAngle(dir.angleDeg());
                int angleIndex = getDiscreteAngleIndex(dir.angleDeg());
                TextureRegion region = getRegion(angleIndex);
                spawnToken(region, 0, flipX(angleIndex));
                //Tuple2<Vector2, Float> debugToken = new Tuple2<>(new Vector2(x, y), angleDeg);
                //point_debugTokens.add(debugToken);
                point_lastDragPoint.set(x, y);
                return;
            }
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

}
