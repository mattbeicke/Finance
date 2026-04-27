package mattb.controllers;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import mattb.Main;
import mattb.dao.AddAccountDAO;
import mattb.dao.AddAccountDAOImpl;
import mattb.model.Account;

public class AddAccountController {
    @FXML
    private ComboBox<String> typeCombo;
    @FXML
    private TextField nameField;
    @FXML
    private TextField balanceField;

    private AddAccountDAO addAccountDAO;

    private boolean editing;
    private int id;

    /**
     * Initializes all FXML items for the add account modal
     */
    @FXML
    public void initialize() {
        addAccountDAO = new AddAccountDAOImpl(Main.getConn());

        ObservableList<String> types = addAccountDAO.getAllTypes();
        typeCombo.setItems(types);

        balanceField.textProperty().addListener((_, oldVal, newVal) -> {
            if (!newVal.matches("\\d*(\\.\\d*)?")) {
                balanceField.setText(oldVal);
            }
        });
    }

    /**
     * Saves new account to database
     */
    @FXML
    private void onAccountSave() {
        int typeId = getTypeId();
        if (typeId == -1 || balanceField.getText().isBlank() || nameField.getText().isBlank()) return;

        addAccountDAO.saveAccount(typeId, Double.parseDouble(balanceField.getText()), nameField.getText(), id, editing);

        ((Stage) typeCombo.getScene().getWindow()).close();
    }

    /**
     * Gets the id number of the currently selected type
     *
     * @return id number that corresponds to the type selected in the type combo box or -1 if it cannot be found
     */
    public int getTypeId() {
        if (typeCombo.getValue().isBlank()) return -1;

        return addAccountDAO.getTypeId(typeCombo.getValue());
    }

    /**
     * Exits either add/edit account modal
     *
     * @param event Button press event
     */
    @FXML
    private void cancel(ActionEvent event) {
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.close();
    }

    /**
     * Sets fields of account edit modal
     *
     * @param a  {@link Account} who is being edited
     * @param id Database id of {@link Account} who is being edited
     */
    public void setFields(Account a, int id) {
        if (a == null || id <= 0) return;

        editing = true;
        this.id = id;

        nameField.setText(a.name());
        typeCombo.setValue(a.type());
        balanceField.setText(String.valueOf(a.balance()));
    }
}
