package com.example.practicaconjuntomandelbrot;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Spinner;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.image.WritableImage;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Future;

public class MandelbrotApp extends Application {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Mandelbrot Set Renderer");

        Canvas canvas = new Canvas(WIDTH, HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        Spinner<Integer> spinner = new Spinner<>(1, 10, 1);

        VBox root = new VBox(spinner, canvas);
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);

        primaryStage.show();

        spinner.valueProperty().addListener((observable, oldValue, newValue) -> {
            int numWorkers = newValue;

            ExecutorService executor = Executors.newFixedThreadPool(numWorkers);

            MandelbrotRenderer renderer = new MandelbrotRenderer(WIDTH, HEIGHT, numWorkers, gc);
            Future<WritableImage> result = executor.submit(renderer);

            executor.shutdown();

            try {
                executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
                WritableImage image = result.get();
                Platform.runLater(() -> gc.drawImage(image, 0, 0));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}

