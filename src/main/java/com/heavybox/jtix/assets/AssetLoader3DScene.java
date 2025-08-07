package com.heavybox.jtix.assets;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.collections.ArrayInt;
import com.heavybox.jtix.collections.MapObjectInt;
import com.heavybox.jtix.graphics.*;
import com.heavybox.jtix.math.Matrix4x4;
import com.heavybox.jtix.math.Vector3;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static com.heavybox.jtix.math.Matrix4x4.*;

// TODO: call .free() for AIMatrix4's
public class AssetLoader3DScene implements AssetLoader<Scene3D> {

    private final MapObjectInt<String> uniformNameTextureTypes = new MapObjectInt<>();
    private final Map<String, String>  namedColorParams        = new HashMap<>();
    private final Map<String, String>  namedProps              = new HashMap<>();

    private NodeData rootNodeData;
    private MeshData[] meshesData;
    private MaterialData[] materialsData;
    private String folderPath;
    private String texturesFolderPath;

    public AssetLoader3DScene() {
        // all possible material texture parameters
        uniformNameTextureTypes.put("u_texture_baseColor", Assimp.aiTextureType_BASE_COLOR);
        uniformNameTextureTypes.put("u_texture_diffuse", Assimp.aiTextureType_DIFFUSE);
        uniformNameTextureTypes.put("u_texture_normalMap", Assimp.aiTextureType_NORMALS);
        uniformNameTextureTypes.put("u_texture_opacity", Assimp.aiTextureType_OPACITY);
        uniformNameTextureTypes.put("u_texture_metalness", Assimp.aiTextureType_METALNESS);
        uniformNameTextureTypes.put("u_texture_roughness", Assimp.aiTextureType_SHININESS);

        // all possible material color parameters
        namedColorParams.put("u_color_diffuse", Assimp.AI_MATKEY_COLOR_DIFFUSE);

        // all possible material props (metallic, roughness etc)
        namedProps.put("u_prop_metallic", Assimp.AI_MATKEY_REFLECTIVITY);
        namedProps.put("u_prop_roughness", Assimp.AI_MATKEY_ROUGHNESS_FACTOR);
        namedProps.put("u_prop_opacity", Assimp.AI_MATKEY_OPACITY);

    }

    // TODO: make use of
    @Override
    public void beforeLoad(String path, HashMap<String, Object> options) {
        if (!Assets.fileExists(path)) throw new AssetsException("File does not exist: " + path);
    }

    @Override
    public Array<AssetDescriptor> load(String path, HashMap<String, Object> options) {
        this.texturesFolderPath = options != null ? (String) options.get("texturesFolderPath") : null;
        this.folderPath = Paths.get(path).getParent().toString();
        final int importFlags =

                Assimp.aiProcess_Triangulate |
                Assimp.aiProcess_ImproveCacheLocality |
                Assimp.aiProcess_GenBoundingBoxes |
                Assimp.aiProcess_CalcTangentSpace |
                Assimp.aiProcess_RemoveRedundantMaterials |
                Assimp.aiProcess_GenSmoothNormals
                ;


        AIScene aiScene = Assimp.aiImportFile(path, importFlags);

        // load meshes:
        PointerBuffer aiMeshes = aiScene.mMeshes();
        int numMeshes = aiScene.mNumMeshes();
        meshesData = new MeshData[numMeshes];
        for (int i = 0; i < numMeshes; i++) {
            AIMesh aiMesh = AIMesh.create(aiMeshes.get(i));
            final MeshData meshData = processMesh(aiMesh);
            meshesData[i] = meshData;
        }

        // load materials
        PointerBuffer aiMaterials  = aiScene.mMaterials();
        int numMaterials = aiScene.mNumMaterials();
        if (numMaterials != 0) {
            materialsData = new MaterialData[numMaterials];
            for (int i = 0; i < numMaterials; i++) {
                AIMaterial aiMaterial = AIMaterial.create(aiMaterials.get(i));
                final MaterialData materialData = processMaterial(aiMaterial);
                materialsData[i] = materialData;
            }
        }

        //AINode root = aiScene.mRootNode();
        //collectNodes(aiScene.mRootNode(),null, nodesData);
        rootNodeData = collectNodes(aiScene.mRootNode(), null);

        Array<AssetDescriptor> dependencies = new Array<>();
        // load textures as materials.
        if (materialsData != null) {
            for (MaterialData materialData : materialsData) {
                Array<MaterialDataTexture> texturesData = materialData.texturesData;
                for (MaterialDataTexture textureData : texturesData) {
                    HashMap<String, Object> materialTextureOptions = new HashMap<>();
                    materialTextureOptions.put("uWrap", Texture.Wrap.REPEAT);
                    materialTextureOptions.put("vWrap", Texture.Wrap.REPEAT);
                    AssetDescriptor assetDescriptor = new AssetDescriptor(Texture.class, textureData.path, materialTextureOptions); // TODO options
                    dependencies.add(assetDescriptor);
                }
            }
        }

        return dependencies;
    }

