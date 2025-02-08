package core;

import tileengine.TETile;
import tileengine.Tileset;

import java.util.*;

public class World {
    private TETile[][] world;
    private Graph<Room> graph;
    public static final int DEFAULT_WIDTH = 90;
    public static final int DEFAULT_HEIGHT = 45;
    public static final int MIN_ROOMS = 15;
    public static final int MAX_ROOMS = 25;
    private int worldWidth;
    private int worldHeight;
    private Random random;
    private ArrayList<Room> allRooms;

    public World(int w, int h, Long seed) {
        graph = new Graph<>();
        random = new Random(seed);
        worldWidth = w;
        worldHeight = h;
        world = new TETile[worldWidth][worldHeight];
        allRooms = new ArrayList<>();
        fillWithNothing(world);
        createRooms(random.nextInt(MIN_ROOMS, MAX_ROOMS));
        connectRooms();
        createHallways();
        drawWalls();
    }
    public World(Long seed) {
        this(DEFAULT_WIDTH, DEFAULT_HEIGHT, seed);
    }

    public void fillWithNothing(TETile[][] tiles) {
        for (int x = 0; x < worldWidth; x++) {
            for (int y = 0; y < worldHeight; y++) {
                tiles[x][y] = Tileset.NOTHING;
            }
        }
    }

    public void drawRoom(Room room) {
        int x = room.getX();
        int y = room.getY();
        for (int w = 0; w < room.getWidth(); w++) {
            for (int l = 0; l < room.getLength(); l++) {
                if (isWall(room, x + w, y + l)) {
                    world[x + w][y + l] = Tileset.WALL;
                } else {
                    world[x + w][y + l] = Tileset.FLOOR;
                }
            }
        }
    }

    public boolean isWall(Room room, int x, int y) {
        if (x == room.getX() || x == room.getX() + room.getWidth() - 1
                || y == room.getY() || y == room.getY() + room.getLength() - 1) {
            return true;
        }
        return false;
    }

    public int randomX() {
        return random.nextInt(worldWidth);
    }

    public int randomY() {
        return random.nextInt(worldHeight);
    }

    public Room createRoom() {
        //Setting initial position for Room
        Room newRoom = new Room(randomX(), randomY());
        int minLength = Room.MIN_ROOM_LENGTH;
        int maxLength = Room.MAX_ROOM_LENGTH;
        int minWidth = Room.MIN_ROOM_WIDTH;
        int maxWidth = Room.MAX_ROOM_WIDTH;
        int targetLength = random.nextInt(minLength, maxLength + 1);
        int targetWidth = random.nextInt(minWidth, maxWidth + 1);
        while (!validPoint(newRoom.getX(), newRoom.getY(), targetLength, targetWidth)) {
            newRoom = new Room(randomX(), randomY());
        }
        expandRoom(newRoom, targetLength, targetWidth);
        drawRoom(newRoom);
        return newRoom;
    }

    public boolean validPoint(int x, int y, int targetLength, int targetWidth) {
        // Check if there's enough space horizontally and vertically for our desired-sized room
        if (x + targetWidth > worldWidth || y + targetLength > worldHeight) {
            return false;
        }

        // Check each point within the target dimension grid
        for (int i = -1; i < targetWidth + 1; i++) {
            for (int j = -1; j < targetLength + 1; j++) {
                // Check if the point is within the world bounds
                if (x + i < 0 || x + i >= worldWidth || y + j < 0 || y + j >= worldHeight) {
                    return false;
                }
                if (world[x + i][y + j] != Tileset.NOTHING) {
                    return false;
                }
            }
        }

        return true;
    }

    public void createRooms(int numRooms) {
        for (int i = 0; i < numRooms; i++) {
            Room currRoom = createRoom();
            allRooms.add(currRoom);
        }
    }

    public void createHallways() {
        ArrayList<Room> connected = new ArrayList<>();
        for (Room room : allRooms) {
            for (Graph<Room>.Edge<Room> neighbor : graph.adj(room)) {
                if (!connected.contains(neighbor.destination)) {
                    if (canConnectStraight(room, neighbor.destination)) {
                        connectStraightHallway(room, neighbor.destination);
                    } else {
                        connectLHallway(room, neighbor.destination);
                    }
                }
            }
            connected.add(room);
        }
    }

