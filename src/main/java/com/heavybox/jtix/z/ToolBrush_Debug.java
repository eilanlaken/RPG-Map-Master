package com.heavybox.jtix.z;

import com.heavybox.jtix.RPGMapMakerScene;
import com.heavybox.jtix.assets.Assets;
import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.collections.ArrayChar;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.graphics.TexturePack;
import com.heavybox.jtix.graphics.TextureRegion;
import com.heavybox.jtix.input.Keyboard;
import com.heavybox.jtix.input.Mouse;
import org.jetbrains.annotations.NotNull;

public class ToolBrush_Debug extends Tool {

    private final TexturePack atlas;
    private TextureRegion region;

    private Mode currentMode = Mode.ADD;
    private Shape currentShape = Shape.POINTS;

    // points mode

    // circle mode

    // line mode

    // polygon mode

    public ToolBrush_Debug(final RPGMapMakerScene scene) {
        super(scene);
        // TODO:
        //Input.addEventHandler(this);
        atlas = Assets.get("assets/texture-packs/layer_3.yml");
        region = atlas.getRegion("assets/textures-layer-3/debug_rect_0.png");
    }

    @Override
    public void update(float delta) {

    }

    @Override
    public void renderToolOverlay(Renderer2D renderer2D, float x, float y) {

    }

    @Override
    public void renderToolText(Renderer2D renderer2D, float x, float y) {

    }

    @Override
    public void onActivate() {

    }

    @Override
    public void onDeactivate() {

    }

    @Override
    public String getName() {
        return "";
    }

    @Override
    public int getLayer() {
        return 2;
    }

    // **************** TODO ****************** ////////////
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
        if (keys.contains(Keyboard.Key.LEFT_SHIFT, true)) {
            // change shape
            return true;
        }

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
