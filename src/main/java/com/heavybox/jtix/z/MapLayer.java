package com.heavybox.jtix.z;

import com.heavybox.jtix.graphics.Camera;
import com.heavybox.jtix.graphics.FrameBuffer;

public class MapLayer {

    public int width;
    public int height;

    public MapLayerLevel_0_new layer0; // Terrain layer (wheat fields)
    public MapLayerLevel_1 layer1; // Ground layer (wheat fields)
    public MapLayerLevel_3 layer3; // Token layer
    public MapLayerLevel_4 layer5; // Token layer

    public FrameBuffer layerFrameBuffer;
    public Camera camera;

}
