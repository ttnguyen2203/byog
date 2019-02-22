package byog.Core;

import byog.TileEngine.TERenderer;
import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;

import java.util.Random;


public class Draw extends Map {
    protected Position entrance;
    protected int direction;
    protected Cursor me;
    private TETile[][] w;

    // Hallway Construc

    public Draw(TETile[][] world, Position p, int d) {
        entrance = new Position(p.xPos, p.yPos);
        direction = d;
        me = new Cursor(p);
        w = world;


    }




    protected void adventure(Random random1) {
        Cursor next = nextHelper(me);
        Cursor nextNext = nextHelper(next);



        if (checkBorder1(me) || checkBorder1(next)) {
            w[me.x][me.y] = WALL;
            return;
        }

        if (checkBorder2(me) || checkBorder2(next)) {
            w[me.x][me.y] = WALL;
            return;
        }


        if (checkwall(next)) {
            fixwall();
            return;
        }

        if (w[next.x][next.y] == FLOOR) {
            checksides(me);
            return;
        } else {
            int random = random1.nextInt(4);
            if (random < 5) {

                move();

                adventure(random1);
            }

        }

    }





    //FUNCTIONS BELOW TELL US WHICH DIRECTION THE HALLWAY IS GOING IN
    protected void move() {

        if (direction == 0) {
            me.right();
            makeFlat(w, me);
        }

        if (direction == 1) {
            me.left();
            makeFlat(w, me);

        }

        if (direction == 2) {
            me.up();
            makeVert(w, me);
        }

        if (direction == 3) {
            me.down();
            makeVert(w, me);
        }
    }



    private Cursor nextHelper(Cursor p) {
        Cursor next = new Cursor(p.getpCur());

        next.ghostmove(direction);
        return next;

    }

    protected void makeVert(TETile[][] world, Cursor p) {

        w[p.x][p.y] = FLOOR;
        w[p.x - 1][p.y] = WALL;
        w[p.x + 1][p.y] = WALL;
    }

    protected void makeFlat(TETile[][] world, Cursor p) {

        w[p.x][p.y] = FLOOR;
        w[p.x][p.y - 1] = WALL;
        w[p.x][p.y + 1] = WALL;
    }


    protected boolean checkwall(Cursor next) {

        if (w[next.x][next.y] == WALL) {
            return true;
        }

        return false;
    }
    protected void fixwall() {

        Cursor next = nextHelper(me);
        Cursor nextNext = nextHelper(next);
        if (checkBorder2(nextNext) || checkBorder1(nextNext)) {

            return;
        }

        if (w[nextNext.x][nextNext.y] == WALL) {
            me = next;
            w[me.x][me.y] = FLOOR;
            checksides(me);

            me = nextHelper(me);
            w[me.x][me.y] = FLOOR;
            if (!checkBorder2(nextNext) && !checkBorder1(nextNext)) {
                checksides(nextNext);
                return;
            }


            return;
        }
        if (w[nextNext.x][nextNext.y] == FLOOR) {
            checksides(me);
            w[next.x][next.y] = FLOOR;
            checksides(me);
            me = nextHelper(me);
            checksides(me);
            return;
        }
    }

    protected void checksides(Cursor p) {

        for (int i = -1; i < 2; i += 1) {
            if (w[p.x][p.y + i] == Tileset.NOTHING) {
                w[p.x][p.y + i] = WALL;
            }
        }

        for (int i = -1; i < 2; i += 1) {
            if (checkBorder2(p)) {
                return;
            }
            if (w[p.x + i][p.y] == Tileset.NOTHING) {
                w[p.x + i][p.y] = WALL;
            }
        }

    }

    protected boolean checkBorder1(Cursor p) {

        if (p.x == 0 || p.x == Game.WIDTH || p.y == 0 || p.y == Game.HEIGHT) {

            return true;
        }
        return false;
    }

    protected boolean checkBorder2(Cursor p) {
        if (checkBorder1(p)) {
            return true;
        }
        if (p.x + 1 == Game.WIDTH) {

            return true;
        }
        if (p.x - 1 == 0) {

            return true;
        }

        if (p.y + 1 == 0) {

            return true;
        }

        if (p.y - 1 == Game.HEIGHT) {

            return true;

        }
        return false;
    }



    public static void main(String[] args) {
        TERenderer ter = new TERenderer();
        ter.initialize(Game.WIDTH, Game.HEIGHT);


    }
}




