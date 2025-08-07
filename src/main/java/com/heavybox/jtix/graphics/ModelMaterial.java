package com.heavybox.jtix.graphics;

import com.heavybox.jtix.memory.MemoryResource;

import java.util.HashMap;
import java.util.Map;


// TODO: make clone()able
public class ModelMaterial implements MemoryResource, Cloneable {

    public String name = null;
    public Shader shader = null;
    public boolean useLights = true;
    public boolean transparent = false;
    public HashMap<String, Object> materialAttributes = new HashMap<>();

    @Override
    public void delete() {
        for (Map.Entry<String, Object> attribute : materialAttributes.entrySet()) {
            Object data = attribute.getValue();
            if (data instanceof MemoryResource) { // this will effectively delete all the textures.
                MemoryResource resource = (MemoryResource) data;
                // Skip the Graphics.java managed Textures. They are deleted only by the engine, after the application closes.
                if (resource == Graphics.getTextureSingleWhitePixel()) continue;
                if (resource == Graphics.getTextureSingleBlackPixelTransparent()) continue;
                if (resource == Graphics.getTextureSingleBlackPixelOpaque()) continue;
                if (resource == Graphics.getTextureSinglePixelNormalMap()) continue;
                resource.delete();
            }
        }
    }

    // TODO: test
    @Override
    public ModelMaterial clone() {
        try {
            // TODO: handle name
            // TODO: handle materialIndex
            // Shallow copy for shader, booleans, etc. (already handled by super.clone())
            // Deep copy materialAttributes
            ModelMaterial cloned = (ModelMaterial) super.clone();
            cloned.materialAttributes = new HashMap<>(this.materialAttributes);
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(); // Should never happen
        }
    }

    @Override
    public String toString() {
        return name + ": ModelMaterial{" +
                materialAttributes +
                '}';
    }

    // TODO
    public static ModelMaterial createPBRMaterial() {
        ModelMaterial material = new ModelMaterial();
        material.materialAttributes.put("u_color_diffuse", Color.RED.clone()); // TODO
        material.materialAttributes.put("u_texture_diffuse", Graphics.getTextureSingleWhitePixel()); // TODO

        material.materialAttributes.put("u_prop_metallic", 0.04f); // TODO
        material.materialAttributes.put("u_texture_metalness", Graphics.getTextureSingleWhitePixel()); // TODO

        material.materialAttributes.put("u_prop_roughness", 0.8f); // TODO
        material.materialAttributes.put("u_texture_roughness", Graphics.getTextureSingleWhitePixel()); // TODO

        material.materialAttributes.put("u_prop_opacity", 1.0f); // TODO
        material.materialAttributes.put("u_texture_opacity", Graphics.getTextureSingleWhitePixel()); // TODO

        material.materialAttributes.put("u_texture_normalMap", Graphics.getTextureSinglePixelNormalMap()); // TODO
        return material;
    }

    public static ModelMaterial create() {
        return new ModelMaterial();
    }

}
