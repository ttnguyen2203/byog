package byog.Core;

import byog.TileEngine.TERenderer;
import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;
import java.util.Random;

public class GameBACKUP {
    public static final int WIDTH = 80; // map is w 80 h 40 rn
    public static final int HEIGHT = 40;
    public static Random SEED;
    public TETile[][] saved = null;
    /**
     * Method used for playing a fresh game. The game should start from the main menu.
     */
    public void playWithKeyboard() {
    }

    /**
     * Method used for autograding and testing the game code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The game should
     * behave exactly as if the user typed these characters into the game after playing
     * playWithKeyboard. If the string ends in ":q", the same world should be returned as if the
     * string did not end with q. For example "n123sss" and "n123sss:q" should return the same
     * world. However, the behavior is slightly different. After playing with "n123sss:q", the game
     * should save, and thus if we then called playWithInputString with the string "l", we'd expect
     * to get the exact same world back again, since this corresponds to loading the saved game.
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] playWithInputString(String input) {
        TETile[][] world;
        int i = 0;
        Character current = input.charAt(i);

        if (current == 'n' || current == 'N') { //new game

            //if only entered one letter;
            if (input.length() == 1) {
                System.out.println("Enter seed");
                return null;
            }

            //not valid seed format
            if (!input.contains("s") && !input.contains("S")) {
                System.out.println("Not valid seed format");
                return null;
            }

            String seedReading = "";
            i += 1; //check letter after N
            current = input.charAt(i);
            while ((current != 'S' || current != 's') && i < input.length() - 1) {
                seedReading += Character.toString(input.charAt(i));
                i += 1;
                current = input.charAt(i);
            }
            long seed = Long.valueOf(seedReading);
            world = startNewGame(seed);

            //add more command saving stuff later;

            if (hasQuit(input)){

                //saving game world

                System.out.println("Quitting");
                System.exit(0);
            }
            return world;

        } else if ((current == 'l' || current == 'L')) { //loading
            if (saved == null) {
                System.out.println("No saved game");
                return null;
            }

            world = saved;

            //add more after;

            return world;

        } else if (i == 0 && (current == 'q' || current == 'Q')) { //quit
            System.out.println("Quitting");
            System.exit(0);
        } else {
            return null;
        }

        //any other case?
        return null;
    }




    private TETile[][] startNewGame(long seed) {
        Map runGame = new Map();
        //Map.SEED = seed; //might not need
        runGame.RANDOM = new Random(seed);
        runGame.generateMap();
        return runGame.world;
    }

    //scan input string for quitting sequence :q
    private boolean hasQuit(String input) {
        Character lastLetter = input.charAt(input.length() - 1);
        Character secondToLast = input.charAt(input.length() - 2);
        if (secondToLast == ':' && (lastLetter == 'q' || lastLetter == 'Q')) {
            return true;
        }
        return false;
    }




    public static void main(String[] args) {
        TERenderer ter = new TERenderer();
        ter.initialize(Game.WIDTH, Game.HEIGHT);
        Game game = new Game();

        TETile[][] world = game.playWithInputString("n4518992736225145867s");
        game.playWithInputString("n4518992736225145867s");

        ter.renderFrame(world);

        System.out.println("done running");

    }

}
