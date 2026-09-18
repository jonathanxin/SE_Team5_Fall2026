// handles user input and updates model on events
//  delegates to model by calling corresponding methods
//  needs a model object
// forwards model data to view for display
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Button;

public class Controller {

    @FXML
    private Label label;

    @FXML 
    private Button button;

    private Model model;

    public void update() {
        // Implement the logic to update the controller state
    }

    public void initialize() {
        // called by FXMLLoader after the fxml file has been loaded
    }

    @FXML
    private void handleButtonAction(ActionEvent event) {
        label.setText("Hello World!");
    }
}
