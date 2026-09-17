import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PlayerDatabase {

    public static String findPlayerById(int id) {
        String sql = "SELECT codename FROM players WHERE id = ?";

        try (Connection connection = Database.connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);

            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    return result.getString("codename");
                }
            }

        } catch (SQLException e) {
            System.out.println("Player lookup failed.");
            e.printStackTrace();
        }

        return null;
    }

    public static boolean addPlayer(int id, String codename) {
        String sql = "INSERT INTO players (id, codename) VALUES (?, ?)";

        try (Connection connection = Database.connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);
            statement.setString(2, codename);

            return statement.executeUpdate() == 1;

        } catch (SQLException e) {
            System.out.println("Player addition failed.");
            e.printStackTrace();
            return false;
        }
    }

    public static void main(String[] args) {
        String codename = findPlayerById(2);

        if (codename != null) {
            System.out.println("Player found: " + codename);
        } else {
            System.out.println("Player not found.");
        }
    }
}
