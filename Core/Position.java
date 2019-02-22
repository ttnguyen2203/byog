package byog.Core;


import java.io.Serializable;

//Coordinate object helper
public class Position extends Map implements Serializable {
    protected int xPos;
    protected int yPos;
    protected int direction;
    public Position(int x, int y) {
        xPos = x;
        yPos = y;
    }

    public boolean isEqual(Position p) {
        return (p.xPos == xPos && p.yPos == yPos);
    }

    //check if given position is out of bound of map
    protected boolean outBound(int mapW, int mapH) {
        if (xPos < 0 || yPos < 0 || //if goes out of bound;
                xPos >= mapW || yPos >= mapH) {
            return true;
        }
        return false;
    }
    protected Position topLeft() {
        return new Position(xPos - 1, yPos + 1);
    }

    protected void setDirection(int direction) {
        this.direction = direction;
    }
}

