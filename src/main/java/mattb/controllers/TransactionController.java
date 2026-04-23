package mattb.controllers;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import mattb.FinanceException;
import mattb.Main;
import mattb.dao.TransactionDAO;
import mattb.dao.TransactionDAOImpl;
import mattb.model.Transaction;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;

import static mattb.FinanceError.OPEN_EDIT_TRANSACTION_MODAL_FAIL;
import static mattb.FinanceError.OPEN_NEW_TRANSACTION_MODAL_FAIL;

public class TransactionController {
    @FXML
    private TableView<Transaction> transactionTable;
    @FXML
    private TableColumn<Transaction, Date> colDate;
    @FXML
    private TableColumn<Transaction, String> colFrom;
    @FXML
    private TableColumn<Transaction, String> colTo;
    @FXML
    private TableColumn<Transaction, String> colCategory;
    @FXML
    private TableColumn<Transaction, Double> colAmount;
    @FXML
    private TableColumn<Transaction, String> colMemo;
    @FXML
    private Button addButton;
    @FXML
    private Button hideButton;
    @FXML
    private Button viewButton;

    private final ObservableList<Transaction> masterData = FXCollections.observableArrayList();
    private HashMap<Integer, Transaction> map;

    private TransactionDAO transactionDAO;

    private boolean onHidden = false;

    /**
     * Initializes all FXML items for the transaction tab
     */
    @FXML
    public void initialize() {
        transactionDAO = new TransactionDAOImpl(Main.getConn());

        if (colDate != null) {
            colDate.setCellValueFactory(cellData ->
                    new ReadOnlyObjectWrapper<>(cellData.getValue().date())
            );
            colFrom.setCellValueFactory(cellData ->
                    new ReadOnlyObjectWrapper<>(cellData.getValue().fromAccountName())
            );
            colTo.setCellValueFactory(cellData ->
                    new ReadOnlyObjectWrapper<>(cellData.getValue().toAccountName())
            );
            colCategory.setCellValueFactory(cellData ->
                    new ReadOnlyObjectWrapper<>(cellData.getValue().category())
            );
            colAmount.setCellValueFactory(cellData ->
                    new ReadOnlyObjectWrapper<>(cellData.getValue().amount())
            );
            colMemo.setCellValueFactory(cellData ->
                    new ReadOnlyObjectWrapper<>(cellData.getValue().memo())
            );

            Main.useCurrency(colAmount);

            transactionTable.setItems(masterData);
            refreshTable();
        }
    }

    /**
     * Refreshes the transaction table with either not hidden or hidden transactions
     */
    private void refreshTable() {
        map = transactionDAO.getAllTransactions(onHidden);
        masterData.setAll(map.values());
    }

    /**
     * Gets the id of the inputted {@link Transaction}
     *
     * @param t {@link Transaction} to get the id of
     * @return database id of the {@link Transaction} or -1 if it's not found
     */
    private int getId(Transaction t) {
        if (t == null) return -1;

        for (Integer i : map.keySet()) {
            if (map.get(i).equals(t)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Hides or unhides currently selected transaction in list
     */
    @FXML
    private void hideSelected() {
        Transaction selected = transactionTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        int transactionId = getId(selected);
        if (transactionId == -1) return;

        transactionDAO.updateTransactionVisibility(transactionId, onHidden);

        refreshTable();
    }

    /**
     * Toggles transaction list between hidden and not hidden transactions
     */
    @FXML
    private void viewHidden() {
        if (!onHidden) {
            onHidden = true;
            addButton.setVisible(false);
            addButton.setManaged(false);
            hideButton.setText("Unhide Selected");
            viewButton.setText("Reset View");
        } else {
            onHidden = false;
            addButton.setVisible(true);
            addButton.setManaged(true);
            hideButton.setText("Hide Selected");
            viewButton.setText("View Hidden");
        }

        refreshTable();
    }

    /**
     * Opens create new transaction modal
     */
    @FXML
    private void addNew() {
        try {
            URL resource = getClass().getResource("/mattb/controllers/add_transaction.fxml");
            if (resource == null) {
                new FinanceException(OPEN_NEW_TRANSACTION_MODAL_FAIL).displayAndLog();
                return;
            }
            FXMLLoader loader = new FXMLLoader(resource);
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Add New Transaction");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            refreshTable();
        } catch (IOException ignored) {
            new FinanceException(OPEN_NEW_TRANSACTION_MODAL_FAIL).displayAndLog();
        }
    }

    /**
     * Opens edit transaction modal, filling all fields with current transaction information
     */
    @FXML
    private void editSelected() {
        Transaction selected = transactionTable.getSelectionModel().getSelectedItem();
        int id = getId(selected);
        try {
            URL resource = getClass().getResource("/mattb/controllers/add_transaction.fxml");
            if (resource == null) {
                new FinanceException(OPEN_NEW_TRANSACTION_MODAL_FAIL).displayAndLog();
                return;
            }
            FXMLLoader loader = new FXMLLoader(resource);
            Parent root = loader.load();

            AddTransactionController controller = loader.getController();
            controller.setFields(selected, id);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Edit Transaction");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            refreshTable();
        } catch (IOException ignored) {
            new FinanceException(OPEN_EDIT_TRANSACTION_MODAL_FAIL).displayAndLog();
        }
    }
}
