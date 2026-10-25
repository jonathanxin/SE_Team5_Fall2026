import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.ChangeListener;

public class Main extends Application {
    PlayerEntryController playerEntryController;
    Model model;    
    Networking networking;
    PlayerDatabase playerDatabase;
    Stage stage;


    double initW = 0.0;
    double initH = 0.0;
    double[] decor = {0.0, 0.0};

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
        initW = scene.getWidth();
        initH = scene.getHeight();
        System.out.println("Scene initial size: " + initW + " x " + initH);
        Runnable updateMinSize = () -> {
            stage.setMinWidth(initW + decor[0]);
            stage.setMinHeight(initH + decor[1]);
        };
        // caluclate window decoration sizes (title bar, borders, etc.) and add to the scene size to get the minimum window size
        // Windows (decorations exist after stage.show() is called)
        double decorTemp = stage.getHeight() - scene.getHeight();
        if (decorTemp > 0) {
            System.out.println("Decorations exist after stage.show() is called (Windows)");
            decor[0] = stage.getWidth() - scene.getWidth();
            decor[1] = decorTemp;
            updateMinSize.run();
        }
        else {
            // Linux (decorations are applied asynchronously after stage.show() is called, so we need to wait for the height to change)
            stage.heightProperty().addListener(new ChangeListener<>() {
                @Override 
                public void changed(ObservableValue<? extends Number> obs, Number oldValue, Number newValue) {
                    if (stage.getHeight() - scene.getHeight() > 0) {
                        System.out.println("Decorations exist after stage.show() is called (Linux)");
                        decor[0] = stage.getWidth() - scene.getWidth();
                        decor[1] = stage.getHeight() - scene.getHeight();
                        updateMinSize.run();
                        stage.heightProperty().removeListener(this); // remove the listener after it has run once
                    }
                }
            });
        }
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
