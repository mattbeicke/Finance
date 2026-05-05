package mattb.service;

import mattb.FinanceException;
import mattb.dao.TransactionDAO;
import mattb.model.Transaction;
import mattb.model.TransactionRequest;
import mattb.model.TransactionResponse;

import java.util.Map;

import static mattb.FinanceError.GET_TRANSACTION_ID_FAIL;

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
    public TransactionResponse processTransaction(TransactionRequest request) {
        int fromAccId = accountService.getAccId(request.fromAcc());
        int toAccId = accountService.getAccId(request.toAcc());

        if (fromAccId == -1 || toAccId == -1 || request.amount() == null || request.amount().isBlank()) {
            return new TransactionResponse(false, "Please fill all required fields", false);
        }

        double amount = Double.parseDouble(request.amount());

        String memo = (request.memo() == null || request.memo().isBlank()) ? "" : request.memo();

        int t_id;
        if (request.isEditing()) {
            transactionDAO.updateTransaction(request.date(), fromAccId, toAccId, amount, memo, request.id());
            t_id = request.id();
        } else {
            t_id = transactionDAO.insertTransaction(request.date(), fromAccId, toAccId, amount, memo);
        }

        if (t_id <= 0) {
            new FinanceException(GET_TRANSACTION_ID_FAIL).displayAndLog();
            return new TransactionResponse(false, "Please try again later", false);
        }

        if (request.isEditing()) {
            transactionDAO.clearCategoriesForTransaction(t_id);
        }

        if (request.category() != null && !request.category().isBlank()) {
            String[] categories = request.category().split(",\\s*");
            for (String cat : categories) {
                int catId = transactionDAO.findOrCreateCategory(cat);
                if (catId == -1) continue;
                transactionDAO.linkTransactionCategory(t_id, catId);
            }
        }
        return new TransactionResponse(true, "", !request.isEditing());
    }

    /**
     * {@inheritDoc}
     */
    @Override
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
    public boolean toggleVisibility(Transaction transaction, Map<Integer, Transaction> currentMap, boolean currentState) {
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
