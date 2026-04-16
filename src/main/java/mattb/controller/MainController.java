package mattb.controller;

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
            Parent view = FXMLLoader.load(getClass().getResource("/dashboard.fxml"));
            mainBorderPane.setCenter(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showTransactions() {
        try {
            Parent view = FXMLLoader.load(getClass().getResource("/transactions.fxml"));
            mainBorderPane.setCenter(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showAccounts() {
        try {
            Parent view = FXMLLoader.load(getClass().getResource("/accounts.fxml"));
            mainBorderPane.setCenter(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
