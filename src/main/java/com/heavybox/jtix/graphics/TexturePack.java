package com.heavybox.jtix.graphics;

import com.heavybox.jtix.assets.Assets;
import com.heavybox.jtix.memory.MemoryResource;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.YAMLException;

import java.util.*;

// TODO: change name to Texture2DPack
// TODO: add string constructor.
public final class TexturePack implements MemoryResource {

    public final Texture[] textures; // TODO: delete.

    public final HashMap<String, TextureRegion> namedRegions;

    // TODO: continue.
    public TexturePack(final String path) {
        String yamlString = Assets.getFileContent(path);
        Yaml yaml = Assets.yaml();

        Map<String, Object> data = yaml.load(yamlString);

        // TODO
        textures = null;
        namedRegions = null;
    }

    @SuppressWarnings("unchecked")
    public TexturePack(Texture[] textures, String yamlString) {
        this.textures = textures;
        this.namedRegions = new HashMap<>();
        try {
            Yaml yaml = Assets.yaml();
            Map<String, Object> data = yaml.load(yamlString);
            List<Map<String, Object>> regions = (List<Map<String, Object>>) data.get("regions");
            for (Map<String, Object> regionData : regions) {
                String name = (String) regionData.get("name");
                Texture texture = textures[(int) regionData.get("textureIndex")];
                int offsetX = (int) regionData.get("offsetX");
                int offsetY = (int) regionData.get("offsetY");
                int originalWidth = (int) regionData.get("originalWidth");
                int originalHeight = (int) regionData.get("originalHeight");
                int packedWidth = (int) regionData.get("packedWidth");
                int packedHeight = (int) regionData.get("packedHeight");
                int x = (int) regionData.get("x");
                int y = (int) regionData.get("y");
                TextureRegion region = new TextureRegion(texture, x, y, offsetX, offsetY, packedWidth, packedHeight, originalWidth, originalHeight);
                namedRegions.put(name, region);
            }
        } catch (YAMLException e) {
            throw new GraphicsException("Failed to create " + TexturePack.class.getSimpleName() + " from invalid yaml: " + yamlString + "\n Error: " + e.getMessage());
        } catch (Exception e) {
            throw new GraphicsException("Failed to create " + TexturePack.class.getSimpleName() + "\n Error: " + e.getMessage());
        }
    }

    public TextureRegion getRegion(final String name) {
        final TextureRegion region = namedRegions.get(name);
        if (region == null) throw new RuntimeException("The " + TexturePack.class.getSimpleName() + " does not contain a region named " + name);
        return region;
    }

    @Override
    public void delete() {
        for (Texture texture : textures) {
            texture.delete();
        }
    }

    /* TODO: this is a replacement after we delete the textures array.
    @Override
    public void delete() {
        Set<Texture> allTextures = new HashSet<>();
        for (var entry : this.namedRegions.entrySet()) {
            Texture texture = entry.getValue().texture;
            allTextures.add(texture);
        }
        for (var t : allTextures) {
            t.delete();
        }
    }

     */

}
