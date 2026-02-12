package com.heavybox.jtix.z;

import com.heavybox.jtix.RPGMapMakerScene;
import com.heavybox.jtix.assets.Assets;
import com.heavybox.jtix.collections.Collections;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.graphics.Texture;
import com.heavybox.jtix.input.Input;
import com.heavybox.jtix.input.Keyboard;
import com.heavybox.jtix.input.Mouse;

public class ToolBrush_Terrain extends Tool {

    public Target target = Target.GROUND;

    public Texture brushAdd;
    public Texture brushSub;

    public int groundIndex = 3;
    public int liquidIndex = 1;

    public ToolBrush_Terrain(RPGMapMakerScene scene) {
        super(scene);
        brushAdd = Assets.get("assets/tools/terrain-brush-draw.png");
        brushSub = Assets.get("assets/tools/terrain-brush-erase.png");
        sclX = 0.5f;
        sclY = 0.5f;
    }

    @Override
    public void update(float delta) {
        boolean leftButtonPressed = Input.mouse.isButtonPressed(Mouse.Button.LEFT);
        boolean leftButtonJustPressed = Input.mouse.isButtonJustPressed(Mouse.Button.LEFT);
        boolean mouseMoved = Input.mouse.moved();
        boolean rightButtonJustPressed = Input.mouse.isButtonJustPressed(Mouse.Button.RIGHT);
        boolean spaceJustPressed = Input.keyboard.isKeyJustPressed(Keyboard.Key.SPACE);

        // TODO brush settings
        if (rightButtonJustPressed) {
            setTarget(Collections.enumNext(target));
            return;
        }

        if (spaceJustPressed) {
            setMode(Collections.enumNext(mode));
            return;
        }

        if (leftButtonJustPressed || (leftButtonPressed && mouseMoved)) {
            CommandTerrainTerraform_new cmd = new CommandTerrainTerraform_new(x, y, sclX, sclY, false); // TODO: anchor
            cmd.target = target;
            cmd.mode = mode;
            cmd.groundIndex = groundIndex;
            cmd.liquidIndex = liquidIndex;
            map.addCommand(cmd);
        }
    }

    public void setTarget(Target target) {
        this.target = target;
        System.out.println(target);
        onSetParameter();
    }

    @Override
    protected void onSetMode() {
        System.out.println(mode);
    }

    @Override
    public void renderToolOverlay(Renderer2D renderer2D, float x, float y) {

    }

    @Override
    public void activate() {

    }

    @Override
    public void deactivate() {

    }

    @Override
    public String getName() {
        return "Tool - Terrain";
    }

    public enum Target {
        GROUND,
        LIQUID,
        BLEND_MAP
    }

}
