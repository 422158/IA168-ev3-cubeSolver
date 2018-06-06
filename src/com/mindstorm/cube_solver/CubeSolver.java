package com.mindstorm.cube_solver;

import lejos.hardware.Button;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;
import lejos.robotics.RegulatedMotorListener;
import lejos.utility.Delay;

public class CubeSolver implements Runnable{
    final private static int GRAB_ANGLE = 115;
    final private static int GRAB_ANGLE_BACKWARD = 65;

    final private static int [] SCAN_ORDER = {7, 8, 5, 2, 1, 0, 3, 6};

    final private static int ROTATE = 270;
    final private static int ROTATE_STEP_CLOCKWISE = 44;
    final private static  int ROTATE_STEP_COUNTERCLOCKWISE = 46;

    final private static int ROTATE_CENTER_COLOR_SHORT = -450;
    final private static int ROTATE_LOW_MID_COLOR_SHORT = -285;
    final private static int ROTATE_LOW_CORNER_COLOR_SHORT = -230;

    final private static int ROTATE_CENTER_COLOR_LONG = -490;
    final private static int ROTATE_LOW_MID_COLOR_LONG = -285;
    final private static int ROTATE_LOW_CORNER_COLOR_LONG = -240;

    private EV3LargeRegulatedMotor arm;
    private EV3LargeRegulatedMotor plate;
    private EV3LargeRegulatedMotor colorArm;
    private ColorSensor colorSensor;

    private Cube cube;

    CubeSolver(StopingListener stopingListener) {
        arm = new EV3LargeRegulatedMotor(MotorPort.A);
        arm.addListener(stopingListener);
        plate = new EV3LargeRegulatedMotor(MotorPort.B);
        plate.addListener(stopingListener);
        colorArm = new EV3LargeRegulatedMotor(MotorPort.C);
        colorArm.addListener(stopingListener);
        colorSensor = new ColorSensor(SensorPort.S1);
        if (arm.getMaxSpeed() < 720)
        {
            System.out.println("arm max speed not sufficient, recharge batteries");
            Button.waitForAnyPress();
            System.exit(1);
        }

        if (plate.getMaxSpeed() < 730)
        {
            System.out.println("plate max speed not sufficient, recharge batteries");
            Button.waitForAnyPress();
            System.exit(1);
        }

        if (colorArm.getMaxSpeed() < 700)
        {
            System.out.println("colrArm max speed not sufficient, recharge batteries");
            Button.waitForAnyPress();
            System.exit(1);
        }
        arm.setSpeed(720);
        plate.setSpeed(780);
        colorArm.setSpeed(700);
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
        cube.changeRotationByFlip();
    }

    void rotateClockwisePlusAngle(int angle) {
        plate.rotateTo(plate.getLimitAngle() + ROTATE + angle);
    }

    void rotateClockwise() {
        rotateClockwisePlusAngle(0);
        cube.changeRotationClockwise();
    }

    void rotateCounterClockwisePlusAngle(int angle) {
        plate.rotateTo(plate.getLimitAngle() - ROTATE - angle);
    }

    void rotateCounterClockwise() {
        rotateCounterClockwisePlusAngle(0);
        cube.changeRotationCounterClockwise();
    }

    void rotateClockwiseExactAngle(int angle){
        plate.rotateTo(plate.getLimitAngle() + angle);
    }

    void rotateCounterClockwiseExactAngle(int angle){
        plate.rotateTo(plate.getLimitAngle() - angle);
    }

    void grabAndRotateCubeCounterClockwise() {
        grab();
        Delay.msDelay(500);
//        rotateCounterClockwiseExactAngle(100);
//        rotateClockwiseExactAngle(100);
        rotateClockwisePlusAngle(ROTATE_STEP_COUNTERCLOCKWISE);
        Delay.msDelay(500);
        plate.rotate(-ROTATE_STEP_COUNTERCLOCKWISE);
        release();
    }



    void grabAndRotateCubeClockwise(boolean twice) {
        grab();
        Delay.msDelay(500);
//        rotateClockwiseExactAngle(100);
//        rotateCounterClockwiseExactAngle(100);
        Delay.msDelay(500);
        if (twice){
            rotateCounterClockwisePlusAngle(0);
        }
        rotateCounterClockwisePlusAngle(ROTATE_STEP_CLOCKWISE);
        Delay.msDelay(500);
        plate.rotate(+ROTATE_STEP_CLOCKWISE);
        release();
    }

    void grab() {
        arm.rotateTo(GRAB_ANGLE);
    }

    void colorArmRelease() {
        colorArm.rotateTo(0);
        Delay.msDelay(500);
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
        arm.rotateTo(0);
        plate.rotateTo(0);

        arm.close();
        plate.close();
        colorArm.close();
        colorSensor.close();
    }


    Cube.Color scanCenterColor(boolean shortSide) {
        colorArm.rotateTo(shortSide ? ROTATE_CENTER_COLOR_SHORT : ROTATE_CENTER_COLOR_LONG);
        Delay.msDelay(200);
        return fetchColorSample();
    }

