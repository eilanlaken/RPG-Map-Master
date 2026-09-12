package com.heavybox.jtix.graphics;

import com.heavybox.jtix.collections.*;
import com.heavybox.jtix.math.MathUtils;
import com.heavybox.jtix.math.Matrix4x4;
import com.heavybox.jtix.math.Vector2;
import com.heavybox.jtix.math.Vector4;
import com.heavybox.jtix.memory.MemoryPool;
import com.heavybox.jtix.memory.MemoryResourceHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;
import org.lwjgl.system.MemoryUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

// TODO: in Graphics.cleanup(), call Renderer2D.delete()
// TODO: in FrameBufferBinder.bind(), throw an exception if Renderer2D or Renderer3D is in drawing state.
// TODO: in begin(), first check if Renderer3D isDrawing = true. They cannot step on each other.
public class Renderer2D implements MemoryResourceHolder {

    private static final Vector2    tmp_Vector     = new Vector2(); // used for in-place optimization.
    private static final ArrayInt   tmp_ArrayInt   = new ArrayInt(true, 8);
    private static final ArrayFloat tmp_ArrayFloat = new ArrayFloat(true, 10);

    private static final int VERTICES_CAPACITY = 8000; // The batch can render VERTICES_CAPACITY vertices (so wee need float buffers of size: VERTICES_CAPACITY * 2 for positions, * 1 for colors, * 2 for uvs etc.)

    private static final Shader  defaultShader  = createDefaultShaderProgram();
    private static final Texture defaultTexture = createDefaultTexture();
    private static final Camera  defaultCamera  = createDefaultCamera();
    private static final Font    defaultFont    = createDefaultFont(); // change back to private

    /* memory pools */ // TODO: remove all these memory pools. Replace with static? arrays.
    private final MemoryPool<Vector2>    vectors2Pool   = new MemoryPool<>(Vector2.class, 10);
    private final MemoryPool<Vector4>    vectors4Pool   = new MemoryPool<>(Vector4.class, 10);
    private final MemoryPool<ArrayFloat> arrayFloatPool = new MemoryPool<>(ArrayFloat.class, 20);
    private final MemoryPool<ArrayInt>   arrayIntPool   = new MemoryPool<>(ArrayInt.class, 20);
    private final Array<Vector2>         vertices       = new Array<>(true, 50);

    /* state */
    private final Stack<Vector4> pixelBounds  = new Stack<>(); // the head of the stack stores the current rectangle bounds for rendering as a Vector4 (x = min_x, y = min_y, z = max_x, w = max_y)
    private Camera    currentCamera       = defaultCamera;
    private Texture   currentTexture      = defaultTexture;
    private Font      currentFont         = defaultFont; // TODO
    private Shader    currentShader       = null;
    private float     currentTint         = Color.WHITE_FLOAT;
    private boolean   drawing             = false;
    private int       vertexIndex         = 0;
    private int       currentMode         = GL11.GL_TRIANGLES;
    private int       perFrameDrawCalls   = 0;
    private float     pixelScaleWidth     = 1;
    private float     pixelScaleWidthInv  = 1;
    private float     pixelScaleHeight    = 1;
    private float     pixelScaleHeightInv = 1;

    /* Vertex Buffers */
    private final int         vao;
    private final int         vboPositions;
    private final int         vboColors;
    private final int         vboTextCoords;
    private final int         ebo;
    private final FloatBuffer positions;
    private final FloatBuffer colors;
    private final FloatBuffer textCoords;
    private final IntBuffer   indices;

    // TODO user this vertex capacitor
    // a sparse array containing float buffer for each 2d vertex attribute.
    /*
    vboBatch[POSITION_2D] = positions;
    vboBatch[COLOR]       = colors;
    ...
     */
    private final FloatBuffer[] vaoBatch = new FloatBuffer[VertexAttribute.USED_FOR_2D_RENDERING.length]; // TODO
    private final int[]         vbos     = new int[VertexAttribute.USED_FOR_2D_RENDERING.length]; // TODO

    /* masking */
    private boolean drawingToStencil = false;
    private boolean maskingEnabled   = false;

    public Renderer2D() {
        positions  = BufferUtils.createFloatBuffer(VERTICES_CAPACITY * 2);
        colors     = BufferUtils.createFloatBuffer(VERTICES_CAPACITY);
        textCoords = BufferUtils.createFloatBuffer(VERTICES_CAPACITY * 2);
        indices    = BufferUtils.createIntBuffer(VERTICES_CAPACITY * 2);

        this.vao = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vao);

        this.vboPositions = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboPositions); // bind
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, positions, GL15.GL_DYNAMIC_DRAW);
        GL20.glVertexAttribPointer(0, 2, GL11.GL_FLOAT, false, 0, 0);

        this.vboColors = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboColors); // bind
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, colors, GL15.GL_DYNAMIC_DRAW);
        GL20.glVertexAttribPointer(1, 4, GL11.GL_UNSIGNED_BYTE, true, 0, 0);

        this.vboTextCoords = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboTextCoords); // bind
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, textCoords, GL15.GL_DYNAMIC_DRAW);
        GL20.glVertexAttribPointer(2, 2, GL11.GL_FLOAT, false, 0, 0);

        this.ebo = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, ebo);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indices, GL15.GL_DYNAMIC_DRAW);

        GL30.glBindVertexArray(0);
    }

    // TODO this is the generalized 2d renderer
