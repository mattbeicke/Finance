package mattb;

import javafx.application.Application;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entrypoint into the program using {@code Spring Boot}. Launches the GUI found in {@link JavaFXApp}
 *
 * @author Matthew Beicke
 */
@SpringBootApplication
public class FinanceApp {
    static void main(String[] args) {
        Application.launch(JavaFXApp.class, args);
    }
}
