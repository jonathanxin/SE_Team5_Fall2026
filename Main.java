// needs objects for model, view, controller

import java.util.Scanner;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Main extends Application {
    Controller controller;
    Model model;    
    
    @Override
    public void start(Stage stage) throws Exception { // main entry point for JavaFX application
        // stage is top-level JavaFX container (window)
        Parent root = FXMLLoader.load(getClass().getResource("View.fxml")); // root node of the scene graph
        Scene scene = new Scene(root); // initialize scene (container for all content) with root node

        // stage (window) information
        stage.setTitle("JavaFX Application"); // title bar
        stage.setScene(scene); 
        stage.show(); 
    }

    public static void main(String[] args) {
        // code here runs before JavaFX application thread
        launch(args); // main JavaFX application thread (blocking)
        // code here runs only after the GUI closes
        // // main loop
        // do
        // {
        //     controller.update();
        //     model.update();
        //     view.update(); // view.repaint();
        // } while (true);
    }
}
