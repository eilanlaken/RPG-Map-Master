package com.heavybox.jtix.z;

import com.heavybox.jtix.assets.Assets;
import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.TexturePack;
import com.heavybox.jtix.input.Keyboard;
import com.heavybox.jtix.userinterface.Anchor;
import com.heavybox.jtix.userinterface.Node;
import com.heavybox.jtix.userinterface.NodeGroup;

public class UINodeToolbar extends NodeGroup {

    // state
    public int selected = 0;

    // (direct) children - buttons
    private final Array<UINodeToolbarButton> buttons = new Array<>(true, 9);
    private final UINodeToolbarButton terrain;
    private final UINodeToolbarButton nature;
    private final UINodeToolbarButton geology;
    private final UINodeToolbarButton props;
    private final UINodeToolbarButton architecture;
    private final UINodeToolbarButton decorations;
    private final UINodeToolbarButton text;
    private final UINodeToolbarButton procedural;
    private final UINodeToolbarButton select;

    public UINodeToolbar() {
        setLayoutVertical();

        this.heightFitContent = true;
        this.widthFitContent = true;
        this.width = 300;
        this.anchor = Anchor.PARENT_TOP_LEFT;
        this.transform.x = 40;
        this.transform.y = -140;

        TexturePack uiIconsPack = Assets.get("assets/texture-packs/user-interface.yml");

        terrain = new UINodeToolbarButton(0, uiIconsPack.getRegion("assets/user-interface/toolbar-icon-terrain.png"), "Terrain", "1");
        nature = new UINodeToolbarButton(1, uiIconsPack.getRegion("assets/user-interface/toolbar-icon-nature.png"), "Nature", "2");
        geology = new UINodeToolbarButton(2, uiIconsPack.getRegion("assets/user-interface/toolbar-icon-geology.png"), "Geology", "3");
        props = new UINodeToolbarButton(3, uiIconsPack.getRegion("assets/user-interface/toolbar-icon-props.png"), "Props", "4");
        architecture = new UINodeToolbarButton(4, uiIconsPack.getRegion("assets/user-interface/toolbar-icon-architecture.png"), "Architecture", "5");
        decorations = new UINodeToolbarButton(5, uiIconsPack.getRegion("assets/user-interface/toolbar-icon-decorations.png"), "Decorations", "6");
        text = new UINodeToolbarButton(6, uiIconsPack.getRegion("assets/user-interface/toolbar-icon-text.png"), "Text", "7");
        procedural = new UINodeToolbarButton(7, uiIconsPack.getRegion("assets/user-interface/toolbar-icon-procedural.png"), "Procedural", "8");
        select = new UINodeToolbarButton(8, uiIconsPack.getRegion("assets/user-interface/toolbar-icon-select.png"), "Select", "9");

        buttons.addAll(terrain, nature, geology, props, architecture, decorations, text, procedural, select);

        childAdd(terrain);
        childAdd(nature);
        childAdd(geology);
        childAdd(props);
        childAdd(architecture);
        childAdd(decorations);
        childAdd(text);
        childAdd(procedural);
        childAdd(select);

        onKeysJustPressed(e -> {
            if (e.keys.contains(Keyboard.Key.KEY_1, true)) selectTool(0);
            if (e.keys.contains(Keyboard.Key.KEY_2, true)) selectTool(1);
            if (e.keys.contains(Keyboard.Key.KEY_3, true)) selectTool(2);
            if (e.keys.contains(Keyboard.Key.KEY_4, true)) selectTool(3);
            if (e.keys.contains(Keyboard.Key.KEY_5, true)) selectTool(4);
            if (e.keys.contains(Keyboard.Key.KEY_6, true)) selectTool(5);
            if (e.keys.contains(Keyboard.Key.KEY_7, true)) selectTool(6);
            if (e.keys.contains(Keyboard.Key.KEY_8, true)) selectTool(7);
            if (e.keys.contains(Keyboard.Key.KEY_9, true)) selectTool(8);
        });

        selectTool(0);
        keyboardListen();
    }

    public void selectTool(int index) {
        this.selected = index;
        for (UINodeToolbarButton button : buttons) {
            button.colorBackground = button.selectIndex == index ? Color.valueOf("#0075FF") : Color.valueOf("#2B2D30");
        }
    }

}
