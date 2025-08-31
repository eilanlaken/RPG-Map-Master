package com.heavybox.jtix.z;

import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.graphics.Texture;
import com.heavybox.jtix.input.Input;
import com.heavybox.jtix.input.Keyboard;
import com.heavybox.jtix.input.Mouse;

public class ToolTerrain extends Tool {

    public Mode mode = Mode.SUB_LAND;

    public Texture brushAdd;
    public Texture brushSub;

    public ToolTerrain(Map map) {
        super(map);
        brushAdd = new Texture("assets/tools/terrain-brush-draw.png");
        brushSub = new Texture("assets/tools/terrain-brush-erase.png");
    }

    @Override
    public void update(float delta) {
        if (Input.keyboard.isKeyJustPressed(Keyboard.Key.TAB)) {
            mode = Mode.values()[(mode.ordinal() + 1) % Mode.values().length];
        }
        if (Input.mouse.isButtonJustPressed(Mouse.Button.LEFT)) {
            CommandTerrain commandTerrain = new CommandTerrain(x, y, 0, sclX, sclY, false, mode);
            map.addCommand(commandTerrain);
        } else if (Input.mouse.isButtonPressed(Mouse.Button.LEFT) && (Input.mouse.getXDelta() != 0 || Input.mouse.getYDelta() != 0)) {
            CommandTerrain commandTerrain = new CommandTerrain(x, y, 0, sclX, sclY, false, mode);
            map.addCommand(commandTerrain);
        } else if (Input.mouse.isButtonJustReleased(Mouse.Button.LEFT)) {

        }
    }

    @Override
    public void renderToolOverlay(Renderer2D renderer2D, float x, float y) {
        renderer2D.setColor(Color.WHITE);
        if (mode == Mode.ADD_LAND || mode == Mode.ADD_ROAD) {
            renderer2D.drawTexture(brushAdd, x, y, 0, 1, 1);
        } else if (mode == Mode.SUB_LAND || mode == Mode.SUB_ROAD) {
            renderer2D.drawTexture(brushSub, x, y, 0, 1, 1);
        }
    }

    @Override
    public void activate() {

    }

    @Override
    public void deactivate() {

    }

    public enum Mode {

        SUB_LAND,
        ADD_LAND,
        ADD_ROAD,
        SUB_ROAD

    }

}
