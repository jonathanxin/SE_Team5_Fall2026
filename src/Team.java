// TODO: add a way to store team score and make it observable so that the GUI can update when the score changes
import javafx.beans.binding.IntegerBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Team {
    private ObservableList<Player> teamList;
    private IntegerBinding teamScore;

    public Team() {
        teamList = FXCollections.observableArrayList(Player.extractor);
        teamScore = new IntegerBinding() {
            {
                super.bind(teamList);
            }

            @Override 
            protected int computeValue() {
                int totalScore = 0;
                for (Player player : teamList) {
                    totalScore += player.getScore();
                }
                return totalScore;
            }
        };
    }

    public ObservableList<Player> getTeamList() {
        return teamList;
    }

    public void addPlayer(Player player) {
        teamList.add(player);
    }

    public int getTeamScore() {
        return teamScore.get();
    }

    public IntegerBinding teamScore() {
        return teamScore;
    }

    public Player getplayerByEquipmentId(int equipmentId) {
        for (Player player : teamList) {
            if (player.getEquipmentId() == equipmentId) {
                return player;
            }
        }
        return null; // player not found
    }
}
