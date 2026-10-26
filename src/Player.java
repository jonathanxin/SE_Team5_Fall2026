import javafx.beans.Observable;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.util.Callback;

public class Player {
    private StringProperty codename = new SimpleStringProperty(this, "codename", "");
    private int ID = -1;
	private int equipmentId = -1;
    private IntegerProperty score = new SimpleIntegerProperty(this, "score", 0);

    public Player(int ID, String codename) {
        this.ID = ID;
		this.equipmentId = equipmentId;
        this.setCodename(codename);
    }

    public StringProperty codenameProperty() {
        return codename;
    }

    public String getCodename() {
        return codename.get();
    }

    public void setCodename(String codename) {
        this.codename.set(codename);
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }
	
	public int getEquipmentId() {
    	return equipmentId;
	}

	public void setEquipmentId(int equipmentId) {
    	this.equipmentId = equipmentId;
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

    // extractor for the ObservableList to listen to changes in the score property so that the wrapping SortedList updates on score changes
    public static Callback<Player, Observable[]> extractor = player -> new Observable[] {player.scoreProperty()};
	
	@Override 
    public String toString() { // used as default cell factory for the table view (if no custom cell factory is set)
	    return getCodename() + " (Score: " + score.get() + ")";
    }
}
