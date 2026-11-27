package com.heavybox.jtix.z;

import com.heavybox.jtix.assets.Assets;
import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Texture;
import com.heavybox.jtix.userinterface.*;
import com.heavybox.jtix.z.tools_new.Tool_new;

public class UINodeToolOptions_1_Terrain extends UIGroup {

    public UINodeToolOptions_1_Terrain() {
        setLayoutVertical();
        useScrollbar = true;
        active = true; // deactivated by default
        width = 400;
        widthFitContent = false;
        heightFitContent = true;

        // TITLE
        UIGraphics titleBackground = new UIGraphics(360, 30);
        titleBackground.color = Color.valueOf("0075FF").toFloatBits();
        UIText titleTool = new UIText("Terrain Tool");
        titleTool.size = 21;
        titleBackground.childAdd(titleTool);
        childAdd(titleBackground);

        // MODE
        UIText titleMode = new UIText("Mode");
        titleMode.size = 18;
        childAdd(titleMode);
        UIGroup modeOptions = new UIGroup();
        modeOptions.setLayoutHorizontal();
        modeOptions.widthFitContent = true;
        modeOptions.heightFitContent = true;
        modeOptions.paddingTop = 0;
        modeOptions.paddingLeft = 0;
        modeOptions.paddingRight = 0;
        modeOptions.paddingBottom = 0;
        UIOption optionAdd = new UIOption(modeOptions.ID, Tool_new.Mode.ADD);
        UIText optionAddText = new UIText("Add");
        optionAddText.size = 14;
        modeOptions.childAdd(optionAdd);
        modeOptions.childAdd(optionAddText);
        UIOption optionSub = new UIOption(modeOptions.ID, Tool_new.Mode.SUB);
        UIText optionSubText = new UIText("Subtract");
        optionSubText.size = 14;
        modeOptions.childAdd(optionSub);
        modeOptions.childAdd(optionSubText);
        childAdd(modeOptions);

        // SHAPE
        UIText titleShape = new UIText("Shape");
        titleShape.size = 18;
        childAdd(titleShape);
        UIGroup modeShape = new UIGroup();
        modeShape.setLayoutHorizontal();
        modeShape.widthFitContent = true;
        modeShape.heightFitContent = true;
        modeShape.paddingTop = 0;
        modeShape.paddingLeft = 0;
        modeShape.paddingRight = 0;
        modeShape.paddingBottom = 0;
        UIOption optionShapePoint = new UIOption(modeShape.ID, Tool_new.Shape.POINT);
        UIText optionShapePointText = new UIText("Point");
        optionShapePointText.size = 14;
        modeShape.childAdd(optionShapePoint);
        modeShape.childAdd(optionShapePointText);
        UIOption optionShapeLine = new UIOption(modeShape.ID, Tool_new.Shape.LINE);
        UIText optionShapeLineText = new UIText("Line");
        optionShapeLineText.size = 14;
        modeShape.childAdd(optionShapeLine);
        modeShape.childAdd(optionShapeLineText);
        UIOption optionShapeCircle = new UIOption(modeShape.ID, Tool_new.Shape.CIRCLE);
        UIText optionShapeCircleText = new UIText("Circle");
        optionShapeCircleText.size = 14;
        modeShape.childAdd(optionShapeCircle);
        modeShape.childAdd(optionShapeCircleText);
        UIOption optionShapePolygon = new UIOption(modeShape.ID, Tool_new.Shape.POLYGON);
        UIText optionShapePolygonText = new UIText("Polygon");
        optionShapePolygonText.size = 14;
        modeShape.childAdd(optionShapePolygon);
        modeShape.childAdd(optionShapePolygonText);
        childAdd(modeShape);

        // TARGET

        // TYPE
        UIText titleType = new UIText("Type");
        titleType.size = 18;
        childAdd(titleType);
        UIGroup typesGrid = new UIGroup();
        typesGrid.setLayoutGrid(true, 4, 4, true);
        typesGrid.widthFitContent = true;
        typesGrid.heightFitContent = true;
        typesGrid.paddingTop = 0;
        typesGrid.paddingLeft = 0;
        typesGrid.paddingRight = 0;
        typesGrid.paddingBottom = 0;
        UIGraphics type_1 = new UIGraphics(60,60);
        type_1.setImage((Texture) Assets.get("assets/textures-layer-0/terrain_land_road_0.jpg"));
        typesGrid.childAdd(type_1);
        UIGraphics type_2 = new UIGraphics(60,60);
        type_2.setImage((Texture) Assets.get("assets/textures-layer-0/terrain_land_grass_0.jpg"));
        typesGrid.childAdd(type_2);
        UIGraphics type_3 = new UIGraphics(60,60);
        type_3.setImage((Texture) Assets.get("assets/textures-layer-0/terrain_land_parchment_0.jpg"));
        typesGrid.childAdd(type_3);
        UIGraphics type_4 = new UIGraphics(60,60);
        type_4.setImage((Texture) Assets.get("assets/textures-layer-0/terrain_land_parchment_1.jpg"));
        typesGrid.childAdd(type_4);
        UIGraphics type_5 = new UIGraphics(60,60);
        type_5.setImage((Texture) Assets.get("assets/textures-layer-0/terrain_land_grass_1.jpg"));
        typesGrid.childAdd(type_5);
        UIGraphics type_6 = new UIGraphics(60,60);
        type_6.setImage((Texture) Assets.get("assets/textures-layer-0/terrain_land_dirt_0.jpg"));
        typesGrid.childAdd(type_6);
        UIGraphics type_7 = new UIGraphics(60,60);
        type_7.setImage((Texture) Assets.get("assets/textures-layer-0/terrain_land_dirt_1.jpg"));
        typesGrid.childAdd(type_7);
        UIGraphics type_8 = new UIGraphics(60,60);
        type_8.setImage((Texture) Assets.get("assets/textures-layer-0/terrain_land_stone_0.jpg"));
        typesGrid.childAdd(type_8);
        UIGraphics type_9 = new UIGraphics(60,60);
        type_9.setImage((Texture) Assets.get("assets/textures-layer-0/terrain_liquid_water_0.jpg"));
        typesGrid.childAdd(type_9);
        UIGraphics type_10 = new UIGraphics(60,60);
        type_10.setImage((Texture) Assets.get("assets/textures-layer-0/terrain_liquid_water_1.jpg"));
        typesGrid.childAdd(type_10);
        UIGraphics type_11 = new UIGraphics(60,60);
        type_11.setImage((Texture) Assets.get("assets/textures-layer-0/terrain_liquid_water_2.jpg"));
        typesGrid.childAdd(type_11);
        UIGraphics type_12 = new UIGraphics(60,60);
        type_12.setImage((Texture) Assets.get("assets/textures-layer-0/terrain_liquid_water_3.jpg"));
        typesGrid.childAdd(type_12);

        UIGraphics typeSelectionIndicator = new UIGraphics(14,14);
        typeSelectionIndicator.color = Color.valueOf("0075FF").toFloatBits();
        typeSelectionIndicator.anchor = Anchor.PARENT_TOP_LEFT;
        typeSelectionIndicator.transform.x = 4;
        typeSelectionIndicator.transform.y = -4;
        type_4.childAdd(typeSelectionIndicator);

        childAdd(typesGrid);

        // TRANSFORM
        UIText titleTransform = new UIText("Transform");
        titleTransform.size = 18;
        childAdd(titleTransform);
        UIGroup angleOptions = new UIGroup();
        angleOptions.setLayoutHorizontal();
        angleOptions.widthFitContent = true;
        angleOptions.heightFitContent = true;
        angleOptions.paddingTop = 0;
        angleOptions.paddingLeft = 0;
        angleOptions.paddingRight = 0;
        angleOptions.paddingBottom = 0;
        UIOption optionRandom = new UIOption(angleOptions.ID, true);
        UIText optionRandomText = new UIText("random angle");
        optionRandomText.size = 14;
        angleOptions.childAdd(optionRandom);
        angleOptions.childAdd(optionRandomText);
        UIOption optionFollowPath = new UIOption(angleOptions.ID, false);
        UIText optionFollowPathText = new UIText("angle follow path");
        optionFollowPathText.size = 14;
        angleOptions.childAdd(optionFollowPath);
        angleOptions.childAdd(optionFollowPathText);
        childAdd(angleOptions);

        // SCALE
        UIGroup scale = new UIGroup();
        scale.setLayoutHorizontal();
        scale.widthFitContent = true;
        scale.heightFitContent = true;
        scale.paddingTop = 0;
        scale.paddingLeft = 0;
        scale.paddingRight = 0;
        scale.paddingBottom = 0;
        UIText scaleText = new UIText("scale: ");
        scaleText.size = 14;
        UISlider scaleSlider = new UISlider();
        UIText scaleValueText = new UIText("1.0");
        scaleValueText.size = 14;
        scale.childAdd(scaleText);
        scale.childAdd(scaleSlider);
        scale.childAdd(scaleValueText);
        childAdd(scale);
    }

}
