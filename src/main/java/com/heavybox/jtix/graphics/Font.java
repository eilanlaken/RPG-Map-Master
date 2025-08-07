package com.heavybox.jtix.graphics;

import com.heavybox.jtix.assets.Assets;
import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.collections.Tuple3;
import com.heavybox.jtix.math.MathUtils;
import com.heavybox.jtix.memory.MemoryResource;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.util.freetype.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/*
TODO:
harfbuzz example:
https://github.com/tangrams/harfbuzz-example/blob/master/src/main.cpp
 */
public class Font implements MemoryResource {

    private final FT_Face                                         ftFace;
    private final Map<Integer, GlyphNotebook>                     glyphsNotebooks = new HashMap<>();
    private final Map<Tuple3<Character, Integer, Boolean>, Glyph> cache           = new HashMap<>(); // <char, size, antialiasing?> -> Glyph
    private final ByteBuffer                                      buffer;
    // TODO: just to be on the safe side. I want to keep a reference so that the GC does not delete
    // TODO: the ByteBuffer while it is still being used by FreeType.

    public Font(final String fontPath) {
        long library = Graphics.getFreeType();
        try {
            this.buffer = Assets.fileToByteBuffer(fontPath);
        } catch (Exception e) {
            throw new GraphicsException("Could not read " + fontPath + " into ByteBuffer. Exception: " + e.getMessage());
        }
        PointerBuffer facePointerBuffer = BufferUtils.createPointerBuffer(1);
        FreeType.FT_New_Memory_Face(library, buffer, 0, facePointerBuffer); // each ttf file may have multiple indices / multiple faces. Guarantees to have 0
        long face = facePointerBuffer.get(0);
        ftFace = FT_Face.create(face);
    }

    public Font(final ByteBuffer bytes) {
        this.buffer = bytes;
        long library = Graphics.getFreeType();
        PointerBuffer facePointerBuffer = BufferUtils.createPointerBuffer(1);
        FreeType.FT_New_Memory_Face(library, bytes, 0, facePointerBuffer); // each ttf file may have multiple indices / multiple faces. Guarantees to have 0
        long face = facePointerBuffer.get(0);
        ftFace = FT_Face.create(face);
    }

    Glyph getGlyph(final char c, int size, boolean antialiasing) {
        Tuple3<Character, Integer, Boolean> props = new Tuple3<>(c,size,antialiasing);
        Glyph glyph = cache.get(props);
        if (glyph != null) return glyph;
        int pageSize = Math.min(2048, MathUtils.nextPowerOf2i(size * 5));
        GlyphNotebook notebook = glyphsNotebooks.computeIfAbsent(size, k -> new GlyphNotebook(pageSize)); // get notebook for given size
        glyph = notebook.draw(c, size, antialiasing);
        cache.put(props, glyph);
        return glyph;
    }

    @Override
    public void delete() {
        FreeType.FT_Done_Face(ftFace);
        for (Map.Entry<Integer, GlyphNotebook> entry : glyphsNotebooks.entrySet()) {
            GlyphNotebook value = entry.getValue();
            for (Texture page : value.pages) {
                page.delete();
            }
        }
    }

    private final class GlyphNotebook {

        private static final int PADDING = 5;

        private int penX = PADDING;
        private int penY = PADDING;
        private int span = 0; // The vertical span of the current written line

        public  final Array<Texture> pages = new Array<>(true,1);
        private final int            pageSize;

        private GlyphNotebook(int pageSize) {
            this.pageSize = pageSize;
        }

