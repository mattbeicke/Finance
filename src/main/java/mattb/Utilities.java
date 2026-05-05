package mattb;

import javafx.scene.Scene;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import org.controlsfx.control.Notifications;

import java.net.URL;
import java.text.NumberFormat;
import java.util.Locale;

import static mattb.FinanceError.OPEN_DARK_THEME_FAIL;

public class Utilities {
    /**
     * Converts a {@link Double} (that reflects a balance or amount) to a formatted {@link String}.
     * This includes a dollar sign and two decimal places and a point (of zeros if it is the case).
     *
     * @param input String to convert
     * @return Converted string
     */
    public static String formatDouble(double input) {
        return NumberFormat.getCurrencyInstance(Locale.US).format(input);
    }

    /**
     * Sets the {@link TableColumn Columns} {@code Balance} and {@code Amount} columns to use the currency format from {@link #formatDouble(double)}
     *
     * @param toConvert The {@link TableColumn} to convert
     * @param <S>       Lets {@code toConvert} be any from any table as long as the column is of a double type
     */
    public static <S> void useCurrency(TableColumn<S, Double> toConvert) {
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
     * Applies dark mode to given Scene
     *
     * @param scene Scene to apply dark mode to (if it is on)
     */
    public static void darkMode(Scene scene) {
        URL themes = Main.class.getResource("/mattb/dark-theme.css");
        if (themes == null) {
            new FinanceException(OPEN_DARK_THEME_FAIL).displayAndLog();
            return;
        }
        if (Config.getDarkMode()) {
            scene.getStylesheets().add(themes.toExternalForm());
        }
    }

    /**
     * Displays a {@link Notifications Notification} depending on if something succeeded or not
     *
     * @param success Whether to set title of the {@link Notifications Notification} to 'Success' or 'Failure'
     * @param message Message to display in {@link Notifications Notification} body
     */
    public static void showNotification(boolean success, String message) {
        Notifications notif = Notifications.create();

        notif.title(success ? "Success" : "Failure");
        notif.text(message);

        if (Config.getDarkMode()) {
            notif.darkStyle();
        }

        notif.showInformation();
    }
}
