package core;

import edu.princeton.cs.algs4.StdDraw;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

import static core.World.*;

public class Main {
    public static final int SQUARE_WIDTH = 16;
    public static final int SQUARE_HEIGHT = 16;
    public static final double RECTANGLE_START = 0.4;
    public static final double RECTANGLE_END = 0.6;
    public static final String SEED = "n328795093287435s";
    public static final String SAVE_FILE = "src/savedWorlds/save.txt";
    public static final double TITLE_Y = 0.8;
    public static final double START_Y = 0.6;
    public static final double GAME_OVER_X = 45;
    public static final double GAME_OVER_Y = 25;
    public static final double QUIT_Y = 0.4;

    public static final int TITLE_SIZE = 36;
    public static final int EIGHTEEN = 18;
    public static final int SUBTITLE_SIZE = 24;
    public static final double BOX1_S = 0.62;
    public static final double BOX1_E = 0.57;
    public static final double BOX2_S = 0.52;
    public static final double BOX2_E = 0.47;
    public static final double BOX3_S = 0.42;
    public static final double BOX3_E = 0.37;

    public static char mainMenu() {
        int canvasWidth = DEFAULT_WIDTH * SQUARE_WIDTH;
        int canvasHeight = DEFAULT_HEIGHT * SQUARE_HEIGHT;
        StdDraw.setCanvasSize(canvasWidth, canvasHeight);
        StdDraw.enableDoubleBuffering();

        boolean gameStarted = false;
        ArrayList<Character> possibleKeys = new ArrayList<>(Arrays.asList('l', 'L', 'q', 'Q', 'n', 'N'));
        char key = ' ';
        // Main menu loop
        while (!gameStarted) {
            // Clear the canvas
            StdDraw.clear(StdDraw.BLACK);

            // Draw title with yellow rectangle
            StdDraw.setPenColor(StdDraw.WHITE);
            StdDraw.setFont(new Font("Monospaced", Font.BOLD, TITLE_SIZE));
            StdDraw.text(0.5, TITLE_Y, "CS61B: THE GAME");

            StdDraw.setPenColor(StdDraw.WHITE);
            StdDraw.setFont(new Font("Monospaced", Font.PLAIN, SUBTITLE_SIZE));
            StdDraw.text(0.5, START_Y, "START WORLD (N)");
            StdDraw.text(0.5, 0.5, "LOAD WORLD (L)");
            StdDraw.text(0.5, QUIT_Y, "QUIT (Q)");

            // Display instructions for navigating the menu
            StdDraw.setFont(new Font("Monospaced", Font.PLAIN, EIGHTEEN));

            // Show the menu
            StdDraw.show();

            // Check for user input
            // Key press
            if (StdDraw.hasNextKeyTyped()) {
                key = StdDraw.nextKeyTyped();
            }

            // Mouse press
            if (StdDraw.isMousePressed()) {
                double xlocation = StdDraw.mouseX();
                double ylocation = StdDraw.mouseY();
                if (xlocation > RECTANGLE_START && xlocation < RECTANGLE_END) {
                    if (ylocation <= BOX1_S && ylocation >= BOX1_E) {
                        key = 'n';
                    }
                    if (ylocation <= BOX2_S && ylocation >= BOX2_E) {
                        key = 'l';
                    }
                    if (ylocation <= BOX3_S && ylocation >= BOX3_E) {
                        key = 'q';
                    }
                }
            }

            if (possibleKeys.contains(key)) {
                gameStarted = true;
            }
        }

        return key;
    }

    public static void endGame() {
        System.exit(0);
    }
    
    public static void main(String[] args) {
        char choice = mainMenu();
        if (choice == 'N' || choice == 'n') {
            String seedString = "";
            StdDraw.clear(Color.black);
            StdDraw.setPenColor(StdDraw.WHITE);
            while (choice != 'S' && choice != 's') {
                if (StdDraw.hasNextKeyTyped()) {
                    choice = StdDraw.nextKeyTyped();
                    if (choice != 'S' && choice != 's') {
                        seedString += choice;
                    }
                }
                StdDraw.clear(Color.black);
                StdDraw.text(0.5, START_Y + 0.2, "Enter seed, then press S: ");
                StdDraw.text(0.5, START_Y, seedString);
                StdDraw.show();
            }
            // String seedString = SEED.substring(1, SEED.length() - 1);
            System.out.println(seedString);
            long seed = Long.parseLong(seedString);
            Game game = new Game(seed);
            game.startGame();
        } else if (choice == 'L' || choice == 'l') {
            Game game = new Game(SAVE_FILE);
            game.startGame();
        } else if (choice == 'Q' || choice == 'q') {
            System.exit(0);
        }
        endGame();
    }
}
