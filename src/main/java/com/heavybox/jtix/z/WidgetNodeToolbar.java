package com.heavybox.jtix.z;

import com.heavybox.jtix.RPGMapMakerScene;
import com.heavybox.jtix.assets.Assets;
import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.TexturePack;
import com.heavybox.jtix.widgets_4.WidgetNodeContainerVertical;
import com.heavybox.jtix.widgets_4.WidgetNodeImage;

public class WidgetNodeToolbar extends WidgetNodeContainerVertical {

    public static final int BUTTON_SIZE = 44;

    /* references and state */
    public Map map;
    public int selected = 0;

    /* child node references */
    private WidgetNodeToolTip toolTip = new WidgetNodeToolTip();

    private WidgetNodeImage toolTerrain;
    private WidgetNodeImage toolWheatFields;
    private WidgetNodeImage toolNature;
    private WidgetNodeImage toolArchitecture;
    private WidgetNodeImage toolRocks;
    private WidgetNodeImage toolProps;
    private WidgetNodeImage toolText;
    private WidgetNodeImage toolSelect;
    private WidgetNodeImage toolDecorations;

    public WidgetNodeToolbar(RPGMapMakerScene scene) {
        this.map = scene.getMap();

        layoutAddScrollbar = false;
        layoutWidthSizing = Sizing.DYNAMIC;
        layoutHeightSizing = Sizing.DYNAMIC;
        layoutOverflowX = Overflow.VISIBLE;
        layoutOverflowY = Overflow.VISIBLE;

        boxBackgroundVisible = true;
        boxBackgroundColor = Color.valueOf("0f0e0a");
        boxBorderSize = 0;
        boxPaddingTop = 0;
        boxPaddingBottom = 0;
        boxPaddingLeft = 0;
        boxPaddingRight = 0;
        boxChildSpacing = 5;

        anchor = Anchor.PARENT_TOP_LEFT;
        anchorX = 0;
        anchorY = 200;

        toolTip.anchor = Anchor.CURSOR_TOP_LEFT;
        toolTip.anchorX = 20;
        toolTip.hidden = true;
        onMouseEnter = e -> {
            toolTip.hidden = false;
        };
        onMouseLeave = e -> {
            toolTip.hidden = true;
        };

        addChildren();
    }

