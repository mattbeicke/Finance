package mattb.controller;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import mattb.Config;

/**
 * Handles UI interactions on the {@code Settings} tab
 *
 * @author Matthew Beicke
 */
public class SettingsController {
    @FXML
    private TextField numTransactions;
    @FXML
    private TextField numAccounts;
    @FXML
    private CheckBox darkMode;

    @FXML
    private void initialize() {
        numTransactions.setText(String.valueOf(Config.getNumTransactions()));
        numAccounts.setText(String.valueOf(Config.getNumAccounts()));
        darkMode.setSelected(Config.getDarkMode());

        numTransactions.textProperty().addListener((_, oldVal, newVal) -> {
            if (!newVal.matches("^[1-9]\\d*$")) {
                numTransactions.setText(oldVal);
            }
        });

        numAccounts.textProperty().addListener((_, oldVal, newVal) -> {
            if (!newVal.matches("^[1-9]\\d*$")) {
                numAccounts.setText(oldVal);
            }
        });
    }

    @FXML
    private void save() {
        if (numTransactions.getText().isBlank() || numAccounts.getText().isBlank()) return;

        Config.save(Integer.parseInt(numTransactions.getText()), Integer.parseInt(numAccounts.getText()), darkMode.isSelected());
    }
}
