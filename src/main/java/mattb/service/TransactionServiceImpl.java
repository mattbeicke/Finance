package mattb.service;

import mattb.dao.TransactionDAO;
import mattb.model.Transaction;
import mattb.model.TransactionRequest;

import java.util.Map;

/**
 * Service Implementation for {@link Transaction Transactions}
 *
 * @author Matthew Beicke
 */
public class TransactionServiceImpl implements TransactionService {
    private final TransactionDAO transactionDAO;
    private final AccountService accountService;

    /**
     * Sets up DAO and Service connection
     *
     * @param transactionDAO Connection to the {@link TransactionDAO}
     * @param accountService Connection to the {@link AccountService}
     */
    public TransactionServiceImpl(TransactionDAO transactionDAO, AccountService accountService) {
        this.transactionDAO = transactionDAO;
        this.accountService = accountService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void processFullTransaction(TransactionRequest request, boolean shouldUpdateBalances) {
        transactionDAO.saveTransaction(request.date(), request.fromId(), request.toId(), request.amount(), request.memo(), request.id(), request.isEditing());

        transactionDAO.saveCategories(request.category());

        if (shouldUpdateBalances) {
            accountService.updateBalances(request.amount(), request.fromId(), request.toId());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void toggleVisibility(Transaction transaction, Map<Integer, Transaction> currentMap, boolean currentState) {
        int id = getTransactionIdFromMap(transaction, currentMap);
        if (id != -1) {
            transactionDAO.updateTransactionVisibility(id, currentState);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<Integer, Transaction> getPagedTransactions(boolean onHidden, int perPage, int page) {
        return transactionDAO.getAllTransactions(onHidden, perPage, page);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getTransactionIdFromMap(Transaction transaction, Map<Integer, Transaction> map) {
        if (transaction == null || map == null) return -1;
        for (Map.Entry<Integer, Transaction> entry : map.entrySet()) {
            if (entry.getValue().equals(transaction)) {
                return entry.getKey();
            }
        }
        return -1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getMaxPage(boolean onHidden, int perPage) {
        int count = transactionDAO.getTransactionCount(onHidden);
        return (int) Math.ceil(count / (double) perPage);
    }
}
