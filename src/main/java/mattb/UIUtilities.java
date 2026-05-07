package mattb;

import javafx.scene.Scene;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import org.controlsfx.control.Notifications;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.text.NumberFormat;
import java.util.Locale;

import static mattb.FinanceError.OPEN_DARK_THEME_FAIL;

/**
 * This class has some helper methods that are used commonly across the project and needed a central home
 *
 * @author Matthew Beicke
 */
@Component
public class UIUtilities {
    private final ConfigService configService;

    /**
     * Used by Spring Boot to do dependency injection for the methods in {@link UIUtilities this} class
     *
     * @param configService The connection to the {@link ConfigService Preferences Config Class}
     */
    public UIUtilities(ConfigService configService) {
        this.configService = configService;
    }

    /**
     * Converts a double amount to a US currency formatted String (e.g., "$1,234.56") via the {@link NumberFormat#getCurrencyInstance(Locale)} with {@link Locale#US}.
     *
     * @param input The double value to format
     * @return Formatted currency string
     */
    public String formatDouble(double input) {
        return NumberFormat.getCurrencyInstance(Locale.US).format(input);
    }

    /**
     * Sets the {@link TableColumn Columns} {@code Balance} and {@code Amount} columns to use the currency format from {@link #formatDouble(double)}
     *
     * @param toConvert The {@link TableColumn} to convert
     * @param <S>       Lets {@code toConvert} be any from any table as long as the column is of a double type
     */
    public <S> void useCurrency(TableColumn<S, Double> toConvert) {
        toConvert.setCellFactory(_ -> new TableCell<>() {
            @Override
            protected void updateItem(Double balance, boolean empty) {
                super.updateItem(balance, empty);
                if (empty || balance == null) {
                    setText(null);
                } else {
                    setText(formatDouble(balance));
                }
            }
        });
    }

    /**
     * Applies dark mode to given Scene if dark mode is on
     *
     * @param scene {@link Scene} to apply dark mode to
     */
    public void darkMode(Scene scene) {
        URL themes = JavaFXApp.class.getResource("/mattb/dark-theme.css");
        if (themes == null) {
            new FinanceException(OPEN_DARK_THEME_FAIL).displayAndLog();
            return;
        }
        if (configService.getDarkMode()) {
            scene.getStylesheets().add(themes.toExternalForm());
        }
    }

    /**
     * Displays a {@link Notifications Notification} box in the bottom right of the screen depending on if something succeeded or not
     *
     * @param success Whether to set title of the {@link Notifications Notification} to 'Success' or 'Failure'
     * @param message Message to display in the {@link Notifications Notification's} body
     */
    public void showNotification(boolean success, String message) {
        Notifications notif = Notifications.create();

        notif.title(success ? "Success" : "Failure");
        notif.text(message);

        if (configService.getDarkMode()) {
            notif.darkStyle();
        }

        notif.showInformation();
    }
}
