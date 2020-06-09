import constants.FxmlLocations;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import objects.Inventory;

import java.io.IOException;

//Created by: Nathan Pepin
//For: Software 1 - C482
//Student ID: 001195100
//
//Note: Dummy data is created in the Inventory class, but can be shut off by deleting line 27
//      It is kept on in the project to assist with ease of grading.
public class Main extends Application {
    public static Inventory inventory = new Inventory();

    public static Controller_Page_Main controller_page_main;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        inventory.generateDummyData();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(FxmlLocations.PAGE_MAIN_FXML));
            Parent root = loader.load();
            controller_page_main = loader.getController();

            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.show();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}
