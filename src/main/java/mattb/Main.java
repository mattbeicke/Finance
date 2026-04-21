package mattb;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.stage.Stage;
import mattb.controllers.MainController;

import java.io.IOException;
import java.sql.*;
import java.text.NumberFormat;
import java.util.HashSet;
import java.util.Locale;

import static mattb.FinanceError.*;

public class Main extends Application {
    private static HashSet<Integer> hiddenTransactions;
    private static HashSet<Integer> hiddenAccounts;
    private static Connection conn;

    static void main() {
        launch();
    }

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
        } catch (SQLException ignored) {
            throw new FinanceException(DATABASE_CONNECTION_FAIL);
        }

        hiddenTransactions = new HashSet<>();
        hiddenAccounts = new HashSet<>();

        updateHidden();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/mattb/controllers/main.fxml"));
        Parent root = loader.load();
        stage.setTitle("Matt's Finance App");
        stage.setScene(new Scene(root));
        stage.show();
        MainController mainController = loader.getController();
        mainController.showDashboard();
    }

    /**
     * Recalculates the hidden transaction and account lists
     */
    public static void updateHidden() {
        hiddenTransactions.clear();
        hiddenAccounts.clear();

        String sql = "select t_id from hidden_transactions";
        try (PreparedStatement pstmt = conn.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                hiddenTransactions.add(rs.getInt("t_id"));
            }
        } catch (SQLException ignored) {
            throw new FinanceException(LOAD_HIDDEN_TRANSACTIONS_FAIL);
        }

        sql = "select acc_id from hidden_accounts";
        try (PreparedStatement pstmt = conn.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                hiddenAccounts.add(rs.getInt("acc_id"));
            }
        } catch (SQLException ignored) {
            throw new FinanceException(LOAD_HIDDEN_ACCOUNTS_FAIL);
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

    /**
     * Converts a double (that reflects a balance or amount) to a nice formatted string (dollar signs and appropriate decimals)
     *
     * @param input String to convert
     * @return Converted string
     */
    public static String formatDouble(double input) {
        return NumberFormat.getCurrencyInstance(Locale.US).format(input);
    }

    /**
     * Reduces redundant code controllers by setting the Balance and Amount columns to use the currency format above
     *
     * @param toConvert {@link TableColumn} to convert
     * @param <S>       Lets {@code toConvert} be any from any table as long as the column is of a double type
     */
    public static <S> void useCurrency(TableColumn<S, Double> toConvert) {
        toConvert.setCellFactory(_ -> new TableCell<>() {
            @Override
            protected void updateItem(Double balance, boolean empty) {
                super.updateItem(balance, empty);
                if (empty || balance == null) {
                    setText(null);
                } else {
                    setText(Main.formatDouble(balance));
                }
            }
        });
    }

}
