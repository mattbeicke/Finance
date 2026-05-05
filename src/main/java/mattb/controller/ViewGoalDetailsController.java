package mattb.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import mattb.Main;
import mattb.model.Goal;
import mattb.service.GoalService;

/**
 * Handles UI interactions on the {@code View Goal Details} modal
 *
 * @author Matthew Beicke
 */
public class ViewGoalDetailsController {
    @FXML
    private TextField nameField;
    @FXML
    private Label account;
    @FXML
    private TextField targetField;

    private GoalService goalService;

    private int id;

    private boolean saveClicked = false;
    private boolean deleteClicked = false;

    /**
     * Initializes {@link FXML} items for the {@code View Goal Details} modal and the {@link GoalService}
     */
    @FXML
    private void initialize() {
        goalService = Main.getGoalService();

        targetField.textProperty().addListener((_, oldVal, newVal) -> {
            if (!newVal.matches("\\d*(\\.\\d*)?")) {
                targetField.setText(oldVal);
            }
        });
    }

    /**
     * Deletes a {@link Goal} from the database
     *
     * @param event The {@link ActionEvent} from pressing the {@code Delete} {@link Button}
     */
    @FXML
    private void delete(ActionEvent event) {
        goalService.deleteGoal(id);

        deleteClicked = true;
        cancel(event);
    }

    /**
     * Updates a {@link Goal} in the database
     *
     * @param event The {@link ActionEvent} from pressing the {@code Update} {@link Button}
     */
    @FXML
    private void update(ActionEvent event) {
        if (goalService.updateGoal(nameField.getText(), targetField.getText(), id)) {
            saveClicked = true;
            cancel(event);
        } else {
            Main.showNotification(false, "Please fill all required fields");
        }
    }

    /**
     * Exits the {@code View Goal Details} modal
     *
     * @param event The {@link ActionEvent} from pressing the {@code Cancel} {@link Button}
     */
    @FXML
    private void cancel(ActionEvent event) {
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.close();
    }

    /**
     * Sets fields of the {@code View Goal Details} modal
     *
     * @param g  The {@link Goal} who is being viewed
     * @param id The database id of {@code g}
     */
    public void setFields(Goal g, int id) {
        if (g == null || id <= 0) return;

        this.id = id;

        nameField.setText(g.name());
        account.setText(g.account());
        targetField.setText(String.valueOf(g.target()));
    }

    /**
     * Gets the status on if the save button was pressed or not
     *
     * @return {@code true} if the save button was pressed, {@code false} if not
     */
    public boolean isSaveClicked() {
        return saveClicked;
    }

    /**
     * Gets the status on if the delete button was pressed or not
     *
     * @return {@code true} if the delete button was pressed, {@code false} if not
     */
    public boolean isDeleteClicked() {
        return deleteClicked;
    }
}
