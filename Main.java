// needs objects for model, view, controller

import java.util.Scanner;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    Controller controller;
    Model model;    
    
    @Override
    public void start(Stage stage) throws Exception {
        // stage is top-level JavaFX container (window)
        Parent root = FXMLLoader.load(getClass().getResource("View.fxml")); // root node of the scene graph
        Scene scene = new Scene(root); // initialize scene (container for all content) with root node

        // stage (window) information
        stage.setTitle("JavaFX Application"); // title bar
        stage.setScene(scene); 
        stage.show(); 
    }

    public static void main(String[] args) {
        launch(args);
        System.out.println("Hello, World!");
        Scanner input = new Scanner(System.in);
        System.out.print("Enter a number: ");
        int number = input.nextInt();
        System.out.println("You entered: " + number);
        // // main loop
        // do
        // {
        //     controller.update();
        //     model.update();
        //     view.update(); // view.repaint();
        // } while (true);
    }
}