    public void expandRoom(Room room, int length, int width) {
        int x = room.getX();
        int y = room.getY();
        for (int i = 0; i < width; i++) {
            if (x + i < worldWidth && world[x + i][y] == Tileset.NOTHING) {
                room.incrementWidth();
            } else {
                break;
            }
        }
        for (int j = 0; j < length; j++) {
            if (y + j < worldHeight && world[x][y + j] == Tileset.NOTHING) {
                room.incrementLength();
            } else {
                break;
            }
        }
    }

    public void connectRooms() {
        Graph<Room> completeGraph = new Graph<>();
        for (Room room1 : allRooms) {
            for (Room room2 : allRooms) {
                completeGraph.addEdge(room1, room2, getDistance(room1, room2));
            }
        }
        List<Graph<Room>.Edge<Room>> listOfEdges = completeGraph.primMST();
        for (Graph<Room>.Edge<Room> edge : listOfEdges) {
            graph.addEdge(edge);
        }
    }

    public int getDistance(Room a, Room b) {
        return Math.abs(a.getX() - b.getX()) + Math.abs(a.getY() - b.getY());
    }

    public ArrayList<Integer> findMutualX(Room a, Room b) {
        // Find left corner of our overlap range
        int leftMostX = Math.max(a.getX(), b.getX());
        // Find right corner of our overlap range
        int rightMostX = Math.min(a.getX() + a.getWidth(), b.getX() + b.getWidth());
        // X coordinates
        ArrayList<Integer> coordinates = new ArrayList<>();

        for (int i = leftMostX + 1; i < rightMostX; i++) {
            coordinates.add(i);
        }
        return coordinates;
    }

    public ArrayList<Integer> findMutualY(Room a, Room b) {
        // Find bottom corner of our overlap range
        int bottomMostX = Math.max(a.getY(), b.getY());
        // Find top corner of our overlap range
        int topMostX = Math.min(a.getY() + a.getLength() - 1, b.getY() + b.getLength() - 1);
        // X coordinates
        ArrayList<Integer> coordinates = new ArrayList<>();

        for (int i = bottomMostX + 1; i < topMostX; i++) {
            coordinates.add(i);
        }
        return coordinates;
    }

    public boolean verticallyAdjacent(Room a, Room b) {
        int aX = a.getX();
        int bX = b.getX();
        int aWidth = a.getWidth();
        int bWidth = b.getWidth();
        if (aX < bX) {
            return aX + aWidth - 2 >= bX + 1;
        } else {
            return bX + bWidth - 2 >= aX + 1;
        }
    }
    public boolean horizontallyAdjacent(Room a, Room b) {
        int aY = a.getY();
        int bY = b.getY();
        int aLength = a.getLength();
        int bLength = b.getLength();
        if (aY < bY) {
            return aY + aLength - 2 >= bY + 1;
        } else {
            return bY + bLength - 2 >= aY + 1;
        }
    }

    // Checks whether two rooms can be connected by a straight hallway
    public boolean canConnectStraight(Room a, Room b) {
        if (horizontallyAdjacent(a, b) || verticallyAdjacent(a, b)) {
            return true;
        }
        return false;
    }

    // Connects two rooms together with a straight hallway
    public void connectStraightHallway(Room a, Room b) {
        if (horizontallyAdjacent(a, b)) {
            ArrayList<Integer> yCoordinates = findMutualY(a, b);
            int doorway = yCoordinates.get(0);
            if (doorway != yCoordinates.get(yCoordinates.size() - 1)) {
                doorway = random.nextInt(yCoordinates.get(0), yCoordinates.get(yCoordinates.size() - 1));
            }
            Room leftRoom;
            Room rightRoom;
            if (a.getX() < b.getX()) {
                leftRoom = a;
                rightRoom = b;
            } else {
                leftRoom = b;
                rightRoom = a;
            }
            int startPoint = leftRoom.getX() + leftRoom.getWidth() - 1;
            int endPoint = rightRoom.getX();
            drawHorizontalHallway(startPoint, endPoint, doorway);
        } else if (verticallyAdjacent(a, b)) {
            ArrayList<Integer> xCoordinates = findMutualX(a, b);
            int doorway = xCoordinates.get(0);
            if (doorway != xCoordinates.get(xCoordinates.size() - 1)) {
                doorway = random.nextInt(xCoordinates.get(0), xCoordinates.get(xCoordinates.size() - 1));
            }
            Room bottomRoom;
            Room topRoom;
            if (a.getY() < b.getY()) {
                bottomRoom = a;
                topRoom = b;
            } else {
                bottomRoom = b;
                topRoom = a;
            }
            int startPoint = bottomRoom.getY() + bottomRoom.getLength() - 1;
            int endPoint = topRoom.getY();
            drawVerticalHallway(startPoint, endPoint, doorway);
        }
    }

