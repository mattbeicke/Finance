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
import mattb.ServiceFactory;
import mattb.Utilities;
import mattb.model.Account;
import mattb.service.AccountService;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

import static mattb.FinanceError.*;

/**
 * Handles UI interactions on the {@code Account} tab
 *
 * @author Matthew Beicke
 */
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
    @FXML
    private Button left;
    @FXML
    private Button right;
    @FXML
    private Label pageLabel;

    private final ObservableList<Account> masterData = FXCollections.observableArrayList();
    private HashMap<Integer, Account> map;

    private AccountService accountService;

    private boolean onHidden = false;
    private int page;
    private int perPage;

    /**
     * Initializes {@link FXML} items for the {@code Account} tab and the {@link AccountService}
     */
    @FXML
    public void initialize() {
        accountService = ServiceFactory.getAccountService();

        colName.setCellValueFactory(cellData ->
                new ReadOnlyObjectWrapper<>(cellData.getValue().name())
        );
        colBalance.setCellValueFactory(cellData ->
                new ReadOnlyObjectWrapper<>(cellData.getValue().balance())
        );
        colType.setCellValueFactory(cellData ->
                new ReadOnlyObjectWrapper<>(cellData.getValue().type())
        );

        Utilities.useCurrency(colBalance);

        accountTable.setItems(masterData);

        perPage = Config.getNumAccounts();
        page = 1;
        updatePageInfo();
    }

    /**
     * Refreshes the {@link Account} {@link TableView Table} with either hidden or non-hidden {@link Account Accounts}
     */
    private void refreshTable() {
        map = (HashMap<Integer, Account>) accountService.getPagedAccounts(onHidden, perPage, page);
        masterData.setAll(map.values());
    }

    /**
     * Hides or unhides currently selected {@link Account} in the {@link TableView Table}
     */
    @FXML
    private void hideAccount() {
        Account selected = accountTable.getSelectionModel().getSelectedItem();

        if (accountService.toggleVisibility(selected, map, onHidden)) {
            if (onHidden) {
                Utilities.showNotification(true, "Account no longer hidden");
            } else {
                Utilities.showNotification(true, "Account hidden");
            }
            updatePageInfo();
        } else {
            Utilities.showNotification(false, "No account selected");
        }
    }

    /**
     * Toggles {@link Account} list between showing the hidden and non-hidden {@link Account Accounts}
     */
    @FXML
    private void viewHidden() {
        if (!onHidden) {
            onHidden = true;
            addAccount.setDisable(true);
            addType.setDisable(true);
            hideAccount.setText("Unhide Selected");
            viewHidden.setText("Reset View");
        } else {
            onHidden = false;
            addAccount.setDisable(false);
            addType.setDisable(false);
            hideAccount.setText("Hide Selected");
            viewHidden.setText("View Hidden");
        }

        page = 1;
        updatePageInfo();
    }

    /**
     * Opens the {@code Add New Account} modal
     */
    @FXML
    private void addNewAccount() {
        try {
            URL resource = getClass().getResource("/mattb/controller/add_account.fxml");
            if (resource == null) {
                new FinanceException(OPEN_NEW_ACCOUNT_MODAL_FAIL).displayAndLog();
                return;
            }
            FXMLLoader loader = new FXMLLoader(resource);
            Parent root = loader.load();

            AddAccountController controller = loader.getController();

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Add New Account");
            Scene scene = new Scene(root);
            Utilities.darkMode(scene);
            stage.setScene(scene);
            stage.showAndWait();

            if (controller.isSaveClicked()) {
                Utilities.showNotification(true, "Account Created");
            }

            updatePageInfo();
        } catch (IOException ignored) {
            new FinanceException(OPEN_NEW_ACCOUNT_MODAL_FAIL).displayAndLog();
        }
    }

    /**
     * Opens the {@code Add New Account Type} modal
     */
    @FXML
    private void addNewType() {
        try {
            URL resource = getClass().getResource("/mattb/controller/add_type.fxml");
            if (resource == null) {
                new FinanceException(OPEN_NEW_ACCOUNT_MODAL_FAIL).displayAndLog();
                return;
            }
            FXMLLoader loader = new FXMLLoader(resource);
            Parent root = loader.load();

            AddTypeController controller = loader.getController();

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Add New Account Type");
            Scene scene = new Scene(root);
            Utilities.darkMode(scene);
            stage.setScene(scene);
            stage.showAndWait();

            if (controller.isSaveClicked()) {
                Utilities.showNotification(true, "Account Type Created");
            }
        } catch (IOException ignored) {
            new FinanceException(OPEN_NEW_TYPE_MODAL_FAIL).displayAndLog();
        }
    }

    /**
     * Opens the {@code Edit Account} modal, filling all fields with the currently selected {@link Account Account's} information
     */
    @FXML
    private void editSelected() {
        Account selected = accountTable.getSelectionModel().getSelectedItem();
        int id = accountService.getAccountIdFromMap(selected, map);
        if (id == -1) {
            Utilities.showNotification(false, "No account selected");
            return;
        }

        try {
            URL resource = getClass().getResource("/mattb/controller/add_account.fxml");
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
            Scene scene = new Scene(root);
            Utilities.darkMode(scene);
            stage.setScene(scene);
            stage.showAndWait();

            if (controller.isSaveClicked()) {
                Utilities.showNotification(true, "Account Updated");
            }

            refreshTable();
        } catch (IOException ignored) {
            new FinanceException(OPEN_EDIT_ACCOUNT_MODAL_FAIL).displayAndLog();
        }
    }

    /**
     * Sets page to previous
     */
    @FXML
    private void leftPage() {
        page--;
        updatePageInfo();
    }

    /**
     * Sets page to next
     */
    @FXML
    private void rightPage() {
        page++;
        updatePageInfo();
    }

    /**
     * Refreshes the page information then refreshes the table
     */
    private void updatePageInfo() {
        int maxPage = accountService.getMaxPage(onHidden, perPage);

        if (page > maxPage) page = maxPage;

        pageLabel.setText("Page " + page + " of " + maxPage);
        left.setDisable(page <= 1);
        right.setDisable(page >= maxPage);
        refreshTable();
    }
}
