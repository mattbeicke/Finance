package mattb.controller;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import mattb.Config;
import mattb.FinanceException;
import mattb.Utilities;

import java.net.URL;

import static mattb.FinanceError.OPEN_DARK_THEME_FAIL;

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

    /**
     * Initializes {@link FXML} items for the {@code Settings} tab
     */
    @FXML
    private void initialize() {
        numTransactions.setText(String.valueOf(Config.getNumTransactions()));
        numAccounts.setText(String.valueOf(Config.getNumAccounts()));
        darkMode.setSelected(Config.getDarkMode());

        numTransactions.textProperty().addListener((_, oldVal, newVal) -> {
            if (!newVal.matches("^(\\s*[1-9]\\d*)?$")) {
                numTransactions.setText(oldVal);
            }
        });

        numAccounts.textProperty().addListener((_, oldVal, newVal) -> {
            if (!newVal.matches("^(\\s*[1-9]\\d*)?$")) {
                numAccounts.setText(oldVal);
            }
        });
    }

    /**
     * Saves all settings via the {@link Config} class
     */
    @FXML
    private void save() {
        if (numTransactions.getText().isBlank() || numAccounts.getText().isBlank()) {
            Utilities.showNotification(false, "Please fill all settings before saving");
            return;
        }

        Config.save(Integer.parseInt(numTransactions.getText()), Integer.parseInt(numAccounts.getText()), darkMode.isSelected());

        Utilities.showNotification(true, "Settings saved");

        Scene scene = darkMode.getScene();

        URL themes = getClass().getResource("/mattb/dark-theme.css");
        if (themes == null) {
            new FinanceException(OPEN_DARK_THEME_FAIL).displayAndLog();
            return;
        }
        String darkCss = themes.toExternalForm();

        if (darkMode.isSelected()) {
            if (!scene.getStylesheets().contains(darkCss)) {
                scene.getStylesheets().add(darkCss);
            }
        } else {
            scene.getStylesheets().remove(darkCss);
        }
    }
}