        // TODO: add SDF using
        // TODO: https://stackoverflow.com/questions/71185718/how-to-use-ft-render-mode-sdf-in-freetype
        // TODO: FT_RENDER_MODE_SDF
        private Glyph draw(char c, int size, boolean antialiasing) {
            /* if size is use for the first time, create the first texture */
            if (pages.size == 0) {
                ByteBuffer bufferEmpty = ByteBuffer.allocateDirect(pageSize * pageSize * 4);
                Texture page = new Texture(pageSize, pageSize, bufferEmpty,
                        Texture.FilterMag.NEAREST, Texture.FilterMin.NEAREST,
                        Texture.Wrap.CLAMP_TO_EDGE, Texture.Wrap.CLAMP_TO_EDGE,1,true);
                pages.add(page);
            }

            /* set the face size */
            //FreeType.FT_Set_Pixel_Sizes(ftFace, size, size);
            FreeType.FT_Set_Pixel_Sizes(ftFace, 0, size);
            //FreeType.nFT_Set_Char_Size(ftFace.address(), size, size, Graphics.getWindowWidth(), Graphics.getWindowHeight());

            /* rasterize the character */
            if (antialiasing) FreeType.FT_Load_Char(ftFace, c, FreeType.FT_LOAD_RENDER | FreeType.FT_LOAD_FORCE_AUTOHINT);
            else FreeType.FT_Load_Char(ftFace, c, FreeType.FT_LOAD_RENDER | FreeType.FT_LOAD_MONOCHROME);

            FT_GlyphSlot glyphSlot = ftFace.glyph();
            FT_Bitmap bitmap = glyphSlot.bitmap();
            int glyph_width  = bitmap.width();
            int glyph_height = bitmap.rows();
            int glyph_pitch  = bitmap.pitch();
            /* create the Glyph and get basic metrics (atlasX and atlasY will be set later */
            Glyph data = new Glyph();
            data.atlasX = -1; // we need to set this to wherever we draw the glyph x
            data.atlasY = -1; // we need to set this to wherever we draw the glyph y
            data.width = glyph_width;
            data.height = glyph_height;
            data.bearingX = glyphSlot.bitmap_left();
            data.bearingY = glyphSlot.bitmap_top();
            data.advanceX = glyphSlot.advance().x() >> 6; // FreeType gives the advance in x64 units, so we divide by 64.
            data.advanceY = glyphSlot.advance().y() >> 6; // FreeType gives the advance in x64 units, so we divide by 64.

            /* get the bitmap ByteBuffer and create our own RGBA byte buffer (for antialiased fonts) */
            ByteBuffer ftCharImageBuffer = bitmap.buffer(Math.abs(glyph_pitch) * glyph_height);
            ByteBuffer buffer = MemoryUtil.memAlloc(data.width * data.height * 4);

            if (antialiasing) { // antialiasing
                for (int i = 0; i < data.width * data.height; i++) {
                    byte value = ftCharImageBuffer.get(i);
                    buffer.put((byte) 255); // Red
                    buffer.put((byte) 255); // Green
                    buffer.put((byte) 255); // Blue
                    buffer.put(value); // Alpha
                }
            } else { // no antialiasing
                for (int y = 0; y < data.height; y++) {
                    for (int x = 0; x < data.width; x++) {
                        // Calculate the byte and bit positions in the monochrome buffer
                        int byteIndex = y * Math.abs(glyph_pitch) + (x / 8);
                        int bitIndex = 7 - (x % 8); // Bits are stored high-to-low in each byte
                        boolean isOn = (ftCharImageBuffer.get(byteIndex) & (1 << bitIndex)) != 0;

                        // RGBA for the current pixel
                        byte r = (byte) (isOn ? 255 : 0); // Red channel
                        byte g = (byte) (isOn ? 255 : 0); // Green channel
                        byte b = (byte) (isOn ? 255 : 0); // Blue channel
                        byte a = (byte) (isOn ? 255 : 0); // Alpha channel (255 for opaque, 0 for transparent)

                        // Write RGBA to the buffer
                        buffer.put(r); // Red
                        buffer.put(g); // Green
                        buffer.put(b); // Blue
                        buffer.put(a); // Alpha
                    }
                }
            }
            buffer.flip();
            span = Math.max(span, data.height);

            if (penX + PADDING + data.width < pageSize && penY + PADDING + data.height < pageSize) {
                TextureBinder.bind(pages.last());
                GL11.glTexSubImage2D(
                        GL11.GL_TEXTURE_2D,
                        0,
                        penX,
                        penY,
                        data.width,
                        data.height,
                        GL11.GL_RGBA,
                        GL11.GL_UNSIGNED_BYTE,
                        buffer          // Data
                );
                data.atlasX = penX;
                data.atlasY = penY;
                data.texture = pages.last();

                penX += PADDING + data.width;
                return data;
            }

            if (penX + PADDING + data.width >= pageSize && penY + span + PADDING + data.height < pageSize) {
                penX = PADDING;
                penY += span + PADDING;

                TextureBinder.bind(pages.last());
                GL11.glTexSubImage2D(
                        GL11.GL_TEXTURE_2D,
                        0,
                        penX,
                        penY,
                        data.width,
                        data.height,
                        GL11.GL_RGBA,
                        GL11.GL_UNSIGNED_BYTE,
                        buffer          // Data
                );
                data.atlasX = penX;
                data.atlasY = penY;
                data.texture = pages.last();

                penX += PADDING + data.width;
                return data;
            }

            // flip page
            ByteBuffer bufferEmpty = ByteBuffer.allocateDirect(pageSize * pageSize * 4);
            Texture page = new Texture(pageSize, pageSize, bufferEmpty,
                    Texture.FilterMag.NEAREST, Texture.FilterMin.NEAREST,
                    Texture.Wrap.CLAMP_TO_EDGE, Texture.Wrap.CLAMP_TO_EDGE, 1, true);
            penX = PADDING;
            penY = PADDING;
            pages.add(page);

            TextureBinder.bind(pages.last());
            GL11.glTexSubImage2D(
                    GL11.GL_TEXTURE_2D,
                    0,
                    penX,
                    penY,
                    data.width,
                    data.height,
                    GL11.GL_RGBA,
                    GL11.GL_UNSIGNED_BYTE,
                    buffer          // Data
            );
            data.atlasX = penX;
            data.atlasY = penY;
            data.texture = pages.last();

            penX += PADDING + data.width;
            return data;
        }

    }

