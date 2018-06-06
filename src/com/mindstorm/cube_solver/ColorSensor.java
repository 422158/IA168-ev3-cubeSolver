package com.mindstorm.cube_solver;

import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.robotics.SampleProvider;
import lejos.robotics.filter.MedianFilter;
import lejos.utility.Delay;

public class ColorSensor  {
    private EV3ColorSensor colorSensor;
    private SampleProvider medianProvider;

    public ColorSensor(Port port)
    {
        colorSensor = new EV3ColorSensor(port);
        medianProvider = new MedianFilter(colorSensor.getRGBMode(), 10);
    }
    Cube.Color getRGBColor() {
        float [] result = new float[medianProvider.sampleSize()];
        for (int i = 0; i < 10; ++i)
            medianProvider.fetchSample(result, 0);

        return determineColor(result);
    }

    public void close(){
        colorSensor.close();
    }

    public static Cube.Color determineColor(float [] colorArray) {
        float red = colorArray[0];
        float green = colorArray[1];
        float blue = colorArray[2];
        System.out.println("R: " + red + " G: " + green + " B: " + blue);

        if (red > 0.09 && green < 0.06)
            return Cube.Color.RED;
        //0.12 0.064 0.024
        if (red > 0.09 && green > 0.06 && green < 0.1)
            return Cube.Color.ORANGE;

        if (red > 0.1 && green > 0.1 && blue > 0.1)
            return Cube.Color.WHITE;

        if (red < 0.1 && green < 0.15 && blue > 0.1)
            return Cube.Color.BLUE;

        if (red < 0.1 && green > 0.15 && blue < 0.1)
            return Cube.Color.GREEN;

        if (red > 0.1 && green > 0.2 && blue < 0.1)
            return Cube.Color.YELLOW;

        return Cube.Color.ANY;

    }
}
