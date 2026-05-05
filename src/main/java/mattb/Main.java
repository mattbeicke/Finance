package mattb;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import mattb.controller.MainController;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;

import static mattb.FinanceError.OPEN_MAIN_FAILED;
import static mattb.Utilities.darkMode;

/**
 * Entrypoint into the program. Does all the setup for custom exception handling and creating the main stage for the GUI
 *
 * @author Matthew Beicke
 */
public class Main extends Application {
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
            ServiceFactory.init();

            URL resource = getClass().getResource("/mattb/controller/main.fxml");
            if (resource == null) {
                new FinanceException(OPEN_MAIN_FAILED).displayAndLog();
                return;
            }
            FXMLLoader loader = new FXMLLoader(resource);
            Parent root = loader.load();

            stage.setTitle("Matt's Finance App");
            Scene scene = new Scene(root);

            darkMode(scene);

            stage.setScene(scene);
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
}
