package mattb.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import static mattb.FinanceError.*;
import mattb.FinanceException;
import mattb.Main;
import mattb.model.Account;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AddAccountController {
    @FXML
    private ComboBox<String> typeCombo;
    @FXML
    private TextField nameField;
    @FXML
    private TextField balanceField;

    private Connection conn = null;
    private boolean editing;
    private int id;

    /**
     * Initializes all FXML items for the add account modal
     */
    @FXML
    public void initialize() {
        conn = Main.getConn();

        ObservableList<String> types = FXCollections.observableArrayList();

        String sql = "select type_id, type from account_type";
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                if (rs.getInt("type_id") == 0) continue;
                types.add(rs.getString("type"));
            }

            types.add("Add more via Accounts tab");
            typeCombo.setItems(types);
        } catch (SQLException ignored) {
            throw new FinanceException(LOAD_ACCOUNT_TYPES_FAIL);
        }

        balanceField.textProperty().addListener((_, oldVal, newVal) -> {
            if (!newVal.matches("\\d*(\\.\\d*)?")) {
                balanceField.setText(oldVal);
            }
        });
    }

    /**
     * Saves new account to database
     */
    @FXML
    private void onAccountSave() {
        int typeId = getTypeId();
        if (typeId == -1 || balanceField.getText().isBlank() || nameField.getText().isBlank()) return;

        String sql;
        if (editing) {
            sql = "update account set acc_type=?,balance=?,name=? where acc_id=?";
        } else {
            sql = "insert or ignore into account (acc_type, balance, name) VALUES (?,?,?)";
        }
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, typeId);
            pstmt.setDouble(2, Double.parseDouble(balanceField.getText()));
            pstmt.setString(3, nameField.getText());
            if (editing) {
                pstmt.setInt(4, id);
            }

            pstmt.executeUpdate();

            ((Stage) typeCombo.getScene().getWindow()).close();
        } catch (SQLException ignored) {
            throw new FinanceException(SAVE_ACCOUNT_FAIL);
        }
    }

    /**
     * Gets the id number of the currently selected type
     *
     * @return id number that corresponds to the type selected in the type combo box or -1 if it cannot be found
     */
    public int getTypeId() {
        if (typeCombo.getValue().isBlank()) return -1;

        String sql = "select type_id from account_type where type=?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, typeCombo.getValue());

            ResultSet rs = pstmt.executeQuery();

            if (!rs.next()) {
                return -1;
            }

            return rs.getInt("type_id");
        } catch (SQLException ignored) {
            throw new FinanceException(GET_ACCOUNT_TYPE_ID_FAIL);
        }
    }

    /**
     * Exits either add/edit account modal
     *
     * @param event Button press event
     */
    @FXML
    private void cancel(ActionEvent event) {
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.close();
    }

    /**
     * Sets fields of account edit modal
     *
     * @param a  {@link Account} who is being edited
     * @param id Database id of {@link Account} who is being edited
     */
    public void setFields(Account a, int id) {
        if (a == null || id <= 0) return;

        editing = true;
        this.id = id;

        nameField.setText(a.getName());
        typeCombo.setValue(a.getType());
        balanceField.setText(String.valueOf(a.getBalance()));
    }
}
