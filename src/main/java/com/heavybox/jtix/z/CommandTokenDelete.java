package com.heavybox.jtix.z;

public class CommandTokenDelete extends Command {

    public final Enum<?> type;

    public CommandTokenDelete(final Enum<?> type, int layer, float x, float y, boolean anchor) {
        super(layer, x, y, 0, 1, 1, anchor);
        this.type = type;
    }

}
