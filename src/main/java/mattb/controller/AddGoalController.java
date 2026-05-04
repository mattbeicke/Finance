package mattb.controller;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import mattb.Main;
import mattb.dao.AccountDAO;
import mattb.dao.AccountDAOImpl;
import mattb.dao.GoalDAO;
import mattb.dao.GoalDAOImpl;
import mattb.model.Account;
import mattb.model.Goal;

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

    private GoalDAO goalDAO;
    private AccountDAO accountDAO;

    /**
     * Initializes {@link FXML} items for the {@code Add New Goal} modal and the {@code DAOs}
     */
    @FXML
    private void initialize() {
        goalDAO = new GoalDAOImpl(Main.getConn());
        accountDAO = new AccountDAOImpl(Main.getConn());

        ObservableList<String> accounts = accountDAO.getAccountNames();
        accountCombo.setItems(accounts);

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
        int accId = getAccId();
        if (accId == -1 || nameField.getText().isBlank() || targetField.getText().isBlank()) return;

        goalDAO.saveGoal(accId, nameField.getText(), Double.parseDouble(targetField.getText()));

        cancel(event);
    }

    /**
     * Gets the database id of the currently selected {@link Account} from the {@link ComboBox}
     *
     * @return Database id of the selected {@link Account} or -1 if it's not found
     */
    public int getAccId() {
        if (accountCombo.getValue().isBlank()) return -1;

        return accountDAO.getAccId(accountCombo.getValue());
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