    public void connectLHallway(Room a, Room b) {
        Room leftRoom;
        Room rightRoom;
        if (a.getX() < b.getX()) {
            leftRoom = a;
            rightRoom = b;
        } else {
            leftRoom = b;
            rightRoom = a;
        }
        if (rightRoom.getY() < leftRoom.getY()) {
            // Y coordinate
            int leftRoomDoorway = random.nextInt(leftRoom.getY() + 2, leftRoom.getY() + leftRoom.getLength() - 2);
            // X coordinate
            int rightRoomDoorway = random.nextInt(rightRoom.getX() + 1,
                    rightRoom.getX() + rightRoom.getWidth() - 1);
            drawHorizontalHallway(leftRoom.getX() + leftRoom.getWidth() - 1, rightRoomDoorway, leftRoomDoorway);
            drawVerticalHallway(rightRoom.getY() + rightRoom.getLength() - 1, leftRoomDoorway, rightRoomDoorway);
        } else {
            // Y coordinate
            int leftRoomDoorway = random.nextInt(leftRoom.getY() + 2,
                    leftRoom.getY() + leftRoom.getLength() - 2);
            // X coordinate
            int rightRoomDoorway = random.nextInt(rightRoom.getX() + 1,
                    rightRoom.getX() + rightRoom.getWidth() - 1);
            drawHorizontalHallway(leftRoom.getX() + leftRoom.getWidth() - 1, rightRoomDoorway, leftRoomDoorway);
            drawVerticalHallway(rightRoom.getY(), leftRoomDoorway, rightRoomDoorway);
        }
    }

    public void drawHorizontalHallway(int x1, int x2, int y) {
        int startX;
        int endX;
        if (x1 < x2) {
            startX = x1;
            endX = x2;
        } else {
            startX = x2;
            endX = x1;
        }
        world[startX][y] = Tileset.FLOOR;
        startX++;
        while (startX < endX) {
            world[startX][y] = Tileset.FLOOR;
            startX++;
        }
        world[startX][y] = Tileset.FLOOR;
    }
    public void drawVerticalHallway(int y1, int y2, int x) {
        int startY;
        int endY;
        if (y1 < y2) {
            startY = y1;
            endY = y2;
        } else {
            startY = y2;
            endY = y1;
        }
        world[x][startY] = Tileset.FLOOR;
        startY++;
        while (startY < endY) {
            world[x][startY] = Tileset.FLOOR;
            startY++;
        }
        world[x][startY] = Tileset.FLOOR;
    }

    public void drawWalls() {
        for (int h = 0; h < DEFAULT_HEIGHT; h++) {
            for (int w = 0; w < DEFAULT_WIDTH; w++) {
                for (int i = -1; i <= 1; i++) {
                    for (int j = -1; j <= 1; j++) {
                        if ((h + i >= 0) && (h + i < DEFAULT_HEIGHT) && (w + j >= 0)
                                && (w + j < DEFAULT_WIDTH) && (i != 0 || j != 0)
                                && (world[w + j][h + i] == Tileset.FLOOR) && (world[w][h] != Tileset.FLOOR)) {
                            world[w][h] = Tileset.WALL;
                        }
                    }
                }
            }
        }
    }
    public TETile[][] getWorld() {
        return world;
    }

    public TETile getTile(int x, int y) {
        return world[x][y];
    }

    public Room getRandomRoom() {
        return allRooms.get(random.nextInt(allRooms.size()));
    }
    public List<Room> getRooms() {
        return allRooms;
    }
}
