package files;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class FireDoom extends Application {

    private int fireWidth = 100, fireHeight = 60;

    private List<Integer> firePixelsArray = new ArrayList<>();

    public void createFireDataStructure() {
        int numOfPixels = (fireWidth * fireHeight);

        for (int i = 0; i < numOfPixels; i++) {
            firePixelsArray.add(0);
        }
    }

    public void calculationFirePropagation() {

        for (int column = 0; column < fireWidth; column++) {
            for (int row = 0; row < fireHeight; row++) {
                int pixelIndex = column + (fireWidth * row);
                updateFireIntensifyPerPixel(pixelIndex);
            }
        }

    }

    public void updateFireIntensifyPerPixel(int currentPixelIndex) {
        int bellowPixelIndex = currentPixelIndex + fireWidth;

        if (bellowPixelIndex >= (fireWidth * fireHeight)) return;

        int decay = (int) Math.floor(Math.random() * 3);
        int newFireIntensify = Math.max(0, firePixelsArray.get(bellowPixelIndex) - decay);
        firePixelsArray.set(Math.max(0, currentPixelIndex - decay), newFireIntensify);
    }

    private void createFireSource() {
        for (int column = 0; column <= fireWidth; column++) {
            int overflowPixelIndex = (fireWidth * fireHeight);
            int pixelIndex = (overflowPixelIndex - fireWidth) + column;
            firePixelsArray.set(pixelIndex - 1, 36);
        }
    }

    private final double sizePixel = 5;
    private final double spacing = 1; // space pixel

    public void renderFire() {

        for (int row = 0; row < fireHeight; row++) {
            for (int column = 0; column < fireWidth; column++) {
                pane.getChildren().add(createCell(row, column));
            }
        }

    }

    private Rectangle createCell(int r, int c) {
        Rectangle rectangle = new Rectangle((sizePixel * c * spacing), (sizePixel * r * spacing), sizePixel, sizePixel);
        rectangle.setFill(Color.WHITE);

        return rectangle;
    }

    private Pane pane;

    private void initialize() {
        createFireDataStructure();
        createFireSource();
        renderFire();

        Thread thread = new Thread(threadRender);
        thread.start();
    }

    private Task threadRender = new Task() {

        @Override
        protected Object call() throws Exception {

            Pallete pallete = new Pallete();

            while (!isCancelled()) {
                calculationFirePropagation();

                Platform.runLater(() -> {
                    for (int i = 0; i < (fireWidth * fireHeight); i++) {
                        int colorIndex = firePixelsArray.get(i);
                        Rectangle rectangle = (Rectangle) pane.getChildren().get(i);
                        rectangle.setFill(pallete.doomFirePallete[colorIndex]);
                    }
                });

                Thread.sleep(60);
            }

            return null;
        }

    };

    @Override
    public void start(Stage primaryStage) throws Exception {
        pane = new Pane();
        pane.setStyle("-fx-background-color: black;");

        Scene scene = new Scene(pane, fireWidth * sizePixel, fireHeight * sizePixel);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Doom Fire JAVAFX");
        primaryStage.setOnCloseRequest(event -> threadRender.cancel());
        primaryStage.show();

        initialize();
    }
}
