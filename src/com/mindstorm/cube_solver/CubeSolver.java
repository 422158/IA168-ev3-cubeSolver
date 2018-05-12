package com.mindstorm.cube_solver;

import lejos.hardware.Button;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;
import lejos.robotics.Color;
import lejos.utility.Delay;

public class CubeSolver {
    final private static int GRAB_ANGLE = 120;
    final private static int GRAB_ANGLE_BACKWARD = 70;

    final private static int ROTATE = 270;
    final private static int ROTATE_STEP = 42;

    final private static int ROTATE_CENTER_COLOR = -510;
    final private static int ROTATE_LOW_MID_COLOR = -360;
    final private static int ROTATE_LOW_CORNER_COLOR = -310;

    private EV3LargeRegulatedMotor arm;
    private EV3LargeRegulatedMotor plate;
    private EV3LargeRegulatedMotor colorArm;
    private ColorSensor colorSensor;

    private Cube cube;

    CubeSolver() {
        arm = new EV3LargeRegulatedMotor(MotorPort.A);
        plate = new EV3LargeRegulatedMotor(MotorPort.B);
        colorArm = new EV3LargeRegulatedMotor(MotorPort.C);
        colorSensor = new ColorSensor(SensorPort.S1);

        arm.setSpeed(arm.getMaxSpeed() - 200);
        plate.setSpeed(arm.getMaxSpeed() - 200);
        colorArm.setSpeed(arm.getMaxSpeed() - 200);

        printInfo();

        cube = new Cube();
    }

    private void printInfo() {
        System.out.println("Arm speed:\n" + arm.getSpeed());
        System.out.println("Plate speed:\n" + plate.getSpeed());
        System.out.println("ColorArm speed:\n" + colorArm.getSpeed());
        System.out.println("----");
    }

    void flipCube() {
        grab();
        arm.rotateTo(GRAB_ANGLE + GRAB_ANGLE_BACKWARD);
        release();

//        cube.setPositionByFlip();
    }

    void rotateClockwisePlusAngle(int angle) {
        plate.rotateTo(plate.getLimitAngle() + ROTATE + angle);
    }

    void rotateClockwise() {
        rotateClockwisePlusAngle(0);
    }

    void rotateCounterClockwisePlusAngle(int angle) {
        plate.rotateTo(plate.getLimitAngle() - ROTATE - angle);
    }

    void rotateCounterClockwise() {
        rotateCounterClockwisePlusAngle(0);
    }

    void grabAndRotateCubeCounterClockwise() {
        grab();
        Delay.msDelay(500);
        rotateClockwisePlusAngle(ROTATE_STEP);
        Delay.msDelay(500);
        release();
        plate.rotate(-ROTATE_STEP);
    }

    void grabAndRotateCubeClockwise() {
        grab();
        Delay.msDelay(500);
        rotateCounterClockwisePlusAngle(ROTATE_STEP);
        Delay.msDelay(500);
        release();
        plate.rotate(+ROTATE_STEP);
    }

    void grab() {
        arm.rotateTo(GRAB_ANGLE);
    }

    void release() {
        arm.rotateTo(0);
        Delay.msDelay(500);
    }

    void grabAndReleaseWithSpeed(int speed) {
        int currentSpeed = arm.getSpeed();
        arm.setSpeed(speed);
        grab();
        release();
        arm.setSpeed(currentSpeed);
    }

    public void clear() {
        Delay.msDelay(1000);

        colorArm.rotateTo(0);
        plate.rotateTo(0);
        arm.rotateTo(0);

        arm.close();
        plate.close();
        colorArm.close();
        colorSensor.close();
    }


    void scanCenterColor() {
        colorArm.rotateTo(ROTATE_CENTER_COLOR);
        fetchColorSample();
    }

    void scanNextLowColor(int angle) {
        colorArm.rotateTo(angle);
        rotateToCorner();
        fetchColorSample();
    }

