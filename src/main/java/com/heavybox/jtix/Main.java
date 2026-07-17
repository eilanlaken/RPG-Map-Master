package com.heavybox.jtix;

import com.heavybox.jtix.application.Application;
import com.heavybox.jtix.application.ApplicationSettings;
import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.collections.Tuple2;
import com.heavybox.jtix.math.MathUtils;
import com.heavybox.jtix.math.Vector2;
import com.heavybox.jtix.z.DevTests;
import com.heavybox.jtix.z.DevTools;
import com.heavybox.jtix.z.Utils;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class Main {

    public static void main(String[] args) throws IOException {

//        DevTests.run();
//        if (true) return;

        ApplicationSettings settings = new ApplicationSettings();
        //settings.resizable = false;
        Application.init(settings);
        //Application.launch(new SceneDemo());
        Application.launch(new SceneInput_3());

    }

}