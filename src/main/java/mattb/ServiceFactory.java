package mattb;

import mattb.dao.AccountDAOImpl;
import mattb.dao.GoalDAOImpl;
import mattb.dao.TransactionDAOImpl;
import mattb.service.*;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.stream.Collectors;

import static mattb.FinanceError.*;

/**
 * Initializes database connection, setting up DAOs and services in the process
 *
 * @author Matthew Beicke
 */
public class ServiceFactory {
    private static Connection conn;

    private static AccountService accountService;
    private static GoalService goalService;
    private static TransactionService transactionService;

    /**
     * Initializes database connection. Sets up service classes and DAO classes.
     * Sets up missing database tables (if there are any).
     * Initializes the tables with the required starting data (the {@code External} account and such)
     */
    public static void init() {
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:finance.db");
        } catch (SQLException ignored) {
            new FinanceException(DATABASE_CONNECTION_FAIL).displayAndLog();
        }

        accountService = new AccountServiceImpl(new AccountDAOImpl(conn));
        goalService = new GoalServiceImpl(new GoalDAOImpl(conn), accountService);
        transactionService = new TransactionServiceImpl(new TransactionDAOImpl(conn), accountService);

        try (Statement stmt = conn.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = ON;");

            InputStream is = Main.class.getResourceAsStream("/mattb/schema.sql");
            if (is == null) {
                new FinanceException(SCHEMA_NOT_FOUND).displayAndLog();
                return;
            }

            String sql = new BufferedReader(new InputStreamReader(is)).lines().collect(Collectors.joining("\n"));

            for (String part : sql.split(";")) {
                if (!part.trim().isEmpty()) {
                    stmt.execute(part);
                }
            }
        } catch (SQLException ignored) {
            new FinanceException(DATABASE_CREATION_FAIL).displayAndLog();
        }
    }

    /**
     * Gets the {@link AccountService}
     *
     * @return The {@link AccountService} created in {@link #init()}
     */
    public static AccountService getAccountService() {
        return accountService;
    }

    /**
     * Gets the {@link GoalService}
     *
     * @return The {@link GoalService} created in {@link #init()}
     */
    public static GoalService getGoalService() {
        return goalService;
    }

    /**
     * Gets the {@link TransactionService}
     *
     * @return The {@link TransactionService} created in {@link #init()}
     */
    public static TransactionService getTransactionService() {
        return transactionService;
    }
}
