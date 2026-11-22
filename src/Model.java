import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import javafx.beans.property.IntegerProperty;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

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
    private ObservableList<String> gameActions = FXCollections.observableArrayList();
    private Timeline gameTimer;
    private int gameSecondsRemaining;
    private boolean gameRunning = false;

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
        // determine
        Player player = greenTeam.getplayerByEquipmentId(equipmentId);
        if (player == null) {
            player = redTeam.getplayerByEquipmentId(equipmentId);
        }
        int newScore = player.getScore() + scoreChange;
        Player playerToUpdate = player;
        Platform.runLater(() -> playerToUpdate.setScore(newScore));
    }

    public String findPlayerById(int playerId) {
        this.playerId = playerId; // store the player ID for later use
        // forward the player ID to the player database class
        String codename = playerDatabase.findPlayerById(playerId);
        if (codename == null) {
            System.out.println("Player ID " + playerId + " not found in database.");
            return null;
        } else {
            System.out.println("Player ID " + playerId + " found in database.");
            this.codename = codename; // store the codename for later use
            return codename;
        }
    }

    public boolean addPlayerToDatabase(String codename) {
        if (playerDatabase.addPlayer(playerId, codename)) {
            this.codename = codename; // store the codename for later use
            return true;
        } else {
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

    public int calculateTeam(int col) {
        // calculate the team based on the player index
        if (col == 1) {
            return 1; // green team
        } else {
            return 2; // red team
        }
    }

    public void addSessionPlayer(int col, int equipmentId) {
        Player player = new Player(playerId, codename, equipmentId);
        currentTeam = calculateTeam(col);
        if (currentTeam == 1) {
            greenTeam.addPlayer(player);
        } else if (currentTeam == 2) {
            redTeam.addPlayer(player);
        }
    }

    public void clearSessionPlayers() {
        greenTeam.clearTeam();
        redTeam.clearTeam();
        //greenTeam.getTeamList().clear();
        //redTeam.getTeamList().clear();
    }

    public Team getGreenTeam() {
        return greenTeam;
    }

    public Team getRedTeam() {
        return redTeam;
    }

    public void checkBaseTagging(int taggingID, int taggedID) {
        // check if the tagging player is on the green team
        Player greenPlayer = greenTeam.getplayerByEquipmentId(taggingID);
        if (greenPlayer != null) {
            // tagging player is on the green team
            // check if taggedID is the red base and base has not been hit yet
            if (taggedID == 53 && !greenTeam.getHitBase()) {
                System.out.println("Green team tagged the red base!");
                changePlayerScore(taggingID, 100);
                Platform.runLater(() -> {
                    greenPlayer.setHitBase(true);
                });
                greenTeam.setHitBase(true);  
                return;
            }
        }
        else {
            // tagging player is on the red team
            Player redPlayer = redTeam.getplayerByEquipmentId(taggingID);
            // check if taggedID is the green base and base has not been hit yet
            if (taggedID == 43 && !redTeam.getHitBase()) {
                System.out.println("Red team tagged the green base!");
                changePlayerScore(taggingID, 100); 
                Platform.runLater(() -> {
                    redPlayer.setHitBase(true);
                });
                redTeam.setHitBase(true);
                return;
            }
        }
    }

    public boolean isFriendlyFire(int attackerEquipmentId, int targetEquipmentId) {
        // A player hitting themselves does not count as friendly fire.
        if (attackerEquipmentId == targetEquipmentId) {
            return false;
        }

        // Both equipment IDs must belong to players on the same team.
        return (hasEquipmentId(greenTeam, attackerEquipmentId)
                && hasEquipmentId(greenTeam, targetEquipmentId))
                || (hasEquipmentId(redTeam, attackerEquipmentId)
                    && hasEquipmentId(redTeam, targetEquipmentId));
    }

    private boolean hasEquipmentId(Team team, int equipmentId) {
        // Check the current team roster by equipment ID, not database player ID.
        if (team.getplayerByEquipmentId(equipmentId) != null) {
            return true;
        }
        return false;
    }

    public int[] getFriendlyFireReplyIds(int attackerEquipmentId, int targetEquipmentId) {
        if (!isFriendlyFire(attackerEquipmentId, targetEquipmentId)) {
            return new int[0];
        }

        // For now, reply with the hit player's ID first, then the tagger's ID.
        return new int[] {targetEquipmentId, attackerEquipmentId};
    }

    public boolean sendFriendlyFireReplies(int attackerEquipmentId, int targetEquipmentId) {
        int[] replyIds = getFriendlyFireReplyIds(attackerEquipmentId, targetEquipmentId);
        if (replyIds.length == 0) {
            return false;
        }

        // Send a separate reply for each equipment ID.
        for (int equipmentId : replyIds) {
            transmitEquipmentId(equipmentId);
        }

        return true;
    }
    
    public ObservableList<String> getGameActions() {
        return gameActions;
    }
    
    public void addGameAction(String action) { //method for adding events alongside the time they happened
        String time = LocalTime.now().format(
        DateTimeFormatter.ofPattern("HH:mm:ss"));

        Platform.runLater(() -> gameActions.add(time + " - " + action));
    }
    public Player findPlayerByEquipmentId(int equipmentId) {
        // for green team
        for (Player player : greenTeam.getTeamList()) {
            if (player.getEquipmentId() == equipmentId) {
                return player;
            }
        }
    
        // for red team
        for (Player player : redTeam.getTeamList()) {
            if (player.getEquipmentId() == equipmentId) {
                return player;
            }
        }
    
        return null;
    }
    
    public void processReceivedData(String message) {
        if(!gameRunning){
            return;
        }
        // Separate attacker target IDs
        String[] ids = message.split(":");
    
        // error checking for more than 2 ids
        if (ids.length != 2) {
            System.out.println("Invalid game message: " + message);
            return;
        }
    
        int attackerId;
        int targetId;
        
        // Convert IDs from strings to ints
        try {
            attackerId = Integer.parseInt(ids[0].trim());
            targetId = Integer.parseInt(ids[1].trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid equipment ID: " + message);
            return;
        }
    
        //find attacker
        Player attacker = findPlayerByEquipmentId(attackerId);
    
        if (attacker == null) {
            System.out.println("Attacker not found: " + attackerId);
            return;
        }
    
        // red base
        if (targetId == 53) {
            if (hasEquipmentId(greenTeam, attackerId) && !redTeam.getHitBase()) {
                changePlayerScore(attackerId, 100); // attacker.setScore(attacker.getScore() + 100);
                redTeam.setHitBase(true);
                Platform.runLater(() -> {
                    attacker.setHitBase(true);
                });
                addGameAction(attacker.getCodename() + " scored the red base (+100 points)");
            } else {
                addGameAction(attacker.getCodename() + " hit the red base");
            }
    
            transmitEquipmentId(targetId);
            return;
        }
        //green base
        if (targetId == 43) {
            if (hasEquipmentId(redTeam, attackerId) && !greenTeam.getHitBase()) {
            changePlayerScore(attackerId, 100); // attacker.setScore(attacker.getScore() + 100);
            greenTeam.setHitBase(true);
            Platform.runLater(() -> {
                attacker.setHitBase(true);
            });
            addGameAction(attacker.getCodename()
                    + " scored the green base (+100 points)");
            } else {
                addGameAction(attacker.getCodename()
                        + " hit the green base");
            }
    
            transmitEquipmentId(targetId);
            return;
        }
    
        //find the target player
        Player target = findPlayerByEquipmentId(targetId);
    
        if (target == null) {
            System.out.println("Target not found: " + targetId);
            return;
        }
    
        // check for friendly fire
        // Ignore self-hits so players cannot gain points by hitting themselves
        if (attackerId == targetId) {
        System.out.println("Self-hit ignored: " + attackerId);
        transmitEquipmentId(targetId);
        return;
        }
        
       if (isFriendlyFire(attackerId, targetId)) {
            changePlayerScore(attackerId, -10); //removes 10 points from the person who hit their teammate
            changePlayerScore(targetId, -10); //removes 10 points from teammate

            addGameAction("FRIENDLY FIRE: " + attacker.getCodename() + " hit " + target.getCodename() + " (-10 points each)");
            sendFriendlyFireReplies(attackerId, targetId);

        } 
        else { //normal hit on enemy team
            changePlayerScore(attackerId, 10); //add 10
    
            addGameAction(attacker.getCodename() + " hit " + target.getCodename() + " (+10 points)");
    
            transmitEquipmentId(targetId);
        }
    }

    public void startGame() {
        // Prevent starting another timer during an active game.
        if (gameRunning) {
            return;
        }
    
        changeToPlayerActionScreen();
    
        gameSecondsRemaining = 6 * 60;
        playerActionController.updateGameTimer(gameSecondsRemaining);
    
        gameRunning = true;
        networking.transmit(202);
    
        gameTimer = new Timeline(
            new KeyFrame(Duration.seconds(1), event -> {
                gameSecondsRemaining--;
                playerActionController.updateGameTimer(gameSecondsRemaining);
            })
        );
    
        gameTimer.setCycleCount(6 * 60);
        gameTimer.setOnFinished(event -> endGame());
        gameTimer.play();
    }
    
    public void endGame() {
        // Prevent sending the end signal multiple times accidentally.
        if (!gameRunning) {
            return;
        }
    
        gameRunning = false;
    
        if (gameTimer != null) {
            gameTimer.stop();
        }
    
        gameSecondsRemaining = 0;
        playerActionController.updateGameTimer(0);
    
        for (int i = 0; i < 3; i++) {
            networking.transmit(221);
        }
    
        addGameAction("Game over!");
    }
}
