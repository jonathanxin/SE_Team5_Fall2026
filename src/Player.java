import javafx.beans.Observable;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.util.Callback;

public class Player {
    private String codename = "";
    private IntegerProperty score = new SimpleIntegerProperty(this, "score", 0);

    public Player(String codename) {
        this.codename = codename;
    }

    public String getCodename() {
        return codename;
    }

    public void setScore(int score) {
        this.score.set(score);
    }

    public int getScore() {
        return score.get();
    }

    public IntegerProperty scoreProperty() {
        return score;
    }

    // extractor for the ObservableList to listen to changes in the score property for wrapped SortedList
    public static Callback<Player, Observable[]> extractor = player -> new Observable[] {player.scoreProperty()};
}
