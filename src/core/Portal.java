package core;

import tileengine.TETile;
import tileengine.Tileset;

public class Portal {
    private int x;
    private int y;
    public static final TETile FLOWER = Tileset.FLOWER;

    public Portal(World world, int x, int y) {
        this.x = x;
        this.y = y;
        world.getWorld()[x][y] = FLOWER;
    }

    public int X() {
        return x;
    }
    public int Y() {
        return y;
    }
}
