package byog.Core;

import byog.TileEngine.TERenderer;
import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;

import java.awt.Color;
import java.io.Serializable;
import java.util.Random;

/**
 * BIGGEST EDGE CASE: wall line directly in front of entrance
 */


public class Map2 implements Serializable {

    /* Feel free to change the width and height. */
    protected static TETile DOT = new TETile('&', Color.white, Color.black, "dot");
    protected static TETile PLAYER = new TETile('@', Color.white, Color.black, "player");
    protected TETile[][] world;
    protected Random RANDOM;
    protected final TETile NOTHING = Tileset.NOTHING;
    protected final TETile FLOOR = Tileset.FLOOR;
    protected final TETile WALL = Tileset.WALL;
    protected ArrayDeque<Cursor> dots = new ArrayDeque<>();
    protected ArrayDeque<Cursor> removeddots = new ArrayDeque<>();
    protected Area entrances;
    protected Area entrancecopy;
    protected Cursor player;
    protected int score = 0;


    public Map2() {
        world = new TETile[Game.WIDTH][Game.HEIGHT];
        entrances = new Area();
        for (int x = 0; x < Game.WIDTH; x += 1) {
            for (int y = 0; y < Game.HEIGHT; y += 1) {
                world[x][y] = NOTHING;
            }
        }


    }




    //generate number of rooms
    //if addRoom turns false (cannot add more rooms), stops loop
    protected void generateRoom(int number) {
        for (int i = 0; i < number; i += 1) {
            if (!addRoom()) {
                break;
            }
        }
    }

    /*add new room by drawing one at random on a draft world
      then copy it over iteratively
      if it takes too long to generate room, then stop
     */




    //returns true if can continue to add room, false if not
    private boolean addRoom() {
        Room draft = new Room();//RANDOM);

        //time out condition
        long time = System.currentTimeMillis();
        long end = time + 110; //if takes longer than 50 millisec to find room, stops

        while (checkOverlap(draft)) {
            if (System.currentTimeMillis() >= end) {
                return false;
            }
            draft = new Room();//(RANDOM);
        }
        Position start = draft.getStartPos();
        entrances.copyOver(draft.getEntrances());
        Area iterating = new Area(start, draft.getH(), draft.getW());
        iterating.fillArea(Game.WIDTH, Game.HEIGHT);
        while (!iterating.isEmpty()) {
            Position p = iterating.first();
            tilePlacer(p, draft.getTile(p));
        }
        return true;
    }


    protected boolean checkOverlap(Room r) {
        Position start = r.getStartPos();
        Area iterating = new Area(start, r.getH(), r.getW());
        iterating.fillArea(Game.WIDTH, Game.HEIGHT);
        while (!iterating.isEmpty()) {
            Position p = iterating.first();
            if (getTile(p) != NOTHING) {
                return true;
            }
        }
        return false;
    }

    protected Position inLine(Position p, Position o) {
        if (p.equals(o)) {
            throw new RuntimeException("Positions entered are the same");
        } else if (p.xPos == o.xPos) {   //if vertical
            if (p.yPos < o.yPos) {
                return new Position(p.xPos, p.yPos - 1);
            } else {
                return new Position(p.xPos, p.yPos + 1);
            }
        } else if (p.yPos == o.yPos) {
            if (p.xPos < o.xPos) {
                return new Position(p.xPos - 1, p.yPos);
            } else {
                return new Position(p.xPos + 1, p.yPos);
            }
        } else {
            return null;
        }
    }

    //ADD HALLWAYS FROM ENTRANCE

    public Position firststep(Position p) {
        for (int i = 0; i < 4; i += 1) {
            Position orig = new Position(p.xPos, p.yPos);
            Cursor check = new Cursor(orig);

            check = firststephelper(check, i);

            Position temp = check.getpCur();
            if (getTile(temp) == FLOOR) {
                return inLine(p, temp);
            }
        }
        return null;
    }



    private Cursor firststephelper(Cursor c, int i) {
        if (i == 0) {
            c.right();
            return c;
        }

        if (i == 1) {
            c.left();
            return c;
        }

        if (i == 2) {
            c.up();
            return c;
        }

        if (i == 3) {
            c.down();
            return c;
        }
        return null;
    }

    protected void makeHallway() {
        while (!entrances.isEmpty()) {
            Position temp = entrances.first();
           // System.out.println(temp.xPos + " " + temp.yPos);
            makeHallwayhelper(temp);
        }
    }

    private void makeHallwayhelper(Position entrance) {
        Cursor orig = new Cursor(entrance); //keeps track of original entrance
      //  System.out.println(entrance.xPos + " " + entrance.yPos);
        Position next = firststep(entrance);  //finds our first step
        int d = directionfinder(entrance, next);  //finds what direction to move in
        Draw draw = new Draw(world, entrance, d);  //draws whatever direction we're going in


        draw.adventure(RANDOM);
        world[orig.x][orig.y] = FLOOR;
        draw.checksides(orig);
    }


