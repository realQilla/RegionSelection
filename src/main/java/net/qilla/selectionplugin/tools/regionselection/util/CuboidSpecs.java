package net.qilla.selectionplugin.tools.regionselection.util;

import org.joml.Vector3f;

public final class CuboidSpecs {

    private static final float edgeSize = 0.05f;

    public static Vector3f[] getPositions(int xDistance, int yDistance, int zDistance) {
        return new Vector3f[]{new Vector3f(0.05f, yDistance - 0.05f, 0), // Top Left Front
                new Vector3f(0, yDistance - 0.05f, 0.05f), // Top Right Front
                new Vector3f(xDistance - 0.05f, yDistance - 0.05f, 0.05f), // Top Left Back
                new Vector3f(0.05f, yDistance - 0.05f, zDistance - 0.05f), // Top Right Back

                new Vector3f(0.05f, 0, 0), // Bottom Left Front
                new Vector3f(0, 0, 0.05f), // Bottom Right Front
                new Vector3f(xDistance - 0.05f, 0, 0.05f), // Bottom Left Back
                new Vector3f(0.05f, 0, zDistance - 0.05f), // Bottom Right Back

                new Vector3f(xDistance - 0.05f, 0.05f, 0), //Left Edge
                new Vector3f(0, 0.05f, zDistance - 0.05f), // Right Edge
                new Vector3f(0, 0.05f, 0), // Front Edge
                new Vector3f(xDistance - 0.05f, 0.05f, zDistance - 0.05f) // Back Edge
        };
    }

    public static Vector3f[] getSizes(int xDistance, int yDistance, int zDistance) {

        return new Vector3f[]{new Vector3f(xDistance - 0.1f, edgeSize, edgeSize), // Top Left Front
                new Vector3f(edgeSize, edgeSize, zDistance - 0.1f), // Top Right Front
                new Vector3f(edgeSize, edgeSize, zDistance - 0.1f), // Top Left Back
                new Vector3f(xDistance - 0.1f, edgeSize, edgeSize), // Top Right Back

                new Vector3f(xDistance - 0.1f, edgeSize, edgeSize), // Bottom Left Front
                new Vector3f(edgeSize, edgeSize, zDistance - 0.1f), // Bottom Right Front
                new Vector3f(edgeSize, edgeSize, zDistance - 0.1f), // Bottom Right Back
                new Vector3f(xDistance - 0.1f, edgeSize, edgeSize), // Bottom Right Back

                new Vector3f(edgeSize, yDistance - 0.1f, edgeSize), // Left Edge
                new Vector3f(edgeSize, yDistance - 0.1f, edgeSize), // Right Edge
                new Vector3f(edgeSize, yDistance - 0.1f, edgeSize), // Front Edge
                new Vector3f(edgeSize, yDistance - 0.1f, edgeSize), // Back Edge
        };
    }
}