    private void addChildren() {
        TexturePack ui = Assets.get("assets/texture-packs/user-interface.yml");

        toolTerrain = new WidgetNodeImage(ui.getRegion("assets/user-interface/tool-bar-terrain.png"));
        toolTerrain.boxBackgroundVisible = true;
        toolTerrain.boxBackgroundColor = Color.valueOf("101010");
        toolTerrain.boxBorderSize = 0;
        toolTerrain.imgWidth = BUTTON_SIZE;
        toolTerrain.imgHeight = BUTTON_SIZE;
        toolTerrain.onMouseLeftClick = e -> {

        };
        toolTerrain.onMouseEnter = e -> {
            toolTerrain.boxBackgroundColor = Color.valueOf("2a2a2a");
            toolTip.tip.text = "Terrain Tool";
        };
        toolTerrain.onMouseLeave = e -> {
            toolTerrain.boxBackgroundColor = Color.valueOf("101010");
        };

        toolWheatFields = new WidgetNodeImage(ui.getRegion("assets/user-interface/tool-bar-wheat-fields.png"));
        toolWheatFields.boxBackgroundVisible = true;
        toolWheatFields.boxBackgroundColor = Color.valueOf("101010");
        toolWheatFields.boxBorderSize = 0;
        toolWheatFields.imgWidth = BUTTON_SIZE;
        toolWheatFields.imgHeight = BUTTON_SIZE;
        toolWheatFields.onMouseLeftClick = e -> {

        };
        toolWheatFields.onMouseEnter = e -> {
            toolWheatFields.boxBackgroundColor = Color.valueOf("2a2a2a");
            toolTip.tip.text = "Wheat Fields Tool";
        };
        toolWheatFields.onMouseLeave = e -> {
            toolWheatFields.boxBackgroundColor = Color.valueOf("101010");
        };


        toolNature = new WidgetNodeImage(ui.getRegion("assets/user-interface/tool-bar-nature.png"));
        toolNature.boxBackgroundVisible = true;
        toolNature.boxBackgroundColor = Color.valueOf("101010");
        toolNature.boxBorderSize = 0;
        toolNature.imgWidth = BUTTON_SIZE;
        toolNature.imgHeight = BUTTON_SIZE;
        toolNature.onMouseLeftClick = e -> {

        };
        toolNature.onMouseEnter = e -> {
            toolNature.boxBackgroundColor = Color.valueOf("2a2a2a");
            toolTip.tip.text = "Trees Tool";
        };
        toolNature.onMouseLeave = e -> {
            toolNature.boxBackgroundColor = Color.valueOf("101010");
        };

        toolArchitecture = new WidgetNodeImage(ui.getRegion("assets/user-interface/tool-bar-architecture.png"));
        toolArchitecture.boxBackgroundVisible = true;
        toolArchitecture.boxBackgroundColor = Color.valueOf("101010");
        toolArchitecture.boxBorderSize = 0;
        toolArchitecture.imgWidth = BUTTON_SIZE;
        toolArchitecture.imgHeight = BUTTON_SIZE;
        toolArchitecture.onMouseLeftClick = e -> {

        };
        toolArchitecture.onMouseEnter = e -> {
            toolArchitecture.boxBackgroundColor = Color.valueOf("2a2a2a");
            toolTip.tip.text = "Architecture Tool";
        };
        toolArchitecture.onMouseLeave = e -> {
            toolArchitecture.boxBackgroundColor = Color.valueOf("101010");
        };

        toolRocks = new WidgetNodeImage(ui.getRegion("assets/user-interface/tool-bar-rocks.png"));
        toolRocks.boxBackgroundVisible = true;
        toolRocks.boxBackgroundColor = Color.valueOf("101010");
        toolRocks.boxBorderSize = 0;
        toolRocks.imgWidth = BUTTON_SIZE;
        toolRocks.imgHeight = BUTTON_SIZE;
        toolRocks.onMouseLeftClick = e -> {

        };
        toolRocks.onMouseEnter = e -> {
            toolRocks.boxBackgroundColor = Color.valueOf("2a2a2a");
            toolTip.tip.text = "Rocks & Boulders Tool";
        };
        toolRocks.onMouseLeave = e -> {
            toolRocks.boxBackgroundColor = Color.valueOf("101010");
        };

        toolProps = new WidgetNodeImage(ui.getRegion("assets/user-interface/tool-bar-props.png"));
        toolProps.boxBackgroundVisible = true;
        toolProps.boxBackgroundColor = Color.valueOf("101010");
        toolProps.boxBorderSize = 0;
        toolProps.imgWidth = BUTTON_SIZE;
        toolProps.imgHeight = BUTTON_SIZE;
        toolProps.onMouseLeftClick = e -> {

        };
        toolProps.onMouseEnter = e -> {
            toolProps.boxBackgroundColor = Color.valueOf("2a2a2a");
            toolTip.tip.text = "Props Tool";
        };
        toolProps.onMouseLeave = e -> {
            toolProps.boxBackgroundColor = Color.valueOf("101010");
        };

        toolText = new WidgetNodeImage(ui.getRegion("assets/user-interface/tool-bar-text.png"));
        toolText.boxBackgroundVisible = true;
        toolText.boxBackgroundColor = Color.valueOf("101010");
        toolText.boxBorderSize = 0;
        toolText.imgWidth = BUTTON_SIZE;
        toolText.imgHeight = BUTTON_SIZE;
        toolText.onMouseLeftClick = e -> {

        };
        toolText.onMouseEnter = e -> {
            toolText.boxBackgroundColor = Color.valueOf("2a2a2a");
            toolTip.tip.text = "Text Tool";
        };
        toolText.onMouseLeave = e -> {
            toolText.boxBackgroundColor = Color.valueOf("101010");
        };

        toolSelect = new WidgetNodeImage(ui.getRegion("assets/user-interface/tool-bar-select.png"));
        toolSelect.boxBackgroundVisible = true;
        toolSelect.boxBackgroundColor = Color.valueOf("101010");
        toolSelect.boxBorderSize = 0;
        toolSelect.imgWidth = BUTTON_SIZE;
        toolSelect.imgHeight = BUTTON_SIZE;
        toolSelect.onMouseLeftClick = e -> {

        };
        toolSelect.onMouseEnter = e -> {
            toolSelect.boxBackgroundColor = Color.valueOf("2a2a2a");
            toolTip.tip.text = "Selection Tool";
        };
        toolSelect.onMouseLeave = e -> {
            toolSelect.boxBackgroundColor = Color.valueOf("101010");
        };

        toolDecorations = new WidgetNodeImage(ui.getRegion("assets/user-interface/tool-bar-decorations.png"));
        toolDecorations.boxBackgroundVisible = true;
        toolDecorations.boxBackgroundColor = Color.valueOf("101010");
        toolDecorations.boxBorderSize = 0;
        toolDecorations.imgWidth = BUTTON_SIZE;
        toolDecorations.imgHeight = BUTTON_SIZE;
        toolDecorations.onMouseLeftClick = e -> {

        };
        toolDecorations.onMouseEnter = e -> {
            toolDecorations.boxBackgroundColor = Color.valueOf("2a2a2a");
            toolTip.tip.text = "Decorations Tool";
        };
        toolDecorations.onMouseLeave = e -> {
            toolDecorations.boxBackgroundColor = Color.valueOf("101010");
        };

        addChild(toolTerrain);
        addChild(toolWheatFields);
        addChild(toolNature);
        addChild(toolArchitecture);
        addChild(toolRocks);
        addChild(toolProps);
        addChild(toolText);
        addChild(toolDecorations);
        addChild(toolSelect);
        addChild(toolTip);
    }

    @Override
    public void fixedUpdateContainer(float delta) {

    }
}
