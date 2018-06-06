package com.mindstorm.cube_solver;

import lejos.robotics.RegulatedMotor;
import lejos.robotics.RegulatedMotorListener;

public class StopingListener implements RegulatedMotorListener {

    private InfraredSensor infraredSensor;

    public StopingListener(InfraredSensor is) {
        infraredSensor = is;
    }

    @Override
    public void rotationStarted(RegulatedMotor motor, int tachoCount, boolean stalled, long timeStamp) {

    }

    @Override
    public void rotationStopped(RegulatedMotor motor, int tachoCount, boolean stalled, long timeStamp) {
        while (infraredSensor.flagSet());
    }
}