//    public Renderer2D(boolean placeholder) {
//        this.vao = GL30.glGenVertexArrays();
//        GL30.glBindVertexArray(vao);
//        Arrays.fill(vbos, -1);
//
//        for (VertexAttribute attribute : ATTRIBUTES) {
//            FloatBuffer buffer = BufferUtils.createFloatBuffer(VERTICES_CAPACITY * attribute.dimension);
//            vaoBatch[attribute.ordinal()] = buffer;
//            final int vbo = GL15.glGenBuffers();
//            vbos[attribute.ordinal()] = vbo;
//
//            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo); // bind
//            GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_DYNAMIC_DRAW);
//            GL20.glVertexAttribPointer(attribute.glslLocation, attribute.dimension, attribute.glType, attribute.normalized, 0, 0);
//        }
//        indices = BufferUtils.createIntBuffer(VERTICES_CAPACITY * 2);
//        this.ebo = GL15.glGenBuffers();
//        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, ebo);
//        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indices, GL15.GL_STATIC_DRAW);
//
//        positions = vaoBatch[VertexAttribute.POSITION_2D.ordinal()];
//        colors = vaoBatch[VertexAttribute.COLOR.ordinal()];
//        textCoords = vaoBatch[VertexAttribute.TEXT_COORDS0.ordinal()];
//
//        GL30.glBindVertexArray(0);
//    }

    public Camera getCurrentCamera() {
        return currentCamera;
    }

    public boolean isDrawing() {
        return drawing;
    }

    public int getPerFrameDrawCalls() { return perFrameDrawCalls; }

    public void begin() {
        begin(null);
    }

    public void begin(Camera camera) {
        if (drawing) throw new GraphicsException("Already in a drawing state; Must call " + Renderer2D.class.getSimpleName() + ".end() before calling begin().");
        Graphics.activeRenderer2Ds++;
        GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT); // TODO: WHY? probably NOT a desired behaviour. Cleaning should be explicit.
        GL11.glColorMask(true, true, true, true); // enable color buffer writes
        GL20.glDepthMask(false);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_BLEND);

        /* init pixel bounds */
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        while (!pixelBounds.isEmpty()) {
            Vector4 bounds = pixelBounds.pop();
            vectors4Pool.free(bounds);
        }

        /* stencil buffer and masking */
        drawingToStencil = false;
        maskingEnabled = false;

        /* set blend function to default blending */
        //GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        this.perFrameDrawCalls = 0;

        // set camera
        if (camera == null) {
            currentCamera = defaultCamera;
            currentCamera.viewportWidth = Graphics.getWindowWidth();
            currentCamera.viewportHeight = Graphics.getWindowHeight();
        } else {
            currentCamera = camera;
        }
        pixelScaleWidth = Graphics.getWindowWidth() / currentCamera.viewportWidth;
        pixelScaleHeight = Graphics.getWindowHeight() / currentCamera.viewportHeight;
        pixelScaleWidthInv = 1.0f / pixelScaleWidth;
        pixelScaleHeightInv = 1.0f / pixelScaleHeight;
        currentCamera.update(); // TODO: probably remove. Redundant update()s

        setShader(defaultShader);
        blendingSet(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA); // TODO: test
        setShaderUniforms(null);
        setTexture(defaultTexture);
        setMode(GL11.GL_TRIANGLES);
        setColor(Color.WHITE_FLOAT);
        this.drawing = true;
    }

    /* State */

    public void setShader(Shader shader) {
        if (shader == null) shader = defaultShader;
        if (currentShader == shader) return;
        flush();
        shader.bind();
        if (shader.uniformExists("u_camera_combined")) shader.bindUniform("u_camera_combined", currentCamera.combined);
        if (shader.uniformExists("u_texture")) shader.bindUniform("u_texture", currentTexture);
        currentShader = shader;
    }

    private void setTexture(@Nullable Texture texture) {
        if (texture == null) texture = defaultTexture;
        if (currentTexture == texture) return;
        flush();
        currentTexture = texture;
        if (currentShader.uniformExists("u_texture")) currentShader.bindUniform("u_texture", currentTexture); // TODO: optimize
    }

    public void setFont(Font font) {
        if (font == null) font = defaultFont;
        if (currentFont == font) return;
        flush();
        currentFont = font;
    }

    public void setShaderUniform(String uniformName, Object uniformValue) {
        flush();
        currentShader.bindUniform(uniformName, uniformValue);
    }

    public void setShaderUniforms(@Nullable HashMap<String, Object> uniformValues) {
        flush();
        currentShader.bindUniforms(uniformValues);
    }

    private void setMode(final int mode) {
        if (currentMode == mode) return;
        flush();
        this.currentMode = mode;
    }

    public void blendingSet(int sFactor, int dFactor) {
        flush();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(sFactor, dFactor);
    }

    public void blendingSet(int sFactorRGB, int dFactorRGB, int sFactorAlpha, int dFactorAlpha) {
        flush();
        GL11.glEnable(GL11.GL_BLEND);
        GL14.glBlendFuncSeparate(sFactorRGB, dFactorRGB, sFactorAlpha, dFactorAlpha);
    }

    public void blendingEnable() {
        flush();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
    }

    public void blendingDisable() {
        flush();
        GL11.glDisable(GL11.GL_BLEND);
    }

    public void setColor(final Color color) {
        if (color == null) setColor(Color.WHITE_FLOAT);
        else setColor(color.toFloatBits());
    }

    public void setColor(float r, float g, float b, float a) {
        setColor(Color.toFloatBits(r,g,b,a));
    }

    public void setColor(float tintFloatBits) {
        this.currentTint = tintFloatBits;
    }

    public void pushPixelBounds(int min_x, int min_y, int max_x, int max_y) {
        flush();
        if (pixelBounds.isEmpty()) {
            GL11.glEnable(GL11.GL_SCISSOR_TEST);
            Vector4 newBounds = vectors4Pool.allocate();
            newBounds.x = min_x;
            newBounds.y = min_y;
            newBounds.z = max_x;
            newBounds.w = max_y;
            pixelBounds.push(newBounds);
        } else {
            Vector4 currentBounds = pixelBounds.peek();
            Vector4 newBounds = vectors4Pool.allocate();
            newBounds.x = Math.max(min_x, currentBounds.x);
            newBounds.y = Math.max(min_y, currentBounds.y);
            newBounds.z = Math.min(max_x, currentBounds.z);
            newBounds.w = Math.min(max_y, currentBounds.w);
            pixelBounds.push(newBounds);
        }

        Vector4 currentBounds = pixelBounds.peek();
        int x = (int) currentBounds.x;
        int y = (int) currentBounds.y;
        int width = (int) (currentBounds.z - currentBounds.x);
        int height = (int) (currentBounds.w - currentBounds.y);
        GL11.glScissor(x, y, width, height);
    }

    public void popPixelBounds() {
        flush();
        if (pixelBounds.isEmpty()) throw new GraphicsException("Trying to popPixelBound() without a matching pushPixelBound()");
        Vector4 bounds = pixelBounds.pop();
        vectors4Pool.free(bounds);
        if (pixelBounds.isEmpty()) {
            GL11.glDisable(GL11.GL_SCISSOR_TEST);
            return;
        }

        Vector4 currentBounds = pixelBounds.peek();
        int x = (int) currentBounds.x;
        int y = (int) currentBounds.y;
        int width = (int) (currentBounds.z - currentBounds.x);
        int height = (int) (currentBounds.w - currentBounds.y);
        GL11.glScissor(x, y, width, height);
    }

    /* masking and stencil testing */

    /*
    pauses rendering to the color buffer and begins rendering to the stencil buffer:
    from now on, every draw call will write to the stencil buffer; the written values
    depends on the selected mode. By default, it writes 1s.
     */
    public void beginStencil() {
        if (!drawing) throw new GraphicsException("Stencil write operations must be made within a begin() and end() blocks.");
        if (drawingToStencil) throw new GraphicsException("call to beginMask() must be followed by a call to endMask() before subsequent calls to beginMask()");
        //if (maskingEnabled) throw new GraphicsException("Stencil and Masking blocks must be separated and not nested: cannot call stencilMaskBegin() while masking is enabled.");
        if (maskingEnabled) disableMasking();
        drawingToStencil = true;
        flush();
        GL11.glEnable(GL11.GL_STENCIL_TEST);
        GL11.glStencilMask(0xFF);
        GL11.glColorMask(false, false, false, false); // Disable color buffer writes
        GL11.glDepthMask(false); // Disable depth buffer writes
        setStencilModeReplace(1);
    }

    public void setStencilModeReplace(int value) {
        if (!drawingToStencil) throw new GraphicsException("call this method only after beginMask() and endMask()");
        flush();
        GL11.glStencilFunc(GL11.GL_ALWAYS, value, 0xFF); // Always pass, ref value = 1
        GL11.glStencilOp(GL11.GL_REPLACE, GL11.GL_REPLACE, GL11.GL_REPLACE);   // Replace stencil value with ref (1)
    }

    public void setStencilModeIncrement() {
        if (!drawingToStencil) throw new GraphicsException("call this method only after beginMask() and endMask()");
        flush();
        // always increase stencil value by 1
        GL11.glStencilFunc(GL11.GL_ALWAYS, 1, 0xFF);
        GL11.glStencilOp(GL11.GL_INCR, GL11.GL_INCR, GL11.GL_INCR);
    }

    public void setStencilModeDecrement() {
        if (!drawingToStencil) throw new GraphicsException("call this method only after beginMask() and endMask()");
        flush();
        // always decrease stencil value by 1
        GL11.glStencilFunc(GL11.GL_ALWAYS, 1, 0xFF); // Always pass, ref value = 1
        GL11.glStencilOp(GL11.GL_DECR, GL11.GL_DECR, GL11.GL_DECR);
    }

    public void endStencil() {
        if (!drawingToStencil) throw new GraphicsException("call to beginMask() expected before endMask()");
        flush();
        GL11.glColorMask(true, true, true, true); // Enable color buffer writes
        GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_KEEP); // Do not modify stencil values
        drawingToStencil = false;
    }

    public void stencilBufferClear() {
        //if (!drawingToStencil) throw new GraphicsException("Cannot clear the stencil while drawing to stencil. Must call endStencil() first.");
        GL11.glClearStencil(0); // Set stencil clear value to 1
        GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT);
    }

    public void stencilBufferClear(int value) {
        //if (!drawingToStencil) throw new GraphicsException("Cannot clear the stencil while drawing to stencil. Must call stencilMaskEnd() first.");
        GL11.glClearStencil(value); // Set stencil clear value to 1
        GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT);
        GL11.glClearStencil(0); // Set stencil clear value to 0
    }

    // enable disable masking
    public void enableMasking() {
        if (drawingToStencil) throw new GraphicsException("Cannot apply mask when drawing to a stencil buffer");
        flush();
        maskingEnabled = true;
        GL11.glEnable(GL11.GL_STENCIL_TEST);
        setMaskingFunctionEquals(1);
    }

    public void setMaskingFunctionNever(int reference) {
        setMaskingFunction(GL11.GL_NEVER, reference);
    }

    public void setMaskingFunctionAlways(int reference) {
        setMaskingFunction(GL11.GL_ALWAYS, reference);
    }

    public void setMaskingFunctionEquals(int reference) {
        setMaskingFunction(GL11.GL_EQUAL, reference);
    }

    public void setMaskingFunctionNotEquals(int reference) {
        setMaskingFunction(GL11.GL_NOTEQUAL, reference);
    }

    public void setMaskingFunctionLess(int reference) {
        setMaskingFunction(GL11.GL_LESS, reference);
    }

    public void setMaskingFunctionLessEquals(int reference) {
        setMaskingFunction(GL11.GL_LEQUAL, reference);
    }

    public void setMaskingFunctionGreater(int reference) {
        setMaskingFunction(GL11.GL_GREATER, reference);
    }

    public void setMaskingFunctionGreaterEquals(int reference) {
        setMaskingFunction(GL11.GL_GEQUAL, reference);
    }

    private void setMaskingFunction(int glStencilFunc, int reference) {
        //if (drawingToStencil) throw new GraphicsException("setMaskingFunction should not be used while drawing to the stencil buffer, only when reading from it.");
        if (!maskingEnabled) throw new GraphicsException("setMaskingFunction() should be called only between enableMasking() and disableMasking().");
        flush();
        GL11.glStencilFunc(glStencilFunc, reference, 0xFF);
    }

    public void disableMasking() {
        if (!maskingEnabled) return;
        flush();
        maskingEnabled = false;
        GL11.glDisable(GL11.GL_STENCIL_TEST);
    }

    /* Rendering API */

    /* Rendering 2D primitives - Textures */

    public void drawTexture(@NotNull Texture texture, float x, float y, float degrees, float scaleX, float scaleY) {
        if (!drawing) throw new GraphicsException("Must call begin() before draw operations.");
        if (requiresFlush(4, 6)) flush();

        setTexture(texture);
        setMode(GL11.GL_TRIANGLES);

        float widthHalf  = texture.width  * scaleX * 0.5f;
        float heightHalf = texture.height * scaleY * 0.5f;

        tmp_Vector.x = -widthHalf;
        tmp_Vector.y =  heightHalf;
        tmp_Vector.rotateDeg(degrees);
        positions.put(tmp_Vector.x + x).put(tmp_Vector.y + y);
        colors.put(currentTint);
        textCoords.put(0).put(0);

        tmp_Vector.x = -widthHalf;
        tmp_Vector.y = -heightHalf;
        tmp_Vector.rotateDeg(degrees);
        positions.put(tmp_Vector.x + x).put(tmp_Vector.y + y);
        colors.put(currentTint);
        textCoords.put(0).put(1);

        tmp_Vector.x = widthHalf;
        tmp_Vector.y = -heightHalf;
        tmp_Vector.rotateDeg(degrees);
        positions.put(tmp_Vector.x + x).put(tmp_Vector.y + y);
        colors.put(currentTint);
        textCoords.put(1).put(1);

        tmp_Vector.x = widthHalf;
        tmp_Vector.y = heightHalf;
        tmp_Vector.rotateDeg(degrees);
        positions.put(tmp_Vector.x + x).put(tmp_Vector.y + y);
        colors.put(currentTint);
        textCoords.put(1).put(0);

        /* put indices */
        int startVertex = this.vertexIndex;
        indices.put(startVertex + 0);
        indices.put(startVertex + 1);
        indices.put(startVertex + 3);
        indices.put(startVertex + 3);
        indices.put(startVertex + 1);
        indices.put(startVertex + 2);
        vertexIndex += 4;
    }


    public void drawTexture(@NotNull Texture texture, float u1, float v1, float u2, float v2,
                            float x, float y, float deg, float scaleX, float scaleY) {
        if (!drawing) throw new GraphicsException("Must call begin() before draw operations.");
        if (requiresFlush(4, 6)) flush();

        setTexture(texture);
        setMode(GL11.GL_TRIANGLES);

        float width = texture.width * scaleX;
        float height = texture.height * scaleY;
        float widthHalf = width * 0.5f;
        float heightHalf = height * 0.5f;

        /* arm0 */
        tmp_Vector.x = -widthHalf + width * u1;
        tmp_Vector.y = heightHalf - height * v1;
        tmp_Vector.rotateDeg(deg);
        positions.put(tmp_Vector.x + x).put(tmp_Vector.y + y);
        colors.put(currentTint);
        textCoords.put(u1).put(v1);

        /* arm1 */
        tmp_Vector.x = -widthHalf + width * u1;
        tmp_Vector.y = -heightHalf + height * (1 - v2);
        tmp_Vector.rotateDeg(deg);
        positions.put(tmp_Vector.x + x).put(tmp_Vector.y + y);
        colors.put(currentTint);
        textCoords.put(u1).put(v2);

        /* arm2 */
        tmp_Vector.x = widthHalf - width * (1 - u2);
        tmp_Vector.y = -heightHalf + height * (1 - v2);
        tmp_Vector.rotateDeg(deg);
        positions.put(tmp_Vector.x + x).put(tmp_Vector.y + y);
        colors.put(currentTint);
        textCoords.put(u2).put(v2);

        /* arm3 */
        tmp_Vector.x = widthHalf - width * (1 - u2);
        tmp_Vector.y = heightHalf - height * v1;
        tmp_Vector.rotateDeg(deg);
        positions.put(tmp_Vector.x + x).put(tmp_Vector.y + y);
        colors.put(currentTint);
        textCoords.put(u2).put(v1);

        /* indices */
        int startVertex = vertexIndex;
        indices.put(startVertex + 0);
        indices.put(startVertex + 1);
        indices.put(startVertex + 3);
        indices.put(startVertex + 3);
        indices.put(startVertex + 1);
        indices.put(startVertex + 2);

        vertexIndex += 4;
    }

    // TODO: remove this
    @Deprecated
    public void drawTextureRegion_old(@NotNull TextureRegion region, float x, float y, float degrees, float scaleX, float scaleY) {
        if (!drawing) throw new GraphicsException("Must call begin() before draw operations.");
        if (requiresFlush(4, 6)) flush();

        setTexture(region.texture);
        setMode(GL11.GL_TRIANGLES);

        final float ui = region.u1;
        final float vi = region.v1;
        final float uf = region.u2;
        final float vf = region.v2;
        final float offsetX = region.offsetX;
        final float offsetY = region.offsetY;
        final float packedWidth = region.packedWidth;
        final float packedHeight = region.packedHeight;
        final float originalWidthHalf = region.originalWidthHalf;
        final float originalHeightHalf = region.originalHeightHalf;

        float localX1 = offsetX - originalWidthHalf;
        float localX2 = offsetX - originalWidthHalf;
        float localX3 = offsetX - originalWidthHalf + packedWidth;
        float localX4 = offsetX - originalWidthHalf + packedWidth;
        float localY1 = offsetY - originalHeightHalf + packedHeight;
        float localY4 = offsetY - originalHeightHalf + packedHeight;
        float localY2 = offsetY - originalHeightHalf;
        float localY3 = offsetY - originalHeightHalf;

        /* apply scale */
        localX1 *= scaleX;
        localX2 *= scaleX;
        localX3 *= scaleX;
        localX4 *= scaleX;
        localY1 *= scaleY;
        localY2 *= scaleY;
        localY3 *= scaleY;
        localY4 *= scaleY;

        /* apply rotation */
        final float sin = MathUtils.sinDeg(degrees);
        final float cos = MathUtils.cosDeg(degrees);
        float x1 = localX1 * cos - localY1 * sin;
        float y1 = localX1 * sin + localY1 * cos;
        float x2 = localX2 * cos - localY2 * sin;
        float y2 = localX2 * sin + localY2 * cos;
        float x3 = localX3 * cos - localY3 * sin;
        float y3 = localX3 * sin + localY3 * cos;
        float x4 = localX4 * cos - localY4 * sin;
        float y4 = localX4 * sin + localY4 * cos;

        /* apply translation */
        x1 += x;
        y1 += y;
        x2 += x;
        y2 += y;
        x3 += x;
        y3 += y;
        x4 += x;
        y4 += y;

        /* put vertices */
        positions.put(x1).put(y1);
        colors.put(currentTint);
        textCoords.put(ui).put(vi);

        positions.put(x2).put(y2);
        colors.put(currentTint);
        textCoords.put(ui).put(vf);

        positions.put(x3).put(y3);
        colors.put(currentTint);
        textCoords.put(uf).put(vf);

        positions.put(x4).put(y4);
        colors.put(currentTint);
        textCoords.put(uf).put(vi);

        /* put indices */
        int startVertex = this.vertexIndex;
        indices.put(startVertex + 0);
        indices.put(startVertex + 1);
        indices.put(startVertex + 3);
        indices.put(startVertex + 3);
        indices.put(startVertex + 1);
        indices.put(startVertex + 2);
        vertexIndex += 4;
    }

    public void drawTextureRegion(@NotNull TextureRegion region, float x, float y, float degrees, float scaleX, float scaleY) {
        if (!drawing) throw new GraphicsException("Must call begin() before draw operations.");
        if (requiresFlush(4, 6)) flush();

        setTexture(region.texture);
        setMode(GL11.GL_TRIANGLES);

        final float ui = region.u1;
        final float vi = region.v1;
        final float uf = region.u2;
        final float vf = region.v2;
        final float offsetX = region.offsetX;
        final float offsetY = region.offsetY;
        final float packedWidth = region.packedWidth;
        final float packedHeight = region.packedHeight;
        final float originalWidthHalf = region.originalWidthHalf;
        final float originalHeightHalf = region.originalHeightHalf;

        /* calculate local bounds */
        final float left = (offsetX - originalWidthHalf) * scaleX;
        final float right = left + packedWidth * scaleX;
        final float bottom = (offsetY - originalHeightHalf) * scaleY;
        final float top = bottom + packedHeight * scaleY;

        /* rotation */
        final float sin = MathUtils.sinDeg(degrees);
        final float cos = MathUtils.cosDeg(degrees);
        final float leftCos = left * cos;
        final float leftSin = left * sin;
        final float rightCos = right * cos;
        final float rightSin = right * sin;
        final float bottomCos = bottom * cos;
        final float bottomSin = bottom * sin;
        final float topCos = top * cos;
        final float topSin = top * sin;

        /* calculate corners */
        final float x1 = leftCos - topSin + x;
        final float y1 = leftSin + topCos + y;
        final float x2 = leftCos - bottomSin + x;
        final float y2 = leftSin + bottomCos + y;
        final float x3 = rightCos - bottomSin + x;
        final float y3 = rightSin + bottomCos + y;
        final float x4 = rightCos - topSin + x;
        final float y4 = rightSin + topCos + y;

        /* put vertices */
        positions.put(x1).put(y1);
        colors.put(currentTint);
        textCoords.put(ui).put(vi);

        positions.put(x2).put(y2);
        colors.put(currentTint);
        textCoords.put(ui).put(vf);

        positions.put(x3).put(y3);
        colors.put(currentTint);
        textCoords.put(uf).put(vf);

        positions.put(x4).put(y4);
        colors.put(currentTint);
        textCoords.put(uf).put(vi);

        /* put indices */
        final int startVertex = this.vertexIndex;
        indices.put(startVertex);
        indices.put(startVertex + 1);
        indices.put(startVertex + 3);
        indices.put(startVertex + 3);
        indices.put(startVertex + 1);
        indices.put(startVertex + 2);
        vertexIndex += 4;
    }

    public void drawTextureRegion(@NotNull TextureRegion region,
                                  float u1, float v1, float u2, float v2,
                                  float x, float y, float degrees,
                                  float scaleX, float scaleY) {
        if (!drawing) throw new GraphicsException("Must call begin() before draw operations.");
        if (requiresFlush(4, 6)) flush();

        setTexture(region.texture);
        setMode(GL11.GL_TRIANGLES);

        final float regionU1 = region.u1;
        final float regionV1 = region.v1;
        final float regionU2 = region.u2;
        final float regionV2 = region.v2;

        final float packedWidth = region.packedWidth;
        final float packedHeight = region.packedHeight;
        final float originalWidthHalf = region.originalWidthHalf;
        final float originalHeightHalf = region.originalHeightHalf;

        final float localLeft = region.offsetX - originalWidthHalf + packedWidth * u1;
        final float localRight = region.offsetX - originalWidthHalf + packedWidth * u2;
        final float localBottom = region.offsetY - originalHeightHalf + packedHeight * (1.0f - v2);
        final float localTop = region.offsetY - originalHeightHalf + packedHeight * (1.0f - v1);

        final float scaledLeft = localLeft * scaleX;
        final float scaledRight = localRight * scaleX;
        final float scaledBottom = localBottom * scaleY;
        final float scaledTop = localTop * scaleY;

        final float sin = MathUtils.sinDeg(degrees);
        final float cos = MathUtils.cosDeg(degrees);

        final float textureU1 = regionU1 + (regionU2 - regionU1) * u1;
        final float textureU2 = regionU1 + (regionU2 - regionU1) * u2;
        final float textureV1 = regionV1 + (regionV2 - regionV1) * v1;
        final float textureV2 = regionV1 + (regionV2 - regionV1) * v2;

        // top-left
        float vertexX = scaledLeft * cos - scaledTop * sin + x;
        float vertexY = scaledLeft * sin + scaledTop * cos + y;

        positions.put(vertexX).put(vertexY);
        colors.put(currentTint);
        textCoords.put(textureU1).put(textureV1);

        // bottom-left
        vertexX = scaledLeft * cos - scaledBottom * sin + x;
        vertexY = scaledLeft * sin + scaledBottom * cos + y;

        positions.put(vertexX).put(vertexY);
        colors.put(currentTint);
        textCoords.put(textureU1).put(textureV2);

        // bottom-right
        vertexX = scaledRight * cos - scaledBottom * sin + x;
        vertexY = scaledRight * sin + scaledBottom * cos + y;

        positions.put(vertexX).put(vertexY);
        colors.put(currentTint);
        textCoords.put(textureU2).put(textureV2);

        // top-right
        vertexX = scaledRight * cos - scaledTop * sin + x;
        vertexY = scaledRight * sin + scaledTop * cos + y;

        positions.put(vertexX).put(vertexY);
        colors.put(currentTint);
        textCoords.put(textureU2).put(textureV1);

        int startVertex = vertexIndex;

        indices.put(startVertex);
        indices.put(startVertex + 1);
        indices.put(startVertex + 3);

        indices.put(startVertex + 3);
        indices.put(startVertex + 1);
        indices.put(startVertex + 2);

        vertexIndex += 4;
    }

    /* optimized rendering - equivalent to libGDXs' SpriteCache */
    // TODO
    public void drawMesh(@NotNull final Mesh mesh, float x, float y, float deg, float sclX, float sclY) {
        if (!drawing) throw new GraphicsException("Must call begin() before draw operations.");
        flush();

        Matrix4x4 transform = new Matrix4x4();
        float cos = MathUtils.cosDeg(deg);
        float sin = MathUtils.sinDeg(deg);
        transform.set(
                cos * sclX, -sin * sclY, 0.0f, x,
                sin * sclX,  cos * sclY, 0.0f,  y,
                0.0f,        0.0f,       1.0f, 0.0f,
                0.0f,        0.0f,       0.0f, 1.0f
        );
        Matrix4x4 transformedCombined = new Matrix4x4(currentCamera.combined).mul(transform);
        if (currentShader.uniformExists("u_camera_combined")) currentShader.bindUniform("u_camera_combined", transformedCombined);

        // render the vao using current shader

        if (currentShader.uniformExists("u_camera_combined")) currentShader.bindUniform("u_camera_combined", currentCamera.combined);
    }

    // TODO: draw meshes
    public void drawModel(@NotNull final Model model, float x, float y, float deg, float sclX, float sclY) {

    }

    /* Rendering 2D primitives - Circles */

    public void drawCircleThin(float r, int refinement, float x, float y, float degrees, float scaleX, float scaleY) {
        if (!drawing) throw new GraphicsException("Must call begin() before draw operations.");

        refinement = Math.max(refinement, 3);
        if (requiresFlush(refinement, 2 * (refinement + 1))) flush();

        setMode(GL11.GL_LINES);
        setTexture(defaultTexture);

        float da = 360f / refinement;
        for (int i = 0; i < refinement; i++) {
            tmp_Vector.x = r * scaleX * MathUtils.cosDeg(da * i);
            tmp_Vector.y = r * scaleY * MathUtils.sinDeg(da * i);
            tmp_Vector.rotateDeg(degrees);

            positions.put(tmp_Vector.x + x).put(tmp_Vector.y + y);
            textCoords.put(0.5f).put(0.5f);
            colors.put(currentTint);
        }

        // put indices
        int startVertex = this.vertexIndex;
        for (int i = 1; i < refinement; i++) {
            indices.put(startVertex + i - 1);
            indices.put(startVertex + i);
        }
        indices.put(startVertex + refinement - 1);
        indices.put(startVertex);

        vertexIndex += refinement;
    }

    public void drawCircleThin(float r, int refinement, float angle, float x, float y, float degrees, float scaleX, float scaleY) {
        if (!drawing) throw new GraphicsException("Must call begin() before draw operations.");

        refinement = Math.max(refinement, 3);

        int vertexCount = refinement + 1;
        int indexCount = refinement * 2;

        if (requiresFlush(vertexCount, indexCount)) flush();

        setMode(GL11.GL_LINES);
        setTexture(defaultTexture);

        float da = angle / refinement;

        for (int i = 0; i <= refinement; i++) {
            tmp_Vector.x = r * scaleX * MathUtils.cosDeg(da * i);
            tmp_Vector.y = r * scaleY * MathUtils.sinDeg(da * i);
            tmp_Vector.rotateDeg(degrees);

            positions.put(tmp_Vector.x + x).put(tmp_Vector.y + y);
            textCoords.put(0.5f).put(0.5f);
            colors.put(currentTint);
        }

        int startVertex = vertexIndex;

        for (int i = 0; i < refinement; i++) {
            indices.put(startVertex + i);
            indices.put(startVertex + i + 1);
        }

        vertexIndex += vertexCount;
    }

    // TODO: fix uv mappings?
    public void drawCircleFilled(float r, int refinement, float x, float y, float degrees, float scaleX, float scaleY) {
        if (!drawing) throw new GraphicsException("Must call begin() before draw operations.");

        refinement = Math.max(refinement, 3);
        int vertexCount = refinement + 1;
        int indexCount = refinement * 3;
        if (requiresFlush(vertexCount, indexCount)) flush();

        setMode(GL11.GL_TRIANGLES);
        setTexture(defaultTexture);

        /* center */
        positions.put(x).put(y);
        colors.put(currentTint);
        textCoords.put(0.5f).put(0.5f);

        float da = 360f / refinement;
        /* perimeter */
        for (int i = 0; i < refinement; i++) {
            tmp_Vector.x = r * scaleX * MathUtils.cosDeg(da * i);
            tmp_Vector.y = r * scaleY * MathUtils.sinDeg(da * i);
            tmp_Vector.rotateDeg(degrees);

            positions.put(x + tmp_Vector.x).put(y + tmp_Vector.y);
            colors.put(currentTint);
            textCoords.put(0.5f).put(0.5f);
        }

        int startVertex = vertexIndex;
        for (int i = 0; i < refinement; i++) {
            indices.put(startVertex);
            indices.put(startVertex + i + 1);
            indices.put(startVertex + (i + 1) % refinement + 1);
        }
        vertexIndex += vertexCount;
    }

    // TODO: fix uv mappings?
    // TODO: what if angle is 360?
    public void drawCircleFilled(float r, int refinement, float angle, float x, float y, float degrees, float scaleX, float scaleY) {
        if (!drawing) throw new GraphicsException("Must call begin() before draw operations.");

        refinement = Math.max(refinement, 3);
        int vertexCount = refinement + 2;
        int indexCount = refinement * 3;
        if (requiresFlush(vertexCount, indexCount)) flush();

        setMode(GL11.GL_TRIANGLES);
        setTexture(defaultTexture);

        // center
        positions.put(x).put(y);
        colors.put(currentTint);
        textCoords.put(0.5f).put(0.5f);

        float da = angle / refinement;
        // perimeter
        for (int i = 0; i <= refinement; i++) {
            tmp_Vector.x = r * scaleX * MathUtils.cosDeg(da * i);
            tmp_Vector.y = r * scaleY * MathUtils.sinDeg(da * i);
            tmp_Vector.rotateDeg(degrees);

            positions.put(x + tmp_Vector.x).put(y + tmp_Vector.y);
            colors.put(currentTint);
            textCoords.put(0.5f).put(0.5f);
        }

        int startVertex = vertexIndex;
        for (int i = 0; i < refinement; i++) {
            indices.put(startVertex);
            indices.put(startVertex + i + 1);
            indices.put(startVertex + i + 2);
        }

        vertexIndex += vertexCount;
    }

    // TODO: fix uv mappings?
    public void drawCircleBorder(float r, float thickness, int refinement, float x, float y, float degrees, float scaleX, float scaleY) {
        if (!drawing) throw new GraphicsException("Must call begin() before draw operations.");

        refinement = Math.max(3, refinement);
        int vertexCount = refinement * 2;
        int indexCount = refinement * 6;
        if (requiresFlush(vertexCount, indexCount)) flush();

        setMode(GL11.GL_TRIANGLES);
        setTexture(defaultTexture);

        float da = 360f / refinement;
        float halfBorder = thickness * 0.5f;
        for (int i = 0; i < refinement; i++) {
            float currentAngle = da * i;
            // inner
            tmp_Vector.x = scaleX * (r - halfBorder) * MathUtils.cosDeg(currentAngle);
            tmp_Vector.y = scaleY * (r - halfBorder) * MathUtils.sinDeg(currentAngle);
            tmp_Vector.rotateDeg(degrees);
            positions.put(tmp_Vector.x + x).put(tmp_Vector.y + y);
            colors.put(currentTint);
            textCoords.put(0.5f).put(0.5f);

            // outer
            tmp_Vector.x = scaleX * (r + halfBorder) * MathUtils.cosDeg(currentAngle);
            tmp_Vector.y = scaleY * (r + halfBorder) * MathUtils.sinDeg(currentAngle);
            tmp_Vector.rotateDeg(degrees);
            positions.put(tmp_Vector.x + x).put(tmp_Vector.y + y);
            colors.put(currentTint);
            textCoords.put(0.5f).put(0.5f);
        }

        int startVertex = vertexIndex;
        for (int i = 0; i < refinement - 1; i++) {
            int current = startVertex + i * 2;
            int next = current + 2;
            indices.put(current);
            indices.put(current + 1);
            indices.put(next);
            indices.put(next);
            indices.put(current + 1);
            indices.put(next + 1);
        }
        // close the ring
        int last = startVertex + refinement * 2 - 2;
        indices.put(last);
        indices.put(last + 1);
        indices.put(startVertex);
        indices.put(startVertex);
        indices.put(last + 1);
        indices.put(startVertex + 1);

        vertexIndex += vertexCount;
    }

    // TODO: fix uv mappings?
    public void drawCircleBorder(float r, float thickness, float angle, int refinement, float x, float y, float degrees, float scaleX, float scaleY) {
        if (!drawing) throw new GraphicsException("Must call begin() before draw operations.");

        refinement = Math.max(3, refinement);
        int vertexCount = refinement * 2;
        int indexCount = (refinement - 1) * 6;
        if (requiresFlush(vertexCount, indexCount)) flush();

        setMode(GL11.GL_TRIANGLES);
        setTexture(defaultTexture);

        float da = angle / refinement;
        float halfBorder = thickness * 0.5f;
        // render arc segments
        for (int i = 0; i < refinement; i++) {
            float currentAngle = da * i;

            // inner
            tmp_Vector.x = scaleX * (r - halfBorder) * MathUtils.cosDeg(currentAngle);
            tmp_Vector.y = scaleY * (r - halfBorder) * MathUtils.sinDeg(currentAngle);
            tmp_Vector.rotateDeg(degrees);
            positions.put(tmp_Vector.x + x).put(tmp_Vector.y + y);
            colors.put(currentTint);
            textCoords.put(0.5f).put(0.5f);

            // outer
            tmp_Vector.x = scaleX * (r + halfBorder) * MathUtils.cosDeg(currentAngle);
            tmp_Vector.y = scaleY * (r + halfBorder) * MathUtils.sinDeg(currentAngle);
            tmp_Vector.rotateDeg(degrees);
            positions.put(tmp_Vector.x + x).put(tmp_Vector.y + y);
            colors.put(currentTint);
            textCoords.put(0.5f).put(0.5f);
        }

        // put indices
        int startVertex = vertexIndex;

        for (int i = 0; i < refinement - 1; i++) {
            int current = startVertex + i * 2;
            int next = current + 2;

            indices.put(current);
            indices.put(current + 1);
            indices.put(next);

            indices.put(next);
            indices.put(current + 1);
            indices.put(next + 1);
        }

        vertexIndex += vertexCount;
    }

    public void drawRectangleThin(float width, float height, float x, float y, float deg, float sclX, float sclY) {
        if (!drawing) throw new GraphicsException("Must call begin() before draw operations.");
        if (requiresFlush(4, 8)) flush();

        setMode(GL11.GL_LINES);
        setTexture(defaultTexture);
        float widthHalf  = width * sclX * 0.5f;
        float heightHalf = height * sclY * 0.5f;

        // top-left
        tmp_Vector.x = -widthHalf;
        tmp_Vector.y = heightHalf;
        tmp_Vector.rotateDeg(deg);
        positions.put(tmp_Vector.x + x).put(tmp_Vector.y + y);
        colors.put(currentTint);
        textCoords.put(0.5f).put(0.5f);

        // bottom-left
        tmp_Vector.x = -widthHalf;
        tmp_Vector.y = -heightHalf;
        tmp_Vector.rotateDeg(deg);
        positions.put(tmp_Vector.x + x).put(tmp_Vector.y + y);
        colors.put(currentTint);
        textCoords.put(0.5f).put(0.5f);

        // bottom-right
        tmp_Vector.x = widthHalf;
        tmp_Vector.y = -heightHalf;
        tmp_Vector.rotateDeg(deg);
        positions.put(tmp_Vector.x + x).put(tmp_Vector.y + y);
        colors.put(currentTint);
        textCoords.put(0.5f).put(0.5f);

        // top-right
        tmp_Vector.x = widthHalf;
        tmp_Vector.y = heightHalf;
        tmp_Vector.rotateDeg(deg);
        positions.put(tmp_Vector.x + x).put(tmp_Vector.y + y);
        colors.put(currentTint);
        textCoords.put(0.5f).put(0.5f);

        int startVertex = vertexIndex;

        indices.put(startVertex);
        indices.put(startVertex + 1);

        indices.put(startVertex + 1);
        indices.put(startVertex + 2);

        indices.put(startVertex + 2);
        indices.put(startVertex + 3);

        indices.put(startVertex + 3);
        indices.put(startVertex);

        vertexIndex += 4;
    }

    // TODO: test
    public void drawRectangleThin(float x0, float y0, float x1, float y1, float x2, float y2, float x3, float y3) {
        if (!drawing) throw new GraphicsException("Must call begin() before draw operations.");
        if (requiresFlush(4, 8)) flush();

        setMode(GL11.GL_LINES);
        setTexture(defaultTexture);

        // put indices
        int startVertex = this.vertexIndex;
        indices
                .put(startVertex + 0)
                .put(startVertex + 1)
                .put(startVertex + 1)
                .put(startVertex + 2)
                .put(startVertex + 2)
                .put(startVertex + 3)
                .put(startVertex + 3)
                .put(startVertex + 0)
        ;

        positions.put(x0).put(y0);
        colors.put(currentTint);
        textCoords.put(0.5f).put(0.5f);

        positions.put(x1).put(y1);
        colors.put(currentTint);
        textCoords.put(0.5f).put(0.5f);

        positions.put(x2).put(y2);
        colors.put(currentTint);
        textCoords.put(0.5f).put(0.5f);

        positions.put(x3).put(y3);
        colors.put(currentTint);
        textCoords.put(0.5f).put(0.5f);

        vertexIndex += 4;
    }

    public void drawRectangleThin(float width, float height, float cornerRadius, int refinement, float x, float y, float deg, float sclX, float sclY) {
        if (!drawing) throw new GraphicsException("Must call begin() before draw operations.");

        refinement = Math.max(2, refinement);
        int vertexCount = refinement * 4;
        int indexCount = refinement * 4 * 2;
        if (requiresFlush(vertexCount, indexCount)) flush();

        setMode(GL11.GL_LINES);
        setTexture(defaultTexture);

        float widthHalf = width * 0.5f;
        float heightHalf = height * 0.5f;
        float da = 90.0f / (refinement - 1);

        // upper left
        for (int i = 0; i < refinement; i++) {
            tmp_Vector.set(-cornerRadius, 0);
            tmp_Vector.rotateDeg(-da * i);
            tmp_Vector.add(-widthHalf + cornerRadius, heightHalf - cornerRadius);
            tmp_Vector.scl(sclX, sclY).rotateDeg(deg).add(x, y);

            positions.put(tmp_Vector.x).put(tmp_Vector.y);
            colors.put(currentTint);
            textCoords.put(0.5f).put(0.5f);
        }

        // upper right
        for (int i = 0; i < refinement; i++) {
            tmp_Vector.set(0, cornerRadius);
            tmp_Vector.rotateDeg(-da * i);
            tmp_Vector.add(widthHalf - cornerRadius, heightHalf - cornerRadius);
            tmp_Vector.scl(sclX, sclY).rotateDeg(deg).add(x, y);

            positions.put(tmp_Vector.x).put(tmp_Vector.y);
            colors.put(currentTint);
            textCoords.put(0.5f).put(0.5f);
        }

        // lower right
        for (int i = 0; i < refinement; i++) {
            tmp_Vector.set(cornerRadius, 0);
            tmp_Vector.rotateDeg(-da * i);
            tmp_Vector.add(widthHalf - cornerRadius, -heightHalf + cornerRadius);
            tmp_Vector.scl(sclX, sclY).rotateDeg(deg).add(x, y);

            positions.put(tmp_Vector.x).put(tmp_Vector.y);
            colors.put(currentTint);
            textCoords.put(0.5f).put(0.5f);
        }

        // lower left
        for (int i = 0; i < refinement; i++) {
            tmp_Vector.set(0, -cornerRadius);
            tmp_Vector.rotateDeg(-da * i);
            tmp_Vector.add(-widthHalf + cornerRadius, -heightHalf + cornerRadius);
            tmp_Vector.scl(sclX, sclY).rotateDeg(deg).add(x, y);

            positions.put(tmp_Vector.x).put(tmp_Vector.y);
            colors.put(currentTint);
            textCoords.put(0.5f).put(0.5f);
        }

        int startVertex = vertexIndex;
        for (int i = 0; i < vertexCount; i++) {
            indices.put(startVertex + i);
            indices.put(startVertex + (i + 1) % vertexCount);
        }

        vertexIndex += vertexCount;
    }

    public void drawRectangleThin(float width, float height,
                                  float cornerRadiusTopLeft, int segmentsTopLeft,
                                  float cornerRadiusTopRight, int segmentsTopRight,
                                  float cornerRadiusBottomRight, int segmentsBottomRight,
                                  float cornerRadiusBottomLeft, int segmentsBottomLeft,
                                  float x, float y, float deg, float sclX, float sclY) {
        if (cornerRadiusTopLeft == 0 && cornerRadiusTopRight == 0
                && cornerRadiusBottomRight == 0 && cornerRadiusBottomLeft == 0) {
            drawRectangleThin(width, height, x, y, deg, sclX, sclY);
            return;
        }

        if (!drawing) throw new GraphicsException("Must call begin() before draw operations.");

        segmentsTopLeft = Math.max(2, segmentsTopLeft);
        segmentsTopRight = Math.max(2, segmentsTopRight);
        segmentsBottomRight = Math.max(2, segmentsBottomRight);
        segmentsBottomLeft = Math.max(2, segmentsBottomLeft);

        int vertexCount = segmentsTopLeft + segmentsTopRight
                + segmentsBottomRight + segmentsBottomLeft;
        int indexCount = vertexCount * 2;

        if (requiresFlush(vertexCount, indexCount)) flush();

        setMode(GL11.GL_LINES);
        setTexture(defaultTexture);

        float widthHalf = width * 0.5f;
        float heightHalf = height * 0.5f;

        float daTL = 90.0f / (segmentsTopLeft - 1);
        float daTR = 90.0f / (segmentsTopRight - 1);
        float daBR = 90.0f / (segmentsBottomRight - 1);
        float daBL = 90.0f / (segmentsBottomLeft - 1);

        // upper left
        for (int i = 0; i < segmentsTopLeft; i++) {
            tmp_Vector.set(-cornerRadiusTopLeft, 0);
            tmp_Vector.rotateDeg(-daTL * i);
            tmp_Vector.add(-widthHalf + cornerRadiusTopLeft,
                    heightHalf - cornerRadiusTopLeft);
            tmp_Vector.scl(sclX, sclY).rotateDeg(deg).add(x, y);

            positions.put(tmp_Vector.x).put(tmp_Vector.y);
            colors.put(currentTint);
            textCoords.put(0.5f).put(0.5f);
        }

        // upper right
        for (int i = 0; i < segmentsTopRight; i++) {
            tmp_Vector.set(0, cornerRadiusTopRight);
            tmp_Vector.rotateDeg(-daTR * i);
            tmp_Vector.add(widthHalf - cornerRadiusTopRight,
                    heightHalf - cornerRadiusTopRight);
            tmp_Vector.scl(sclX, sclY).rotateDeg(deg).add(x, y);

            positions.put(tmp_Vector.x).put(tmp_Vector.y);
            colors.put(currentTint);
            textCoords.put(0.5f).put(0.5f);
        }

        // lower right
        for (int i = 0; i < segmentsBottomRight; i++) {
            tmp_Vector.set(cornerRadiusBottomRight, 0);
            tmp_Vector.rotateDeg(-daBR * i);
            tmp_Vector.add(widthHalf - cornerRadiusBottomRight,
                    -heightHalf + cornerRadiusBottomRight);
            tmp_Vector.scl(sclX, sclY).rotateDeg(deg).add(x, y);

            positions.put(tmp_Vector.x).put(tmp_Vector.y);
            colors.put(currentTint);
            textCoords.put(0.5f).put(0.5f);
        }

        // lower left
        for (int i = 0; i < segmentsBottomLeft; i++) {
            tmp_Vector.set(0, -cornerRadiusBottomLeft);
            tmp_Vector.rotateDeg(-daBL * i);
            tmp_Vector.add(-widthHalf + cornerRadiusBottomLeft,
                    -heightHalf + cornerRadiusBottomLeft);
            tmp_Vector.scl(sclX, sclY).rotateDeg(deg).add(x, y);

            positions.put(tmp_Vector.x).put(tmp_Vector.y);
            colors.put(currentTint);
            textCoords.put(0.5f).put(0.5f);
        }

        int startVertex = vertexIndex;

        for (int i = 0; i < vertexCount; i++) {
            indices.put(startVertex + i);
            indices.put(startVertex + (i + 1) % vertexCount);
        }

        vertexIndex += vertexCount;
    }

    public void drawRectangleFilled(float width, float height, float x, float y, float degrees, float scaleX, float scaleY) {
        drawRectangleFilled(null, width, height, x, y, degrees, scaleX, scaleY);
    }

    public void drawRectangleFilled(@Nullable Texture texture,
                                    float width, float height,
                                    float x, float y, float degrees,
                                    float scaleX, float scaleY) {
        if (!drawing) throw new GraphicsException("Must call begin() before draw operations.");
        if (requiresFlush(4, 6)) flush();

        float uSpan = 1f;
        float vSpan = 1f;
        if (texture != null) {
            uSpan = width / texture.width;
            vSpan = height / texture.height;
        }

        setMode(GL11.GL_TRIANGLES);
        setTexture(texture);

        float widthHalf = width * scaleX * 0.5f;
        float heightHalf = height * scaleY * 0.5f;

        float u0 = 0.5f - uSpan * 0.5f;
        float u1 = 0.5f + uSpan * 0.5f;
        float v0 = 0.5f - vSpan * 0.5f;
        float v1 = 0.5f + vSpan * 0.5f;

        // top-left
        tmp_Vector.x = -widthHalf;
        tmp_Vector.y = heightHalf;
        tmp_Vector.rotateDeg(degrees);
        positions.put(tmp_Vector.x + x).put(tmp_Vector.y + y);
        colors.put(currentTint);
        textCoords.put(u0).put(v0);

        // bottom-left
        tmp_Vector.x = -widthHalf;
        tmp_Vector.y = -heightHalf;
        tmp_Vector.rotateDeg(degrees);
        positions.put(tmp_Vector.x + x).put(tmp_Vector.y + y);
        colors.put(currentTint);
        textCoords.put(u0).put(v1);

        // bottom-right
        tmp_Vector.x = widthHalf;
        tmp_Vector.y = -heightHalf;
        tmp_Vector.rotateDeg(degrees);
        positions.put(tmp_Vector.x + x).put(tmp_Vector.y + y);
        colors.put(currentTint);
        textCoords.put(u1).put(v1);

        // top-right
        tmp_Vector.x = widthHalf;
        tmp_Vector.y = heightHalf;
        tmp_Vector.rotateDeg(degrees);
        positions.put(tmp_Vector.x + x).put(tmp_Vector.y + y);
        colors.put(currentTint);
        textCoords.put(u1).put(v0);

        int startVertex = vertexIndex;
        // triangle 1
        indices.put(startVertex);
        indices.put(startVertex + 1);
        indices.put(startVertex + 2);
        // triangle 2
        indices.put(startVertex + 2);
        indices.put(startVertex + 3);
        indices.put(startVertex);

        vertexIndex += 4;
    }

    public void drawRectangleFilled(float width, float height, float cornerRadius, int refinement, float x, float y, float degrees, float scaleX, float scaleY) {
        drawRectangleFilled(null, width, height, cornerRadius, refinement, x, y, degrees, scaleX, scaleY);
    }

    public void drawRectangleFilled(@Nullable Texture texture,
                                    float width, float height,
                                    float cornerRadius, int refinement,
                                    float x, float y, float degrees,
                                    float scaleX, float scaleY) {
        if (!drawing) throw new GraphicsException("Must call begin() before draw operations.");

        if (cornerRadius == 0) {
            drawRectangleFilled(texture, width, height, x, y, degrees, scaleX, scaleY);
            return;
        }
        refinement = Math.max(2, refinement);
        int vertexCount = refinement * 4;
        int indexCount = (vertexCount - 2) * 3;
        if (requiresFlush(vertexCount, indexCount)) flush();

        setMode(GL11.GL_TRIANGLES);
        setTexture(texture);

        float widthHalf = width * 0.5f;
        float heightHalf = height * 0.5f;
        float da = 90.0f / (refinement - 1);

        // upper left
        for (int i = 0; i < refinement; i++) {
            tmp_Vector.set(-cornerRadius, 0);
            tmp_Vector.rotateDeg(-da * i);
            tmp_Vector.add(-widthHalf + cornerRadius, heightHalf - cornerRadius);
            float u = 0.5f + tmp_Vector.x * currentTexture.invWidth * pixelScaleWidth;
            float v = 0.5f - tmp_Vector.y * currentTexture.invHeight * pixelScaleHeight;
            textCoords.put(u).put(v);

            tmp_Vector.scl(scaleX, scaleY).rotateDeg(degrees).add(x, y);
            positions.put(tmp_Vector.x).put(tmp_Vector.y);
            colors.put(currentTint);
        }

        // upper right
        for (int i = 0; i < refinement; i++) {
            tmp_Vector.set(0, cornerRadius);
            tmp_Vector.rotateDeg(-da * i);
            tmp_Vector.add(widthHalf - cornerRadius, heightHalf - cornerRadius);
            float u = 0.5f + tmp_Vector.x * currentTexture.invWidth * pixelScaleWidth;
            float v = 0.5f - tmp_Vector.y * currentTexture.invHeight * pixelScaleHeight;
            textCoords.put(u).put(v);

            tmp_Vector.scl(scaleX, scaleY).rotateDeg(degrees).add(x, y);
            positions.put(tmp_Vector.x).put(tmp_Vector.y);
            colors.put(currentTint);
        }

        // lower right
        for (int i = 0; i < refinement; i++) {
            tmp_Vector.set(cornerRadius, 0);
            tmp_Vector.rotateDeg(-da * i);
            tmp_Vector.add(widthHalf - cornerRadius, -heightHalf + cornerRadius);
            float u = 0.5f + tmp_Vector.x * currentTexture.invWidth * pixelScaleWidth;
            float v = 0.5f - tmp_Vector.y * currentTexture.invHeight * pixelScaleHeight;
            textCoords.put(u).put(v);

            tmp_Vector.scl(scaleX, scaleY).rotateDeg(degrees).add(x, y);
            positions.put(tmp_Vector.x).put(tmp_Vector.y);
            colors.put(currentTint);
        }

        // lower left
        for (int i = 0; i < refinement; i++) {
            tmp_Vector.set(0, -cornerRadius);
            tmp_Vector.rotateDeg(-da * i);
            tmp_Vector.add(-widthHalf + cornerRadius, -heightHalf + cornerRadius);
            float u = 0.5f + tmp_Vector.x * currentTexture.invWidth * pixelScaleWidth;
            float v = 0.5f - tmp_Vector.y * currentTexture.invHeight * pixelScaleHeight;
            textCoords.put(u).put(v);

            tmp_Vector.scl(scaleX, scaleY).rotateDeg(degrees).add(x, y);
            positions.put(tmp_Vector.x).put(tmp_Vector.y);
            colors.put(currentTint);
        }

        int startVertex = vertexIndex;

        for (int i = 0; i < vertexCount - 2; i++) {
            indices.put(startVertex);
            indices.put(startVertex + i + 1);
            indices.put(startVertex + i + 2);
        }

        vertexIndex += vertexCount;
    }

    public void drawRectangleFilled(float width, float height,
                                    float cornerRadiusTopLeft, int refinementTopLeft,
                                    float cornerRadiusTopRight, int refinementTopRight,
                                    float cornerRadiusBottomRight, int refinementBottomRight,
                                    float cornerRadiusBottomLeft, int refinementBottomLeft,
                                    float x, float y, float degrees, float scaleX, float scaleY) {
        drawRectangleFilled(null, width, height,
                cornerRadiusTopLeft, refinementTopLeft,
                cornerRadiusTopRight, refinementTopRight,
                cornerRadiusBottomRight, refinementBottomRight,
                cornerRadiusBottomLeft, refinementBottomLeft,
                x, y, degrees, scaleX, scaleY);
    }

    public void drawRectangleFilled(@Nullable Texture texture, float width, float height,
                                    float cornerRadiusTopLeft, int refinementTopLeft,
                                    float cornerRadiusTopRight, int refinementTopRight,
                                    float cornerRadiusBottomRight, int refinementBottomRight,
                                    float cornerRadiusBottomLeft, int refinementBottomLeft,
                                    float x, float y, float degrees, float scaleX, float scaleY) {
        if (!drawing) throw new GraphicsException("Must call begin() before draw operations.");

        if (cornerRadiusTopLeft == 0 && cornerRadiusTopRight == 0
                && cornerRadiusBottomRight == 0 && cornerRadiusBottomLeft == 0) {
            drawRectangleFilled(texture, width, height, x, y, degrees, scaleX, scaleY);
            return;
        }

        refinementTopLeft = Math.max(2, refinementTopLeft);
        refinementTopRight = Math.max(2, refinementTopRight);
        refinementBottomRight = Math.max(2, refinementBottomRight);
        refinementBottomLeft = Math.max(2, refinementBottomLeft);

        int vertexCount = (MathUtils.isZero(cornerRadiusTopLeft) ? 1 : refinementTopLeft)
                + (MathUtils.isZero(cornerRadiusTopRight) ? 1 : refinementTopRight)
                + (MathUtils.isZero(cornerRadiusBottomRight) ? 1 : refinementBottomRight)
                + (MathUtils.isZero(cornerRadiusBottomLeft) ? 1 : refinementBottomLeft);

        int indexCount = (vertexCount - 2) * 3;
        if (requiresFlush(vertexCount, indexCount)) flush();

        setMode(GL11.GL_TRIANGLES);
        setTexture(texture);

        float widthHalf = width * 0.5f;
        float heightHalf = height * 0.5f;

        float daTL = 90.0f / (refinementTopLeft - 1);
        float daTR = 90.0f / (refinementTopRight - 1);
        float daBR = 90.0f / (refinementBottomRight - 1);
        float daBL = 90.0f / (refinementBottomLeft - 1);

        // upper left
        if (MathUtils.isZero(cornerRadiusTopLeft)) {
            tmp_Vector.set(-widthHalf, heightHalf);
            float u = 0.5f + tmp_Vector.x * currentTexture.invWidth * pixelScaleWidth;
            float v = 0.5f - tmp_Vector.y * currentTexture.invHeight * pixelScaleHeight;
            textCoords.put(u).put(v);

            tmp_Vector.scl(scaleX, scaleY).rotateDeg(degrees).add(x, y);
            positions.put(tmp_Vector.x).put(tmp_Vector.y);
            colors.put(currentTint);
        } else {
            for (int i = 0; i < refinementTopLeft; i++) {
                tmp_Vector.set(-cornerRadiusTopLeft, 0);
                tmp_Vector.rotateDeg(-daTL * i);
                tmp_Vector.add(-widthHalf + cornerRadiusTopLeft, heightHalf - cornerRadiusTopLeft);
                float u = 0.5f + tmp_Vector.x * currentTexture.invWidth * pixelScaleWidth;
                float v = 0.5f - tmp_Vector.y * currentTexture.invHeight * pixelScaleHeight;
                textCoords.put(u).put(v);

                tmp_Vector.scl(scaleX, scaleY).rotateDeg(degrees).add(x, y);
                positions.put(tmp_Vector.x).put(tmp_Vector.y);
                colors.put(currentTint);
            }
        }

        // upper right
        if (MathUtils.isZero(cornerRadiusTopRight)) {
            tmp_Vector.set(widthHalf, heightHalf);
            float u = 0.5f + tmp_Vector.x * currentTexture.invWidth * pixelScaleWidth;
            float v = 0.5f - tmp_Vector.y * currentTexture.invHeight * pixelScaleHeight;
            textCoords.put(u).put(v);

            tmp_Vector.scl(scaleX, scaleY).rotateDeg(degrees).add(x, y);
            positions.put(tmp_Vector.x).put(tmp_Vector.y);
            colors.put(currentTint);
        } else {
            for (int i = 0; i < refinementTopRight; i++) {
                tmp_Vector.set(0, cornerRadiusTopRight);
                tmp_Vector.rotateDeg(-daTR * i);
                tmp_Vector.add(widthHalf - cornerRadiusTopRight, heightHalf - cornerRadiusTopRight);

                float u = 0.5f + tmp_Vector.x * currentTexture.invWidth * pixelScaleWidth;
                float v = 0.5f - tmp_Vector.y * currentTexture.invHeight * pixelScaleHeight;
                textCoords.put(u).put(v);
                tmp_Vector.scl(scaleX, scaleY).rotateDeg(degrees).add(x, y);
                positions.put(tmp_Vector.x).put(tmp_Vector.y);
                colors.put(currentTint);
            }
        }

        // lower right
        if (MathUtils.isZero(cornerRadiusBottomRight)) {
            tmp_Vector.set(widthHalf, -heightHalf);
            float u = 0.5f + tmp_Vector.x * currentTexture.invWidth * pixelScaleWidth;
            float v = 0.5f - tmp_Vector.y * currentTexture.invHeight * pixelScaleHeight;
            textCoords.put(u).put(v);

            tmp_Vector.scl(scaleX, scaleY).rotateDeg(degrees).add(x, y);
            positions.put(tmp_Vector.x).put(tmp_Vector.y);
            colors.put(currentTint);
        } else {
            for (int i = 0; i < refinementBottomRight; i++) {
                tmp_Vector.set(cornerRadiusBottomRight, 0);
                tmp_Vector.rotateDeg(-daBR * i);
                tmp_Vector.add(widthHalf - cornerRadiusBottomRight, -heightHalf + cornerRadiusBottomRight);

                float u = 0.5f + tmp_Vector.x * currentTexture.invWidth * pixelScaleWidth;
                float v = 0.5f - tmp_Vector.y * currentTexture.invHeight * pixelScaleHeight;
                textCoords.put(u).put(v);
                tmp_Vector.scl(scaleX, scaleY).rotateDeg(degrees).add(x, y);
                positions.put(tmp_Vector.x).put(tmp_Vector.y);
                colors.put(currentTint);
            }
        }

        // lower left
        if (MathUtils.isZero(cornerRadiusBottomLeft)) {
            tmp_Vector.set(-widthHalf, -heightHalf);
            float u = 0.5f + tmp_Vector.x * currentTexture.invWidth * pixelScaleWidth;
            float v = 0.5f - tmp_Vector.y * currentTexture.invHeight * pixelScaleHeight;
            textCoords.put(u).put(v);

            tmp_Vector.scl(scaleX, scaleY).rotateDeg(degrees).add(x, y);
            positions.put(tmp_Vector.x).put(tmp_Vector.y);
            colors.put(currentTint);
        } else {
            for (int i = 0; i < refinementBottomLeft; i++) {
                tmp_Vector.set(0, -cornerRadiusBottomLeft);
                tmp_Vector.rotateDeg(-daBL * i);
                tmp_Vector.add(-widthHalf + cornerRadiusBottomLeft, -heightHalf + cornerRadiusBottomLeft);
                float u = 0.5f + tmp_Vector.x * currentTexture.invWidth * pixelScaleWidth;
                float v = 0.5f - tmp_Vector.y * currentTexture.invHeight * pixelScaleHeight;
                textCoords.put(u).put(v);

                tmp_Vector.scl(scaleX, scaleY).rotateDeg(degrees).add(x, y);
                positions.put(tmp_Vector.x).put(tmp_Vector.y);
                colors.put(currentTint);
            }
        }

        int startVertex = vertexIndex;
        for (int i = 0; i < vertexCount - 2; i++) {
            indices.put(startVertex);
            indices.put(startVertex + i + 2);
            indices.put(startVertex + i + 1);
        }

        vertexIndex += vertexCount;
    }

    public void drawRectangleBorder(float width, float height, float thickness,
                                    float x, float y, float deg,
                                    float scaleX, float scaleY) {
        if (!drawing) throw new GraphicsException("Must call begin() before draw operations.");
        if (requiresFlush(8, 24)) flush();

        setMode(GL11.GL_TRIANGLES);
        setTexture(defaultTexture);

        float widthHalf = width * 0.5f;
        float heightHalf = height * 0.5f;
        float thicknessHalf = thickness * 0.5f;

        // inner top-left
        tmp_Vector.set(-widthHalf + thicknessHalf, heightHalf - thicknessHalf);
        float u = 0.5f + tmp_Vector.x * currentTexture.invWidth * pixelScaleWidth;
        float v = 0.5f - tmp_Vector.y * currentTexture.invHeight * pixelScaleHeight;
        textCoords.put(u).put(v);
        tmp_Vector.scl(scaleX, scaleY).rotateDeg(deg).add(x, y);
        positions.put(tmp_Vector.x).put(tmp_Vector.y);
        colors.put(currentTint);

        // inner bottom-left
        tmp_Vector.set(-widthHalf + thicknessHalf, -heightHalf + thicknessHalf);
        u = 0.5f + tmp_Vector.x * currentTexture.invWidth * pixelScaleWidth;
        v = 0.5f - tmp_Vector.y * currentTexture.invHeight * pixelScaleHeight;
        textCoords.put(u).put(v);
        tmp_Vector.scl(scaleX, scaleY).rotateDeg(deg).add(x, y);
        positions.put(tmp_Vector.x).put(tmp_Vector.y);
        colors.put(currentTint);

        // inner bottom-right
        tmp_Vector.set(widthHalf - thicknessHalf, -heightHalf + thicknessHalf);
        u = 0.5f + tmp_Vector.x * currentTexture.invWidth * pixelScaleWidth;
        v = 0.5f - tmp_Vector.y * currentTexture.invHeight * pixelScaleHeight;
        textCoords.put(u).put(v);
        tmp_Vector.scl(scaleX, scaleY).rotateDeg(deg).add(x, y);
        positions.put(tmp_Vector.x).put(tmp_Vector.y);
        colors.put(currentTint);

        // inner top-right
        tmp_Vector.set(widthHalf - thicknessHalf, heightHalf - thicknessHalf);
        u = 0.5f + tmp_Vector.x * currentTexture.invWidth * pixelScaleWidth;
        v = 0.5f - tmp_Vector.y * currentTexture.invHeight * pixelScaleHeight;
        textCoords.put(u).put(v);
        tmp_Vector.scl(scaleX, scaleY).rotateDeg(deg).add(x, y);
        positions.put(tmp_Vector.x).put(tmp_Vector.y);
        colors.put(currentTint);

        // outer top-left
        tmp_Vector.set(-widthHalf - thicknessHalf, heightHalf + thicknessHalf);
        u = 0.5f + tmp_Vector.x * currentTexture.invWidth * pixelScaleWidth;
        v = 0.5f - tmp_Vector.y * currentTexture.invHeight * pixelScaleHeight;
        textCoords.put(u).put(v);
        tmp_Vector.scl(scaleX, scaleY).rotateDeg(deg).add(x, y);
        positions.put(tmp_Vector.x).put(tmp_Vector.y);
        colors.put(currentTint);

        // outer bottom-left
        tmp_Vector.set(-widthHalf - thicknessHalf, -heightHalf - thicknessHalf);
        u = 0.5f + tmp_Vector.x * currentTexture.invWidth * pixelScaleWidth;
        v = 0.5f - tmp_Vector.y * currentTexture.invHeight * pixelScaleHeight;
        textCoords.put(u).put(v);
        tmp_Vector.scl(scaleX, scaleY).rotateDeg(deg).add(x, y);
        positions.put(tmp_Vector.x).put(tmp_Vector.y);
        colors.put(currentTint);

        // outer bottom-right
        tmp_Vector.set(widthHalf + thicknessHalf, -heightHalf - thicknessHalf);
        u = 0.5f + tmp_Vector.x * currentTexture.invWidth * pixelScaleWidth;
        v = 0.5f - tmp_Vector.y * currentTexture.invHeight * pixelScaleHeight;
        textCoords.put(u).put(v);
        tmp_Vector.scl(scaleX, scaleY).rotateDeg(deg).add(x, y);
        positions.put(tmp_Vector.x).put(tmp_Vector.y);
        colors.put(currentTint);

        // outer top-right
        tmp_Vector.set(widthHalf + thicknessHalf, heightHalf + thicknessHalf);
        u = 0.5f + tmp_Vector.x * currentTexture.invWidth * pixelScaleWidth;
        v = 0.5f - tmp_Vector.y * currentTexture.invHeight * pixelScaleHeight;
        textCoords.put(u).put(v);
        tmp_Vector.scl(scaleX, scaleY).rotateDeg(deg).add(x, y);
        positions.put(tmp_Vector.x).put(tmp_Vector.y);
        colors.put(currentTint);

        int startVertex = vertexIndex;

        indices.put(startVertex);
        indices.put(startVertex + 4);
        indices.put(startVertex + 5);

        indices.put(startVertex);
        indices.put(startVertex + 5);
        indices.put(startVertex + 1);

        indices.put(startVertex + 1);
        indices.put(startVertex + 5);
        indices.put(startVertex + 6);

        indices.put(startVertex + 1);
        indices.put(startVertex + 6);
        indices.put(startVertex + 2);

        indices.put(startVertex + 2);
        indices.put(startVertex + 6);
        indices.put(startVertex + 7);

        indices.put(startVertex + 2);
        indices.put(startVertex + 7);
        indices.put(startVertex + 3);

        indices.put(startVertex + 3);
        indices.put(startVertex + 7);
        indices.put(startVertex + 4);

        indices.put(startVertex + 3);
        indices.put(startVertex + 4);
        indices.put(startVertex);

        vertexIndex += 8;
    }

    public void drawRectangleBorder(float width, float height, float thickness,
                                    float cornerRadiusTopLeft, int segmentsTopLeft,
                                    float cornerRadiusTopRight, int segmentsTopRight,
                                    float cornerRadiusBottomRight, int segmentsBottomRight,
                                    float cornerRadiusBottomLeft, int segmentsBottomLeft,
                                    float x, float y, float deg, float sclX, float sclY) {
        if (!drawing) throw new GraphicsException("Must call begin() before draw operations.");

        segmentsTopLeft = Math.max(2, segmentsTopLeft);
        segmentsTopRight = Math.max(2, segmentsTopRight);
        segmentsBottomRight = Math.max(2, segmentsBottomRight);
        segmentsBottomLeft = Math.max(2, segmentsBottomLeft);

        int innerVertexCount =
                (MathUtils.isZero(cornerRadiusTopLeft) ? 1 : segmentsTopLeft)
                        + (MathUtils.isZero(cornerRadiusTopRight) ? 1 : segmentsTopRight)
                        + (MathUtils.isZero(cornerRadiusBottomRight) ? 1 : segmentsBottomRight)
                        + (MathUtils.isZero(cornerRadiusBottomLeft) ? 1 : segmentsBottomLeft);

        int sharpCornerCount = 0;
        if (MathUtils.isZero(cornerRadiusTopLeft)) sharpCornerCount++;
        if (MathUtils.isZero(cornerRadiusTopRight)) sharpCornerCount++;
        if (MathUtils.isZero(cornerRadiusBottomRight)) sharpCornerCount++;
        if (MathUtils.isZero(cornerRadiusBottomLeft)) sharpCornerCount++;

        int vertexCount = (innerVertexCount + sharpCornerCount) * 3;
        int indexCount = innerVertexCount * 9 + sharpCornerCount * 3;
        if (requiresFlush(vertexCount, indexCount)) flush();

        setMode(GL11.GL_TRIANGLES);
        setTexture(defaultTexture);

        float widthHalfInner = width * 0.5f;
        float heightHalfInner = height * 0.5f;

        thickness = Math.abs(thickness);

        float daTL = 90.0f / (segmentsTopLeft - 1);
        float daTR = 90.0f / (segmentsTopRight - 1);
        float daBR = 90.0f / (segmentsBottomRight - 1);
        float daBL = 90.0f / (segmentsBottomLeft - 1);

        Array<Vector2> inners = new Array<>(true, innerVertexCount);

        // add upper left corner vertices
        if (MathUtils.isZero(cornerRadiusTopLeft)) {
            Vector2 cornerInner = vectors2Pool.allocate();
            cornerInner.set(-widthHalfInner, heightHalfInner);
            inners.add(cornerInner);
        } else {
            for (int i = 0; i < segmentsTopLeft; i++) {
                Vector2 cornerInner = vectors2Pool.allocate();
                cornerInner.set(-cornerRadiusTopLeft, 0);
                cornerInner.rotateDeg(-daTL * i);
                cornerInner.add(
                        -widthHalfInner + cornerRadiusTopLeft,
                        heightHalfInner - cornerRadiusTopLeft
                );
                inners.add(cornerInner);
            }
        }

        // add upper right corner vertices
        if (MathUtils.isZero(cornerRadiusTopRight)) {
            Vector2 cornerInner = vectors2Pool.allocate();
            cornerInner.set(widthHalfInner, heightHalfInner);
            inners.add(cornerInner);
        } else {
            for (int i = 0; i < segmentsTopRight; i++) {
                Vector2 cornerInner = vectors2Pool.allocate();
                cornerInner.set(0, cornerRadiusTopRight);
                cornerInner.rotateDeg(-daTR * i);
                cornerInner.add(
                        widthHalfInner - cornerRadiusTopRight,
                        heightHalfInner - cornerRadiusTopRight
                );
                inners.add(cornerInner);
            }
        }

        // add lower right corner vertices
        if (MathUtils.isZero(cornerRadiusBottomRight)) {
            Vector2 cornerInner = vectors2Pool.allocate();
            cornerInner.set(widthHalfInner, -heightHalfInner);
            inners.add(cornerInner);
        } else {
            for (int i = 0; i < segmentsBottomRight; i++) {
                Vector2 cornerInner = vectors2Pool.allocate();
                cornerInner.set(cornerRadiusBottomRight, 0);
                cornerInner.rotateDeg(-daBR * i);
                cornerInner.add(
                        widthHalfInner - cornerRadiusBottomRight,
                        -heightHalfInner + cornerRadiusBottomRight
                );
                inners.add(cornerInner);
            }
        }

        // add lower left corner vertices
        if (MathUtils.isZero(cornerRadiusBottomLeft)) {
            Vector2 cornerInner = vectors2Pool.allocate();
            cornerInner.set(-widthHalfInner, -heightHalfInner);
            inners.add(cornerInner);
        } else {
            for (int i = 0; i < segmentsBottomLeft; i++) {
                Vector2 cornerInner = vectors2Pool.allocate();
                cornerInner.set(0, -cornerRadiusBottomLeft);
                cornerInner.rotateDeg(-daBL * i);
                cornerInner.add(
                        -widthHalfInner + cornerRadiusBottomLeft,
                        -heightHalfInner + cornerRadiusBottomLeft
                );
                inners.add(cornerInner);
            }
        }

        Array<Vector2> outers = new Array<>(true, inners.size * 2);

        for (int i = 0; i < inners.size; i++) {
            Vector2 innerPrev = inners.getCyclic(i - 1);
            Vector2 inner = inners.get(i);
            Vector2 innerNext = inners.getCyclic(i + 1);

            Vector2 prev = vectors2Pool.allocate();
            prev.x = innerPrev.x - inner.x;
            prev.y = innerPrev.y - inner.y;
            prev.rotate90(-1);
            prev.nor().scl(thickness).add(inner);

            Vector2 next = vectors2Pool.allocate();
            next.x = innerNext.x - inner.x;
            next.y = innerNext.y - inner.y;
            next.rotate90(1);
            next.nor().scl(thickness).add(inner);

            outers.add(prev);
            outers.add(next);
        }

        // transform vertices and put them in the buffer
        for (int i = 0; i < inners.size; i++) {
            Vector2 inner = inners.get(i);
            inner.scl(sclX, sclY).rotateDeg(deg).add(x, y);

            positions.put(inner.x).put(inner.y);
            colors.put(currentTint);
            textCoords.put(0.5f).put(0.5f);

            Vector2 outerPrev = outers.get(2 * i);
            outerPrev.scl(sclX, sclY).rotateDeg(deg).add(x, y);

            positions.put(outerPrev.x).put(outerPrev.y);
            colors.put(currentTint);
            textCoords.put(0.5f).put(0.5f);

            Vector2 outerNext = outers.get(2 * i + 1);
            outerNext.scl(sclX, sclY).rotateDeg(deg).add(x, y);

            positions.put(outerNext.x).put(outerNext.y);
            colors.put(currentTint);
            textCoords.put(0.5f).put(0.5f);
        }

        // put indices
        int startVertex = vertexIndex;
        int totalVertices = inners.size + outers.size;

        for (int i = 0; i < totalVertices; i += 3) {
            indices.put(startVertex + (i + 0) % totalVertices);
            indices.put(startVertex + (i + 2) % totalVertices);
            indices.put(startVertex + (i + 1) % totalVertices);

            indices.put(startVertex + (i + 0) % totalVertices);
            indices.put(startVertex + (i + 3) % totalVertices);
            indices.put(startVertex + (i + 2) % totalVertices);

            indices.put(startVertex + (i + 2) % totalVertices);
            indices.put(startVertex + (i + 3) % totalVertices);
            indices.put(startVertex + (i + 4) % totalVertices);
        }

        vertexIndex += totalVertices;

        // adjustment: for every sharp corner, fill the triangle to create a square corner

        // top left
        startVertex = vertexIndex;
        if (MathUtils.isZero(cornerRadiusTopLeft)) {
            Vector2 cornerTopLeft1 = vectors2Pool.allocate();
            cornerTopLeft1.add(-widthHalfInner - thickness, heightHalfInner);
            cornerTopLeft1.scl(sclX, sclY).rotateDeg(deg).add(x, y);

            positions.put(cornerTopLeft1.x).put(cornerTopLeft1.y);
            colors.put(currentTint);
            textCoords.put(0.5f).put(0.5f);

            Vector2 cornerTopLeft2 = vectors2Pool.allocate();
            cornerTopLeft2.add(-widthHalfInner, heightHalfInner + thickness);
            cornerTopLeft2.scl(sclX, sclY).rotateDeg(deg).add(x, y);

            positions.put(cornerTopLeft2.x).put(cornerTopLeft2.y);
            colors.put(currentTint);
            textCoords.put(0.5f).put(0.5f);

            Vector2 cornerTopLeft3 = vectors2Pool.allocate();
            cornerTopLeft3.add(-widthHalfInner - thickness, heightHalfInner + thickness);
            cornerTopLeft3.scl(sclX, sclY).rotateDeg(deg).add(x, y);

            positions.put(cornerTopLeft3.x).put(cornerTopLeft3.y);
            colors.put(currentTint);
            textCoords.put(0.5f).put(0.5f);

            indices.put(startVertex);
            indices.put(startVertex + 1);
            indices.put(startVertex + 2);

            vertexIndex += 3;

            vectors2Pool.free(cornerTopLeft1);
            vectors2Pool.free(cornerTopLeft2);
            vectors2Pool.free(cornerTopLeft3);
        }

        // top right
        startVertex = vertexIndex;
        if (MathUtils.isZero(cornerRadiusTopRight)) {
            Vector2 cornerTopRight1 = vectors2Pool.allocate();
            cornerTopRight1.add(widthHalfInner + thickness, heightHalfInner);
            cornerTopRight1.scl(sclX, sclY).rotateDeg(deg).add(x, y);

            positions.put(cornerTopRight1.x).put(cornerTopRight1.y);
            colors.put(currentTint);
            textCoords.put(0.5f).put(0.5f);

            Vector2 cornerTopRight2 = vectors2Pool.allocate();
            cornerTopRight2.add(widthHalfInner, heightHalfInner + thickness);
            cornerTopRight2.scl(sclX, sclY).rotateDeg(deg).add(x, y);

            positions.put(cornerTopRight2.x).put(cornerTopRight2.y);
            colors.put(currentTint);
            textCoords.put(0.5f).put(0.5f);

            Vector2 cornerTopRight3 = vectors2Pool.allocate();
            cornerTopRight3.add(widthHalfInner + thickness, heightHalfInner + thickness);
            cornerTopRight3.scl(sclX, sclY).rotateDeg(deg).add(x, y);

            positions.put(cornerTopRight3.x).put(cornerTopRight3.y);
            colors.put(currentTint);
            textCoords.put(0.5f).put(0.5f);

            indices.put(startVertex);
            indices.put(startVertex + 1);
            indices.put(startVertex + 2);

            vertexIndex += 3;

            vectors2Pool.free(cornerTopRight1);
            vectors2Pool.free(cornerTopRight2);
            vectors2Pool.free(cornerTopRight3);
        }

        // bottom right
        startVertex = vertexIndex;
        if (MathUtils.isZero(cornerRadiusBottomRight)) {
            Vector2 cornerBottomRight1 = vectors2Pool.allocate();
            cornerBottomRight1.add(widthHalfInner + thickness, -heightHalfInner);
            cornerBottomRight1.scl(sclX, sclY).rotateDeg(deg).add(x, y);

            positions.put(cornerBottomRight1.x).put(cornerBottomRight1.y);
            colors.put(currentTint);
            textCoords.put(0.5f).put(0.5f);

            Vector2 cornerBottomRight2 = vectors2Pool.allocate();
            cornerBottomRight2.add(widthHalfInner, -heightHalfInner - thickness);
            cornerBottomRight2.scl(sclX, sclY).rotateDeg(deg).add(x, y);

            positions.put(cornerBottomRight2.x).put(cornerBottomRight2.y);
            colors.put(currentTint);
            textCoords.put(0.5f).put(0.5f);

            Vector2 cornerBottomRight3 = vectors2Pool.allocate();
            cornerBottomRight3.add(widthHalfInner + thickness, -heightHalfInner - thickness);
            cornerBottomRight3.scl(sclX, sclY).rotateDeg(deg).add(x, y);

            positions.put(cornerBottomRight3.x).put(cornerBottomRight3.y);
            colors.put(currentTint);
            textCoords.put(0.5f).put(0.5f);

            indices.put(startVertex);
            indices.put(startVertex + 1);
            indices.put(startVertex + 2);

            vertexIndex += 3;

            vectors2Pool.free(cornerBottomRight1);
            vectors2Pool.free(cornerBottomRight2);
            vectors2Pool.free(cornerBottomRight3);
        }

        // bottom left
        startVertex = vertexIndex;
        if (MathUtils.isZero(cornerRadiusBottomLeft)) {
            Vector2 cornerBottomLeft1 = vectors2Pool.allocate();
            cornerBottomLeft1.add(-widthHalfInner - thickness, -heightHalfInner);
            cornerBottomLeft1.scl(sclX, sclY).rotateDeg(deg).add(x, y);

            positions.put(cornerBottomLeft1.x).put(cornerBottomLeft1.y);
            colors.put(currentTint);
            textCoords.put(0.5f).put(0.5f);

            Vector2 cornerBottomLeft2 = vectors2Pool.allocate();
            cornerBottomLeft2.add(-widthHalfInner, -heightHalfInner - thickness);
            cornerBottomLeft2.scl(sclX, sclY).rotateDeg(deg).add(x, y);

            positions.put(cornerBottomLeft2.x).put(cornerBottomLeft2.y);
            colors.put(currentTint);
            textCoords.put(0.5f).put(0.5f);

            Vector2 cornerBottomLeft3 = vectors2Pool.allocate();
            cornerBottomLeft3.add(-widthHalfInner - thickness, -heightHalfInner - thickness);
            cornerBottomLeft3.scl(sclX, sclY).rotateDeg(deg).add(x, y);

            positions.put(cornerBottomLeft3.x).put(cornerBottomLeft3.y);
            colors.put(currentTint);
            textCoords.put(0.5f).put(0.5f);

            indices.put(startVertex);
            indices.put(startVertex + 1);
            indices.put(startVertex + 2);

            vertexIndex += 3;

            vectors2Pool.free(cornerBottomLeft1);
            vectors2Pool.free(cornerBottomLeft2);
            vectors2Pool.free(cornerBottomLeft3);
        }

        vectors2Pool.freeAll(inners);
        vectors2Pool.freeAll(outers);
    }

    /* Rendering 2D primitives - Polygons */ // TODO: do something about this code duplication.

    public void drawPolygonThin(ArrayFloat polygon, boolean triangulated,
                                float x, float y, float degrees,
                                float scaleX, float scaleY) {
        if (!drawing) throw new GraphicsException("Must call begin() before draw operations.");
        if (polygon.size < 6) throw new GraphicsException("A polygon requires a minimum of 3 vertices, so the polygon array must be of length > 6. Got: " + polygon.size);
        if (polygon.size % 2 != 0) throw new GraphicsException("Polygon must be represented as a flat array of vertices, each vertex must have x and y coordinates: " + "[x0,y0, x1,y1, ...]. Therefore, polygon array length must be even.");

        int count = polygon.size / 2;
        if (requiresFlush(count, count * 6)) flush();

        setMode(GL11.GL_LINES);
        setTexture(defaultTexture);

        int startVertex = vertexIndex;
        if (!triangulated) {
            for (int i = 0; i < polygon.size; i += 2) {
                tmp_Vector.set(polygon.get(i), polygon.get(i + 1));
                tmp_Vector.scl(scaleX, scaleY);
                tmp_Vector.rotateDeg(degrees);
                tmp_Vector.add(x, y);

                positions.put(tmp_Vector.x).put(tmp_Vector.y);
                colors.put(currentTint);
                textCoords.put(0.5f).put(0.5f);
            }
            for (int i = 0; i < count - 1; i++) {
                indices.put(startVertex + i);
                indices.put(startVertex + i + 1);
            }

            indices.put(startVertex + count - 1);
            indices.put(startVertex);
            vertexIndex += count;
            return;
        }

        tmp_ArrayFloat.clear();
        tmp_ArrayFloat.addAll(polygon);
        tmp_ArrayInt.clear();
        try {
            MathUtils.polygonTriangulate(tmp_ArrayFloat, tmp_ArrayInt);
        } catch (Exception e) { // Probably the polygon has collapsed into a single point.
            return;
        }

        for (int i = 0; i < tmp_ArrayFloat.size; i += 2) {
            tmp_Vector.set(tmp_ArrayFloat.get(i), tmp_ArrayFloat.get(i + 1));
            tmp_Vector.scl(scaleX, scaleY);
            tmp_Vector.rotateDeg(degrees);
            tmp_Vector.add(x, y);

            positions.put(tmp_Vector.x).put(tmp_Vector.y);
            colors.put(currentTint);
            textCoords.put(0.5f).put(0.5f);
        }

        for (int i = 0; i < tmp_ArrayInt.size; i += 3) {
            int v0 = startVertex + tmp_ArrayInt.get(i);
            int v1 = startVertex + tmp_ArrayInt.get(i + 1);
            int v2 = startVertex + tmp_ArrayInt.get(i + 2);

            // triangle 0 -> 1
            indices.put(v0);
            indices.put(v1);

            // triangle 1 -> 2
            indices.put(v1);
            indices.put(v2);

            // triangle 2 -> 0
            indices.put(v2);
            indices.put(v0);
        }

        vertexIndex += tmp_ArrayFloat.size / 2;
    }

    public void drawPolygonThin(float[] polygon, boolean triangulated,
                                float x, float y, float degrees,
                                float scaleX, float scaleY) {
        if (!drawing) throw new GraphicsException("Must call begin() before draw operations.");
        if (polygon.length < 6) throw new GraphicsException("A polygon requires a minimum of 3 vertices, so the polygon array must be of length > 6. Got: " + polygon.length);
        if (polygon.length % 2 != 0) throw new GraphicsException("Polygon must be represented as a flat array of vertices, each vertex must have x and y coordinates: " + "[x0,y0, x1,y1, ...]. Therefore, polygon array length must be even.");
        int count = polygon.length / 2;
        if (requiresFlush(count, count * 6)) flush();

        setMode(GL11.GL_LINES);
        setTexture(defaultTexture);

        int startVertex = vertexIndex;
        if (!triangulated) {
            for (int i = 0; i < polygon.length; i += 2) {
                tmp_Vector.set(polygon[i], polygon[i + 1]);
                tmp_Vector.scl(scaleX, scaleY);
                tmp_Vector.rotateDeg(degrees);
                tmp_Vector.add(x, y);

                positions.put(tmp_Vector.x).put(tmp_Vector.y);
                colors.put(currentTint);
                textCoords.put(0.5f).put(0.5f);
            }

            for (int i = 0; i < count - 1; i++) {
                indices.put(startVertex + i);
                indices.put(startVertex + i + 1);
            }

            indices.put(startVertex + count - 1);
            indices.put(startVertex);

            vertexIndex += count;
            return;
        }

        tmp_ArrayFloat.clear();
        tmp_ArrayInt.clear();
        try {
            MathUtils.polygonTriangulate(polygon, tmp_ArrayFloat, tmp_ArrayInt);
        } catch (Exception e) {
            return;
        }

        for (int i = 0; i < tmp_ArrayFloat.size; i += 2) {
            tmp_Vector.set(tmp_ArrayFloat.get(i), tmp_ArrayFloat.get(i + 1));
            tmp_Vector.scl(scaleX, scaleY);
            tmp_Vector.rotateDeg(degrees);
            tmp_Vector.add(x, y);

            positions.put(tmp_Vector.x).put(tmp_Vector.y);
            colors.put(currentTint);
            textCoords.put(0.5f).put(0.5f);
        }

        for (int i = 0; i < tmp_ArrayInt.size; i += 3) {
            int v0 = startVertex + tmp_ArrayInt.get(i);
            int v1 = startVertex + tmp_ArrayInt.get(i + 1);
            int v2 = startVertex + tmp_ArrayInt.get(i + 2);

            indices.put(v0);
            indices.put(v1);
            indices.put(v1);
            indices.put(v2);
            indices.put(v2);
            indices.put(v0);
        }

        vertexIndex += tmp_ArrayFloat.size / 2;
    }

    public void drawPolygonThin(final ArrayFloat polygon, final ArrayInt triangles,
                                float x, float y, float degrees,
                                float scaleX, float scaleY) {
        if (!drawing) throw new GraphicsException("Must call begin() before draw operations.");
        if (polygon.size < 6) throw new GraphicsException("A polygon requires a minimum of 3 vertices, so the polygon array must be of length > 6. Got: " + polygon.size);
        if (polygon.size % 2 != 0) throw new GraphicsException("Polygon must be represented as a flat array of vertices, each vertex must have x and y coordinates: " + "[x0,y0, x1,y1, ...]. Therefore, polygon array length must be even.");
        int count = polygon.size / 2;
        if (requiresFlush(count, count * 6)) flush();

        setMode(GL11.GL_LINES);

        for (int i = 0; i < polygon.size; i += 2) {
            float polyX = polygon.get(i);
            float polyY = polygon.get(i + 1);
            float u = 0.5f + polyX * currentTexture.invWidth * pixelScaleWidth;
            float v = 0.5f - polyY * currentTexture.invHeight * pixelScaleHeight;
            textCoords.put(u).put(v);

            tmp_Vector.set(polyX, polyY);
            tmp_Vector.scl(scaleX, scaleY);
            tmp_Vector.rotateDeg(degrees);
            tmp_Vector.add(x, y);
            positions.put(tmp_Vector.x).put(tmp_Vector.y);
            colors.put(currentTint);
        }

        int startVertex = vertexIndex;
        for (int i = 0; i < triangles.size; i += 3) {
            int v0 = startVertex + triangles.get(i);
            int v1 = startVertex + triangles.get(i + 1);
            int v2 = startVertex + triangles.get(i + 2);

            indices.put(v0);
            indices.put(v1);
            indices.put(v1);
            indices.put(v2);
            indices.put(v2);
            indices.put(v0);
        }

        vertexIndex += count;
    }

    // TODO: handle uv properly.
    public void drawPolygonFilled(float[] polygon) {
        if (!drawing) throw new GraphicsException("Must call begin() before draw operations.");
        if (polygon.length < 6) throw new GraphicsException("A polygon requires a minimum of 3 vertices, so the polygon array must be of length > 6. Got: " + polygon.length);
        if (polygon.length % 2 != 0) throw new GraphicsException("Polygon must be represented as a flat array of vertices, each vertex must have x and y coordinates: " + "[x0,y0, x1,y1, ...]. Therefore, polygon array length must be even.");
        int count = polygon.length / 2;
        if (requiresFlush(count, count * 3)) flush();

        setMode(GL11.GL_TRIANGLES);

        tmp_ArrayFloat.clear();
        tmp_ArrayInt.clear();
        try {
            MathUtils.polygonTriangulate(polygon, tmp_ArrayFloat, tmp_ArrayInt);
        } catch (Exception e) { // Probably the polygon has collapsed into a single point.
            return;
        }

        for (int i = 0; i < tmp_ArrayFloat.size; i += 2) {
            float polyX = tmp_ArrayFloat.get(i);
            float polyY = tmp_ArrayFloat.get(i + 1);
            float u = 0.5f + polyX * currentTexture.invWidth * pixelScaleWidth;
            float v = 0.5f - polyY * currentTexture.invHeight * pixelScaleHeight;

            textCoords.put(u).put(v);
            positions.put(polyX).put(polyY);
            colors.put(currentTint);
        }

        int startVertex = vertexIndex;
        for (int i = 0; i < tmp_ArrayInt.size; i++) {
            indices.put(startVertex + tmp_ArrayInt.get(i));
        }

        vertexIndex += tmp_ArrayFloat.size / 2;
    }

    @Deprecated
    public void drawPolygonFilled(float[] polygon, Texture texture,
                                  float x, float y, float deg,
                                  float scaleX, float scaleY) {
        if (!drawing) throw new GraphicsException("Must call begin() before draw operations.");
        if (polygon.length < 6) throw new GraphicsException("A polygon requires a minimum of 3 vertices, so the polygon array must be of length > 6. Got: " + polygon.length);
        if (polygon.length % 2 != 0) throw new GraphicsException("Polygon must be represented as a flat array of vertices, each vertex must have x and y coordinates: " + "[x0,y0, x1,y1, ...]. Therefore, polygon array length must be even.");
        int count = polygon.length / 2;
        if (requiresFlush(count, count * 3)) flush();

        setTexture(texture);
        setMode(GL11.GL_TRIANGLES);

        tmp_ArrayFloat.clear();
        tmp_ArrayInt.clear();
        try {
            MathUtils.polygonTriangulate(polygon, tmp_ArrayFloat, tmp_ArrayInt);
        } catch (Exception e) {
            // Probably the polygon has collapsed into a single point.
            return;
        }

        for (int i = 0; i < tmp_ArrayFloat.size; i += 2) {
            float polyX = tmp_ArrayFloat.get(i);
            float polyY = tmp_ArrayFloat.get(i + 1);
            float u = 0.5f + polyX * currentTexture.invWidth * pixelScaleWidth;
            float v = 0.5f - polyY * currentTexture.invHeight * pixelScaleHeight;
            textCoords.put(u).put(v);

            tmp_Vector.set(polyX, polyY);
            tmp_Vector.scl(scaleX, scaleY);
            tmp_Vector.rotateDeg(deg);
            tmp_Vector.add(x, y);
            positions.put(tmp_Vector.x).put(tmp_Vector.y);
            colors.put(currentTint);
        }

        int startVertex = vertexIndex;
        for (int i = 0; i < tmp_ArrayInt.size; i++) {
            indices.put(startVertex + tmp_ArrayInt.get(i));
        }
        vertexIndex += tmp_ArrayFloat.size / 2;
    }

    public void drawPolygonFilled(@Nullable Texture texture,
                                  @NotNull ArrayFloat polygon, @Nullable ArrayInt triangles,
                                  float x, float y, float deg, float sclX, float sclY) {
        if (texture == null) texture = defaultTexture;
        drawPolygonFilled(texture.region, polygon, triangles, x, y, deg, sclX, sclY);
    }

    // TODO: test
    public void drawPolygonFilled(@Nullable TextureRegion region,
                                  @NotNull ArrayFloat polygon, @Nullable ArrayInt triangles,
                                  float x, float y, float deg, float sclX, float sclY) {
        if (!drawing) throw new GraphicsException("Must call begin() before draw operations.");
        if (polygon.size < 6) throw new GraphicsException("A polygon requires a minimum of 3 vertices, so the polygon array must be of length > 6. Got: " + polygon.size);
        if (polygon.size % 2 != 0) throw new GraphicsException("Polygon must be represented as a flat array of vertices, each vertex must have x and y coordinates: " + "[x0,y0, x1,y1, ...]. Therefore, polygon array length must be even.");

        int vertexCount = polygon.size / 2;
        // Maximum possible indices for a triangulated polygon.
        if (requiresFlush(vertexCount, vertexCount * 3)) flush();

        region = region == null ? defaultTexture.region : region;
        if (triangles == null) {
            tmp_ArrayInt.clear();
            try {
                MathUtils.polygonTriangulate(polygon, tmp_ArrayInt);
            } catch (Exception e) {
                // Probably the polygon has collapsed into a single point.
                return;
            }
            triangles = tmp_ArrayInt;
        }
        setTexture(region.texture);
        setMode(GL11.GL_TRIANGLES);

        final float left = region.offsetX - region.originalWidthHalf;
        final float bottom = region.offsetY - region.originalHeightHalf;
        final float packedWidth = region.packedWidth;
        final float packedHeight = region.packedHeight;
        final float uRange = region.u2 - region.u1;
        final float vRange = region.v2 - region.v1;

        final float sin = MathUtils.sinDeg(deg);
        final float cos = MathUtils.cosDeg(deg);

        for (int i = 0; i < polygon.size; i += 2) {
            float polyX = polygon.get(i);
            float polyY = polygon.get(i + 1);

            float regionX = polyX - left;
            float regionY = polyY - bottom;

            float u = region.u1 + (regionX / packedWidth) * uRange;
            float v = region.v2 - (regionY / packedHeight) * vRange;

            textCoords.put(u).put(v);
            colors.put(currentTint);

            float localX = polyX * sclX;
            float localY = polyY * sclY;

            float vertexX = localX * cos - localY * sin + x;
            float vertexY = localX * sin + localY * cos + y;

            positions.put(vertexX).put(vertexY);
        }

        int startVertex = vertexIndex;
        for (int i = 0; i < triangles.size; i++) {
            indices.put(startVertex + triangles.get(i));
        }
        vertexIndex += vertexCount;
    }

    public void drawPolygonFilled(float[] polygon, Texture texture,
                                  @Nullable Function<Vector2, Vector2> uvTransform,
                                  float x, float y, float deg,
                                  float scaleX, float scaleY) {
        if (!drawing) throw new GraphicsException("Must call begin() before draw operations.");
        if (polygon.length < 6) throw new GraphicsException("A polygon requires a minimum of 3 vertices, so the polygon array must be of length > 6. Got: " + polygon.length);
        if (polygon.length % 2 != 0) throw new GraphicsException("Polygon must be represented as a flat array of vertices, each vertex must have x and y coordinates: " + "[x0,y0, x1,y1, ...]. Therefore, polygon array length must be even.");

        int count = polygon.length / 2;
        if (requiresFlush(count, count * 3)) flush();

        setTexture(texture);
        setMode(GL11.GL_TRIANGLES);

        tmp_ArrayFloat.clear();
        tmp_ArrayInt.clear();
        try {
            MathUtils.polygonTriangulate(polygon, tmp_ArrayFloat, tmp_ArrayInt);
        } catch (Exception e) {
            // Probably the polygon has collapsed into a single point.
            return;
        }

        for (int i = 0; i < tmp_ArrayFloat.size; i += 2) {
            float polyX = tmp_ArrayFloat.get(i);
            float polyY = tmp_ArrayFloat.get(i + 1);
            float u = 0.5f + polyX * currentTexture.invWidth * pixelScaleWidth;
            float v = 0.5f - polyY * currentTexture.invHeight * pixelScaleHeight;

            tmp_Vector.set(u, v);
            if (uvTransform != null) {
                uvTransform.apply(tmp_Vector);
            }
            textCoords.put(tmp_Vector.x).put(tmp_Vector.y);

            tmp_Vector.set(polyX, polyY);
            tmp_Vector.scl(scaleX, scaleY);
            tmp_Vector.rotateDeg(deg);
            tmp_Vector.add(x, y);
            positions.put(tmp_Vector.x).put(tmp_Vector.y);
            colors.put(currentTint);
        }

        int startVertex = vertexIndex;
        for (int i = 0; i < tmp_ArrayInt.size; i++) {
            indices.put(startVertex + tmp_ArrayInt.get(i));
        }
        vertexIndex += tmp_ArrayFloat.size / 2;
    }

    public void drawPolygonFilled(float[] polygon, float x, float y, float deg, float scaleX, float scaleY) {
        drawPolygonFilled(polygon, (Texture) null, x, y, deg, scaleX, scaleY);
    }

    public void drawPolygonFilled(ArrayFloat polygon, ArrayInt triangles, float x, float y, float deg, float scaleX, float scaleY) {
        final float[] points = polygon.pack();
        final int[] indices = triangles.pack();
        drawPolygonFilled(points, indices, x, y, deg, scaleX, scaleY);
    }

    public void drawPolygonFilled(float[] polygon, int[] triangles,
                                  float x, float y, float deg,
                                  float scaleX, float scaleY) {
        if (!drawing) throw new GraphicsException("Must call begin() before draw operations.");
        if (polygon.length < 6) throw new GraphicsException("A polygon requires a minimum of 3 vertices, so the polygon array must be of length > 6. Got: " + polygon.length);
        if (polygon.length % 2 != 0) throw new GraphicsException("Polygon must be represented as a flat array of vertices, each vertex must have x and y coordinates: " + "[x0,y0, x1,y1, ...]. Therefore, polygon array length must be even.");
        int vertexCount = polygon.length / 2;
        if (requiresFlush(vertexCount, triangles.length)) flush();

        setMode(GL11.GL_TRIANGLES);
        setTexture(defaultTexture);

        for (int i = 0; i < polygon.length; i += 2) {
            float polyX = polygon[i];
            float polyY = polygon[i + 1];
            float u = 0.5f + polyX * currentTexture.invWidth * pixelScaleWidth;
            float v = 0.5f - polyY * currentTexture.invHeight * pixelScaleHeight;
            textCoords.put(u).put(v);

            tmp_Vector.set(polyX, polyY);
            tmp_Vector.scl(scaleX, scaleY);
            tmp_Vector.rotateDeg(deg);
            tmp_Vector.add(x, y);
            positions.put(tmp_Vector.x).put(tmp_Vector.y);
            colors.put(currentTint);
        }

        int startVertex = vertexIndex;
        for (int triangle : triangles) {
            indices.put(startVertex + triangle);
        }
        vertexIndex += vertexCount;
    }

    /* Rendering 2D primitives - lines */

    public final void drawLineThin(float x1, float y1, float x2, float y2) {
        if (!drawing) throw new GraphicsException("Must call begin() before draw operations.");
        if (requiresFlush(2, 2)) flush();

        setMode(GL11.GL_LINES);
        setTexture(defaultTexture);

        positions.put(x1).put(y1);
        positions.put(x2).put(y2);

        colors.put(currentTint);
        colors.put(currentTint);

        textCoords.put(0.5f).put(0.5f);
        textCoords.put(0.5f).put(0.5f);

        // put indices
        int startVertex = this.vertexIndex;
        indices.put(startVertex);
        indices.put(startVertex + 1);
        vertexIndex += 2;
    }

    public final void drawLineThin(float p1X, float p1Y, float p2X, float p2Y,
                                   float x, float y, float degrees,
                                   float scaleX, float scaleY) {
        if (!drawing) throw new GraphicsException("Must call begin() before draw operations.");
        if (requiresFlush(2, 2)) flush();

        setMode(GL11.GL_LINES);
        setTexture(defaultTexture);

        tmp_Vector.set(p1X, p1Y);
        tmp_Vector.scl(scaleX, scaleY);
        tmp_Vector.rotateDeg(degrees);
        tmp_Vector.add(x, y);

        positions.put(tmp_Vector.x).put(tmp_Vector.y);
        colors.put(currentTint);
        textCoords.put(0.5f).put(0.5f);

        tmp_Vector.set(p2X, p2Y);
        tmp_Vector.scl(scaleX, scaleY);
        tmp_Vector.rotateDeg(degrees);
        tmp_Vector.add(x, y);

        positions.put(tmp_Vector.x).put(tmp_Vector.y);
        colors.put(currentTint);
        textCoords.put(0.5f).put(0.5f);

        int startVertex = vertexIndex;
        indices.put(startVertex);
        indices.put(startVertex + 1);

        vertexIndex += 2;
    }

    public void drawLineFilled(float x1, float y1, float x2, float y2, float thickness) {
        if (!drawing) throw new GraphicsException("Must call begin() before draw operations.");
        if (requiresFlush(4, 6)) flush();

        setMode(GL11.GL_TRIANGLES);
        setTexture(defaultTexture);

        tmp_Vector.x = x2 - x1;
        tmp_Vector.y = y2 - y1;
        tmp_Vector.nor();
        tmp_Vector.scl(thickness * 0.5f);
        tmp_Vector.rotate90(1);

        positions.put(x1 + tmp_Vector.x).put(y1 + tmp_Vector.y);
        positions.put(x1 - tmp_Vector.x).put(y1 - tmp_Vector.y);
        positions.put(x2 - tmp_Vector.x).put(y2 - tmp_Vector.y);
        positions.put(x2 + tmp_Vector.x).put(y2 + tmp_Vector.y);

        colors.put(currentTint);
        colors.put(currentTint);
        colors.put(currentTint);
        colors.put(currentTint);

        textCoords.put(0.5f).put(0.5f);
        textCoords.put(0.5f).put(0.5f);
        textCoords.put(0.5f).put(0.5f);
        textCoords.put(0.5f).put(0.5f);

        int startVertex = vertexIndex;
        indices.put(startVertex);
        indices.put(startVertex + 1);
        indices.put(startVertex + 2);
        indices.put(startVertex);
        indices.put(startVertex + 2);
        indices.put(startVertex + 3);

        vertexIndex += 4;
    }

    public void drawLineFilled(float x1, float y1, float x2, float y2,
                               float thickness, float x, float y,
                               float degrees, float scaleX, float scaleY) {
        if (!drawing) throw new GraphicsException("Must call begin() before draw operations.");
        if (requiresFlush(4, 6)) flush();

        setMode(GL11.GL_TRIANGLES);
        setTexture(defaultTexture);

        float dirX = x2 - x1;
        float dirY = y2 - y1;
        float length = (float) Math.sqrt(dirX * dirX + dirY * dirY);
        float scale = thickness * 0.5f / length;
        float offsetX = -dirY * scale;
        float offsetY = dirX * scale;

        tmp_Vector.set(x1 + offsetX, y1 + offsetY);
        tmp_Vector.scl(scaleX, scaleY).rotateDeg(degrees).add(x, y);
        positions.put(tmp_Vector.x).put(tmp_Vector.y);

        tmp_Vector.set(x1 - offsetX, y1 - offsetY);
        tmp_Vector.scl(scaleX, scaleY).rotateDeg(degrees).add(x, y);
        positions.put(tmp_Vector.x).put(tmp_Vector.y);

        tmp_Vector.set(x2 - offsetX, y2 - offsetY);
        tmp_Vector.scl(scaleX, scaleY).rotateDeg(degrees).add(x, y);
        positions.put(tmp_Vector.x).put(tmp_Vector.y);

        tmp_Vector.set(x2 + offsetX, y2 + offsetY);
        tmp_Vector.scl(scaleX, scaleY).rotateDeg(degrees).add(x, y);
        positions.put(tmp_Vector.x).put(tmp_Vector.y);

        colors.put(currentTint);
        colors.put(currentTint);
        colors.put(currentTint);
        colors.put(currentTint);

        textCoords.put(0.5f).put(0.5f);
        textCoords.put(0.5f).put(0.5f);
        textCoords.put(0.5f).put(0.5f);
        textCoords.put(0.5f).put(0.5f);

        int startVertex = vertexIndex;
        indices.put(startVertex);
        indices.put(startVertex + 1);
        indices.put(startVertex + 2);
        indices.put(startVertex);
        indices.put(startVertex + 2);
        indices.put(startVertex + 3);

        vertexIndex += 4;
    }

    /* Rendering 2D primitives - curves */

    public void drawCurveThin(final Vector2... values) {
        if (!drawing) throw new GraphicsException("Must call begin() before draw operations.");
        if (values == null || values.length < 2) return;
        if (requiresFlush(values.length, values.length * 2)) flush();

        setMode(GL11.GL_LINES);
        setTexture(defaultTexture);

        /* put vertices */
        for (Vector2 value : values) {
            positions.put(value.x).put(value.y);
            colors.put(currentTint);
            textCoords.put(0.5f).put(0.5f);
        }

        /* put indices */
        int startVertex = this.vertexIndex;
        for (int i = 0; i < values.length - 1; i++) {
            indices.put(startVertex + i);
            indices.put(startVertex + i + 1);
        }
        vertexIndex += values.length;
    }

    public void drawCurveThin(final Vector2[] values, float x, float y, float deg, float scaleX, float scaleY) {
        if (!drawing) throw new GraphicsException("Must call begin() before draw operations.");
        if (values == null || values.length < 2) return;
        if (requiresFlush(values.length, values.length * 2)) flush();

        setMode(GL11.GL_LINES);
        setTexture(defaultTexture);

        for (Vector2 value : values) {
            tmp_Vector.set(value.x, value.y);
            tmp_Vector.scl(scaleX, scaleY);
            tmp_Vector.rotateDeg(deg);
            tmp_Vector.add(x, y);
            positions.put(tmp_Vector.x).put(tmp_Vector.y);
            colors.put(currentTint);
            textCoords.put(0.5f).put(0.5f);
        }

        int startVertex = vertexIndex;
        for (int i = 0; i < values.length - 1; i++) {
            indices.put(startVertex + i);
            indices.put(startVertex + i + 1);
        }
        vertexIndex += values.length;
    }


    // The filled curve tesselation algorithm works.
    // It does not handle edge cases of high thickness / segment length ratio, but I that is a degenerate case.
    // Note: might produce rendering artifacts for highly refined functions and a color with transparency.
    public void drawCurveFilled(float stroke, int smoothness, final Array<Vector2> points, float x, float y, float deg, float scaleX, float scaleY) {
        if (!drawing) throw new GraphicsException("Must call begin() before draw operations.");
        setMode(GL11.GL_TRIANGLES);
        setTexture(defaultTexture);

        Vector2[] points_transformed = new Vector2[points.size];
        /* transform vertices */
        for (int i = 0; i < points_transformed.length; i++) {
            Vector2 vertex = new Vector2(points.get(i));
            vertex.scl(scaleX, scaleY);
            vertex.rotateDeg(deg);
            vertex.add(x, y);
            points_transformed[i] = vertex;
        }

        Array<Vector2> vertices = curveFilledCalculateVertices(stroke, smoothness, points_transformed);
        if (requiresFlush(vertices.size, vertices.size)) flush();

        /*
        In the case of curve rendering, we might have a case where the number of vertices exceeds the capacity of the entire batch.
        In that case, we write triangle by triangle.
        */
        if (vertices.size > VERTICES_CAPACITY) {
            for (int i = 0; i < vertices.size; i += 3) {
                Vector2 p0 = vertices.get(i + 0);
                Vector2 p1 = vertices.get(i + 1);
                Vector2 p2 = vertices.get(i + 2);
                drawTriangleFilled(p0, p1, p2);
            }
        } else {
            for (int i = 0; i < vertices.size; i++) {
                Vector2 vertex = vertices.get(i);
                positions.put(vertex.x).put(vertex.y);
                colors.put(currentTint);
                textCoords.put(0.5f).put(0.5f);
                indices.put(vertexIndex + i);
            }

            vertexIndex += vertices.size;
        }
    }

    // The filled curve tesselation algorithm works.
    // It does not handle edge cases of high thickness / segment length ratio, but I that is a degenerate case.
    // Note: might produce rendering artifacts for highly refined functions and a color with transparency.
    public void drawCurveFilled(float stroke, int smoothness, final Vector2[] points, float x, float y, float deg, float scaleX, float scaleY) {
        if (!drawing) throw new GraphicsException("Must call begin() before draw operations.");
        setMode(GL11.GL_TRIANGLES);
        setTexture(defaultTexture);

        Vector2[] points_transformed = new Vector2[points.length];
        /* transform vertices */
        for (int i = 0; i < points_transformed.length; i++) {
            Vector2 vertex = new Vector2(points[i]);
            vertex.scl(scaleX, scaleY);
            vertex.rotateDeg(deg);
            vertex.add(x, y);
            points_transformed[i] = vertex;
        }

        Array<Vector2> vertices = curveFilledCalculateVertices(stroke, smoothness, points_transformed);
        if (requiresFlush(vertices.size, vertices.size)) flush();

        /*
        In the case of curve rendering, we might have a case where the number of vertices exceeds the capacity of the entire batch.
        In that case, we write triangle by triangle.
        */
        if (vertices.size > VERTICES_CAPACITY) {
            for (int i = 0; i < vertices.size; i += 3) {
                Vector2 p0 = vertices.get(i + 0);
                Vector2 p1 = vertices.get(i + 1);
                Vector2 p2 = vertices.get(i + 2);
                drawTriangleFilled(p0, p1, p2);
            }
        } else {
            for (int i = 0; i < vertices.size; i++) {
                Vector2 vertex = vertices.get(i);
                positions.put(vertex.x).put(vertex.y);
                colors.put(currentTint);
                textCoords.put(0.5f).put(0.5f);
                indices.put(vertexIndex + i);
            }
            vertexIndex += vertices.size;
        }
    }

    private Array<Vector2> curveFilledCalculateVertices(float stroke, int smoothness, Vector2... points) {
        if (points.length < 2) {
            return null;
        }

        float lineWidth = Math.abs(stroke) * 0.5f;
        Array<Vector2> vertices = new Array<>();
        Array<Vector2> middlePoints = new Array<>();  // middle points: one for each line segment.
        boolean closed = false;

        if (points.length == 2) {
            Vector2 midPoint = new Vector2();
            Vector2.midPoint(points[0], points[1], midPoint);
            createTriangles(points[0], midPoint, points[1], lineWidth, smoothness, vertices);
        } else {

            if (points[0].equals(points[points.length - 1])) {
                Vector2 p0 = new Vector2();
                Vector2.midPoint(points[0], points[1], p0);
                Vector2[] points2 = new Vector2[points.length + 1];
                points2[0] = p0;
                System.arraycopy(points, 1, points2, 1, points.length - 1);
                points2[points.length] = p0;
                points = points2;
                closed = true;
            }

            for (int i = 0; i < points.length - 1; i++) {
                if (i == 0) {
                    middlePoints.add(points[0]);
                } else if (i == points.length - 2) {
                    middlePoints.add(points[points.length - 1]);
                } else {
                    Vector2 mid = new Vector2();
                    Vector2.midPoint(points[i], points[i + 1], mid);
                    middlePoints.add(mid);
                }
            }

            for (int i = 1; i < middlePoints.size; i++) {
                createTriangles(middlePoints.get(i - 1), points[i], middlePoints.get(i), lineWidth, smoothness, vertices);
            }
        }

        if (!closed) {
            Vector2 p00 = vertices.get(0);
            Vector2 p01 = vertices.get(1);
            Vector2 p02 = points[1];
            Vector2 p10 = vertices.get(vertices.size - 1);
            Vector2 p11 = vertices.get(vertices.size - 3);
            Vector2 p12 = points[points.length - 2];
            createRoundCap(points[0], p00, p01, p02, smoothness, vertices);
            createRoundCap(points[points.length - 1], p10, p11, p12, smoothness, vertices);
        }

        return vertices;
    }

    private void createTriangles(Vector2 p0, Vector2 p1, Vector2 p2, float width, int refinement, Array<Vector2> out) {
        var t0 = new Vector2(p1).sub(p0);
        var t2 = new Vector2(p2).sub(p1);

        t0.rotate90(1);
        t2.rotate90(1);

        float signedTriangleArea = MathUtils.areaTriangleSigned(p0, p1, p2);
        if (signedTriangleArea > 0) {
            t0.flip();
            t2.flip();
        }

        t0.nor();
        t2.nor();
        t0.scl(width);
        t2.scl(width);

        Vector2 pIntersection = new Vector2();
        MathUtils.segmentsIntersection(new Vector2(t0).add(p0), new Vector2(t0).add(p1), new Vector2(t2).add(p2), new Vector2(t2).add(p1), pIntersection);

        Vector2 anchor = new Vector2(pIntersection).sub(p1);
        float anchorLength = anchor.len();
        Vector2 p0p1 = new Vector2(p0).sub(p1);
        float p0p1Length = p0p1.len();
        Vector2 p1p2 = new Vector2(p1).sub(p2);
        float p1p2Length = p1p2.len();

        if (anchorLength > p0p1Length || anchorLength > p1p2Length) {

            out.add(new Vector2(p0).add(t0));
            out.add(new Vector2(p0).sub(t0));
            out.add(new Vector2(p1).add(t0));

            out.add(new Vector2(p0).sub(t0));
            out.add(new Vector2(p1).add(t0));
            out.add(new Vector2(p1).sub(t0));

            createRoundCap(p1, new Vector2(p1).add(t0), new Vector2(p1).add(t2), p2, refinement, out);

            out.add(new Vector2(p2).add(t2));
            out.add(new Vector2(p1).sub(t2));
            out.add(new Vector2(p1).add(t2));

            out.add(new Vector2(p2).add(t2));
            out.add(new Vector2(p1).sub(t2));
            out.add(new Vector2(p2).sub(t2));

        } else {

            out.add(new Vector2(p0).add(t0));
            out.add(new Vector2(p0).sub(t0));
            out.add(new Vector2(p1).sub(anchor));

            out.add(new Vector2(p0).add(t0));
            out.add(new Vector2(p1).sub(anchor));
            out.add(new Vector2(p1).add(t0));

            Vector2 _p0 = new Vector2(p1).add(t0);
            Vector2 _p1 = new Vector2(p1).add(t2);
            Vector2 _p2 = new Vector2(p1).sub(anchor);

            Vector2 center = p1;

            out.add(_p0);
            out.add(center);
            out.add(_p2);

            createRoundCap(center, _p0, _p1, _p2, refinement, out);

            out.add(center);
            out.add(_p1);
            out.add(_p2);

            out.add(new Vector2(p2).add(t2));
            out.add(new Vector2(p1).sub(anchor));
            out.add(new Vector2(p1).add(t2));

            out.add(new Vector2(p2).add(t2));
            out.add(new Vector2(p1).sub(anchor));
            out.add(new Vector2(p2).sub(t2));
        }
    }

    private void createRoundCap(Vector2 center, Vector2 _p0, Vector2 _p1, Vector2 nextPointInLine, int refinement, Array<Vector2> out) {
        float radius = new Vector2(center).sub(_p0).len();

        float angle0 = MathUtils.atan2((_p1.y - center.y), (_p1.x - center.x));
        float angle1 = MathUtils.atan2((_p0.y - center.y), (_p0.x - center.x));

        float orgAngle0 = angle0;

        if (angle1 > angle0) {
            if (angle1 - angle0 >= MathUtils.PI - MathUtils.FLOAT_ROUNDING_ERROR) {
                angle1 = angle1 - 2 * MathUtils.PI;
            }
        } else {
            if (angle0 - angle1 >= MathUtils.PI - MathUtils.FLOAT_ROUNDING_ERROR) {
                angle0 = angle0 - 2 * MathUtils.PI;
            }
        }

        var angleDiff = angle1 - angle0;

        if (Math.abs(angleDiff) >= MathUtils.PI - MathUtils.FLOAT_ROUNDING_ERROR && Math.abs(angleDiff) <= Math.PI + MathUtils.FLOAT_ROUNDING_ERROR) {
            var r1 = new Vector2(center).sub(nextPointInLine);
            if (r1.x == 0) {
                if (r1.y > 0) {
                    angleDiff = -angleDiff;
                }
            } else if (r1.x >= -MathUtils.FLOAT_ROUNDING_ERROR ) {
                angleDiff = -angleDiff;
            }
        }

        float da = angleDiff / refinement;
        for (int i = 0; i < refinement; i++) {
            out.add(new Vector2(center.x, center.y));
            out.add(new Vector2(
                    center.x + radius * MathUtils.cosRad(orgAngle0 + da * i),
                    center.y + radius * MathUtils.sinRad(orgAngle0 + da * i)
            ));
            out.add(new Vector2(
                    center.x + radius * MathUtils.cosRad(orgAngle0 + da * (1 + i)),
                    center.y + radius * MathUtils.sinRad(orgAngle0 + da * (1 + i))
            ));
        }
    }

    /* Rendering 2D primitives - triangles */

    public void drawTriangleFilled(@NotNull Texture texture,
                                   float x1, float y1, float u1, float v1,
                                   float x2, float y2, float u2, float v2,
                                   float x3, float y3, float u3, float v3) {
        drawTriangleFilled(texture,
                x1, y1, currentTint, u1, v1,
                x2, y2, currentTint, u2, v2,
                x3, y3, currentTint, u3, v3
        );
    }

    public void drawTriangleFilled(Vector2 p0,
                                   Vector2 p1,
                                   Vector2 p2) {
        drawTriangleFilled(null,
                p0.x, p0.y, currentTint, 0.5f, 0.5f,
                p1.x, p1.y, currentTint, 0.5f, 0.5f,
                p2.x, p2.y, currentTint, 0.5f, 0.5f
        );
    }

    public void drawTriangleFilled(float x1, float y1,
                                   float x2, float y2,
                                   float x3, float y3) {
        drawTriangleFilled(null,
                x1, y1, currentTint, 0.5f, 0.5f,
                x2, y2, currentTint, 0.5f, 0.5f,
                x3, y3, currentTint, 0.5f, 0.5f
        );
    }

    public void drawTriangleFilled(float x1, float y1, float c1,
                                   float x2, float y2, float c2,
                                   float x3, float y3, float c3) {
        drawTriangleFilled(null,
                x1, y1, c1, 0.5f, 0.5f,
                x2, y2, c2, 0.5f, 0.5f,
                x3, y3, c3, 0.5f, 0.5f
        );
    }

    public void drawTriangleFilled(@Nullable Texture texture,
                                   float x1, float y1, float c1, float u1, float v1,
                                   float x2, float y2, float c2, float u2, float v2,
                                   float x3, float y3, float c3, float u3, float v3) {
        if (!drawing) throw new GraphicsException("Must call begin() before draw operations.");
        if (requiresFlush(3, 3)) flush();

        setTexture(texture);
        setMode(GL11.GL_TRIANGLES);

        positions.put(x1).put(y1);
        positions.put(x2).put(y2);
        positions.put(x3).put(y3);

        colors.put(c1);
        colors.put(c2);
        colors.put(c3);

        textCoords.put(u1).put(v1);
        textCoords.put(u2).put(v2);
        textCoords.put(u3).put(v3);

        indices.put(vertexIndex);
        indices.put(vertexIndex+1);
        indices.put(vertexIndex+2);
        vertexIndex += 3;
    }

    /* Rendering 2D primitives - Strings */

    public void drawStringLine(final String line, int size, boolean antialiasing, float offsetX, float offsetY, float x, float y, float deg, float sclX, float sclY) {
        drawStringLine(line, size, antialiasing, 0, line.length(), offsetX, offsetY, x,y,deg,sclX,sclY);
    }

    public void drawStringLine(final String line, int size, boolean antialiasing, float x, float y, float deg, float sclX, float sclY) {
        drawStringLine(line, size, antialiasing, 0, line.length(),0, 0, x, y, deg, sclX, sclY);
    }

    public void drawStringLine(final String line, int size, boolean antialiasing,
                               int startIndex, int endIndex,
                               float offsetX, float offsetY,
                               float x, float y, float deg,
                               float sclX, float sclY) {
        if (!drawing) throw new GraphicsException("Must call begin() before draw operations.");

        flush();
        setMode(GL11.GL_TRIANGLES);

        /* calculate the line total width */
        float totalWidth = 0;
        for (int i = startIndex; i < endIndex; i++) {
            char c = line.charAt(i);
            final Font.Glyph glyph = currentFont.getGlyph(c, size, antialiasing);
            if (glyph == null) continue;
            totalWidth += glyph.advanceX;
        }

        /* render a quad for every character */
        float penX = -totalWidth * 0.5f;
        float penY = -size * 0.25f;

        for (int i = startIndex; i < endIndex; i++) {
            char c = line.charAt(i);
            final Font.Glyph glyph = currentFont.getGlyph(c, size, antialiasing);
            if (glyph == null) continue;

            setTexture(glyph.texture);

            /* calculate the quad's x, y, width, height */
            float charX = penX + glyph.bearingX;
            float charY = penY - (glyph.height - glyph.bearingY);
            float w = glyph.width;
            float h = glyph.height;

            /* calculate the quad's UV coordinates */
            float u0 = glyph.atlasX * glyph.texture.invWidth;
            float v0 = glyph.atlasY * glyph.texture.invHeight;
            float u1 = (glyph.atlasX + glyph.width) * glyph.texture.invWidth;
            float v1 = (glyph.atlasY + glyph.height) * glyph.texture.invHeight;

            int startVertex = vertexIndex;

            // vertex 1
            tmp_Vector.set(charX + offsetX, charY + h + offsetY);
            tmp_Vector.scl(sclX, sclY).rotateDeg(deg).add(x, y);
            positions.put(tmp_Vector.x).put(tmp_Vector.y);
            colors.put(currentTint);
            textCoords.put(u0).put(v0);

            // vertex 2
            tmp_Vector.set(charX + offsetX, charY + offsetY);
            tmp_Vector.scl(sclX, sclY).rotateDeg(deg).add(x, y);
            positions.put(tmp_Vector.x).put(tmp_Vector.y);
            colors.put(currentTint);
            textCoords.put(u0).put(v1);

            // vertex 3
            tmp_Vector.set(charX + w + offsetX, charY + offsetY);
            tmp_Vector.scl(sclX, sclY).rotateDeg(deg).add(x, y);
            positions.put(tmp_Vector.x).put(tmp_Vector.y);
            colors.put(currentTint);
            textCoords.put(u1).put(v1);

            // vertex 4
            tmp_Vector.set(charX + w + offsetX, charY + h + offsetY);
            tmp_Vector.scl(sclX, sclY).rotateDeg(deg).add(x, y);
            positions.put(tmp_Vector.x).put(tmp_Vector.y);
            colors.put(currentTint);
            textCoords.put(u1).put(v0);

            indices.put(startVertex);
            indices.put(startVertex + 1);
            indices.put(startVertex + 3);
            indices.put(startVertex + 3);
            indices.put(startVertex + 1);
            indices.put(startVertex + 2);

            vertexIndex += 4;

            penX += glyph.advanceX;
            penY += glyph.advanceY;
        }
    }

