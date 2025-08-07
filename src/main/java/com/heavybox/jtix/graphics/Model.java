package com.heavybox.jtix.graphics;

import com.heavybox.jtix.memory.MemoryResource;

public class Model implements MemoryResource {

    public ModelMesh[]     meshes;
    public ModelMaterial[] materials;

    public Model(ModelMesh[] meshes, ModelMaterial[] materials) {
        this.meshes = meshes;
        this.materials = materials;
    }

    public Model(ModelMesh mesh, ModelMaterial material) {
        this.meshes = new ModelMesh[1];
        this.meshes[0] = mesh;
        this.materials = new ModelMaterial[1];
        this.materials[0] = material;
    }

    @Override
    public void delete() {

    }

}
