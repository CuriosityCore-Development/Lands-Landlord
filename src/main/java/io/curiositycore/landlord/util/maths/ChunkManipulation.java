package io.curiositycore.landlord.util.maths;

/**
 * Methods for low-level chunk data manipulation.
 */
public class ChunkManipulation {
    /**
     * Determines the chunk a set of coordinates are in, without the needing to check the chunk data.
     * @param xCoordinate The x-coordinate to check
     * @param zCoordinate The z-coordinate to check
     * @return
     */
    public static int[] chunkCheck(int xCoordinate,int zCoordinate){
        int chunkXt =  xCoordinate >> 4;
        int chunkZt =  zCoordinate >> 4;
        return new int[]{chunkXt,chunkZt};
    }
}
