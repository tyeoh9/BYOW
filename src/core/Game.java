package core;

import edu.princeton.cs.algs4.StdDraw;
import tileengine.TERenderer;
import tileengine.TETile;
import tileengine.Tileset;

import java.awt.*;
import java.util.ArrayList;
import java.util.Objects;

import static utils.FileUtils.readFile;
import static utils.FileUtils.writeFile;

import static core.World.DEFAULT_HEIGHT;
import static core.World.DEFAULT_WIDTH;

public class Game {

    private static final int STARTING_COINS = 17;
    private static int TOTAL_COINS = 0;
    private long seed;
    private World world;
    private final int height;
    private final int width;
    private boolean isGameOver;
    private boolean mainGame;
    private boolean portalEntered;
    private static final int TITLE_FONT_SIZE = 18;
    private static final int COORDINATES_X = 85;
    private static final int SCORE_X = 75;
    private final long SIDE_LEVEL_SEED = 109482480284092L;
    private boolean colonPressed;
    private Avatar avatar;
    private static final String SAVE_FILE = "src/savedWorlds/save.txt";
    private static final String MAIN_GAME = "src/savedWorlds/mainGame.txt";
    private static final String SIDE_LEVEL = "src/savedWorlds/sideLevel.txt";
    private static final String SIDE_LEVEL_EDITABLE = "src/savedWorlds/sideLevelEdit.txt";
    private Portal portal;
    private ArrayList<Coin> coins = new ArrayList<>();

    public Game(long seed) {
        // Initialize world
        this.height = DEFAULT_HEIGHT;
        this.width = DEFAULT_WIDTH;
        this.seed = seed;
        this.world = new World(seed);
        if (seed != SIDE_LEVEL_SEED) {
            mainGame = true;
        } else {
            mainGame = false;
        }
        isGameOver = false;
        colonPressed = false;

        // Initialize avatar and place in random room
        Room avatarRoom = world.getRandomRoom();
        this.avatar = new Avatar(world, avatarRoom.getX() + 1, avatarRoom.getY() + 1);
        Room portalRoom = world.getRandomRoom();
        // Initialize portal and place in random room
        this.portal = new Portal(world, portalRoom.getX() + portalRoom.getWidth() - 2,
                portalRoom.getY() + portalRoom.getLength() - 2);
    }

    public Game(String filename) {
        this.world = loadWorld(filename);
        if (Objects.equals(filename, MAIN_GAME)) {
            mainGame = true;
        } else if (Objects.equals(filename, SIDE_LEVEL)) {
            mainGame = false;
            // Generate coins
            Room coin1Room = world.getRandomRoom();
            Room coin2Room = world.getRandomRoom();
        }
        this.height = DEFAULT_HEIGHT;
        this.width = DEFAULT_WIDTH;
        isGameOver = false;
        portalEntered = false;
        colonPressed = false;
    }

    public void startGame() {
        TERenderer ter = new TERenderer();
        ter.initialize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        while (!isGameOver) {
            updateBoard();
            renderWorld(ter);
        }
    }

    public void renderWorld(TERenderer ter) {
        ter.drawTiles(world.getWorld());
        mouseHover();
        StdDraw.show();
    }

    public void mouseHover() {
        int currentX = (int) Math.floor(StdDraw.mouseX());
        int currentY = (int) Math.floor(StdDraw.mouseY());
        if (currentX < width && currentY < height) {
            TETile currTile = world.getTile(currentX, currentY);
            StdDraw.setPenColor(StdDraw.WHITE);
            StdDraw.setFont(new Font("Monospaced", Font.PLAIN, TITLE_FONT_SIZE));

            // Draw new text based on the current tile
            StdDraw.setPenColor(StdDraw.WHITE);
            if (currTile == Tileset.NOTHING) {
                StdDraw.text(3, 1, "Void");
            } else if (currTile == Tileset.WALL) {
                StdDraw.text(3, 1, "Wall");
            } else if (currTile == Tileset.FLOOR) {
                StdDraw.text(3, 1, "Floor");
            } else if (currTile == Tileset.FLOWER) {
                StdDraw.text(3, 1, "Portal");
            } else if (currTile == Tileset.COIN) {
                StdDraw.text(3, 1, "Treasure");
            }
            StdDraw.text(COORDINATES_X, 1, "X: " + currentX + "  Y: " + currentY);
            StdDraw.text(SCORE_X, 1, "Score: " + (TOTAL_COINS - coins.size()));
        }
    }

