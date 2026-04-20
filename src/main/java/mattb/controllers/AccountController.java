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
import mattb.model.Account;

import java.io.IOException;
import java.sql.*;
import java.util.HashMap;
import java.util.HashSet;

public class AccountController {
    @FXML
    private TableView<Account> accountTable;
    @FXML
    private TableColumn<Account, String> colName;
    @FXML
    private TableColumn<Account, Double> colBalance;
    @FXML
    private TableColumn<Account, String> colType;
    @FXML
    private ComboBox<String> typeCombo;
    @FXML
    private TextField nameField;
    @FXML
    private TextField balanceField;
    @FXML
    private TextField typeField;

    private final ObservableList<Account> masterData = FXCollections.observableArrayList();

    private Connection conn = null;
    private HashSet<Integer> hiddenAccounts = null;

    /**
     * Initializes all FXML items for the account tab
     */
    @FXML
    public void initialize() {
        conn = Main.getConn();
        hiddenAccounts = Main.getHiddenAccounts();

        if (colName != null) {
            colName.setCellValueFactory(new PropertyValueFactory<>("name"));
            colBalance.setCellValueFactory(new PropertyValueFactory<>("balance"));
            colType.setCellValueFactory(new PropertyValueFactory<>("type"));

            accountTable.setItems(masterData);
            refreshTable();
        }

        if (typeCombo != null) {
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
            } catch (SQLException e) {
                System.err.println("Could not load types: " + e.getMessage());
            }

            balanceField.textProperty().addListener((_, oldVal, newVal) -> {
                if (!newVal.matches("\\d*(\\.\\d*)?")) {
                    balanceField.setText(oldVal);
                }
            });
        }
    }

    /**
     * Refreshes the Account table
     */
    private void refreshTable() {
        HashMap<Integer, Account> map = new HashMap<>();
        masterData.clear();

        String sql = """
                select acc_id, name,balance,type from account
                left join account_type on acc_type = type_id
                """;
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                int acc_id = rs.getInt("acc_id");
                if (acc_id == 0 || hiddenAccounts.contains(acc_id)) continue;
                map.put(acc_id, new Account(
                        rs.getDouble("balance"),
                        rs.getString("type"),
                        rs.getString("name")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        masterData.addAll(map.values());
    }

    /**
     * Opens create new account modal
     */
    @FXML
    private void addNewAccount() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mattb/controllers/add_account.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Add New Account");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            refreshTable();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Opens create new account type modal
     */
    @FXML
    private void addNewType() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mattb/controllers/add_type.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Add New Account Type");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the id number of the currently selected type
     *
     * @return id number that corresponds to the type selected in the type combo box
     */
    public int getType() {
        String sql = "select type_id from account_type where type=?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, typeCombo.getValue());

            ResultSet rs = pstmt.executeQuery();

            if (!rs.next()) {
                return -1;
            }

            return rs.getInt("type_id");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Saves new account
     */
    @FXML
    private void onAccountSave() {
        String sql = "insert or ignore into account (acc_type, balance, name) VALUES (?,?,?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, getType());
            pstmt.setDouble(2, Double.parseDouble(balanceField.getText()));
            pstmt.setString(3, nameField.getText());

            pstmt.executeUpdate();

            ((Stage) typeCombo.getScene().getWindow()).close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Saves new type
     */
    @FXML
    private void onTypeSave() {
        if (typeField.getText().isBlank()) return;

        String sql = "insert or ignore into account_type(type) values (?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, typeField.getText());

            pstmt.executeUpdate();

            ((Stage) typeField.getScene().getWindow()).close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Exits either "create new" modal
     *
     * @param event Button press event
     */
    @FXML
    private void cancel(ActionEvent event) {
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.close();
    }
}
