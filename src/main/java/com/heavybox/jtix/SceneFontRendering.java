package com.heavybox.jtix;

import com.heavybox.jtix.application.Scene;
import com.heavybox.jtix.assets.Assets;
import com.heavybox.jtix.graphics.Font;
import com.heavybox.jtix.graphics.FrameBufferBinder;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.graphics.Texture;
import com.heavybox.jtix.widgets_4.Widgets;
import org.lwjgl.opengl.GL11;

public class SceneFontRendering implements Scene {

    private final Renderer2D renderer2D = new Renderer2D();
    private Texture texture;

    @Override
    public void setup() {
        Assets.loadTexture("assets/textures-layer-3/boulder_grass_big_2.png",Texture.FilterMag.LINEAR, Texture.FilterMin.LINEAR_MIPMAP_LINEAR, Texture.Wrap.MIRRORED_REPEAT, Texture.Wrap.MIRRORED_REPEAT, 1);
        Assets.finishLoading();
        texture = Assets.get("assets/textures-layer-3/boulder_grass_big_2.png");
    }

    @Override
    public void start() {
        //Renderer2D.calculateStringLineWidth("abcdefghijklmABCDEFGHIJKLMNOPQRSTUVWXYZ|,.:", Renderer2D.defaultFont, Widgets.themeTextSize, Widgets.themeTextAntialiasing);
    }

    @Override
    public void update() {
        FrameBufferBinder.bind(null);
        GL11.glClearColor(0.01f,0.01f,0.01f,1);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_STENCIL_BUFFER_BIT); // should probably clear the stencil

        Font f = renderer2D.defaultFont;

        renderer2D.begin();
        renderer2D.setFont(Widgets.themeTextFont);
        // TODO problem with page flipping
        //renderer2D.drawStringLine("abcdefghijklmnABCDEFGHIJKLMNOPQRSTUVWXYZ|,.:", Widgets.themeTextSize, Widgets.themeTextAntialiasing, 0,300,0,1,1);
        renderer2D.drawRectangleFilled(texture, 1920, 1080,0,0,0,1,1);
        //renderer2D.drawTexture(f.getPages().get(0), 0,0,0,1,1);
        //renderer2D.drawStringLine("a|,.:", Widgets.themeTextSize, Widgets.themeTextAntialiasing, 0,0,0,1,1);
        renderer2D.end();
    }

    @Override
    public void finish() {

    }
}
