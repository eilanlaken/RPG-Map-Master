package com.heavybox.jtix.z;

import com.heavybox.jtix.assets.Assets;
import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.graphics.Texture;
import com.heavybox.jtix.input.Input;
import com.heavybox.jtix.input.Mouse;

// THIS IS JUST FOR THE DEMO
public class ToolStampDecorations extends Tool {

    public Texture sun;

    public ToolStampDecorations(Map map) {
        super(map);
        sun = Assets.get("assets/textures-layer-5/decorations_sun.png");
    }

    @Override
    public void update(float delta) {
        // inputs
        float verticalScroll = Input.mouse.getVerticalScroll();
        boolean leftButtonClicked = Input.mouse.isButtonClicked(Mouse.Button.LEFT);
        boolean rightButtonClicked = Input.mouse.isButtonClicked(Mouse.Button.RIGHT);

        if (verticalScroll != 0) {
            deg += verticalScroll * 5;
            return;
        }

        if (leftButtonClicked) {
            
        }
    }

    @Override
    public void renderToolOverlay(Renderer2D renderer2D, float x, float y) {
        renderer2D.drawTexture(sun, x, y, deg, 1, 1);
        renderer2D.setColor(Color.RED);
        renderer2D.drawRectangleThin(sun.width, sun.height, x, y, deg, 1, 1);
        renderer2D.setColor(Color.WHITE);
    }

    @Override
    public void activate() {

    }

    @Override
    public void deactivate() {

    }
}
