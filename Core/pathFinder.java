package byog.Core;

import byog.TileEngine.TERenderer;
import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;
import org.w3c.dom.html.HTMLImageElement;

import java.lang.reflect.Array;
import java.util.Random;

public class pathFinder extends Map {
    protected TETile[][] realWorld;
    protected Random RANDOM;
    protected Position entrance;
    protected int direction;
    protected Cursor me;
    protected boolean done = false;
    protected boolean firstStep = true;
    protected boolean splitTwo = false;
    protected boolean splitThree = false;
    private static final int SPLIT_CHANCE = 2;
    private static final int TURN_CHANCE = 5;
    private static final int MINLENGTH = 5; //minimum straight steps before chance to turn
    private int currentLength = 0;

    /**chances:
     *  2 / SPLIT_CHANCE to split
     *  TURN_CHANCE/10 to turn
     */




    public pathFinder(Position p, int d, TETile[][] w, Random RANDOM) {
        entrance = new Position(p.xPos, p.yPos);
        direction = d;
        me = new Cursor(p);
        realWorld = w;
        this.RANDOM = RANDOM;
    }

    //OVERLOADING WITH CHEAT PATHFINDER:
    public pathFinder(Position p, TETile[][] w, Random RANDOM) {
        entrance = new Position(p.xPos, p.yPos);
        me = new Cursor(p);
        realWorld = w;
        this.RANDOM = RANDOM;
    }

    @Override
    protected int randomInt(int a, int b) {
        return RandomUtils.uniform(RANDOM, a, b);
    }


    /**method used to update cursor: try 2 approaches: recursive and step-wise
     *
     * STOP CONDITIONS:
     * 1. if spawns at border (next = outBound) --> WILL NOT HAPPEN b/c ROOM SPAWN CHECK
     * 2. if next step is border --> put wall at next and stop
     * 3. if next step is wall:
     *      a. if nextnext step is floor --> break through
     *      b. if nextnext step is wall --> drill
     *
     * BRANCHING CONDITION CHECK: -- do this before stop condition checks;
     * A. if next is outBound --> place wall at current and stop
     * B. if nextnext is outBound
     *      --> if next is wall (not Tileset.NOTHING) --> stop
     *      --> if next is Tileset.NOTHING --> place wall at next
     *
     * EDGE CASE:
     * 1. moving along border --> cannot turn to border
     * 2. 2 walls ahead --> DRILL
     *          DRILL = move forward and check sides to see if there's an existing floor
     *                  if there is, fill empty spaces around with walls and stop;
     */
    protected boolean finished() {
        return done;
    }
    protected void adventure() {
        //if done then stops
        if (done) {
            return;
        }
        if (me.outBound()) { //back up in case shit falls through
            done = true;
            return;
        }
        Cursor next = nextHelper(me);
        Cursor nextNext = nextHelper(next);

        //VARIABLE CHECKS TO AVOID OUT OF BOUND;
        //if next step is out of bound
        if (next.outBound()) { //put wall right under and stops;
            tilePlacer(me.getpCur(), WALL); //overwriting tiling rules
            done = true;
        }
        //if 2 steps ahead is out of bound
        else if (nextNext.outBound()) {
            //something is ahead already --> stop
            if (getTile(next.getpCur()) != Tileset.NOTHING) {
                done = true;
            } else {
                //NOTHING is ahead --> put wall and stop
                tiling(WALL, next);
                fillWalls(me);
                done = true;
            }
        }
        //STOP CONDITION CHECKS

        //if next step is a wall
        else if (getTile(next.getpCur()) == WALL) {
            TETile tileAfter = getTile(nextNext.getpCur());
            if (tileAfter == FLOOR) { // if floor after initial wall
                goStraight();
                done = true;
            } else if (tileAfter == WALL) { //if more walls after --> DRILL
                //TRYING: DRILLING TWICE FOR EFFICIENCY;
                drill();
                drill();

            } else if (getTile(next.getpCur()) == FLOOR) {
                //if floor ahead, stop
                done = true;
            } else {
                //

                goStraight(); //increments another step and hope
                              //previous conditions will catch
                //throw new RuntimeException("Empty space after wall");
                 //--> account for this if happens
            }
        }
        //TRYING RECURSION AT LOW LEVEL
         else {
            if (firstStep) { //can't turn before taking first step
                takeFirstStep();
                currentLength += 1; //first step counts to length
            }
            //when MINLENGTH is met, stop going straight and has a chance to turn
            //length counter is reset with a turn;
            if (currentLength < MINLENGTH) {
                goStraight();
                currentLength += 1;
            } else {
                //splitRoll(); //SPLITTING IS TOO RANDOM RN
                straightOrTurn(); //roll for chance to turn
            }
        }
    }


