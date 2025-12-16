package com.heavybox.jtix.z;

import com.heavybox.jtix.assets.Assets;
import com.heavybox.jtix.graphics.*;
import com.heavybox.jtix.input.Input;
import com.heavybox.jtix.input.Keyboard;
import com.heavybox.jtix.input.Mouse;
import com.heavybox.jtix.math.MathUtils;

// mountains, hills, rocks, trees?
public class ToolBrushRocks extends Tool {

    public Mode currentMode = Mode.values()[0];
    public int currentIndex = 0;
    public TexturePack layer3;
    public TextureRegion region;

    public ToolBrushRocks(Map map) {
        super(map);
        layer3 = Assets.get("assets/texture-packs/layer_3.yml");
        region = layer3.getRegion("assets/textures-layer-3/boulder_plain_big_0.png");
        sclX = 1f;
        sclY = 1f;
    }

    @Override
    public String getName() {
        return "Rocks Brush";
    }

    private void selectRandomIndex() {
        currentIndex = MathUtils.randomUniformInt(0,6);
        region = layer3.getRegion("assets/textures-layer-3/" + currentMode.name().toLowerCase() + "_" + currentIndex + ".png");
    }

    @Override
    public void update(float delta) {
        // input
        float verticalScroll = Input.mouse.getVerticalScroll();
        boolean leftJustPressed = Input.mouse.isButtonJustPressed(Mouse.Button.LEFT);
        boolean tabJustPressed = Input.keyboard.isKeyJustReleased(Keyboard.Key.TAB);

        float deltaScale = Input.keyboard.isKeyPressed(Keyboard.Key.S) ? -Input.mouse.getYDelta() / (Graphics.getWindowHeight() * 0.3f) : 0;
        sclX += deltaScale;
        sclY += deltaScale;
        sclX = MathUtils.clampFloat(sclX, 0.25f, 2.5f);
        sclY = MathUtils.clampFloat(sclY, 0.25f, 2.5f);

        // tool - settings: mode
        if (verticalScroll > 0) {
            Mode[] values = Mode.values();
            int index = currentMode.ordinal();
            currentMode = values[(index + 1) % values.length];
            selectRandomIndex();
            return;
        } else if (verticalScroll < 0) {
            Mode[] values = Mode.values();
            int prevIndex = (currentMode.ordinal() - 1 + values.length) % values.length;
            currentMode = values[prevIndex];
            selectRandomIndex();
            return;
        }

        if (leftJustPressed) {
            CommandTokenCreate createMountain = new CommandTokenCreate(
                    3,
                    x,y,deg,sclX,sclY,true,
                    region
            );
            createMountain.anchor = true;
            map.addCommand(createMountain);
            float diff = MathUtils.randomUniformFloat(-0.05f, 0.05f);
            sclX += diff;
            sclY += diff;
            selectRandomIndex();
            return;
        }
    }

    @Override
    public void renderToolOverlay(Renderer2D renderer2D, float x, float y) {
        renderer2D.setColor(Color.WHITE);
        renderer2D.drawTextureRegion(region, x, y, deg, sclX, sclY);
    }

    @Override
    public void activate() {

    }

    @Override
    public void deactivate() {

    }

    public enum Mode {
        BOULDER_PLAIN_BIG,
        BOULDER_PLAIN_SMALL,

        BOULDER_GRASS_BIG,
        BOULDER_GRASS_SMALL,

        HILLS_GREEN,
        HILLS_BROWN,
    }

}
