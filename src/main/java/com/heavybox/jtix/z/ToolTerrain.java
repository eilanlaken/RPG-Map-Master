package com.heavybox.jtix.z;

import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.graphics.Texture;
import com.heavybox.jtix.input.Input;
import com.heavybox.jtix.input.Keyboard;
import com.heavybox.jtix.input.Mouse;

public class ToolTerrain extends Tool {

    public Mode mode = Mode.SUB;
    public Target target = Target.FOREGROUND_STONE;

    public Texture brushAdd;
    public Texture brushSub;

    public float scale = 0.5f;
    public float size = 200;

    public ToolTerrain(Map map) {
        super(map);
        brushAdd = new Texture("assets/tools/terrain-brush-draw.png");
        brushSub = new Texture("assets/tools/terrain-brush-erase.png");
        sclX = sclY = scale;
    }

    @Override
    public void update(float delta) {
        size += 2 * Input.mouse.getVerticalScroll();
        sclX = size / 512.0f;
        sclY = size / 512.0f;
        if (Input.keyboard.isKeyJustPressed(Keyboard.Key.EQUAL)) {
            mode = Mode.ADD;
        } else if (Input.keyboard.isKeyJustPressed(Keyboard.Key.MINUS)) {
            mode = Mode.SUB;
        } else if (Input.mouse.isButtonClicked(Mouse.Button.RIGHT)) {
            target = Target.values()[(target.ordinal() + 1) % Target.values().length];
        }
        if (Input.mouse.isButtonJustPressed(Mouse.Button.LEFT)) {
            CommandTerrain commandTerrain = new CommandTerrain(x, y, 0, sclX, sclY, size,false, mode);
            map.addCommand(commandTerrain);
        } else if (Input.mouse.isButtonPressed(Mouse.Button.LEFT) && (Input.mouse.getXDelta() != 0 || Input.mouse.getYDelta() != 0)) {
            CommandTerrain commandTerrain = new CommandTerrain(x, y, 0, sclX, sclY, size,false, mode);
            map.addCommand(commandTerrain);
        } else if (Input.mouse.isButtonJustReleased(Mouse.Button.LEFT)) {

        }
    }

    @Override
    public void renderToolOverlay(Renderer2D renderer2D, float x, float y) {
        renderer2D.setColor(Color.WHITE);
        if (mode == Mode.ADD) {
            renderer2D.drawTexture(brushAdd, x, y, 0, sclX, sclY);
        } else if (mode == Mode.SUB) {
            renderer2D.drawTexture(brushSub, x, y, 0, sclX, sclY);
        }
    }

    @Override
    public void activate() {

    }

    @Override
    public void deactivate() {

    }

    public enum Mode {
        SUB,
        ADD,
    }

    public enum Target {
        BACKGROUND,
        TERRAIN,
        FOREGROUND_STONE,
        FOREGROUND_ROAD,
    }

}
