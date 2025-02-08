package core;

import tileengine.TETile;
import tileengine.Tileset;

public class Avatar {
    private int x;
    private int y;
    private boolean teleport = false;
    private boolean coinCollected = false;
    public static final TETile AVATAR_TILE = Tileset.AVATAR;

    public Avatar(World world, int x, int y) {
        this.x = x;
        this.y = y;
        world.getWorld()[x][y] = AVATAR_TILE;
    }

    public void tryMove(TETile[][] world, int targetX, int targetY) {
        if (world[targetX][targetY] == Tileset.FLOOR) {
            coinCollected = false;
            world[this.x][this.y] = Tileset.FLOOR;
            world[targetX][targetY] = AVATAR_TILE;
            this.x = targetX;
            this.y = targetY;
        } else if (world[targetX][targetY] == Tileset.FLOWER) {
            teleport = true;
        } else if (world[targetX][targetY] == Tileset.COIN) {
            coinCollected = true;
            world[this.x][this.y] = Tileset.FLOOR;
            world[targetX][targetY] = AVATAR_TILE;
            this.x = targetX;
            this.y = targetY;
        }
    }

    public boolean teleport() {
        return teleport;
    }
    public void setTeleportFalse() {
        teleport = false;
    }
    public int X() {
        return x;
    }
    public int Y() {
        return y;
    }

    public boolean isCoinCollected() {
        return coinCollected;
    }
}
