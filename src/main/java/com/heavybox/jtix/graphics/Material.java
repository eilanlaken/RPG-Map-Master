package com.heavybox.jtix.graphics;

import java.util.HashMap;

public class Material {

    public String  name        = null;
    public Shader  shader      = null;
    public boolean useLights   = true;
    public boolean transparent = false;

    public HashMap<String, Object> materialAttributes = new HashMap<>();

}
