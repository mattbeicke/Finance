package mattb;

import javafx.application.Platform;
import javafx.scene.control.Alert;

public class FinanceException extends RuntimeException {
    public FinanceException(FinanceError error) {
        super(error.getMessage());
    }

    public void displayAndLog() {
        String errorMessage = this.getMessage();
        if (Platform.isFxApplicationThread()) {
            showDialog(errorMessage);
        } else {
            Platform.runLater(() -> showDialog(errorMessage));
        }
    }

    private void showDialog(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Application Error");
        alert.setHeaderText("An Error Occurred");
        alert.setContentText(msg);
        alert.showAndWait();
    }
}