    protected void move() {
        /*
        Position current = new Position(me.getpCur().xPos, me.getpCur().yPos);
        if (checkBorder(current)) {
            tilePlacer(current, wall);
            return;
        }*/
        if (direction == 0) {
           // System.out.println(me.getpCur().xPos + " " + me.getpCur().yPos);
            me.right();
            makeVert(me);
        } else if (direction == 1) {
            me.left();
            makeVert(me);
        } else if (direction == 2) {
            me.up();
            makeFlat( me);
        } else if (direction == 3) {
            me.down();
            makeFlat(me);
        }
    }

    /**Draws input tile onto given Position with some special cases:
     *  floor can overwrites every existing tile during map gen
     *  wall cannot overwrite floor
     *
     *  STILL OVERWRITING FLOORS ??
     */
    protected void tiling(TETile tile, Position p) {
        if (p.outBound(Game.WIDTH, Game.HEIGHT)) {
            return;
        }
        if (getTile(p) == FLOOR && tile == WALL) {
            return;
        } else {
            tilePlacer(p, tile);
        }
    }
    protected void tiling(TETile tile, Cursor c) {
        if (c.outBound()) {
            return;
        }
        Position p = c.getpCur();
        if (getTile(p) == FLOOR && tile == WALL) {
            return;
        } else {
            tilePlacer(p, tile);
        }
    }


    @Override
    protected void tilePlacer(Position p, TETile tile) {
        realWorld[p.xPos][p.yPos] = tile;
    }
    @Override
    protected TETile getTile(Position p) {
        return realWorld[p.xPos][p.yPos];
    }

    //EDGECASE: DRILL
    //returns a deque containing positions of 2 tiles on the sides of c
    //POTENTIALLY RETURN OUT OF BOUND POSITIONS
    private Area sideTiles(Cursor c) {
        Area sides = new Area();
        if (direction == 0 || direction == 1) {
            Position first = new Position(c.x, c.y + 1);
            Position second = new Position(c.x, c.y - 1);
            sides.addPosition(first);
            sides.addPosition(second);

        } else if (direction == 2 || direction == 3) {
            Position first = new Position(c.x + 1, c.y);
            Position second = new Position(c.x - 1, c.y);
            sides.addPosition(first);
            sides.addPosition(second);
        }
        return sides;
    }
    //return true if either one of the side tiles is a floor tile
    private boolean daWae(Cursor c) {
        Area sides = sideTiles(c);
        while (!sides.isEmpty()) {
            Position temp = sides.first();
            if (temp.outBound(Game.WIDTH, Game.HEIGHT)) {
                return false;
            }
            if (getTile(temp) == FLOOR) {
                return true;
            }
        }
        return false;
    }
    //fill in all tiles around given cursor c using tiling rules
    private void fillWalls(Cursor c) {
        Position current = c.getpCur();
        Position topLeft = current.topLeft();
        Area around = new Area(topLeft, 3, 3);
        around.fillEdge(Game.WIDTH, Game.HEIGHT);
        while (!around.isEmpty()) {
            Position temp = around.first();
            tiling(WALL, temp);
        }
    }
    /*
    //return position of tile 1 up and 1 left
    protected Position topLeft(Position p) {
        return new Position(p.xPos - 1, p.yPos + 1);
    }
    */

    /**Makes wall-floor-wall building block of each hallway
     * by using makeVertWall or makeFlatWall,
     * then place floor tile at current cursor p
     */
    protected void makeVert(Cursor p) {
        makeVertWall(p);
        tiling(FLOOR, p);
    }
    protected void makeFlat(Cursor p) {
        makeFlatWall(p);
        tiling(FLOOR, p);
    }

    //CREATES ALL WALLS

    //places 3 wall tiles in a column with p at the middle
    protected void makeVertWall(Cursor p) {
        Area walls = threeVert(p);
        while (!walls.isEmpty()) {
            Position temp = walls.first();
            tiling(WALL, temp);
        }
    }
    protected void makeFlatWall(Cursor p) {
        Area walls = threeHorz(p);
        while (!walls.isEmpty()) {
            Position temp = walls.first();
            tiling(WALL, temp);
        }
    }

