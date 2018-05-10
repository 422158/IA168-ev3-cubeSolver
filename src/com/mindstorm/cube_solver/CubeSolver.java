package com.mindstorm.cube_solver;

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

    CubeSolver() {
        arm = new EV3LargeRegulatedMotor(MotorPort.A);
        plate = new EV3LargeRegulatedMotor(MotorPort.B);
        colorArm = new EV3LargeRegulatedMotor(MotorPort.C);
        colorSensor = new ColorSensor(SensorPort.S1);

        arm.setSpeed(arm.getMaxSpeed() - 200);
        plate.setSpeed(arm.getMaxSpeed() - 200);
        colorArm.setSpeed(arm.getMaxSpeed() - 200);

        printInfo();
    }

    private void printInfo() {
        System.out.println("Arm speed: " + arm.getSpeed());
        System.out.println("Plate speed: " + plate.getSpeed());
        System.out.println("ColorArm speed: " + colorArm.getSpeed());
    }

    void flipCube() {
        grab();
        arm.rotateTo(GRAB_ANGLE + GRAB_ANGLE_BACKWARD);
        release();
    }

    void rotateToLeftPlusAngle(int angle) {
        plate.rotateTo(plate.getLimitAngle() + ROTATE + angle);
    }

    void rotateToLeft() {
        rotateToLeftPlusAngle(0);
    }

    void rotateToRightPlusAngle(int angle) {
        plate.rotateTo(plate.getLimitAngle() - ROTATE - angle);
    }

    void rotateToRight() {
        rotateToRightPlusAngle(0);
    }

    void grabAndRotateToLeft() {
        grab();
        Delay.msDelay(500);
        rotateToLeftPlusAngle(ROTATE_STEP);
        Delay.msDelay(500);
        release();
        plate.rotate(-ROTATE_STEP);
    }

    void grabAndRotateToRight() {
        grab();
        Delay.msDelay(500);
        rotateToRightPlusAngle(ROTATE_STEP);
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

        for (int i=0; i<3; i++) {
            rotateToCorner();
            stabilize();
            scanColor(ROTATE_LOW_MID_COLOR);
            scanNextLowColor(ROTATE_LOW_CORNER_COLOR);
        }

        rotateToCorner();
        stabilize();
    }

    public void run() {
        scanSideColors();
        flipCube();
        scanSideColors();
        flipCube();
        scanSideColors();
        flipCube();

//        rotateToLeft();
//        rotateToLeft();
//        rotateToLeft();
//
//        flipCube();
//        flipCube();
//
//        rotateToRight();
//        rotateToRight();
//
//        grabAndRotateToLeft();
//        grabAndRotateToLeft();
//        grabAndRotateToLeft();
//        grabAndRotateToRight();
//        grabAndRotateToRight();
//        grabAndRotateToLeft();
//        clear();
    }
}
