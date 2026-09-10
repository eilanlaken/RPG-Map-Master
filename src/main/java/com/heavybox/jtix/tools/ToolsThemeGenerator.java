package com.heavybox.jtix.tools;

import com.heavybox.jtix.assets.Assets;
import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.GraphicsException;
import com.heavybox.jtix.graphics.TextureRegion;

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
    public static String groupTextureBackgroundPath     = null;
    public static Color  groupColorBackground           = Color.WHITE.clone();
    public static float  groupPaddingTop                = 10;
    public static float  groupPaddingBottom             = 10;
    public static float  groupPaddingLeft               = 10;
    public static float  groupPaddingRight              = 10;
    public static float  groupChildSpacingVertical      = 20;
    public static float  groupChildSpacingHorizontal    = 20;
    public static float  groupCornerRadiusTopLeft       = 0;
    public static float  groupCornerRadiusTopRight      = 0;
    public static float  groupCornerRadiusBottomRight   = 0;
    public static float  groupCornerRadiusBottomLeft    = 0;
    public static int    groupCornerSegmentsTopLeft     = 10;
    public static int    groupCornerSegmentsTopRight    = 10;
    public static int    groupCornerSegmentsBottomRight = 10;
    public static int    groupCornerSegmentsBottomLeft  = 10;
    public static float  groupSizeBorder                = 0;
    public static Color  groupColorBorder               = Color.valueOf("#2B2B2B");

    /* checkbox */
    public static String checkboxImageCheckedPath         = null;
    public static String checkboxImageUncheckedPath       = null;
    public static Color  checkboxColorBorderChecked       = Color.valueOf("0075FF");
    public static Color  checkboxColorBorderUnchecked     = Color.valueOf("767676");
    public static Color  checkboxColorCheckmarkBackground = Color.valueOf("0075FF");
    public static Color  checkboxColorCheckmark           = Color.valueOf("FFFFFF");
    public static float  checkboxSize                     = 27;
    public static float  checkboxSizeBorder               = 5;

    /* option */
    public static String optionImageOnPath  = null;
    public static String optionImageOffPath = null;
    public static Color  optionColorBorder  = Color.valueOf("0075FF");
    public static Color  optionColorFill    = Color.valueOf("0075FF");
    public static float  optionSize         = 22;

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
          option:
            optionImageOnPath:                  %s
            optionImageOffPath:                 %s
            optionColorBorder:                  %s
            optionColorFill:                    %s
            optionSize:                         %s
          scrollbar:
            scrollbarImageBarPath:              %s
            scrollbarImageThumbPath:            %s
            scrollbarColorBar:                  %s
            scrollbarColorThumb:                %s
            scrollbarThickness:                 %s
          group:
            groupTextureBackgroundPath:         %s
            groupColorBackground:               %s
            groupPaddingTop:                    %s
            groupPaddingBottom:                 %s
            groupPaddingLeft:                   %s
            groupPaddingRight:                  %s
            groupChildSpacingVertical:          %s
            groupChildSpacingHorizontal:        %s
            groupCornerRadiusTopLeft:           %s
            groupCornerRadiusTopRight:          %s
            groupCornerRadiusBottomRight:       %s
            groupCornerRadiusBottomLeft:        %s
            groupCornerSegmentsTopLeft:         %s
            groupCornerSegmentsTopRight:        %s
            groupCornerSegmentsBottomRight:     %s
            groupCornerSegmentsBottomLeft:      %s
            groupSizeBorder:                    %s
            groupColorBorder:                   %s
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
                // option
                optionImageOnPath,
                optionImageOffPath,
                colorToYaml(optionColorBorder),
                colorToYaml(optionColorFill),
                optionSize,
                // scrollbar
                scrollbarImageBarPath,
                scrollbarImageThumbPath,
                colorToYaml(scrollbarColorBar),
                colorToYaml(scrollbarColorThumb),
                scrollbarThickness,
                // group
                groupTextureBackgroundPath,
                colorToYaml(groupColorBackground),
                groupPaddingTop,
                groupPaddingBottom,
                groupPaddingLeft,
                groupPaddingRight,
                groupChildSpacingVertical,
                groupChildSpacingHorizontal,
                groupCornerRadiusTopLeft,
                groupCornerRadiusTopRight,
                groupCornerRadiusBottomRight,
                groupCornerRadiusBottomLeft,
                groupCornerSegmentsTopLeft,
                groupCornerSegmentsTopRight,
                groupCornerSegmentsBottomRight,
                groupCornerSegmentsBottomLeft,
                groupSizeBorder,
                colorToYaml(groupColorBorder),
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
        // option
        imagePaths.add(optionImageOnPath);
        imagePaths.add(optionImageOffPath);
        imagePaths.removeIf(Objects::isNull);
        return imagePaths;
    }

}
