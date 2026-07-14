package com.heavybox.jtix.input;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.math.MathUtils;
import org.junit.jupiter.api.Test;

import java.util.Comparator;

public class InputEventHandlerTest {

    @Test
    public void sort() {
        Array<InputEventHandler> layers = new Array<>(true, 1);
        for (int i = 0; i < 5; i++) {
            layers.add(new MyInputEventHandler());
        }

        layers.sort(Comparator.comparingInt(InputEventHandler::getInputLayer).reversed()); // printing confirms it is sorted in descending order.
    }

    private static class MyInputEventHandler implements InputEventHandler {

        private final int level = MathUtils.randomUniformInt(0,10);

        @Override
        public int getInputLayer() {
            return level;
        }

        @Override
        public boolean isActive() {
            return false;
        }

        @Override
        public String toString() {
            return "MyInputLayer{" +
                    "level=" + level +
                    '}';
        }
    }

}
