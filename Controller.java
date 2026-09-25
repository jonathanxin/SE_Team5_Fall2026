// handles user input and updates model on events
//  delegates to model by calling corresponding methods
// forwards model data to view for display via bindings and listeners
import javafx.util.Duration;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.Node;
import javafx.scene.image.ImageView;

public class Controller {
    private Model model;

    public void setModel(Model model) {
        this.model = model;
    }

    @FXML 
    private GridPane playerGrid;

    @FXML
    private Button changeNetworkButton;

    @FXML
    private TextField networkAddress;

    @FXML
    private ImageView splashScreen;


    @FXML
    void changeNetworkButtonPress(ActionEvent event) {
        // TODO: implement logic to change network
        System.out.println("Change Network button pressed");
        System.out.println("Network address: " + networkAddress.getText());
    }

    public void onSceneReady() {
        // Implement the logic to update the controller state after the window is initialized
        // only logic that requires scene or stage information should be placed here (e.g., getParent(), getScene(), getWindow(), getScreenBounds(), etc.)

        StackPane parent = (StackPane) splashScreen.getParent(); // get StackPane (root)

        // bind ImageView to parent to allow resizing of the splash screen image
        splashScreen.fitWidthProperty().bind(parent.widthProperty());
        splashScreen.fitHeightProperty().bind(parent.heightProperty());

        // set focus to the first player field 
        TextField firstField = (TextField) playerGrid.getChildren().get(0);
        firstField.requestFocus(); // request focus on the first field (focus can only be set after scene and stage are initialized)
    }

    public void initialize() {
        // called by FXMLLoader after the fxml file has been loaded and all @FXML annotated fields have been injected (all elements can be binded, listeners can be added, and properties can be set)
        // NOTE: initialze() is called before parent layout is attatched to scene or a stage (cannot request window actions (e.g., geting screen bounds), or )

        // change ImageView to be visible (set invisible to allow for easier editing of the FXML file in SceneBuilder)
        splashScreen.setVisible(true);

        // initialize all player fields in playerGrid with properties and listeners
        for (Node node : playerGrid.getChildren()) {
            if (node instanceof TextField playerField) {
                playerField.setFocusTraversable(false); // prevent default Tab behavior of moving focus to the next field
                playerField.setMouseTransparent(true); // prevent mouse clicks from stealing focus
                playerField.focusedProperty().addListener((observable, oldValue, newValue) -> { // add change listener to each player fields focusedProperty
                    handlePlayerFieldFocus(new ActionEvent(playerField, null), newValue);
                });
            }
        }

        // make splashScreen fade out after 3 seconds and then pop it from the StackPane
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(3), splashScreen);
        fadeOut.setFromValue(1.0); // start fully visible
        fadeOut.setToValue(0.0); // end fully transparent
        fadeOut.statusProperty().addListener((observable) -> { // add invalidation listner to statusProperty of fadeOut to remove splashScreen from parent StackPane after fade out is complete
            if (fadeOut.getStatus() == Animation.Status.STOPPED) {
                StackPane parent = (StackPane) splashScreen.getParent();
                parent.getChildren().remove(splashScreen);
                System.out.println("Splash screen removed from parent StackPane");
            }
        });
        fadeOut.play(); // start fade out transition
    }

    // called onKeyPress in the player entry fields
    @FXML
    void handlePlayerFieldKeyPress(KeyEvent event) {
        // process player entry on Tab
        if (event.getCode() == KeyCode.TAB) {
            event.consume(); // prevent default Tab behavior of moving focus to the next field
            handleTabKeyPress((TextField) event.getSource());
        }
    }

    // helper method to validate player entry and shift focus on Tab key press
    private void handleTabKeyPress(TextField playerField) {
        boolean isValid = false; // placeholder for validation result
        // get player ID
        String playerId = playerField.getText();
        System.out.println("Player ID entered: " + playerId); // debug output to console
        // validate player ID is in database
        // TODO: implement validation logic here
        if (playerId != null && !playerId.isEmpty()) {
            // Example validation logic (replace with actual database check)
            isValid = true; // Placeholder for actual validation
        }
        // modify playerField properties for invalid entries
        if (!isValid) {
            playerField.setStyle("-fx-border-color: red;"); // change textfield border color to red for invalid entry
            playerField.clear(); // clear invalid entry
            playerField.setPromptText("Enter Nickname"); // set prompt text to indicate addition of valid player
            // call method to add player to database (TODO: implement actual database addition logic)
        }
        // shift focus to next field if valid
        else {
            int currentIndex = playerGrid.getChildren().indexOf(playerField);
            int nextIndex = (currentIndex + 1) % playerGrid.getChildren().size();
            if (nextIndex == 0) return; // don't loop back to the first field
            // prvents invalid cast exception if the next node is not a TextField (Group)
            if (playerGrid.getChildren().get(nextIndex) instanceof TextField nextField) {
                nextField.requestFocus();
            }
            playerField.setMouseTransparent(true); // remove mouse transparenct for completed fields
            playerField.setStyle(""); // reset textfield border color to default for valid entry
        }
    }

    // called when playerFieled changes focus (onFocusChange)
    @FXML
    void handlePlayerFieldFocus(ActionEvent event, boolean isFocused) {
        TextField playerField = (TextField) event.getSource();
        // System.out.println("Player field focused: " + playerGrid.getChildren().indexOf(playerField)); // debug output to console
        // set prompt text to "Enter Player ID"
        if (isFocused) {
            playerField.setPromptText("Enter Player ID");
            playerField.setMouseTransparent(false); // allow mouse clicks to focus the field
        }
    }
}