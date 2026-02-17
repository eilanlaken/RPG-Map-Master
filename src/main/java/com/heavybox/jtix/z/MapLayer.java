package com.heavybox.jtix.z;

import com.heavybox.jtix.graphics.Camera;
import com.heavybox.jtix.graphics.FrameBuffer;

public class MapLayer {

    public int width;
    public int height;

    public MapLayerLevel_0_Terrain layer0; // Terrain and farmslands layer
    public MapLayerLevel_1_Tokens layer3; // Token layer

    public FrameBuffer layerFrameBuffer;
    public Camera camera;

}
