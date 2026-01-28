import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    public Connection getConnection() {
        try {
            return DriverManager.getConnection("jdbc:postgresql://localhost:5432/mini_dish_db", "postgres", "fitia");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