    /**returns Area deque of 3 tiles in a line given middle tile's position/cursor
     * if a position is out of bound, skip it
     */
    protected Area threeVert(Cursor c) {
        Area deque = new Area();
        Cursor ghost = c.copy();
        ghost.up();
        for (int i = 0; i < 3; i += 1) {
            Position temp = new Position(ghost.x, ghost.y);
            if (temp.outBound(Game.WIDTH, Game.HEIGHT)) {
                continue;
            }
            deque.addPosition(temp);
            ghost.down();
        }
        return deque;
    }
    protected Area threeHorz(Cursor c) {
        Area deque = new Area();
        Cursor ghost = c.copy();
        ghost.left();
        for (int i = 0; i < 3; i += 1) {
            Position temp = new Position(ghost.x, ghost.y);
            if (temp.outBound(Game.WIDTH, Game.HEIGHT)) {
                continue;
            }
            deque.addPosition(temp);
            ghost.right();
        }
        return deque;
    }

    /**intended direction keys:
     * going left/down = 0;
     * going right/up = 1;
     */
    protected void turn(int intended) {
        if (direction == 0 || direction == 1) {      //if currently horizontal
            Cursor next = nextHelper(me);
            makeVertWall(next);                      //if next is out of bound, nothing is drawn
            if (intended == 0) {           //going down
                direction = 3;
                move();
                move();
            } else if (intended == 1) {    //going up
                direction = 2;
                move();
                move();
            }
        } else if (direction == 2 || direction == 3) {
            Cursor next = nextHelper(me);             //if currently vertical
            makeFlatWall(next);
            if (intended == 0) {           //going left
                direction = 1;
                move();
                move();
            } else if (intended == 1) {
                direction = 0;             //going right
                move();
                move();
            }
        }
    }

    //returns a new cursor at next tile in same direction as me
    private Cursor nextHelper(Cursor p) {
        Cursor next = p.copy();
        next.directionSwitch(direction);
        return next;

    }


    //takes one step in the opposite direction
    private void oneStepBack() {
        if (direction == 0) {
            me.left();
        } else if (direction == 1) {
            me.right();
        } else if (direction == 2) {
            me.down();
        } else if (direction == 3) {
            me.up();
        }
    }
    private void turnTo(int intended) {
        if (intended == 2 || intended == 0) { //up or right
            turn(1);
        } else if (intended == 3 || intended == 1) { //left or down
            turn(0);
        }
    }

    private void changeDirection() {
        if (direction == 0) {
            direction = 1;
        } else if (direction == 1) {
            direction = 0;
        } else if (direction == 2) {
            direction = 3;
        } else if (direction == 3) {
            direction = 2;
        }
    }

    protected Area twoAway(Cursor c) {
        Area twoAway = new Area();
        Cursor clone = c.copy();
        Position p = clone.getpCur();
        Position first;
        Position second;
        if (direction == 1 || direction == 0) {
            first = new Position(p.xPos, p.yPos + 2);
            second = new Position(p.xPos, p.yPos - 2);
        } else {
            first = new Position(p.xPos + 2, p.yPos);
            second = new Position(p.xPos - 2, p.yPos);
        }
        if (!first.outBound(Game.WIDTH, Game.HEIGHT)) {
            twoAway.addPosition(first);
        }
        if (!second.outBound(Game.WIDTH, Game.HEIGHT)) {
            twoAway.addPosition(second);
        }
        return twoAway;
    }


    //run this every step to check for shortcut
    protected void feelers() {
        Area twoAway = twoAway(me);
        while (!twoAway.isEmpty()) {
            Position temp = twoAway.first();
            Position current = me.getpCur();
            if (getTile(temp) == FLOOR) {
                int direction = directionfinder(current, temp);
                //shortCut = direction;
            }
        }
    }


    //return true if orientation of me is horizontal or vetical
    private boolean isHorizontal() {
        return direction == 0 || direction == 1;
    }
    private boolean isVertical() {
        return direction == 2 || direction == 3;
    }

    private  void drawUnder() {
        if (isHorizontal()) {
            makeVert(me);
        } else if (isVertical()) {
            makeFlat(me);
        }
    }

    protected void goStraight() {
        move();
        currentLength += 1;
    }

    //chance go to straight or turn;
    protected void straightOrTurn() {
        int decision = randomInt(0, 10); //decides to move or turn
        if (decision < TURN_CHANCE) {
            goStraight();
        } else {
            int intended = randomInt(0, 2); //decides which direction to turn
            turn(intended);
            currentLength = 0;
        }
    }

    //roll for chance to split
    private void splitRoll() {
        int splitting = randomInt(0, 10); //decides if splitting
        if (splitting < SPLIT_CHANCE) {
            int howMany = randomInt(2, 4); //roll 2 or 3;
            branch(howMany);
        }
    }



    //push through existing wall and check two side tiles for floor
    //if theres floor, fill wall around me, then stop;
    protected void drill() {
        move();
        if (daWae(me)) {
            fillWalls(me);
            done = true;
        }
    }

