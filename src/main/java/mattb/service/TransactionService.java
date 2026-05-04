package mattb.service;

import mattb.model.Account;
import mattb.model.Transaction;
import mattb.model.TransactionRequest;

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
     * @param request              A {@link TransactionRequest} object containing all data needed for the {@link Transaction}
     * @param shouldUpdateBalances Whether to update {@link Account} balances or not
     */
    void processFullTransaction(TransactionRequest request, boolean shouldUpdateBalances);

    /**
     * Changes a {@link Transaction Transaction's} visibility
     *
     * @param transaction  {@link Transaction} to update
     * @param currentMap   {@link Map} containing all currently loaded {@link Transaction Transactions}
     * @param currentState What to set the transaction to (in terms of visibility)
     */
    void toggleVisibility(Transaction transaction, Map<Integer, Transaction> currentMap, boolean currentState);

    /**
     * Gets list of all unhidden {@link Transaction transactions} (if {@code hidden} is false) or
     * gets list of all hidden {@link Transaction transactions} (if {@code hidden} is true)
     *
     * @param onHidden Whether to get the hidden or unhidden {@link Transaction transactions}
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
     * @param onHidden Whether we are currently looking at hidden or unhidden {@link Transaction Transactions}
     * @param perPage  How many {@link Transaction Transactions} should be displayed per page
     * @return The max page number of the {@link Transaction} table
     */
    int getMaxPage(boolean onHidden, int perPage);
}
