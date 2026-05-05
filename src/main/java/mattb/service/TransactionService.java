package mattb.service;

import mattb.model.Account;
import mattb.model.Transaction;
import mattb.model.TransactionResponse;

import java.time.LocalDate;
import java.util.Map;

/**
 * Service Interface for {@link Transaction Transactions}
 *
 * @author Matthew Beicke
 */
public interface TransactionService {
    /**
     * Saves an {@link Transaction} to the database or updates one that is there already
     *
     * @param date     {@link Transaction} date
     * @param fromAcc  Name of the {@link Account} where money came from
     * @param toAcc    Name of the {@link Account} id where money went
     * @param amount   Amount of money transferred
     * @param category Categories of the transaction (comma separated)
     * @param memo     Memo of the transaction
     * @param id       Database id of the transaction (if editing)
     * @param editing  {@code true} if this request will update a transaction, {@code false} if saving new
     * @return A {@link TransactionResponse} object containing data based on if the {@link Transaction} processed correctly or not
     */
    TransactionResponse processTransaction(LocalDate date, String fromAcc, String toAcc, String amount, String category, String memo, int id, boolean editing);

    /**
     * Updates {@link Account} balances
     *
     * @param fromAcc {@link Account} to remove money from
     * @param toAcc   {@link Account} to add money to
     * @param amount  Amount of money to add/remove
     * @return {@code false} if something went wrong, {@code true} if not
     */
    boolean updateBalances(String fromAcc, String toAcc, String amount);

    /**
     * Changes a {@link Transaction Transaction's} visibility
     *
     * @param transaction  {@link Transaction} to update
     * @param currentMap   {@link Map} containing all currently loaded {@link Transaction Transactions}
     * @param currentState What to set the transaction to (in terms of visibility)
     * @return {@code true} if successful, {@code false} if not
     */
    boolean toggleVisibility(Transaction transaction, Map<Integer, Transaction> currentMap, boolean currentState);

    /**
     * Gets list of all non-hidden {@link Transaction transactions} (if {@code hidden} is false) or
     * gets list of all hidden {@link Transaction transactions} (if {@code hidden} is true)
     *
     * @param onHidden Whether to get the hidden or non-hidden {@link Transaction transactions}
     * @param perPage  Number of {@link Transaction transactions} to get
     * @param page     Offset of {@link Transaction transactions} request
     * @return A map of all requested {@link Transaction Transactions}. The {@link Transaction} id as key and the object itself as value
     */
    Map<Integer, Transaction> getPagedTransactions(boolean onHidden, int perPage, int page);

    /**
     * Checks if a {@link Transaction} is in the local {@link Map} cache
     *
     * @param transaction {@link Transaction} to look for
     * @param map         {@link Map} to check through
     * @return The database id of the {@link Transaction} or -1 if it cannot be found
     */
    int getTransactionIdFromMap(Transaction transaction, Map<Integer, Transaction> map);

    /**
     * Computes the max page of the {@link Transaction} table
     *
     * @param onHidden Whether we are currently looking at hidden or non-hidden {@link Transaction Transactions}
     * @param perPage  How many {@link Transaction Transactions} should be displayed per page
     * @return The max page number of the {@link Transaction} table
     */
    int getMaxPage(boolean onHidden, int perPage);
}
