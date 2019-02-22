package byog.Core;


import java.io.Serializable;

/**Class that takes in a starting Position at top left corner,
 * length and width to simulate a rectangular area
 *
 * can fill the deque with all positions in a h by w area
 * can fill deque with edges of said area
 * can fill deque with corners of said area
 * can act as generic deque
 *
 */
public class Area implements Serializable {
    protected ArrayDeque<Position> area;
    private static Position start;
    private int height;
    private int width;
    private int size;
    private ArrayDeque<Position> corners;


    protected Area(Position p, int h, int w) {
        start = p;
        height = h;
        width = w;
        area = new ArrayDeque<Position>();
        size = 0;
    }

    protected Area() {
        area = new ArrayDeque<Position>();
        size = 0;
    }

    //iterates through given area and store each Position
    protected void fillArea(int mapW, int mapH) {
        for (int i = 0; i < height; i += 1) {
            for (int j = 0; j < width; j += 1) {
                int x = start.xPos + j;
                int y = start.yPos - i;
                Position temp = new Position(x, y);
                if (temp.outBound(mapW, mapH)) {
                    continue;
                }
                area.addLast(temp);
                size += 1;
            }
        }
    }

    //returns elements in order one at a time
    public Position first() {
        size -= 1;
        return area.removeFirst();
    }

    public int size() {
        return area.size();
    }

    public boolean isEmpty() {
        return area.isEmpty();
    }



    //trace edge of a room and adds every corresponding Position to area
    //starts at startPos
    protected void fillEdge(int mapW, int mapH) {
        //area = new ArrayDeque<Position>();
        Position p = new Position(start.xPos, start.yPos);
        Cursor cursor = new Cursor(p);
        for (int i = 0; i < 4; i += 1) {  //4 sides
            if (i == 0 || i == 2) {        //if going left or right
                edgeHelper(cursor, width, i, mapW, mapH);
            } else { //if going up or down
                edgeHelper(cursor, height, i, mapW, mapH);
            }
        }
        size = area.size();

    }

    //helper to avoid repeated code when tracing edge
    //directions: 0 R, 1 D, 2 L, 3 U
    //don't add if x or y is negative
    private void edgeHelper(Cursor c, int dimension, int direction, int mapW, int mapH) {
        for (int i = 0; i < dimension - 1; i += 1) {
            Position pCur = c.getpCur();
            if (pCur.outBound(mapW, mapH)) {
                continue;
            }
            Position temp = new Position(pCur.xPos, pCur.yPos);
            area.addLast(temp);
            size += 1;
            if (direction == 0) {
                c.right();
            } else if (direction == 1) {
                c.down();
            } else if (direction == 2) {
                c.left();
            } else if (direction == 3) {
                c.up();
            }
        }
    }

    protected Position getPos(int index) {
        return area.get(index);
    }

    protected void fillCorner() {
        //area = new ArrayDeque<Position>();
        Position p = new Position(start.xPos, start.yPos);
        Cursor c = new Cursor(p);
        //tL
        area.addLast(makePosition(c));
        //tR
        for (int i = 0; i < width - 1; i += 1) {
            c.right();
        }
        area.addLast(makePosition(c));
        //bR
        for (int i = 0; i < height - 1; i += 1) {
            c.down();
        }
        area.addLast(makePosition(c));
        //bL
        for (int i = 0; i < width - 1; i += 1) {
            c.left();
        }
        area.addLast(makePosition(c));
        size += 4;
    }

    private Position makePosition(Cursor c) {
        return new Position(c.getpCur().xPos, c.getpCur().yPos);
    }

    public void addPosition(Position pos) {
        area.addLast(pos);
        size += 1;
    }

    //adds all positions in b to current deque
    public void copyOver(Area b) {
        while (!b.isEmpty()) {
            Position temp = b.first();
            area.addLast(temp);
        }
    }
}
