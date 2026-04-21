package mattb.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import mattb.FinanceException;

import java.io.IOException;

import static mattb.FinanceError.*;

public class MainController {
    @FXML
    private BorderPane mainBorderPane;

    /**
     * Sets tab to the dashboard tab
     */
    public void showDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mattb/controllers/dashboard.fxml"));
            Parent view = loader.load();
            mainBorderPane.setCenter(view);
        } catch (IOException ignored) {
            throw new FinanceException(OPEN_DASHBOARD_TAB_FAILED);
        }
    }

    /**
     * Sets tab to the transactions tab
     */
    public void showTransactions() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mattb/controllers/transactions.fxml"));
            Parent view = loader.load();
            mainBorderPane.setCenter(view);
        } catch (IOException ignored) {
            throw new FinanceException(OPEN_TRANSACTIONS_TAB_FAILED);
        }
    }

    /**
     * Sets tab to the accounts tab
     */
    public void showAccounts() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mattb/controllers/accounts.fxml"));
            Parent view = loader.load();
            mainBorderPane.setCenter(view);
        } catch (IOException ignored) {
            throw new FinanceException(OPEN_ACCOUNTS_TAB_FAILED);
        }
    }
}
