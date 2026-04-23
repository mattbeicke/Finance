package mattb.dao;

import mattb.model.Transaction;

import java.util.HashMap;

public interface TransactionDAO {
    /**
     * Gets list of all unhidden {@link Transaction transactions} (if {@code hidden} is false) or
     * Gets list of all hidden {@link Transaction transactions} (if {@code hidden} is true)
     *
     * @param hidden Whether to get the hidden or unhidden {@link Transaction transactions}
     * @return A map of all requested {@link Transaction Transactions}. The transaction id as key and the thing itself as value
     */
    HashMap<Integer, Transaction> getAllTransactions(boolean hidden);

    /**
     * Changes {@link Transaction} with given {@code transactionId} to the status of {@code hidden}
     *
     * @param transactionId Database id of transaction to update
     * @param hidden        Whether to set transaction to hidden (if true) or unhidden (if false)
     */
    void updateTransactionVisibility(int transactionId, boolean hidden);
}
