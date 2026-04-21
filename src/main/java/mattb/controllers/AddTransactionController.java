package mattb.controllers;

import javafx.collections.FXCollections;
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
import mattb.Main;
import mattb.model.Transaction;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;

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
    private boolean update = false;

    /**
     * Initializes all FXML items for the add transaction modal
     */
    @FXML
    public void initialize() {
        conn = Main.getConn();

        if (fromCombo != null && toCombo != null) {
            ObservableList<String> accountNames = FXCollections.observableArrayList();

            String sql = "select name from account where acc_id not in (select acc_id from hidden_accounts)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {

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
     * Activates when save button is pressed on the "create transaction" modal.
     * Populates the transaction table with the provided data
     */
    @FXML
    private void onSave() {
        int fromAccId = getAccId(fromCombo.getValue());
        int toAccId = getAccId(toCombo.getValue());
        if (fromAccId == -1 || toAccId == -1 || datePicker.getValue() == null || amountField.getText().isBlank() || fromCombo.getValue().equals("Add more via Accounts tab") || toCombo.getValue().equals("Add more via Accounts tab")) {
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
            pstmt.setInt(2, fromAccId);
            pstmt.setInt(3, toAccId);
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

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/mattb/controllers/update_balance.fxml"));
                Parent root = loader.load();

                AddTransactionController popupController = loader.getController();

                Stage stage = new Stage();
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setTitle("Update Balances");
                stage.setScene(new Scene(root));
                stage.showAndWait();

                if (popupController.update) {
                    updateBalances();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (update) {
                updateBalances();
            }

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
        if (accName == null || accName.isBlank()) return -1;

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
     * Populates the tcat table for the transaction
     */
    private void addCategory() {
        // Find transaction id
        int t_id;
        String sql = "select max(t_id) as t_id from \"transaction\"";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

            ResultSet rs = pstmt.executeQuery();
            rs.next();
            t_id = rs.getInt("t_id");
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        if (t_id <= 0) return;

        // Get all categories the user entered
        String[] categories = categoryField.getText().split(",\\s*");

        if (categories.length == 0) return;

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
        String placeholders = String.join(",", Collections.nCopies(cats.length, "(?,?)"));
        sql = "insert into tcat(trans, cat) values " + placeholders;
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (int i = 0; i < cats.length; i++) {
                pstmt.setInt(i * 2 + 1, t_id);
                pstmt.setInt(i * 2 + 2, cats[i]);
            }

            pstmt.executeUpdate();
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
        if (cat == null || cat.isBlank()) return;

        String sql = "insert or ignore into category(cat_name) values (?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, cat);

            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
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
        if (t == null || id <= 0) return;

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

    /**
     * Updates account balances (if they are not the reserved external one)
     */
    private void updateBalances() {
        if (amountField.getText().isBlank() || fromCombo.getValue().isBlank() || toCombo.getValue().isBlank()) return;

        double amount = Double.parseDouble(amountField.getText());
        int fromAccId = getAccId(fromCombo.getValue());
        int toAccId = getAccId(toCombo.getValue());

        // Update from account's balance
        if (fromAccId != 0) {
            String sql = "update account set balance=balance-? where acc_id=?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setDouble(1, amount);
                pstmt.setInt(2, fromAccId);

                pstmt.executeUpdate();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Update to account's balance
        if (toAccId != 0) {
            String sql = "update account set balance=balance+? where acc_id=?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setDouble(1, amount);
                pstmt.setInt(2, toAccId);

                pstmt.executeUpdate();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
