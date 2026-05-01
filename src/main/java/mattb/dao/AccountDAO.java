package mattb.dao;

import mattb.model.Account;

import java.util.HashMap;

public interface AccountDAO {
    /**
     * Gets list of all unhidden {@link Account accounts} (if {@code hidden} is false) or
     * gets list of all hidden {@link Account accounts} (if {@code hidden} is true)
     *
     * @param hidden Whether to get the hidden or unhidden {@link Account accounts}
     * @return A map of all requested {@link Account Accounts}. The {@link Account} id as key and the object itself as value
     */
    HashMap<Integer, Account> getAllAccounts(boolean hidden);

    /**
     * Changes {@link Account} with given {@code accountId} to the status of {@code hidden}
     *
     * @param accountId Database id of {@link Account} to update
     * @param hidden    Whether to set {@link Account} to hidden (if true) or unhidden (if false)
     */
    void updateAccountVisibility(int accountId, boolean hidden);
}
