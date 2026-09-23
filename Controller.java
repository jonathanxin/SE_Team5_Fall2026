// handles user input and updates model on events
//  delegates to model by calling corresponding methods
//  needs a model object
// forwards model data to view for display
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;
import javafx.scene.Node;

public class Controller {
    private Model model;

    @FXML 
    private GridPane playerGrid;

    @FXML
    private Button changeNetworkButton;

    @FXML
    private TextField networkAddress;


    @FXML
    void changeNetworkButtonPress(ActionEvent event) {
        // TODO: implement logic to change network
        System.out.println("Change Network button pressed");
        System.out.println("Network address: " + networkAddress.getText());
    }

    public void update() {
        // Implement the logic to update the controller state
    }

    public void initialize() {
        // called by FXMLLoader after the fxml file has been loaded
        // initialize all player fields in playerGrid with properties and listeners
        for (Node node : playerGrid.getChildren()) {
            if (node instanceof TextField playerField) {
                playerField.setFocusTraversable(false); // prevent default Tab behavior of moving focus to the next field
                playerField.setMouseTransparent(true); // prevent mouse clicks from stealing focus
                playerField.focusedProperty().addListener((observable, oldValue, newValue) -> { // bind change listener to each player fields focusedProperty
                    handlePlayerFieldFocus(new ActionEvent(playerField, null), newValue);
                });
            }
        }
        // set focus to the first player field
        TextField firstField = (TextField) playerGrid.getChildren().get(0);
        Platform.runLater(firstField::requestFocus); // request focus on the first field after the scene is rendered
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