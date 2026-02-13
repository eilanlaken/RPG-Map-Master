package com.heavybox.jtix.z;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class DevTools {

    public static void run() {
        try {
            renameFiles_add_isometric();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void renameFiles_castle_to_side_view() throws IOException {
        Path dir = Paths.get("C:\\Users\\eilan\\OneDrive\\Desktop\\Heavy Box Games\\projects\\RPG Map Master\\art\\backup\\architecture 2");
        try (Stream<Path> paths = Files.list(dir)) {
            paths
                .filter(Files::isRegularFile)
                .forEach(path -> {
                    String name = path.getFileName().toString();
                    if (name.contains("_castle_")) {
                        String newName = name.replace("_castle_", "_side_view_");
                        Path target = path.resolveSibling(newName);
                        try {
                            Files.move(path, target);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
        }
    }

    public static void renameFiles_flat_to_top_view() throws IOException {
        Path dir = Paths.get("C:\\Users\\eilan\\OneDrive\\Desktop\\Heavy Box Games\\projects\\RPG Map Master\\art\\backup\\architecture 2");
        try (Stream<Path> paths = Files.list(dir)) {
            paths
                    .filter(Files::isRegularFile)
                    .forEach(path -> {
                        String name = path.getFileName().toString();
                        if (name.contains("_flat_")) {
                            String newName = name.replace("_flat_", "_top_view_");
                            Path target = path.resolveSibling(newName);
                            try {
                                Files.move(path, target);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    });
        }
    }

    public static void renameFiles_add_isometric() throws IOException {
        Path dir = Paths.get("C:\\Users\\eilan\\OneDrive\\Desktop\\Heavy Box Games\\projects\\RPG Map Master\\art\\backup\\architecture 3");
        try (Stream<Path> paths = Files.list(dir)) {
            paths
                    .filter(Files::isRegularFile)
                    .forEach(path -> {
                        String name = path.getFileName().toString();
                        if (name.contains("_wall_") && !name.contains("isometric")) {
                            String newName = name.replace("_wall_", "_isometric_view_wall_");
                            Path target = path.resolveSibling(newName);
                            try {
                                Files.move(path, target);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }

                    });
        }
    }

}
