package com.heavybox.jtix.graphics;

import com.heavybox.jtix.collections.MapObjectInt;
import com.heavybox.jtix.math.*;
import com.heavybox.jtix.memory.MemoryResource;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL32;

import java.nio.IntBuffer;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Shader implements MemoryResource {

    @Deprecated private static final Pattern GLSL_COMMENT_PATTERN   = Pattern.compile("//.*|/\\*[^*]*\\*+(?:[^/*][^*]*\\*+)*/");
    @Deprecated private static final Pattern GLSL_LAYOUT_IN_PATTERN = Pattern.compile("layout\\s*\\(\\s*location\\s*=\\s*\\d+\\s*\\)\\s*(in|attribute)");

    private boolean deleted = false;

    public final String vertexShaderSource;
    public final String geometryShaderSource;
    public final String fragmentShaderSource;
    public final int    program;
    public final int    vertexShaderId;
    public final int    geometryShaderId;
    public final int    fragmentShaderId;
    public final int    vertexAttributesBitmask;

    public final MapObjectInt<String> attributeLocations;
    public final MapObjectInt<String> attributeTypes;
    public final MapObjectInt<String> attributeSizes;
    public final MapObjectInt<String> uniformLocations;
    public final MapObjectInt<String> uniformTypes;
    public final MapObjectInt<String> uniformSizes;
    public final String[]             attributeNames;
    public final String[]             uniformNames;

    private final HashMap<Integer, Object> uniformsCache;

    public Shader(final String vertexShaderSource, final String geometryShaderSource, final String fragmentShaderSource) {
        if (vertexShaderSource == null)   throw new GraphicsException("Vertex shader cannot be null.");
        if (fragmentShaderSource == null) throw new GraphicsException("Fragment shader cannot be null.");
        /* pre-process shader code */
        this.vertexShaderSource = vertexShaderSource;//preprocessVertexShader(vertexShaderSource);
        this.geometryShaderSource = geometryShaderSource;//preprocessVertexShader(vertexShaderSource);
        this.fragmentShaderSource = fragmentShaderSource;//preprocessFragmentShader(fragmentShaderSource);
        /* attributes */
        this.attributeLocations = new MapObjectInt<>();
        this.attributeTypes = new MapObjectInt<>();
        this.attributeSizes = new MapObjectInt<>();
        /* uniforms */
        this.uniformLocations = new MapObjectInt<>();
        this.uniformTypes = new MapObjectInt<>();
        this.uniformSizes = new MapObjectInt<>();
        /* create shader */
        this.program = GL20.glCreateProgram();
        if (program == 0) throw new GraphicsException("Could not create shader");

        /* create vertex shader */
        this.vertexShaderId = GL20.glCreateShader(GL20.GL_VERTEX_SHADER);
        if (vertexShaderId == 0) throw new GraphicsException("Error creating vertex shader.");
        GL20.glShaderSource(vertexShaderId, this.vertexShaderSource);
        GL20.glCompileShader(vertexShaderId);
        if (GL20.glGetShaderi(vertexShaderId, GL20.GL_COMPILE_STATUS) == 0)
            throw new RuntimeException("Error compiling vertex shader: " + GL20.glGetShaderInfoLog(vertexShaderId, 1024));
        GL20.glAttachShader(program, vertexShaderId);

        /* optional: create geometry shader */
        if (geometryShaderSource != null) {
            this.geometryShaderId = GL20.glCreateShader(GL32.GL_GEOMETRY_SHADER);
            if (geometryShaderId == 0)
                throw new RuntimeException("Error creating geometry shader.");
            GL20.glShaderSource(geometryShaderId, this.geometryShaderSource);
            GL20.glCompileShader(geometryShaderId);
            if (GL20.glGetShaderi(geometryShaderId, GL20.GL_COMPILE_STATUS) == 0)
                throw new RuntimeException("Error compiling geometry shader: " + GL20.glGetShaderInfoLog(geometryShaderId, 1024));
            GL20.glAttachShader(program, geometryShaderId);
        } else {
            this.geometryShaderId = -1; // no geometry shader.
        }

        /* create fragment shader */
        this.fragmentShaderId = GL20.glCreateShader(GL20.GL_FRAGMENT_SHADER);
        if (fragmentShaderId == 0)
            throw new RuntimeException("Error creating fragment shader.");
        GL20.glShaderSource(fragmentShaderId, this.fragmentShaderSource);
        GL20.glCompileShader(fragmentShaderId);
        if (GL20.glGetShaderi(fragmentShaderId, GL20.GL_COMPILE_STATUS) == 0)
            throw new RuntimeException("Error compiling fragment shader: " + GL20.glGetShaderInfoLog(fragmentShaderId, 1024));
        GL20.glAttachShader(program, fragmentShaderId);

        /* set attribute locations to what's expected using the standard */
        for (VertexAttribute attribute : VertexAttribute.values()) {
            GL20.glBindAttribLocation(program, attribute.glslLocation, attribute.glslVariableName);
        }

        /* link program */
        GL20.glLinkProgram(program);
        if (GL20.glGetProgrami(program, GL20.GL_LINK_STATUS) == 0)
            throw new GraphicsException("Error linking shader code: " + GL20.glGetProgramInfoLog(program, 1024));
        GL20.glDetachShader(program, vertexShaderId);
        if (geometryShaderId != -1) GL20.glDetachShader(program, geometryShaderId);
        GL20.glDetachShader(program, fragmentShaderId);

        /* validate program */
        GL20.glValidateProgram(program);
        if (GL20.glGetProgrami(program, GL20.GL_VALIDATE_STATUS) == 0)
            throw new GraphicsException("Could not validate shader code: " + GL20.glGetProgramInfoLog(program, 1024));

        /* register attributes */
        IntBuffer params_attributes = BufferUtils.createIntBuffer(1);
        IntBuffer type_attributes = BufferUtils.createIntBuffer(1);
        GL20.glGetProgramiv(this.program, GL20.GL_ACTIVE_ATTRIBUTES, params_attributes);
        int numAttributes = params_attributes.get(0);
        this.attributeNames = new String[numAttributes];
        for(int i = 0; i < numAttributes; ++i) {
            params_attributes.clear();
            params_attributes.put(0, 1);
            type_attributes.clear();
            String name = GL20.glGetActiveAttrib(this.program, i, params_attributes, type_attributes);
            int location = GL20.glGetAttribLocation(this.program, name);
            this.attributeLocations.put(name, location);
            this.attributeTypes.put(name, type_attributes.get(0));
            this.attributeSizes.put(name, params_attributes.get(0));
            this.attributeNames[i] = name;
        }
        this.vertexAttributesBitmask = VertexAttribute.getShaderBitmask(attributeNames);

        /* register uniforms */
        IntBuffer params_uniforms = BufferUtils.createIntBuffer(1);
        IntBuffer type_uniforms = BufferUtils.createIntBuffer(1);
        GL20.glGetProgramiv(this.program, GL20.GL_ACTIVE_UNIFORMS, params_uniforms);
        int uniformSymbolsCount = params_uniforms.get(0);
        for (int i = 0; i < uniformSymbolsCount; i++) {
            params_uniforms.clear();
            params_uniforms.put(0, 1);
            type_uniforms.clear();
            String name = GL20.glGetActiveUniform(this.program, i, params_uniforms, type_uniforms);
            int size = params_uniforms.get(0);
            final int location = GL20.glGetUniformLocation(this.program, name);
            this.uniformSizes.put(name, size);
            this.uniformTypes.put(name, type_uniforms.get(0));
            this.uniformLocations.put(name, location);
            if (size > 1) { // array of uniforms.
                String prefix = name.replaceAll("\\[.*?]", "");;
                for (int k = 1; k < size; k++) {
                    String nextName = prefix + "[" + k + "]";
                    this.uniformSizes.put(nextName, size);
                    this.uniformTypes.put(nextName, type_uniforms.get(0));
                    this.uniformLocations.put(nextName, location + k);
                }
            }
        }
        this.uniformNames = new String[uniformLocations.size];
        int i = 0;
        for (MapObjectInt.Entry<String> entry : uniformLocations) {
            this.uniformNames[i] = entry.key;
            i++;
        }
        /* instantiate cache */
        this.uniformsCache = new HashMap<>();

        /* validation */
        /* validate: limit the allowed max sampled textures */
        final int maxSampledTextures = Graphics.getMaxFragmentShaderTextureUnits();
        int sampledTextures = 0;
        for (MapObjectInt.Entry<String> uniform : uniformTypes.entries()) {
            int type = uniform.value;
            if (type == GL20.GL_SAMPLER_2D) sampledTextures++;
        }
        if (sampledTextures > maxSampledTextures) throw new GraphicsException("Error: shader code trying to sample " + sampledTextures + ". The allowed maximum on this hardware is " + maxSampledTextures);
    }

    // TODO: simply call the all args constructor
    public Shader(final String vertexShaderSource, final String fragmentShaderSource) {
        if (vertexShaderSource == null)   throw new GraphicsException("Vertex shader cannot be null.");
        if (fragmentShaderSource == null) throw new GraphicsException("Fragment shader cannot be null.");
        /* pre-process shader code */
        this.vertexShaderSource = vertexShaderSource;//preprocessVertexShader(vertexShaderSource);
        this.geometryShaderSource = null;
        this.fragmentShaderSource = fragmentShaderSource;//preprocessFragmentShader(fragmentShaderSource);
        /* attributes */
        this.attributeLocations = new MapObjectInt<>();
        this.attributeTypes = new MapObjectInt<>();
        this.attributeSizes = new MapObjectInt<>();
        /* uniforms */
        this.uniformLocations = new MapObjectInt<>();
        this.uniformTypes = new MapObjectInt<>();
        this.uniformSizes = new MapObjectInt<>();
        /* create shader */
        this.program = GL20.glCreateProgram();
        if (program == 0) throw new GraphicsException("Could not create shader");

        /* create vertex shader */
        this.vertexShaderId = GL20.glCreateShader(GL20.GL_VERTEX_SHADER);
        if (vertexShaderId == 0) throw new GraphicsException("Error creating vertex shader.");
        GL20.glShaderSource(vertexShaderId, this.vertexShaderSource);
        GL20.glCompileShader(vertexShaderId);
        if (GL20.glGetShaderi(vertexShaderId, GL20.GL_COMPILE_STATUS) == 0)
            throw new RuntimeException("Error compiling vertex shader: " + GL20.glGetShaderInfoLog(vertexShaderId, 1024));
        GL20.glAttachShader(program, vertexShaderId);

        /* skip creation of geometry shader */
        this.geometryShaderId = -1;

        /* create fragment shader */
        this.fragmentShaderId = GL20.glCreateShader(GL20.GL_FRAGMENT_SHADER);
        if (fragmentShaderId == 0)
            throw new RuntimeException("Error creating fragment shader.");
        GL20.glShaderSource(fragmentShaderId, this.fragmentShaderSource);
        GL20.glCompileShader(fragmentShaderId);
        if (GL20.glGetShaderi(fragmentShaderId, GL20.GL_COMPILE_STATUS) == 0)
            throw new RuntimeException("Error compiling fragment shader: " + GL20.glGetShaderInfoLog(fragmentShaderId, 1024));
        GL20.glAttachShader(program, fragmentShaderId);

        /* set attribute locations to what's expected using the standard */
        for (VertexAttribute attribute : VertexAttribute.values()) {
            GL20.glBindAttribLocation(program, attribute.glslLocation, attribute.glslVariableName);
        }

        /* link program */
        GL20.glLinkProgram(program);
        if (GL20.glGetProgrami(program, GL20.GL_LINK_STATUS) == 0)
            throw new GraphicsException("Error linking shader code: " + GL20.glGetProgramInfoLog(program, 1024));
        GL20.glDetachShader(program, vertexShaderId);
        GL20.glDetachShader(program, fragmentShaderId);
        GL20.glValidateProgram(program);
        if (GL20.glGetProgrami(program, GL20.GL_VALIDATE_STATUS) == 0)
            throw new GraphicsException("Could not validate shader code: " + GL20.glGetProgramInfoLog(program, 1024));

        /* register attributes */
        IntBuffer params_attributes = BufferUtils.createIntBuffer(1);
        IntBuffer type_attributes = BufferUtils.createIntBuffer(1);
        GL20.glGetProgramiv(this.program, GL20.GL_ACTIVE_ATTRIBUTES, params_attributes);
        int numAttributes = params_attributes.get(0);
        this.attributeNames = new String[numAttributes];
        for(int i = 0; i < numAttributes; ++i) {
            params_attributes.clear();
            params_attributes.put(0, 1);
            type_attributes.clear();
            String name = GL20.glGetActiveAttrib(this.program, i, params_attributes, type_attributes);
            int location = GL20.glGetAttribLocation(this.program, name);
            this.attributeLocations.put(name, location);
            this.attributeTypes.put(name, type_attributes.get(0));
            this.attributeSizes.put(name, params_attributes.get(0));
            this.attributeNames[i] = name;
        }
        this.vertexAttributesBitmask = VertexAttribute.getShaderBitmask(attributeNames);

        /* register uniforms */
        IntBuffer params_uniforms = BufferUtils.createIntBuffer(1);
        IntBuffer type_uniforms = BufferUtils.createIntBuffer(1);
        GL20.glGetProgramiv(this.program, GL20.GL_ACTIVE_UNIFORMS, params_uniforms);
        int uniformSymbolsCount = params_uniforms.get(0);
        for (int i = 0; i < uniformSymbolsCount; i++) {
            params_uniforms.clear();
            params_uniforms.put(0, 1);
            type_uniforms.clear();
            String name = GL20.glGetActiveUniform(this.program, i, params_uniforms, type_uniforms);
            int size = params_uniforms.get(0);
            final int location = GL20.glGetUniformLocation(this.program, name);
            this.uniformSizes.put(name, size);
            this.uniformTypes.put(name, type_uniforms.get(0));
            this.uniformLocations.put(name, location);
            if (size > 1) { // array of uniforms.
                String prefix = name.replaceAll("\\[.*?]", "");;
                for (int k = 1; k < size; k++) {
                    String nextName = prefix + "[" + k + "]";
                    this.uniformSizes.put(nextName, size);
                    this.uniformTypes.put(nextName, type_uniforms.get(0));
                    this.uniformLocations.put(nextName, location + k);
                }
            }
        }
        this.uniformNames = new String[uniformLocations.size];
        int i = 0;
        for (MapObjectInt.Entry<String> entry : uniformLocations) {
            this.uniformNames[i] = entry.key;
            i++;
        }
        /* instantiate cache */
        this.uniformsCache = new HashMap<>();

        /* validation */
        /* validate: limit the allowed max sampled textures */
        final int maxSampledTextures = Graphics.getMaxFragmentShaderTextureUnits();
        int sampledTextures = 0;
        for (MapObjectInt.Entry<String> uniform : uniformTypes.entries()) {
            int type = uniform.value;
            if (type == GL20.GL_SAMPLER_2D) sampledTextures++;
        }
        if (sampledTextures > maxSampledTextures) throw new GraphicsException("Error: shader code trying to sample " + sampledTextures + ". The allowed maximum on this hardware is " + maxSampledTextures);
        // TODO
        /* validate: attribute names should conform to ShaderVertexAttribute enum */
//        Set<String> invalidAttributeNames = new HashSet<>();
//        for (final String attributeName : attributeNames) {
//            if (VertexAttribute.isValidGlslAttributeName(attributeName)) continue;
//            invalidAttributeNames.add(attributeName);
//        }
//        if (!invalidAttributeNames.isEmpty()) {
//            throw new GraphicsException("Shader attributes contain invalid attribute names: \n" + invalidAttributeNames + "\n" +
//                    "You can only use the following attribute names in your shaders: " + VertexAttribute.getValidAttributeNames());
//        }
    }

    public boolean hasVertexAttribute(final VertexAttribute attribute) {
        return (vertexAttributesBitmask & attribute.bitmask) != 0;
    }

    @Deprecated private String preprocessVertexShader(final String vertexShaderSource) {
        Matcher comments = GLSL_COMMENT_PATTERN.matcher(vertexShaderSource);
        String noComments = comments.replaceAll("");
        Matcher layouts = GLSL_LAYOUT_IN_PATTERN.matcher(noComments);
        return layouts.replaceAll("in");
    }

    @Deprecated private String preprocessFragmentShader(final String fragmentShaderSource) {
        // TODO: see how this should be preprocessed
        return fragmentShaderSource;
    }

    // TODO: maybe change back to protected. This is very error prone because a uniform bind is a state change that must be observed by the Renderer2D.
    public final void bindUniforms(final HashMap<String, Object> uniforms) {
        if (uniforms == null) return;
        for (Map.Entry<String, Object> entry : uniforms.entrySet()) {
            final String name = entry.getKey();
            final Object value = uniforms.get(name);
            try {
                bindUniform(name, value);
            } catch (Exception e) {
                throw new GraphicsException("Trying to bind " + null + " value to a shader uniform: \n" + "name:  <" + name + ">" + "\n" + "value: <" + value + ">");
            }
        }
    }

    public boolean uniformExists(final String name) {
        final int location = uniformLocations.get(name, -1);
        return location != -1;
    }

    public int getUniformLocation(final String name) {
        return uniformLocations.get(name, -1);
    }

    public Object getUniformValue(final String name) {
        final int location = uniformLocations.get(name, -1);
        if (location == -1) return null;

        return uniformsCache.get(location);
    }

    public void bindUniform(final String name, final Object value) {
        if (value == null) throw new GraphicsException("Trying to bind null value to a uniform variable.");
        final int location = uniformLocations.get(name, -1);
        if (location == -1) throw new GraphicsException("\n\nError: " + this.getClass().getSimpleName() +  " does not have a uniform named " + name + "." + "\nIf you have defined the uniform but have not used it, the GLSL compiler discarded it.\n");
        final int type = uniformTypes.get(name, -1);
        switch (type) {

            case GL20.GL_SAMPLER_2D -> {
                Texture texture = (Texture) value;
                int slot = TextureBinder.bind(texture);
                final Integer cache = (Integer) uniformsCache.get(location);
                if (cache == null || !cache.equals(slot)) {
                    GL20.glUniform1i(location, slot); // bind
                    uniformsCache.put(location, slot);
                }
            }

            // TODO: cubemaps textures

            // TODO: 3d textures
            case GL20.GL_SAMPLER_3D -> {

            }

            case GL20.GL_BOOL -> {
                boolean b = (Boolean) value;
                final Boolean cache = (Boolean) uniformsCache.get(location);
                if (cache == null || !cache.equals(b)) {
                    GL20.glUniform1i(location, b ? GL20.GL_TRUE : GL20.GL_FALSE);  // bind
                    uniformsCache.put(location, b);
                }
            }

            case GL20.GL_INT -> {
                int i = (Integer) value;
                final Integer cache = (Integer) uniformsCache.get(location);
                if (cache == null || !cache.equals(i)) {
                    GL20.glUniform1i(location, i); // bind
                    uniformsCache.put(location, i);
                }
            }

            case GL20.GL_FLOAT -> {
                //float f = (Float) value;
                float f = ((Number) value).floatValue();
                final Float cache = (Float) uniformsCache.get(location);
                if (cache == null || !cache.equals(f)) {
                    GL20.glUniform1f(location, f); // bind
                    uniformsCache.put(location, f);
                }
            }

            case GL20.GL_FLOAT_MAT4 -> {
                Matrix4x4 matrix4 = (Matrix4x4) value;
                final Matrix4x4 cache = (Matrix4x4) uniformsCache.get(location);
                if (cache == null) {
                    GL20.glUniformMatrix4fv(location, false, matrix4.val); // bind
                    Matrix4x4 newCache = new Matrix4x4();
                    newCache.set(matrix4);
                    uniformsCache.put(location, newCache);
                } else if (!cache.equals(matrix4)) {
                    GL20.glUniformMatrix4fv(location, false, matrix4.val); // bind
                    cache.set(matrix4);
                }
            }

            case GL20.GL_FLOAT_VEC2 -> {
                Vector2 vector2 = (Vector2) value;
                final Vector2 cache = (Vector2) uniformsCache.get(location);
                if (cache == null) {
                    GL20.glUniform2f(location, vector2.x, vector2.y); // bind
                    Vector2 newCache = new Vector2(); // create cache
                    newCache.set(vector2);
                    uniformsCache.put(location, newCache);
                } else if (!cache.equals(vector2)) {
                    GL20.glUniform2f(location, vector2.x, vector2.y); // bind
                    cache.set(vector2); // store cache
                }
            }

            case GL20.GL_FLOAT_VEC3 -> {
                Vector3 vector3 = (Vector3) value;
                final Vector3 cache = (Vector3) uniformsCache.get(location);
                if (cache == null) {
                    GL20.glUniform3f(location, vector3.x, vector3.y, vector3.z); // bind
                    Vector3 newCache = new Vector3(); // create cache
                    newCache.set(vector3); // store cache
                } else if (!cache.equals(vector3)) {
                    GL20.glUniform3f(location, vector3.x, vector3.y, vector3.z); // bind
                    cache.set(vector3); // store cache
                }
            }

            case GL20.GL_FLOAT_VEC4 -> {
                final Vector4 cache = (Vector4) uniformsCache.get(location);
                if (value instanceof Color) {
                    Color color = (Color) value;
                    if (cache == null) {
                        GL20.glUniform4f(location, color.r, color.g, color.b, color.a); // bind
                        Vector4 newCache = new Vector4(); // create cache
                        newCache.set(color.r, color.g, color.b, color.a); // store cache
                    } else if (cache.x != color.r || cache.y != color.g || cache.z != color.b || cache.w != color.a) {
                        GL20.glUniform4f(location, color.r, color.g, color.b, color.a); // bind
                        cache.set(color.r, color.g, color.b, color.a); // store cache
                    }
                } else if (value instanceof Vector4) {
                    Vector4 vector4 = (Vector4) value;
                    if (cache == null) {
                        GL20.glUniform4f(location, vector4.x, vector4.y, vector4.z, vector4.w); // bind
                        Vector4 newCache = new Vector4(); // create cache
                        newCache.set(vector4); // store cache
                    } else if (!cache.equals(vector4)) {
                        GL20.glUniform4f(location, vector4.x, vector4.y, vector4.z, vector4.w); // bind
                        cache.set(vector4); // store cache
                    }
                }
            }

        }
    }

    @Override
    public void delete() {
        if (deleted) return;
        GL20.glUseProgram(0);
        GL20.glDeleteProgram(vertexShaderId);
        if (geometryShaderId != -1) GL20.glDeleteProgram(geometryShaderId);
        GL20.glDeleteProgram(fragmentShaderId);
        GL20.glDeleteProgram(program);
        deleted = true;
    }

    @Override
    public String toString() {
        return "Shader : " + program + '\n' +
                "Vertex Shader : " + '\n' + vertexShaderSource + '\n' +
                "Fragment Shader : " + '\n' + fragmentShaderSource + '\n' +
                "Attributes Bitmask : " + '\n' + Integer.toBinaryString(vertexAttributesBitmask) + '\n';
    }

}
