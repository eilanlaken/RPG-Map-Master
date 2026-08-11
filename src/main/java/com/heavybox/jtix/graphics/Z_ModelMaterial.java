package com.heavybox.jtix.graphics;

import com.heavybox.jtix.memory.MemoryResource;

import java.util.HashMap;
import java.util.Map;


// TODO: remove MemoryResource
public class Z_ModelMaterial implements MemoryResource, Cloneable {

    public String name = null;
    public Shader shader = null;
    public boolean useLights = true;
    public boolean transparent = false;
    public HashMap<String, Object> materialAttributes = new HashMap<>();

    // TODO: remove memory resource. This can cause a serious error when cleaning up materials that share textures.
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
    public Z_ModelMaterial clone() {
        try {
            // TODO: handle name
            // TODO: handle materialIndex
            // Shallow copy for shader, booleans, etc. (already handled by super.clone())
            // Deep copy materialAttributes
            Z_ModelMaterial cloned = (Z_ModelMaterial) super.clone();
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

    public static Z_ModelMaterial create() {
        return new Z_ModelMaterial();
    }

}
