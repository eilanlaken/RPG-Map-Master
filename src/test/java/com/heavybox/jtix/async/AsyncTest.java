package com.heavybox.jtix.async;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class AsyncTest {

    @Test
    void sync() {
    }

    @Test
    void getAvailableProcessors() {
        Assertions.assertTrue(Async.getAvailableProcessorsNumber() > 0);
    }
}