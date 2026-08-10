package com.heavybox.jtix.assets;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.TexturePack;
import com.heavybox.jtix.userinterface.Theme;
import com.heavybox.jtix.widgets.Theme_old;
import org.yaml.snakeyaml.Yaml;

import java.util.HashMap;
import java.util.Map;

public class AssetLoaderTheme implements AssetLoader<Theme> {

    private Array<AssetDescriptor> dependencies;
    private String                 yamlString;
    private String                 texturePackPath;
    private Map<String, Object>    themeMap;

    @Override
    public Array<AssetDescriptor> load(String path, HashMap<String, Object> options) {
        yamlString = Assets.getFileContent(path);
        Yaml yaml = Assets.yaml();

        Map<String, Object> root = yaml.load(yamlString);
        themeMap = (Map<String, Object>) root.get("theme");
        texturePackPath = (String) themeMap.get("texturePackPath");

        dependencies = new Array<>(1);
        dependencies.add(new AssetDescriptor(TexturePack.class, texturePackPath, options));
        return dependencies;
    }

    @Override
    public Theme afterLoad() {
        Theme theme = new Theme();
        Map<String, Object> checkbox = (Map<String, Object>) themeMap.get("checkbox");

        if (texturePackPath != null) {
            TexturePack texturePack = Assets.get(texturePackPath);
            theme.texturePack = texturePack;
            theme.checkboxImageChecked = texturePack.getRegion((String) checkbox.get("checkboxImageCheckedPath"));
            theme.checkboxImageUnchecked = texturePack.getRegion((String) checkbox.get("checkboxImageUncheckedPath"));
        }

        Map<String, Object> color;

        color = (Map<String, Object>) checkbox.get("checkboxColorBorderChecked");
        if (color != null) theme.checkboxColorBorderChecked = colorFromYaml(color);
        color = (Map<String, Object>) checkbox.get("checkboxColorBorderUnchecked");
        if (color != null) theme.checkboxColorBorderUnchecked = colorFromYaml(color);
        color = (Map<String, Object>) checkbox.get("checkboxColorCheckmarkBackground");
        if (color != null) theme.checkboxColorCheckmarkBackground = colorFromYaml(color);
        color = (Map<String, Object>) checkbox.get("checkboxColorCheckmark");
        if (color != null) theme.checkboxColorCheckmark = colorFromYaml(color);

        Number value;
        value = (Number) checkbox.get("checkboxSize");
        if (value != null) theme.checkboxSize = value.floatValue();
        value = (Number) checkbox.get("checkboxSizeBorder");
        if (value != null) theme.checkboxSizeBorder = value.floatValue();

        return theme;
    }

    private Color colorFromYaml(Map<String, Object> map) {
        if (map == null) return null;
        return new Color(((Number) map.get("r")).floatValue(), ((Number) map.get("g")).floatValue(), ((Number) map.get("b")).floatValue(), ((Number) map.get("a")).floatValue());
    }

}
