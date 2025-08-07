package com.heavybox.jtix.tools;

import com.heavybox.jtix.assets.Assets;
import com.heavybox.jtix.collections.Collections;
import com.heavybox.jtix.math.MathUtils;
import com.heavybox.jtix.math.Vector3;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public final class ToolsTextureGenerator {

    // perlin noise static data
    private static final int[]     PERM_PERLIN_256     = new int[256];
    private static final int[]     PERM_PERLIN_512     = new int[512];
    private static final Vector3[] GRAD_3              = {new Vector3(1,1,0),new Vector3(-1,1,0),new Vector3(1,-1,0),new Vector3(-1,-1,0), new Vector3(1,0,1),new Vector3(-1,0,1),new Vector3(1,0,-1),new Vector3(-1,0,-1), new Vector3(0,1,1),new Vector3(0,-1,1),new Vector3(0,1,-1),new Vector3(0,-1,-1)};
    private static final short[]   P_SIMPLEX           = {151,160,137,91,90,15, 131,13,201,95,96,53,194,233,7,225,140,36,103,30,69,142,8,99,37,240,21,10,23, 190, 6,148,247,120,234,75,0,26,197,62,94,252,219,203,117,35,11,32,57,177,33, 88,237,149,56,87,174,20,125,136,171,168, 68,175,74,165,71,134,139,48,27,166, 77,146,158,231,83,111,229,122,60,211,133,230,220,105,92,41,55,46,245,40,244, 102,143,54, 65,25,63,161, 1,216,80,73,209,76,132,187,208, 89,18,169,200,196, 135,130,116,188,159,86,164,100,109,198,173,186, 3,64,52,217,226,250,124,123, 5,202,38,147,118,126,255,82,85,212,207,206,59,227,47,16,58,17,182,189,28,42, 223,183,170,213,119,248,152, 2,44,154,163, 70,221,153,101,155,167, 43,172,9, 129,22,39,253, 19,98,108,110,79,113,224,232,178,185, 112,104,218,246,97,228, 251,34,242,193,238,210,144,12,191,179,162,241, 81,51,145,235,249,14,239,107, 49,192,214, 31,181,199,106,157,184, 84,204,176,115,121,50,45,127, 4,150,254, 138,236,205,93,222,114,67,29,24,72,243,141,128,195,78,66,215,61,156,180};
    private static final short[]   SIMPLEX_PERM        = new short[512];
    private static final short[]   SIMPLEX_PERM_MOD_12 = new short[512];
    private static final float     F2                  = (float) (0.5f * (Math.sqrt(3.0) - 1.0f));
    private static final float     G2                  = (float) (3.0 - Math.sqrt(3.0)) / 6.0f;

    static {
        // perlin
        for (int i = 0; i < 256; i++) {
            PERM_PERLIN_256[i] = i;
        }
        Collections.shuffle(PERM_PERLIN_256);
        for (int i = 0; i < 256; i++) {
            PERM_PERLIN_512[i] = PERM_PERLIN_256[i];
            PERM_PERLIN_512[256 + i] = PERM_PERLIN_256[i];
        }
        // simplex
        for (int i = 0; i < 512; i++) {
            SIMPLEX_PERM[i]= P_SIMPLEX[i & 255];
            SIMPLEX_PERM_MOD_12[i] = (short)(SIMPLEX_PERM[i] % 12);
        }
    }



    private ToolsTextureGenerator() {}

    /* Noise */

    public static void generateTextureNoisePerlin(int width, int height, final String directory, final String outputName, boolean overrideExistingFile) throws IOException {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                float nx = x / (float) width;
                float ny = y / (float) height;
                float noise = noisePerlin(nx * 10, ny * 10, 0);
                int value = (int) ((noise + 1) * 128); // Convert [-1,1] to [0,255]
                int rgb = value | (value << 8) | (value << 16); // Gray color
                image.setRGB(x, y, rgb);
            }
        }

        Assets.saveImage(directory, outputName, image);
    }


    public static void generateTextureNoiseSimplex(int width, int height, final String directory, final String outputName, boolean overrideExistingFile) throws IOException {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        float scale = 0.1f;
        float persistence = 0.5f;
        int octaves = 1;
        int lacunarity = 2;
        float exponentiation = 1;
        float H = 1f;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                float nx = x / (float) width;
                float ny = y / (float) height;
                float noise = simplexGetValue(nx, ny, scale, persistence, octaves, lacunarity, exponentiation, H);
                int value = (int) ((noise + 1) * 128); // Convert [-1,1] to [0,255]
                int rgb = value | (value << 8) | (value << 16); // Gray color
                image.setRGB(x, y, rgb);
            }
        }

        Assets.saveImage(directory, outputName, image);
    }

    private static float simplexGetValue(float x, float y, float scale, float persistence, int octaves,
                     float lacunarity, float exponentiation, float height) {

        float xs = x / scale;
        float ys = y / scale;

        float G = (float) Math.pow(2.0f, -persistence);

        float amplitude = 1.0f;
        float frequency = 1.0f;
        float normalization = 0.0f;
        float total = 0.0f;

        for (int o = 0; o < octaves; o++) {
            float noiseValue = (float) (noiseSimplex(xs * frequency, ys * frequency) * 0.5 + 0.5);
            total += noiseValue * amplitude;
            normalization += amplitude;
            amplitude *= G;
            frequency *= lacunarity;
        }

        total /= normalization;
        return (float) Math.pow(total, exponentiation) * height;
    }

    public static void generateTextureNoiseWhite() {}
    public static void generateTextureNoiseValue() {}
    public static void generateTextureNoiseVornoi() {}

    /* Maps */
    // https://codepen.io/BJS3D/pen/YzjXZgV?editors=1010
    // https://github.com/Theverat/NormalmapGenerator/blob/master/src_generators/normalmapgenerator.cpp#L142

    public static void generateTextureMapNormal(final String directory, final String outputName, final String sourcePath, float strength, boolean tiled) {
        // TODO: check if already generated.

        /* get original image */
        BufferedImage imageInput = null;
        try {
            imageInput = ImageIO.read(new File(sourcePath));
        } catch (IOException e) {
            e.printStackTrace();
            throw new ToolsException("Could not generate normal map: creating " + BufferedImage.class.getSimpleName() + " for the original image failed. Check the path. \n" + e.getMessage());
        }

        /* generate intensity map */
        int width = imageInput.getWidth();
        int height = imageInput.getHeight();
        BufferedImage imageIntensityMap = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        for(int y = 0; y < width; y++) {
            for (int x = 0; x < height; x++) {
                int rgb = imageInput.getRGB(x, y);
                int red = (rgb >> 16) & 0xFF;
                int green = (rgb >> 8) & 0xFF;
                int blue = rgb & 0xFF;
                int average = (red + green + blue) / 3;
                int invertedAverage = 255 - average;
                int grayscale = (invertedAverage << 16) | (invertedAverage << 8) | invertedAverage; // R, G, B all set to the same average value
                imageIntensityMap.setRGB(x, y, grayscale);
            }
        }

        float strengthInv = 1.0f / strength;
        BufferedImage imageNormalMap = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for(int y = 0; y < width; y++) {
            for (int x = 0; x < height; x++) {

                final float topLeft      = readPixel(imageIntensityMap, x - 1, y - 1, tiled);
                final float top          = readPixel(imageIntensityMap, x - 1, y, tiled);
                final float topRight     = readPixel(imageIntensityMap, x - 1, y + 1, tiled);
                final float right        = readPixel(imageIntensityMap, x, y + 1, tiled);
                final float bottomRight  = readPixel(imageIntensityMap, x + 1, y + 1, tiled);
                final float bottom       = readPixel(imageIntensityMap, x + 1, y, tiled);
                final float bottomLeft   = readPixel(imageIntensityMap, x + 1, y - 1, tiled);
                final float left         = readPixel(imageIntensityMap, x, y - 1, tiled);

                final float[][] convolution_kernel = {
                    {topLeft,    top,    topRight},
                    {left,       0.0f,   right},
                    {bottomLeft, bottom, bottomRight}
                };

                Vector3 normal = sobel(convolution_kernel, strengthInv);
                // map normal -1..1 to rgb 0..255
                int mappedRed = (int) ((normal.x + 1.0f) * (255.0f / 2.0f));
                int mappedGreen = (int) ((normal.y + 1.0f) * (255.0f / 2.0f));
                int mappedBlue = (int) ((normal.z + 1.0f) * (255.0f / 2.0f));

                // Combine the mapped values into an RGB color
                int rgb = (mappedRed << 16) | (mappedGreen << 8) | mappedBlue;
                // Set the pixel in the normal map
                imageNormalMap.setRGB(x, y, rgb);
            }
        }

        try {
            Assets.saveImage(directory, outputName, imageNormalMap);
        } catch (IOException e) {
            // ignore
        }
    }

    public static void generateTextureMapRoughness() {}
    public static void generateTextureMapMetallic() {}
    public static void generateTextureMapSSAO() {}

    private static float fade(float t) {
        return t * t * t * (t * (t * 6 - 15) + 10); // t^3 * (6 * t^2 - 16 * t + 10)
    }

    private static float grad(int hash, float x, float y, float z) {
        int h = hash & 15;
        float u = h < 8 ? x : y;
        float v = h < 4 ? y : (h == 12 || h == 14 ? x : z);
        return ((h & 1) == 0 ? u : -u) + ((h & 2) == 0 ? v : -v);
    }

    private static float noisePerlin(float x, float y, float z) {
        int X = (int) Math.floor(x) & 255;
        int Y = (int) Math.floor(y) & 255;
        int Z = (int) Math.floor(z) & 255;

        x -= (float) Math.floor(x);
        y -= (float) Math.floor(y);
        z -= (float) Math.floor(z);

        float u = fade(x);
        float v = fade(y);
        float w = fade(z);

        int A = PERM_PERLIN_512[X] + Y, AA = PERM_PERLIN_512[A] + Z, AB = PERM_PERLIN_512[A + 1] + Z;
        int B = PERM_PERLIN_512[X + 1] + Y, BA = PERM_PERLIN_512[B] + Z, BB = PERM_PERLIN_512[B + 1] + Z;

        /* Sometimes it is beneficial to not ask questions. */
        return MathUtils.lerp(w, MathUtils.lerp(v, MathUtils.lerp(u, grad(PERM_PERLIN_512[AA], x, y, z),
                                grad(PERM_PERLIN_512[BA], x - 1, y, z)),
                        MathUtils.lerp(u, grad(PERM_PERLIN_512[AB], x, y - 1, z), grad(PERM_PERLIN_512[BB], x - 1, y - 1, z))),
                MathUtils.lerp(v, MathUtils.lerp(u, grad(PERM_PERLIN_512[AA + 1], x, y, z - 1), grad(PERM_PERLIN_512[BA + 1], x - 1, y, z - 1)), MathUtils.lerp(u, grad(PERM_PERLIN_512[AB + 1], x, y - 1, z - 1), grad(PERM_PERLIN_512[BB + 1], x - 1, y - 1, z - 1))));
    }

    private static float dot(Vector3 g, float x, float y) {
        return g.x * x + g.y * y;
    }

    /* Noise: simplex */
    public static float noiseSimplex(float xin, float yin) {
        float n0, n1, n2; // Noise contributions from the three corners
        // Skew the input space to determine which simplex cell we're in
        float s = (xin+yin)*F2; // Hairy factor for 2D
        int i = MathUtils.floor(xin + s);
        int j = MathUtils.floor(yin + s);
        float t = (i+j)*G2;
        float X0 = i-t; // Unskew the cell origin back to (x,y) space
        float Y0 = j-t;
        float x0 = xin-X0; // The x,y distances from the cell origin
        float y0 = yin-Y0;
        // For the 2D case, the simplex shape is an equilateral triangle.
        // Determine which simplex we are in.
        int i1, j1; // Offsets for second (middle) corner of simplex in (i,j) coords
        if (x0 > y0) { // lower triangle, XY order: (0,0)->(1,0)->(1,1)
            i1=1;
            j1=0;
        } else {
            i1=0;
            j1=1;
        }      // upper triangle, YX order: (0,0)->(0,1)->(1,1)
        // A step of (1,0) in (i,j) means a step of (1-c,-c) in (x,y), and
        // a step of (0,1) in (i,j) means a step of (-c,1-c) in (x,y), where
        // c = (3-sqrt(3))/6
        float x1 = x0 - i1 + G2; // Offsets for middle corner in (x,y) unskewed coords
        float y1 = y0 - j1 + G2;
        float x2 = x0 - 1.0f + 2.0f * G2; // Offsets for last corner in (x,y) unskewed coords
        float y2 = y0 - 1.0f + 2.0f * G2;
        // Work out the hashed gradient indices of the three simplex corners
        int ii = i & 255;
        int jj = j & 255;
        int gi0 = SIMPLEX_PERM_MOD_12[ii+ SIMPLEX_PERM[jj]];
        int gi1 = SIMPLEX_PERM_MOD_12[ii+i1+ SIMPLEX_PERM[jj+j1]];
        int gi2 = SIMPLEX_PERM_MOD_12[ii+1+ SIMPLEX_PERM[jj+1]];
        // Calculate the contribution from the three corners
        float t0 = 0.5f - x0*x0-y0*y0;
        if (t0 < 0) n0 = 0.0f;
        else {
            t0 *= t0;
            n0 = t0 * t0 * dot(GRAD_3[gi0], x0, y0);  // (x,y) of grad3 used for 2D gradient
        }
        float t1 = 0.5f - x1*x1-y1*y1;
        if(t1<0) n1 = 0.0f;
        else {
            t1 *= t1;
            n1 = t1 * t1 * dot(GRAD_3[gi1], x1, y1);
        }
        float t2 = 0.5f - x2*x2-y2*y2;
        if (t2 < 0) n2 = 0.0f;
        else {
            t2 *= t2;
            n2 = t2 * t2 * dot(GRAD_3[gi2], x2, y2);
        }
        // Add contributions from each corner to get the final noise value.
        // The result is scaled to return values in the interval [-1,1].
        return 70.0f * (n0 + n1 + n2);
    }

    // convolution_kernel is 3x3
    private static Vector3 sobel(final float[][] convolution_kernel, float strengthInv) {
        final float top_side    = convolution_kernel[0][0] + 2.0f * convolution_kernel[0][1] + convolution_kernel[0][2];
        final float bottom_side = convolution_kernel[2][0] + 2.0f * convolution_kernel[2][1] + convolution_kernel[2][2];
        final float right_side  = convolution_kernel[0][2] + 2.0f * convolution_kernel[1][2] + convolution_kernel[2][2];
        final float left_side   = convolution_kernel[0][0] + 2.0f * convolution_kernel[1][0] + convolution_kernel[2][0];

        final float dY = right_side - left_side;
        final float dX = bottom_side - top_side;
        final float dZ = strengthInv;

        return new Vector3(dX, dY, dZ).nor();
    }

    private static float readPixel(final BufferedImage img, int x, int y, boolean tiled) {
        int width = img.getWidth();
        int height = img.getHeight();

        if (x >= width) {
            x = tiled ? width - x : width - 1;
        } else if (x < 0) {
            x = tiled ? width + x : 0;
        }

        if (y >= height) {
            y = tiled ? height - y : height - 1;
        } else if (y < 0) {
            y = tiled ? height + y : 0;
        }

        return (img.getRGB(x, y) & 0xFF) / 255f;
    }

}