    @Override
    public Scene3D afterLoad() {
        ModelMesh[] allSceneMeshes = new ModelMesh[meshesData.length];
        for (int i = 0; i < allSceneMeshes.length; i++) {
            MeshData meshData = meshesData[i];
            allSceneMeshes[i] = new ModelMesh(meshData.positions, meshData.textureCoords0, meshData.colors, meshData.normals, meshData.tangents, meshData.biTangents, meshData.indices, meshData.boundingSphereRadius);
        }

        ModelMaterial[] allDifferentMaterials = new ModelMaterial[materialsData.length];
        for (int i = 0; i < allDifferentMaterials.length; i++) {
            MaterialData materialData = materialsData[i];
            ModelMaterial modelMaterial = convertMaterialDataToPBRModelMaterial(materialData);
            allDifferentMaterials[i] = modelMaterial;
        }

        Scene3D scene = new Scene3D();
        scene.allMaterials = allDifferentMaterials;
        scene.allMeshes = allSceneMeshes;
        scene.root = buildNodeTree(allSceneMeshes, allDifferentMaterials, rootNodeData, null);
        scene.allNodes = getAllNodesAsArray(scene.root);
        scene.namedNodes = getNamedNodesMap(scene.allNodes);

        return scene;
    }

    private ModelMaterial convertMaterialDataToPBRModelMaterial(final AssetLoader3DScene.MaterialData materialData) {
        ModelMaterial material = new ModelMaterial();

        material.name = materialData.name;
        // add all the textures
        for (AssetLoader3DScene.MaterialDataTexture textureData : materialData.texturesData) {
            Texture texture = Assets.get(textureData.path);
            material.materialAttributes.put(textureData.uniform, texture);
        }
        // add all the colors
        for (AssetLoader3DScene.MaterialDataColor colorData : materialData.colorsData) {
            Color color = new Color(colorData.r, colorData.g, colorData.b, colorData.a);
            material.materialAttributes.put(colorData.uniform, color);
        }
        // add all the props (metallic, roughness etc.).
        for (AssetLoader3DScene.MaterialDataProp propData : materialData.propsData) {
            String uniform = propData.uniform;
            float value = propData.value;
            material.materialAttributes.put(uniform, value);
        }

        // determine if material is fully opaque or uses transparency
        boolean transparent = false;
        if (material.materialAttributes.get("u_prop_opacity") != null) {
            float opacityValue = (Float) material.materialAttributes.get("u_prop_opacity");
            if (opacityValue < 1.0f) transparent = true;
        }
        Texture opacityTexture = (Texture) material.materialAttributes.get("u_texture_opacity");
        if (opacityTexture != null) {
            transparent = true;
        }
        material.transparent = transparent;

        //if (true) return material;
        // in order to make sure a PBR material has all required uniforms, we check for missing attributes and "fill" them with default values
        /* make sure diffuse texture is available */
        Texture texture_diffuse = (Texture) material.materialAttributes.get("u_texture_diffuse");
        Color color_diffuse = (Color) material.materialAttributes.get("u_color_diffuse");
        if (texture_diffuse != null) {
            material.materialAttributes.put("u_color_diffuse", Color.WHITE.clone());
        } else if (color_diffuse != null) {
            material.materialAttributes.put("u_texture_diffuse", Graphics.getTextureSingleWhitePixel());
        }

        /* make sure normal map value (texture) is available */
        Texture texture_normalMap = (Texture) material.materialAttributes.get("u_texture_normalMap");
        if (texture_normalMap == null) { // missing normal map, use default
            material.materialAttributes.put("u_texture_normalMap", Graphics.getTextureSinglePixelNormalMap());
        }

        /* make sure metallic map (texture) is available */
        Texture texture_metallicMap = (Texture) material.materialAttributes.get("u_texture_metalness");
        Float metallic = (Float) material.materialAttributes.get("u_prop_metallic");
        if (texture_metallicMap != null) {
            material.materialAttributes.put("u_prop_metallic", 1);
        } else if (metallic != null) {
            material.materialAttributes.put("u_texture_metalness", Graphics.getTextureSingleWhitePixel());
        }

        /* make sure roughness value is available */
        Texture texture_roughnessMap = (Texture) material.materialAttributes.get("u_texture_roughness");
        Float roughness = (Float) material.materialAttributes.get("u_prop_roughness");
        if (texture_roughnessMap != null) {
            material.materialAttributes.put("u_prop_roughness", 1);
        } else if (roughness != null) {
            material.materialAttributes.put("u_texture_roughness", Graphics.getTextureSingleWhitePixel());
        }

        /* make sure opacity map is available */
        Texture texture_opacity = (Texture) material.materialAttributes.get("u_texture_opacity");
        Float opacity = (Float) material.materialAttributes.get("u_prop_opacity");
        if (texture_opacity != null) {
            material.materialAttributes.put("u_prop_opacity", 1);
        } else if (opacity != null) {
            material.materialAttributes.put("u_texture_opacity", Graphics.getTextureSingleWhitePixel());
        }

        return material;
    }

