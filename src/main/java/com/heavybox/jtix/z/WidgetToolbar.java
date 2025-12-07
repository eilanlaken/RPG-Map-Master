package com.heavybox.jtix.z;

import com.heavybox.jtix.assets.Assets;
import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.TexturePack;
import com.heavybox.jtix.widgets_4.WidgetContainer;
import com.heavybox.jtix.widgets_4.WidgetImage;

public class WidgetToolbar extends WidgetContainer {

    public static final int BUTTON_SIZE = 42;

    public int selected = 0;

    public WidgetToolbar() {
        layout = Layout.VERTICAL;
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
        boxChildSpacingHorizontal = 5;

        anchor = Anchor.TOP_LEFT;
        anchorX = 0;
        anchorY = 200;

        addChildren();
    }

    private void addChildren() {
        TexturePack ui = Assets.get("assets/texture-packs/user-interface.yml");

        WidgetImage toolTerrain = new WidgetImage(ui.getRegion("assets/user-interface/tool-bar-terrain.png"));
        toolTerrain.boxBackgroundVisible = true;
        toolTerrain.boxBackgroundColor = Color.valueOf("101010");
        toolTerrain.boxBorderSize = 0;
        toolTerrain.imgWidth = BUTTON_SIZE;
        toolTerrain.imgHeight = BUTTON_SIZE;
        toolTerrain.onMouseLeftClick = e -> {

            return false;
        };
        toolTerrain.onMouseEnter = e -> {
            toolTerrain.boxBackgroundColor = Color.valueOf("2a2a2a");
            return false;
        };
        toolTerrain.onMouseLeave = e -> {
            toolTerrain.boxBackgroundColor = Color.valueOf("101010");
            return false;
        };


        WidgetImage toolNature = new WidgetImage(ui.getRegion("assets/user-interface/tool-bar-nature.png"));
        toolNature.boxBackgroundVisible = true;
        toolNature.boxBackgroundColor = Color.valueOf("101010");
        toolNature.boxBorderSize = 0;
        toolNature.imgWidth = BUTTON_SIZE;
        toolNature.imgHeight = BUTTON_SIZE;
        toolNature.onMouseLeftClick = e -> {

            return false;
        };
        toolNature.onMouseEnter = e -> {
            toolNature.boxBackgroundColor = Color.valueOf("2a2a2a");
            return false;
        };
        toolNature.onMouseLeave = e -> {
            toolNature.boxBackgroundColor = Color.valueOf("101010");
            return false;
        };

        WidgetImage toolArchitecture = new WidgetImage(ui.getRegion("assets/user-interface/tool-bar-architecture.png"));
        toolArchitecture.boxBackgroundVisible = true;
        toolArchitecture.boxBackgroundColor = Color.valueOf("101010");
        toolArchitecture.boxBorderSize = 0;
        toolArchitecture.imgWidth = BUTTON_SIZE;
        toolArchitecture.imgHeight = BUTTON_SIZE;
        toolArchitecture.onMouseLeftClick = e -> {

            return false;
        };
        toolArchitecture.onMouseEnter = e -> {
            toolArchitecture.boxBackgroundColor = Color.valueOf("2a2a2a");
            return false;
        };
        toolArchitecture.onMouseLeave = e -> {
            toolArchitecture.boxBackgroundColor = Color.valueOf("101010");
            return false;
        };

        WidgetImage toolProps = new WidgetImage(ui.getRegion("assets/user-interface/tool-bar-props.png"));
        toolProps.boxBackgroundVisible = true;
        toolProps.boxBackgroundColor = Color.valueOf("101010");
        toolProps.boxBorderSize = 0;
        toolProps.imgWidth = BUTTON_SIZE;
        toolProps.imgHeight = BUTTON_SIZE;
        toolProps.onMouseLeftClick = e -> {

            return false;
        };
        toolProps.onMouseEnter = e -> {
            toolProps.boxBackgroundColor = Color.valueOf("2a2a2a");
            return false;
        };
        toolProps.onMouseLeave = e -> {
            toolProps.boxBackgroundColor = Color.valueOf("101010");
            return false;
        };

        WidgetImage toolText = new WidgetImage(ui.getRegion("assets/user-interface/tool-bar-text.png"));
        toolText.boxBackgroundVisible = true;
        toolText.boxBackgroundColor = Color.valueOf("101010");
        toolText.boxBorderSize = 0;
        toolText.imgWidth = BUTTON_SIZE;
        toolText.imgHeight = BUTTON_SIZE;
        toolText.onMouseLeftClick = e -> {

            return false;
        };
        toolText.onMouseEnter = e -> {
            toolText.boxBackgroundColor = Color.valueOf("2a2a2a");
            return false;
        };
        toolText.onMouseLeave = e -> {
            toolText.boxBackgroundColor = Color.valueOf("101010");
            return false;
        };

        WidgetImage toolSelect = new WidgetImage(ui.getRegion("assets/user-interface/tool-bar-select.png"));
        toolSelect.boxBackgroundVisible = true;
        toolSelect.boxBackgroundColor = Color.valueOf("101010");
        toolSelect.boxBorderSize = 0;
        toolSelect.imgWidth = BUTTON_SIZE;
        toolSelect.imgHeight = BUTTON_SIZE;
        toolSelect.onMouseLeftClick = e -> {

            return false;
        };
        toolSelect.onMouseEnter = e -> {
            toolSelect.boxBackgroundColor = Color.valueOf("2a2a2a");
            return false;
        };
        toolSelect.onMouseLeave = e -> {
            toolSelect.boxBackgroundColor = Color.valueOf("101010");
            return false;
        };

        WidgetImage toolDecorations = new WidgetImage(ui.getRegion("assets/user-interface/tool-bar-decorations.png"));
        toolDecorations.boxBackgroundVisible = true;
        toolDecorations.boxBackgroundColor = Color.valueOf("101010");
        toolDecorations.boxBorderSize = 0;
        toolDecorations.imgWidth = BUTTON_SIZE;
        toolDecorations.imgHeight = BUTTON_SIZE;
        toolDecorations.onMouseLeftClick = e -> {

            return false;
        };
        toolDecorations.onMouseEnter = e -> {
            toolDecorations.boxBackgroundColor = Color.valueOf("2a2a2a");
            return false;
        };
        toolDecorations.onMouseLeave = e -> {
            toolDecorations.boxBackgroundColor = Color.valueOf("101010");
            return false;
        };

        addChild(toolTerrain);
        addChild(toolNature);
        addChild(toolArchitecture);
        addChild(toolProps);
        addChild(toolText);
        addChild(toolDecorations);
        addChild(toolSelect);
    }

    @Override
    protected void fixedUpdateContainer(float delta) {

    }
}
