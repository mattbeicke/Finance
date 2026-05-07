package mattb.service;

import javafx.collections.ObservableList;
import mattb.model.Account;
import mattb.model.Transaction;
import mattb.model.TransactionResponse;

import java.time.LocalDate;

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
     * @param currentState What to set the transaction to (in terms of visibility)
     * @return {@code true} if successful, {@code false} if not
     */
    boolean toggleVisibility(Transaction transaction, boolean currentState);

    /**
     * Gets list of all non-hidden {@link Transaction transactions} (if {@code hidden} is false) or
     * gets list of all hidden {@link Transaction transactions} (if {@code hidden} is true)
     *
     * @param onHidden Whether to get the hidden or non-hidden {@link Transaction transactions}
     * @param perPage  Number of {@link Transaction transactions} to get
     * @param page     Offset of {@link Transaction transactions} request
     * @return A list of all requested {@link Transaction Transactions}
     */
    ObservableList<Transaction> getPagedTransactions(boolean onHidden, int perPage, int page);

    /**
     * Gets a {@link Transaction Transactions} database id
     *
     * @param transaction {@link Transaction} to find id of
     * @return Database id of {@code transaction} or -1 if it cannot be found
     */
    int getTID(Transaction transaction);

    /**
     * Computes the max page of the {@link Transaction} table
     *
     * @param onHidden Whether we are currently looking at hidden or non-hidden {@link Transaction Transactions}
     * @param perPage  How many {@link Transaction Transactions} should be displayed per page
     * @return The max page number of the {@link Transaction} table
     */
    int getMaxPage(boolean onHidden, int perPage);
}
