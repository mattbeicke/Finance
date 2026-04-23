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
import mattb.model.Account;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import static mattb.FinanceError.*;

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
    private Button addAccount;
    @FXML
    private Button addType;
    @FXML
    private Button hideAccount;
    @FXML
    private Button viewHidden;

    private final ObservableList<Account> masterData = FXCollections.observableArrayList();
    private HashMap<Integer, Account> map;

    private Connection conn = null;
    private boolean onHidden = false;

    /**
     * Initializes all FXML items for the account tab
     */
    @FXML
    public void initialize() {
        conn = Main.getConn();

        if (colName != null) {
            colName.setCellValueFactory(cellData ->
                    new ReadOnlyObjectWrapper<>(cellData.getValue().name())
            );
            colBalance.setCellValueFactory(cellData ->
                    new ReadOnlyObjectWrapper<>(cellData.getValue().balance())
            );
            colType.setCellValueFactory(cellData ->
                    new ReadOnlyObjectWrapper<>(cellData.getValue().type())
            );

            Main.useCurrency(colBalance);

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
                    select acc_id, name, balance, type from account
                    left join account_type on acc_type = type_id
                    where acc_id not in (select acc_id from hidden_accounts union select 0)
                    """;
        } else {
            sql = """
                    select acc_id, name, balance, type from account
                    left join account_type on acc_type = type_id
                    where acc_id in (select acc_id from hidden_accounts)
                    """;
        }
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                map.put(rs.getInt("acc_id"), new Account(
                        rs.getDouble("balance"),
                        rs.getString("type"),
                        rs.getString("name")
                ));
            }
        } catch (SQLException ignored) {
            new FinanceException(LOAD_ACCOUNTS_FAIL).displayAndLog();
        }

        masterData.addAll(map.values());
    }

    /**
     * Gets the id of the inputted {@link Account}
     *
     * @param a {@link Account} to get the id of
     * @return database id of the {@link Account} or -1 if it's not found
     */
    private int getId(Account a) {
        if (a == null) return -1;

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
        if (selected == null) return;

        int accountId = getId(selected);
        if (accountId == -1) return;

        String sql;
        if (onHidden) {
            sql = "delete from hidden_accounts where acc_id=?";
        } else {
            sql = "insert or ignore into hidden_accounts (acc_id) values (?)";
        }

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, accountId);

            pstmt.executeUpdate();

            refreshTable();
        } catch (SQLException ignored) {
            new FinanceException(UPDATE_HIDDEN_ACCOUNT_LIST_FAIL).displayAndLog();
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

        refreshTable();
    }

    /**
     * Opens create new account modal
     */
    @FXML
    private void addNewAccount() {
        try {
            URL resource = getClass().getResource("/mattb/controllers/add_account.fxml");
            if (resource == null) {
                new FinanceException(OPEN_NEW_ACCOUNT_MODAL_FAIL).displayAndLog();
                return;
            }
            FXMLLoader loader = new FXMLLoader(resource);
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Add New Account");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            refreshTable();
        } catch (IOException ignored) {
            new FinanceException(OPEN_NEW_ACCOUNT_MODAL_FAIL).displayAndLog();
        }
    }

    /**
     * Opens create new account type modal
     */
    @FXML
    private void addNewType() {
        try {
            URL resource = getClass().getResource("/mattb/controllers/add_type.fxml");
            if (resource == null) {
                new FinanceException(OPEN_NEW_ACCOUNT_MODAL_FAIL).displayAndLog();
                return;
            }
            FXMLLoader loader = new FXMLLoader(resource);
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Add New Account Type");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException ignored) {
            new FinanceException(OPEN_NEW_TYPE_MODAL_FAIL).displayAndLog();
        }
    }

    /**
     * Opens edit account modal, filling all fields with current account information
     */
    @FXML
    private void editSelected() {
        Account selected = accountTable.getSelectionModel().getSelectedItem();
        int id = getId(selected);
        if (id == -1) return;

        try {
            URL resource = getClass().getResource("/mattb/controllers/add_account.fxml");
            if (resource == null) {
                new FinanceException(OPEN_NEW_ACCOUNT_MODAL_FAIL).displayAndLog();
                return;
            }
            FXMLLoader loader = new FXMLLoader(resource);
            Parent root = loader.load();

            AddAccountController controller = loader.getController();

            controller.setFields(selected, id);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Edit Transaction");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            refreshTable();
        } catch (IOException ignored) {
            new FinanceException(OPEN_EDIT_ACCOUNT_MODAL_FAIL).displayAndLog();
        }
    }
}