    void scanFirstLowColor() {
        colorArm.rotateTo(ROTATE_LOW_MID_COLOR);
        fetchColorSample();
    }

    void scanColor(int angle) {
        colorArm.rotateTo(angle);
        fetchColorSample();
    }

    void fetchColorSample() {
        Color color = colorSensor.getRGBColor();
        System.out.println(ColorSensor.colorName(color));
    }

    void rotateToCorner() {
        plate.rotate(ROTATE / 2);
    }

    void stabilize() {
        colorArm.rotateTo(0);
        grabAndReleaseWithSpeed(300);
    }

    void scanSideColors() {
        stabilize();
        colorSensor.setCurrentMode("RGB");

        scanCenterColor();
        scanFirstLowColor();
        scanNextLowColor(ROTATE_LOW_CORNER_COLOR);

        for (int i = 0; i < 3; i++) {
            rotateToCorner();
            stabilize();
            scanColor(ROTATE_LOW_MID_COLOR);
            scanNextLowColor(ROTATE_LOW_CORNER_COLOR);
        }

        rotateToCorner();
        stabilize();
    }

    String findSolution() {
        Search searchObj = new Search();
        String optimum = null;

        String result = searchObj.solution(cube.toScrambleString(), 21, 500, 0, 0);
        if (!result.contains("Error")) {
            optimum = result;
        }

        for (int i = 0; i < 4; i++) {
            result = searchObj.next(500, 0, 0);
            if (!result.contains("Error")) {
                optimum = result;
            } else {
                break;
            }
        }

        return optimum;
    }

    void setCubePosition(char symbol) {

        int goalSide = Cube.getSideFromChar(symbol);

        if (goalSide == cube.getBottomPosition()) {
            return;
        }

        int nextPositionByFlip = cube.getNextPositionByFlip();
        if (goalSide == nextPositionByFlip) {
            flipCube();
            cube.setBottomPosition(nextPositionByFlip);
            return ;
        }

        int nextNextPositionByFlip = Cube.getNextPositionByFlip(nextPositionByFlip, cube.getRotation());

        if (goalSide == nextNextPositionByFlip) {
            flipCube();
            flipCube();
            cube.setBottomPosition(nextNextPositionByFlip);
            return ;
        }

        int nextNextNextPositionByFlip = Cube.getNextPositionByFlip(nextNextPositionByFlip, cube.getRotation());
        if (goalSide == nextNextNextPositionByFlip) {
            flipCube();
            flipCube();
            flipCube();
            cube.setBottomPosition(nextNextNextPositionByFlip);
            return ;
        }

        int clockwiseRotate = cube.getNextClockwiseRotation();
        if (goalSide == Cube.getNextPositionByFlip(cube.getBottomPosition(), clockwiseRotate)) {
            rotateClockwise();
            flipCube();

            cube.setBottomPosition(Cube.getNextPositionByFlip(cube.getBottomPosition(), clockwiseRotate));
            cube.rotateClockwise();
            return ;
        }

        int counterClockwiseRotate = cube.getNextCounterClockwiseRotation();
        if (goalSide == Cube.getNextPositionByFlip(cube.getBottomPosition(), counterClockwiseRotate)) {
            rotateCounterClockwise();
            flipCube();

            cube.setBottomPosition(Cube.getNextPositionByFlip(cube.getBottomPosition(), counterClockwiseRotate));
            cube.rotateCounterClockwise();
            return ;
        }
    }

