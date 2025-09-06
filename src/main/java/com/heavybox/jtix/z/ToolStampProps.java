package com.heavybox.jtix.z;

import com.heavybox.jtix.assets.Assets;
import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.graphics.TexturePack;
import com.heavybox.jtix.graphics.TextureRegion;
import com.heavybox.jtix.input.Input;
import com.heavybox.jtix.input.Mouse;

import java.util.HashSet;
import java.util.Set;

public class ToolStampProps extends Tool {

    public TexturePack layer3;
    public TextureRegion region;
    public int currentPropIndex = 0;

    public Array<String> propNames = new Array<>();

    public ToolStampProps(Map map) {
        super(map);
        sclX = 0.5f;
        sclY = 0.5f;
        layer3 = Assets.get("assets/texture-packs/layer_3.yml");
        for (String name : layer3.namedRegions.keySet()) {
            if (name.contains("prop")) propNames.add(name);
        }
        region = layer3.getRegion(propNames.get(currentPropIndex));
    }

    @Override
    public void update(float delta) {
        if (Input.mouse.isButtonClicked(Mouse.Button.LEFT)) {
            CommandTokenCreate createProp = new CommandTokenCreate(
                    3,
                    x, y, deg, sclX, sclY, true,
                    region
            );
            createProp.type = MapToken.Type.TREE;
            map.addCommand(createProp);
        } else if (Input.mouse.getVerticalScroll() > 0) {
            currentPropIndex++;
            System.out.println(currentPropIndex);
            region = layer3.getRegion(propNames.getCyclic(currentPropIndex));
        } else if (Input.mouse.getVerticalScroll() < 0) {
            currentPropIndex--;
            System.out.println(currentPropIndex);
            region = layer3.getRegion(propNames.getCyclic(currentPropIndex));
        }
    }

    @Override
    public void renderToolOverlay(Renderer2D renderer2D, float x, float y) {
        renderer2D.setColor(Color.WHITE);
        renderer2D.drawTextureRegion(region, x, y, 0, this.sclX, this.sclY);
    }

    @Override
    public void activate() {

    }

    @Override
    public void deactivate() {

    }

}
