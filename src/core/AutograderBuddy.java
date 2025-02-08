package core;

import tileengine.TETile;
import tileengine.Tileset;

public class AutograderBuddy {

    /**
     * Simulates a game, but doesn't render anything or call any StdDraw
     * methods. Instead, returns the world that would result if the input string
     * had been typed on the keyboard.
     *
     * Recall that strings ending in ":q" should cause the game to quit and
     * save. To "quit" in this method, save the game to a file, then just return
     * the TETile[][]. Do not call System.exit(0) in this method.
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */

    private static final String SAVE_FILE = "save.txt";

    public static TETile[][] getWorldFromInput(String input) {
        String seedString = "";
        String commands;
        int i = 0;
        Game game;
        if (input.charAt(0) == 'N' || input.charAt(0) == 'n') {
            while (i < input.length() && input.charAt(i) != 'S' && input.charAt(i) != 's') {
                seedString += input.charAt(i);
                i++;
            }
            seedString += input.charAt(i);
            commands = input.substring(++i);
            long seed = Long.parseLong(seedString.substring(1, seedString.length() - 1));
            game = new Game(seed);
        } else {
            commands = input.substring(i + 1);
            game = new Game(SAVE_FILE);
        }

        // If there are commands to execute
        while (!commands.isEmpty()) {
            if (commands.startsWith("L") || commands.startsWith("l")) {
                game = new Game(SAVE_FILE);
            } else if (commands.startsWith(":")) {
                game.command(':');
                commands = commands.substring(1);
                if (commands.startsWith("Q") || commands.startsWith("1")) {
                    game.command('q');
                    commands = commands.substring(1);
                }
            } else {
                game.command(commands.charAt(0));
                commands = commands.substring(1);
            }
        }

        return game.getWorldTiles();
    }


    /**
     * Used to tell the autograder which tiles are the floor/ground (including
     * any lights/items resting on the ground). Change this
     * method if you add additional tiles.
     */
    public static boolean isGroundTile(TETile t) {
        return t.character() == Tileset.FLOOR.character()
                || t.character() == Tileset.AVATAR.character()
                || t.character() == Tileset.FLOWER.character();
    }

    /**
     * Used to tell the autograder while tiles are the walls/boundaries. Change
     * this method if you add additional tiles.
     */
    public static boolean isBoundaryTile(TETile t) {
        return t.character() == Tileset.WALL.character()
                || t.character() == Tileset.LOCKED_DOOR.character()
                || t.character() == Tileset.UNLOCKED_DOOR.character();
    }
}

