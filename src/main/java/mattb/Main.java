package mattb;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.stage.Stage;
import mattb.controller.MainController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.stream.Collectors;

import static mattb.FinanceError.*;

/**
 * Entrypoint into the program. Does all the setup (database connection, exception handling, etc.).
 *
 * @author Matthew Beicke
 */
public class Main extends Application {
    private static Connection conn;

    static void main() {
        launch();
    }

    /**
     * Sets up exception handling to be done via {@link FinanceException}.
     * Initializes the Database {@link Connection}.
     * Starts the GUI.
     *
     * @param stage The primary stage for this application, onto which
     *              the application scene can be set.
     *              Applications may create other stages, if needed, but they will not be
     *              primary stages.
     */
    @Override
    public void start(Stage stage) {
        Thread.setDefaultUncaughtExceptionHandler((_, throwable) -> {
            Throwable cause = throwable;
            while (cause != null) {
                if (cause instanceof FinanceException) {
                    final String errorMessage = cause.getMessage();
                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Application Error");
                        alert.setHeaderText(errorMessage);
                        alert.setContentText("Saved to error.log file");
                        FinanceException.logToFile(errorMessage);
                        alert.showAndWait();
                    });
                    return;
                }
                cause = cause.getCause();
            }
            throwable.printStackTrace();
        });

        try {
            try {
                conn = DriverManager.getConnection("jdbc:sqlite:finance.db");
            } catch (SQLException ignored) {
                new FinanceException(DATABASE_CONNECTION_FAIL).displayAndLog();
            }

            if (ensureDB()) {
                return;
            }

            URL resource = getClass().getResource("/mattb/controller/main.fxml");
            if (resource == null) {
                new FinanceException(OPEN_MAIN_FAILED).displayAndLog();
                return;
            }
            FXMLLoader loader = new FXMLLoader(resource);
            Parent root = loader.load();

            stage.setTitle("Matt's Finance App");
            stage.setScene(new Scene(root));
            stage.show();
            MainController mainController = loader.getController();
            mainController.showDashboard();
        } catch (FinanceException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Critical Startup Error");
            alert.setHeaderText(e.getMessage());
            alert.setContentText("Saved to error.log file");
            FinanceException.logToFile(e.getMessage());
            alert.showAndWait();

            Platform.exit();
        } catch (IOException ignored) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Critical Startup Error");
            alert.setHeaderText("A necessary JavaFX FXML file could not be found");
            alert.setContentText("Saved to error.log file");
            FinanceException.logToFile("A necessary JavaFX FXML file could not be found");
            alert.showAndWait();

            Platform.exit();
        }
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
     * Converts a {@link Double} (that reflects a balance or amount) to a formatted {@link String}.
     * This includes a dollar sign and two decimal places and a point (of zeros if it is the case).
     *
     * @param input String to convert
     * @return Converted string
     */
    public static String formatDouble(double input) {
        return NumberFormat.getCurrencyInstance(Locale.US).format(input);
    }

    /**
     * Sets the {@link TableColumn Columns} {@code Balance} and {@code Amount} columns to use the currency format from {@link #formatDouble(double)}
     *
     * @param toConvert The {@link TableColumn} to convert
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

    /**
     * Sets up missing database tables (if there are any).
     * Initializes the tables with the required starting data (the {@code External} account and such)
     *
     * @return {@code true} if something went wrong
     */
    private boolean ensureDB() {
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = ON;");

            var is = Main.class.getResourceAsStream("/mattb/schema.sql");
            if (is == null) {
                new FinanceException(SCHEMA_NOT_FOUND).displayAndLog();
                return true;
            }

            String sql = new BufferedReader(new InputStreamReader(is)).lines().collect(Collectors.joining("\n"));

            for (String part : sql.split(";")) {
                if (!part.trim().isEmpty()) {
                    stmt.execute(part);
                }
            }

            return false;
        } catch (SQLException ignored) {
            new FinanceException(DATABASE_CREATION_FAIL).displayAndLog();
        }
        return true;
    }
}
