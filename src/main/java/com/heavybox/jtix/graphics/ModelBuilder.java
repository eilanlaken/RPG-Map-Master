package com.heavybox.jtix.graphics;

// TODO
public final class ModelBuilder {

    private static final ModelBuilder builder = new ModelBuilder();

    private static boolean buildingMaterial = false;

    private ModelBuilder() {}

    /* material builder */

    public static ModelBuilder materialBegin() {
        if (buildingMaterial) throw new GraphicsException("Error: nesting ModelMaterialBuilder.begin() and ModelMaterialBuilder.end()" +
                " calls is not allowed. Must call ModelMaterialBuilder.end() before building a new material.");
        buildingMaterial = true;
        return builder;
    }

    // setDiffuse, setCustom etc.

    public static ModelMaterial materialEnd() {
        buildingMaterial = false;
        return null;
    }


    /* mesh builder */


    /* model builder */


    public static ModelBuilder begin() {
        return builder;
    }

    public static ModelBuilder add(final ModelMesh mesh, final ModelMaterial material) {
        return builder;
    }

    public static Model end() {
        return null;
    }

}