    public void run() {

        cube.setField(Cube.UP, 0, Cube.Color.WHITE);
        cube.setField(Cube.UP, 1, Cube.Color.WHITE);
        cube.setField(Cube.UP, 2, Cube.Color.WHITE);
        cube.setField(Cube.UP, 3, Cube.Color.WHITE);
        cube.setField(Cube.UP, 4, Cube.Color.WHITE);
        cube.setField(Cube.UP, 5, Cube.Color.WHITE);
        cube.setField(Cube.UP, 6, Cube.Color.WHITE);
        cube.setField(Cube.UP, 7, Cube.Color.WHITE);
        cube.setField(Cube.UP, 8, Cube.Color.WHITE);

        cube.setField(Cube.FACE, 0, Cube.Color.GREEN);
        cube.setField(Cube.FACE, 1, Cube.Color.GREEN);
        cube.setField(Cube.FACE, 2, Cube.Color.GREEN);
        cube.setField(Cube.FACE, 3, Cube.Color.GREEN);
        cube.setField(Cube.FACE, 4, Cube.Color.GREEN);
        cube.setField(Cube.FACE, 5, Cube.Color.GREEN);
        cube.setField(Cube.FACE, 6, Cube.Color.GREEN);
        cube.setField(Cube.FACE, 7, Cube.Color.GREEN);
        cube.setField(Cube.FACE, 8, Cube.Color.GREEN);

        cube.setField(Cube.DOWN, 0, Cube.Color.YELLOW);
        cube.setField(Cube.DOWN, 1, Cube.Color.YELLOW);
        cube.setField(Cube.DOWN, 2, Cube.Color.YELLOW);
        cube.setField(Cube.DOWN, 3, Cube.Color.YELLOW);
        cube.setField(Cube.DOWN, 4, Cube.Color.YELLOW);
        cube.setField(Cube.DOWN, 5, Cube.Color.YELLOW);
        cube.setField(Cube.DOWN, 6, Cube.Color.YELLOW);
        cube.setField(Cube.DOWN, 7, Cube.Color.YELLOW);
        cube.setField(Cube.DOWN, 8, Cube.Color.YELLOW);

        cube.setField(Cube.RIGHT, 0, Cube.Color.RED);
        cube.setField(Cube.RIGHT, 1, Cube.Color.RED);
        cube.setField(Cube.RIGHT, 2, Cube.Color.RED);
        cube.setField(Cube.RIGHT, 3, Cube.Color.RED);
        cube.setField(Cube.RIGHT, 4, Cube.Color.RED);
        cube.setField(Cube.RIGHT, 5, Cube.Color.RED);
        cube.setField(Cube.RIGHT, 6, Cube.Color.RED);
        cube.setField(Cube.RIGHT, 7, Cube.Color.ORANGE);
        cube.setField(Cube.RIGHT, 8, Cube.Color.BLUE);

        cube.setField(Cube.LEFT, 0, Cube.Color.ORANGE);
        cube.setField(Cube.LEFT, 1, Cube.Color.ORANGE);
        cube.setField(Cube.LEFT, 2, Cube.Color.ORANGE);
        cube.setField(Cube.LEFT, 3, Cube.Color.ORANGE);
        cube.setField(Cube.LEFT, 4, Cube.Color.ORANGE);
        cube.setField(Cube.LEFT, 5, Cube.Color.ORANGE);
        cube.setField(Cube.LEFT, 6, Cube.Color.BLUE);
        cube.setField(Cube.LEFT, 7, Cube.Color.RED);
        cube.setField(Cube.LEFT, 8, Cube.Color.ORANGE);

        cube.setField(Cube.BACK, 0, Cube.Color.BLUE);
        cube.setField(Cube.BACK, 1, Cube.Color.BLUE);
        cube.setField(Cube.BACK, 2, Cube.Color.BLUE);
        cube.setField(Cube.BACK, 3, Cube.Color.BLUE);
        cube.setField(Cube.BACK, 4, Cube.Color.BLUE);
        cube.setField(Cube.BACK, 5, Cube.Color.BLUE);
        cube.setField(Cube.BACK, 6, Cube.Color.ORANGE);
        cube.setField(Cube.BACK, 7, Cube.Color.BLUE);
        cube.setField(Cube.BACK, 8, Cube.Color.RED);

        String solution = findSolution();

        if (solution == null) {
            System.out.println("Solution does not exist.");
            return;
        }

        System.out.println(solution);

        setCubePosition('R');
        setCubePosition('F');
        setCubePosition('L');


        clear();

//        clear();
    }
}