//    public void drawStringLine_old(final String line, int size, boolean antialiasing, int startIndex, int endIndex, float offsetX, float offsetY, float x, float y, float deg, float sclX, float sclY) {
//        if (!drawing) throw new GraphicsException("Must call begin() before draw operations.");
//        flush();
//        if (requiresFlush(line.length() * 4, line.length() * 4)) flush();
//
//        setMode(GL11.GL_TRIANGLES);
//
//        /* calculate the line total width */
//        float total_width = 0;
//        for (int i = startIndex; i < endIndex; i++) {
//            char c = line.charAt(i);
//            final Font.Glyph glyph = currentFont.getGlyph(c, size, antialiasing);
//            if (glyph == null) continue;
//            total_width += glyph.advanceX;
//        }
//
//        vertices.clear();
//
//        /* render a quad for every character */
//        float penX = -total_width * 0.5f;
//        float penY = -size * 0.25f;
//        for (int i = startIndex; i < endIndex; i++) {
//            char c = line.charAt(i);
//            final Font.Glyph glyph = currentFont.getGlyph(c, size, antialiasing);
//            if (glyph == null) continue;
//
//            setTexture(glyph.texture);
//
//            /* calculate the quad's x, y, width, height */
//            float char_x = penX + glyph.bearingX;
//            float char_y = penY - (glyph.height - glyph.bearingY);
//            float w = glyph.width;
//            float h = glyph.height;
//
//            /* calculate the quad's uv coordinates */
//            float u0 = glyph.atlasX * glyph.texture.invWidth;
//            float v0 = (glyph.atlasY) * glyph.texture.invHeight;
//            float u1 = (glyph.atlasX + glyph.width) * glyph.texture.invWidth;
//            float v1 = (glyph.atlasY + glyph.height) * glyph.texture.invHeight;
//
//            /* put vertices */
//            Vector2 vertex_1 = vectors2Pool.allocate();
//            vertex_1.set(char_x + offsetX, char_y + h + offsetY);
//            colors.put(currentTint);
//            textCoords.put(u0).put(v0);
//
//            Vector2 vertex_2 = vectors2Pool.allocate();
//            vertex_2.set(char_x + offsetX, char_y + offsetY);
//            colors.put(currentTint);
//            textCoords.put(u0).put(v1);
//
//            Vector2 vertex_3 = vectors2Pool.allocate();
//            vertex_3.set(char_x + w + offsetX, char_y + offsetY);
//            colors.put(currentTint);
//            textCoords.put(u1).put(v1);
//
//            Vector2 vertex_4 = vectors2Pool.allocate();
//            vertex_4.set(char_x + w + offsetX, char_y + h + offsetY);
//            colors.put(currentTint);
//            textCoords.put(u1).put(v0);
//
//            vertices.add(vertex_1);
//            vertices.add(vertex_2);
//            vertices.add(vertex_3);
//            vertices.add(vertex_4);
//
//            /* put indices */
//            int startVertex = this.vertexIndex;
//            indices.put(startVertex + 0);
//            indices.put(startVertex + 1);
//            indices.put(startVertex + 3);
//            indices.put(startVertex + 3);
//            indices.put(startVertex + 1);
//            indices.put(startVertex + 2);
//            vertexIndex += 4;
//
//            penX += glyph.advanceX;
//            penY += glyph.advanceY;
//        }
//
//        for (Vector2 vertex : vertices) {
//            vertex.scl(sclX, sclY).rotateDeg(deg).add(x, y);
//            positions.put(vertex.x).put(vertex.y);
//        }
//
//        vectors2Pool.freeAll(vertices);
//    }

    /* Rendering primitives: Functions */

    public void drawFunctionThin(int widthPixels, float minX, float maxX, int refinement,
                                 Function<Float, Float> f,
                                 float x, float y, float degrees,
                                 float scaleX, float scaleY) {
        if (!drawing) throw new GraphicsException("Must call begin() before draw operations.");

        refinement = Math.max(2, refinement);
        if (requiresFlush(refinement, refinement * 2)) flush();

        setMode(GL11.GL_LINES);
        setTexture(defaultTexture);
        if (minX > maxX) {
            float tmp = minX;
            minX = maxX;
            maxX = tmp;
        }

        float domainLength = maxX - minX;
        float domainLengthInv = 1 / domainLength;
        scaleX *= widthPixels * domainLengthInv * pixelScaleWidthInv;
        scaleY *= widthPixels * domainLengthInv * pixelScaleHeightInv;
        float step = domainLength / refinement;
        for (int i = 0; i < refinement; i++) {
            tmp_Vector.x = minX + i * step;
            tmp_Vector.y = f.apply(tmp_Vector.x);
            tmp_Vector.scl(scaleX, scaleY);
            tmp_Vector.rotateDeg(degrees);
            tmp_Vector.add(x, y);
            positions.put(tmp_Vector.x).put(tmp_Vector.y);
            colors.put(currentTint);
            textCoords.put(0.5f).put(0.5f);
        }

        int startVertex = vertexIndex;
        for (int i = 0; i < refinement - 1; i++) {
            indices.put(startVertex + i);
            indices.put(startVertex + i + 1);
        }
        vertexIndex += refinement;
    }

    public void drawFunctionFilled(int widthPixels, int strokePixels, int smoothness, float minX, float maxX, int refinement, Function<Float, Float> f, float x, float y) {
        drawFunctionFilled(widthPixels, strokePixels, smoothness, minX, maxX, refinement, f, x, y, 0, 1, 1);
    }

    // TODO: optimize
    public void drawFunctionFilled(int widthPixels, int strokePixels, int smoothness, float minX, float maxX, int refinement, Function<Float, Float> f, float x, float y, float deg, float scaleX, float scaleY) {
        refinement = Math.max(2, refinement);

        if (minX > maxX) {
            float tmp = minX;
            minX = maxX;
            maxX = tmp;
        }
        float domainLength = maxX - minX;
        float domainLengthInv = 1 / domainLength;
        scaleX = scaleX * widthPixels * domainLengthInv * pixelScaleWidthInv;
        scaleY = scaleY * widthPixels * domainLengthInv * pixelScaleHeightInv;
        float step = domainLength / refinement;

        Vector2[] points = new Vector2[refinement];
        for (int i = 0; i < refinement; i++) {
            Vector2 vertex = new Vector2();
            vertex.x = minX + i * step;
            vertex.y = f.apply(vertex.x);
            points[i] = vertex;
        }

        drawCurveFilled(strokePixels * pixelScaleHeightInv, smoothness, points, x, y, deg, scaleX, scaleY);
    }

    /* Rendering Meshes */

    // TODO
    public void drawMesh(@Nullable Shader shader, @Nullable Map<String, Object> shaderUniforms, Map<VertexAttribute, ArrayFloat> vbos, ArrayInt triangles) {

    }

    /* Rendering Ops: ensureCapacity(), flush(), end(), deleteAll(), createDefaults...() */

    /**
     * returns true if the batch needs a flush (at full capacity) before the next draw operation.
     * @param extraVertices the number of vertices that the next operation will write to the batch
     * @param extraIndices the number of indices that the next operation will write to the batch
     * @return true if the batch is at full vertex capacity
     */
    private boolean requiresFlush(int extraVertices, int extraIndices) {
        boolean hasSpaceVertices = vertexIndex + extraVertices < VERTICES_CAPACITY;
        boolean hasSpaceIndices  = indices.position() + extraIndices < indices.capacity();
        return !hasSpaceVertices || !hasSpaceIndices;
    }

    // TODO: work on this when trying out multiple shaders and general mesh rendering.
    public void flush_2() {
        if (vertexIndex == 0) return;

        // copy used buffers to the gpu
        GL30.glBindVertexArray(vao);
        for (int i = 0; i < VertexAttribute.USED_FOR_2D_RENDERING.length; i++) {
            VertexAttribute attribute = VertexAttribute.USED_FOR_2D_RENDERING[i];
            int vbo = vbos[i];
            if (vbo == -1) continue;
            FloatBuffer buffer = vaoBatch[i];
            if (currentShader.hasVertexAttribute(attribute) && buffer != null) {
                buffer.flip();
                GL20.glEnableVertexAttribArray(attribute.glslLocation); // enable attribute
                GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
                GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, buffer);
            } else {
                GL20.glDisableVertexAttribArray(attribute.glslLocation); // disable attribute
            }
        }

        // draw elements
        if (indices.position() == 0) {
            GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, ebo);
            GL15.glBufferSubData(GL15.GL_ELEMENT_ARRAY_BUFFER, 0, indices);
            GL11.glDrawElements(currentMode, indices.limit(), GL11.GL_UNSIGNED_INT, 0);
        } else { // draw arrays
            GL11.glDrawArrays(currentMode, 0, vertexIndex);
        }

        // reset
        GL30.glBindVertexArray(0);
        for (FloatBuffer buffer : vaoBatch) {
            if (buffer != null) buffer.clear();
        }
        indices.clear();
        vertexIndex = 0;
        perFrameDrawCalls++;
    }

    // TODO: revisit with VertexAttribute in mind.
    @Deprecated public void flush() {
        if (vertexIndex == 0) return;

        GL30.glBindVertexArray(vao);
        positions.flip();
        colors.flip();
        textCoords.flip();
        indices.flip();

        // TODO: should probably be in the loop.
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboPositions);
        GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, positions);

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboColors);
        GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, colors);

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboTextCoords);
        GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, textCoords);

        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, ebo);
        GL15.glBufferSubData(GL15.GL_ELEMENT_ARRAY_BUFFER, 0, indices);

        for (VertexAttribute attribute : VertexAttribute.USED_FOR_2D_RENDERING) {
            final boolean hasAttribute = (currentShader.vertexAttributesBitmask & attribute.bitmask) != 0;
            if (hasAttribute) GL20.glEnableVertexAttribArray(attribute.glslLocation); // enable attribute
            else GL20.glDisableVertexAttribArray(attribute.glslLocation); // disable attribute
        }
        GL11.glDrawElements(currentMode, indices.limit(), GL11.GL_UNSIGNED_INT, 0);


        GL30.glBindVertexArray(0);
        positions.clear();
        colors.clear();
        textCoords.clear();
        indices.clear();
        vertexIndex = 0;
        perFrameDrawCalls++;
    }

    public void end() {
        if (!drawing) throw new GraphicsException("Called " + Renderer2D.class.getSimpleName() + ".end() without calling " + Renderer2D.class.getSimpleName() + ".begin() first.");
        if (drawingToStencil) throw new GraphicsException("Called end() while still drawing to stencil. Must call stencilMaskEnd() after stencilMaskBegin() and before end().");
        flush();
        GL20.glDepthMask(true);
        GL11.glEnable(GL11.GL_CULL_FACE);
        currentCamera = null;
        currentShader = null;
        drawing = false;
        Graphics.activeRenderer2Ds--;
    }

    @Override
    public void deleteAll() {
        defaultShader.delete();
        GL30.glDeleteVertexArrays(vao);
        GL30.glDeleteBuffers(vboPositions);
        GL30.glDeleteBuffers(vboColors);
        GL30.glDeleteBuffers(vboTextCoords);
        GL30.glDeleteBuffers(ebo);
        defaultTexture.delete();
        defaultFont.delete();
    }

    /* Create defaults: shader, texture (single white pixel), camera */

    private static Shader createDefaultShaderProgram() {
        try (InputStream vertexShaderInputStream = Renderer2D.class.getClassLoader().getResourceAsStream("graphics-2d-default-shader.vert");
             BufferedReader vertexShaderBufferedReader = new BufferedReader(new InputStreamReader(vertexShaderInputStream, StandardCharsets.UTF_8));
             InputStream fragmentShaderInputStream = Renderer2D.class.getClassLoader().getResourceAsStream("graphics-2d-default-shader.frag");
             BufferedReader fragmentShaderBufferedReader = new BufferedReader(new InputStreamReader(fragmentShaderInputStream, StandardCharsets.UTF_8))) {

            String vertexShader = vertexShaderBufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));
            String fragmentShader = fragmentShaderBufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));
            return new Shader(vertexShader, fragmentShader);
        } catch (Exception e) {
            System.err.println("Could not create shader program from resources. Creating manually.");

            String vertexShader = """
                    #version 450

                    // attributes
                    layout(location = 0) in vec2 a_position;
                    layout(location = 1) in vec4 a_color;
                    layout(location = 2) in vec2 a_textCoords0;

                    // uniforms
                    uniform mat4 u_camera_combined;

                    // outputs
                    out vec4 color;
                    out vec2 uv;

                    void main() {
                        color = a_color;
                        uv = a_textCoords0;
                        gl_Position = u_camera_combined * vec4(a_position.x, a_position.y, 0.0, 1.0);
                    };""";

            String fragmentShader = """
                    #version 450

                    // inputs
                    in vec4 color;
                    in vec2 uv;

                    // uniforms
                    uniform sampler2D u_texture;

                    // outputs
                    layout (location = 0) out vec4 out_color;

                    void main() {
                        out_color = color * texture(u_texture, uv);
                    }""";

            return new Shader(vertexShader, fragmentShader);
        }
    }

    /*
    creates a single-white-pixel texture.
     */
    private static Texture createDefaultTexture() {
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

    private static Camera createDefaultCamera() {
        return new Camera(Camera.Mode.ORTHOGRAPHIC, Graphics.getWindowWidth(), Graphics.getWindowHeight(), 1, 0, 100, 80);
    }

    private static Font createDefaultFont() {
        try (InputStream inputStream = Renderer2D.class.getClassLoader().getResourceAsStream("LiberationSans-Regular.ttf")) {
            if (inputStream == null) throw new GraphicsException("Resource not found: " + "LiberationSans-Regular.ttf");
            byte[] bytes = inputStream.readAllBytes();
            ByteBuffer buffer = MemoryUtil.memAlloc(bytes.length);
            buffer.put(bytes);
            buffer.flip(); // Prepare the buffer for reading
            return new Font(buffer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /* auxiliary methods */

    public static float calculateStringLineWidth(final String line, @Nullable Font font, int fontSize, boolean antialiasing) {
        return calculateStringLineWidth(line, 0, line.length(), font, fontSize, antialiasing);
    }

    public static float calculateStringLineWidth(final String line, int fromIndex, int toIndex, @Nullable Font font, int fontSize, boolean antialiasing) {
        font = Objects.requireNonNullElse(font, defaultFont);
        float total_width = 0;
        for (int i = fromIndex; i < toIndex; i++) {
            char c = line.charAt(i);
            final Font.Glyph glyph = font.getGlyph(c, fontSize, antialiasing);
            if (glyph == null) continue;
            total_width += glyph.advanceX;
        }
        return total_width;
    }

}