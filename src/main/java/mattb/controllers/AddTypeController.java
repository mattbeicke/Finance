package mattb.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import mattb.Main;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class AddTypeController {
    @FXML
    private TextField typeField;

    private Connection conn = null;

    /**
     * Initializes all FXML items for the add type modal
     */
    @FXML
    public void initialize() {
        conn = Main.getConn();
    }

    /**
     * Saves new type
     */
    @FXML
    private void onTypeSave() {
        if (typeField.getText().isBlank()) return;

        String sql = "insert or ignore into account_type(type) values (?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, typeField.getText());

            pstmt.executeUpdate();

            ((Stage) typeField.getScene().getWindow()).close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Exits add type modal
     *
     * @param event Button press event
     */
    @FXML
    private void cancel(ActionEvent event) {
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.close();
    }
}
