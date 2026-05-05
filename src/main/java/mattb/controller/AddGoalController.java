package mattb.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import mattb.Main;
import mattb.model.Goal;
import mattb.service.AccountService;
import mattb.service.GoalService;

/**
 * Handles UI interactions on the {@code Add New Goal} modal
 *
 * @author Matthew Beicke
 */
public class AddGoalController {
    @FXML
    private TextField nameField;
    @FXML
    private ComboBox<String> accountCombo;
    @FXML
    private TextField targetField;

    private GoalService goalService;
    private AccountService accountService;

    private boolean saveClicked = false;

    /**
     * Initializes {@link FXML} items for the {@code Add New Goal} modal and the {@link GoalService} and {@link AccountService}
     */
    @FXML
    private void initialize() {
        goalService = Main.getGoalService();
        accountService = Main.getAccountService();

        accountCombo.setItems(accountService.getAccountNames());

        targetField.textProperty().addListener((_, oldVal, newVal) -> {
            if (!newVal.matches("\\d*(\\.\\d*)?")) {
                targetField.setText(oldVal);
            }
        });
    }

    /**
     * Saves new {@link Goal} to database
     *
     * @param event The {@link ActionEvent} from pressing the {@code Save} {@link Button}
     */
    @FXML
    private void save(ActionEvent event) {
        int accId = accountService.getAccId(accountCombo.getValue());
        if (accId == -1 || nameField.getText().isBlank() || targetField.getText().isBlank()) {
            Main.showNotification(false, "Please fill all required fields");
            return;
        }

        goalService.saveGoal(accId, nameField.getText(), Double.parseDouble(targetField.getText()));

        saveClicked = true;
        cancel(event);
    }

    /**
     * Exits the {@code Add New Goal} modal
     *
     * @param event The {@link ActionEvent} from pressing the {@code Cancel} {@link Button}
     */
    @FXML
    private void cancel(ActionEvent event) {
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.close();
    }

    /**
     * Gets the status on if the save button was pressed or cancel button was pressed
     *
     * @return {@code true} if the save button was pressed, {@code false} if not
     */
    public boolean isSaveClicked() {
        return saveClicked;
    }
}
