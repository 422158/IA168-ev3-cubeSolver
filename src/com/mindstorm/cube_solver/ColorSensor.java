package com.mindstorm.cube_solver;

import lejos.hardware.port.Port;
import lejos.hardware.port.UARTPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.robotics.Color;

public class ColorSensor extends EV3ColorSensor {
    public ColorSensor(UARTPort port) {
        super(port);
    }

    public ColorSensor(Port port) {
        super(port);
    }

    Color getRGBColor() {
        float sample[] = new float[3];
        fetchSample(sample, 0);

        float colorRedThreshold = 0.16f;
        float colorGreenThreshold = 0.22f;
        float colorBlueThreshold = 0.16f;

        int red = Math.round(sample[0] / colorRedThreshold * 255);
        int green = Math.round(sample[1] / colorGreenThreshold * 255);
        int blue = Math.round(sample[2] / colorBlueThreshold * 255);
        System.out.println("R: " + red + "G: " + green + "B: " + blue);
        red = red > 255 ? 255 : red;
        green = green > 255 ? 255 : green;
        blue = blue > 255 ? 255 : blue;

        return new Color(red, green, blue);
    }

    public static String colorName(Color color) {

        if (color.getRed() > 179 &&
                color.getGreen() > 70 && color.getGreen() < 130 &&
                color.getBlue() < 70) {
            return "orange";
        }

        if (color.getRed() > 179 &&
                color.getGreen() < 71 &&
                color.getBlue() < 71) {
            return "red";
        }

        if (color.getRed() < 100 &&
                color.getGreen() > 190 &&
                color.getBlue() < 100) {
            return "green";
        }

        if (color.getRed() > 210 &&
                color.getGreen() > 210 &&
                color.getBlue() < 90) {
            return "yellow";
        }

        if (color.getRed() > 210 &&
                color.getGreen() > 210 &&
                color.getBlue() > 210) {
            return "white";
        }

        if (color.getRed() < 100 &&
                color.getGreen() < 150 &&
                color.getBlue() > 190) {
            return "blue";
        }

        if (color.getRed() > 140 && color.getRed() < 177 &&
                color.getGreen() > 210 &&
                color.getBlue() > 210) {
            return "white custom";
        }


        return "any";
    }
}
