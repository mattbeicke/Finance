package mattb.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import mattb.Main;
import mattb.model.Transaction;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.HashSet;

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
    @FXML
    private Button addButton;
    @FXML
    private Button hideButton;
    @FXML
    private Button viewButton;

    private final ObservableList<Transaction> masterData = FXCollections.observableArrayList();
    private HashMap<Integer, Transaction> map;

    private Connection conn = null;
    private HashSet<Integer> hiddenTransactions = null;

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

            transactionTable.setItems(masterData);
            refreshTable();
        }

        if (fromCombo != null && toCombo != null) {
            ObservableList<String> accountNames = FXCollections.observableArrayList();

            String sql = "select name from account where acc_id not in (select acc_id from hidden_accounts)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql);
                 ResultSet rs = pstmt.executeQuery()) {

                while (rs.next()) {
                    accountNames.add(rs.getString("name"));
                }

                accountNames.add("Add more via Accounts tab");
                fromCombo.setItems(accountNames);
                toCombo.setItems(accountNames);
            } catch (SQLException e) {
                System.err.println("Could not load accounts: " + e.getMessage());
            }

            amountField.textProperty().addListener((_, oldVal, newVal) -> {
                if (!newVal.matches("\\d*(\\.\\d*)?")) {
                    amountField.setText(oldVal);
                }
            });
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
        } catch (SQLException e) {
            e.printStackTrace();
        }

        masterData.addAll(map.values());
    }

    /**
     * Opens the "create new transaction" modal
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets an account id from an account name
     *
     * @param accName Account name
     * @return Account ID associated with the account name or -1 if no account was found
     */
    private int getAccId(String accName) {
        String sql = "select acc_id from account where name=?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, accName);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("acc_id");
            } else {
                return -1;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Activates when save button is pressed on the "create transaction" modal.
     * Populates the transaction table with the provided data
     */
    @FXML
    private void onSave() {
        if (fromCombo.getValue().equals("Add more via Accounts tab") || toCombo.getValue().equals("Add more via Accounts tab")) {
            return;
        }

        String sql = "insert into \"transaction\" (date, from_acc, to_acc, amount, memo) values (?,?,?,?,?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            LocalDate selectedDate = datePicker.getValue();
            if (selectedDate == null) {
                selectedDate = LocalDate.now();
            }
            pstmt.setInt(1, (int) selectedDate.atStartOfDay(ZoneId.systemDefault()).toEpochSecond());
            pstmt.setInt(2, getAccId(fromCombo.getValue()));
            pstmt.setInt(3, getAccId(toCombo.getValue()));
            pstmt.setDouble(4, Double.parseDouble(amountField.getText()));
            if (!memoField.getText().isBlank()) {
                pstmt.setString(5, memoField.getText());
            } else {
                pstmt.setString(5, "");
            }

            pstmt.executeUpdate();

            addCategory();

            ((Stage) amountField.getScene().getWindow()).close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates a category if it does not exist
     *
     * @param cat Category to add if needed
     */
    private void createCatIfNeeded(String cat) {
        String sql = "insert or ignore into category(cat_name) values (?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, cat);

            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Populates the tcat table for the transaction
     */
    private void addCategory() {
        // Find transaction id
        int t_id;
        String sql = "select max(t_id) as t_id from \"transaction\"";
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:finance.db");
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            ResultSet rs = pstmt.executeQuery();
            rs.next();
            t_id = rs.getInt("t_id");
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        // Get all categories the user entered
        String[] categories = categoryField.getText().split(",\\s*");
        int[] cats = new int[categories.length];

        for (int i = 0; i < categories.length; i++) {
            String category = categories[i];
            createCatIfNeeded(category);

            sql = "select cat_id from category where cat_name=?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, category);

                ResultSet rs = pstmt.executeQuery();
                rs.next();
                cats[i] = rs.getInt("cat_id");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Update the tcat table
        sql = "insert into tcat(trans, cat) values (?,?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            conn.setAutoCommit(false);

            for (int category : cats) {
                pstmt.setInt(1, t_id);
                pstmt.setInt(2, category);
                pstmt.addBatch();
            }
            pstmt.executeBatch();
            conn.commit();
        } catch (Exception e) {
            if (conn != null) try {
                conn.rollback();
            } catch (Exception ignored) {
            }
            e.printStackTrace();
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (Exception ignored) {
            }
        }
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
     * Gets the id of the inputted {@link Transaction}
     *
     * @param t {@link Transaction} to get the id of
     * @return database id of the {@link Transaction} or -1 if it's not found
     */
    private int getId(Transaction t) {
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
        String sql;
        if (onHidden) {
            sql = "delete from hidden_transactions where t_id=?";
        } else {
            sql = "insert or ignore into hidden_transactions (t_id) values (?)";
        }

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, getId(selected));

            pstmt.executeUpdate();

            Main.updateHidden();
            refreshTable();
        } catch (Exception e) {
            e.printStackTrace();
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
}
