package byog.Core;

import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;
import java.util.Date;
import java.awt.*;
import java.io.*;
import java.util.Random;
import byog.TileEngine.TERenderer;
import edu.princeton.cs.introcs.StdDraw;


public class Game {
    public static final int WIDTH = 60;
    public static final int HEIGHT = 30;
    protected TETile[][] world;

    /**
     * Method used for playing a fresh game. The game should start from the main menu.
     */
    public void playWithKeyboard() {
        StdDraw.enableDoubleBuffering();
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT + 10);
        Font defaultFont = new Font("San Serif", Font.PLAIN, 16);
        drawMainMenu();
        while (true) { //game is running
            if (StdDraw.hasNextKeyTyped()) {
                char c = StdDraw.nextKeyTyped();
                switch (c) {
                    case 'N':
                    case 'n': //NEW GAME
                        StdDraw.clear(StdDraw.BLACK);
                        long seed = seedScreen(); //entering seed
                        Map runGame = startNewGame(seed);
                        StdDraw.setFont(defaultFont);
                        ter.renderFrame(runGame.world);
                        playGame(runGame, ter);

                        break;
                    case 'L':
                    case 'l': //load game
                        Map currentGame = loadGame();
                        if (currentGame == null) { //no saved game, stay at main menu
                            break;
                        } else {
                            StdDraw.setFont(defaultFont);
                            ter.renderFrame(currentGame.world);
                            playGame(currentGame, ter);

                        }
                        break;
                    case 'Q':
                    case 'q':    //quit game
                        System.exit(0);
                    default:
                        break;
                }

            }

        }
    }



    private static void drawMainMenu() {
        StdDraw.clear(StdDraw.BLACK);  //background picture maybe
        Font title = new Font("Arial", Font.BOLD, 80);
        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.setFont(title);

        StdDraw.text(WIDTH * 0.5, HEIGHT * 0.80, "LILITH");
        Font options = new Font("Impact", Font.PLAIN, 30);
        StdDraw.setFont(options);
        StdDraw.text(WIDTH * 0.5, HEIGHT * 0.50, "N - New Game");
        StdDraw.text(WIDTH * 0.5, HEIGHT * 0.42, "L - Load");
        StdDraw.text(WIDTH * 0.5, HEIGHT * 0.34,"Q - Quit");
        StdDraw.show();
        StdDraw.pause(1000);
    }


    /*
    private static long seedScreen() {
        StdDraw.clear(StdDraw.BLACK);  //background picture maybe
        Font seedFont = new Font("Impact", Font.ITALIC, 30);
        Font numberFont = new Font("Impact", Font.PLAIN, 20);
        String seedString = "";
        StdDraw.setFont(seedFont);
        StdDraw.text(WIDTH * 0.5, HEIGHT *0.8, "Enter seed:");
        StdDraw.setFont(numberFont);
        StdDraw.text(WIDTH * 0.5, HEIGHT * 0.5, seedString);
        StdDraw.show();
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char c = StdDraw.nextKeyTyped();
                if (Character.isDigit(c)) { //entering
                    StdDraw.clear(StdDraw.BLACK);
                    seedString += c;   //maybe check for length and Long limit
                    StdDraw.setFont(seedFont);
                    StdDraw.text(WIDTH * 0.5, HEIGHT * 0.8, "Enter seed:");
                    StdDraw.setFont(numberFont);
                    StdDraw.text(WIDTH * 0.5, HEIGHT * 0.5, seedString);
                    StdDraw.show();
                } else if (c == 's') {
                    long seed = Long.valueOf(seedString);
                    return seed;
                }
            }
        }
    }
*/

    private static long seedScreen() {
        StdDraw.clear(StdDraw.BLACK);  //background picture maybe
        Font seedFont = new Font("Impact", Font.ITALIC, 30);
        Font numberFont = new Font("Impact", Font.PLAIN, 20);
        Font directionFont = new Font("Impact", Font.PLAIN, 18);
        String seedString = "";
        StdDraw.setFont(seedFont);
        StdDraw.text(WIDTH * 0.5, HEIGHT * 0.8, "ENTER A SEQUENCE OF NUMBERS:");
        StdDraw.setFont(numberFont);
        StdDraw.text(WIDTH * 0.5, HEIGHT * 0.5, seedString);
        StdDraw.show();
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char c = StdDraw.nextKeyTyped();
                if (!Character.isDigit(c) && c != 's') {
                    return seedScreen();
                }
                if (Character.isDigit(c)) { //entering
                    StdDraw.clear(StdDraw.BLACK);
                    seedString += c;   //maybe check for length and Long limit
                    StdDraw.setFont(seedFont);
                    StdDraw.text(WIDTH * 0.5, HEIGHT * 0.8, "PRESS 'S' TO START GAME");
                    StdDraw.setFont(directionFont);
                    StdDraw.text(WIDTH * 0.5, HEIGHT * 0.65, "LENGTH MUST BE LESS THAN 10 DIGITS");
                    StdDraw.setFont(numberFont);
                    StdDraw.text(WIDTH * 0.5, HEIGHT * 0.5, seedString);
                    StdDraw.show();
                } else if (c == 's') {
                    long seed = Long.valueOf(seedString);
                    return seed;
                }
            }
        }

    }



    //dont know how this work but w/e
    private static Map loadGame() {
        File f = new File("./world.ser");
            if (!f.exists()) {
                return null;
        } else {
            try {
                FileInputStream fs = new FileInputStream(f);
                ObjectInputStream os = new ObjectInputStream(fs);
                Map loadGame = (Map) os.readObject();
                os.close();
                return loadGame;
            } catch (FileNotFoundException e) {

                return null;

            } catch (IOException e) { //this might not happen probably
                System.out.println(e);
                System.exit(0);
            } catch (ClassNotFoundException e) {
                System.out.println("class not found");
                System.exit(0);
            }
        }
        //no world saved yet, do nothing and stay at main menu

        //OR ACT AS IF NEW GAME WAS ENTERED
        return null;
    }
    private static void saveWorld(Map runGame) {
        File f = new File("./world.ser");
        try {
            if (!f.exists()) {
                f.createNewFile();
            }
            FileOutputStream fs = new FileOutputStream(f);
            ObjectOutputStream os = new ObjectOutputStream(fs);
            os.writeObject(runGame);
            os.close();
        }  catch (FileNotFoundException e) {
            System.out.println("file not found");
            System.exit(0);
        } catch (IOException e) {
            System.out.println(e);
            System.exit(0);
        }
    }
    private Map startNewGame(long seed) {
        Random random = new Random(seed);
        Map runGame = new Map();
        runGame.setRANDOM(random);

        //runGame.RANDOM = random;

        runGame.generateMap1(random);
        return runGame;
    }

    //finish anything related to gameplay here
    private void playGame(Map runGame, TERenderer ter) {
        while (true) {
            StdDraw.enableDoubleBuffering();

            //revert = key before render
            keycommands(runGame);
            ter.renderFrame(runGame.world);
            draweverything(runGame);
        }
    }

    /*
    private void showtile(Map map) {
        int x = (int) Math.round(StdDraw.mouseX());
        int y = (int) Math.round(StdDraw.mouseY());
        StdDraw.setPenColor(StdDraw.WHITE);
        if (x < 0 || x > WIDTH - 1 || y < 0 || y > HEIGHT - 1 ) {//add - 10 later for space) {
            return;
        }
        if (map.world[x][y] == Tileset.FLOOR) {
            StdDraw.text(5, 35, "THIS TILE IS A: floor");
        }
        if (map.world[x][y] == Tileset.WALL) {
            StdDraw.text(5, 35, "THIS TILE IS A: wall");
        }
        if (map.world[x][y] == Tileset.NOTHING) {
            StdDraw.text(5, 35, "THIS TILE IS: nothing");
        }

        StdDraw.show();
        StdDraw.pause(100);

    }
    */

    private void showtile(Map map) {
        int x = (int) StdDraw.mouseX();
        int y = (int) StdDraw.mouseY();
        StdDraw.setPenColor(StdDraw.WHITE);
        if (x < 0 || x > WIDTH || y < 0 || y > HEIGHT - 1) {
            return;
        }
        if (map.world[x][y].equals(map.FLOOR)) {
            StdDraw.text(5, 35, "THIS TILE IS A: floor");
        }
        if (map.world[x][y].equals(map.WALL)) {
            StdDraw.text(5, 35, "THIS TILE IS A: wall");
        }
        if (map.world[x][y].equals(map.NOTHING)) {
            StdDraw.text(5, 35, "THIS TILE IS: nothing");
        }

        if (map.world[x][y].equals(map.PLAYER)) {
            StdDraw.text(5, 35, "THIS TILE IS: you!");
        }

        if (map.world[x][y].equals(map.DOT)) {
            StdDraw.text(5, 35, "THIS TILE IS: YOUR ENEMY! >:(");
        }

    }

