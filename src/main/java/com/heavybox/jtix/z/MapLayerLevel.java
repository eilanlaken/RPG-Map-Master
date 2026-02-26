package com.heavybox.jtix.z;

import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.graphics.Texture2D;

public interface MapLayerLevel {

    void executeCommand(Command command);
    void redraw(Renderer2D renderer2D);
    void applyChanges(Renderer2D renderer2D);
    Texture2D getTexture();

    int getOrderIndex();

    // maybe only temporary functionality for simple undo implementation
    @Deprecated void clear();

}
