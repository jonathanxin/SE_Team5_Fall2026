import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;

public class Model {
    private Networking networking;
    private PlayerEntryController playerEntryController;
    private PlayerActionController playerActionController;
    private PlayerDatabase playerDatabase;
    private int playerId; // store the player ID for validation
    private Main application;
    private int currentTeam; // 1 for green team, 2 for red team
    private ObservableList<Player> redTeamList = FXCollections.observableArrayList(Player.extractor);
    private ObservableList<Player> greenTeamList = FXCollections.observableArrayList(Player.extractor);

    public Model(PlayerDatabase playerDatabase, Networking networking) {
        this.playerDatabase = playerDatabase;
        this.networking = networking;
    }

    public void setApp(Main application) {
        this.application = application;
    }

    public void setController(PlayerEntryController controller) {
        this.playerEntryController = controller;
    }

    public void setNetworkAddress(String networkAddress) {
        // forward the network address to the networking class
        networking.setNetworkAddress(networkAddress);
    }

    public String findPlayerById(int playerId) {
        this.playerId = playerId; // store the player ID for validation
        // forward the player ID to the player database class
        String codename = playerDatabase.findPlayerById(playerId);
        if (codename == null) {
            System.out.println("Player ID " + playerId + " not found in database.");
            return null;
        } 
        else {
            System.out.println("Player ID " + playerId + " found in database.");
            addPlayer(codename); // add the player to the model's list
            return codename;
        }
    }

    public boolean addPlayerToDatabase(String codename) {
        if (playerDatabase.addPlayer(playerId, codename))
        {
            addPlayer(codename);
            return true;
        } 
        else
        {
            return false;
        }
    }
    
    public void transmitEquipmentId(int equipmentId) {
        
        networking.transmit(equipmentId);
    }

    public void changeToPlayerActionScreen() {
        try {
            playerActionController = application.switchScene();
            playerActionController.setModel(this);
            playerActionController.initializeTeams();
        } catch (Exception e) {
            throw new IllegalStateException(
                    "Could not switch to the Player Action screen.", e);
        }
    }

    public void calculateTeam(int playerIndex) {
        // calculate the team based on the player index
        if (playerIndex % 2 == 0) {
            currentTeam = 1; // green team
        } else {
            currentTeam = 2; // red team
        }
    }

    public void addPlayer(String codename) {
        Player player = new Player(codename);
        if (currentTeam == 1) {
            greenTeamList.add(player);
        } else if (currentTeam == 2) {
            redTeamList.add(player);
        }
    }

    public ObservableList<Player> getSortedGreenTeamList() {
        return greenTeamList;
    }

    public ObservableList<Player> getSortedRedTeamList() {
        return redTeamList;
    }
}
