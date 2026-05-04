package mattb.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import mattb.Main;
import mattb.dao.AccountDAO;
import mattb.dao.AccountDAOImpl;

/**
 * Handles UI interactions on the {@code Add New Account Type} modal
 *
 * @author Matthew Beicke
 */
public class AddTypeController {
    @FXML
    private TextField typeField;

    private AccountDAO accountDAO;

    /**
     * Initializes {@link FXML} items for the {@code Add New Account Type} modal and the {@link AccountDAO DAO}
     */
    @FXML
    public void initialize() {
        accountDAO = new AccountDAOImpl(Main.getConn());
    }

    /**
     * Saves a new {@code Account Type} to the database
     *
     * @param event The {@link ActionEvent} from pressing the {@code Save} {@link Button}
     */
    @FXML
    private void onTypeSave(ActionEvent event) {
        if (typeField.getText().isBlank()) return;

        accountDAO.saveAccountType(typeField.getText());

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
