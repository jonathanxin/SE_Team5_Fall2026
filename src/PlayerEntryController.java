// handles user input and updates model on events
//  delegates to model by calling corresponding methods
// forwards model data to view for display via bindings and listeners
// TODO: center buttons vertically when resizing window

import javafx.util.Duration;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.When;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Label;
import javafx.scene.image.Image;

public class PlayerEntryController { 
    private Model model;
    private BooleanProperty addingPlayer = new SimpleBooleanProperty(false);
    private BooleanProperty enteringEquipment = new SimpleBooleanProperty(false);
    private String currentNickname;
    private Timeline countdownTimeline;
    private TextField[][] playerFields = new TextField[20][4];
    private TextField currentTeamPlayerField;
    private TextField otherTeamPlayerField;
    
    public void setModel(Model model) {  
        this.model = model;
    }

    @FXML 
    private GridPane playerGrid;

    @FXML
    private Button changeNetworkButton;

    @FXML
    private TextField networkAddressField;

    @FXML
    private ImageView splashScreen;

    @FXML
    private Button startGameButton;

    @FXML
    private Button clearEntriesButton;

    @FXML
    void clearEntriesButtonAction(ActionEvent event) {
        clearPlayerEntries();
    }

    private final static PseudoClass INVALID_DATA = PseudoClass.getPseudoClass("invalid-data");
    BooleanProperty invalidDataBooleanProperty = new SimpleBooleanProperty(false);

    @FXML
    private ImageView countdownImageView;

    @FXML
    void changeNetworkButtonPress(ActionEvent event) {
        System.out.println("Change Network button pressed");
        if (networkAddressField.getText().isEmpty()) {
            System.out.println("Network address field is empty. Please enter a valid network address.");
            return;
        }
        System.out.println("Network address: " + networkAddressField.getText());
        model.setNetworkAddress(networkAddressField.getText());
    }

    @FXML
    void networkAddressFieldOnAction(ActionEvent event) {
        event.consume(); // prevent dupplicate action events from firing (button press and textfield onAction)
    }

    public void onSceneReady() {
        // Implement the logic to update the controller state after the window is initialized
        // only logic that requires scene or stage information should be placed here (e.g., getParent(), getScene(), getWindow(), getScreenBounds(), etc.)

        StackPane parent = (StackPane) splashScreen.getParent(); // get StackPane (root)

        // bind ImageView to parent to allow resizing of the splash screen image
        splashScreen.fitHeightProperty().bind(parent.heightProperty());
        splashScreen.fitWidthProperty().bind(parent.widthProperty());

        // capture scene-wide key presses using a key event filter to prevent nodes from consuming the event instead
        parent.getScene().addEventFilter(KeyEvent.KEY_PRESSED, event -> handleGlobalKeyPresses(event));

        // bind countdownImageView to to window size to allow resizing of the countdown image
        countdownImageView.fitHeightProperty().bind(parent.heightProperty());
        countdownImageView.fitWidthProperty().bind(parent.widthProperty());
    }

