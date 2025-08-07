package com.heavybox.jtix.tools;

import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public final class ToolsTextureManipulator {

    private ToolsTextureManipulator() {}

    public static BufferedImage[][] slice(final String source, int rows, int cols) {
        BufferedImage image;
        try {
            image = ImageIO.read(new File(source));
        } catch (IOException e) {
            throw new ToolsException("Path to image file is wrong: " + source);
        }
        return slice(image, rows, cols);
    }

    public static BufferedImage[][] slice(@NotNull BufferedImage source, int rows, int cols) {
        if (rows < 1 || cols < 1) throw new ToolsException("rows and cols should be at least 1.");

        int tileWidth = source.getWidth() / cols;
        int tileHeight = source.getHeight() / rows;
        BufferedImage[][] slices = new BufferedImage[rows][cols];

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                slices[row][col] = source.getSubimage(
                        col * tileWidth,
                        row * tileHeight,
                        tileWidth,
                        tileHeight
                );
            }
        }

        return slices;
    }

    public static void save(final BufferedImage[] images, final String path) {
        try {
            for (int i = 0; i < images.length; i++) {
                ImageIO.write(images[i], "jpg", new File(path + "[" + i + "]"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void save(final BufferedImage[][] images, final String path) {
        try {
            for (int i = 0; i < images.length; i++) {
                for (int j = 0; j < images[0].length; j++) {
                    ImageIO.write(images[i][j], "jpg", new File(path + "[" + i + "][" + j + "]"));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void save(final BufferedImage image, final String path) {
        try {
            ImageIO.write(image, "jpg", new File(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
