package com.heavybox.jtix.input;

import com.heavybox.jtix.collections.Array;

// TODO
public class InputHandlerChain implements InputHandler {

    private Array<InputHandler> inputHandlers = new Array<>(true, 2);

    public void add(final InputHandler handler) {
        inputHandlers.add(handler);
    }

    public void clear() {
        inputHandlers.clear();
    }

}
