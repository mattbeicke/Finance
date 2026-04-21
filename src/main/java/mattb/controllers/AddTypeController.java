package mattb.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import mattb.FinanceException;
import mattb.Main;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static mattb.FinanceError.SAVE_ACCOUNT_TYPE_FAIL;

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
     * Saves a new account type to the database
     */
    @FXML
    private void onTypeSave() {
        if (typeField.getText().isBlank()) return;

        String sql = "insert or ignore into account_type(type) values (?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, typeField.getText());

            pstmt.executeUpdate();

            ((Stage) typeField.getScene().getWindow()).close();
        } catch (SQLException ignored) {
            throw new FinanceException(SAVE_ACCOUNT_TYPE_FAIL);
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
