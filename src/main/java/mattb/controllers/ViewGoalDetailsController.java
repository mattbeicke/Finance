package mattb.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import mattb.model.Goal;

public class ViewGoalDetailsController {
    @FXML
    private TextField nameField;
    @FXML
    private Label account;
    @FXML
    private TextField targetField;

    @FXML
    private void initialize() {
    }

    @FXML
    private void delete(ActionEvent event) {

        cancel(event);
    }

    @FXML
    private void update(ActionEvent event) {

        cancel(event);
    }

    @FXML
    private void cancel(ActionEvent event) {
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.close();
    }

    public void setFields(Goal goal, int id) {

    }
}
