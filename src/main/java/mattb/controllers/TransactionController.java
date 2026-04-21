package mattb.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import mattb.FinanceException;
import mattb.Main;
import mattb.model.Transaction;

import java.io.IOException;
import java.sql.*;
import java.util.HashMap;
import java.util.HashSet;

import static mattb.FinanceError.*;

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
    private HashSet<Integer> hiddenTransactions = null;

    private Connection conn = null;
    private boolean onHidden = false;

    /**
     * Initializes all FXML items for the transaction tab
     */
    @FXML
    public void initialize() {
        conn = Main.getConn();
        hiddenTransactions = Main.getHiddenTransactions();

        if (colDate != null) {
            colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
            colFrom.setCellValueFactory(new PropertyValueFactory<>("fromAccountName"));
            colTo.setCellValueFactory(new PropertyValueFactory<>("toAccountName"));
            colCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
            colAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
            colMemo.setCellValueFactory(new PropertyValueFactory<>("memo"));

            colAmount.setCellFactory(_ -> new TableCell<>() {
                @Override
                protected void updateItem(Double balance, boolean empty) {
                    super.updateItem(balance, empty);
                    if (empty || balance == null) {
                        setText(null);
                    } else {
                        setText(Main.formatDouble(balance));
                    }
                }
            });

            transactionTable.setItems(masterData);
            refreshTable();
        }
    }

    /**
     * Refreshes the transaction table with either not hidden or hidden transactions
     */
    private void refreshTable() {
        String sql;
        if (!onHidden) {
            sql = """
                    select t_id, date, fa.name as from_acc_name, ta.name as to_acc_name, amount, memo, cat_name from "transaction"
                    left join tcat on t_id = trans
                    left join category on cat = cat_id
                    left join account ta on to_acc = ta.acc_id
                    left join account fa on from_acc = fa.acc_id
                    """;
        } else {
            sql = """
                    select t.t_id, date, fa.name as from_acc_name, ta.name as to_acc_name, amount, memo, cat_name from "transaction" t
                    join hidden_transactions ht on ht.t_id = t.t_id
                    left join tcat on t.t_id = trans
                    left join category on cat = cat_id
                    left join account ta on to_acc = ta.acc_id
                    left join account fa on from_acc = fa.acc_id
                    """;
        }
        map = new HashMap<>();
        masterData.clear();

        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                if (!onHidden && hiddenTransactions.contains(rs.getInt("t_id"))) continue;
                if (map.containsKey(rs.getInt("t_id"))) {
                    map.get(rs.getInt("t_id")).setCategory(map.get(rs.getInt("t_id")).getCategory() + ", " + rs.getString("cat_name"));
                } else {
                    map.put(rs.getInt("t_id"), new Transaction(
                            rs.getString("to_acc_name"),
                            rs.getString("from_acc_name"),
                            rs.getDouble("amount"),
                            rs.getString("cat_name"),
                            rs.getString("memo"),
                            new java.util.Date(1000L * rs.getInt("date"))
                    ));
                }
            }
        } catch (SQLException ignored) {
            throw new FinanceException(LOAD_TRANSACTIONS_FAIL);
        }

        masterData.addAll(map.values());
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

        String sql;
        if (onHidden) {
            sql = "delete from hidden_transactions where t_id=?";
        } else {
            sql = "insert or ignore into hidden_transactions (t_id) values (?)";
        }

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, transactionId);

            pstmt.executeUpdate();

            Main.updateHidden();
            refreshTable();
        } catch (SQLException ignored) {
            throw new FinanceException(UPDATE_HIDDEN_TRANSACTION_LIST_FAIL);
        }
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

        Main.updateHidden();
        refreshTable();
    }

    /**
     * Opens create new transaction modal
     */
    @FXML
    private void addNew() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mattb/controllers/add_transaction.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Add New Transaction");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            refreshTable();
        } catch (IOException ignored) {
            throw new FinanceException(OPEN_NEW_TRANSACTION_MODAL_FAIL);
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mattb/controllers/add_transaction.fxml"));
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
            throw new FinanceException(OPEN_EDIT_TRANSACTION_MODAL_FAIL);
        }
    }
}
