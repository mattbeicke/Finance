package mattb.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import mattb.UIUtilities;
import mattb.service.AccountService;
import org.springframework.stereotype.Component;

/**
 * Handles UI interactions on the {@code Add New Account Type} modal
 *
 * @author Matthew Beicke
 */
@Component
public class AddTypeController {
    @FXML
    private TextField typeField;

    private final AccountService accountService;
    private final UIUtilities uiUtilities;

    private boolean saveClicked = false;

    public AddTypeController(AccountService accountService, UIUtilities uiUtilities) {
        this.accountService = accountService;
        this.uiUtilities = uiUtilities;
    }

    /**
     * Saves a new {@code Account Type} to the database
     *
     * @param event The {@link ActionEvent} from pressing the {@code Save} {@link Button}
     */
    @FXML
    private void onTypeSave(ActionEvent event) {
        if (!accountService.saveAccountType(typeField.getText())) {
            uiUtilities.showNotification(false, "Type field must not be blank");
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
