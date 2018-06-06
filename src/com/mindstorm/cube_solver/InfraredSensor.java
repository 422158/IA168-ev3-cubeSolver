package com.mindstorm.cube_solver;

import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3IRSensor;
import lejos.utility.Delay;


public class InfraredSensor implements Runnable {
    private EV3IRSensor infraSensor;

    private boolean flag = false;

    public InfraredSensor(Port port)
    {
        infraSensor = new EV3IRSensor(port);
    }

    @Override
    public void run() {
        int pressed;
        while (true)
        {
            pressed = infraSensor.getRemoteCommand(0);
            if (pressed == 1)
            {

                flag = !flag;
                Delay.msDelay(1000);
            }
            if (Thread.currentThread().isInterrupted())
                break;
        }
    }

    public boolean flagSet() {
        return flag;
    }
}
