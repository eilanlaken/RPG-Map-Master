package com.heavybox.jtix.graphics;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.collections.Collections;
import com.heavybox.jtix.math.Matrix4x4;
import com.heavybox.jtix.math.Vector3;
import com.heavybox.jtix.memory.MemoryPool;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.stream.Collectors;

// TODO:
// draw model surface material ("regular" drawing.)
// draw model wireframe
// draw model volume (smoke, clouds, water, jelly, ...)
// render objects with opacity.

// Note: here, we can't really batch draw calls as the transform cannot be directly applied to the vertices,
// but rather sent to the GPU as a uniform u_transform.
// So the only possible optimization is sorting to minimize context switches (shader bindings).
// You must accept at least 1 draw-call per draw() operation.
// (for complex models with multiple model parts, expect more).
public class Renderer3D {

    @Deprecated public static Vector3 lightDir = new Vector3(0,0,-1).nor(); // TODO: remove

    // defaults
    private static final Texture whitePixelTexture  = Graphics.getTextureSingleWhitePixel();
    private static final Texture blackPixelTexture  = Graphics.getTextureSingleBlackPixelOpaque();
    private static final Texture normalMapTexture   = Graphics.getTextureSinglePixelNormalMap();
    public static final Shader  defaultShaderPBR   = createDefaultPBRShader();
    public static final Shader defaultShaderWireframeLines = createWireframeLinesShader(); // TODO: change back to private
    public static final Shader   defaultShaderWireframePoints    = createWireframePointsShader(); // TODO: change back to private
    // TODO: make normals rendering shader using geometry
    // TODO: make bounding box rendering using geometry
    private static final Shader  defaultShaderUnlit = createDefaultUnlitShader();

    private static final MemoryPool<RenderCommand> renderCommandsPool        = new MemoryPool<>(RenderCommand.class, 5);
    private static final Array<RenderCommand>      renderCommandsOpaque      = new Array<>(false, 20);
    private static final Array<RenderCommand>      renderCommandsTransparent = new Array<>(false, 20);
    private static final RenderEnvironment         renderEnvironment         = new RenderEnvironment();

    private static boolean drawing       = false;
    private static Camera  currentCamera = null;
    private static Shader  currentShader = null;

    // TODO: check if Renderer2D is currently rendering.
    public static void begin(Camera camera) {
        if (drawing) throw new GraphicsException("Cannot call begin() while drawing. Must call end()");
        if (camera == null) throw new GraphicsException("camera cannot be null when rendering using " + Renderer3D.class.getSimpleName() + ".begin(Camera camera).");

//        GL11.glColorMask(true, true, true, true); // enable color buffer writes
//        GL20.glDepthMask(true);
//        GL11.glEnable(GL11.GL_CULL_FACE); // TODO: enable!
//        GL11.glEnable(GL11.GL_DEPTH_TEST);
//        GL11.glEnable(GL11.GL_BLEND);
//        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        currentCamera = camera;
        drawing = true;
        currentShader = null;
    }

