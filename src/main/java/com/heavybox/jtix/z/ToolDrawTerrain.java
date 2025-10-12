package com.heavybox.jtix.z;

import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Graphics;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.graphics.Texture;
import com.heavybox.jtix.input.Input;
import com.heavybox.jtix.input.Keyboard;
import com.heavybox.jtix.input.Mouse;

public class ToolDrawTerrain extends Tool {

    public Mode mode = Mode.SUB;
    public Target target = Target.TERRAIN;

    public Texture brushAdd;
    public Texture brushSub;

    public float scale = 0.5f;
    public float size = 200;

    public ToolDrawTerrain(Map map) {
        super(map);
        brushAdd = new Texture("assets/tools/terrain-brush-draw.png");
        brushSub = new Texture("assets/tools/terrain-brush-erase.png");
        sclX = sclY = scale;
    }

    @Override
    public void update(float delta) {
        // input
        boolean rightButtonClicked = Input.mouse.isButtonClicked(Mouse.Button.RIGHT);
        boolean leftButtonPressed = Input.mouse.isButtonPressed(Mouse.Button.LEFT);
        boolean leftButtonJustPressed = Input.mouse.isButtonJustPressed(Mouse.Button.LEFT);
        boolean sKeyPressed = Input.keyboard.isKeyPressed(Keyboard.Key.S);
        boolean qKeyJustPressed = Input.keyboard.isKeyJustPressed(Keyboard.Key.Q);
        boolean wKeyJustPressed = Input.keyboard.isKeyJustPressed(Keyboard.Key.W);
        float dx = Input.mouse.getXDelta();
        float dy = Input.mouse.getYDelta();
        float verticalScroll = Input.mouse.getVerticalScroll();

        if (qKeyJustPressed) {
            CommandTerrainChangeEnvironment cmd = new CommandTerrainChangeEnvironment(CommandTerrainChangeEnvironment.Type.SELECT_NEXT_GROUND);
            map.addCommand(cmd);
            return;
        }

        if (wKeyJustPressed) {
            CommandTerrainChangeEnvironment cmd = new CommandTerrainChangeEnvironment(CommandTerrainChangeEnvironment.Type.SELECT_NEXT_LIQUID);
            map.addCommand(cmd);
            return;
        }

        if (rightButtonClicked) {
            if (mode == Mode.ADD) mode = Mode.SUB;
            else mode = Mode.ADD;
            return;
        }

        if (verticalScroll > 0) {
            target = Target.values()[(target.ordinal() + 1) % Target.values().length];
            System.out.println(target.name());
            return;
        } else if (verticalScroll < 0) {
            target = Target.values()[(target.ordinal() - 1 + Target.values().length) % Target.values().length];
            System.out.println(target.name());
            return;
        }

        if (sKeyPressed && dy != 0) {
            size -= dy / 1000 * Graphics.getWindowHeight();
            sclX = size / 100;
            sclY = size / 100;
            return;
        }

        if (leftButtonJustPressed) {
            CommandTerrain commandTerrain = new CommandTerrain(x, y, 0, sclX, sclY, size,false, mode, target);
            map.addCommand(commandTerrain);
            return;
        } else if (leftButtonPressed && (dx != 0 || dy != 0)) {
            CommandTerrain commandTerrain = new CommandTerrain(x, y, 0, sclX, sclY, size,false, mode, target);
            map.addCommand(commandTerrain);
            return;
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