    public void initialize() {
        // called by FXMLLoader after the fxml file has been loaded and all @FXML annotated members have been injected (all elements can be binded, listeners can be added, and properties can be set)
        // NOTE: initialze() is called before parent layout is attatched to scene or a stage (cannot request window actions (e.g., geting screen bounds))

        StackPane parent = (StackPane) splashScreen.getParent();
        
        // change ImageView to be visible (set invisible to allow for easier editing of the FXML file in SceneBuilder)
        splashScreen.setVisible(true);
        // set parent's background fill to black to prevent background showing through the letterboxing when resizing the window during splash screen display
        parent.setStyle("-fx-background-color: black;");
        // set BorderPane to invisible to prevent it from showing through the letterboxing when resizing the window during splash screen display
        parent.getChildren().getFirst().setVisible(false);

        // initialize all player fields in playerGrid and add fields to an array for easier access
        for (Node node : playerGrid.getChildren()) {
            if (node instanceof TextField playerField) {
                playerField.setFocusTraversable(false); // prevent default Tab behavior of moving focus to the next field
                playerField.setMouseTransparent(true); // prevent mouse clicks from stealing focus
                // playerField.mouseTransparentProperty()
                playerField.focusedProperty().addListener((observable, oldValue, newValue) -> { // add change listener to each player fields focusedProperty
                    handlePlayerFieldFocus(playerField, newValue);
                });
                Integer boxedRow = GridPane.getRowIndex(playerField);
                int row = (boxedRow == null) ? 0 : boxedRow; // unset row index defaults to 0
                Integer boxedCol = GridPane.getColumnIndex(playerField);
                int col = (boxedCol == null) ? 0 : boxedCol; // unset column index defaults to 0
                playerFields[row][col] = playerField;
            }
        }

        // make splashScreen fade out after 3 seconds and then pop it from the StackPane
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(1), splashScreen);
        fadeOut.setDelay(Duration.seconds(3)); // delay fade out for 3 seconds
        fadeOut.setFromValue(1.0); // start fully visible
        fadeOut.setToValue(0.0); // end fully transparent
        fadeOut.statusProperty().addListener((observable) -> { // add invalidation listner to statusProperty of fadeOut to remove splashScreen from parent StackPane after fade out is complete
            if (fadeOut.getStatus() == Animation.Status.STOPPED) {
                parent.getChildren().remove(splashScreen);
                parent.setStyle(""); // reset parent's background fill to default to allow background to show through the letterboxing when resizing the window
                parent.getChildren().getFirst().setVisible(true); // set BorderPane (first child of StackPane) to visible
                // set initial focus and mouse transparencies to first player fields for each team (green team first)
                // green team first player field (set focus and mouse transparency to allow user input)
                TextField greenFirstField = playerFields[0][1];
                currentTeamPlayerField = greenFirstField;
                greenFirstField.setPromptText("Enter Player ID");
                greenFirstField.setMouseTransparent(false); // allow mouse clicks to focus the field
                greenFirstField.requestFocus(); // request focus on the first field (focus can only be set after scene and stage are initialized)
                // red team first player field (set mouse transparency only)
                TextField redFirstField = playerFields[0][3];
                otherTeamPlayerField = redFirstField;
                otherTeamPlayerField.setPromptText("Enter Player ID");
                otherTeamPlayerField.mouseTransparentProperty().bind(addingPlayer.or(enteringEquipment)); 

                System.out.println("Splash screen removed from parent StackPane");
            }
        });
        fadeOut.play(); // start fade out transition

        changeNetworkButton.disableProperty().bind(addingPlayer.or(enteringEquipment)); // disable mouse clicks on networkAddressField when adding player or entering equipment
        
