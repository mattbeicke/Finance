package mattb;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.util.HashSet;

public class Main extends Application {
    private static HashSet<Integer> ignoredTransactions;
    private static HashSet<Integer> ignoredAccounts;
    private static Connection conn;

    @Override
    public void start(Stage stage) throws IOException {
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:finance.db");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        ignoredTransactions = new HashSet<>();
        ignoredAccounts = new HashSet<>();

        try {
            String sql = "select t_id from ignored_transactions";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                ignoredTransactions.add(rs.getInt("t_id"));
            }

            sql = "select acc_id from ignored_accounts";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                ignoredAccounts.add(rs.getInt("acc_id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/mattb/controllers/main.fxml"));
        Parent root = loader.load();
        stage.setScene(new Scene(root));
        stage.show();
    }

    public static HashSet<Integer> getIgnoredTransactions() {
        return ignoredTransactions;
    }

    public static HashSet<Integer> getIgnoredAccounts() {
        return ignoredAccounts;
    }

    public static Connection getConn() {
        return conn;
    }
}
