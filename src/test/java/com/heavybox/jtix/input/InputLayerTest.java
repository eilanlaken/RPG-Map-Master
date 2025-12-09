package com.heavybox.jtix.input;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.math.MathUtils;
import org.junit.jupiter.api.Test;

import java.util.Comparator;

public class InputLayerTest {

    @Test
    public void sort() {
        Array<InputLayer> layers = new Array<>(true, 1);
        for (int i = 0; i < 5; i++) {
            layers.add(new MyInputLayer());
        }

        layers.sort(Comparator.comparingInt(InputLayer::getLevel).reversed()); // printing confirms it is sorted in descending order.
    }

    private static class MyInputLayer implements InputLayer {

        private final int level = MathUtils.randomUniformInt(0,10);

        @Override
        public int getLevel() {
            return level;
        }

        @Override
        public String toString() {
            return "MyInputLayer{" +
                    "level=" + level +
                    '}';
        }
    }

}