    protected int directionfinder(Position p, Position u) {

        if (p.xPos == u.xPos) {   //if vertical
            if (p.yPos < u.yPos) {
                return 2;
            } else {
                return 3;
            }
        } else if (p.yPos == u.yPos) {
            if (p.xPos < u.xPos) {
                return 0;
            } else {
                return 1;
            }
        } else {
            throw new RuntimeException("not in line");
        }

    }

    protected TETile getTile(Position p) {
        return world[p.xPos][p.yPos];
    }

    protected void tilePlacer(Position p, TETile tile) {
        world[p.xPos][p.yPos] = tile;
    }


    protected int randomInt(int a, int b) {
        return RandomUtils.uniform(RANDOM, a, b);
    }


    protected boolean checkBorder(Position p) {
        Position topLeft = new Position(0, Game.HEIGHT - 1);
        Area border = new Area(topLeft, Game.HEIGHT, Game.WIDTH);
        border.fillEdge(Game.WIDTH, Game.HEIGHT);
        while (!border.isEmpty()) {
            Position temp = border.first();
            if (p.isEqual(temp)) {
                return true;
            }
        }
        return false;
    }

    //GENERATES THE FIRST ORIGINAL MAP AND WORLD
    protected TETile[][] generateMap1(Random random) {
        RANDOM = random;
        generateRoom(8);
        makeHallway();

        for (int i = 10; i <= Game.WIDTH - 1; i += 1) {
            for (int j = 1; j < Game.HEIGHT - 1; j += 1) {
                if (world[i][j] == FLOOR) {
                    Position temp = new Position(i, j);
                    player = new Cursor(temp);
                    world[i][j] = PLAYER;
                    for (int k = 0; k < 8; k += 1) {
                        Cursor tempdot = new Cursor(makedot());
                        dots.addLast(tempdot);
                    }

                    return world;
                }
            }
        }
        return world;
    }

    //MAKES DOTS ON THE FLOOR
    protected Position makedot() {
        for (int i = RandomUtils.uniform(RANDOM, Game.WIDTH - 1); i <= Game.WIDTH - 1; i += 1) {
            for (int j = RandomUtils.uniform(RANDOM, Game.HEIGHT - 1);
                 j < Game.HEIGHT - 1; j += 1) {
                if (world[i][j] == FLOOR) {
                    Position temp = new Position(i, j);
                    world[i][j] = DOT;
                    return temp;

                }
            }
        }
        return player.getpCur();

    }



    //THIS FUNCTION IS CALLED AFTER MOVEMENT, AND NEEDING TO UPDATE THE POSITIONS
    //cursor p is player olds position, cursor d is dots old position
    protected TETile[][] generateMap2(TETile[][] world1, Cursor p) {
        world1[p.x][p.y] = FLOOR;
        world1[player.x][player.y] = PLAYER;

        return world1;
    }

    protected void movealldots() {

        for (int i = 0; i < dots.size(); i += 1) {
            if (checkequals(dots.get(i), player)) {
                dots.get(i).x = 0;
                dots.get(i).y = 0;
                world[dots.get(i).x][dots.get(i).y] = NOTHING;
            }
            Cursor temp = new Cursor(dots.get(i).getpCur());
            if (removeddots.isEmpty() || !checkremoved(dots.get(i), removeddots)) {
                movedotchar(dots.get(i));
                if (checkequals(dots.get(i), player)) {
                    dots.get(i).x = 0;
                    dots.get(i).y = 0;
                    world[dots.get(i).x][dots.get(i).y] = NOTHING;
                } else {
                    if (world[dots.get(i).x][dots.get(i).y].equals(WALL)) {
                        dots.get(i).x = temp.x;
                        dots.get(i).y = temp.y;
                        dots.get(i).pCur = new Position(temp.x, temp.y);
                    }
                    world[temp.x][temp.y] = FLOOR;
                    world[dots.get(i).x][dots.get(i).y] = DOT;

                }
            }
        }

    }

    protected boolean checkequals(Cursor dot, Cursor player1) {
        if (dot.x == player1.x && dot.y == player1.y) {
            removeddots.addLast(dot);
            score += 1;
            return true;
        }

        return false;
    }

    protected void movedotchar(Cursor p) {
        int random = RandomUtils.uniform(RANDOM, 4);
        switch (random) {
            case 0:
                p.up();
                break;
            case 1:
                p.down();
                break;
            case 2:
                p.left();
                break;
            case 3:
                p.right();
                break;
            default:

        }
    }

    private boolean checkremoved(Cursor dot, ArrayDeque<Cursor> removeddots1) {
        for (int j = 0; j < removeddots1.size(); j += 1) {
            if (dot == removeddots1.get(j)) {
                return true;
            }
        }

        return false;

    }

/*


    //For testing only
    public static void main(String[] args) {
        // initialize the tile rendering engine with a window of size WIDTH x HEIGHT
        TERenderer ter = new TERenderer();
        ter.initialize(Game.WIDTH, Game.HEIGHT);

        Map test = new Map();
        long k = 8471551043745955672L;
        Random testr = new Random(k);
        test.generateMap1(testr);
        test.generateMap1(testr);






        ter.renderFrame(test.world);
    }

*/
}
