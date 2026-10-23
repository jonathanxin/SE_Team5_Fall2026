// TODO: create countdown timer for game start
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class Main extends Application {
    PlayerEntryController playerEntryController;
    Model model;    
    Networking networking;
    PlayerDatabase playerDatabase;
    Stage stage;

    @Override 
    public void init() throws Exception {
        // called before start() and before the GUI is initialized (runs on background thread)
        // setup ntworking
        networking = new Networking(); // initialize networking
        networking.setupUdpSockets(); // setup UDP sockets
        networking.startListening(); // start listening for incoming packets

        // setup database
        playerDatabase = new PlayerDatabase(); // initialize player database

        model = new Model(playerDatabase, networking); // initialize model with networking
    }

    @Override 
    public void stop() throws Exception {
        // called when the application is shutting down
        networking.closeUdpSockets(); // close UDP sockets
    }

    @Override
    public void start(Stage stage) throws Exception { // main entry point for JavaFX application
        // stage is top-level JavaFX container (window)
        this.stage = stage;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/PlayerEntryView.fxml")); // loads first FXML file (Player Entry Screen)
        Parent root = loader.load(); // returns root node of the scene graph (initialize() in Controller is called here)
        playerEntryController = loader.getController(); // get the controller instance that FXML loader created
        model.setplayerEntryController(playerEntryController); // set controller in model
        model.setApp(this); // set the main application in the model
        Scene scene = new Scene(root); // initialize scene (container for all content) with root node (can access scene now (not null))

        // stage (window) information
        stage.setTitle("Photon"); // title bar
        stage.setScene(scene); // set the scene on the stage (can access stage (window) now (not null))
        stage.show();  // real sizes of the scene and stage are now available (can access stage (window) now (not null))
        // set minumum window size to the that of the splash screen
        // caluclate window decoration sizes (title bar, borders, etc.) and add to the scene size to get the minimum window size
        // TODO: fix minimum Height being too small on Linux
        double decorW = stage.getWidth() - scene.getWidth();
        double decorH = stage.getHeight() - scene.getHeight();
        stage.setMinWidth(scene.getWidth() + decorW);
        stage.setMinHeight(scene.getHeight() + decorH);
        // code below runs after GUI is fully initialized and displayed
        playerEntryController.setModel(model); // set model in controller
        playerEntryController.onSceneReady(); // update controller state after window is initialized
    }

    public PlayerActionController switchScene() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/PlayerActionView.fxml"));
        AnchorPane root = loader.load();
        
        // keep the current stage size when switching scenes
        double stageWidth = stage.getWidth();
        if (!Double.isNaN(stageWidth)) {
            stageWidth -= (stage.getWidth() - stage.getScene().getWidth());
        }
        
        double stageHeight = stage.getHeight();
        if (!Double.isNaN(stageHeight)) {
            stageHeight -= (stage.getHeight() - stage.getScene().getHeight());
        }
        
        Scene scene = new Scene(root);
        if (!Double.isNaN(stageWidth)) {
            root.setPrefWidth(stageWidth);
        }
        if (!Double.isNaN(stageHeight)) {
            root.setPrefHeight(stageHeight);
        }
        
        stage.setScene(scene);
        stage.sizeToScene();
        return (PlayerActionController) loader.getController();
    }

    public static void main(String[] args) {
        // code here runs before JavaFX application thread
        launch(args); // main JavaFX application thread (blocking)
        // code here runs only after the GUI closes
    }
}
