package com.heavybox.jtix.z;

import com.heavybox.jtix.graphics.Camera;
import com.heavybox.jtix.input.Input;
import com.heavybox.jtix.math.Vector3;
import com.heavybox.jtix.widgets_4.WidgetContainer;
import com.heavybox.jtix.widgets_4.WidgetText;

public class WidgetStatisticsBar extends WidgetContainer {

    private final Vector3 screen = new Vector3(Input.mouse.getX(), Input.mouse.getY(), 0);

    private final WidgetText mousePosition = new WidgetText("");
    private final WidgetText cameraZoom = new WidgetText("");

    /* references */
    private final Camera camera;
    private float cameraZoomPrev;

    public WidgetStatisticsBar(final Camera camera) {
        this.camera = camera;

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

        addChild(mousePosition);
        addChild(cameraZoom);
    }

    @Override
    protected void fixedUpdateContainer(float delta) {
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
