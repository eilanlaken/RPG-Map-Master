package com.heavybox.jtix.networking;

import java.awt.*;
import java.net.URI;

public class Networking {

    private Networking() {}

    public static boolean openWebpage(String url) {
        Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
        if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
            try {
                URI uri = new URI(url);
                desktop.browse(uri);
                return true;
            } catch (Exception e) {
                throw new NetworkingException(e.getMessage());
            }
        }
        return false;
    }

}
