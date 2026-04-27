package mattb.dao;

import javafx.collections.ObservableList;

import java.time.LocalDate;

public interface AddTransactionDAO {
    /**
     * Gets list of all non-hidden accounts
     *
     * @return List of non-hidden accounts
     */
    ObservableList<String> loadAccountNames();

    /**
     * Gets an account id from an account name
     *
     * @param accName Account name
     * @return Account ID associated with the account name or -1 if no account was found
     */
    int getAccId(String accName);

    /**
     * Saves or updates a transaction to or in the database
     *
     * @param date      Date transaction occurred on
     * @param fromAccId Database id of the account money came from
     * @param toAccId   Database id of the account money went to
     * @param amount    Amount of money transferred
     * @param memo      Memo associated with transaction
     * @param id        Database id of transaction (if editing)
     * @param editing   Whether we are editing or saving new
     */
    void saveTransaction(LocalDate date, int fromAccId, int toAccId, double amount, String memo, int id, boolean editing);

    /**
     * Populates the tcat table for the transaction
     *
     * @param input User input into the transactions "Category" field
     */
    void saveCategories(String input);

    /**
     * Updates account balances (if they are not the reserved external one)
     *
     * @param amount    Amount of money transferred
     * @param fromAccId Database id of the account money came from
     * @param toAccId   Database id of the account money went to
     */
    void updateBalances(double amount, int fromAccId, int toAccId);
}
