package com.example.practicaconjuntomandelbrot;

import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;

public class MandelbrotCalculator {
    public static int calculateMandelbrotColor(double x, double y) {
        int maxIterations = 1000; // Número máximo de iteraciones
        double real = x;
        double imag = y;
        int n = 0;

        while (n < maxIterations) {
            double real2 = real * real;
            double imag2 = imag * imag;

            if (real2 + imag2 > 4.0) {
                return n;
            }

            imag = 2 * real * imag + y;
            real = real2 - imag2 + x;
            n++;
        }

        return maxIterations; // El punto está dentro del conjunto
    }

    public static Color getColorFromInt(int color) {
        // Mapea el valor de color a un color específico según tu preferencia
        double hue = (color % 256) / 255.0; // Ajusta el rango de colores
        return Color.hsb(hue * 360, 1.0, 1.0);
    }
}