    //take first step by drawing right under just in case
    //then go straight, b/c first step can't turn
    protected void takeFirstStep() {
        drawUnder();
        move();
        firstStep = false;

    }



    //IMPLEMENT SPLITTING
    //SPIT OUT 2 OR 3 NEW PATHFINDERS AND RUN THEIR PATH TILL END
    private Area validSpawner() {
        //makes a deque of 3 or less possible new spawner positions
        Area possible = sideTiles(me);
        for (int i = 0; i < 3; i += 1) {
            Position checking = possible.first();
            if (checking.outBound(Game.WIDTH, Game.HEIGHT) || checkBorder(checking)) {
                continue;
            } else {
                possible.addPosition(checking);
            }
        }
        Cursor inFront = nextHelper(me);
        possible.addPosition(inFront.getpCur());
        return possible;
    }

    private Area twoSpawns(Area deque) {
        //cannot spawn or only 1 possible --> dont spawn
        if (deque.isEmpty() || deque.size() == 1) {
            return null;
        } else if (deque.size() == 3){  //if 3 in deque, take one out
            Area rest = new Area();
            int which = randomInt(0, deque.size());
            for (int i = 0; i < 3; i += 1) {
                if (i == which) {
                    continue;
                } else {
                    rest.addPosition(deque.getPos(i));
                }
            }
            return rest;
        } else { //if two, return as is
            return deque;
        }
    }

    //return a deque of with a number of new pathFinders
    //kinda janky bc uses arraydeque
    private ArrayDeque<pathFinder> makeSplit(int number) {
        ArrayDeque<pathFinder> newFinders = new ArrayDeque<>();
        Area possible = validSpawner();
        if (number == 2) {
            twoSpawns(possible);
        }
        for (int i = 0; i < possible.size(); i += 1) {
            Position temp = possible.first();
            int d = directionfinder(me.getpCur(), temp);
            pathFinder spawn = new pathFinder(temp, d, realWorld, RANDOM);
            newFinders.addLast(spawn);
        }
        return newFinders;
    }


    //run all new runners
    private void branch(int number) {
        ArrayDeque<pathFinder> finders = makeSplit(number);
        while (!finders.isEmpty()) {
            pathFinder temp = finders.removeFirst();
            while (!temp.done) {
                temp.adventure();
            }
        }
    }


    //alternative pathfinding algorithm to connect all rooms;
    protected void cheatPath(Position destination) {
        //if done then stops
        if (done) {
            return;
        }
        if (me.outBound()) { //back up in case shit falls through
            done = true;
            return;
        }
        if (destination.isEqual(me.getpCur())) {
            done = true;
            return;
        } else {
            while (differenceX(destination) != 0) {
                setDirection(destination, 0);
                goStraight();
            }
            if (differenceY(destination) > 0) {
                turnOneStep(1);
            } else {
                turnOneStep(0);
            }
            while (differenceY(destination) != 0) {
                setDirection(destination, 0);
                goStraight();
            }
            fillWalls(me);

        }

    }

    //different version of turn that turn and ONLY MOVES ONCE
    protected void turnOneStep(int intended) {
        if (direction == 0 || direction == 1) {      //if currently horizontal
            Cursor next = nextHelper(me);
            makeVertWall(next);                      //if next is out of bound, nothing is drawn
            if (intended == 0) {           //going down
                direction = 3;
                move();
            } else if (intended == 1) {    //going up
                direction = 2;
                move();
            }
        } else if (direction == 2 || direction == 3) {
            Cursor next = nextHelper(me);             //if currently vertical
            makeFlatWall(next);
            if (intended == 0) {           //going left
                direction = 1;
                move();
            } else if (intended == 1) {
                direction = 0;             //going right
                move();
            }
        }
    }

    //return difference between me and position
    private int differenceX(Position p) {
        Position current = me.getpCur();
        return p.xPos - current.xPos;
    }
    private int differenceY(Position p) {
        Position current = me.getpCur();
        return p.yPos - current.yPos;
    }

    //check for both direction
    //0 for x direction, 1 for y direction
    protected void setDirection(Position destination, int which) {
        int goalX = differenceX(destination);
        int goalY = differenceY(destination);
        if (which == 0) { //going in x direction
            if (goalX < 0) {
                direction = 1;
            } else if (goalX > 0) {
                direction = 0;
            }
        } else if (which == 1){ //goign in y direction
            if (goalY < 0) {
                direction = 3;
            } else if (goalY > 0) {
                direction = 2;
            }
        }
    }



















}
