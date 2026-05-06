package mattb.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import mattb.UIUtilities;
import mattb.model.Goal;
import mattb.model.GoalResponse;
import mattb.service.AccountService;
import mattb.service.GoalService;
import org.springframework.stereotype.Component;

/**
 * Handles UI interactions on the {@code Add New Goal} modal
 *
 * @author Matthew Beicke
 */
@Component
public class AddGoalController {
    @FXML
    private TextField nameField;
    @FXML
    private ComboBox<String> accountCombo;
    @FXML
    private TextField targetField;

    private final GoalService goalService;
    private final AccountService accountService;
    private final UIUtilities uiUtilities;

    private boolean saveClicked = false;

    /**
     * Used by Spring Boot to do dependency injection for the below items
     *
     * @param goalService    The connection to the {@link GoalService Goal Service}
     * @param accountService The connection to the {@link AccountService Account Service}
     * @param uiUtilities    The connection to the {@link UIUtilities Utlities Class}
     */
    public AddGoalController(GoalService goalService, AccountService accountService, UIUtilities uiUtilities) {
        this.goalService = goalService;
        this.accountService = accountService;
        this.uiUtilities = uiUtilities;
    }

    /**
     * Initializes {@link FXML} items for the {@code Add New Goal} modal
     */
    @FXML
    private void initialize() {
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
        GoalResponse response = goalService.saveGoal(accountCombo.getValue(), nameField.getText(), targetField.getText());

        if (!response.success()) {
            uiUtilities.showNotification(false, response.message());
            return;
        }

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
