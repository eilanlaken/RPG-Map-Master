package com.heavybox.jtix.tools;

import com.heavybox.jtix.assets.Assets;
import com.heavybox.jtix.graphics.Color;
import com.heavybox.jtix.graphics.GraphicsException;

import java.util.HashSet;
import java.util.Set;

@Deprecated
public final class ToolsThemeGenerator {

    // a bunch of static variables containing defaults + paths.

    /* container */ // TODO: add nine 9 patch support


    /* checkbox */
    public static String themeCheckboxImageUnchecked           = null;
    public static String themeCheckboxImageChecked             = null;
    public static Color  themeCheckboxColorBorderUnchecked     = null;
    public static Color  themeCheckboxColorBorderChecked       = null;
    public static Color  themeCheckboxColorCheckmarkBackground = null;
    public static Color  themeCheckboxColorCheckmark           = null;

    private ToolsThemeGenerator() {}

    public static void generateTheme(String outputDirectory, String outputName) {
        final Set<String> imagePaths = gatherAllImagePaths();
        boolean generatedTexturePack = false;
        String texturePackPath = null;
        try {
            if (!imagePaths.isEmpty()) {
                generatedTexturePack = true;
                String[] paths = imagePaths.toArray(new String[0]);
                texturePackPath = outputDirectory + "/" + outputName + "-pack.yml";
                ToolsTexturePacker.packTextures(outputDirectory, outputName + "-pack", 2, 2, ToolsTexturePacker.TexturePackSize.LARGE_2048, paths);
            }
        } catch (Exception e) {
            // TODO: handle the exception properly
            System.out.println(e.getMessage());
        }

        // export yaml
        String yaml = """
        theme:
          texturePackPath:                       %s
          checkbox:
            themeCheckboxImageUnchecked:           %s
            themeCheckboxImageChecked:             %s
            themeCheckboxColorBorderUnchecked:     %s
            themeCheckboxColorBorderChecked:       %s
            themeCheckboxColorCheckmarkBackground: %s
            themeCheckboxColorCheckmark:           %s
        """.formatted(
            texturePackPath,
            // checkbox
            themeCheckboxImageUnchecked,
            themeCheckboxImageChecked,
            colorToYaml(themeCheckboxColorBorderUnchecked),
            colorToYaml(themeCheckboxColorBorderChecked),
            colorToYaml(themeCheckboxColorCheckmarkBackground),
            colorToYaml(themeCheckboxColorCheckmark)
        );

        try {
            Assets.saveFile(outputDirectory, outputName + ".yml", yaml);
        } catch (Exception e) {
            throw new GraphicsException("Could not save texture pack data file. Exception: " + e.getMessage());
        }
    }

    private static Set<String> gatherAllImagePaths() {
        final Set<String> imagePaths = new HashSet<>();
        if (themeCheckboxImageUnchecked != null) imagePaths.add(themeCheckboxImageUnchecked);
        if (themeCheckboxImageChecked != null) imagePaths.add(themeCheckboxImageChecked);
        return imagePaths;
    }

    private static String colorToYaml(final Color c) {
        if (c == null) return "null";

        return String.format("{ r: %.3f, g: %.3f, b: %.3f, a: %.3f }",
                c.r, c.g, c.b, c.a);
    }

}
