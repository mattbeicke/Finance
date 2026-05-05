package mattb.dao;

import javafx.collections.ObservableList;
import mattb.model.Account;

import java.util.HashMap;

/**
 * DAO Interface for {@link Account Accounts}
 *
 * @author Matthew Beicke
 */
public interface AccountDAO {
    /**
     * Saves an {@link Account} to the database or updates one that is there already
     *
     * @param typeId  Database id of the {@link Account account's} {@code type}
     * @param balance Balance of {@link Account}
     * @param name    Name of {@link Account}
     * @param id      Database id of account (only used if {@code editing})
     * @param editing Whether we are editing ({@code true}) or saving new ({@code false})
     */
    void saveAccount(int typeId, double balance, String name, int id, boolean editing);

    /**
     * Changes {@link Account} with given {@code accountId} to the status of {@code hidden}
     *
     * @param accountId Database id of {@link Account} to update
     * @param hidden    Whether to set {@link Account} to hidden (if true) or unhidden (if false)
     */
    void updateAccountVisibility(int accountId, boolean hidden);

    /**
     * Updates {@link Account} balances (if they are not {@code External})
     *
     * @param amount    Amount of money transferred
     * @param fromAccId Database id of the {@link Account} money came from
     * @param toAccId   Database id of the {@link Account} money went to
     */
    void updateBalances(double amount, int fromAccId, int toAccId);

    /**
     * Sums the users non-hidden {@link Account} balances
     *
     * @return Net worth of the user
     */
    double getNetWorth();

    /**
     * Gets list of all unhidden {@link Account accounts} (if {@code hidden} is false) or
     * gets list of all hidden {@link Account accounts} (if {@code hidden} is true)
     *
     * @param hidden  Whether to get the hidden or unhidden {@link Account accounts}
     * @param perPage Number of {@link Account accounts} to get
     * @param page    Offset of {@link Account} request
     * @return A map of all requested {@link Account Accounts}. The {@link Account} id as key and the object itself as value
     */
    HashMap<Integer, Account> getAllAccounts(boolean hidden, int perPage, int page);

    /**
     * Gets list of all non-hidden {@link Account Accounts} not including External
     *
     * @return List of non-hidden {@link Account Accounts}
     */
    ObservableList<String> getAccountNames();

    /**
     * Gets list of all non-hidden {@link Account Accounts} including External and a message on how to add more
     *
     * @return List of {@link Account Accounts}
     */
    ObservableList<String> getAccountNamesExternal();

    /**
     * Gets the number of {@link Account accounts} in the database
     *
     * @param hidden Whether to get the hidden or unhidden {@link Account accounts}
     * @return Number of hidden or unhidden {@link Account accounts} in the database or -1 if none found
     */
    int getAccountCount(boolean hidden);

    /**
     * Gets an {@link Account} id from its {@code name}
     *
     * @param accName {@link Account} name
     * @return Database id associated with the {@link Account} name or -1 if no account was found
     */
    int getAccId(String accName);

    /**
     * Saves new {@code account type} to database
     *
     * @param type Name of the new {@code type} to save
     */
    void saveAccountType(String type);

    /**
     * Gets a list of all (non-{@code External} Account Types)
     *
     * @return A list of all Account Types plus a message that tells the user to add more in the {@link Account accounts} tab
     */
    ObservableList<String> getAllTypes();

    /**
     * Looks up the database id of the provided {@code type}
     *
     * @param type The {@code Account Type's} value
     * @return The database id of the {@code Account Type} or -1 if not found
     */
    int getTypeId(String type);
}
