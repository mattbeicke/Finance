package mattb.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import mattb.FinanceError;
import mattb.FinanceException;

import java.io.IOException;

public class MainController {
    @FXML
    private BorderPane mainBorderPane;

    /**
     * Sets tab to the dashboard tab
     *
     * @throws FinanceException If there is an error opening the dashboard tab
     */
    public void showDashboard() throws FinanceException {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mattb/controllers/dashboard.fxml"));
            Parent view = loader.load();
            mainBorderPane.setCenter(view);
        } catch (IOException ignored) {
            throw new FinanceException(FinanceError.OPEN_DASHBOARD_TAB_FAILED);
        }
    }

    /**
     * Sets tab to the transactions tab
     *
     * @throws FinanceException If there is an error opening the transactions tab
     */
    public void showTransactions() throws FinanceException {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mattb/controllers/transactions.fxml"));
            Parent view = loader.load();
            mainBorderPane.setCenter(view);
        } catch (IOException ignored) {
            throw new FinanceException(FinanceError.OPEN_TRANSACTIONS_TAB_FAILED);
        }
    }

    /**
     * Sets tab to the accounts tab
     *
     * @throws FinanceException If there is an error opening the accounts tab
     */
    public void showAccounts() throws FinanceException {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mattb/controllers/accounts.fxml"));
            Parent view = loader.load();
            mainBorderPane.setCenter(view);
        } catch (IOException ignored) {
            throw new FinanceException(FinanceError.OPEN_ACCOUNTS_TAB_FAILED);
        }
    }
}
