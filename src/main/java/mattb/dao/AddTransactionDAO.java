package mattb.dao;

import javafx.collections.ObservableList;
import mattb.model.Account;
import mattb.model.Transaction;

import java.time.LocalDate;

/**
 * DAO Interface for the AddTransactionController
 *
 * @author Matthew Beicke
 */
public interface AddTransactionDAO {
    /**
     * Gets list of all non-hidden {@link Account Accounts}
     *
     * @return List of non-hidden {@link Account Accounts}
     */
    ObservableList<String> loadAccountNames();

    /**
     * Gets an {@link Account} id from its {@code name}
     *
     * @param accName {@link Account} name
     * @return Database id associated with the {@link Account} name or -1 if no account was found
     */
    int getAccId(String accName);

    /**
     * Saves an {@link Transaction} to the database or updates one that is there already
     *
     * @param date      Date {@link Transaction} occurred on
     * @param fromAccId Database id of the {@link Account} money came from
     * @param toAccId   Database id of the {@link Account} money went to
     * @param amount    Amount of money transferred
     * @param memo      Memo associated with {@link Transaction}
     * @param id        Database id of {@link Transaction} (only used if {@code editing})
     * @param editing   Whether we are editing ({@code true}) or saving new ({@code false})
     */
    void saveTransaction(LocalDate date, int fromAccId, int toAccId, double amount, String memo, int id, boolean editing);

    /**
     * Populates the {@code tcat} table for the {@link Transaction}
     *
     * @param input User input into the Add Transaction Modal's "Category" field
     */
    void saveCategories(String input);

    /**
     * Updates {@link Account} balances (if they are {@code External})
     *
     * @param amount    Amount of money transferred
     * @param fromAccId Database id of the {@link Account} money came from
     * @param toAccId   Database id of the {@link Account} money went to
     */
    void updateBalances(double amount, int fromAccId, int toAccId);
}
