package mattb.dao;

import javafx.collections.ObservableList;

public interface AddGoalDAO {
    /**
     * Gets list of all Accounts
     *
     * @return A list of all Accounts
     */
    ObservableList<String> getAccounts();

    /**
     * Gets the database id of an account
     *
     * @param accName Account to look up
     * @return Database id of given account
     */
    int getAccId(String accName);

    /**
     * Saves goal to database
     *
     * @param accId    Database id of account goal is tracking
     * @param goalName Name of goal
     * @param target   Target account balance
     */
    void saveGoal(int accId, String goalName, double target);
}
