package mattb;

import javafx.application.Application;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FinanceApp {
    static void main(String[] args) {
        Application.launch(JavaFXApp.class, args);
    }
}
