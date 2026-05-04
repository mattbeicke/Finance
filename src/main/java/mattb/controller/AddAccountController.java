package mattb.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import mattb.Main;
import mattb.model.Account;
import mattb.service.AccountService;

/**
 * Handles UI interactions on the {@code Add New Account} modal
 *
 * @author Matthew Beicke
 */
public class AddAccountController {
    @FXML
    private ComboBox<String> typeCombo;
    @FXML
    private TextField nameField;
    @FXML
    private TextField balanceField;

    private AccountService accountService;

    private boolean editing;
    private int id;

    /**
     * Initializes {@link FXML} items for the {@code Add New Account} modal and the {@link AccountService}
     */
    @FXML
    public void initialize() {
        accountService = Main.getAccountService();

        typeCombo.setItems(accountService.getAccountTypes());

        balanceField.textProperty().addListener((_, oldVal, newVal) -> {
            if (!newVal.matches("\\d*(\\.\\d*)?")) {
                balanceField.setText(oldVal);
            }
        });
    }

    /**
     * Saves a new {@link Account} to the database
     *
     * @param event The {@link ActionEvent} from pressing the {@code Save} {@link Button}
     */
    @FXML
    private void onAccountSave(ActionEvent event) {
        int typeId = accountService.getTypeIdByName(typeCombo.getValue());

        if (typeId == -1 || balanceField.getText().isBlank() || nameField.getText().isBlank()) return;

        accountService.saveAccount(typeId, Double.parseDouble(balanceField.getText()), nameField.getText(), id, editing);
        cancel(event);
    }

    /**
     * Exits the {@code Add New Account} or {@code Edit Account} modal
     *
     * @param event The {@link ActionEvent} from pressing the {@code Cancel} {@link Button}
     */
    @FXML
    private void cancel(ActionEvent event) {
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.close();
    }

    /**
     * Sets the fields of the {@code Edit Account} modal
     *
     * @param a  The {@link Account} who is being edited
     * @param id Database id of {@code a}
     */
    public void setFields(Account a, int id) {
        if (a == null || id <= 0) return;

        editing = true;
        this.id = id;

        nameField.setText(a.name());
        typeCombo.setValue(a.type());
        balanceField.setText(String.valueOf(a.balance()));
    }
}
