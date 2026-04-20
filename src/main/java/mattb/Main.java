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
    private static HashSet<Integer> hiddenTransactions;
    private static HashSet<Integer> hiddenAccounts;
    private static Connection conn;

    /**
     * Initializes Database {@link Connection}, Hidden transaction and account {@link HashSet}, and the GUI
     *
     * @param stage the primary stage for this application, onto which
     *              the application scene can be set.
     *              Applications may create other stages, if needed, but they will not be
     *              primary stages.
     * @throws IOException If something goes wrong?
     */
    @Override
    public void start(Stage stage) throws IOException {
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:finance.db");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        hiddenTransactions = new HashSet<>();
        hiddenAccounts = new HashSet<>();

        updateHidden();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/mattb/controllers/main.fxml"));
        Parent root = loader.load();
        stage.setScene(new Scene(root));
        stage.show();
    }

    /**
     * Recalculates the hidden transaction and account lists
     */
    public static void updateHidden() {
        hiddenTransactions.clear();
        hiddenAccounts.clear();

        try {
            String sql = "select t_id from hidden_transactions";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                hiddenTransactions.add(rs.getInt("t_id"));
            }

            sql = "select acc_id from hidden_accounts";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                hiddenAccounts.add(rs.getInt("acc_id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the hidden transaction list
     *
     * @return hidden transaction list
     */
    public static HashSet<Integer> getHiddenTransactions() {
        return hiddenTransactions;
    }

    /**
     * Gets the hidden account list
     *
     * @return hidden account list
     */
    public static HashSet<Integer> getHiddenAccounts() {
        return hiddenAccounts;
    }

    /**
     * Gets the database {@link Connection}
     *
     * @return Database {@link Connection}
     */
    public static Connection getConn() {
        return conn;
    }
}
