package com.heavybox.jtix;

import com.heavybox.jtix.graphics.Camera;
import com.heavybox.jtix.z.Map;

public interface RPGMapMakerScene {

    Camera getCamera();
    Map getMap();

    void undo();
    void redo();
    void saveAs(final String path);
    void exportAs(final String path);
    void load(final String path);

}
