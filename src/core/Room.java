package core;

public class Room implements Comparable<Room> {

    // Determines the length (including all walls)
    private int length = 0;
    // Determines the width (including all walls)
    private int width = 0;
    // Determines the initial point where the room expands from (bottom left corner of the room)
    private int x;
    private int y;
    public static final int MAX_ROOM_LENGTH = 10;
    public static final int MAX_ROOM_WIDTH = 10;
    public static final int MIN_ROOM_LENGTH = 5;
    public static final int MIN_ROOM_WIDTH = 5;

    public Room(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return this.width;
    }

    public int getLength() {
        return this.length;
    }

    // Increments the room length by 1
    public void incrementLength() {
        this.length++;
    }

    // Increments the room width by 1
    public void incrementWidth() {
        this.width++;
    }

    @Override
    public int compareTo(Room otherRoom) {
        if (this.x != otherRoom.x) {
            return Integer.compare(this.x, otherRoom.x);
        }
        // If x coordinates are equal, compare y coordinates
        return Integer.compare(this.y, otherRoom.y);
    }
}