    private MaterialData processMaterial(final AIMaterial aiMaterial) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            MaterialData materialData = new MaterialData();

            AIString ai_name = AIString.calloc();
            if (Assimp.aiGetMaterialString(aiMaterial, Assimp.AI_MATKEY_NAME, 0, 0, ai_name) == Assimp.aiReturn_SUCCESS) {
                materialData.name = ai_name.dataString();
            }

            for (MapObjectInt.Entry<String> entry : uniformNameTextureTypes) {
                AIString ai_path = AIString.calloc();
                IntBuffer ai_mapping = stack.mallocInt(1);
                IntBuffer ai_uvIndex = stack.mallocInt(1);
                IntBuffer ai_op = stack.mallocInt(1);
                IntBuffer ai_mapMode = stack.mallocInt(1);
                FloatBuffer ai_blendMode = stack.mallocFloat(1);
                int result = Assimp.aiGetMaterialTexture(aiMaterial, entry.value, 0, ai_path, ai_mapping, ai_uvIndex, ai_blendMode, ai_op, ai_mapMode, null);
                if (result == Assimp.aiReturn_SUCCESS) {
                    MaterialDataTexture materialTexture = new MaterialDataTexture();
                    materialTexture.uniform = entry.key;
                    if (texturesFolderPath != null) {
                        Path base = Paths.get(texturesFolderPath);
                        String fileName = Paths.get(ai_path.dataString()).getFileName().toString();
                        Path fullPath = base.resolve(fileName);
                        materialTexture.path = fullPath.toString().replace("\\", "/");
                    } else {
                        Path base = Paths.get(folderPath);
                        Path fullPath = base.resolve(ai_path.dataString());
                        materialTexture.path = fullPath.toString().replace("\\", "/");
                    }
                    // ... TODO texture filters etc.
                    materialData.texturesData.add(materialTexture);
                }
            }

            AIColor4D aiColor = AIColor4D.create();
            for (Map.Entry<String, String> colorEntry : namedColorParams.entrySet()) {
                int result = Assimp.aiGetMaterialColor(aiMaterial, colorEntry.getValue(), Assimp.aiTextureType_NONE, 0, aiColor);
                if (result == Assimp.aiReturn_SUCCESS) {
                    MaterialDataColor colorData = new MaterialDataColor();
                    colorData.uniform = colorEntry.getKey();
                    colorData.r = aiColor.r();
                    colorData.g = aiColor.g();
                    colorData.b = aiColor.b();
                    colorData.a = aiColor.a();
                    materialData.colorsData.add(colorData);
                }
            }

