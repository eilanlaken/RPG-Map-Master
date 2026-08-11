package com.heavybox.jtix.graphics;

import com.heavybox.jtix.memory.MemoryResource;

public class Z_Model implements MemoryResource {

    public Z_ModelMesh[]     meshes;
    public Z_ModelMaterial[] materials;

    public Z_Model(Z_ModelMesh[] meshes, Z_ModelMaterial[] materials) {
        this.meshes = meshes;
        this.materials = materials;
    }

    public Z_Model(Z_ModelMesh mesh, Z_ModelMaterial material) {
        this.meshes = new Z_ModelMesh[1];
        this.meshes[0] = mesh;
        this.materials = new Z_ModelMaterial[1];
        this.materials[0] = material;
    }

    @Override
    public void delete() {

    }

}
