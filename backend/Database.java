import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {

    public static Connection connect() throws SQLException {
        String url = "jdbc:postgresql://localhost/photon";
        String user = "student";
        String password = "student";

        return DriverManager.getConnection(url, user, password);
    }
}
