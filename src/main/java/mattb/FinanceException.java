package mattb;

import javafx.application.Platform;
import javafx.scene.control.Alert;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Custom exception and handling
 *
 * @author Matthew Beicke
 */
public class FinanceException extends RuntimeException {
    /**
     * Sends the error to {@link RuntimeException}
     *
     * @param error Custom error code/message
     */
    public FinanceException(FinanceError error) {
        super(error.getMessage());
    }

    /**
     * Determines whether to display the error alert now or when the application thread can, depending on whom the calling thread is
     */
    public void displayAndLog() {
        String errorMessage = this.getMessage();
        logToFile(errorMessage);
        if (Platform.isFxApplicationThread()) {
            showDialog(errorMessage);
        } else {
            Platform.runLater(() -> showDialog(errorMessage));
        }
    }

    /**
     * Prints the specified error message to the {@code error.log} file
     *
     * @param msg Error message to print
     */
    public static void logToFile(String msg) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String timestamp = dtf.format(LocalDateTime.now());

        try (FileWriter fw = new FileWriter("error.log", true); PrintWriter pw = new PrintWriter(fw)) {
            pw.println("[" + timestamp + "] ERROR: " + msg);
        } catch (IOException e) {
            System.err.println("Could not write to log file: " + e.getMessage());
        }
    }

    /**
     * Displays the {@link Alert} message box with the specified message
     *
     * @param msg Error message to display
     */
    private void showDialog(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Application Error");
        alert.setHeaderText(msg);
        alert.setContentText("Saved to error.log file");
        alert.showAndWait();
    }
}