    Cube.Color scanColor(int angle) {
        colorArm.rotateTo(angle);
        Delay.msDelay(200);
        Cube.Color sample =  fetchColorSample();
        if (sample == Cube.Color.ANY){
            System.out.println("Reparations");
            colorArm.rotateTo(angle - 10);
            Delay.msDelay(200);
            sample = fetchColorSample();
        }
        return sample;
    }

    Cube.Color fetchColorSample() {
        Cube.Color color = colorSensor.getRGBColor();
        System.out.println(color);
        return color;
    }

    void rotateHalfwayPlusAngle(int angle) {
        plate.rotate(ROTATE / 2 + angle);
    }

    void stabilize() {
        colorArm.rotateTo(0);
        grabAndReleaseWithSpeed(300);
    }

    void scanSideColors(int cubeSide) {
        boolean shortSide = true;
        stabilize();
        cube.setField(cubeSide, 4, scanCenterColor(shortSide));
        //Button.waitForAnyPress();
        Cube.Color tmp;
        for (int i = 0; i < 4; i++) {
            if (i != 0) stabilize();
            tmp = scanColor(shortSide ? ROTATE_LOW_MID_COLOR_SHORT : ROTATE_LOW_MID_COLOR_LONG);
            cube.setField(cubeSide, SCAN_ORDER[i], tmp);
            //Button.waitForAnyPress();
            rotateHalfwayPlusAngle(shortSide ? -5 : -15);
            tmp = scanColor(shortSide ? ROTATE_LOW_CORNER_COLOR_SHORT : ROTATE_LOW_CORNER_COLOR_LONG);
            cube.setField(cubeSide, SCAN_ORDER[i + 1], tmp);
            //Button.waitForAnyPress();
            rotateHalfwayPlusAngle(shortSide ? +5 : 15);
            shortSide = !shortSide;
        }

        colorArmRelease();

    }

