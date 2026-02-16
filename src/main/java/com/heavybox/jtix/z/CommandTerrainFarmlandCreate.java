package com.heavybox.jtix.z;

public class CommandTerrainFarmlandCreate extends Command {

    public float[] polygon;
    public int baseType;
    public float linesAngle;

    public CommandTerrainFarmlandCreate() {
        super(0, 0, 0, 0, 1, 1, true);
    }

}
