package com.heavybox.jtix.assets;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.graphics.TexturePack;
import com.heavybox.jtix.widgets.Theme_old;
import org.yaml.snakeyaml.Yaml;

import java.util.HashMap;
import java.util.Map;

@Deprecated
public class AssetLoaderTheme_old implements AssetLoader<Theme_old> {

    private Array<AssetDescriptor> dependencies;
    private String                 yamlString;
    private String                 texturePackPath;
    private Map<String, Object>    data;
    private Map<String, Object>    themeMap;

    @Override
    public void beforeLoad(String path, HashMap<String, Object> options) {
        if (!Assets.fileExists(path)) throw new AssetsException("File does not exist: " + path);
    }

    @Override
    public Array<AssetDescriptor> load(String path, HashMap<String, Object> options) {
        yamlString = Assets.getFileContent(path);
        Yaml yaml = Assets.yaml();
        data = yaml.load(yamlString);

        Map<String, Object> root = yaml.load(yamlString);
        themeMap = (Map<String, Object>) root.get("theme");
        texturePackPath = (String) themeMap.get("texturePackPath");

        System.out.println(texturePackPath);
        dependencies = new Array<>(1);
        dependencies.add(new AssetDescriptor(TexturePack.class, texturePackPath, options));
        return dependencies;
    }

    @Override
    public Theme_old afterLoad() {
        /* get Textures */
        Theme_old theme = new Theme_old();

        if (texturePackPath != null) {
            TexturePack texturePack = Assets.get(texturePackPath);
            theme.texturePack = texturePack;

            Map<String, Object> checkbox = (Map<String, Object>) themeMap.get("checkbox");
            theme.themeCheckboxImageChecked = texturePack.getRegion((String) checkbox.get("themeCheckboxImageChecked"));
            theme.themeCheckboxImageUnchecked = texturePack.getRegion((String) checkbox.get("themeCheckboxImageUnchecked"));
            System.out.println(theme.themeCheckboxImageChecked);
        }

        return theme;
    }

}
