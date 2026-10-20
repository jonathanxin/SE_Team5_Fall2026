// TODO: sort team lists by player scores
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

public class PlayerActionController {
    private Model model;
    private ObservableList<Player> greenTeamList;
    private ObservableList<Player> redTeamList;
    private SortedList<Player> sortedGreenTeamList;
    private SortedList<Player> sortedRedTeamList;

    @FXML
    private ListView<Player> gameActionsListView;

    @FXML
    private ListView<Player> greenTeamListView;

    @FXML
    private ListView<Player> redTeamListView;

    public void setModel(Model model) {
        this.model = model;
    }

    public void initializeTeams() {
        greenTeamList = model.getSortedGreenTeamList();
        redTeamList = model.getSortedRedTeamList();
        sortedGreenTeamList = new SortedList(greenTeamList);
        sortedGreenTeamList.setComparator((p1, p2) -> Integer.compare(p2.getScore(), p1.getScore()));
        sortedRedTeamList = new SortedList(redTeamList);
        sortedRedTeamList.setComparator((p1, p2) -> Integer.compare(p2.getScore(), p1.getScore()));
        greenTeamListView.setItems(sortedGreenTeamList);
        redTeamListView.setItems(sortedRedTeamList);
    }
}
