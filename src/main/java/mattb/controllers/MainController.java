package mattb.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

public class MainController {
    @FXML
    private BorderPane mainBorderPane;

    public void showDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mattb/controllers/dashboard.fxml"));
            Parent view = loader.load();
            mainBorderPane.setCenter(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showTransactions() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mattb/controllers/transactions.fxml"));
            Parent view = loader.load();
            mainBorderPane.setCenter(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showAccounts() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mattb/controllers/accounts.fxml"));
            Parent view = loader.load();
            mainBorderPane.setCenter(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
