package com.heavybox.jtix;

import com.heavybox.jtix.application.Application;
import com.heavybox.jtix.application.ApplicationSettings;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        ApplicationSettings settings = new ApplicationSettings();
        Application.init(settings);
        Application.launch(new SceneDemo());
    }



}