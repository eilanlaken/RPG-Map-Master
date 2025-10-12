package com.heavybox.jtix.z;

public class CommandTerrainChangeEnvironment extends Command {

    public final Type type;

    public CommandTerrainChangeEnvironment(Type type) {
        super(0,0,0,0,1,1,true);
        this.type = type;
    }

    public enum Type {
        SELECT_NEXT_GROUND,
        SELECT_NEXT_LIQUID,
        ;
    }

}
