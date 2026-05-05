package mattb.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

/**
 * Handles UI interactions on the {@code Update Balances} modal
 *
 * @author Matthew Beicke
 */
public class UpdateBalancesController {
    private boolean update = false;

    /**
     * Indicates a {@code yes} answer to the {@code Update Balances} modal
     *
     * @param event Button press {@link ActionEvent}
     */
    @FXML
    private void yes(ActionEvent event) {
        update = true;
        cancel(event);
    }

    /**
     * Indicates a {@code no} answer to the {@code Update Balances} modal
     *
     * @param event Button press {@link ActionEvent}
     */
    @FXML
    private void no(ActionEvent event) {
        update = false;
        cancel(event);
    }

    /**
     * Exits the {@code Update Balances} modal
     *
     * @param event The {@link ActionEvent} from pressing the {@code Cancel} {@link Button}
     */
    @FXML
    private void cancel(ActionEvent event) {
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.close();
    }

    /**
     * Gets the response status from this modal
     *
     * @return {@code true} if the user pressed 'yes', {@code false} if the user pressed 'no' or exited the modal
     */
    public boolean getUpdate() {
        return update;
    }
}
