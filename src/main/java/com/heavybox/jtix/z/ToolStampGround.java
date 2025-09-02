package com.heavybox.jtix.z;

import com.heavybox.jtix.assets.Assets;
import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.graphics.TexturePack;
import com.heavybox.jtix.graphics.TextureRegion;
import com.heavybox.jtix.input.Input;
import com.heavybox.jtix.input.Mouse;
import com.heavybox.jtix.math.MathUtils;

// mountains, hills, rocks, trees?
public class ToolStampGround extends Tool {

    public Mode currentMode = Mode.values()[0];
    public int currentIndex = 0;
    public TexturePack layer3;
    public TextureRegion region;

    public ToolStampGround(Map map) {
        super(map);
        layer3 = Assets.get("assets/texture-packs/layer_3.yml");
        region = layer3.getRegion("assets/textures-layer-3/mountains_brown_0.png");
        sclX = 0.5f;
        sclY = 0.5f;
    }

    private void nextMode() {
        Mode[] values = Mode.values();
        int index = currentMode.ordinal();
        currentMode = values[(index + 1) % values.length];
        region = layer3.getRegion("assets/textures-layer-3/mountain-" + currentMode.name().toLowerCase() + "_" + currentIndex + ".png");
    }

    private void selectRandomIndex() {
        if (currentMode == Mode.MOUNTAINS) {
            currentIndex = MathUtils.randomUniformInt(0, 3);
            region = layer3.getRegion("assets/textures-layer-3/mountains_brown_" + currentIndex + ".png");
        } else if (currentMode == Mode.HILLS_GREEN) {
            currentIndex = MathUtils.randomUniformInt(0, 6);
            region = layer3.getRegion("assets/textures-layer-3/hills_green_" + currentIndex + ".png");
        } else if (currentMode == Mode.HILLS_BROWN) {
            currentIndex = MathUtils.randomUniformInt(0, 6);
            region = layer3.getRegion("assets/textures-layer-3/hills_brown_" + currentIndex + ".png");
        } else if (currentMode == Mode.ROCK_BIG) {
            currentIndex = MathUtils.randomUniformInt(0, 10);
            region = layer3.getRegion("assets/textures-layer-3/rock-big_" + currentIndex + ".png");
        } else if (currentMode == Mode.ROCK_SMALL) {
            currentIndex = MathUtils.randomUniformInt(0, 6);
            region = layer3.getRegion("assets/textures-layer-3/rock-small_" + currentIndex + ".png");
        }
    }

    @Override
    public void update(float delta) {
        if (Input.mouse.isButtonClicked(Mouse.Button.LEFT)) {
            CommandTokenCreate createMountain = new CommandTokenCreate(
              3,
              x,y,deg,sclX,sclY,true,
              region
            );
            map.addCommand(createMountain);
            float diff = MathUtils.randomUniformFloat(-0.1f, 0.1f);
            sclX = 0.5f + diff;
            sclY = 0.5f + diff;
            selectRandomIndex();
        } else if (Input.mouse.getVerticalScroll() > 0) {
            Mode[] values = Mode.values();
            int index = currentMode.ordinal();
            currentMode = values[(index + 1) % values.length];
            selectRandomIndex();
        } else if (Input.mouse.getVerticalScroll() < 0) {
            Mode[] values = Mode.values();
            int prevIndex = (currentMode.ordinal() - 1 + values.length) % values.length;
            currentMode = values[prevIndex];
            selectRandomIndex();
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
        MOUNTAINS,

        HILLS_GREEN,
        HILLS_BROWN,

        ROCK_BIG,
        ROCK_SMALL,
        ;
    }

}
