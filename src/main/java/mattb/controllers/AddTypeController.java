package mattb.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import mattb.Main;
import mattb.dao.AddTypeDAO;
import mattb.dao.AddTypeDAOImpl;

public class AddTypeController {
    @FXML
    private TextField typeField;

    private AddTypeDAO addTypeDAO;

    /**
     * Initializes all FXML items for the add type modal
     */
    @FXML
    public void initialize() {
        addTypeDAO = new AddTypeDAOImpl(Main.getConn());
    }

    /**
     * Saves a new account type to the database
     */
    @FXML
    private void onTypeSave() {
        if (typeField.getText().isBlank()) return;

        addTypeDAO.saveType(typeField.getText());

        ((Stage) typeField.getScene().getWindow()).close();
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
