package mattb.service;

import mattb.FinanceException;
import mattb.dao.TransactionDAO;
import mattb.model.Transaction;
import mattb.model.TransactionResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Map;

import static mattb.FinanceError.GET_TRANSACTION_ID_FAIL;

/**
 * Service Implementation for {@link Transaction Transactions}
 *
 * @author Matthew Beicke
 */
@Service
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
    @Transactional
    public TransactionResponse processTransaction(LocalDate date, String fromAcc, String toAcc, String amount, String category, String memo, int id, boolean editing) {
        int fromAccId = accountService.getAccId(fromAcc);
        int toAccId = accountService.getAccId(toAcc);

        if (fromAccId == -1 || toAccId == -1 || amount == null || amount.isBlank()) {
            return new TransactionResponse(false, "Please fill all required fields", false);
        }

        double amountParsed = Double.parseDouble(amount);

        String memoParsed = (memo == null || memo.isBlank()) ? "" : memo;

        int t_id = id;
        if (editing) {
            transactionDAO.updateTransaction(date, fromAccId, toAccId, amountParsed, memoParsed, id);
        } else {
            t_id = transactionDAO.insertTransaction(date, fromAccId, toAccId, amountParsed, memoParsed);
        }

        if (t_id <= 0) {
            new FinanceException(GET_TRANSACTION_ID_FAIL).displayAndLog();
            return new TransactionResponse(false, "Please try again later", false);
        }

        if (editing) {
            transactionDAO.clearCategoriesForTransaction(t_id);
        }

        if (category != null && !category.isBlank()) {
            String[] categories = category.split(",\\s*");
            for (String cat : categories) {
                if (cat == null || cat.isBlank()) continue;
                int catId = transactionDAO.findOrCreateCategory(cat);
                if (catId == -1) continue;
                transactionDAO.linkTransactionCategory(t_id, catId);
            }
        }
        return new TransactionResponse(true, "", !editing);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public boolean updateBalances(String fromAcc, String toAcc, String amount) {
        int fromAccId = accountService.getAccId(fromAcc);
        int toAccId = accountService.getAccId(toAcc);

        if (fromAccId == -1 || toAccId == -1 || amount == null) {
            return false;
        }

        accountService.updateBalances(Double.parseDouble(amount), fromAccId, toAccId);

        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public boolean toggleVisibility(Transaction transaction, Map<Integer, Transaction> currentMap, boolean currentState) {
        if (transaction == null || currentMap == null) return false;

        int id = getTransactionIdFromMap(transaction, currentMap);

        if (id != -1) {
            transactionDAO.updateTransactionVisibility(id, currentState);
            return true;
        }

        return false;
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
