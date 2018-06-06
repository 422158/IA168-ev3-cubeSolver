package com.mindstorm.cube_solver;

import lejos.hardware.port.SensorPort;

public class Main {

    public static void main(String[] args) throws InterruptedException {

        InfraredSensor irSensor = new InfraredSensor(SensorPort.S2);
        StopingListener stopingListener = new StopingListener(irSensor);

        CubeSolver solver = new CubeSolver(stopingListener);
        Thread t1 = new Thread(solver);
        t1.start();

        Thread t2 = new Thread(irSensor);
        t2.start();

        try {
            t1.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        t2.interrupt();
        t2.join();
    }
}
