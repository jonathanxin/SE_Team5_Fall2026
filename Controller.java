// handles user input and updates model on events
//  delegates to model by calling corresponding methods
//  needs a model object
// forwards model data to view for display
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.control.Button;

public class Controller {
    private Model model;

    public void update() {
        // Implement the logic to update the controller state
    }

    public void initialize() {
        // called by FXMLLoader after the fxml file has been loaded
    }

    @FXML
    private Button changeNetworkButton;

    @FXML 
    void networkButtonPressed(ActionEvent event) {
        // handle button press event
        System.out.println("Change Network button pressed");

    }

    @FXML
    private TextField networkField;


    @FXML
    void networkFieldKeyPress(KeyEvent event) {
        // grab entered text on enter key press
        if (event.getCode() == KeyCode.ENTER) {
            String networkName = networkField.getText();
            // output text to console for testing purposes
            System.out.println("Network name entered: " + networkName);
        }
    }

    @FXML // fx:id="playerEntryField"
    private TextField playerEntryField; 

    @FXML // called when user types (key press) in the playerEntryField
    void jumpToNextField(KeyEvent event) {
        // get player entry on enter key press
        if (event.getCode() == KeyCode.ENTER) {
            String playerName = playerEntryField.getText();
            // output text to console for testing purposes
            System.out.println("Player name entered: " + playerName);
        }
        // jump to next field when user presses the tab key
        if (event.getCode() == KeyCode.TAB) {
            System.out.println("tab key pressed");
        }
    }
}