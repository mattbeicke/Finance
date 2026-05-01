package mattb.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import mattb.FinanceError;
import mattb.FinanceException;

import java.io.IOException;
import java.net.URL;

import static mattb.FinanceError.*;

/**
 * Handles UI interactions for tab switching
 *
 * @author Matthew Beicke
 */
public class MainController {
    @FXML
    private BorderPane mainBorderPane;

    /**
     * Sets current tab to {@code Dashboard}
     */
    public void showDashboard() {
        loadView("/mattb/controller/dashboard.fxml", OPEN_DASHBOARD_TAB_FAILED);
    }

    /**
     * Sets current tab to {@code Transactions}
     */
    public void showTransactions() {
        loadView("/mattb/controller/transactions.fxml", OPEN_TRANSACTIONS_TAB_FAILED);
    }

    /**
     * Sets current tab to {@code Accounts}
     */
    public void showAccounts() {
        loadView("/mattb/controller/accounts.fxml", OPEN_ACCOUNTS_TAB_FAILED);
    }

    /**
     * Sets current tab to {@code Settings}
     */
    public void showSettings() {
        loadView("/mattb/controller/settings.fxml", OPEN_SETTINGS_TAB_FAILED);
    }

    /**
     * Reduces redundant code in this class by doing the tab loading here
     */
    private void loadView(String fxmlPath, FinanceError errorContext) {
        URL resource = getClass().getResource(fxmlPath);
        if (resource == null) {
            new FinanceException(errorContext).displayAndLog();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(resource);
            Parent view = loader.load();
            mainBorderPane.setCenter(view);
        } catch (IOException e) {
            new FinanceException(errorContext).displayAndLog();
        }
    }
}
