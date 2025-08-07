package com.heavybox.jtix.application;

public final class ApplicationSettings implements Cloneable {

    public int     posX                   = -1;
    public int     posY                   = -1;
    public int     width                  = 640*2;
    public int     height                 = 480*2;
    public int     minWidth               = -1;
    public int     minHeight              = -1;
    public int     maxWidth               = -1;
    public int     maxHeight              = -1;
    public int     MSAA                   =  0; // TODO
    public boolean autoMinimized          = true;
    public boolean minimized              = false;
    public boolean maximized              = false;
    public String  iconPath               = null;
    public boolean visible                = true;
    public boolean fullScreen             = false;
    public String  title                  = "JTix Game";
    public boolean vSyncEnabled           = false;

    public boolean initialVisible         = true;
    public boolean resizable              = true;
    public boolean decorated              = true;
    public boolean floating               = false;
    public boolean transparentWindow      = false;

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

}