    @Deprecated public static void drawModel_tmp_5(ModelMesh mesh, ModelMaterial material, Matrix4x4 transform) {
        ShaderBinder.bind(currentShader);
        currentShader.bindUniform("u_transform", transform);
        currentShader.bindUniform("u_camera_combined", currentCamera.combined); // TODO: camera binding should not be here.
        currentShader.bindUniform("u_camera_position", currentCamera.position); // TODO: camera binding should not be here.

        // TODO: bind environment lights when binding the camera.
//        currentShader.bindUniform("pointLights[0].position", new Vector3(0,-5,5));
//        currentShader.bindUniform("pointLights[0].color", new Vector3(1f,0.0f,0.0f));
//        currentShader.bindUniform("pointLights[0].intensity", 1);
//
//        currentShader.bindUniform("pointLights[1].position", new Vector3(0,-5,-5));
//        currentShader.bindUniform("pointLights[1].color", new Vector3(0f,0.0f,1.0f));
//        currentShader.bindUniform("pointLights[1].intensity", 1);

        currentShader.bindUniform("directionalLights[0].direction", lightDir);
        currentShader.bindUniform("directionalLights[0].color", new Vector3(1f,1f,1.0f));
        currentShader.bindUniform("directionalLights[0].intensity", 0.2f);

        //System.out.println("=====  " + material.name + " =======");

        Texture texture_diffuse = (Texture) material.materialAttributes.get("u_texture_diffuse");
        //if (texture_diffuse == null) System.out.println("X diffuse map");
        Color color_diffuse = (Color) material.materialAttributes.get("u_color_diffuse");
        //if (color_diffuse == null) System.out.println("X diffuse color");
        if (texture_diffuse != null) {
            currentShader.bindUniform("u_texture_diffuse", texture_diffuse);
            currentShader.bindUniform("u_color_diffuse", Color.WHITE);
        } else if (color_diffuse != null) {
            currentShader.bindUniform("u_texture_diffuse", whitePixelTexture);
            currentShader.bindUniform("u_color_diffuse", color_diffuse);
        } else { // TODO: handle error: missing both diffuse texture and color.

        }

        Texture texture_normalMap = (Texture) material.materialAttributes.get("u_texture_normalMap");
        //if (texture_normalMap == null) System.out.println("X normal map");
        currentShader.bindUniform("u_texture_normalMap", Objects.requireNonNullElse(texture_normalMap, normalMapTexture));

        Texture texture_metallicMap = (Texture) material.materialAttributes.get("u_texture_metalness");
        //if (texture_metallicMap == null) System.out.println("X metalness map");
        Float metallic = (Float) material.materialAttributes.get("u_prop_metallic");
        if (metallic == null) System.out.println("X metalness value");
        if (texture_metallicMap != null) {
            currentShader.bindUniform("u_texture_metalness", texture_metallicMap);
            currentShader.bindUniform("u_prop_metallic", 1);
        } else {
            currentShader.bindUniform("u_texture_metalness", whitePixelTexture);
            currentShader.bindUniform("u_prop_metallic", metallic);
        }

        Texture texture_roughnessMap = (Texture) material.materialAttributes.get("u_texture_roughness");
        //if (texture_roughnessMap == null) System.out.println("X roughness map");
        Float roughness = (Float) material.materialAttributes.get("u_prop_roughness");
        //if (roughness == null) System.out.println("X roughness value");
        if (texture_roughnessMap != null) {
            currentShader.bindUniform("u_texture_roughness", texture_roughnessMap);
            currentShader.bindUniform("u_prop_roughness", 1);
        } else {
            currentShader.bindUniform("u_texture_roughness", whitePixelTexture);
            currentShader.bindUniform("u_prop_roughness", roughness);
        }

        Texture texture_opacity = (Texture) material.materialAttributes.get("u_texture_opacity");
        float opacity = (Float) material.materialAttributes.get("u_prop_opacity");
        if (texture_opacity != null) {
            currentShader.bindUniform("u_texture_opacity", texture_opacity);
            currentShader.bindUniform("u_prop_opacity", 1);
        } else {
            currentShader.bindUniform("u_texture_opacity", whitePixelTexture);
            currentShader.bindUniform("u_prop_opacity", opacity);
        }

        GL30.glBindVertexArray(mesh.vertexArrayObjectId);
        {
            // turn vbos on based on the shader and mesh
            for (VertexAttribute attribute : VertexAttribute.values()) {
                if (!currentShader.hasVertexAttribute(attribute)) continue;
                if (!mesh.hasVertexAttribute(attribute)) continue;
                GL20.glEnableVertexAttribArray(attribute.glslLocation);
            }

            if (mesh.useIndices) GL11.glDrawElements(GL11.GL_TRIANGLES, mesh.vertexCount, GL11.GL_UNSIGNED_INT, 0);
            else GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, mesh.vertexCount);

            // turn vbos off based on the shader and mesh
            for (VertexAttribute attribute : VertexAttribute.values()) {
                if (!currentShader.hasVertexAttribute(attribute)) continue;
                if (!mesh.hasVertexAttribute(attribute)) continue;
                GL20.glDisableVertexAttribArray(attribute.glslLocation);
            }
        }
        GL30.glBindVertexArray(0);
    }

    @Deprecated public static void drawModel_tmp_6(ModelMesh mesh, ModelMaterial material, Matrix4x4 transform) {
        ShaderBinder.bind(currentShader);
        currentShader.bindUniform("u_camera_combined", currentCamera.combined); // TODO: camera binding should not be here.
        currentShader.bindUniform("u_camera_position", currentCamera.position); // TODO: camera binding should not be here.

        // TODO: bind environment lights when binding the camera.
//        currentShader.bindUniform("pointLights[0].position", new Vector3(0,-5,5));
//        currentShader.bindUniform("pointLights[0].color", new Vector3(1f,0.0f,0.0f));
//        currentShader.bindUniform("pointLights[0].intensity", 1);
//
//        currentShader.bindUniform("pointLights[1].position", new Vector3(0,-5,-5));
//        currentShader.bindUniform("pointLights[1].color", new Vector3(0f,0.0f,1.0f));
//        currentShader.bindUniform("pointLights[1].intensity", 1);

        currentShader.bindUniform("directionalLights[0].direction", lightDir);
        currentShader.bindUniform("directionalLights[0].color", new Vector3(1f,1f,1.0f));
        currentShader.bindUniform("directionalLights[0].intensity", 0.2f);

        currentShader.bindUniform("u_transform", transform);

        for (String uniform : material.materialAttributes.keySet()) {
            if (!currentShader.uniformExists(uniform)) continue;
            Object value = material.materialAttributes.get(uniform);
            currentShader.bindUniform(uniform, value);
        }

        GL30.glBindVertexArray(mesh.vertexArrayObjectId);
        {
            // turn vbos on based on the shader and mesh
            for (VertexAttribute attribute : VertexAttribute.values()) {
                if (!currentShader.hasVertexAttribute(attribute)) continue;
                if (!mesh.hasVertexAttribute(attribute)) continue;
                GL20.glEnableVertexAttribArray(attribute.glslLocation);
            }

            if (mesh.useIndices) GL11.glDrawElements(GL11.GL_TRIANGLES, mesh.vertexCount, GL11.GL_UNSIGNED_INT, 0);
            else GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, mesh.vertexCount);

            // turn vbos off based on the shader and mesh
            for (VertexAttribute attribute : VertexAttribute.values()) {
                if (!currentShader.hasVertexAttribute(attribute)) continue;
                if (!mesh.hasVertexAttribute(attribute)) continue;
                GL20.glDisableVertexAttribArray(attribute.glslLocation);
            }
        }
        GL30.glBindVertexArray(0);
    }

    @Deprecated public static void drawModel_custom_shader(Shader shader, ModelMesh mesh, ModelMaterial material, Matrix4x4 transform) {
        ShaderBinder.bind(shader);

        shader.bindUniform("u_camera_combined", currentCamera.combined); // TODO: camera binding should not be here.
        shader.bindUniform("u_transform", transform);
        //currentShader.bindUniform("u_camera_position", currentCamera.position); // TODO: camera binding should not be here.

        // TODO: bind environment lights when binding the camera.
        //currentShader.bindUniform("pointLight.position", new Vector3(0,-5,0));
        //currentShader.bindUniform("pointLight.color", new Vector3(1,1f,1f));
        //currentShader.bindUniform("pointLight.intensity", 1);

        // bind custom material uniforms
        for (String uniform : shader.uniformNames) {
            Object value = material.materialAttributes.get(uniform);
            if (value == null) continue;
            shader.bindUniform(uniform, value);
        }

//        Texture texture_diffuse = (Texture) material.materialAttributes.get("u_texture_diffuse");
//        Color color_diffuse = (Color) material.materialAttributes.get("u_color_diffuse");
//
//        if (texture_diffuse != null) {
//            currentShader.bindUniform("u_texture_diffuse", texture_diffuse);
//            currentShader.bindUniform("u_color_diffuse", Color.WHITE);
//        } else if (color_diffuse != null) {
//            currentShader.bindUniform("u_texture_diffuse", defaultTexture);
//            currentShader.bindUniform("u_color_diffuse", color_diffuse);
//        } else { // TODO: handle error: missing both diffuse texture and color.
//
//        }

//        Texture texture_normalMap = (Texture) material.materialAttributes.get("u_texture_normalMap");
//        currentShader.bindUniform("u_texture_normalMap", Objects.requireNonNullElse(texture_normalMap, normalMapTexture));

        //float metallic = (Float) material.materialAttributes.get("u_prop_metallic");
        //float roughness = (Float) material.materialAttributes.get("u_prop_roughness");
        // TODO: conditional uniform binding - based on the shader attribute.
        //currentShader.bindUniform("u_prop_metallic", 1f);
        //currentShader.bindUniform("u_prop_roughness", 1);

        GL30.glBindVertexArray(mesh.vertexArrayObjectId);
        {
            // turn vbos on based on the shader and mesh
            for (VertexAttribute attribute : VertexAttribute.values()) {
                if (!shader.hasVertexAttribute(attribute)) continue;
                if (!mesh.hasVertexAttribute(attribute)) continue;
                GL20.glEnableVertexAttribArray(attribute.glslLocation);
            }

            if (mesh.useIndices) GL11.glDrawElements(GL11.GL_TRIANGLES, mesh.vertexCount, GL11.GL_UNSIGNED_INT, 0);
            else GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, mesh.vertexCount);

            // turn vbos off based on the shader and mesh
            for (VertexAttribute attribute : VertexAttribute.values()) {
                if (!shader.hasVertexAttribute(attribute)) continue;
                if (!mesh.hasVertexAttribute(attribute)) continue;
                GL20.glDisableVertexAttribArray(attribute.glslLocation);
            }
        }
        GL30.glBindVertexArray(0);
    }

    @Deprecated public static void drawModel_custom_shader_2(Shader shader, ModelMesh mesh, ModelMaterial material, Matrix4x4 transform) {
        ShaderBinder.bind(shader);
        //GL11.glDisable(GL11.GL_CULL_FACE); // TODO: enable!

        // TODO: bind environment lights when binding the camera.
        try {
            shader.bindUniform("u_camera_combined", currentCamera.combined); // TODO: camera binding should not be here.
            shader.bindUniform("u_transform", transform);
            shader.bindUniform("u_camera_position", currentCamera.position); // TODO: camera binding should not be here.

            shader.bindUniform("directionalLight.direction", lightDir);
            shader.bindUniform("directionalLight.color", new Vector3(1,1f,1f));
            shader.bindUniform("directionalLight.intensity", 1.8f);
        } catch (Exception e) {
            //System.out.println(e.getMessage());
        }

        // bind custom material uniforms
        for (String uniform : shader.uniformNames) {
            Object value = material.materialAttributes.get(uniform);
            if (value == null) continue;
            shader.bindUniform(uniform, value);
        }

//        Texture texture_diffuse = (Texture) material.materialAttributes.get("u_texture_diffuse");
//        Color color_diffuse = (Color) material.materialAttributes.get("u_color_diffuse");
//
//        if (texture_diffuse != null) {
//            currentShader.bindUniform("u_texture_diffuse", texture_diffuse);
//            currentShader.bindUniform("u_color_diffuse", Color.WHITE);
//        } else if (color_diffuse != null) {
//            currentShader.bindUniform("u_texture_diffuse", defaultTexture);
//            currentShader.bindUniform("u_color_diffuse", color_diffuse);
//        } else { // TODO: handle error: missing both diffuse texture and color.
//
//        }

//        Texture texture_normalMap = (Texture) material.materialAttributes.get("u_texture_normalMap");
//        currentShader.bindUniform("u_texture_normalMap", Objects.requireNonNullElse(texture_normalMap, normalMapTexture));

        //float metallic = (Float) material.materialAttributes.get("u_prop_metallic");
        //float roughness = (Float) material.materialAttributes.get("u_prop_roughness");
        // TODO: conditional uniform binding - based on the shader attribute.
        //currentShader.bindUniform("u_prop_metallic", 1f);
        //currentShader.bindUniform("u_prop_roughness", 1);

        GL30.glBindVertexArray(mesh.vertexArrayObjectId);
        {
            // turn vbos on based on the shader and mesh
            for (VertexAttribute attribute : VertexAttribute.values()) {
                if (!shader.hasVertexAttribute(attribute)) continue;
                if (!mesh.hasVertexAttribute(attribute)) continue;
                GL20.glEnableVertexAttribArray(attribute.glslLocation);
            }

            if (mesh.useIndices) GL11.glDrawElements(GL11.GL_TRIANGLES, mesh.vertexCount, GL11.GL_UNSIGNED_INT, 0);
            else GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, mesh.vertexCount);

            // turn vbos off based on the shader and mesh
            for (VertexAttribute attribute : VertexAttribute.values()) {
                if (!shader.hasVertexAttribute(attribute)) continue;
                if (!mesh.hasVertexAttribute(attribute)) continue;
                GL20.glDisableVertexAttribArray(attribute.glslLocation);
            }
        }
        GL30.glBindVertexArray(0);

    }

    @Deprecated public static void drawModel_cloud_shader_2(Shader shader, ModelMesh mesh, ModelMaterial material, Matrix4x4 transform, int index) {
        ShaderBinder.bind(shader);
        GL11.glDisable(GL11.GL_CULL_FACE); // TODO: enable!


        // TODO: bind environment lights when binding the camera.
        try {
            shader.bindUniform("u_camera_combined", currentCamera.combined); // TODO: camera binding should not be here.
            shader.bindUniform("u_transform", transform);
            shader.bindUniform("u_camera_position", currentCamera.position); // TODO: camera binding should not be here.

            shader.bindUniform("directionalLight.direction", lightDir);
            shader.bindUniform("directionalLight.color", new Vector3(1,1f,1f));
            shader.bindUniform("directionalLight.intensity", 1.2f);
        } catch (Exception e) {
            //System.out.println(e.getMessage());
        }

        // bind custom material uniforms
        for (String uniform : shader.uniformNames) {
            Object value = material.materialAttributes.get(uniform);
            if (value == null) continue;
            shader.bindUniform(uniform, value);
        }
        shader.bindUniform("u_frame", index % 64);

        GL30.glBindVertexArray(mesh.vertexArrayObjectId);
        {
            // turn vbos on based on the shader and mesh
            for (VertexAttribute attribute : VertexAttribute.values()) {
                if (!shader.hasVertexAttribute(attribute)) continue;
                if (!mesh.hasVertexAttribute(attribute)) continue;
                GL20.glEnableVertexAttribArray(attribute.glslLocation);
            }

            if (mesh.useIndices) GL11.glDrawElements(GL11.GL_TRIANGLES, mesh.vertexCount, GL11.GL_UNSIGNED_INT, 0);
            else GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, mesh.vertexCount);

            // turn vbos off based on the shader and mesh
            for (VertexAttribute attribute : VertexAttribute.values()) {
                if (!shader.hasVertexAttribute(attribute)) continue;
                if (!mesh.hasVertexAttribute(attribute)) continue;
                GL20.glDisableVertexAttribArray(attribute.glslLocation);
            }
        }
        GL30.glBindVertexArray(0);
        GL11.glEnable(GL11.GL_CULL_FACE); // TODO: enable!
    }

    @Deprecated public static void drawModel_custom_unlit_shader(ModelMesh mesh, ModelMaterial material, Matrix4x4 transform) {
        ShaderBinder.bind(defaultShaderUnlit);

        defaultShaderUnlit.bindUniform("u_camera_combined", currentCamera.combined); // TODO: camera binding should not be here.
        defaultShaderUnlit.bindUniform("u_transform", transform);

        Texture texture_diffuse = (Texture) material.materialAttributes.get("u_texture_diffuse");
        Color color_diffuse = (Color) material.materialAttributes.get("u_color_diffuse");

        if (texture_diffuse != null) {
            defaultShaderUnlit.bindUniform("u_texture_diffuse", texture_diffuse);
            defaultShaderUnlit.bindUniform("u_color_diffuse", Color.WHITE);
        } else if (color_diffuse != null) {
            defaultShaderUnlit.bindUniform("u_texture_diffuse", whitePixelTexture);
            defaultShaderUnlit.bindUniform("u_color_diffuse", color_diffuse);
        } else { // TODO: handle error: missing both diffuse texture and color.

        }

        Texture texture_opacity = (Texture) material.materialAttributes.get("u_texture_opacity");
        Float opacity = (Float) material.materialAttributes.get("u_prop_opacity");
        if (texture_opacity != null) {
            defaultShaderUnlit.bindUniform("u_texture_opacity", texture_opacity);
            defaultShaderUnlit.bindUniform("u_prop_opacity", 1);
        } else if (opacity != null) {
            defaultShaderUnlit.bindUniform("u_texture_opacity", whitePixelTexture);
            defaultShaderUnlit.bindUniform("u_prop_opacity", opacity);
        } else {
            defaultShaderUnlit.bindUniform("u_texture_opacity", whitePixelTexture);
            defaultShaderUnlit.bindUniform("u_prop_opacity",1);
        }

        GL30.glBindVertexArray(mesh.vertexArrayObjectId);
        {
            // turn vbos on based on the shader and mesh
            for (VertexAttribute attribute : VertexAttribute.values()) {
                if (!defaultShaderUnlit.hasVertexAttribute(attribute)) continue;
                if (!mesh.hasVertexAttribute(attribute)) continue;
                GL20.glEnableVertexAttribArray(attribute.glslLocation);
            }

            if (mesh.useIndices) GL11.glDrawElements(GL11.GL_TRIANGLES, mesh.vertexCount, GL11.GL_UNSIGNED_INT, 0);
            else GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, mesh.vertexCount);

            // turn vbos off based on the shader and mesh
            for (VertexAttribute attribute : VertexAttribute.values()) {
                if (!defaultShaderUnlit.hasVertexAttribute(attribute)) continue;
                if (!mesh.hasVertexAttribute(attribute)) continue;
                GL20.glDisableVertexAttribArray(attribute.glslLocation);
            }
        }
        GL30.glBindVertexArray(0);
    }

    // TODO: add support for wireframes
    public static void drawModel(Model model, Matrix4x4 transform) {
        // for every mesh, create a render command
        for (int i = 0; i < model.meshes.length; i++) {
            RenderCommand renderCommand = renderCommandsPool.allocate();
            renderCommand.mesh = model.meshes[i];
            renderCommand.material = model.materials[i];
            renderCommand.transform = transform;
            renderCommand.shader = renderCommand.material.shader;
            if (renderCommand.shader == null) {
                renderCommand.shader = renderCommand.material.useLights ? defaultShaderPBR : defaultShaderUnlit;
            }

            if (renderCommand.material.transparent) {
                renderCommandsTransparent.add(renderCommand);
            } else {
                renderCommandsOpaque.add(renderCommand);
            }
        }
    }

    public static void drawModel(Shader shader, ModelMesh mesh, ModelMaterial material, Matrix4x4 transform) {
        RenderCommand renderCommand = renderCommandsPool.allocate();
        renderCommand.mesh = mesh;
        renderCommand.material = material;
        renderCommand.transform = transform;
        renderCommand.shader = shader;
        if (renderCommand.shader == null) {
            renderCommand.shader = renderCommand.material.useLights ? defaultShaderPBR : defaultShaderUnlit;
        }

        if (renderCommand.material.transparent) {
            renderCommandsTransparent.add(renderCommand);
        } else {
            renderCommandsOpaque.add(renderCommand);
        }
    }

    public static void end() {
        if (!drawing) throw new GraphicsException("Called " + Renderer3D.class.getSimpleName() + ".end() without calling " + Renderer3D.class.getSimpleName() + ".begin() first.");

        GL11.glColorMask(true, true, true, true); // enable color buffer writes
        GL20.glDepthMask(true);
        GL11.glEnable(GL11.GL_CULL_FACE); // TODO: enable!
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        /* draw all opaque object */
        // TODO: sort renderables by shader -> material index.
        for (RenderCommand command : renderCommandsOpaque) {
            Shader shader = command.shader;
            ModelMesh mesh = command.mesh;
            ModelMaterial material = command.material;
            Matrix4x4 transform = command.transform;
            drawMesh(shader, mesh, material, transform);
        }

        /* draw all transparent object */
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL20.glDepthMask(false);
        // TODO: sort transparent by camera z-depth -> shader -> material index
        Vector3 position_o1 = new Vector3();
        Vector3 position_o2 = new Vector3();
        Collections.sort(renderCommandsTransparent, (o1, o2) -> {
            Matrix4x4 t1 = o1.transform;
            Matrix4x4 t2 = o2.transform;
            float d1 = currentCamera.position.dst2(t1.getTranslation(position_o1));
            float d2 = currentCamera.position.dst2(t2.getTranslation(position_o2));
            return Float.compare(d2, d1); // farthest first
        });
        for (RenderCommand command : renderCommandsTransparent) {
            Shader shader = command.shader;
            ModelMesh mesh = command.mesh;
            ModelMaterial material = command.material;
            Matrix4x4 transform = command.transform;
            drawMesh(shader, mesh, material, transform);
        }
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL20.glEnable(GL20.GL_DEPTH_TEST);
        GL20.glDepthMask(true);

        drawing = false;
        renderCommandsPool.freeAll(renderCommandsOpaque);
        renderCommandsPool.freeAll(renderCommandsTransparent);
        renderCommandsOpaque.clear();
        renderCommandsTransparent.clear();
    }

    private static void setShader(@NotNull Shader shader) {
        if (currentShader == shader) return;

        ShaderBinder.bind(shader);

        // TODO: bind all camera uniforms
        if (shader.uniformExists("u_camera_combined")) {
            shader.bindUniform("u_camera_combined", currentCamera.combined);
        }
        if (shader.uniformExists("u_camera_projection")) {
            shader.bindUniform("u_camera_projection", currentCamera.projection);
        }
        if (shader.uniformExists("u_camera_view")) {
            shader.bindUniform("u_camera_view", currentCamera.view);
        }
        if (shader.uniformExists("u_camera_position")) {
            shader.bindUniform("u_camera_position", currentCamera.position);
        }
        if (shader.uniformExists("u_camera_near")) {
            shader.bindUniform("u_camera_near", currentCamera.near);
        }
        if (shader.uniformExists("u_camera_far")) {
            shader.bindUniform("u_camera_far", currentCamera.far);
        }

        // TODO: improve and refactor
        // TODO: bind environment uniforms
        if (shader.uniformExists("directionalLights[0].direction")) {
            lightDir.nor();
            shader.bindUniform("directionalLights[0].direction", lightDir);
        }
        if (shader.uniformExists("directionalLights[0].color")) {
            shader.bindUniform("directionalLights[0].color", new Vector3(1f,1f,1.0f));
        }
        if (shader.uniformExists("directionalLights[0].intensity")) {
            shader.bindUniform("directionalLights[0].intensity", 1.6f);
        }

        // TODO: bind all global variables uniforms (u_time, u_delta_time)
        if (shader.uniformExists("u_delta_time")) {
            shader.bindUniform("u_delta_time", Graphics.getDeltaTime());
        }
        if (shader.uniformExists("u_time")) {
            shader.bindUniform("u_time", Graphics.getElapsedTime());
        }

        currentShader = shader;
    }

    // TODO: make private
    public static void drawMesh(Shader shader, ModelMesh mesh, ModelMaterial material, Matrix4x4 transform) {
        setShader(shader);

        /* bind transform, if present. */
        if (currentShader.uniformExists("u_transform")) {
            currentShader.bindUniform("u_transform", transform);
        }

        // TODO: bind skeletal animation, if present.

        /* bind material parameters */
        for (String uniform : material.materialAttributes.keySet()) {
            if (!currentShader.uniformExists(uniform)) continue;
            Object value = material.materialAttributes.get(uniform);
            currentShader.bindUniform(uniform, value);
        }

        /* the actual draw call */
        GL30.glBindVertexArray(mesh.vertexArrayObjectId);
        {
            // turn on VBOs based on the shader and mesh
            for (VertexAttribute attribute : VertexAttribute.values()) {
                if (!currentShader.hasVertexAttribute(attribute)) continue;
                if (!mesh.hasVertexAttribute(attribute)) continue;
                GL20.glEnableVertexAttribArray(attribute.glslLocation);
            }
            if (mesh.useIndices) GL11.glDrawElements(GL11.GL_TRIANGLES, mesh.vertexCount, GL11.GL_UNSIGNED_INT, 0); // draw!
            else GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, mesh.vertexCount); // draw!
            // turn off VBOs based on the shader and mesh
            for (VertexAttribute attribute : VertexAttribute.values()) {
                if (!currentShader.hasVertexAttribute(attribute)) continue;
                if (!mesh.hasVertexAttribute(attribute)) continue;
                GL20.glDisableVertexAttribArray(attribute.glslLocation);
            }
        }
        GL30.glBindVertexArray(0);
    }

    // TODO: remove these two methods. Right at the draw call, you will bind transforms and materials for each mesh.
    @Deprecated private static void bindModelMaterialParametersToShader() {

    }
    @Deprecated private static void bindModelTransformToShader() {

    }

    private static Shader createDefaultUnlitShader() {
        try (InputStream vertexShaderInputStream = Renderer2D.class.getClassLoader().getResourceAsStream("graphics-3d-default-unlit-shader.vert");
             BufferedReader vertexShaderBufferedReader = new BufferedReader(new InputStreamReader(vertexShaderInputStream, StandardCharsets.UTF_8));
             InputStream fragmentShaderInputStream = Renderer2D.class.getClassLoader().getResourceAsStream("graphics-3d-default-unlit-shader.frag");
             BufferedReader fragmentShaderBufferedReader = new BufferedReader(new InputStreamReader(fragmentShaderInputStream, StandardCharsets.UTF_8))) {
            String vertexShader = vertexShaderBufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));
            String fragmentShader = fragmentShaderBufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));
            return new Shader(vertexShader, fragmentShader);
        } catch (Exception e) {
            throw new RuntimeException("error creating default shader");
        }
    }

    private static Shader createDefaultPBRShader() {
        try (InputStream vertexShaderInputStream = Renderer2D.class.getClassLoader().getResourceAsStream("graphics-3d-default-pbr-shader.vert");
             BufferedReader vertexShaderBufferedReader = new BufferedReader(new InputStreamReader(vertexShaderInputStream, StandardCharsets.UTF_8));
             InputStream fragmentShaderInputStream = Renderer2D.class.getClassLoader().getResourceAsStream("graphics-3d-default-pbr-shader.frag");
             BufferedReader fragmentShaderBufferedReader = new BufferedReader(new InputStreamReader(fragmentShaderInputStream, StandardCharsets.UTF_8))) {
            String vertexShader = vertexShaderBufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));
            String fragmentShader = fragmentShaderBufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));
            return new Shader(vertexShader, fragmentShader);
        } catch (Exception e) {
            throw new RuntimeException("error creating default shader: " + e.getMessage());
        }
    }

    private static Shader createWireframeLinesShader() {
        try (InputStream vertexShaderInputStream = Renderer2D.class.getClassLoader().getResourceAsStream("graphics-3d-wireframe-shader.vert");
             BufferedReader vertexShaderBufferedReader = new BufferedReader(new InputStreamReader(vertexShaderInputStream, StandardCharsets.UTF_8));
             InputStream geometryShaderInputStream = Renderer2D.class.getClassLoader().getResourceAsStream("graphics-3d-wireframe-lines-shader.geom");
             BufferedReader geometryShaderBufferedReader = new BufferedReader(new InputStreamReader(geometryShaderInputStream, StandardCharsets.UTF_8));
             InputStream fragmentShaderInputStream = Renderer2D.class.getClassLoader().getResourceAsStream("graphics-3d-wireframe-shader.frag");
             BufferedReader fragmentShaderBufferedReader = new BufferedReader(new InputStreamReader(fragmentShaderInputStream, StandardCharsets.UTF_8))) {
            String vertexShader = vertexShaderBufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));
            String geometryShader = geometryShaderBufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));
            String fragmentShader = fragmentShaderBufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));
            return new Shader(vertexShader, geometryShader, fragmentShader);
        } catch (Exception e) {
            throw new RuntimeException("error creating default shader: " + e.getMessage());
        }
    }

    private static Shader createWireframePointsShader() {
        try (InputStream vertexShaderInputStream = Renderer2D.class.getClassLoader().getResourceAsStream("graphics-3d-wireframe-shader.vert");
             BufferedReader vertexShaderBufferedReader = new BufferedReader(new InputStreamReader(vertexShaderInputStream, StandardCharsets.UTF_8));
             InputStream geometryShaderInputStream = Renderer2D.class.getClassLoader().getResourceAsStream("graphics-3d-wireframe-points-shader.geom");
             BufferedReader geometryShaderBufferedReader = new BufferedReader(new InputStreamReader(geometryShaderInputStream, StandardCharsets.UTF_8));
             InputStream fragmentShaderInputStream = Renderer2D.class.getClassLoader().getResourceAsStream("graphics-3d-wireframe-shader.frag");
             BufferedReader fragmentShaderBufferedReader = new BufferedReader(new InputStreamReader(fragmentShaderInputStream, StandardCharsets.UTF_8))) {
            String vertexShader = vertexShaderBufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));
            String geometryShader = geometryShaderBufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));
            String fragmentShader = fragmentShaderBufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));
            return new Shader(vertexShader, geometryShader, fragmentShader);
        } catch (Exception e) {
            throw new RuntimeException("error creating default shader: " + e.getMessage());
        }
    }

    /*
    TODO: this is common to both Renderer2D and Renderer3D and should be refactored.
    creates a single-white-pixel texture.
     */
    @Deprecated private static Texture createDefaultTexture() {
        ByteBuffer buffer = ByteBuffer.allocateDirect(4);
        buffer.put((byte) ((0xFFFFFFFF >> 16) & 0xFF)); // Red component
        buffer.put((byte) ((0xFFFFFFFF >> 8) & 0xFF));  // Green component
        buffer.put((byte) (0xFF));                      // Blue component
        buffer.put((byte) ((0xFFFFFFFF >> 24) & 0xFF)); // Alpha component
        buffer.flip();

        return new Texture(1, 1, buffer,
                Texture.FilterMag.NEAREST, Texture.FilterMin.NEAREST,
                Texture.Wrap.CLAMP_TO_EDGE, Texture.Wrap.CLAMP_TO_EDGE,1);
    }

    /*
    TODO: this is common to both Renderer2D and Renderer3D and should be refactored.
    creates a single-white-pixel texture.
     */
    @Deprecated private static Texture createNormalMapTexture() {
        ByteBuffer buffer = ByteBuffer.allocateDirect(4);
        buffer.put((byte) 0x80); // Red component (128)
        buffer.put((byte) 0x80); // Green component (128)
        buffer.put((byte) 0xFF); // Blue component (255)
        buffer.put((byte) 0xFF); // Alpha component (255)
        buffer.flip();

        return new Texture(1, 1, buffer,
                Texture.FilterMag.NEAREST, Texture.FilterMin.NEAREST,
                Texture.Wrap.CLAMP_TO_EDGE, Texture.Wrap.CLAMP_TO_EDGE, 1);
    }

    public static final class RenderCommand implements MemoryPool.Reset {

        public ModelMesh     mesh      = null;
        public ModelMaterial material  = null;
        public Matrix4x4     transform = null;
        public Shader        shader    = null;

        public RenderCommand() {} // using reflection.

        @Override
        public void reset() {
            this.mesh = null;
            this.transform = null;
            this.material = null;
            this.shader = null;
        }

    }

    // TODO: improve to include: multiple light sources, point lights, spot lights, area lights, environmental effects (fog, ?, ...).
    private static final class RenderEnvironment {

        public Vector3 directionalLightColor = new Vector3(1,1,1);
        public Vector3 directionalLightDirection = new Vector3(0,0,-1);
        public float   directionalLightIntensity = 0.2f; // TODO: set these defaults to "0".

        // TODO: add ambient light

        // TODO: add point lights

        // TODO: add spot lights

        // TODO: add ara lights

        // TODO: add fog

    }

}

