package com.heavybox.jtix;

import com.heavybox.jtix.application.Application;
import com.heavybox.jtix.application.ApplicationSettings;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {




//        try {
//            //ToolsTextureGenerator.generateTextureMapNormal("assets/textures", "stoneN", "assets/textures/stones512.jpg", 0.5f,true);
//        } catch (Exception e) {
//
//        }

        /* texture generator tests */
//        try {
//            TextureGenerator.generateTextureNoisePerlin(128, 128, "assets/textures", "hi", false);
//        } catch (Exception e) {
//            throw e;
//        }


        //TextureBuilder.buildTextureFont("assets/fonts", "bitmap", "assets/fonts/OpenSans-Italic.ttf", 32, false);
//        ToolsFontGenerator.generateFontBitmap("assets/fonts/OpenSans-Regular.ttf", 25, true, "ABCD");
//        if (true) return;
        //ToolsFontGenerator.generateFontBitmap("assets/fonts/OpenSans-Regular.ttf", 24, true, "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~");
//        AsyncTask italic = new AsyncTask() {
//            @Override
//            public void task() {
//                ToolsFontGenerator.generateFontBitmap("assets/fonts/OpenSans-Italic.ttf", 32, true, null);
//            }
//        };


//        AsyncTask regular = new AsyncTask() {
//            @Override
//            public void task() {
//                FontGenerator.generateBitmapFont("assets/fonts/OpenSans-Regular.ttf", 64, false, null);
//            }
//        };

        //AsyncTaskRunner.await(AsyncTaskRunner.async(italic));


//        try {
//            //TextureGenerator.generateTexturePack("assets/atlases", "spots", 2,2, TextureGenerator.TexturePackSize.SMALL_512,"assets/textures/red30x30.png", "assets/textures/green25x25.png", "assets/textures/blue100x100.png");
//        } catch (Exception e) {
//            return;
//        }

        //if (true) return;

//        ApplicationWindowAttributes config = new ApplicationWindowAttributes();
//        Application.create();
//        Application.launch(new ScreenLoading());

//        ToolCloudAtlasGenerator.run();

//        BufferedImage[][] tiles = ToolsTextureManipulator.slice("assets/game-maps/map_volcano.jpg", 12, 12);
//        ToolsTextureManipulator.save(tiles[3][8], "assets/game-maps/tile[3][8].jpg");
//        ToolsTextureManipulator.save(tiles[3][9], "assets/game-maps/tile[3][9].jpg");
//        ToolsTextureManipulator.save(tiles[4][8], "assets/game-maps/tile[4][8].jpg");
//        ToolsTextureManipulator.save(tiles[4][9], "assets/game-maps/tile[4][9].jpg");
//        if (true) return;

        //ToolsTextureGenerator.generateTextureNoiseSimplex(512, 512, "assets/generated", "perlin", true);
        //if (true) return;;

        ApplicationSettings settings = new ApplicationSettings();
        Application.init(settings); // can init with options.
        //Application.launch(new ScenePlanesGame_Terrain_New_15());
        //Application.launch(new SceneRendering3D_Primitives());
        //Application.launch(new SceneRendering3D_ModelsViewer_Map_1());
        //Application.launch(new ScenePlanesGame_Terrain_New_4());
        //Application.launch(new SceneRendering3D_VFX_1());
        //Application.launch(new SceneRendering3D_NewClouds());
        //Application.launch(new ScenePlanesGame_Mountains_4());
        Application.launch(new ScenePhysics2D());


    }



}