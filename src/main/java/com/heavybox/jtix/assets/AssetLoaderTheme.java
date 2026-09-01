package com.heavybox.jtix.assets;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.Font;
import com.heavybox.jtix.graphics.Texture;
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
    private String                 textFontPath;
    //private String                 groupTextureBackgroundPath;
    private Map<String, Object>    themeMap;

    @Override
    public Array<AssetDescriptor> load(String path, HashMap<String, Object> options) {
        yamlString = Assets.getFileContent(path);
        Yaml yaml = Assets.yaml();

        Map<String, Object> root = yaml.load(yamlString);
        themeMap = (Map<String, Object>) root.get("theme");
        texturePackPath = (String) themeMap.get("texturePackPath");

        Map<String, Object> text = (Map<String, Object>) themeMap.get("text");
        textFontPath = (String) text.get("textFontPath");

        Map<String, Object> scrollbar = (Map<String, Object>) themeMap.get("scrollbar");
        String scrollbarImageBarPath = (String) scrollbar.get("scrollbarImageBarPath");
        String scrollbarImageThumbPath = (String) scrollbar.get("scrollbarImageThumbPath");

        Map<String, Object> group = (Map<String, Object>) themeMap.get("group");
        String groupTextureBackgroundPath = (String) group.get("groupTextureBackgroundPath");

        dependencies = new Array<>(1);
        if (texturePackPath != null) dependencies.add(new AssetDescriptor(TexturePack.class, texturePackPath, options));
        if (textFontPath != null) dependencies.add(new AssetDescriptor(Font.class, textFontPath, options));
        if (groupTextureBackgroundPath != null) dependencies.add(new AssetDescriptor(Texture.class, groupTextureBackgroundPath, options));
        if (scrollbarImageBarPath != null) dependencies.add(new AssetDescriptor(Texture.class, scrollbarImageBarPath, options));
        if (scrollbarImageThumbPath != null) dependencies.add(new AssetDescriptor(Texture.class, scrollbarImageThumbPath, options));
        return dependencies;
    }

    @Override
    public Theme afterLoad() {
        Theme theme = new Theme();
        Map<String, Object> scrollbar = (Map<String, Object>) themeMap.get("scrollbar");
        Map<String, Object> group = (Map<String, Object>) themeMap.get("group");
        Map<String, Object> text = (Map<String, Object>) themeMap.get("text");
        Map<String, Object> slider = (Map<String, Object>) themeMap.get("slider");
        Map<String, Object> checkbox = (Map<String, Object>) themeMap.get("checkbox");

        if (texturePackPath != null) {
            TexturePack texturePack = Assets.get(texturePackPath);
            theme.texturePack = texturePack;
            theme.checkboxImageChecked = texturePack.getRegion((String) checkbox.get("checkboxImageCheckedPath"));
            theme.checkboxImageUnchecked = texturePack.getRegion((String) checkbox.get("checkboxImageUncheckedPath"));

            theme.sliderImageBackground = texturePack.getRegion((String) slider.get("sliderImageBackgroundPath"));
            theme.sliderImageFill = texturePack.getRegion((String) slider.get("sliderImageFillPath"));
            theme.sliderImageThumb = texturePack.getRegion((String) slider.get("sliderImageThumbPath"));
        }

        if (textFontPath != null) {
            theme.textFont = Assets.get(textFontPath);
        }

        Map<String, Object> color;
        Number value;

        // scrollbar
        String scrollbarImageBarPath = (String) scrollbar.get("scrollbarImageBarPath");
        if (scrollbarImageBarPath != null) theme.scrollbarImageBar = Assets.get(scrollbarImageBarPath);
        String scrollbarImageThumbPath = (String) scrollbar.get("scrollbarImageThumbPath");
        if (scrollbarImageThumbPath != null) theme.scrollbarImageThumb = Assets.get(scrollbarImageThumbPath);
        color = (Map<String, Object>) scrollbar.get("scrollbarColorBar");
        if (color != null) theme.scrollbarColorBar = colorFromYaml(color);
        color = (Map<String, Object>) scrollbar.get("scrollbarColorThumb");
        if (color != null) theme.scrollbarColorThumb = colorFromYaml(color);
        value = (Number) scrollbar.get("scrollbarThickness");
        if (value != null) theme.scrollbarThickness = value.floatValue();
        System.out.println(theme.scrollbarColorBar);
        System.out.println(theme.scrollbarColorThumb);

        // group
        String groupTextureBackgroundPath = (String) group.get("groupTextureBackgroundPath");
        if (groupTextureBackgroundPath != null) theme.groupTextureBackground = Assets.get(groupTextureBackgroundPath);

        // text
        color = (Map<String, Object>) text.get("textColor");
        if (color != null) theme.textColor = colorFromYaml(color);
        value = (Number) text.get("textSize");
        if (value != null) theme.textSize = value.intValue();
        value = (Number) text.get("textLineSpacing");
        if (value != null) theme.textLineSpacing = value.intValue();
        Boolean antialiasing = (Boolean) text.get("textAntialiasing");
        if (value != null) theme.textAntialiasing = antialiasing;

        // slider
        color = (Map<String, Object>) slider.get("sliderColorBackground");
        if (color != null) theme.sliderColorBackground = colorFromYaml(color);
        color = (Map<String, Object>) slider.get("sliderColorFill");
        if (color != null) theme.sliderColorFill = colorFromYaml(color);
        color = (Map<String, Object>) slider.get("sliderColorThumb");
        if (color != null) theme.sliderColorThumb = colorFromYaml(color);
        value = (Number) slider.get("sliderLength");
        if (value != null) theme.sliderLength = value.floatValue();
        value = (Number) slider.get("sliderThickness");
        if (value != null) theme.sliderThickness = value.floatValue();
        value = (Number) slider.get("sliderThumbSize");
        if (value != null) theme.sliderThumbSize = value.floatValue();

        // checkbox
        color = (Map<String, Object>) checkbox.get("checkboxColorBorderChecked");
        if (color != null) theme.checkboxColorBorderChecked = colorFromYaml(color);
        color = (Map<String, Object>) checkbox.get("checkboxColorBorderUnchecked");
        if (color != null) theme.checkboxColorBorderUnchecked = colorFromYaml(color);
        color = (Map<String, Object>) checkbox.get("checkboxColorCheckmarkBackground");
        if (color != null) theme.checkboxColorCheckmarkBackground = colorFromYaml(color);
        color = (Map<String, Object>) checkbox.get("checkboxColorCheckmark");
        if (color != null) theme.checkboxColorCheckmark = colorFromYaml(color);
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
