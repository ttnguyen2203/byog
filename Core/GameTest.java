package byog.Core;
import static org.junit.Assert.*;

import byog.TileEngine.TERenderer;
import org.junit.Test;
import byog.TileEngine.TETile;

public class GameTest {
    public static final int WIDTH = 60;
    public static final int HEIGHT = 30;


    @Test
    public void testplaywithinputstring() {
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);
        Game game = new Game();
        TETile[][] world1 = game.playWithInputString("n455857754086099036s");
        Game game2 = new Game();
        TETile[][] world2 = game2.playWithInputString("n455857754086099036s");

        for (int i = 0; i < world1.length; i += 1) {
            for (int j = 0; i < world2[i].length; i += 1) {
                assertEquals(world1[i][j], world2[i][j]);
            }
        }
        System.out.print("works fine");
    }

}

