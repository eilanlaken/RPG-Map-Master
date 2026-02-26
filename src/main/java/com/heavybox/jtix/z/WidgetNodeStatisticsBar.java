package com.heavybox.jtix.z;

import com.heavybox.jtix.RPGMapMakerScene;
import com.heavybox.jtix.graphics.Camera;
import com.heavybox.jtix.input.Input;
import com.heavybox.jtix.math.Vector3;
import com.heavybox.jtix.widgets.WidgetNodeContainerHorizontal;
import com.heavybox.jtix.widgets.WidgetNodeText;

public class WidgetNodeStatisticsBar extends WidgetNodeContainerHorizontal {

    private final Vector3 screen = new Vector3(Input.mouse.getX(), Input.mouse.getY(), 0);

    private final WidgetNodeText objectCount = new WidgetNodeText("Objects: 0 |");
    private final WidgetNodeText mousePosition = new WidgetNodeText("");
    private final WidgetNodeText cameraZoom = new WidgetNodeText("");

    /* references */
    private final RPGMapMakerScene scene;
    private final Map map;
    private final Camera camera;
    private float cameraZoomPrev;
    private int objectCountPrev = 0;

    public WidgetNodeStatisticsBar(final RPGMapMakerScene scene) {
        this.scene = scene;
        this.map = scene.getMap();
        this.camera = scene.getCamera();

        layoutOverflowX = Overflow.VISIBLE;
        layoutOverflowY = Overflow.VISIBLE;
        layoutAddScrollbar = false;
        layoutWidthSizing = Sizing.DYNAMIC;
        layoutHeightSizing = Sizing.DYNAMIC;
        boxBackgroundVisible = false;
        boxBorderSize = 0;
        boxPaddingTop = 0;
        boxPaddingBottom = 0;
        boxPaddingLeft = 0;
        boxPaddingRight = 0;
        boxChildSpacing = 5;
        anchor = Anchor.PARENT_TOP_RIGHT;
        anchorX = 50;
        anchorY = 50;

        addChild(objectCount);
        addChild(mousePosition);
        addChild(cameraZoom);
    }

    @Override
    public void fixedUpdateContainer(float delta) {
        if (this.map.layer3.allTokens.size != objectCountPrev) {
            objectCountPrev = this.map.layer3.allTokens.size;
            objectCount.text = "Objects: " + this.map.layer3.allTokens.size + " |";
        }

        if (Input.mouse.moved()) {
            screen.set(Input.mouse.getX(), Input.mouse.getY(), 0);
            camera.unProject(screen);
            mousePosition.text = "x: " + (int) screen.x + " | y: " + (int) screen.y + " |";
        }

        if (camera.zoom != cameraZoomPrev) {
            cameraZoomPrev = camera.zoom;
            cameraZoom.text = "zoom: " + String.format("%.2f", camera.zoom) + " |";
        }
    }
}
