package byog.Core;

import byog.TileEngine.TERenderer;
import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;

import java.util.Random;

public class Hallways extends Map{

    protected TETile[][] w;
    protected Cursor p;



    // Hallway Constructor
    public Hallways() {
        w = new TETile[0][0];  //dummy

    }
/*
    //CODING OF CREATING DIFFERENT KINDS OF HALLWAYS, SUCH AS STRAIGHT, ANGLED, AND BRANCHED
    private void makeHallway(TETile[][] world, Cursor p, Random random){
        int length =  7; //random.nextInt(7);
        switch (3) {
            case 0:
                godown();
                return;
            case 1:
                goup();
                return;
            case 2:
                goleft();
                return;
            case 3:
                goright();
                return;

        }
    }
    //EITHER GOES UP OR DOWN THEN TURNS
    private void makeVangHallway(TETile[][] world, Cursor p, Random random){
        int length =  7; //random.nextInt(7);
        switch (1){
            case 0:
                godown(world, p, length);
                makeFlatWall(world, p, -1);
                switch(1){
                    case 0:
                        goright(world, p, length);

                    case 1:
                        goleft(world, p, length);

                }
                return;
            case 1:
                goup(world, p, length);
                makeFlatWall(world, p, 1);
                switch(1){
                    case 0:
                        goright(world, p, length);

                    case 1:
                        goleft(world, p, length);


                }


        }
    }

    //EITHER GOES RIGHT OR LEFT THEN TURNS
    private void makeFangHallway(TETile[][] world, Cursor p, Random random){
        int length = 7;
        switch(1) {
            case 0:
                goright();
                makeVertWall(world, p, 1);
                switch(1) {
                    case 0:
                        goup(world, p, length);
                    case 1:
                        godown(world, p, length);
                }
                return;

            case 1:
                goleft(world, p, length);
                makeVertWall(world, p, -1);
                switch(1) {
                    case 0:
                        goup(world, p, length);
                    case 1:
                        godown(world, p, length);
                }

        }
    }

    private void makeVBranchHallway(TETile[][] world, Cursor p, Random random, int d) {
        int length = 7;
        if (d == 3) {

            goup(); //GOES UP
            Cursor tempP1 = new Cursor(p.getpCur()); //KEEPS TRACK OF STARTING POSITION FOR CURSOR
            makeFlatWall(world, p, 1);
            goright(); // CREATES A HALLWAY TO THE RIGHT
            p = tempP1; //RESETS CURSOR TO PREVIOUS POSITION
            goleft(); //CREATES A HALLWAY TO THE LEFT TO CREATE A BRANCH
            return;
        }
        if (d == 4) {

            godown();
            Cursor tempP2 = new Cursor(p.getpCur()); //KEEPS TRACK OF STARTING POSITION FOR CURSOR
            makeFlatWall(world, p, -1);
            goright();
            p = tempP2;
            goleft();
        }
    }

        }

    private void makeFBranchHallway(TETile[][] world, Cursor p, Random random) {
        int length = 7;
        switch(1) {
            case 0:
                goright(world, p, length); //GOES UP
                Cursor tempP1 = new Cursor(p.getpCur()); //KEEPS TRACK OF STARTING POSITION FOR CURSOR
                makeVertFloor(world, p, 1);
                makeVertWall(world, p, 1);
                goup(world, p, length); // CREATES A HALLWAY TO THE RIGHT
                p = tempP1; //RESETS CURSOR TO PREVIOUS POSITION
                godown(world, p, length); //CREATES A HALLWAY TO THE LEFT TO CREATE A BRANCH
                return;
            case 1:
                goleft(world, p, length);
                Cursor tempP2 = new Cursor(p.getpCur()); //KEEPS TRACK OF STARTING POSITION FOR CURSOR
                makeVertFloor(world, p, -1);
                makeVertWall(world, p, -1);
                goup(world, p, length);
                p = tempP2;
                godown(world, p, length);
        }
    }
*/

//CODING OF ALL THE WALLS

    //CREATES WALL FLOOR WALL
    protected void makeVert(Cursor p){
        if (p.outBound()) {
            return;
        }
        w[p.x][p.y] = floor;
        w[p.x - 1][p.y] = wall;
        w[p.x + 1][p.y] = wall;
    }

    protected void makeFlat(Cursor p) {
        if (p.outBound()) {
            return;
        }
        w[p.x][p.y] = floor;
        w[p.x][p.y - 1] = wall;
        w[p.x][p.y + 1] = wall;
    }

    //CREATES ALL WALLS
    protected void makeVertWall(Cursor p){
        if (p.outBound()) {
            return;
        }
        w[p.x][p.y] = wall;
        w[p.x - 1][p.y] = wall;
        w[p.x + 1][p.y] = wall;
    }

    protected void makeFlatWall(Cursor p){
        if (p.outBound()) {
            return;
        }
        world[p.x][p.y] = wall;
        world[p.x][p.y + 1] = wall;
        world[p.x][p.y - 1] = wall;
    }

    /*
    //CREATES ALL FLOORS
    protected void makeFlatFloor(TETile[][] world, Cursor p, int d) {
        world[p.x][p.y + d] = floor;
        world[p.x - 1][p.y + d] = floor;
        world[p.x + 1][p.y + d] = floor;
      //  p.y += d;
    }

    protected void makeVertFloor(TETile[][] world, Cursor p, int d){
        world[p.x + d][p.y] = floor;
        world[p.x + d][p.y + 1] = floor;
        world[p.x + d][p.y - 1] = floor;
      //  p.x += d;
    }
 //FUNCTIONS BELOW TELL US WHICH DIRECTION THE HALLWAY IS GOING IN

    private void goup() {
        p.up();
        makeVert(p);

    }

    private void godown() {

        p.down();
        makeVert(p);


    }

    private void goleft(){

        p.left();
        makeFlat(w, p);

    }

    private void goright() {

        p.right();
        makeFlat(w, p);

    } */



}