    void scanCube()
    {
        //scanning top
        scanSideColors(cube.getCurrentRotationsTop());

        //scanning face
        flipCube();
        scanSideColors(cube.getCurrentRotationsTop());

        //scanning down
        flipCube();
        scanSideColors(cube.getCurrentRotationsTop());

        //scanning back
        flipCube();
        rotateClockwise();
        rotateClockwise();
        scanSideColors(cube.getCurrentRotationsTop());

        //scanning right
        rotateCounterClockwise();
        flipCube();
        rotateClockwise();
        scanSideColors(cube.getCurrentRotationsTop());

        //scanning left
        rotateCounterClockwise();
        flipCube();
        flipCube();
        rotateClockwise();
        scanSideColors(cube.getCurrentRotationsTop());
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



    void setSideToBottom(int side) {
        switch (cube.getCurrentPositionOfSide(side))
        {
            case 0:
                flipCube();
                flipCube();
                return;
            case 1:
                rotateCounterClockwise();
                flipCube();
                return;
            case 2:
                flipCube();
                flipCube();
                flipCube();
                return;
            case 3:
                return;
            case 4:
                rotateClockwise();
                flipCube();
                return;
            case 5:
                flipCube();
            default:
                return;
        }

    }
    void solveCubeUsingSolution(String solution)
    {
        String steps [] = solution.split(" ");
        for (int i = 0; i < steps.length; i++)
        {
            if (steps[i].length() == 0) continue;
            switch (steps[i].charAt(0))
            {
                case 'R':
                    setSideToBottom(Cube.RIGHT);
                    break;
                case 'L':
                    setSideToBottom(Cube.LEFT);
                    break;
                case 'U':
                    setSideToBottom(Cube.UP);
                    break;
                case 'D':
                    setSideToBottom(Cube.DOWN);
                    break;
                case 'F':
                    setSideToBottom(Cube.FACE);
                    break;
                case 'B':
                    setSideToBottom(Cube.BACK);
                    break;
            }
            System.out.println(steps[i] + " " + steps[i].length());
            Button.waitForAnyPress();
            if (steps[i].length() == 2) {
                if (steps[i].charAt(1) == '\'')
                    grabAndRotateCubeCounterClockwise();
                else if (steps[i].charAt(1) == '2')
                    grabAndRotateCubeClockwise(true);
            }
            else if (steps[i].length() == 1) {
                grabAndRotateCubeClockwise(false);
            }
        }
    }
    public void run() {

        cube.setField(Cube.UP, 0, Cube.Color.WHITE);
        cube.setField(Cube.UP, 1, Cube.Color.YELLOW);
        cube.setField(Cube.UP, 2, Cube.Color.RED);
        cube.setField(Cube.UP, 3, Cube.Color.GREEN);
        cube.setField(Cube.UP, 4, Cube.Color.WHITE);
        cube.setField(Cube.UP, 5, Cube.Color.WHITE);
        cube.setField(Cube.UP, 6, Cube.Color.YELLOW);
        cube.setField(Cube.UP, 7, Cube.Color.ORANGE);
        cube.setField(Cube.UP, 8, Cube.Color.ORANGE);

        cube.setField(Cube.FACE, 0, Cube.Color.RED);
        cube.setField(Cube.FACE, 1, Cube.Color.GREEN);
        cube.setField(Cube.FACE, 2, Cube.Color.WHITE);
        cube.setField(Cube.FACE, 3, Cube.Color.ORANGE);
        cube.setField(Cube.FACE, 4, Cube.Color.GREEN);
        cube.setField(Cube.FACE, 5, Cube.Color.YELLOW);
        cube.setField(Cube.FACE, 6, Cube.Color.ORANGE);
        cube.setField(Cube.FACE, 7, Cube.Color.WHITE);
        cube.setField(Cube.FACE, 8, Cube.Color.GREEN);

        cube.setField(Cube.DOWN, 0, Cube.Color.YELLOW);
        cube.setField(Cube.DOWN, 1, Cube.Color.RED);
        cube.setField(Cube.DOWN, 2, Cube.Color.YELLOW);
        cube.setField(Cube.DOWN, 3, Cube.Color.RED);
        cube.setField(Cube.DOWN, 4, Cube.Color.YELLOW);
        cube.setField(Cube.DOWN, 5, Cube.Color.GREEN);
        cube.setField(Cube.DOWN, 6, Cube.Color.RED);
        cube.setField(Cube.DOWN, 7, Cube.Color.RED);
        cube.setField(Cube.DOWN, 8, Cube.Color.GREEN);

        cube.setField(Cube.RIGHT, 0, Cube.Color.BLUE);
        cube.setField(Cube.RIGHT, 1, Cube.Color.ORANGE);
        cube.setField(Cube.RIGHT, 2, Cube.Color.BLUE);
        cube.setField(Cube.RIGHT, 3, Cube.Color.BLUE);
        cube.setField(Cube.RIGHT, 4, Cube.Color.RED);
        cube.setField(Cube.RIGHT, 5, Cube.Color.ORANGE);
        cube.setField(Cube.RIGHT, 6, Cube.Color.RED);
        cube.setField(Cube.RIGHT, 7, Cube.Color.WHITE);
        cube.setField(Cube.RIGHT, 8, Cube.Color.YELLOW);

        cube.setField(Cube.LEFT, 0, Cube.Color.GREEN);
        cube.setField(Cube.LEFT, 1, Cube.Color.RED);
        cube.setField(Cube.LEFT, 2, Cube.Color.BLUE);
        cube.setField(Cube.LEFT, 3, Cube.Color.BLUE);
        cube.setField(Cube.LEFT, 4, Cube.Color.ORANGE);
        cube.setField(Cube.LEFT, 5, Cube.Color.YELLOW);
        cube.setField(Cube.LEFT, 6, Cube.Color.WHITE);
        cube.setField(Cube.LEFT, 7, Cube.Color.BLUE);
        cube.setField(Cube.LEFT, 8, Cube.Color.BLUE);

        cube.setField(Cube.BACK, 0, Cube.Color.WHITE);
        cube.setField(Cube.BACK, 1, Cube.Color.GREEN);
        cube.setField(Cube.BACK, 2, Cube.Color.ORANGE);
        cube.setField(Cube.BACK, 3, Cube.Color.BLUE);
        cube.setField(Cube.BACK, 4, Cube.Color.BLUE);
        cube.setField(Cube.BACK, 5, Cube.Color.WHITE);
        cube.setField(Cube.BACK, 6, Cube.Color.ORANGE);
        cube.setField(Cube.BACK, 7, Cube.Color.YELLOW);
        cube.setField(Cube.BACK, 8, Cube.Color.GREEN);
//        while (solution == null){
//            scanCube();
//            solution = findSolution();
//            if (solution == null)
//                System.out.println("Solutiuon does not exist");
//        }
        String solution = findSolution();
        solveCubeUsingSolution(solution);

//        System.out.println(solution);
//
//        solveCubeUsingSolution(solution);
//        scanSideColors(cube.getCurrentRotationsTop());
//        Button.waitForAnyPress();
//        alternativeGrabAndRotateCubeCounterClockwise();
//        Button.waitForAnyPress();
//        flipCube();
//        stabilize();
//        scanCenterColor();
//        Button.waitForAnyPress();
//        rotateClockwise();
//        stabilize();
//        scanCenterColor();
//        Button.waitForAnyPress();



//        grabAndRotateCubeClockwise(false);
//        Button.waitForAnyPress();
//        grabAndRotateCubeClockwise(true);
//        Button.waitForAnyPress();
//        flipCube();
//        flipCube();
//        grabAndRotateCubeClockwise(false);
//        flipCube();
//        grabAndRotateCubeClockwise(false);
//        flipCube();
//        grabAndRotateCubeCounterClockwise();
//        grabAndRotateCubeCounterClockwise();
//        grabAndRotateCubeCounterClockwise();
//        grabAndRotateCubeClockwise(true);
//        grabAndRotateCubeClockwise(true);
        clear();

    }
}
