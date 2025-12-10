package com.heavybox.jtix.z;

import com.heavybox.jtix.RPGMapMakerScene;
import com.heavybox.jtix.graphics.Camera;
import com.heavybox.jtix.input.Input;
import com.heavybox.jtix.math.Vector3;
import com.heavybox.jtix.widgets_4.NodeContainer;
import com.heavybox.jtix.widgets_4.NodeText;

public class NodeStatisticsBar extends NodeContainer {

    private final Vector3 screen = new Vector3(Input.mouse.getX(), Input.mouse.getY(), 0);

    private final NodeText objectCount = new NodeText("Objects: 0 |");
    private final NodeText mousePosition = new NodeText("");
    private final NodeText cameraZoom = new NodeText("");

    /* references */
    private final RPGMapMakerScene scene;
    private final Map map;
    private final Camera camera;
    private float cameraZoomPrev;
    private int objectCountPrev = 0;

    public NodeStatisticsBar(final RPGMapMakerScene scene) {
        this.scene = scene;
        this.map = scene.getMap();
        this.camera = scene.getCamera();

        layout = Layout.HORIZONTAL;
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
        boxChildSpacingHorizontal = 5;
        anchor = Anchor.TOP_RIGHT;
        anchorX = 50;
        anchorY = 50;

        addChild(objectCount);
        addChild(mousePosition);
        addChild(cameraZoom);
    }

    @Override
    protected void fixedUpdateContainer(float delta) {
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
