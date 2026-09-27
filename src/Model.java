package src;

public class Model {
    private Networking networking;
    private Controller controller;
    private PlayerDatabase playerDatabase;
    private int playerId; // store the player ID for validation

    public Model(PlayerDatabase playerDatabase, Networking networking) {
        this.playerDatabase = playerDatabase;
        this.networking = networking;
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public void setNetworkAddress(String networkAddress) {
        // forward the network address to the networking class
        networking.setNetworkAddress(networkAddress);
    }

    public String findPlayerById(int playerId) {
        this.playerId = playerId; // store the player ID for validation
        // forward the player ID to the player database class
        if (playerDatabase.findPlayerById(playerId) == null) {
            System.out.println("Player ID " + playerId + " not found in database.");
            return null;
        } 
        else {
            System.out.println("Player ID " + playerId + " found in database.");
            return playerDatabase.findPlayerById(playerId);
        }
    }

    public void addPlayerToDatabase(String codename)
    // forward the codename to the player database class
    {
        playerDatabase.addPlayer(playerId, codename); // add player to database with ID 0 (auto-incremented)
    }
}