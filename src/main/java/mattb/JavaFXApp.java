package mattb;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import mattb.controller.MainController;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.util.Arrays;

import static mattb.FinanceError.OPEN_MAIN_FAILED;

/**
 * Does all the setup for custom exception handling and creating the main stage for the GUI.
 * Initializes Spring Boot.
 *
 * @author Matthew Beicke
 */
public class JavaFXApp extends Application {
    private ConfigurableApplicationContext springContext;

    /**
     * Initializes Spring Boot
     */
    @Override
    public void init() {
        this.springContext = new SpringApplicationBuilder()
                .sources(FinanceApp.class)
                .run(getParameters().getRaw().toArray(new String[0]));
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
            if (throwable != null) {
                System.err.println("Something went wrong" + Arrays.toString(throwable.getStackTrace()));
            }
        });

        try {
            URL resource = getClass().getResource("/mattb/controller/main.fxml");
            if (resource == null) {
                new FinanceException(OPEN_MAIN_FAILED).displayAndLog();
                return;
            }
            FXMLLoader loader = new FXMLLoader(resource);
            loader.setControllerFactory(springContext::getBean);
            Parent root = loader.load();

            stage.setTitle("Matt's Finance App");
            Scene scene = new Scene(root);

            UIUtilities uiUtilities = springContext.getBean(UIUtilities.class);
            uiUtilities.darkMode(scene);

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

            stop();
        } catch (IOException ignored) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Critical Startup Error");
            alert.setHeaderText("A necessary JavaFX FXML file could not be found");
            alert.setContentText("Saved to error.log file");
            FinanceException.logToFile("A necessary JavaFX FXML file could not be found");
            alert.showAndWait();

            stop();
        }
    }

    /**
     * Gracefully exits Spring Boot and the GUI
     */
    @Override
    public void stop() {
        springContext.close();
        Platform.exit();
    }
}
