package mattb.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import mattb.ServiceFactory;
import mattb.Utilities;
import mattb.service.AccountService;

/**
 * Handles UI interactions on the {@code Add New Account Type} modal
 *
 * @author Matthew Beicke
 */
public class AddTypeController {
    @FXML
    private TextField typeField;

    private AccountService accountService;

    private boolean saveClicked = false;

    /**
     * Initializes {@link FXML} items for the {@code Add New Account Type} modal and the {@link AccountService}
     */
    @FXML
    public void initialize() {
        accountService = ServiceFactory.getAccountService();
    }

    /**
     * Saves a new {@code Account Type} to the database
     *
     * @param event The {@link ActionEvent} from pressing the {@code Save} {@link Button}
     */
    @FXML
    private void onTypeSave(ActionEvent event) {
        if (!accountService.saveAccountType(typeField.getText())) {
            Utilities.showNotification(false, "Type field must not be blank");
            return;
        }

        saveClicked = true;
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

    /**
     * Gets the status on if the save button was pressed or cancel button was pressed
     *
     * @return {@code true} if the save button was pressed, {@code false} if not
     */
    public boolean isSaveClicked() {
        return saveClicked;
    }
}
