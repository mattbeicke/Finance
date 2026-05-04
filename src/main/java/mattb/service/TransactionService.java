package mattb.service;

import mattb.model.Transaction;

import java.time.LocalDate;
import java.util.Map;

public interface TransactionService {
    Map<Integer, Transaction> getPagedTransactions(boolean onHidden, int perPage, int page);

    void toggleVisibility(Transaction transaction, Map<Integer, Transaction> currentMap, boolean currentState);

    int getTransactionIdFromMap(Transaction transaction, Map<Integer, Transaction> map);

    int getMaxPage(boolean onHidden, int perPage);

    void saveTransaction(LocalDate date, int fromAccId, int toAccId, double amount, String memo, int id, boolean editing);

    void saveCategories(String input);
}