/*

public void draw(final ModelPart modelPart, final ComponentTransform_1 transform) {
        // TODO: maybe updating the bounding sphere should be somewhere else.
        float centerX = modelPart.mesh.boundingSphereCenter.x;
        float centerY = modelPart.mesh.boundingSphereCenter.y;
        float centerZ = modelPart.mesh.boundingSphereCenter.z;
        Vector3 boundingSphereCenter = new Vector3(centerX + transform.x, centerY + transform.y, centerZ + transform.z);
        float boundingSphereRadius = MathUtils.max(transform.scaleX, transform.scaleY, transform.scaleZ) * modelPart.mesh.boundingSphereRadius;
        if (componentGraphicsCamera.lens.frustumIntersectsSphere(boundingSphereCenter, boundingSphereRadius)) {
            System.out.println("intersects");
        } else {
            System.out.println("CULLING");
        }

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_CULL_FACE);
        // todo: see when it makes sense to compute the matrix transform
        currentShader.bindUniform("u_body_transform", transform.world());
        ModelPartMaterial material = modelPart.material;
        //currentShader.bindUniforms(material.materialParams);
        currentShader.bindUniform("colorDiffuse", material.uniformParams.get("colorDiffuse"));
        ModelPartMesh mesh = modelPart.mesh;
        System.out.println("ddddd " + mesh.vaoId);
        GL30.glBindVertexArray(mesh.vaoId);
        {
            for (VertexAttribute_old attribute : VertexAttribute_old.values()) {
                System.out.println("attrib: " + attribute.slot);
                if (mesh.hasVertexAttribute(attribute)) GL20.glEnableVertexAttribArray(attribute.slot);
            }
            if (mesh.indexed) GL11.glDrawElements(GL11.GL_TRIANGLES, mesh.vertexCount, GL11.GL_UNSIGNED_INT, 0);
            else GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, mesh.vertexCount);
            for (VertexAttribute_old attribute : VertexAttribute_old.values()) if (mesh.hasVertexAttribute(attribute)) GL20.glDisableVertexAttribArray(attribute.slot);
        }
        GL30.glBindVertexArray(0);
    }


 */