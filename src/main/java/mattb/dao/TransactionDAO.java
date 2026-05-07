package mattb.dao;

import javafx.collections.ObservableList;
import mattb.model.Account;
import mattb.model.Transaction;

import java.time.LocalDate;
import java.util.Date;

/**
 * DAO Interface for {@link Transaction Transactions}
 *
 * @author Matthew Beicke
 */
public interface TransactionDAO {
    /**
     * Saves a {@link Transaction} to the database
     *
     * @param date      Date {@link Transaction} occurred on
     * @param fromAccId Database id of the {@link Account} money came from
     * @param toAccId   Database id of the {@link Account} money went to
     * @param amount    Amount of money transferred
     * @param memo      Memo associated with {@link Transaction}
     * @return The database id of the {@link Transaction} Added
     */
    int insertTransaction(LocalDate date, int fromAccId, int toAccId, double amount, String memo);

    /**
     * Updates a {@link Transaction} that is in the database
     *
     * @param date      Date {@link Transaction} occurred on
     * @param fromAccId Database id of the {@link Account} money came from
     * @param toAccId   Database id of the {@link Account} money went to
     * @param amount    Amount of money transferred
     * @param memo      Memo associated with {@link Transaction}
     * @param id        Database id of {@link Transaction} (only used if {@code editing})
     */
    void updateTransaction(LocalDate date, int fromAccId, int toAccId, double amount, String memo, int id);

    /**
     * Changes {@link Transaction} with given {@code transactionId} to the status of {@code hidden}
     *
     * @param transactionId Database id of {@link Transaction} to update
     * @param hidden        Whether to set {@link Transaction} to hidden (if true) or non-hidden (if false)
     */
    void updateTransactionVisibility(int transactionId, boolean hidden);

    /**
     * Populates the {@code tcat} table for the {@link Transaction}
     *
     * @param t_id   Database id of the {@link Transaction}
     * @param cat_id Database id of the {@code Category}
     */
    void linkTransactionCategory(int t_id, int cat_id);

    /**
     * Gets the database id of the {@code Category}, creating it if necessary
     *
     * @param cat Category to find or create
     * @return Database id of {@code cat}
     */
    int findOrCreateCategory(String cat);

    /**
     * Clears all of a {@link Transaction Transactions} categories
     *
     * @param t_id Database id of the {@link Transaction} to clear
     */
    void clearCategoriesForTransaction(int t_id);

    /**
     * Gets list of all non-hidden {@link Transaction transactions} (if {@code hidden} is false) or
     * gets list of all hidden {@link Transaction transactions} (if {@code hidden} is true)
     *
     * @param hidden  Whether to get the hidden or non-hidden {@link Transaction transactions}
     * @param perPage Number of {@link Transaction transactions} to get
     * @param page    Offset of {@link Transaction transactions} request
     * @return A list of all requested {@link Transaction Transactions}
     */
    ObservableList<Transaction> getAllTransactions(boolean hidden, int perPage, int page);

    /**
     * Gets a {@link Transaction Transactions} database id
     *
     * @param date      Date {@link Transaction} occurred on
     * @param fromAccId Database id of the {@link Account} money came from
     * @param toAccId   Database id of the {@link Account} money went to
     * @param amount    Amount of money transferred
     * @param memo      Memo associated with {@link Transaction}
     * @return Database id of the {@link Transaction} or -1 if it cannot be found
     */
    int getTID(Date date, int fromAccId, int toAccId, double amount, String memo);

    /**
     * Gets the number of {@link Transaction transactions} in the database
     *
     * @param hidden Whether to get the hidden or non-hidden {@link Transaction transactions}
     * @return Number of hidden or non-hidden {@link Transaction transactions} in the database or -1 if none found
     */
    int getTransactionCount(boolean hidden);
}
