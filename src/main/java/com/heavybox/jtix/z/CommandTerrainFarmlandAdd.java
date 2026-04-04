package com.heavybox.jtix.z;

@Deprecated
public class CommandTerrainFarmlandAdd extends Command {

    public float[] polygon;
    public int baseType;
    public float linesAngle;

    public CommandTerrainFarmlandAdd() {
        super(0, 0, 0, 0, 1, 1, true);
    }

}
