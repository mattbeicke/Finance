package mattb.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import mattb.ServiceFactory;
import mattb.Utilities;
import mattb.model.Account;
import mattb.model.AccountResponse;
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
    private boolean saveClicked = false;

    /**
     * Initializes {@link FXML} items for the {@code Add New Account} modal and the {@link AccountService}
     */
    @FXML
    public void initialize() {
        accountService = ServiceFactory.getAccountService();

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
        AccountResponse response = accountService.processAccount(typeCombo.getValue(), balanceField.getText(), nameField.getText(), id, editing);

        if (!response.success()) {
            Utilities.showNotification(false, response.message());
            return;
        }

        saveClicked = true;
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

    /**
     * Gets the status on if the save button was pressed or cancel button was pressed
     *
     * @return {@code true} if the save button was pressed, {@code false} if not
     */
    public boolean isSaveClicked() {
        return saveClicked;
    }
}
