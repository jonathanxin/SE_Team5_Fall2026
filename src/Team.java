// TODO: add a way to store team score and make it observable so that the GUI can update when the score changes
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

    public void addPlayer(Player player) {
        teamList.add(player);
    }
}