/*
    private boolean checkwall(Map map, TETile[][] world, Cursor temp, Cursor p) {
        if (world[p.x][p.y] == Tileset.WALL) {
            return true;
        }
        draweverything(world, map, temp);
        return false;
    }
    */

    //CHECKS IF NEXT POSITION IS A WALL TO NOT GO THROUGH THE WALL
    //cursor temp1 is players old position, cursor p is players updated position
    //cursor temp2 is dots old position, cursor d is dots updated position
    private boolean checkwallplayer(Map runGame, Cursor p) {
        TETile[][] world = runGame.world;
        if (p.x >= WIDTH || p.y >= HEIGHT) {
            return true;
        }
        if (world[p.x][p.y].equals(runGame.WALL)) {
            return true;
        }

        return false;
    }

    private void draweverything(Map map) {
        StdDraw.setPenColor(StdDraw.WHITE);
        Font defaultFont = new Font("San Serif", Font.PLAIN, 16);
        Font title = new Font("San Serif", Font.PLAIN, 40);
        StdDraw.text(5, 37, "SCOREBOARD: " + map.score);
        StdDraw.setFont(title);
        StdDraw.text(30, 37, "LILITH");
        StdDraw.setFont(defaultFont);
        StdDraw.text(30, 34, "(CATCH THEM!)");
        StdDraw.text(50, 37, " " + new Date());
        StdDraw.text(50, 35, "W - MOVE UP");
        StdDraw.text(50, 34, "S - MOVE DOWN");
        StdDraw.text(50, 33, "D - MOVE RIGHT");
        StdDraw.text(50, 32, "A - MOVE LEFT");
        StdDraw.text(50, 31, ":Q - QUIT AND SAVE GAME");
        showtile(map);
        StdDraw.show();
        StdDraw.pause(180);
    }

