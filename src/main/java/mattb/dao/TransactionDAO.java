package mattb.dao;

import mattb.model.Transaction;

import java.util.HashMap;

/**
 * DAO Interface for the TransactionController
 *
 * @author Matthew Beicke
 */
public interface TransactionDAO {
    /**
     * Gets list of all unhidden {@link Transaction transactions} (if {@code hidden} is false) or
     * gets list of all hidden {@link Transaction transactions} (if {@code hidden} is true)
     *
     * @param hidden  Whether to get the hidden or unhidden {@link Transaction transactions}
     * @param perPage Number of {@link Transaction transactions} to get
     * @param page    Offset of {@link Transaction transactions} request
     * @return A map of all requested {@link Transaction Transactions}. The {@link Transaction} id as key and the object itself as value
     */
    HashMap<Integer, Transaction> getAllTransactions(boolean hidden, int perPage, int page);

    /**
     * Changes {@link Transaction} with given {@code transactionId} to the status of {@code hidden}
     *
     * @param transactionId Database id of {@link Transaction} to update
     * @param hidden        Whether to set {@link Transaction} to hidden (if true) or unhidden (if false)
     */
    void updateTransactionVisibility(int transactionId, boolean hidden);

    /**
     * Gets the number of {@link Transaction transactions} in the database
     *
     * @param hidden Whether to get the hidden or unhidden {@link Transaction transactions}
     * @return Number of hidden or unhidden {@link Transaction transactions} in the database or -1 if none found
     */
    int getNumTransactions(boolean hidden);
}
