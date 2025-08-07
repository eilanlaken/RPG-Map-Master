package com.heavybox.jtix;

import com.heavybox.jtix.application.Scene;
import com.heavybox.jtix.assets.Assets;
import com.heavybox.jtix.memory.MemoryResource;
import org.lwjgl.opengl.GL11;

import java.util.Map;

public class ScreenLoading implements Scene {
    @Override
    public void setup() {

    }

    @Override
    public void start() {

    }

    @Override
    public void update() {

    }

    @Override
    public void finish() {

    }

    //    private ApplicationScreen screen = null;
//
//    @Override
//    public void start() {
//        Map<String, Class<? extends MemoryResource>> requiredAssets = screen.getRequiredAssets();
//        for (Map.Entry<String, Class<? extends MemoryResource>> requiredAsset : requiredAssets.entrySet()) {
//            Assets.load(requiredAsset.getValue(), requiredAsset.getKey());
//        }
//    }
//
//    @Override
//    public void refresh() {
//        if (!AssetStore.isLoadingInProgress()) {
//            Application.switchScreen(screen);
//        }
//
//        // frame update
//        GL11.glEnable(GL11.GL_DEPTH_TEST);
//        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
//        GL11.glClearColor(0,0,0,1);
//    }
//
//    @Override
//    public void resize(int width, int height) {
//
//    }
//
//
//    @Override
//    public void hide() {
//
//    }
//
//    @Override
//    public void deleteAll() {
//
//    }

}
