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

    public Type currentType = Type.values()[0];
    public int currentIndex = 0;
    public TexturePack layer3;
    public TextureRegion region;

    public ToolStampGround(Map map) {
        super(map);
        layer3 = Assets.get("assets/texture-packs/layer_3.yml");
        region = layer3.getRegion("assets/textures-layer-3/mountain-brown_0.png");
    }

    private void selectNextType() {
        Type[] values = Type.values();
        int index = currentType.ordinal();
        currentType = values[(index + 1) % values.length];
        region = layer3.getRegion("assets/textures-layer-3/mountain-" + currentType.name().toLowerCase() + "_" + currentIndex + ".png");
    }

    private void selectRandomIndex() {
        currentIndex = MathUtils.randomUniformInt(0,8);
        region = layer3.getRegion("assets/textures-layer-3/mountain-" + currentType.name().toLowerCase() + "_" + currentIndex + ".png");
    }

    @Override
    public void update(float delta) {
        if (Input.mouse.isButtonClicked(Mouse.Button.LEFT)) {
            System.out.println("clicked");
            CommandTokenCreate createMountain = new CommandTokenCreate(
              3,
              x,y,deg,sclX,sclY,true,
              region
            );
            map.addCommand(createMountain);
        }
        if (Input.mouse.isButtonClicked(Mouse.Button.RIGHT)) {
            selectNextType();
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

    public enum Type {
        MOUNTAIN_BROWN,
        MOUNTAIN_GREEN,
        MOUNTAIN_GREY,
        MOUNTAIN_OLIVE,

        HILLS_GREEN,
        HILLS_BROWN,

        STONES,
        ;
    }

}
