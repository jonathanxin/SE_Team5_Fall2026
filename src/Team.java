import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Team {
    private ObservableList<Player> teamList;

    public Team() {
        teamList = FXCollections.observableArrayList(Player.extractor);
    }

    public ObservableList<Player> getTeamList() {
        return teamList;
    }
}