            PointerBuffer pointerBuffer = stack.mallocPointer(1);
            for (Map.Entry<String, String> propEntry : namedProps.entrySet()) {
                int result = Assimp.aiGetMaterialProperty(aiMaterial, propEntry.getValue(), pointerBuffer);
                if (result == Assimp.aiReturn_SUCCESS) {
                    AIMaterialProperty property = AIMaterialProperty.create(pointerBuffer.get(0));
                    MaterialDataProp propData = new MaterialDataProp();
                    propData.uniform = propEntry.getKey();
                    propData.value = property.mData().asFloatBuffer().get();
                    materialData.propsData.add(propData);
                    // TODO: check if the data type is 4 bytes to handle custom material props (like booleans).
                }
            }

            return materialData;
        }
    }


    private MeshData processMesh(final AIMesh aiMesh) {
        MeshData meshData = new MeshData();
        meshData.materialIndex = aiMesh.mMaterialIndex();
        meshData.positions = getPositions(aiMesh);
        meshData.colors = getColors(aiMesh);
        meshData.textureCoords0 = getTextureCoords0(aiMesh);
        meshData.normals = getNormals(aiMesh);
        meshData.tangents = getTangents(aiMesh);
        meshData.biTangents = getBiTangents(aiMesh); // TODO: remove. This is calculated in the shader.
        meshData.indices = getIndices(aiMesh);
        meshData.vertexCount = getVertexCount(aiMesh);
        //meshData.boundingSphere = getBoundingSphere(aiMesh); TODO: remove this line
        // set bounding sphere radius
        AIAABB aiAABB = aiMesh.mAABB();
        AIVector3D min = aiAABB.mMin();
        AIVector3D max = aiAABB.mMax();
        Vector3 center = new Vector3();
        center.add(min.x(), min.y(), min.z());
        center.add(max.x(), max.y(), max.z());
        center.scl(0.5f);
        float radius = Vector3.dst(min.x(), min.y(), min.z(), max.x(), max.y(), max.z());
        meshData.boundingSphereRadius = radius + Vector3.len(center.x, center.y, center.z);
        return meshData;
    }

    private int getVertexCount(final AIMesh aiMesh) {
        int faceCount = aiMesh.mNumFaces();
        if (faceCount > 0) return faceCount * 3;
        else return aiMesh.mNumVertices();
    }

    // TODO: see what to do with the transform. This only works when the transform is applied.
    private float[] getPositions(final AIMesh aiMesh) {
        AIVector3D.Buffer positionsBuffer = aiMesh.mVertices();
        float[] positions = new float[aiMesh.mVertices().limit() * 3];
        for (int i = 0; i < positionsBuffer.limit(); i++) {
            AIVector3D vector3D = positionsBuffer.get(i);
            positions[3*i] = vector3D.x();
            positions[3*i+1] = vector3D.y();
            positions[3*i+2] = vector3D.z();
        }
        return positions;
    }

    @Deprecated
    private float[] getColors_old(final AIMesh mesh) {
        AIColor4D.Buffer colorsBuffer = mesh.mColors(0);
        if (colorsBuffer == null) return null;
        float[] colors = new float[colorsBuffer.limit() * 4];
        for (int i = 0; i < colorsBuffer.limit(); i++) {
            AIColor4D color = colorsBuffer.get(i);
            colors[4*i] = color.r();
            colors[4*i+1] = color.g();
            colors[4*i+2] = color.b();
            colors[4*i+3] = color.a();
        }
        return colors;
    }

    private float[] getColors(final AIMesh mesh) {
        AIColor4D.Buffer colorsBuffer = mesh.mColors(0);
        if (colorsBuffer == null) return null;
        float[] colors = new float[colorsBuffer.limit()];
        for (int i = 0; i < colorsBuffer.limit(); i++) {
            AIColor4D color = colorsBuffer.get(i);
            colors[i] = Color.toFloatBits(color.r(), color.g(), color.b(), color.a());
        }
        return colors;
    }

    private float[] getTextureCoords0(final AIMesh mesh) {
        AIVector3D.Buffer textureCoordinatesBuffer = mesh.mTextureCoords(0);
        if (textureCoordinatesBuffer == null) return null;
        float[] textureCoordinates0 = new float[mesh.mVertices().limit() * 2];
        for (int i = 0; i < textureCoordinatesBuffer.limit(); i++) {
            AIVector3D coordinates = textureCoordinatesBuffer.get(i);
            textureCoordinates0[2*i] = coordinates.x();
            textureCoordinates0[2*i+1] = 1 - coordinates.y();
        }
        return textureCoordinates0;
    }

    // TODO: pack and normalize
    private float[] getNormals(final AIMesh mesh) {
        AIVector3D.Buffer normalsBuffer = mesh.mNormals();
        if (normalsBuffer == null) return null;
        float[] normals = new float[normalsBuffer.limit() * 3];
        for (int i = 0; i < normalsBuffer.limit(); i++) {
            AIVector3D vector3D = normalsBuffer.get(i);
            normals[3*i] = vector3D.x();
            normals[3*i+1] = vector3D.y();
            normals[3*i+2] = vector3D.z();
        }
        return normals;
    }

    // TODO: pack and normalize
    private float[] getTangents(final AIMesh mesh) {
        AIVector3D.Buffer tangentsBuffer = mesh.mTangents();
        if (tangentsBuffer == null) return null;
        float[] tangents = new float[tangentsBuffer.limit() * 3];
        for (int i = 0; i < tangentsBuffer.limit(); i++) {
            AIVector3D vector3D = tangentsBuffer.get(i);
            tangents[3*i] = vector3D.x();
            tangents[3*i+1] = vector3D.y();
            tangents[3*i+2] = vector3D.z();
        }
        return tangents;
    }

    // TODO: pack and normalize
    @Deprecated private float[] getBiTangents(final AIMesh mesh) {
        AIVector3D.Buffer biTangentsBuffer = mesh.mBitangents();
        if (biTangentsBuffer == null) return null;
        float[] biTangents = new float[biTangentsBuffer.limit() * 3];
        for (int i = 0; i < biTangentsBuffer.limit(); i++) {
            AIVector3D vector3D = biTangentsBuffer.get(i);
            biTangents[3*i] = vector3D.x();
            biTangents[3*i+1] = vector3D.y();
            biTangents[3*i+2] = vector3D.z();
        }
        return biTangents;
    }

    private int[] getIndices(final AIMesh aiMesh) {
        int faceCount = aiMesh.mNumFaces();
        if (faceCount <= 0) return null;
        AIFace.Buffer faces = aiMesh.mFaces();
        int[] indices = new int[faceCount * 3];
        for (int i = 0; i < faceCount; i++) {
            AIFace aiFace = faces.get(i);
            if (aiFace.mNumIndices() != 3) throw new IllegalArgumentException("Faces were not properly triangulated");
            indices[3*i] = aiFace.mIndices().get(0);
            indices[3*i + 1] = aiFace.mIndices().get(1);
            indices[3*i + 2] = aiFace.mIndices().get(2);
        }
        return indices;
    }

    private NodeData collectNodes(AINode node, NodeData parent) {
        if (node == null) return null;

        AIMatrix4x4 currentTransform = AIMatrix4x4.calloc();
        currentTransform.set(node.mTransformation());
        //if (parent != null) Assimp.aiMultiplyMatrix4(currentTransform, parent.matrix);

        NodeData nodeData = new NodeData();
        nodeData.parent = parent;
        nodeData.name = node.mName().dataString();
        nodeData.matrix = currentTransform;

        int numMeshes = node.mNumMeshes();
        IntBuffer meshIndices = node.mMeshes();
        for (int i = 0; i < numMeshes; i++) {
            int meshIndex = meshIndices.get(i);
            nodeData.meshes.add(meshIndex);
        }

        for (int i = 0; i < numMeshes; i++) {
            int meshIndex = nodeData.meshes.get(i);
            MeshData meshData = meshesData[meshIndex];
            nodeData.materials.add(meshData.materialIndex);
        }

        int numChildren = node.mNumChildren();
        nodeData.children = new NodeData[numChildren];
        PointerBuffer children = node.mChildren();
        for (int i = 0; i < numChildren; i++) {
            nodeData.children[i] = collectNodes(AINode.create(children.get(i)), nodeData);
        }

        return nodeData;
    }

    private Scene3D.Node buildNodeTree(ModelMesh[] allSceneMeshes, ModelMaterial[] allSceneMaterials, final NodeData nodeData, final Scene3D.Node parent) {
        if (nodeData == null) return null;

        Scene3D.Node node = new Scene3D.Node();
        node.parent = parent;
        node.name = nodeData.name;
        node.localTransform = convertToMatrix4x4(nodeData.matrix);
        ModelMesh[] nodeMeshes = new ModelMesh[nodeData.meshes.size];
        for (int i = 0; i < nodeData.meshes.size; i++) {
            nodeMeshes[i] = allSceneMeshes[nodeData.meshes.get(i)];
        }
        ModelMaterial[] nodeMaterials = new ModelMaterial[nodeData.materials.size];
        for (int i = 0; i < nodeData.materials.size; i++) {
            nodeMaterials[i] = allSceneMaterials[nodeData.materials.get(i)];
        }
        node.model = new Model(nodeMeshes, nodeMaterials);

        node.children = new Scene3D.Node[nodeData.children.length];
        for (int i = 0; i < nodeData.children.length; i++) {
            node.children[i] = buildNodeTree(allSceneMeshes, allSceneMaterials, nodeData.children[i], node);
        }

        return node;
    }

    private Scene3D.Node[] getAllNodesAsArray(Scene3D.Node root) {
        Array<Scene3D.Node> nodes = new Array<>();
        getAllNodesAsArrayHelper(root, nodes);
        nodes.pack();
        return nodes.toArray(Scene3D.Node.class);
    }

    private void getAllNodesAsArrayHelper(Scene3D.Node node, Array<Scene3D.Node> out) {
        if (node == null) return;
        out.add(node);
        if (node.children != null) {
            for (Scene3D.Node child : node.children) {
                getAllNodesAsArrayHelper(child, out);
            }
        }
    }

    private HashMap<String, Scene3D.Node> getNamedNodesMap(Scene3D.Node[] allNodes) {
        HashMap<String, Scene3D.Node> namedNodes = new HashMap<>();
        for (Scene3D.Node node : allNodes) {
            namedNodes.put(node.name, node);
        }
        return namedNodes;
    }

    private Matrix4x4 convertToMatrix4x4(AIMatrix4x4 aiMatrix4x4) {
        Matrix4x4 m = new Matrix4x4();
        m.val[M00] = aiMatrix4x4.a1(); m.val[M01] = aiMatrix4x4.a2(); m.val[M02] = aiMatrix4x4.a3(); m.val[M03] = aiMatrix4x4.a4(); // row 0
        m.val[M10] = aiMatrix4x4.b1(); m.val[M11] = aiMatrix4x4.b2(); m.val[M12] = aiMatrix4x4.b3(); m.val[M13] = aiMatrix4x4.b4(); // row 1
        m.val[M20] = aiMatrix4x4.c1(); m.val[M21] = aiMatrix4x4.c2(); m.val[M22] = aiMatrix4x4.c3(); m.val[M23] = aiMatrix4x4.c4(); // row 2
        m.val[M30] = aiMatrix4x4.d1(); m.val[M31] = aiMatrix4x4.d2(); m.val[M32] = aiMatrix4x4.d3(); m.val[M33] = aiMatrix4x4.d4(); // row 3
        return m;
    }

    private static class NodeData {

        public NodeData    parent;
        public String      name;
        public ArrayInt    meshes    = new ArrayInt();
        public ArrayInt    materials = new ArrayInt();
        public NodeData[]  children;
        public AIMatrix4x4 matrix; // TODO: free before returning

    }

    private static class MeshData {

        public int     vertexCount;
        public float[] positions;
        public float[] colors;
        public float[] textureCoords0;
        public float[] normals;
        public float[] tangents;
        public float[] biTangents;
        public int[]   indices;
        public float   boundingSphereRadius;
        public int     materialIndex;

    }

    private static class MaterialData {

        public String name;
        public Array<MaterialDataTexture> texturesData = new Array<>();
        public Array<MaterialDataColor>   colorsData   = new Array<>();
        public Array<MaterialDataProp>    propsData    = new Array<>();
        public boolean                    transparent  = false;

    }

    private static final class MaterialDataTexture {

        public String uniform;
        public String path;
        public int    uvIndex;
        public int    mapping;
        public int    mapMode;
        public int    op;
        public float  blendMode;

    }

    private static final class MaterialDataColor {

        public String uniform;
        public float  r, g, b, a;

    }

    private static final class MaterialDataProp {

        public String uniform;
        public float  value;

    }

}
