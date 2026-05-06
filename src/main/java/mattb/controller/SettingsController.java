package mattb.controller;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import mattb.ConfigService;
import mattb.FinanceException;
import mattb.UIUtilities;
import org.springframework.stereotype.Component;

import java.net.URL;

import static mattb.FinanceError.OPEN_DARK_THEME_FAIL;

/**
 * Handles UI interactions on the {@code Settings} tab
 *
 * @author Matthew Beicke
 */
@Component
public class SettingsController {
    @FXML
    private TextField numTransactions;
    @FXML
    private TextField numAccounts;
    @FXML
    private CheckBox darkMode;

    private final ConfigService configService;
    private final UIUtilities uiUtilities;

    public SettingsController(ConfigService configService, UIUtilities uiUtilities) {
        this.configService = configService;
        this.uiUtilities = uiUtilities;
    }

    /**
     * Initializes {@link FXML} items for the {@code Settings} tab
     */
    @FXML
    private void initialize() {
        numTransactions.setText(String.valueOf(configService.getNumTransactions()));
        numAccounts.setText(String.valueOf(configService.getNumAccounts()));
        darkMode.setSelected(configService.getDarkMode());

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
     * Saves all settings via the {@link ConfigService} class
     */
    @FXML
    private void save() {
        if (numTransactions.getText().isBlank() || numAccounts.getText().isBlank()) {
            uiUtilities.showNotification(false, "Please fill all settings before saving");
            return;
        }

        configService.save(Integer.parseInt(numTransactions.getText()), Integer.parseInt(numAccounts.getText()), darkMode.isSelected());

        uiUtilities.showNotification(true, "Settings saved");

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
