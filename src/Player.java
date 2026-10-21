import javafx.beans.Observable;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.util.Callback;

public class Player {
    private String codename = "";
    private int ID = -1;
    private IntegerProperty score = new SimpleIntegerProperty(this, "score", 0);

    public Player(int ID, String codename) {
        this.ID = ID;
        this.codename = codename;
    }

    public String getCodename() {
        return codename;
    }

    public int getID() {
        return ID;
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
	
	@Override 
    public String toString() {
        return codename + " (Score: " + score.get() + ")";
        // TODO: temp, properly format the player display in the list view
    }
}
