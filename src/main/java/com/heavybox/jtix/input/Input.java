package com.heavybox.jtix.input;

import com.heavybox.jtix.collections.Array;

import java.util.Comparator;

public final class Input {

    public static final Keyboard keyboard = new Keyboard();
    public static final Mouse    mouse    = new Mouse();
    public static final Webcam   webcam   = new Webcam();

    private static final Array<InputLayer> inputLayers = new Array<>(true, 2);

    private Input() {}

    public static void update() {
        // sort input layers every frame as the layers index may change.
        inputLayers.sort(Comparator.comparingInt(InputLayer::getLevel).reversed());

        keyboard.update();
        mouse.update();
    }

    public static void cleanup() {
        webcam.deleteAll();
        clearLayers();
    }

    public static void addLayer(final InputLayer inputLayer) {
        if (inputLayer == null) throw new InputException("inputLayer cannot be null.");
        inputLayers.add(inputLayer);
        inputLayers.sort(Comparator.comparingInt(InputLayer::getLevel).reversed());
    }

    public static void removeLayer(final InputLayer inputLayer) {
        inputLayers.removeValue(inputLayer, true);
        inputLayers.sort(Comparator.comparingInt(InputLayer::getLevel)); // order should be maintained but ok.
    }

    public static void clearLayers() {
        inputLayers.clear();
    }

}
