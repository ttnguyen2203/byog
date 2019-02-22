package byog.Core;

import byog.TileEngine.TERenderer;
import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;
import java.util.Random;

public class Room extends Map {
    private Position entrance;
    private Position cheat;
    private int h;
    private int w;
    private int floorH;
    private int floorW;
    protected Random RANDOM;
    private TETile[][] drawBoard;
    private static Position startPos;
    private Area entrances;
    private static final int MAX_ENTRANCES = 5;


    public Room(Random RANDOM) {
        drawBoard = new TETile[Game.WIDTH][Game.HEIGHT];
        for (int x = 0; x < Game.WIDTH; x += 1) {
            for (int y = 0; y < Game.HEIGHT; y += 1) {
                drawBoard[x][y] = NOTHING;
            }
        }
        this.RANDOM = RANDOM;
        entrances = new Area();
        viableRoom();
        addCheat();
    }
    public Room() {
        drawBoard = new TETile[Game.WIDTH][Game.HEIGHT];
        for (int x = 0; x < Game.WIDTH; x += 1) {
            for (int y = 0; y < Game.HEIGHT; y += 1) {
                drawBoard[x][y] = NOTHING;
            }
        }
        entrances = new Area();
        viableRoom();
        addCheat();
    }

    public Position getCheat() {
        return cheat;
    }

    public Position getEntrance() {
        return entrance;
    }

    public TETile[][] getDrawBoard() {
        return drawBoard;
    }

    public Position getStartPos() {
        return startPos;
    }

    public int getH() {
        return h;
    }

    public int getW() {
        return w;
    }

    public Area getEntrances() {
        return entrances;
    }

    @Override
    protected TETile getTile(Position p) {
        return drawBoard[p.xPos][p.yPos];
    }


    //check for overlap with real world objects THIS SHIT NEEDS MORE WORK BC IT AINT WORKING
    //returns entrance position of a viable room
    private Position viableRoom() {
        Position temp = makeRoom();
        return temp;
    }

    /*  Room seed parameters for randomization;
        x: min 0, max WIDTH - (w + 2)
        y: min at h + 2, max at HEIGHT
     */
    private Position seedMaker(int h, int w) {
        Position seed = new Position(0,0);
        seed.xPos = randomInt(0, Game.WIDTH - w - 2);
        seed.yPos = randomInt(h + 2, Game.HEIGHT);
        return seed;
    }

    @Override
    protected int randomInt(int a, int b) {
        return RandomUtils.uniform(this.RANDOM, a, b);
    }


    //Make room with 1 space in wall for entrance
    //starting at top left corner wall
    //fill can overlap
    //sets instance variables
    public Position makeRoom() {
        floorH = randomInt(3, 8);
        floorW = randomInt(3, 10);
        h = floorH + 2;
        w = floorW + 2;
        Position pos = seedMaker(floorH, floorW);
        startPos = new Position(pos.xPos, pos.yPos);
        makeBox(startPos, w, h);

        //multiple entrances
        int random = randomInt(0, 2);
        switch(random) {
            case(0): multipleEntrances(MAX_ENTRANCES - 2);
                break;
            case(1): multipleEntrances(MAX_ENTRANCES - 1);
                break;
            case(2): multipleEntrances(MAX_ENTRANCES - 0);
                break;
        }


        //prints some instance variables of each room for testing
//        System.out.println("Entrance at" + " " + entrance.xPos + " " + entrance. yPos);
//        System.out.println("height " + floorH + " width " + floorW);


        return entrance;
    }

    //makes box of wall edges, and floor filling
    private void makeBox(Position pos, int wid, int hei) {
        flatFill(pos, wid, hei, FLOOR);
        edgeFill(pos, wid, hei, WALL);
    }
    private void flatFill(Position pos, int wid, int hei, TETile tile) {
        //fill all with floor
        Area deque = new Area(pos, hei, wid);
        deque.fillArea(Game.WIDTH, Game.HEIGHT);
        while (!deque.isEmpty()) {
            tilePlacer(deque.first(), tile);
        }

    }
    private void edgeFill(Position pos, int wid, int hei, TETile tile) {
        //fill edge with wall
        Area deque = new Area(pos, hei, wid);
        deque.fillEdge(Game.WIDTH, Game.HEIGHT);
        while (!deque.isEmpty()) {
            tilePlacer(deque.first(), tile);
        }
    }

    //return a randomized Position of an edge except for corner
    private Position entranceMaker(Position pos, int wid, int hei) {
        Area deque = new Area(pos, hei, wid);
        deque.fillEdge(Game.WIDTH, Game.HEIGHT);
        int length = deque.size();
        int index = randomInt(0, length);
        Position entrance = deque.getPos(index);
        while (isCornerv3(entrance) || checkBorder(entrance)) {
            index = randomInt(0, length);
            entrance = deque.getPos(index);
        }

        //test
        tilePlacer(entrance, FLOOR);

        return entrance;
    }

    private void multipleEntrances(int number) {
        for (int i = 0; i < number; i += 1) {
            Position temp = entranceMaker(startPos, w, h);
            entrances.addPosition(temp);
        }
    }
    @Override
    protected void tilePlacer(Position p, TETile tile) {
        drawBoard[p.xPos][p.yPos] = tile;
    }



    //returns Position on the opposite side of p in line with o
    //if p and o are not in line, return null
    //



    private boolean isCornerv3(Position p) {
        Area corners = new Area(startPos, h, w);
        corners.fillCorner();
        while (!corners.isEmpty()) {
            Position check = corners.first();
            if (p.isEqual(check)) {
                return true;
            }
        }
        return false;
    }


    private Position bottomRight(Position p) {
        return new Position(p.xPos + 1,p.yPos - 1);
    }

    private void addCheat() {
        Position floorCorner = bottomRight(startPos);
        Area floor = new Area(floorCorner, floorH, floorW);
        floor.fillArea(Game.WIDTH, Game.HEIGHT);
        int pickCheat = randomInt(0, floor.size());
        Position cheat = floor.getPos(pickCheat);
        this.cheat = cheat;
    }







   //TESTING ONLY
    public static void main(String[] args) {

        // initialize the tile rendering engine with a window of size WIDTH x HEIGHT
        TERenderer ter = new TERenderer();
        ter.initialize(Game.WIDTH, Game.HEIGHT);

        // initialize tiles
        TETile[][] world = new TETile[Game.WIDTH][Game.HEIGHT];
        for (int x = 0; x < Game.WIDTH; x += 1) {
            for (int y = 0; y < Game.HEIGHT; y += 1) {
                world[x][y] = Tileset.NOTHING;
            }
        }


        //Room idk = new Room();
        //Game test = new Game();
        //test.world = world;

        //test.fillVertical(test.test, 5, test.wall);
        //test.makeRoom(test.test);

        // draws the world to the screen
        //ter.renderFrame(idk.getDrawBoard());
    }
}

