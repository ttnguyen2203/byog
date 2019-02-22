package byog.Core;

import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;

public class MapGenerator {
    private static final int WIDTH = 80;
    private static final int HEIGHT = 40;
    private static final TETile floor = Tileset.FLOOR;
    private static final TETile wall = Tileset.WALL;
    private static final int roomNumber = 100; //map size will max out at some number
    public TETile[][] world;


    public MapGenerator(long seed) {
    }
}
