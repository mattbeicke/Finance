package mattb.controller;

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
     * Initializes {@link FXML} items for the {@code Add New Account Type} modal and the {@link AddTypeDAO DAO}
     */
    @FXML
    public void initialize() {
        addTypeDAO = new AddTypeDAOImpl(Main.getConn());
    }

    /**
     * Saves a new {@code Account Type} to the database
     *
     * @param event The {@link ActionEvent} from pressing the {@code Save} {@link Button}
     */
    @FXML
    private void onTypeSave(ActionEvent event) {
        if (typeField.getText().isBlank()) return;

        addTypeDAO.saveType(typeField.getText());

        cancel(event);
    }

    /**
     * Exits the {@code Add New Account Type} modal
     *
     * @param event The {@link ActionEvent} from pressing the {@code Cancel} {@link Button}
     */
    @FXML
    private void cancel(ActionEvent event) {
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.close();
    }
}
