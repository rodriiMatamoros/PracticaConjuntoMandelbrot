package com.example.practicaconjuntomandelbrot;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Future;
import java.util.concurrent.Callable;
import java.util.List;
import java.util.ArrayList;

public class MandelbrotRenderer implements Callable<WritableImage> {
    private int width;
    private int height;
    private int numWorkers;
    private GraphicsContext gc;

    public MandelbrotRenderer(int width, int height, int numWorkers, GraphicsContext gc) {
        this.width = width;
        this.height = height;
        this.numWorkers = numWorkers;
        this.gc = gc;
    }

    @Override
    public WritableImage call() {
        WritableImage image = new WritableImage(width, height);
        PixelWriter pw = image.getPixelWriter();

        // Divide el trabajo en sentido vertical para diferentes filas.
        int rowsPerWorker = height / numWorkers;

        List<Callable<Void>> tasks = new ArrayList<>();

        for (int i = 0; i < numWorkers; i++) {
            final int startRow = i * rowsPerWorker;
            final int endRow = (i == numWorkers - 1) ? height : (i + 1) * rowsPerWorker;

            tasks.add(() -> {
                calculateMandelbrot(startRow, endRow, pw);
                return null;
            });
        }

        try {
            ExecutorService workerExecutor = Executors.newFixedThreadPool(numWorkers);

            // Ejecuta las tareas en paralelo
            workerExecutor.invokeAll(tasks);

            workerExecutor.shutdown();
            workerExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return image;
    }

    private void calculateMandelbrot(int startRow, int endRow, PixelWriter pw) {
        for (int y = startRow; y < endRow; y++) {
            for (int x = 0; x < width; x++) {
                double x0 = map(x, 0, width, -2.5, 1.0);
                double y0 = map(y, 0, height, -1.0, 1.0);
                int color = MandelbrotCalculator.calculateMandelbrotColor(x0, y0);
                pw.setColor(x, y, MandelbrotCalculator.getColorFromInt(color));
            }
        }
    }

    private double map(double value, double start1, double stop1, double start2, double stop2) {
        return start2 + (stop2 - start2) * ((value - start1) / (stop1 - start1));
    }
}
