package mattb.controllers;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import mattb.Main;
import mattb.dao.AddGoalDAO;
import mattb.dao.AddGoalDAOImpl;

public class AddGoalController {
    @FXML
    private TextField nameField;
    @FXML
    private ComboBox<String> accountCombo;
    @FXML
    private TextField targetField;

    private AddGoalDAO addGoalDAO;

    /**
     * Initializes all FXML items for the add goal modal
     */
    @FXML
    private void initialize() {
        addGoalDAO = new AddGoalDAOImpl(Main.getConn());

        ObservableList<String> accounts = addGoalDAO.getAccounts();
        accountCombo.setItems(accounts);

        targetField.textProperty().addListener((_, oldVal, newVal) -> {
            if (!newVal.matches("\\d*(\\.\\d*)?")) {
                targetField.setText(oldVal);
            }
        });
    }

    /**
     * Saves new goal to database
     *
     * @param event Button press event
     */
    @FXML
    private void save(ActionEvent event) {
        int accId = getAccId();
        if (accId == -1 || nameField.getText().isBlank() || targetField.getText().isBlank()) return;

        addGoalDAO.saveGoal(accId, nameField.getText(), Double.parseDouble(targetField.getText()));

        cancel(event);
    }

    /**
     * Gets the id number of the currently selected account
     *
     * @return Database id number that corresponds to the account selected in the type combo box or -1 if it cannot be found
     */
    public int getAccId() {
        if (accountCombo.getValue().isBlank()) return -1;

        return addGoalDAO.getAccId(accountCombo.getValue());
    }

    /**
     * Exits add goal modal
     *
     * @param event Button press event
     */
    @FXML
    private void cancel(ActionEvent event) {
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.close();
    }
}