        // testing end of player fields
        // playerFields[19][1].setMouseTransparent(false);
        // currentTeamPlayerField = playerFields[19][1]; // set currentTeamPlayerField to last player field for green team
        // playerFields[19][3].setMouseTransparent(false);
        // otherTeamPlayerField = playerFields[19][3]; // set otherTeamPlayerField to last player field for red team
        // otherTeamPlayerField.mouseTransparentProperty().bind(addingPlayer.or(enteringEquipment)); // bind mouse transparency of last player field for red team to addingPlayer and enteringEquipment properties
    }

    private void handleGlobalKeyPresses(KeyEvent event) {
        if (event.getCode() == KeyCode.F12) {
            event.consume();
            clearPlayerEntries();
        } else if (event.getCode() == KeyCode.F5) {
            event.consume();
            startGameButtonAction(null);
        }
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

    private void startCountdown(ImageView countdownImageView) {
        // Ignore additional F5 presses while counting down.
        if (countdownTimeline != null
                && countdownTimeline.getStatus() == Animation.Status.RUNNING) {
            return;
        }

        StackPane parent = (StackPane) countdownImageView.getParent(); // get StackPane (root)
        parent.setStyle("-fx-background-color: black;");
        // set BorderPane to invisible to prevent it from showing through the letterboxing when resizing the window during countdown display
        parent.getChildren().getFirst().setVisible(false);

        final int[] secondsLeft = {30};
    
        // Display 30 immediately.
        showCountdownNumber(30);
        countdownImageView.setVisible(true);
    
        countdownTimeline = new Timeline(
            new KeyFrame(Duration.seconds(1), event -> {
                secondsLeft[0]--;
                showCountdownNumber(secondsLeft[0]);
            })
        );
    
        countdownTimeline.setCycleCount(30);
    
        // Runs only when all 30 seconds finish, not when stop() is called.
        countdownTimeline.setOnFinished(event -> {
            countdownImageView.setVisible(false);
            model.startGame();
        });
    
        countdownTimeline.play();
    }

    private void showCountdownNumber(int seconds) {
        String imagePath =
                "/resources/countdown/countdown" + seconds + ".jpg";
    
        var imageUrl = getClass().getResource(imagePath);
    
        if (imageUrl == null) {
            throw new IllegalStateException(
                    "Missing countdown image: " + imagePath);
        }
    
        countdownImageView.setImage(new Image(imageUrl.toExternalForm()));
    }

    private void clearPlayerEntries() {
        if (countdownTimeline.getStatus() == Animation.Status.RUNNING) {
            return; // don't clear entries if countdown is running
        }

        countdownImageView.setVisible(false);
        countdownImageView.setImage(null);
        
        for (Node node : playerGrid.getChildren()) {
            if (node instanceof TextField playerField) {
                playerField.clear();
                playerField.setPromptText("");
            }
        }

        addingPlayer.set(false);
        enteringEquipment.set(false);
        currentNickname = null;

        TextField greenTeamFirstField = playerFields[0][1]; // first player field for green team
        currentTeamPlayerField = greenTeamFirstField;
        greenTeamFirstField.setPromptText("Enter Player ID");
        greenTeamFirstField.mouseTransparentProperty().unbind(); // prevent edge case of resetting on first row
        greenTeamFirstField.setMouseTransparent(false);
        greenTeamFirstField.requestFocus();
        TextField redTeamFirstField = playerFields[0][3]; // first player field for red team
        otherTeamPlayerField = redTeamFirstField;
        redTeamFirstField.setPromptText("Enter Player ID");
        redTeamFirstField.mouseTransparentProperty().bind(addingPlayer.or(enteringEquipment)); 
        model.clearSessionPlayers(); // clear the session players in the model
    }

    // helper method to validate player entry and shift focus on Tab key press
    private void handleTabKeyPress(TextField playerField) {
        int playerId = -1;
        String input = playerField.getText().trim();
        // get index (col) of current player field in the grid to determine team
        int col = GridPane.getColumnIndex(playerField);
        invalidDataBooleanProperty.setValue(false); // reset invalid data boolean property to false to remove the pseudo-class state for new input
        BooleanBinding invalidDataCondition = playerField.focusedProperty().and(invalidDataBooleanProperty); // create a BooleanBinding to track the invalid data condition (focused and invalid data)
        invalidDataCondition.addListener((observable, oldValue, newValue) -> playerField.pseudoClassStateChanged(INVALID_DATA, newValue)); // add change listener to the BooleanBinding to update the pseudo-class state of the player field when the invalid data condition changes
    
        if (input.isEmpty()) {
            return;
        }
    
        // Final step: collect and transmit the equipment ID.
        if (enteringEquipment.get()) {
            int equipmentId;
    
            try {
                equipmentId = Integer.parseInt(input);
    
                if (equipmentId < 0) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException e) {
                playerField.clear();
                playerField.setPromptText("Enter a valid equipment ID");
                invalidDataBooleanProperty.setValue(true); // set invalidDataBooleanProperty to true to trigger the pseudo-class state change
                return;
            }
    
            model.transmitEquipmentId(equipmentId);
            playerField.setText(currentNickname);
            enteringEquipment.set(false);
            model.addSessionPlayer(col, equipmentId); // add player to session's list with the current index
            changePlayerFieldFocus(playerField);
            return;
        }
    
        // New player: save their nickname before requesting equipment.
        if (addingPlayer.get()) {
            boolean saved = model.addPlayerToDatabase(input);
    
            if (!saved) {
                System.err.println("Player was not saved. Check the database.");
                return;
            }
    
            currentNickname = input;
            addingPlayer.set(false);
            enteringEquipment.set(true);
    
            playerField.clear();
            playerField.setPromptText("Enter Equipment ID");
            return;
        }
    
        // First step: look up the player ID.    
        try {
            playerId = Integer.parseInt(input);
    
            if (playerId < 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            playerField.clear();
            playerField.setPromptText("Enter a valid player ID");
            invalidDataBooleanProperty.setValue(true); // set invalidDataBooleanProperty to true to trigger the pseudo-class state change
            return;
        }
    
        currentNickname = model.findPlayerById(playerId);
        playerField.clear();
    
        if (currentNickname == null) {
            addingPlayer.set(true);
            playerField.setPromptText("Enter Nickname");
        } else {
            enteringEquipment.set(true);
            playerField.setPromptText("Enter Equipment ID");
        }
    }

    // handles shifting focus after a player field is completed (on Tab key press)
    private void changePlayerFieldFocus(TextField playerField) {
        // private helper method to change the focus of the player field and update the UI accordingly
        // shift focus to next playerField on same team (col) if valid
        int currentRow = GridPane.getRowIndex(playerField);
        int currentCol = GridPane.getColumnIndex(playerField);
        if (currentRow == 19) {
            // remove focus from last field
            playerField.getParent().requestFocus(); // shift focus to parent node (GridPane) to remove focus from the last field
            playerField.mouseTransparentProperty().unbind(); 
            playerField.setMouseTransparent(true); 
            return; 
        }
        TextField nextField = playerFields[currentRow + 1][currentCol];
        nextField.setPromptText("Enter Player ID");
        nextField.requestFocus(); 
        nextField.setMouseTransparent(false); // allow mouse clicks to focus the next field
        currentTeamPlayerField = nextField;
        playerField.setMouseTransparent(true); // remove mouse transparency for completed fields
    }

    // called when playerFieled changes focus
    void handlePlayerFieldFocus(TextField playerField, boolean isFocused) {
        // System.out.println("Player field focused: " + playerGrid.getChildren().indexOf(playerField)); // debug output to console
        // changing focus to another player Field
        if (isFocused) {
            if (playerField == otherTeamPlayerField) // changing focus to other team
            {   
                // swap currentTeamPlayerField and otherTeamPlayerField to allow for proper focus handling and mouse transparency
                otherTeamPlayerField.mouseTransparentProperty().unbind();
                TextField previousCurrentTeamPlayerField = currentTeamPlayerField;
                if (!currentTeamPlayerField.isMouseTransparent()) // other team is not complete
                {
                    previousCurrentTeamPlayerField.clear();
                    previousCurrentTeamPlayerField.setPromptText("Enter Player ID");
                    currentTeamPlayerField = playerField;
                    otherTeamPlayerField = previousCurrentTeamPlayerField;
                    otherTeamPlayerField.mouseTransparentProperty().bind(addingPlayer.or(enteringEquipment));
                }
                else
                {
                    currentTeamPlayerField = playerField;
                    otherTeamPlayerField = previousCurrentTeamPlayerField;
                }
            }
        }
        else // changing focus away from a player Field
        {
            if (networkAddressField.isFocused()) // focus in on networkAddressField
            {
                if (!currentTeamPlayerField.isMouseTransparent()) // a team is not complete
                {
                    currentTeamPlayerField.clear();
                    currentTeamPlayerField.setPromptText("Enter Player ID");
                }
            }
        }
    }

    @FXML
    void startGameButtonAction(ActionEvent event) {
        startCountdown(countdownImageView); // start the countdown timer
    }
}
