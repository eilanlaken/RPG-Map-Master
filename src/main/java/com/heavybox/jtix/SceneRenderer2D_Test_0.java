package com.heavybox.jtix;

import com.heavybox.jtix.application.Scene;
import com.heavybox.jtix.assets.Assets;
import com.heavybox.jtix.graphics.*;
import com.heavybox.jtix.input.Input;
import com.heavybox.jtix.input.Mouse;
import com.heavybox.jtix.math.Matrix3x3;
import com.heavybox.jtix.widgets.*;
import org.lwjgl.opengl.GL11;

public class SceneRenderer2D_Test_0 implements Scene {

    public Renderer2D renderer2D = new Renderer2D();
    Camera camera = new Camera(Camera.Mode.ORTHOGRAPHIC, Graphics.getWindowWidth(), Graphics.getWindowHeight(), 1, 0, 100, 1);

    private TexturePack atlas;

    TextureRegion region;

    @Override
    public void start() {
        Assets.loadTexturePack("assets/texture-packs/layer_3.yml", Texture.FilterMag.NEAREST, Texture.FilterMin.LINEAR_MIPMAP_LINEAR, 4);
        Assets.finishLoading();

        atlas = Assets.get("assets/texture-packs/layer_3.yml");
        region = atlas.getRegion("assets/textures-layer-3/architecture_dwarf_house_diagonal_tall_7.png");
    }

    private WidgetNode currentRect;

    @Override
    public void update() {


        GL11.glClearColor(0.01f,0.01f,0.01f,1);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_STENCIL_BUFFER_BIT); // should probably clear the stencil

        Matrix3x3 perspective = new Matrix3x3();

        perspective.set(
                1.089951f,   0.236768f,  0.000000f,
                -0.064638f,   0.809154f,  0.000102f,
                -1.993454f, -11.710125f,  1.000000f
        );


        renderer2D.begin();
        // TODO: replace this with a new call with an appropriate matrix
        renderer2D.drawTextureRegion(region, perspective,0,0,0,0.3f,0.3f);
        //renderer2D.drawTextureRegion(region,0,0,0,1,1);
        renderer2D.end();
    }

    @Override
    public boolean isActive() {
        return false;
    }

    @Override
    public void finish() {

    }

}
