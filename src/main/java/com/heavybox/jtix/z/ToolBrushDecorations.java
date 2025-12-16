package com.heavybox.jtix.z;

import com.heavybox.jtix.assets.Assets;
import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.graphics.*;
import com.heavybox.jtix.input.Input;
import com.heavybox.jtix.input.Keyboard;
import com.heavybox.jtix.input.Mouse;
import com.heavybox.jtix.math.MathUtils;

// THIS IS JUST FOR THE DEMO
public class ToolBrushDecorations extends Tool {

    private Layer layer = Layer.FOREGROUND;
    Array<String> decorationsPaths = new Array<>();

    public Texture[] decorations;
    private int currentDecorationIndex = 0;

    private boolean flipped = false;

    public ToolBrushDecorations(Map map) {
        super(map);

        // TODO: put this back in
        if (true) return;

        Array<String> layer5Assets = Assets.getDirectoryFiles("assets/textures-layer-5", true);
        for (String layer5Asset : layer5Assets) {
            System.out.println(layer5Asset);
            if (layer5Asset.startsWith("assets/textures-layer-5/decorations_") || layer5Asset.startsWith("assets\\textures-layer-5\\decorations_")) {
                decorationsPaths.add(layer5Asset);
            }
        }

        decorations = new Texture[decorationsPaths.size];
        for (String decorationPath : decorationsPaths) {
            Assets.loadTexture(decorationPath);
        }
        Assets.finishLoading();
        for (int i = 0; i < decorationsPaths.size; i++) {
            String path = decorationsPaths.get(i);
            decorations[i] = Assets.get(path);
        }
    }

    @Override
    public String getName() {
        return "Decorations Brush";
    }

    @Override
    public void update(float delta) {
        // inputs
        float verticalScroll = Input.mouse.getVerticalScroll();
        boolean leftButtonClicked = Input.mouse.isButtonClicked(Mouse.Button.LEFT);
        boolean rightButtonClicked = Input.mouse.isButtonClicked(Mouse.Button.RIGHT);
        boolean keyAJustPressed = Input.keyboard.isKeyJustReleased(Keyboard.Key.A);
        boolean keyZJustPressed = Input.keyboard.isKeyJustReleased(Keyboard.Key.Z);
        boolean keyLeftShiftJustPressed = Input.keyboard.isKeyJustReleased(Keyboard.Key.LEFT_SHIFT);

        // tool settings
        float deltaScale = Input.keyboard.isKeyPressed(Keyboard.Key.S) ? -Input.mouse.getYDelta() / (Graphics.getWindowHeight() * 0.3f) : 0;
        sclX += deltaScale;
        sclY += deltaScale;
        sclX = MathUtils.clampFloat(sclX, 0.25f, 1.5f);
        sclY = MathUtils.clampFloat(sclY, 0.25f, 1.5f);

        if (keyLeftShiftJustPressed) {
            flipped = !flipped;
            return;
        }

        if (verticalScroll != 0) {
            deg += verticalScroll * 15;
            return;
        }

        if (rightButtonClicked) {
            layer = Layer.values()[(layer.ordinal() + 1) % Layer.values().length];
            System.out.println(layer);
            return;
        }

        if (keyAJustPressed) {
            currentDecorationIndex++;
            currentDecorationIndex %= decorations.length;
            return;
        }

        if (keyZJustPressed) {
            currentDecorationIndex--;
            if (currentDecorationIndex < 0) currentDecorationIndex = decorations.length - 1;
            return;
        }

        if (leftButtonClicked) {
            TextureRegion region = new TextureRegion(decorations[currentDecorationIndex]);
            CommandTokenCreate cmd = new CommandTokenCreate(layer == Layer.MIDDLE ? 3 : 5,
                    x, y, deg, flipped? -sclX : sclX, sclY, true, region);
            cmd.type = MapToken.Type.DECORATION;
            cmd.anchor = true;
            map.addCommand(cmd);
            return;
        }

    }

    @Override
    public void renderToolOverlay(Renderer2D renderer2D, float x, float y) {
        renderer2D.drawTexture(decorations[currentDecorationIndex], x, y, deg, flipped? -sclX : sclX, sclY);
        renderer2D.setColor(Color.RED);
        renderer2D.drawRectangleThin(decorations[currentDecorationIndex].width, decorations[currentDecorationIndex].height, x, y, deg, sclX, sclY);
        renderer2D.setColor(Color.WHITE);
    }

    @Override
    public void activate() {

    }

    @Override
    public void deactivate() {

    }

    private enum Layer {
        //BACKGROUND,
        MIDDLE,
        FOREGROUND,
        ;
    }

}
