package com.mindstorm.cube_solver;

public class Cube {

    public enum Color {
        WHITE, RED, GREEN, ORANGE, BLUE, YELLOW, ANY
    }

    final public static int UP = 0, RIGHT = 1, FACE = 2, DOWN = 3, LEFT = 4, BACK = 5;

    private final int centerPosition = 4;

    private Color side[][];

    private int bottomPosition = DOWN;
    private int rotation = 0; // 0,1,2,3

    Cube() {
        init();
    }

    int getRotation() {
        return rotation;
    }

    int getNextClockwiseRotation() {
        int rotate = rotation + 1;
        if (rotate == 4) rotate = 0;

        return rotate;
    }

    int getNextCounterClockwiseRotation() {
        int rotate = rotation - 1;
        if (rotate == -1) rotate = 3;

        return rotate;
    }

    void rotateClockwise() {
        rotation = getNextClockwiseRotation();
    }

    void rotateCounterClockwise() {
        rotation = getNextCounterClockwiseRotation();
    }

    private void init() {
        side = new Color[6][9];
        for (int i = 0; i < 6; i++)
            for (int j = 0; j < 9; j++)
                side[i][j] = Color.ANY;
    }

    public String toScrambleString() {
        StringBuilder builder = new StringBuilder(54);
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 9; j++) {
                builder.append(getScrambleChar(side[i][j]));
            }
        }

        return builder.toString();
    }

    public int getBottomPosition() {
        return bottomPosition;
    }

    public void setBottomPosition(int bottomPosition) {
        this.bottomPosition = bottomPosition;
    }

    public void setField(int side, int position, Color color) {
        this.side[side][position] = color;
    }

    private char getScrambleChar(Color color) {

        if (color == side[UP][centerPosition]) {
            return 'U';
        }

        if (color == side[RIGHT][centerPosition]) {
            return 'R';
        }

        if (color == side[FACE][centerPosition]) {
            return 'F';
        }

        if (color == side[DOWN][centerPosition]) {
            return 'D';
        }

        if (color == side[LEFT][centerPosition]) {
            return 'L';
        }

        if (color == side[BACK][centerPosition]) {
            return 'B';
        }

        return 'A';
    }

    public static int getSideFromChar(char c) {
        switch (c) {
            case 'U':
                return UP;
            case 'R':
                return RIGHT;
            case 'F':
                return FACE;
            case 'D':
                return DOWN;
            case 'L':
                return LEFT;
            case 'B':
                return BACK;
            default:
                return Integer.MAX_VALUE;
        }
    }

    public void setNextPositionByFlip() {
        setBottomPosition(getNextPositionByFlip());
    }

    public int getNextPositionByFlip() {
        return getNextPositionByFlip(getBottomPosition(), rotation);
    }

    public static int getNextPositionByFlip(int side, int rotation) {
        switch (side) {
            case UP:
                switch (rotation) {
                    case 0:
                        return FACE;
                    case 1:
                        return LEFT;
                    case 2:
                        return BACK;
                    case 3:
                        return RIGHT;
                }
                break;
            case FACE:
                switch (rotation) {
                    case 0:
                        return DOWN;
                    case 1:
                        return LEFT;
                    case 2:
                        return UP;
                    case 3:
                        return RIGHT;
                }
                break;
            case DOWN:
                switch (rotation) {
                    case 0:
                        return BACK;
                    case 1:
                        return LEFT;
                    case 2:
                        return FACE;
                    case 3:
                        return RIGHT;
                }
                break;
            case RIGHT:
                switch (rotation) {
                    case 0:
                        return BACK;
                    case 1:
                        return DOWN;
                    case 2:
                        return FACE;
                    case 3:
                        return UP;
                }
            case LEFT:
                switch (rotation) {
                    case 0:
                        return BACK;
                    case 1:
                        return UP;
                    case 2:
                        return FACE;
                    case 3:
                        return DOWN;
                }
                break;
            case BACK:
                switch (rotation) {
                    case 0:
                        return UP;
                    case 1:
                        return LEFT;
                    case 2:
                        return DOWN;
                    case 3:
                        return RIGHT;
                }
                break;
        }

        return UP;
    }
}
