package com.heavybox.jtix.z;

public class CommandTerrainWheatFieldCreate extends Command {

    public float[] polygon;
    public int baseType;
    public float linesAngle;

    public CommandTerrainWheatFieldCreate() {
        super(0, 0, 0, 0, 1, 1, true);
    }

}
