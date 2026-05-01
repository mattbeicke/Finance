package mattb.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import mattb.Main;
import mattb.dao.UpdateGoalDAO;
import mattb.dao.UpdateGoalDAOImpl;
import mattb.model.Goal;

public class ViewGoalDetailsController {
    @FXML
    private TextField nameField;
    @FXML
    private Label account;
    @FXML
    private TextField targetField;

    private UpdateGoalDAO updateGoalDAO;

    private int id;

    @FXML
    private void initialize() {
        updateGoalDAO = new UpdateGoalDAOImpl(Main.getConn());

        targetField.textProperty().addListener((_, oldVal, newVal) -> {
            if (!newVal.matches("\\d*(\\.\\d*)?")) {
                targetField.setText(oldVal);
            }
        });
    }

    @FXML
    private void delete(ActionEvent event) {
        updateGoalDAO.deleteGoal(id);

        cancel(event);
    }

    @FXML
    private void update(ActionEvent event) {
        if (nameField.getText().isBlank() || targetField.getText().isBlank()) return;

        updateGoalDAO.updateGoal(nameField.getText(), Double.parseDouble(targetField.getText()), id);

        cancel(event);
    }

    @FXML
    private void cancel(ActionEvent event) {
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.close();
    }

    /**
     * Sets fields of view goal details modal
     *
     * @param g  {@link Goal} who is being viewed
     * @param id Database id of {@link Goal} who is being viewed
     */
    public void setFields(Goal g, int id) {
        if (g == null || id <= 0) return;

        this.id = id;

        nameField.setText(g.name());
        account.setText(g.account());
        targetField.setText(String.valueOf(g.target()));
    }
}
