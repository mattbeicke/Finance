package mattb.service;

import javafx.collections.ObservableList;
import mattb.model.Account;

import java.util.Map;

/**
 * Service Interface for {@link Account Accounts}
 *
 * @author Matthew Beicke
 */
public interface AccountService {
    /**
     * Saves an {@link Account} to the database or updates one that is there already
     *
     * @param typeId    Database id of the {@link Account account's} {@code type}
     * @param balance   Balance of {@link Account}
     * @param name      Name of {@link Account}
     * @param id        Database id of account (only used if {@code editing})
     * @param isEditing Whether we are editing ({@code true}) or saving new ({@code false})
     */
    void saveAccount(int typeId, double balance, String name, int id, boolean isEditing);

    /**
     * Changes a {@link Account Account's} visibility
     *
     * @param account      {@link Account} to update
     * @param currentMap   {@link Map} containing all currently loaded {@link Account Accounts}
     * @param currentState What to set the transaction to (in terms of visibility)
     * @return {@code true} if successful, {@code false} if not
     */
    boolean toggleVisibility(Account account, Map<Integer, Account> currentMap, boolean currentState);

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
     * @param onHidden Whether to get the hidden or unhidden {@link Account accounts}
     * @param perPage  Number of {@link Account accounts} to get
     * @param page     Offset of {@link Account} request
     * @return A map of all requested {@link Account Accounts}. The {@link Account} id as key and the object itself as value
     */
    Map<Integer, Account> getPagedAccounts(boolean onHidden, int perPage, int page);

    /**
     * Gets list of all non-hidden {@link Account Accounts}
     *
     * @return List of non-hidden {@link Account Accounts}
     */
    ObservableList<String> getAccountNames();

    /**
     * Gets an {@link Account} id from its {@code name}
     *
     * @param name {@link Account} name
     * @return Database id associated with the {@link Account} name or -1 if no account was found
     */
    int getAccId(String name);

    /**
     * Checks if a {@link Account} is in the local {@link Map} cache
     *
     * @param account {@link Account} to look for
     * @param map     {@link Map} to check through
     * @return The database id of the {@link Account} or -1 if it cannot be found
     */
    int getAccountIdFromMap(Account account, Map<Integer, Account> map);

    /**
     * Computes the max page of the {@link Account} table
     *
     * @param onHidden Whether we are currently looking at hidden or unhidden {@link Account Accounts}
     * @param perPage  How many {@link Account Accounts} should be displayed per page
     * @return The max page number of the {@link Account} table
     */
    int getMaxPage(boolean onHidden, int perPage);

    /**
     * Saves new {@code account type} to database
     *
     * @param type Name of the new {@code type} to save
     * @return {@code true} if something went wrong, {@code false} if something did not
     */
    boolean saveAccountType(String type);

    /**
     * Gets a list of all (non-{@code External} Account Types)
     *
     * @return A list of all Account Types plus a message that tells the user to add more in the {@link Account accounts} tab
     */
    ObservableList<String> getAccountTypes();

    /**
     * Looks up the database id of the provided {@code type}
     *
     * @param typeName The {@code Account Type's} value
     * @return The database id of the {@code Account Type} or -1 if not found
     */
    int getTypeIdByName(String typeName);
}