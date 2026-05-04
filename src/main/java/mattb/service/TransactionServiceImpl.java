package mattb.service;

import mattb.dao.TransactionDAO;
import mattb.model.Transaction;

import java.time.LocalDate;
import java.util.Map;

public class TransactionServiceImpl implements TransactionService {
    private final TransactionDAO transactionDAO;

    public TransactionServiceImpl(TransactionDAO transactionDAO) {
        this.transactionDAO = transactionDAO;
    }

    @Override
    public Map<Integer, Transaction> getPagedTransactions(boolean onHidden, int perPage, int page) {
        return transactionDAO.getAllTransactions(onHidden, perPage, page);
    }

    @Override
    public void toggleVisibility(Transaction transaction, Map<Integer, Transaction> currentMap, boolean currentState) {
        int id = getTransactionIdFromMap(transaction, currentMap);
        if (id != -1) {
            transactionDAO.updateTransactionVisibility(id, currentState);
        }
    }

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

    @Override
    public int getMaxPage(boolean onHidden, int perPage) {
        int count = transactionDAO.getTransactionCount(onHidden);
        return (int) Math.ceil(count / (double) perPage);
    }

    @Override
    public void saveTransaction(LocalDate date, int fromAccId, int toAccId, double amount, String memo, int id, boolean editing) {
        transactionDAO.saveTransaction(date, fromAccId, toAccId, amount, memo, id, editing);
    }

    @Override
    public void saveCategories(String input) {
        transactionDAO.saveCategories(input);
    }
}
