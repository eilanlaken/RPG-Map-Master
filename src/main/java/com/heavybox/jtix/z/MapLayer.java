package com.heavybox.jtix.z;

import com.heavybox.jtix.graphics.Renderer2D;
import com.heavybox.jtix.graphics.Texture;

public interface MapLayer {

    void executeCommand(Command command);
    void redraw(Renderer2D renderer2D);
    void applyChanges(Renderer2D renderer2D);
    Texture getTexture();

}
