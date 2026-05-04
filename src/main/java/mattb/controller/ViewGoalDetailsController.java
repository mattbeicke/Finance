package mattb.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import mattb.Main;
import mattb.dao.GoalDAO;
import mattb.dao.GoalDAOImpl;
import mattb.model.Goal;

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

    private GoalDAO goalDAO;

    private int id;

    /**
     * Initializes {@link FXML} items for the {@code View Goal Details} modal and the {@link GoalDAO DAO}
     */
    @FXML
    private void initialize() {
        goalDAO = new GoalDAOImpl(Main.getConn());

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
        goalDAO.deleteGoal(id);

        cancel(event);
    }

    /**
     * Updates a {@link Goal} in the database
     *
     * @param event The {@link ActionEvent} from pressing the {@code Update} {@link Button}
     */
    @FXML
    private void update(ActionEvent event) {
        if (nameField.getText().isBlank() || targetField.getText().isBlank()) return;

        goalDAO.updateGoal(nameField.getText(), Double.parseDouble(targetField.getText()), id);

        cancel(event);
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
}
