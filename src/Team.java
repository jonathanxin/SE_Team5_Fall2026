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
        
        teamScore.set(teamScore.get() + player.getScore());

        //listen for changes in player score and update the teamscore accordingly
        player.scoreProperty().addListener((observable, oldValue, newValue) -> {
            int difference = newValue.intValue() - oldValue.intValue();
            teamScore.set(teamScore.get() + difference);
        });
    }
    public IntegerProperty teamScoreProperty() {
        return teamScore;
    }

    public int getTeamScore() {
        return teamScore.get();
    }
    public void clearTeam() {
        teamList.clear();
        teamScore.set(0); //reset score from previous game
    }
    
    
}
