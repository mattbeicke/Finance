package mattb.controllers;

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
import mattb.dao.AddTransactionDAO;
import mattb.dao.AddTransactionDAOImpl;
import mattb.model.Transaction;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;

import static mattb.FinanceError.OPEN_UPDATE_BALANCE_MODAL_FAIL;

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

    private AddTransactionDAO addTransactionDAO;

    private boolean editing;
    private int id;
    private boolean update = false;

    /**
     * Initializes all FXML items for the add transaction modal
     */
    @FXML
    public void initialize() {
        addTransactionDAO = new AddTransactionDAOImpl(Main.getConn());

        if (fromCombo != null && toCombo != null) {
            ObservableList<String> accountNames = addTransactionDAO.loadAccountNames();

            fromCombo.setItems(accountNames);
            toCombo.setItems(accountNames);

            amountField.textProperty().addListener((_, oldVal, newVal) -> {
                if (!newVal.matches("\\d*(\\.\\d*)?")) {
                    amountField.setText(oldVal);
                }
            });
        }
    }

    /**
     * Activates when saveTransaction button is pressed on the "create transaction" modal.
     * Populates the transaction table with the provided data
     */
    @FXML
    private void onSave() {
        int fromAccId = addTransactionDAO.getAccId(fromCombo.getValue());
        int toAccId = addTransactionDAO.getAccId(toCombo.getValue());
        if (fromAccId == -1 || toAccId == -1 || amountField.getText().isBlank() || fromCombo.getValue().equals("Add more via Accounts tab") || toCombo.getValue().equals("Add more via Accounts tab")) {
            return;
        }

        addTransactionDAO.saveTransaction(datePicker.getValue(), fromAccId, toAccId, Double.parseDouble(amountField.getText()), memoField.getText(), id, editing);

        addTransactionDAO.saveCategories(categoryField.getText());

        try {
            URL resource = getClass().getResource("/mattb/controllers/update_balance.fxml");
            if (resource == null) {
                new FinanceException(OPEN_UPDATE_BALANCE_MODAL_FAIL).displayAndLog();
                return;
            }
            FXMLLoader loader = new FXMLLoader(resource);
            Parent root = loader.load();

            AddTransactionController popupController = loader.getController();

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Update Balances");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            if (popupController.update) {
                update = true;
            }
        } catch (IOException ignored) {
            new FinanceException(OPEN_UPDATE_BALANCE_MODAL_FAIL).displayAndLog();
        }

        if (update) {
            if (amountField.getText().isBlank() || fromCombo.getValue().isBlank() || toCombo.getValue().isBlank())
                return;

            addTransactionDAO.updateBalances(Double.parseDouble(amountField.getText()), fromAccId, toAccId);
        }

        ((Stage) amountField.getScene().getWindow()).close();
    }

    /**
     * Exits the "create new transaction" modal
     *
     * @param event Button press event
     */
    @FXML
    private void cancel(ActionEvent event) {
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.close();
    }

    /**
     * Sets fields of transaction edit modal
     *
     * @param t  {@link Transaction} who is being edited
     * @param id Database id of {@link Transaction} who is being edited
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
     * Indicates a 'yes' answer to the update balances modal
     *
     * @param event Button press {@link ActionEvent}
     */
    @FXML
    private void yes(ActionEvent event) {
        update = true;
        cancel(event);
    }

    /**
     * Indicates a 'no' answer to the update balances modal
     *
     * @param event Button press {@link ActionEvent}
     */
    @FXML
    private void no(ActionEvent event) {
        update = false;
        cancel(event);
    }
}
