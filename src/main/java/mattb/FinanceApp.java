package mattb;

import javafx.application.Application;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;

import static mattb.FinanceError.DATABASE_CREATION_FAIL;

/**
 * Entrypoint into the program. Launches the GUI found in {@link JavaFXApp}
 *
 * @author Matthew Beicke
 */
@SpringBootApplication
public class FinanceApp {
    static void main(String[] args) {
        Application.launch(JavaFXApp.class, args);
    }

    /**
     * Sets up database with all required tables and fields
     *
     * @param jdbcTemplate Tells the program where to look for the database connection
     * @return A CommandLineRunner bean that executes the database initialization logic (found in {@code schema.sql}) on startup
     */
    @Bean
    public CommandLineRunner initDatabase(JdbcTemplate jdbcTemplate) {
        return _ -> {
            try {
                System.out.println("Database initialized successfully.");
            } catch (Exception e) {
                new FinanceException(DATABASE_CREATION_FAIL).displayAndLog();
            }
        };
    }
}
