package com.heavybox.jtix.assets;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.graphics.Texture;
import com.heavybox.jtix.graphics.TexturePack;
import org.yaml.snakeyaml.Yaml;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AssetLoaderTexturePack implements AssetLoader<TexturePack> {

    private Array<AssetDescriptor> dependencies;
    private String                 yamlString;

    @Override
    public void beforeLoad(String path, HashMap<String, Object> options) {
        if (!Assets.fileExists(path)) throw new AssetsException("File does not exist: " + path);
    }

    @Override
    public Array<AssetDescriptor> load(String path, final HashMap<String, Object> options) {
        yamlString = Assets.getFileContent(path);
        Yaml yaml = Assets.yaml();
        Map<String, Object> data = yaml.load(yamlString);

        /* get dependencies */
        List<Map<String, Object>> textures = (List<Map<String, Object>>) data.get("textures");
        dependencies = new Array<>(textures.size());
        for (Map<String, Object> texture : textures) {
            String fileName = (String) texture.get("file");
            Path directoryPath = Paths.get(path).getParent();
            String filePath = Paths.get(directoryPath.toString(), fileName).toString();
            dependencies.add(new AssetDescriptor(Texture.class, filePath, options));
        }

        return dependencies;
    }

    @Override
    public TexturePack afterLoad() {
        /* get Textures */
        Texture[] textures = new Texture[dependencies.size];
        for (int i = 0; i < textures.length; i++) {
            String path = dependencies.get(i).path;
            Texture texture = Assets.get(path);
            textures[i] = texture;
        }

        return new TexturePack(textures, yamlString);
    }

}
