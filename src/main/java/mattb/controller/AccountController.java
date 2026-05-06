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
import mattb.ConfigService;
import mattb.FinanceException;
import mattb.UIUtilities;
import mattb.model.Account;
import mattb.service.AccountService;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

import static mattb.FinanceError.*;

/**
 * Handles UI interactions on the {@code Account} tab
 *
 * @author Matthew Beicke
 */
@Component
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

    private final ApplicationContext context;
    private final AccountService accountService;
    private final ConfigService configService;
    private final UIUtilities uiUtilities;

    private final ObservableList<Account> masterData = FXCollections.observableArrayList();
    private HashMap<Integer, Account> map;

    private boolean onHidden = false;
    private int page;
    private int perPage;

    /**
     * Used by Spring Boot to do dependency injection for the below items
     *
     * @param context        Used to create modals properly
     * @param accountService The connection to the {@link AccountService Account Service}
     * @param configService  The connection to the {@link ConfigService Preferences Config Class}
     * @param uiUtilities    The connection to the {@link UIUtilities Utlities Class}
     */
    public AccountController(ApplicationContext context, AccountService accountService, ConfigService configService, UIUtilities uiUtilities) {
        this.context = context;
        this.accountService = accountService;
        this.configService = configService;
        this.uiUtilities = uiUtilities;
    }

    /**
     * Initializes {@link FXML} items for the {@code Account} tab
     */
    @FXML
    public void initialize() {
        colName.setCellValueFactory(cellData ->
                new ReadOnlyObjectWrapper<>(cellData.getValue().name())
        );
        colBalance.setCellValueFactory(cellData ->
                new ReadOnlyObjectWrapper<>(cellData.getValue().balance())
        );
        colType.setCellValueFactory(cellData ->
                new ReadOnlyObjectWrapper<>(cellData.getValue().type())
        );

        uiUtilities.useCurrency(colBalance);

        accountTable.setItems(masterData);

        perPage = configService.getNumAccounts();
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
                uiUtilities.showNotification(true, "Account no longer hidden");
            } else {
                uiUtilities.showNotification(true, "Account hidden");
            }
            updatePageInfo();
        } else {
            uiUtilities.showNotification(false, "No account selected");
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
        loadModal("Add New Account", null, -1);
    }

    /**
     * Opens the {@code Edit Account} modal, filling all fields with the currently selected {@link Account Account's} information
     */
    @FXML
    private void editSelected() {
        Account selected = accountTable.getSelectionModel().getSelectedItem();
        int id = accountService.getAccountIdFromMap(selected, map);
        if (id == -1) {
            uiUtilities.showNotification(false, "No account selected");
            return;
        }

        loadModal("Edit Account", selected, id);
    }

    /**
     * Opens one of the {@link AddAccountController AddAccountController's} modals
     *
     * @param title  Title of the {@code modal}
     * @param toEdit {@link Account} that will be edited (if this is the {@code Edit Account} modal
     * @param id     Database id of the {@link Account} that will be edited or -1 if not editing
     */
    private void loadModal(String title, Account toEdit, int id) {
        try {
            URL resource = getClass().getResource("/mattb/controller/add_account.fxml");
            FXMLLoader loader = new FXMLLoader(resource);

            loader.setControllerFactory(context::getBean);

            Parent root = loader.load();
            AddAccountController controller = loader.getController();

            if (toEdit != null) {
                controller.setFields(toEdit, id);
            }

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle(title);
            Scene scene = new Scene(root);
            uiUtilities.darkMode(scene);
            stage.setScene(scene);
            stage.showAndWait();

            if (controller.isSaveClicked()) {
                uiUtilities.showNotification(true, toEdit == null ? "Account Created" : "Account Updated");
                updatePageInfo();
            }
        } catch (IOException e) {
            new FinanceException(toEdit == null ? OPEN_NEW_ACCOUNT_MODAL_FAIL : OPEN_EDIT_ACCOUNT_MODAL_FAIL).displayAndLog();
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

            loader.setControllerFactory(context::getBean);

            Parent root = loader.load();

            AddTypeController controller = loader.getController();

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Add New Account Type");
            Scene scene = new Scene(root);
            uiUtilities.darkMode(scene);
            stage.setScene(scene);
            stage.showAndWait();

            if (controller.isSaveClicked()) {
                uiUtilities.showNotification(true, "Account Type Created");
            }
        } catch (IOException ignored) {
            new FinanceException(OPEN_NEW_TYPE_MODAL_FAIL).displayAndLog();
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
