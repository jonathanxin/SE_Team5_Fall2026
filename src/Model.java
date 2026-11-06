// TODO: refactor data to be a single collection where players have team propertys (only sort by team once in PAC)
import javafx.collections.ObservableList;

public class Model {
    private Networking networking;
    private PlayerEntryController playerEntryController;
    private PlayerActionController playerActionController;
    private PlayerDatabase playerDatabase;
    private int playerId; // store the player ID for validation
    private String codename; // store the codename for validation
    private Main application;
    private int currentTeam; // 1 for green team, 2 for red team
    private Team redTeam = new Team();
    private Team greenTeam = new Team();

    public Model(PlayerDatabase playerDatabase) {
        this.playerDatabase = playerDatabase;
    }

    public void setNetworking(Networking networking) {
        this.networking = networking;
    }

    public void setApp(Main application) {
        this.application = application;
    }

    public void setplayerEntryController(PlayerEntryController playerEntryController) {
        this.playerEntryController = playerEntryController;
    }

    public void setNetworkAddress(String networkAddress) {
        // forward the network address to the networking class
        networking.setNetworkAddress(networkAddress);
    }

    public void changePlayerScore(int equipmentId, int scoreChange) {
        Player player = greenTeam.getplayerByEquipmentId(equipmentId);
        if (player == null) {
            player = redTeam.getplayerByEquipmentId(equipmentId);
        }
        int newScore = player.getScore() + scoreChange;
        player.setScore(newScore);
    }

    public Boolean checkFriendlyFire(int taggingID, int taggedID) {
        // search the teams for the players with the given equipment IDs
        Player taggingPlayer = greenTeam.getplayerByEquipmentId(taggingID);
        Player taggedPlayer = greenTeam.getplayerByEquipmentId(taggedID);
        if (taggingPlayer == null ^ taggedPlayer == null) {
            return false;
        }
        return true;
    }

    public String findPlayerById(int playerId) {
        this.playerId = playerId; // store the player ID for later use
        // forward the player ID to the player database class
        String codename = playerDatabase.findPlayerById(playerId);
        if (codename == null) {
            System.out.println("Player ID " + playerId + " not found in database.");
            return null;
        } 
        else {
            System.out.println("Player ID " + playerId + " found in database.");
            this.codename = codename; // store the codename for later use
            return codename;
        }
    }

    public boolean addPlayerToDatabase(String codename) {
        if (playerDatabase.addPlayer(playerId, codename))
        {
            this.codename = codename; // store the codename for later use
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

    public int calculateTeam(int playerIndex) {
        // calculate the team based on the player index
        if (playerIndex % 2 == 0) {
            return 1; // green team
        } else {
            return 2; // red team
        }
    }

    public void addSessionPlayer(int playerIndex, int equipmentId) {
        Player player = new Player(playerId, codename, equipmentId);
        currentTeam = calculateTeam(playerIndex);
        if (currentTeam == 1) {
            greenTeam.addPlayer(player);
        } else if (currentTeam == 2) {
            redTeam.addPlayer(player);
        }
    }

    public void clearSessionPlayers() {
        greenTeam.getTeamList().clear();
        redTeam.getTeamList().clear();
    }

    public ObservableList<Player> getGreenTeamList() {
        return greenTeam.getTeamList();
    }

    public ObservableList<Player> getRedTeamList() {
        return redTeam.getTeamList();
    }
    
    public void startGame() {
        networking.transmit(202);
        changeToPlayerActionScreen();
    }
}
