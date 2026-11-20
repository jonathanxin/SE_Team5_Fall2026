import java.net.URL;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.binding.When;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.WeakChangeListener;
import javafx.application.Platform;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.collections.ObservableList;
import javafx.collections.ListChangeListener;
import javafx.collections.transformation.SortedList;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollBar;
import javafx.scene.input.ScrollEvent;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class PlayerActionController {
    private Model model;
    private ObservableList<Player> greenTeamList;
    private ObservableList<Player> redTeamList;
    private SortedList<Player> sortedGreenTeamList;
    private SortedList<Player> sortedRedTeamList;
    private Team greenTeam;
    private Team redTeam;
    private final static PseudoClass HIGHEST_SCORE = PseudoClass.getPseudoClass("highest-score");
    private Image baseIconImage;
    private ObjectBinding<Player> highestGreenScorePlayer;
    private ObjectBinding<Player> highestRedScorePlayer;
    private boolean gameActionsAutoScroll = true;
    private ScrollBar gameActionsScrollBar;

    @FXML
    private ListView<String> gameActionsListView;

    @FXML
    private TableColumn<Player, Number> greenTeamMemberScore;

    @FXML
    private TableColumn<Player, String> greenTeamMemberCol;

    @FXML
    private TableView<Player> greenTeamTableView;

    @FXML
    private TableColumn<Player, String> redTeamMemberCol;

    @FXML
    private TableColumn<Player, Number> redTeamMemberScore;

    @FXML
    private TableView<Player> redTeamTableView;

    @FXML
    private Label redTeamScore;
    @FXML 
    private Label greenTeamScore;


    public void setModel(Model model) {
        this.model = model;
    }
    
    public void initializeTeams() {
        greenTeam = model.getGreenTeam();
        redTeam = model.getRedTeam();
        greenTeamList = greenTeam.getTeamList();
        redTeamList = redTeam.getTeamList();
        // using SortedList to respond to changes in the underlying ObservableList and sort by score in descending order (comparator)
        // sortedGreenTeamList = new SortedList<>(greenTeamList);
        // sortedGreenTeamList.setComparator((p1, p2) -> Integer.compare(p2.getScore(), p1.getScore()));
        // sortedRedTeamList = new SortedList<>(redTeamList);
        // sortedRedTeamList.setComparator((p1, p2) -> Integer.compare(p2.getScore(), p1.getScore()));
        greenTeamTableView.setItems(greenTeamList);
        redTeamTableView.setItems(redTeamList);

        initializeBaseIcon();

        // cell factories:
        // cell value factories define what data is displayed in the table view
        // getValue() returns the Player object for the current row
        greenTeamMemberCol.setCellValueFactory(player -> player.getValue().codenameProperty());
        greenTeamMemberScore.setCellValueFactory(player -> player.getValue().scoreProperty());
        redTeamMemberCol.setCellValueFactory(player -> player.getValue().codenameProperty());
        redTeamMemberScore.setCellValueFactory(player -> player.getValue().scoreProperty());
        // cell factories to define how the data is displayed in the table view
        greenTeamMemberCol.setCellFactory(p -> createMemberCell());
        redTeamMemberCol.setCellFactory(p -> createMemberCell());

        // disable selection
        greenTeamTableView.setSelectionModel(null);
        greenTeamTableView.setMouseTransparent(true);
        redTeamTableView.setSelectionModel(null);
        redTeamTableView.setMouseTransparent(true);

        // create data model for the game actions list view
        //ObservableList<String> gameActions = FXCollections.observableArrayList(); //I moved this into model 
        gameActionsListView.setItems(model.getGameActions()); //changed this from gameActionsListView.setItems(gameActions); to connect it to model
        gameActionsListView.scrollTo(model.getGameActions().size() - 1); // scroll to the bottom of the list view when new items are added
        // TODO: disable selection in the list view
        gameActionsListView.setSelectionModel(null);
        // Track whether the user is at the bottom so new actions do not interrupt manual scrolling.
        gameActionsListView.skinProperty().addListener((obs, oldSkin, newSkin) ->
            Platform.runLater(this::configureGameActionsScrollBar));
        Platform.runLater(this::configureGameActionsScrollBar);
        gameActionsListView.addEventFilter(ScrollEvent.SCROLL, event -> {
            if (event.getDeltaY() != 0) {
                gameActionsAutoScroll = false;
                Platform.runLater(this::updateGameActionsAutoScroll);
            }
        });
        model.getGameActions().addListener((ListChangeListener<String>) change -> {
            while (change.next()) {
                if (change.wasAdded() && gameActionsAutoScroll) {
                    Platform.runLater(() -> {
                        if (gameActionsAutoScroll && !model.getGameActions().isEmpty()) {
                            gameActionsListView.scrollTo(model.getGameActions().size() - 1);
                        }
                    });
                }
            }
        });

        // bind team score labels to team score properties
        redTeamScore.textProperty().bind(model.getRedTeam().teamScore().asString());
        greenTeamScore.textProperty().bind(model.getGreenTeam().teamScore().asString());

        // create bindings for the highest score pseudo-class for each team table view
        highestGreenScorePlayer = Bindings.createObjectBinding(() -> {
            if (greenTeamList.isEmpty()) {
                return null;
            }
            Player highestPlayer = greenTeamList.get(0);
            for (Player player : greenTeamList) {
                if (player.getScore() > highestPlayer.getScore()) {
                    highestPlayer = player;
                }
            }
            return highestPlayer;
        }, greenTeamList);
        highestRedScorePlayer = Bindings.createObjectBinding(() -> {
            if (redTeamList.isEmpty()) {
                return null;
            }
            Player highestPlayer = redTeamList.get(0);
            for (Player player : redTeamList) {
                if (player.getScore() > highestPlayer.getScore()) {
                    highestPlayer = player;
                }
            }
            return highestPlayer;
        }, redTeamList);
    }

    private void configureGameActionsScrollBar() {
        if (gameActionsScrollBar != null) {
            return;
        }

        for (Node node : gameActionsListView.lookupAll(".scroll-bar")) {
            if (node instanceof ScrollBar) {
                ScrollBar scrollBar = (ScrollBar) node;
                if (scrollBar.getOrientation() == Orientation.VERTICAL) {
                    gameActionsScrollBar = scrollBar;
                    updateGameActionsAutoScroll();
                    scrollBar.valueProperty().addListener((obs, oldVal, newVal) ->
                        updateGameActionsAutoScroll());
                    return;
                }
            }
        }
    }

    private void updateGameActionsAutoScroll() {
        if (gameActionsScrollBar != null) {
            gameActionsAutoScroll = gameActionsScrollBar.getValue() >= gameActionsScrollBar.getMax();
        }
    }

    private void initializeBaseIcon() {
        String baseIconPath = "/resources/baseicon.jpg";
        URL baseIconURL = getClass().getResource(baseIconPath);
        baseIconImage = new Image(baseIconURL.toString());
    }

    private TableCell<Player, String> createMemberCell() {
        return new TableCell<Player, String>() {
            ImageView cellIcon = new ImageView(baseIconImage);
            Player player;
            ChangeListener<Player> highestGreenScorePlayerListener = (obs, oldVal, newVal) -> {
                updateHighestScoreState();
            };
            ChangeListener<Player> highestRedScorePlayerListener = (obs, oldVal, newVal) -> {
                updateHighestScoreState();
            };
            {
                cellIcon.setPreserveRatio(true);
                cellIcon.fitHeightProperty().bind(Bindings.createDoubleBinding(
                    () -> Math.max(0, getHeight() - getInsets().getTop() - getInsets().getBottom()), // remove padding from height to prevent cell from growing when graphic is added
                    heightProperty()));

                // create listeners to update the pseudo-class state when the highest score player changes
                highestGreenScorePlayer.addListener(new WeakChangeListener<>(highestGreenScorePlayerListener));
                highestRedScorePlayer.addListener(new WeakChangeListener<>(highestRedScorePlayerListener));
            }
            private void updateHighestScoreState() {
                boolean isHighestScore = (player != null) && (player == highestGreenScorePlayer.get() || player == highestRedScorePlayer.get());
                pseudoClassStateChanged(HIGHEST_SCORE, isHighestScore);
            }
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                graphicProperty().unbind(); // prevent modifying graphic while the cell is bound

                if (empty || item == null) {
                    player = null;
                    setText(null);
                    setGraphic(null);
                    pseudoClassStateChanged(HIGHEST_SCORE, false); // reset pseudo-class state when cell is empty
                    return;
                }

                // set cell's graphic to base icon if player in cell hit the base
                setText(item);
                player = getTableRow().getItem(); // update player reference to the current row's player
                graphicProperty().bind(
                    new When(player.hitBaseProperty()).then(cellIcon).otherwise((ImageView) null));
                updateHighestScoreState(); // update pseudo-class state when the cell is updated
            }
        };
    }
}