/*
    //added exit command for key presses : then q
    private void keycommands(Map runGame) {
        if (StdDraw.hasNextKeyTyped()) {
            Cursor temp = new Cursor(runGame.player.getpCur());
            char c = StdDraw.nextKeyTyped();
            switch (c) {
                case 'w':
                    runGame.player.up();
                    if (checkwall(runGame, runGame.world, temp, runGame.player)) {
                        runGame.player.down();
                    }
                    break;
                case 's':
                    runGame.player.down();
                    if (checkwall(runGame, runGame.world, temp, runGame.player)) {
                        runGame.player.up();
                    }
                    break;
                case 'a':
                    runGame.player.left();
                    if (checkwall(runGame, runGame.world, temp, runGame.player)) {
                        runGame.player.right();
                    }
                    break;
                case 'd':
                    runGame.player.right();
                    if (checkwall(runGame, runGame.world, temp, runGame.player)) {
                        runGame.player.left();
                    }
                    break;
                case ':':
                    if (isQ()) {

                        //save world
                        saveWorld(runGame);

                        System.exit(0);
                    }
            }
        }
    }

*/


    private void keycommands(Map runGame) {
        if (StdDraw.hasNextKeyTyped()) {
            Cursor temp1 = new Cursor(runGame.player.getpCur());
            char c = StdDraw.nextKeyTyped();
            switch (c) {
                case 'W':
                case 'w':
                    runGame.player.up();
                    break;
                case 'S':
                case 's':
                    runGame.player.down();
                    break;
                case 'A':
                case 'a':
                    runGame.player.left();
                    break;
                case 'D':
                case 'd':
                    runGame.player.right();
                    break;
                case ':':
                    if (StdDraw.hasNextKeyTyped()) {
                        char q = StdDraw.nextKeyTyped();
                        if (q == 'Q' || q ==  'q') {
                            saveWorld(runGame);
                            System.exit(0);
                            break;

                        } else {
                            break;
                        }
                    }
                    break;

                default:
            }

            if (checkwallplayer(runGame, runGame.player)) {
                runGame.player = temp1;
            }
            runGame.movealldots();
            runGame.generateMap2(runGame.world, temp1);
        }
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
        Character first = input.charAt(0);

        //3 DIFFERENT ROUTES DEPENDING ON STARTING LETTER

        //NEW GAME
        if (charCheck(first, 'n')) {
            long seed = Long.valueOf(getSeed(input));
            Map runGame = startNewGame(seed);
            String commands = snipSeed(input);

         /*   while (i < commands.length()) {
                if (saveQuitCheck(i, commands)) {
                    saveWorld(runGame);
                    return runGame.world;
                }
                stringCommands(current, runGame)
                i += 1;
                if (i == input.length()){
                    break;
                }
                current = commands.charAt(i);
            }
           */
            for (int i = 0; i < commands.length(); i += 1) {
                char current = commands.charAt(i);
                if (saveQuitCheck(i, commands)) {
                    saveWorld(runGame);
                    return runGame.world;
                }
                stringCommands(current, runGame);
            }

            //no :q sequence
            return runGame.world;

        } else if (charCheck(first, 'l')) { //loading
            Map runGame = loadGame();
            String commands = snipLoad(input);
            for (int i = 0; i < commands.length(); i += 1) {
                char current = commands.charAt(i);
                if (saveQuitCheck(i, commands)) {
                    saveWorld(runGame);
                    return runGame.world;
                }
                stringCommands(current, runGame);
            }
            return runGame.world;
        } else if (first == 'q' || first == 'Q') { //quit
            return null;
        } else {
            return null;
        }
    }


    //scan input string for quitting sequence :q
    private boolean saveQuitCheck(int i, String input) {
        int last = input.length() - 1;
        int sToLast = input.length() - 2;
        if (i != sToLast) {
            return false; //terminates check early
        } else if (input.charAt(sToLast) == ':' && input.charAt(last) == 'q') {
            return true;
        }
        return false;
    }
    private void stringCommands(char c, Map runGame) {
        Cursor temp1 = new Cursor(runGame.player.getpCur());
        if (charCheck(c, 'w')) {
            runGame.player.up();
        } else if (charCheck(c, 's')) {
            runGame.player.down();
        } else if (charCheck(c, 'd')) {
            runGame.player.right();
        } else if (charCheck(c, 'a')) {
            runGame.player.left();
        }
        if (checkwallplayer(runGame, runGame.player)) {
            runGame.player = temp1;
        }
        runGame.movealldots();

        //line might not be needed
        runGame.generateMap2(runGame.world, temp1);


    }
    //check both upper and lower cases of checkedChar against keyEntered
    private boolean charCheck(char keyEntered, char checkedChar) {
        char upCase = Character.toUpperCase(checkedChar);
        return keyEntered == checkedChar || keyEntered == upCase;
    }
    private String getSeed(String input) {
        String seedString = "";
        int i = 1; //skip first letter N
        while (input.charAt(i) != 's' && input.charAt(i) != 'S') {
            seedString += Character.toString(input.charAt(i));
            i += 1;
        }
        return seedString;
    }
    private String snipSeed(String input) {
        String seedString = "";
        int i = 0; //skip first letter N
        while (input.charAt(i) != 's' && input.charAt(i) != 'S') {
            seedString += Character.toString(input.charAt(i));
            i += 1;
        }
        seedString += Character.toString(input.charAt(i)); //string includes n and s
        String truncated = input;
        return truncated.replace(seedString, "");
    }
    private String snipLoad(String input) {
        char firstLetter = input.charAt(0);
        String removing = Character.toString(firstLetter);
        return input.replace(removing, "");
    }





    public static void main(String[] args) {
        TERenderer ter = new TERenderer();
        ter.initialize(Game.WIDTH, Game.HEIGHT);
        Game test = new Game();
        TETile[][] world = test.playWithInputString("n4820759759604654444sws");


        //test.playWithInputString("n4518992736225145867s");

        //TETile[][] worldLoading = test.playWithInputString("lsss");

        //ter.renderFrame(world);
        test.playWithKeyboard();

    }


}