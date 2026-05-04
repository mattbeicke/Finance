package mattb.controller;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import mattb.Config;
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

/**
 * Handles UI interactions on the {@code Transaction} tab
 *
 * @author Matthew Beicke
 */
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
    @FXML
    private Button left;
    @FXML
    private Button right;
    @FXML
    private Label pageLabel;

    private final ObservableList<Transaction> masterData = FXCollections.observableArrayList();
    private HashMap<Integer, Transaction> map;

    private TransactionDAO transactionDAO;

    private boolean onHidden = false;
    private int page;
    private int perPage;

    /**
     * Initializes {@link FXML} items for the {@code Transaction} tab and the {@link TransactionDAO DAO}
     */
    @FXML
    public void initialize() {
        transactionDAO = new TransactionDAOImpl(Main.getConn());

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

        perPage = Config.getNumTransactions();
        page = 1;
        updatePageInfo();
    }

    /**
     * Refreshes the {@link Transaction} {@link TableView Table} with either hidden or non-hidden {@link Transaction Transactions}
     */
    private void refreshTable() {
        map = transactionDAO.getAllTransactions(onHidden, perPage, page);
        masterData.setAll(map.values());
    }

    /**
     * Gets the database id of the inputted {@link Transaction}
     *
     * @param t The {@link Transaction} to get the id of
     * @return The database id of the {@link Transaction} or -1 if it's not found
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
     * Hides or unhides currently selected {@link Transaction} in the {@link TableView Table}
     */
    @FXML
    private void hideSelected() {
        Transaction selected = transactionTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        int transactionId = getId(selected);
        if (transactionId == -1) return;

        transactionDAO.updateTransactionVisibility(transactionId, onHidden);

        updatePageInfo();
    }

    /**
     * Toggles {@link Transaction} list between showing the hidden and non-hidden {@link Transaction Transactions}
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

        page = 1;
        updatePageInfo();
    }

    /**
     * Opens the {@code Add New Transaction} modal
     */
    @FXML
    private void addNew() {
        try {
            URL resource = getClass().getResource("/mattb/controller/add_transaction.fxml");
            if (resource == null) {
                new FinanceException(OPEN_NEW_TRANSACTION_MODAL_FAIL).displayAndLog();
                return;
            }
            FXMLLoader loader = new FXMLLoader(resource);
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Add New Transaction");
            Scene scene = new Scene(root);
            Main.darkMode(scene);
            stage.setScene(scene);
            stage.showAndWait();

            updatePageInfo();
        } catch (IOException ignored) {
            new FinanceException(OPEN_NEW_TRANSACTION_MODAL_FAIL).displayAndLog();
        }
    }

    /**
     * Opens the {@code Edit Transaction} modal, filling all fields with the currently selected {@link Transaction Transaction's} information
     */
    @FXML
    private void editSelected() {
        Transaction selected = transactionTable.getSelectionModel().getSelectedItem();
        int id = getId(selected);
        try {
            URL resource = getClass().getResource("/mattb/controller/add_transaction.fxml");
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
            Scene scene = new Scene(root);
            Main.darkMode(scene);
            stage.setScene(scene);
            stage.showAndWait();

            refreshTable();
        } catch (IOException ignored) {
            new FinanceException(OPEN_EDIT_TRANSACTION_MODAL_FAIL).displayAndLog();
        }
    }

    @FXML
    private void leftPage() {
        page--;
        updatePageInfo();
    }

    @FXML
    private void rightPage() {
        page++;
        updatePageInfo();
    }

    /**
     * Refreshes the page information then refreshes the table
     */
    private void updatePageInfo() {
        int maxPage = (int) Math.ceil(transactionDAO.getNumTransactions(onHidden) / ((double) perPage));

        if (page > maxPage) page = maxPage;

        pageLabel.setText("Page " + page + " of " + maxPage);
        left.setDisable(page <= 1);
        right.setDisable(page >= maxPage);
        refreshTable();
    }
}
