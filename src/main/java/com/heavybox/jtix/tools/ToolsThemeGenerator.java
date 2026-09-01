package com.heavybox.jtix.tools;

import com.heavybox.jtix.assets.Assets;
import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.GraphicsException;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public final class ToolsThemeGenerator {

    /* text */
    public static Color   textColor        = Color.WHITE.clone();
    public static String  textFontPath     = null;
    public static int     textSize         = 22;
    public static boolean textAntialiasing = true;
    public static float   textLineSpacing  = 1.1f;

    /* slider */
    public static String sliderImageBackgroundPath = null;
    public static String sliderImageFillPath       = null;
    public static String sliderImageThumbPath      = null;
    public static float  sliderLength              = 200.0f;
    public static float  sliderThickness           = 8.0f;
    public static float  sliderThumbSize           = 18;
    public static Color  sliderColorBackground     = Color.GRAY.clone();
    public static Color  sliderColorFill           = Color.valueOf("0075FF");
    public static Color  sliderColorThumb          = Color.valueOf("0075FF");

    /* scrollbar */
    public static String scrollbarImageBarPath   = null;
    public static String scrollbarImageThumbPath = null;
    public static Color  scrollbarColorBar       = Color.valueOf("343538");
    public static Color  scrollbarColorThumb     = Color.valueOf("5c5d5e");
    public static float  scrollbarThickness      = 10;

    /* group */
    public static String groupTextureBackgroundPath = null;

    /* checkbox */
    public static String checkboxImageCheckedPath         = null;
    public static String checkboxImageUncheckedPath       = null;
    public static Color  checkboxColorBorderChecked       = Color.valueOf("0075FF");
    public static Color  checkboxColorBorderUnchecked     = Color.valueOf("767676");
    public static Color  checkboxColorCheckmarkBackground = Color.valueOf("0075FF");
    public static Color  checkboxColorCheckmark           = Color.valueOf("FFFFFF");
    public static float  checkboxSize                     = 27;
    public static float  checkboxSizeBorder               = 5;

    public static void generateTheme(final String outputDirectoryPath, final String outputFileName) {
        final Set<String> imagePaths = gatherAllImagePaths();
        boolean generatedTexturePack = false;
        String texturePackPath = null;
        try {
            if (!imagePaths.isEmpty()) {
                generatedTexturePack = true;
                String[] paths = imagePaths.toArray(new String[0]);
                texturePackPath = outputDirectoryPath + "/" + outputFileName + "-pack.yml";
                ToolsTexturePacker.packTextures(outputDirectoryPath, outputFileName + "-pack", 2, 2, ToolsTexturePacker.TexturePackSize.LARGE_2048, paths);
            }
        } catch (Exception e) {
            throw new ToolsException("Could not generate Theme: " + e.getMessage());
        }

        // export yaml
        String yaml = """
        theme:
          texturePackPath:                      %s
          scrollbar:
            scrollbarImageBarPath:              %s
            scrollbarImageThumbPath:            %s
            scrollbarColorBar:                  %s
            scrollbarColorThumb:                %s
            scrollbarThickness:                 %s
          group:
            groupTextureBackgroundPath:         %s
          text:
            textColor:                          %s
            textFontPath:                       %s
            textSize:                           %s
            textAntialiasing:                   %s
            textLineSpacing:                    %s
          slider:
            sliderImageBackgroundPath:          %s
            sliderImageFillPath:                %s
            sliderImageThumbPath:               %s
            sliderLength:                       %s
            sliderThickness:                    %s
            sliderThumbSize:                    %s
            sliderColorBackground:              %s
            sliderColorFill:                    %s
            sliderColorThumb:                   %s
          checkbox:
            checkboxImageCheckedPath:           %s
            checkboxImageUncheckedPath:         %s
            checkboxColorBorderChecked:         %s
            checkboxColorBorderUnchecked:       %s
            checkboxColorCheckmarkBackground:   %s
            checkboxColorCheckmark:             %s
            checkboxSize:                       %s
            checkboxSizeBorder:                 %s
        """.formatted(
                texturePackPath,
                // scrollbar
                scrollbarImageBarPath,
                scrollbarImageThumbPath,
                colorToYaml(scrollbarColorBar),
                colorToYaml(scrollbarColorThumb),
                scrollbarThickness,
                // group
                groupTextureBackgroundPath,
                // text
                textColor,
                textFontPath,
                textSize,
                textAntialiasing,
                textLineSpacing,
                // slider
                sliderImageBackgroundPath,
                sliderImageFillPath,
                sliderImageThumbPath,
                sliderLength,
                sliderThickness,
                sliderThumbSize,
                colorToYaml(sliderColorBackground),
                colorToYaml(sliderColorFill),
                colorToYaml(sliderColorThumb),
                // checkbox
                checkboxImageCheckedPath,
                checkboxImageUncheckedPath,
                colorToYaml(checkboxColorBorderChecked),
                colorToYaml(checkboxColorBorderUnchecked),
                colorToYaml(checkboxColorCheckmarkBackground),
                colorToYaml(checkboxColorCheckmark),
                checkboxSize,
                checkboxSizeBorder
        );

        try {
            Assets.saveFile(outputDirectoryPath, outputFileName + ".yml", yaml);
        } catch (Exception e) {
            throw new GraphicsException("Could not save texture pack data file. Exception: " + e.getMessage());
        }
    }

    private static String colorToYaml(final Color c) {
        if (c == null) return "null";
        return String.format("{ r: %.3f, g: %.3f, b: %.3f, a: %.3f }", c.r, c.g, c.b, c.a);
    }

    private static Set<String> gatherAllImagePaths() {
        final Set<String> imagePaths = new HashSet<>();
        // slider
        imagePaths.add(sliderImageBackgroundPath);
        imagePaths.add(sliderImageFillPath);
        imagePaths.add(sliderImageThumbPath);
        // checkbox
        imagePaths.add(checkboxImageCheckedPath);
        imagePaths.add(checkboxImageUncheckedPath);
        imagePaths.removeIf(Objects::isNull);
        return imagePaths;
    }

}
