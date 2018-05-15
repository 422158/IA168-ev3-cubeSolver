package com.mindstorm.cube_solver;

public class Cube {

    public enum Color {
        WHITE, RED, GREEN, ORANGE, BLUE, YELLOW, ANY
    }

    final public static int UP = 0, RIGHT = 1, FACE = 2, DOWN = 3, LEFT = 4, BACK = 5;

    private final int centerPosition = 4;

    private Color side[][];

    private int currentRotation [];

    Cube() {
        init();
    }

    public int getCurrentPositionOfSide(int side) {
        for (int i = 0; i < 6; ++i)
            if (currentRotation[i] == side)
                return i;
        return -1;
    }



    public void changeRotationClockwise()
    {
        int tmp = currentRotation[2];
        currentRotation[2] = currentRotation[1];
        currentRotation[1] = currentRotation[5];
        currentRotation[5] = currentRotation[4];
        currentRotation[4] = tmp;
    }

    public void changeRotationCounterClockwise()
    {
        int tmp = currentRotation[2];
        currentRotation[2] = currentRotation[4];
        currentRotation[4] = currentRotation[5];
        currentRotation[5] = currentRotation[1];
        currentRotation[1] = tmp;
    }

    public void changeRotationByFlip()
    {
        int tmp = currentRotation[0];
        currentRotation[0] = currentRotation[2];
        currentRotation[2] = currentRotation[3];
        currentRotation[3] = currentRotation[5];
        currentRotation[5] = tmp;
    }


    private void init() {
        side = new Color[6][9];
        for (int i = 0; i < 6; i++)
            for (int j = 0; j < 9; j++)
                side[i][j] = Color.ANY;

        currentRotation = new int [6];
        currentRotation[0] = UP;
        currentRotation[1] = RIGHT;
        currentRotation[2] = FACE;
        currentRotation[3] = DOWN;
        currentRotation[4] = LEFT;
        currentRotation[5] = BACK;
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

}
