package com.heavybox.jtix.assets;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.graphics.Shader;

import java.util.HashMap;

public class AssetLoaderShader implements AssetLoader<Shader> {

    private String vertexShaderSrcCode;
    private String geometryShaderSrcCode;
    private String fragmentShaderSrcCode;

    @Override
    public void beforeLoad(String path, HashMap<String, Object> options) {
        final String vertexShaderFilepath = (String) options.get("vertexShaderFilepath");
        final String geometryShaderFilepath = (String) options.get("geometryShaderFilepath");
        final String fragmentShaderFilepath = (String) options.get("fragmentShaderFilepath");

        if (!Assets.fileExists(vertexShaderFilepath))   throw new AssetsException("File does not exist: " + vertexShaderFilepath);
        if (geometryShaderFilepath != null) {
            if (!Assets.fileExists(geometryShaderFilepath))   throw new AssetsException("File does not exist: " + geometryShaderFilepath);
        }
        if (!Assets.fileExists(fragmentShaderFilepath)) throw new AssetsException("File does not exist: " + fragmentShaderFilepath);
    }

    @Override
    public Array<AssetDescriptor> load(String path, final HashMap<String, Object> options) {
        final String vertexShaderFilepath = (String) options.get("vertexShaderFilepath");
        final String geometryShaderFilepath = (String) options.get("geometryShaderFilepath");
        final String fragmentShaderFilepath = (String) options.get("fragmentShaderFilepath");
        vertexShaderSrcCode = Assets.getFileContent(vertexShaderFilepath);
        if (geometryShaderFilepath != null) {
            geometryShaderSrcCode = Assets.getFileContent(geometryShaderFilepath);
        }
        fragmentShaderSrcCode = Assets.getFileContent(fragmentShaderFilepath);
        return null;
    }

    @Override
    public Shader afterLoad() {
        // TODO: after refactoring Shader constructor just use the 3-args Shader constructor.
        if (geometryShaderSrcCode != null) {
            return new Shader(vertexShaderSrcCode, geometryShaderSrcCode, fragmentShaderSrcCode);
        }
        return new Shader(vertexShaderSrcCode, fragmentShaderSrcCode);
    }

}