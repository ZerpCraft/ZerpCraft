package com.ezpzxrp.zerpcraft.LandAdmin;

public class WorldUtils {

    public static int[] getYAxisByServerVersion(String version) {

        int[] heights = new int[2];
        if (version.contains("1.18")) {

            heights[0] = 320;
            heights[1] = -64;
        }
        else  {

            heights[0] = 255;
            heights[1] = -64;
        }

        return heights;
    }
}