    static final class Glyph {

        public Texture texture;
        public int     width;
        public int     height;
        public float   bearingX; // TODO: change to int
        public float   bearingY; // TODO: change to int
        public float   advanceX; // TODO: change to int
        public float   advanceY; // TODO: change to int
        public int     atlasX;
        public int     atlasY;

    }

}

/*
HarfBuzz non-working example:

private static void harfbuzz() {

        try {
            // Step 1: Initialize FreeType
            PointerBuffer libPointerBuffer = BufferUtils.createPointerBuffer(1);
            FreeType.FT_Init_FreeType(libPointerBuffer);
            long library = libPointerBuffer.get(0);

            // Load the font file
            String fontPath = "C:\\Windows\\Fonts\\ahronbd.ttf"; // Update to your font file
            PointerBuffer facePointerBuffer = BufferUtils.createPointerBuffer(1);
            System.out.println("hi1");
            ByteBuffer fontDataBuffer = Assets.fileToByteBuffer(fontPath);
            if (FT_New_Memory_Face(library, fontDataBuffer, 0, facePointerBuffer) != 0) {
                throw new RuntimeException("Failed to load font");
            }
            long face = facePointerBuffer.get(0);
            FT_Face ftFace = FT_Face.create(face);
            System.out.println("hi2");

            // Set font size
            FT_Set_Char_Size(ftFace, 0, 16 * 64, 96, 96);
            System.out.println("hi");
            // Step 2: Initialize HarfBuzz

            System.out.println("bye");


            long hbBuffer = hb_buffer_create(); // Create HarfBuzz buffer
            long blob = hb_blob_create_from_file("C:\\Windows\\Fonts\\ahronbd.ttf"); /* or hb_blob_create_from_file_or_fail()
            long hb_face = hb_face_create(blob, 0);
            long hb_font = hb_font_create(hb_face);

            // Step 3: Add text to buffer
            String text = "bbb bbb"; // Hebrew text
            hb_buffer_add_utf8(hbBuffer, text, 0, text.length());
            hb_buffer_set_direction(hbBuffer, HB_DIRECTION_RTL); // Set RTL direction
            hb_buffer_set_script(hbBuffer, HB_SCRIPT_HEBREW);    // Set script
            hb_buffer_set_language(hbBuffer, hb_language_from_string("he")); // Hebrew language

            // Step 4: Shape the text
            hb_shape(hb_font, hbBuffer, null);

            // Step 5: Retrieve shaped glyphs
            int glyphCount = hb_buffer_get_length(hbBuffer);
            hb_glyph_info_t.Buffer glyphInfos = hb_buffer_get_glyph_infos(hbBuffer);
            hb_glyph_position_t.Buffer glyphPositions = hb_buffer_get_glyph_positions(hbBuffer);

                        System.out.println("Shaped Glyphs:");
                        for (int i = 0; i < glyphCount; i++) {
            hb_glyph_info_t glyphInfo = glyphInfos.get(i);
            hb_glyph_position_t glyphPos = glyphPositions.get(i);

                            System.out.printf("Glyph %d: ID=%d, xAdvance=%d, xOffset=%d%n",
                                              i,
                                              glyphInfo.codepoint(),
                                    glyphPos.x_advance() / 64, // Divide to convert from 26.6 fixed-point
                    glyphPos.x_offset() / 64);
                    }

            // Cleanup
            hb_buffer_destroy(hbBuffer);
            hb_font_destroy(hb_font);
            FT_Done_Face(ftFace);
            FT_Done_FreeType(library);
                    } catch (Exception e) {
                    e.printStackTrace();
                    }
                            }


 */

/*
simpler HB example - does not work on Hebrew or Russian, for example.

private void harfbuzz_example() {

long buf = hb_buffer_create();
        hb_buffer_add_utf8(buf, "aloha", 0, "Hello".length());

// If you know the direction, script, and language
//        hb_buffer_set_direction(buf, HB_DIRECTION_LTR);
//        hb_buffer_set_script(buf, HB_SCRIPT_LATIN);
//        hb_buffer_set_language(buf, hb_language_from_string("en"));

// If you don't know the direction, script, and language
hb_buffer_guess_segment_properties(buf);

        long blob = hb_blob_create_from_file("C:\\Windows\\Fonts\\ahronbd.ttf");
long face = hb_face_create(blob, 0);
long font = hb_font_create(face);

hb_shape(font, buf, null);

var glyph_infos = hb_buffer_get_glyph_infos(buf);
var glyph_poses = hb_buffer_get_glyph_positions(buf);

        while (glyph_infos.remaining() > 0) {
var info = glyph_infos.get();
            System.out.println("codepoint: " + info.codepoint());
        }

        while (glyph_poses.remaining() > 0) {
var pos = glyph_poses.get();
            System.out.println("codepoint: " + pos.x_advance() / 64);
        }

// tidy up
hb_buffer_destroy(buf);
hb_font_destroy(font);
hb_face_destroy(face);
hb_blob_destroy(blob);

}

 */