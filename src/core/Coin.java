package core;

import tileengine.TETile;
import tileengine.Tileset;

public class Coin {
    private int x;
    private int y;
    public static final TETile COIN = Tileset.COIN;

    public Coin(World world, int x, int y) {
        this.x = x;
        this.y = y;
        world.getWorld()[x][y] = COIN;
    }

    public int X() {
        return x;
    }

    public int Y() {
        return y;
    }

    public void setCoinPosition(int currX, int currY) {
        this.x = currX;
        this.y = currY;
    }
}