    private void updateBoard() {
        if (StdDraw.hasNextKeyTyped()) {
            char key = StdDraw.nextKeyTyped();
            command(key);
            if (avatar.teleport()) {
                teleport();
            } else if (avatar.isCoinCollected()) {
                clearCoin(avatar.X(), avatar.Y());
            }
        }
    }

    public void command(char key) {
        if (key == 'w' || key == 'W') {
            avatar.tryMove(world.getWorld(), avatar.X(), avatar.Y() + 1);
        } else if (key == 'a' || key == 'A') {
            avatar.tryMove(world.getWorld(), avatar.X() - 1, avatar.Y());
        } else if (key == 's' || key == 'S') {
            avatar.tryMove(world.getWorld(), avatar.X(), avatar.Y() - 1);
        } else if (key == 'd' || key == 'D') {
            avatar.tryMove(world.getWorld(), avatar.X() + 1, avatar.Y());
        } else if (key == ':') {
            colonPressed = true;
        } else if (colonPressed) {
            if (key == 'q' || key == 'Q') {
                saveWorld(SAVE_FILE);
                isGameOver = true;
            } else {
                colonPressed = false;
            }
        }
    }

    public void saveWorld(String saveFile) {
        String board = Long.toString(seed);
        board += "\n";
        board += avatar.X() + " " + avatar.Y() + "\n";
        board += portal.X() + " " + portal.Y() + "\n";
        if (!saveFile.equals(MAIN_GAME)) {
            for (Coin coin : coins) {
                board += coin.X() + " " + coin.Y() + "\n";
            }
        }
        writeFile(saveFile, board);
    }

    public World loadWorld(String filename) {
        String readString = readFile(filename);
        String[] data = readString.split("\n");
        this.seed = Long.parseLong(data[0]);
        int avatarX = Integer.parseInt(data[1].split(" ")[0]);
        int avatarY = Integer.parseInt(data[1].split(" ")[1]);
        int portalX = Integer.parseInt(data[2].split(" ")[0]);
        int portalY = Integer.parseInt(data[2].split(" ")[1]);
        World newWorld = new World(seed);

        // Load coins
        if (seed != SIDE_LEVEL_SEED) {
            mainGame = true;
        } else {
            mainGame = false;
            loadCoins(newWorld, data);
        }
        this.avatar = new Avatar(newWorld, avatarX, avatarY);
        this.portal = new Portal(newWorld, portalX, portalY);
        return newWorld;
    }

    public void loadCoins(World newWorld, String[] data) {
        for (int i = 3; i < data.length; i++) {
            int coinX = Integer.parseInt(data[i].split(" ")[0]);
            int coinY = Integer.parseInt(data[i].split(" ")[1]);
            if (coinX != -1 && coinY != -1) {
                Coin coin = new Coin(newWorld, coinX, coinY);
                coins.add(coin);
            }
        }
        TOTAL_COINS = STARTING_COINS;
    }

    public TETile[][] getWorldTiles() {
        return world.getWorld();
    }

    public void teleport() {
        avatar.setTeleportFalse();
        if (mainGame && !portalEntered) {
            saveWorld(MAIN_GAME);
            world = loadWorld(SIDE_LEVEL);

            // Initialize portal and place in random room
            mainGame = false;
            portalEntered = true;
        } else if (mainGame) {
            saveWorld(MAIN_GAME);
            world = loadWorld(SIDE_LEVEL_EDITABLE);

            // Initialize portal and place in random room
            mainGame = false;
            portalEntered = true;
        } else {
            saveWorld(SIDE_LEVEL_EDITABLE);
            world = loadWorld(MAIN_GAME);
            mainGame = true;
        }
    }

    public void clearCoin(int x, int y) {
        Coin coinToRemove = null;
        for (Coin coin : coins) {
            if (coin.X() == x && coin.Y() == y) {
                coinToRemove = coin;
                break;
            }
        }
        if (coinToRemove != null) {
            coins.remove(coinToRemove);
        }
    }
}
