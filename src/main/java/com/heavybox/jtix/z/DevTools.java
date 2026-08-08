package com.heavybox.jtix.z;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class DevTools {

    public static void run() {
        try {
            rename_files_remove_isometric_view();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void rename_files_remove_isometric_view() throws IOException {
        Path directory = Paths.get("assets/textures-layer-3/");

        try (DirectoryStream<Path> files = Files.newDirectoryStream(directory)) {
            for (Path file : files) {
                if (!Files.isRegularFile(file)) {
                    continue;
                }

                String fileName = file.getFileName().toString();

                if (!fileName.contains("isometric_view")) {
                    continue;
                }

                String newFileName = fileName.replace("_isometric_view", "");
                Path newFile = file.resolveSibling(newFileName);

                Files.move(file, newFile);
            }
        }
    }

    public static void renameFiles_add_change_prefix_farmland_props() throws IOException {
        Path dir = Paths.get("C:\\Users\\eilan\\OneDrive\\Desktop\\Heavy Box Games\\projects\\RPG Map Master\\art\\backup\\farmland props");
        try (Stream<Path> paths = Files.list(dir)) {
            paths
                    .filter(Files::isRegularFile)
                    .forEach(path -> {
                        String name = path.getFileName().toString();
                        if (true) {
                            String newName = name.replace("farmland_prop", "prop_farmland");
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

    public static void renameFiles_add_prefix_geology() throws IOException {
        Path dir = Paths.get("C:\\Users\\eilan\\OneDrive\\Desktop\\Heavy Box Games\\projects\\RPG Map Master\\art\\backup\\geology");
        try (Stream<Path> paths = Files.list(dir)) {
            paths
                    .filter(Files::isRegularFile)
                    .forEach(path -> {
                        String name = path.getFileName().toString();
                        String prefix = name.split("_")[0];
                        if (true) {
                            String newName = name.replace(prefix, "geology_" + prefix + "_");
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

    public static void renameFiles_add_prefix_nature_to_trees() throws IOException {
        Path dir = Paths.get("C:\\Users\\eilan\\OneDrive\\Desktop\\Heavy Box Games\\projects\\RPG Map Master\\art\\backup\\trees");
        try (Stream<Path> paths = Files.list(dir)) {
            paths
                    .filter(Files::isRegularFile)
                    .forEach(path -> {
                        String name = path.getFileName().toString();
                        if (name.startsWith("tree_")) {
                            String newName = name.replace("tree_", "nature_tree_");
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

    public static void renameFiles_add_prefix_nature_to_flowers() throws IOException {
        Path dir = Paths.get("C:\\Users\\eilan\\OneDrive\\Desktop\\Heavy Box Games\\projects\\RPG Map Master\\art\\backup\\trees");
        try (Stream<Path> paths = Files.list(dir)) {
            paths
                    .filter(Files::isRegularFile)
                    .forEach(path -> {
                        String name = path.getFileName().toString();
                        if (name.startsWith("plant_")) {
                            String newName = name.replace("plant_", "nature_");
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
