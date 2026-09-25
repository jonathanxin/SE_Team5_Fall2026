import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    Controller controller;
    Model model;    

    // @Override 
    // public void init() throws Exception {
    //     // called before start() and before the GUI is initialized (runs on background thread)
    // }

    // @Override 
    // public void stop() throws Exception {
    //     // called when the application is shutting down
    // }

    @Override
    public void start(Stage stage) throws Exception { // main entry point for JavaFX application
        // stage is top-level JavaFX container (window)
        FXMLLoader loader = new FXMLLoader(getClass().getResource("View.fxml")); // load FXML file
        Parent root = loader.load(); // returns root node of the scene graph (initialize() in Controller is called here)
        controller = loader.getController(); // get the controller instance that FXML loader created
        Scene scene = new Scene(root); // initialize scene (container for all content) with root node (can access scene now (not null))

        // stage (window) information
        stage.setTitle("JavaFX Application"); // title bar
        stage.setScene(scene); // set the scene on the stage (can access stage (window) now (not null))
        stage.show();  // real sizes of the scene and stage are now available (can access stage (window) now (not null))
        // code below runs after GUI is fully initialized and displayed
        controller.setModel(model); // set model in controller
        controller.onSceneReady(); // update controller state after window is initialized
    }

    public static void main(String[] args) {
        // code here runs before JavaFX application thread
        launch(args); // main JavaFX application thread (blocking)
        // code here runs only after the GUI closes
    }
}
