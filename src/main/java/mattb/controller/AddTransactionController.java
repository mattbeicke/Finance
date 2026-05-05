package mattb.controller;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import mattb.FinanceException;
import mattb.Main;
import mattb.model.Account;
import mattb.model.Transaction;
import mattb.model.TransactionRequest;
import mattb.model.TransactionResponse;
import mattb.service.AccountService;
import mattb.service.TransactionService;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;

import static mattb.FinanceError.OPEN_UPDATE_BALANCE_MODAL_FAIL;

/**
 * Handles UI interactions on the {@code Add New Transaction} modal
 *
 * @author Matthew Beicke
 */
public class AddTransactionController {
    @FXML
    private ComboBox<String> fromCombo;
    @FXML
    private ComboBox<String> toCombo;
    @FXML
    private TextField amountField;
    @FXML
    private TextField categoryField;
    @FXML
    private TextField memoField;
    @FXML
    private DatePicker datePicker;

    private TransactionService transactionService;
    private AccountService accountService;

    private boolean editing;
    private int id;
    private boolean saveClicked = false;

    /**
     * Initializes {@link FXML} items for the {@code Add New Transaction} modal
     */
    @FXML
    public void initialize() {
        transactionService = Main.getTransactionService();
        accountService = Main.getAccountService();

        ObservableList<String> accountNames = accountService.getAccountNamesExternal();
        fromCombo.setItems(accountNames);
        toCombo.setItems(accountNames);

        amountField.textProperty().addListener((_, oldVal, newVal) -> {
            if (!newVal.matches("\\d*(\\.\\d*)?")) {
                amountField.setText(oldVal);
            }
        });
    }

    /**
     * Saves a {@link Transaction} to the database
     *
     * @param event The {@link ActionEvent} from pressing the {@code Save} {@link Button}
     */
    @FXML
    private void onSave(ActionEvent event) {
        TransactionResponse response = transactionService.processTransaction(new TransactionRequest(
                datePicker.getValue(),
                fromCombo.getValue(),
                toCombo.getValue(),
                amountField.getText(),
                categoryField.getText(),
                memoField.getText(),
                id,
                editing
        ));

        if (!response.success()) {
            Main.showNotification(false, response.message());
            return;
        }

        if (response.requiresBalanceConfirmation() && promptForBalanceUpdate()) {
            if (!transactionService.updateBalances(fromCombo.getValue(), toCombo.getValue(), amountField.getText())) {
                Main.showNotification(false, "Balances failed to update");
            }
        }

        saveClicked = true;
        cancel(event);
    }


    /**
     * Starts the {@code Update Balances} modal
     *
     * @return {@code true} if we are updating {@link Account} balances, {@code false} if not
     */
    private boolean promptForBalanceUpdate() {
        try {
            URL resource = getClass().getResource("/mattb/controller/update_balance.fxml");
            if (resource == null) {
                new FinanceException(OPEN_UPDATE_BALANCE_MODAL_FAIL).displayAndLog();
                return false;
            }
            FXMLLoader loader = new FXMLLoader(resource);
            Parent root = loader.load();

            UpdateBalancesController controller = loader.getController();

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Update Balances");
            Scene scene = new Scene(root);
            Main.darkMode(scene);
            stage.setScene(scene);
            stage.showAndWait();

            return controller.getUpdate();
        } catch (IOException ignored) {
            new FinanceException(OPEN_UPDATE_BALANCE_MODAL_FAIL).displayAndLog();
            return false;
        }
    }

    /**
     * Exits the {@code Add New Transaction} or {@code Edit Transaction} modal
     *
     * @param event The {@link ActionEvent} from pressing the {@code Cancel} {@link Button}
     */
    @FXML
    private void cancel(ActionEvent event) {
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.close();
    }

    /**
     * Sets the fields of the {@code Edit Transaction} modal
     *
     * @param t  The {@link Transaction} who is being edited
     * @param id Database id of {@code t}
     */
    public void setFields(Transaction t, int id) {
        if (t == null || id <= 0) return;

        editing = true;
        this.id = id;

        fromCombo.setValue(t.fromAccountName());
        toCombo.setValue(t.toAccountName());
        if (t.category() == null) {
            categoryField.setText("");
        } else {
            categoryField.setText(t.category());
        }
        amountField.setText(String.valueOf(t.amount()));
        if (t.memo() == null) {
            memoField.setText("");
        } else {
            memoField.setText(t.memo());
        }
        datePicker.setValue(LocalDate.ofInstant(t.date().toInstant(), ZoneId.systemDefault()));
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
