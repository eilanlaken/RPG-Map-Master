package com.heavybox.jtix.assets;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.graphics.TexturePack;
import com.heavybox.jtix.widgets_4.Theme;

import java.util.HashMap;

public class AssetLoaderTheme implements AssetLoader<Theme> {

    @Override
    public void beforeLoad(String path, HashMap<String, Object> options) {

    }

    @Override
    public Array<AssetDescriptor> load(String path, HashMap<String, Object> options) {
        return null;
    }

    @Override
    public Theme afterLoad() {
        return null;
    }

}
