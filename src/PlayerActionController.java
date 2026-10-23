import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class PlayerActionController {
    private Model model;
    private ObservableList<Player> greenTeamList;
    private ObservableList<Player> redTeamList;
    private SortedList<Player> sortedGreenTeamList;
    private SortedList<Player> sortedRedTeamList;

    @FXML
    private ListView<Player> gameActionsListView;

    @FXML
    private TableColumn<Player, Number> greenTeamMemberScore;

    @FXML
    private TableColumn<Player, String> greenTeamMemeberCol;

    @FXML
    private TableView<Player> greenTeamTableView;

    @FXML
    private TableColumn<Player, String> redTeamMemberCol;

    @FXML
    private TableColumn<Player, Number> redTeamMemberScore;

    @FXML
    private TableView<Player> redTeamTableView;

    public void setModel(Model model) {
        this.model = model;
    }

    public void initializeTeams() {
        greenTeamList = model.getGreenTeamList();
        redTeamList = model.getRedTeamList();
        // using SortedList to respond to changes in the underlying ObservableList and sort by score in descending order (comparator)
        sortedGreenTeamList = new SortedList<>(greenTeamList);
        sortedGreenTeamList.setComparator((p1, p2) -> Integer.compare(p2.getScore(), p1.getScore()));
        sortedRedTeamList = new SortedList<>(redTeamList);
        sortedRedTeamList.setComparator((p1, p2) -> Integer.compare(p2.getScore(), p1.getScore()));
        greenTeamTableView.setItems(sortedGreenTeamList);
        redTeamTableView.setItems(sortedRedTeamList);

        // cell factories:
        // cell value factories define what data is displayed in the table view
        // getValue() returns the Player object for the current row
        greenTeamMemeberCol.setCellValueFactory(player -> player.getValue().getCodenameProperty());
        greenTeamMemberScore.setCellValueFactory(player -> player.getValue().scoreProperty());
        redTeamMemberCol.setCellValueFactory(player -> player.getValue().getCodenameProperty());
        redTeamMemberScore.setCellValueFactory(player -> player.getValue().scoreProperty());
        
        // disable selection 
        greenTeamTableView.getSelectionModel().setCellSelectionEnabled(false);
        redTeamTableView.getSelectionModel().setCellSelectionEnabled(false);
    }
}
