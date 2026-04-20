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
    private TextField typeField;
    @FXML
    private Button addAccount;
    @FXML
    private Button addType;
    @FXML
    private Button hideAccount;
    @FXML
    private Button viewHidden;

    private final ObservableList<Account> masterData = FXCollections.observableArrayList();
    private HashMap<Integer, Account> map;
    private HashSet<Integer> hiddenAccounts = null;

    private Connection conn = null;
    private boolean onHidden = false;

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
    }

    /**
     * Refreshes the Account table
     */
    private void refreshTable() {
        map = new HashMap<>();
        masterData.clear();

        String sql;
        if (!onHidden) {
            sql = """
                    select acc_id, name,balance,type from account
                    left join account_type on acc_type = type_id
                    """;
        } else {
            sql = """
                    select a.acc_id, name,balance,type from account as a
                    join hidden_accounts ha on a.acc_id = ha.acc_id
                    left join account_type on acc_type = type_id
                    """;
        }
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                int acc_id = rs.getInt("acc_id");
                if (acc_id == 0 || (!onHidden && hiddenAccounts.contains(acc_id))) continue;
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
     * Exits either add type modal
     *
     * @param event Button press event
     */
    @FXML
    private void cancel(ActionEvent event) {
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.close();
    }

    /**
     * Gets the id of the inputted {@link Account}
     *
     * @param a {@link Account} to get the id of
     * @return database id of the {@link Account} or -1 if it's not found
     */
    private int getId(Account a) {
        for (Integer i : map.keySet()) {
            if (map.get(i).equals(a)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Hides or unhides currently selected account in list
     */
    @FXML
    private void hideAccount() {
        Account selected = accountTable.getSelectionModel().getSelectedItem();
        String sql;
        if (onHidden) {
            sql = "delete from hidden_accounts where acc_id=?";
        } else {
            sql = "insert or ignore into hidden_accounts (acc_id) values (?)";
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
     * Toggles account list between hidden and not hidden accounts
     */
    @FXML
    private void viewHidden() {
        if (!onHidden) {
            onHidden = true;
            addAccount.setVisible(false);
            addAccount.setManaged(false);
            addType.setVisible(false);
            addType.setManaged(false);
            hideAccount.setText("Unhide Selected");
            viewHidden.setText("Reset View");
        } else {
            onHidden = false;
            addAccount.setVisible(true);
            addAccount.setManaged(true);
            addType.setVisible(true);
            addType.setManaged(true);
            hideAccount.setText("Hide Selected");
            viewHidden.setText("View Hidden");
        }

        Main.updateHidden();
        refreshTable();
    }

    /**
     * Setup for editing an account
     */
    @FXML
    private void editSelected() {
        Account selected = accountTable.getSelectionModel().getSelectedItem();
        int id = getId(selected);
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mattb/controllers/add_account.fxml"));
            Parent root = loader.load();

            AddAccountController controller = loader.getController();

            controller.setFields(selected, id);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Edit Transaction");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            refreshTable();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
