package mattb.dao;

import javafx.collections.ObservableList;
import mattb.model.Account;
import mattb.model.Goal;

public interface AddGoalDAO {
    /**
     * Gets list of all {@link Account Accounts}
     *
     * @return A list of all {@link Account Accounts} (both hidden and non-hidden)
     */
    ObservableList<String> getAccounts();

    /**
     * Gets the database id of an {@link Account}
     *
     * @param accName Name of the {@link Account} to look up
     * @return Database id of given {@link Account}
     */
    int getAccId(String accName);

    /**
     * Saves {@link Goal} to database
     *
     * @param accId    Database id of the {@link Account} the {@link Goal} is tracking
     * @param goalName Name of {@link Goal}
     * @param target   Target {@link Account} balance
     */
    void saveGoal(int accId, String goalName, double target);
}
