import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

void main() {
    String url = "jdbc:sqlite:finance.db";

    try (Connection conn = DriverManager.getConnection(url)) {
        if (conn != null) {
            Statement stmt = conn.createStatement();

            //stmt.execute("CREATE TABLE IF NOT EXISTS expenses (id INTEGER PRIMARY KEY, item TEXT, amount REAL)");
            //stmt.execute("INSERT INTO expenses (item, amount) VALUES ('Coffee', 5.50)");
        }
    } catch (SQLException e) {
        System.err.println("DB Error: " + e.getMessage());
    }
}
