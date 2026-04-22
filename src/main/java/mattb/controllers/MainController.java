package mattb.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import mattb.FinanceError;
import mattb.FinanceException;

import java.io.IOException;
import java.net.URL;

import static mattb.FinanceError.*;

public class MainController {
    @FXML
    private BorderPane mainBorderPane;

    /**
     * Sets tab to the dashboard tab
     */
    public void showDashboard() {
        loadView("/mattb/controllers/dashboard.fxml", OPEN_DASHBOARD_TAB_FAILED);
    }

    /**
     * Sets tab to the transactions tab
     */
    public void showTransactions() {
        loadView("/mattb/controllers/transactions.fxml", OPEN_TRANSACTIONS_TAB_FAILED);
    }

    /**
     * Sets tab to the accounts tab
     */
    public void showAccounts() {
        loadView("/mattb/controllers/accounts.fxml", OPEN_ACCOUNTS_TAB_FAILED);
    }

    /**
     * Private helper to validate resources before loading to prevent console spam
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
