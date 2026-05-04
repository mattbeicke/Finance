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

    /**
     * Initializes {@link FXML} items for the {@code Add New Goal} modal
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
        if (accId == -1 || nameField.getText().isBlank() || targetField.getText().isBlank()) return;

        goalService.saveGoal(accId, nameField.getText(), Double.parseDouble(targetField.getText()));

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
}
