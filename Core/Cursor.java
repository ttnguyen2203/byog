package byog.Core;


import java.io.Serializable;

public class Cursor extends Map implements Serializable {
    protected Position pCur;
    protected int x;
    protected int y;
    protected Position getpCur() {
        return pCur;
    }


    public Cursor(Position p) {
        this.pCur = new Position(p.xPos, p.yPos);
        x = pCur.xPos;
        y = pCur.yPos;
    }

    protected void left() {
        x -= 1;
        pCur.xPos = x;
    }

    protected void right() {
        x += 1;
        pCur.xPos = x;
    }

    protected void up() {
        y += 1;
        pCur.yPos = y;
    }

    protected void down() {
        y -= 1;
        pCur.yPos = y;
    }

    protected void ghostmove(int i) {
        if (i == 0) {
            right();

        }

        if (i == 1) {
            left();
        }

        if (i == 2) {
            up();
        }

        if (i == 3) {
            down();
        }
    }

    protected boolean outBound() {
        if (this.pCur.outBound(Game.WIDTH, Game.HEIGHT)) {
            return true;
        }
        return false;
    }
    protected Cursor copy() {
        return new Cursor(pCur);
    }
    //increments cursor to current direction direction
    protected void directionSwitch(int i) {
        if (i == 0) {
            right();
        }
        if (i == 1) {
            left();
        }
        if (i == 2) {
            up();
        }
        if (i == 3) {
            down();
        }
    }


}











