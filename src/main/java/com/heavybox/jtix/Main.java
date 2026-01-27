package com.heavybox.jtix;

import com.heavybox.jtix.application.Application;
import com.heavybox.jtix.application.ApplicationSettings;
import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.collections.Tuple2;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {

    public static void main(String[] args) throws IOException {

//        rename();
//        if (true) return;

        ApplicationSettings settings = new ApplicationSettings();
        //settings.resizable = false;
        Application.init(settings);
        Application.launch(new SceneDemo());
        //Application.launch(new SceneTestUI2());
        //Application.launch(new SceneTestInput_1());
        //Application.launch(new SceneWidgetsInput());
        //Application.launch(new SceneNewContainers());
//        Application.launch(new SceneFontRendering());
        //Application.launch(new SceneWidgetsThemes());
    }

    private static String from_1 = "castle_bridge_horizontal_orc_";
    private static String to_1 = "architecture_orc_bridge_";

    private static String from_2 = "castle_building_diagonal_short_left_orc_";
    private static String to_2 = "architecture_orc_house_diagonal_short_";

    private static String from_3 = "castle_building_diagonal_tall_left_orc_";
    private static String to_3 = "architecture_orc_house_diagonal_tall_";

    private static String from_4 = "castle_building_narrow_short_orc_";
    private static String to_4 = "architecture_orc_house_vertical_short_";

    private static String from_5 = "castle_building_narrow_tall_orc_";
    private static String to_5 = "architecture_orc_house_vertical_tall_";

    private static String from_6 = "castle_building_wide_short_orc_";
    private static String to_6 = "architecture_orc_house_horizontal_short_";

    private static String from_7 = "castle_building_wide_tall_orc_";
    private static String to_7 = "architecture_orc_house_horizontal_tall_";

    private static String from_8 = "castle_cube_left_orc_";
    private static String to_8 = "architecture_orc_hut_diagonal_";

    private static String from_9 = "castle_cube_up_orc_";
    private static String to_9 = "architecture_orc_hut_vertical_";

    private static String from_10 = "castle_tower_short_orc_";
    private static String to_10 = "architecture_orc_tower_short_";

    private static String from_11 = "castle_tower_tall_orc_";
    private static String to_11 = "architecture_orc_tower_tall_";

    private static String from_12 = "castle_wall_back_left_orc_";
    private static String to_12 = "architecture_orc_wall_back_";

    private static String from_13 = "castle_wall_front_left_orc_";
    private static String to_13 = "architecture_orc_wall_front_";

    private static void rename() throws RuntimeException {
        Path dir = Paths.get("C:\\Users\\eilan\\OneDrive\\Desktop\\Heavy Box Games\\projects\\RPG Map Master\\art\\architecture name change - orc");

        Array<Tuple2<String, String>> fromToPairs = new Array<>();
        fromToPairs.add(new Tuple2<>(from_1, to_1));
        fromToPairs.add(new Tuple2<>(from_2, to_2));
        fromToPairs.add(new Tuple2<>(from_3, to_3));
        fromToPairs.add(new Tuple2<>(from_4, to_4));
        fromToPairs.add(new Tuple2<>(from_5, to_5));
        fromToPairs.add(new Tuple2<>(from_6, to_6));
        fromToPairs.add(new Tuple2<>(from_7, to_7));
        fromToPairs.add(new Tuple2<>(from_8, to_8));
        fromToPairs.add(new Tuple2<>(from_9, to_9));
        fromToPairs.add(new Tuple2<>(from_10, to_10));
        fromToPairs.add(new Tuple2<>(from_11, to_11));
        fromToPairs.add(new Tuple2<>(from_12, to_12));
        fromToPairs.add(new Tuple2<>(from_13, to_13));

        for (Tuple2<String, String> fromToPair : fromToPairs) {

            try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, fromToPair.t1 + "*.png")) {

                for (Path oldPath : stream) {
                    String oldName = oldPath.getFileName().toString();
                    String newName = oldName.replace(fromToPair.t1, fromToPair.t2);
                    Path newPath = oldPath.resolveSibling(newName);

                    Files.move(oldPath, newPath);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

}