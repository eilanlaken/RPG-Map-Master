package com.heavybox.jtix.z;

import com.heavybox.jtix.assets.Assets;
import com.heavybox.jtix.graphics.FrameBuffer;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.graphics.Shader;
import com.heavybox.jtix.graphics.Texture;

public class MapLayer_0 implements MapLayer {

    private FrameBuffer layer0 = new FrameBuffer(1920, 1080);;

    private Texture terrainGrass;
    private Texture terrainWater;
    private Texture terrainSteepness;
    private Texture terrainRoad;

    public Texture terrainBrushSub;
    public Texture terrainBrushAdd;

    private Shader terrainShader;

    public MapLayer_0() {
        terrainGrass = Assets.get("assets/textures-layer-0/terrain-grass_1920x1080.png");
        terrainWater = Assets.get("assets/textures-layer-0/terrain-water_1920x1080.png");
        terrainSteepness = Assets.get("assets/textures-layer-0/terrain-rock_1920x1080.jpg");
        terrainBrushSub = new Texture("assets/tools/terrain-brush-erase.png");
        terrainBrushAdd = new Texture("assets/tools/terrain-brush-draw.png");

        String terrainVertexShaderSrc = Assets.getFileContent("assets/shaders/terrain-mask.vert");
        String terrainFragmentShaderSrc = Assets.getFileContent("assets/shaders/terrain-mask.frag");
        this.terrainShader = new Shader(terrainVertexShaderSrc, terrainFragmentShaderSrc);
    }

    @Override
    public void executeCommand(Command command) {

    }

    @Override
    public void redraw(Renderer2D renderer2D) {

    }

    @Override
    public void applyChanges(Renderer2D renderer2D) {

    }

    @Override
    public Texture getTexture() {
        return null;
    }
}
