package mattb;

import javafx.application.Application;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;

import static mattb.FinanceError.DATABASE_CREATION_FAIL;

@SpringBootApplication
public class FinanceApp {
    static void main(String[] args) {
        Application.launch(JavaFXApp.class, args);
    }

    @Bean
    public CommandLineRunner initDatabase(JdbcTemplate jdbcTemplate) {
        return args -> {
            try {
                System.out.println("Database initialized successfully.");
            } catch (Exception e) {
                new FinanceException(DATABASE_CREATION_FAIL).displayAndLog();
            }
        };
    }
}
