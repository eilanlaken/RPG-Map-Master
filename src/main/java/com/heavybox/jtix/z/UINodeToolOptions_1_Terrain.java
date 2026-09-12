package com.heavybox.jtix.z;

import com.heavybox.jtix.assets.Assets;
import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Texture;
import com.heavybox.jtix.userinterface.*;
import com.heavybox.jtix.z.tools_new.Tool_new;

public class UINodeToolOptions_1_Terrain extends NodeGroup {

    public UINodeToolOptions_1_Terrain() {
        setLayoutVertical();
        useScrollbar = true;
        active = true; // deactivated by default
        width = 400;
        widthFitContent = false;
        heightFitContent = true;

        // TITLE
        NodeGraphics titleBackground = new NodeGraphics(360, 30);
        titleBackground.color = Color.valueOf("0075FF").toFloatBits();
        NodeText titleTool = new NodeText("Terrain Tool");
        titleTool.size = 21;
        titleBackground.childAdd(titleTool);
        childAdd(titleBackground);

        // MODE
        NodeText titleMode = new NodeText("Mode");
        titleMode.size = 18;
        childAdd(titleMode);
        NodeGroup modeOptions = new NodeGroup();
        modeOptions.setLayoutHorizontal();
        modeOptions.widthFitContent = true;
        modeOptions.heightFitContent = true;
        modeOptions.paddingTop = 0;
        modeOptions.paddingLeft = 0;
        modeOptions.paddingRight = 0;
        modeOptions.paddingBottom = 0;
        NodeOption optionAdd = new NodeOption(modeOptions.ID, Tool_new.Mode.ADD);
        NodeText optionAddText = new NodeText("Add");
        optionAddText.size = 14;
        modeOptions.childAdd(optionAdd);
        modeOptions.childAdd(optionAddText);
        NodeOption optionSub = new NodeOption(modeOptions.ID, Tool_new.Mode.SUB);
        NodeText optionSubText = new NodeText("Subtract");
        optionSubText.size = 14;
        modeOptions.childAdd(optionSub);
        modeOptions.childAdd(optionSubText);
        childAdd(modeOptions);

        // SHAPE
        NodeText titleShape = new NodeText("Shape");
        titleShape.size = 18;
        childAdd(titleShape);
        NodeGroup modeShape = new NodeGroup();
        modeShape.setLayoutHorizontal();
        modeShape.widthFitContent = true;
        modeShape.heightFitContent = true;
        modeShape.paddingTop = 0;
        modeShape.paddingLeft = 0;
        modeShape.paddingRight = 0;
        modeShape.paddingBottom = 0;
        NodeOption optionShapePoint = new NodeOption(modeShape.ID, Tool_new.Shape.POINT);
        NodeText optionShapePointText = new NodeText("Point");
        optionShapePointText.size = 14;
        modeShape.childAdd(optionShapePoint);
        modeShape.childAdd(optionShapePointText);
        NodeOption optionShapeLine = new NodeOption(modeShape.ID, Tool_new.Shape.LINE);
        NodeText optionShapeLineText = new NodeText("Line");
        optionShapeLineText.size = 14;
        modeShape.childAdd(optionShapeLine);
        modeShape.childAdd(optionShapeLineText);
        NodeOption optionShapeCircle = new NodeOption(modeShape.ID, Tool_new.Shape.CIRCLE);
        NodeText optionShapeCircleText = new NodeText("Circle");
        optionShapeCircleText.size = 14;
        modeShape.childAdd(optionShapeCircle);
        modeShape.childAdd(optionShapeCircleText);
        NodeOption optionShapePolygon = new NodeOption(modeShape.ID, Tool_new.Shape.POLYGON);
        NodeText optionShapePolygonText = new NodeText("Polygon");
        optionShapePolygonText.size = 14;
        modeShape.childAdd(optionShapePolygon);
        modeShape.childAdd(optionShapePolygonText);
        childAdd(modeShape);

        // TARGET

        // TYPE
        NodeText titleType = new NodeText("Type");
        titleType.size = 18;
        childAdd(titleType);
        NodeGroup typesGrid = new NodeGroup();
        typesGrid.setLayoutGrid(true, 4, 4, true);
        typesGrid.widthFitContent = true;
        typesGrid.heightFitContent = true;
        typesGrid.paddingTop = 0;
        typesGrid.paddingLeft = 0;
        typesGrid.paddingRight = 0;
        typesGrid.paddingBottom = 0;
        NodeGraphics type_1 = new NodeGraphics(60,60);
        type_1.setImage((Texture) Assets.get("assets/textures-layer-0/terrain_land_road_0.jpg"));
        typesGrid.childAdd(type_1);
        NodeGraphics type_2 = new NodeGraphics(60,60);
        type_2.setImage((Texture) Assets.get("assets/textures-layer-0/terrain_land_grass_0.jpg"));
        typesGrid.childAdd(type_2);
        NodeGraphics type_3 = new NodeGraphics(60,60);
        type_3.setImage((Texture) Assets.get("assets/textures-layer-0/terrain_land_parchment_0.jpg"));
        typesGrid.childAdd(type_3);
        NodeGraphics type_4 = new NodeGraphics(60,60);
        type_4.setImage((Texture) Assets.get("assets/textures-layer-0/terrain_land_parchment_1.jpg"));
        typesGrid.childAdd(type_4);
        NodeGraphics type_5 = new NodeGraphics(60,60);
        type_5.setImage((Texture) Assets.get("assets/textures-layer-0/terrain_land_grass_1.jpg"));
        typesGrid.childAdd(type_5);
        NodeGraphics type_6 = new NodeGraphics(60,60);
        type_6.setImage((Texture) Assets.get("assets/textures-layer-0/terrain_land_dirt_0.jpg"));
        typesGrid.childAdd(type_6);
        NodeGraphics type_7 = new NodeGraphics(60,60);
        type_7.setImage((Texture) Assets.get("assets/textures-layer-0/terrain_land_dirt_1.jpg"));
        typesGrid.childAdd(type_7);
        NodeGraphics type_8 = new NodeGraphics(60,60);
        type_8.setImage((Texture) Assets.get("assets/textures-layer-0/terrain_land_stone_0.jpg"));
        typesGrid.childAdd(type_8);
        NodeGraphics type_9 = new NodeGraphics(60,60);
        type_9.setImage((Texture) Assets.get("assets/textures-layer-0/terrain_liquid_water_0.jpg"));
        typesGrid.childAdd(type_9);
        NodeGraphics type_10 = new NodeGraphics(60,60);
        type_10.setImage((Texture) Assets.get("assets/textures-layer-0/terrain_liquid_water_1.jpg"));
        typesGrid.childAdd(type_10);
        NodeGraphics type_11 = new NodeGraphics(60,60);
        type_11.setImage((Texture) Assets.get("assets/textures-layer-0/terrain_liquid_water_2.jpg"));
        typesGrid.childAdd(type_11);
        NodeGraphics type_12 = new NodeGraphics(60,60);
        type_12.setImage((Texture) Assets.get("assets/textures-layer-0/terrain_liquid_water_3.jpg"));
        typesGrid.childAdd(type_12);

        NodeGraphics typeSelectionIndicator = new NodeGraphics(14,14);
        typeSelectionIndicator.color = Color.valueOf("0075FF").toFloatBits();
        typeSelectionIndicator.anchor = Anchor.PARENT_TOP_LEFT;
        typeSelectionIndicator.transform.x = 4;
        typeSelectionIndicator.transform.y = -4;
        type_4.childAdd(typeSelectionIndicator);

        childAdd(typesGrid);

        // TRANSFORM
        NodeText titleTransform = new NodeText("Transform");
        titleTransform.size = 18;
        childAdd(titleTransform);
        NodeGroup angleOptions = new NodeGroup();
        angleOptions.setLayoutHorizontal();
        angleOptions.widthFitContent = true;
        angleOptions.heightFitContent = true;
        angleOptions.paddingTop = 0;
        angleOptions.paddingLeft = 0;
        angleOptions.paddingRight = 0;
        angleOptions.paddingBottom = 0;
        NodeOption optionRandom = new NodeOption(angleOptions.ID, true);
        NodeText optionRandomText = new NodeText("random angle");
        optionRandomText.size = 14;
        angleOptions.childAdd(optionRandom);
        angleOptions.childAdd(optionRandomText);
        NodeOption optionFollowPath = new NodeOption(angleOptions.ID, false);
        NodeText optionFollowPathText = new NodeText("angle follow path");
        optionFollowPathText.size = 14;
        angleOptions.childAdd(optionFollowPath);
        angleOptions.childAdd(optionFollowPathText);
        childAdd(angleOptions);

        // SCALE
        NodeGroup scale = new NodeGroup();
        scale.setLayoutHorizontal();
        scale.widthFitContent = true;
        scale.heightFitContent = true;
        scale.paddingTop = 0;
        scale.paddingLeft = 0;
        scale.paddingRight = 0;
        scale.paddingBottom = 0;
        NodeText scaleText = new NodeText("scale: ");
        scaleText.size = 14;
        NodeSlider scaleSlider = new NodeSlider();
        NodeText scaleValueText = new NodeText("1.0");
        scaleValueText.size = 14;
        scale.childAdd(scaleText);
        scale.childAdd(scaleSlider);
        scale.childAdd(scaleValueText);
        childAdd(scale);
    }

}
