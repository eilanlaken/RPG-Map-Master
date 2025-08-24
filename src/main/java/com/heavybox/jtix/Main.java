package com.heavybox.jtix;

import com.heavybox.jtix.application.Application;
import com.heavybox.jtix.application.ApplicationSettings;
import com.heavybox.jtix.tools.ToolsTexturePacker;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {

        ApplicationSettings settings = new ApplicationSettings();
        //settings.resizable = false;
        Application.init(settings);
        Application.launch(new SceneDemo_4());
    }



}