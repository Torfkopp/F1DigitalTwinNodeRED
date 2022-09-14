package f1digitaltwin;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Class for the Application
 */
public class Application extends javafx.application.Application {
    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 800);
        stage.setTitle("F1 Race Simulation");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Closes the connection when programme is closed
     */
    @Override
    public void stop() {
        NodeREDCommunication.disconnect();
    }
}