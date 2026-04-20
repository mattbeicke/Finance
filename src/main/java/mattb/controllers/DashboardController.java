package mattb.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class DashboardController {
    @FXML
    private Button submitButton; // Name must match fx:id exactly

    @FXML
    public void handleSubmit() {
        System.out.println("Button was clicked!");
    }
}
