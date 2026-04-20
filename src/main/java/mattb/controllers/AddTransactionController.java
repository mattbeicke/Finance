package mattb.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import mattb.Main;
import mattb.model.Transaction;

import java.sql.*;
import java.time.LocalDate;
import java.time.ZoneId;

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

    private Connection conn = null;
    private boolean editing;
    private int id;

    /**
     * Initializes all FXML items for the add transaction modal
     */
    @FXML
    public void initialize() {
        conn = Main.getConn();

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

    /**
     * Activates when save button is pressed on the "create transaction" modal.
     * Populates the transaction table with the provided data
     */
    @FXML
    private void onSave() {
        if (fromCombo.getValue().equals("Add more via Accounts tab") || toCombo.getValue().equals("Add more via Accounts tab")) {
            return;
        }

        String sql;
        if (editing) {
            sql = "update \"transaction\" set date=?,from_acc=?,to_acc=?,amount=?,memo=? where t_id=?";
        } else {
            sql = "insert into \"transaction\" (date, from_acc, to_acc, amount, memo) values (?,?,?,?,?)";
        }
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
            if (editing) {
                pstmt.setInt(6, id);
            }

            pstmt.executeUpdate();

            addCategory();

            ((Stage) amountField.getScene().getWindow()).close();
        } catch (Exception e) {
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
     * Sets fields of transaction edit modal
     *
     * @param t  {@link Transaction} who is being edited
     * @param id Database id of {@link Transaction} who is being edited
     */
    public void setFields(Transaction t, int id) {
        editing = true;
        this.id = id;

        fromCombo.setValue(t.getFromAccountName());
        toCombo.setValue(t.getToAccountName());
        if (t.getCategory() == null) {
            categoryField.setText("");
        } else {
            categoryField.setText(t.getCategory());
        }
        amountField.setText(String.valueOf(t.getAmount()));
        if (t.getMemo() == null) {
            memoField.setText("");
        } else {
            memoField.setText(t.getMemo());
        }
        datePicker.setValue(LocalDate.ofInstant(t.getDate().toInstant(), ZoneId.systemDefault()));
    }
}
