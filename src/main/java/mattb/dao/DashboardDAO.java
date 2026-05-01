package mattb.dao;

import mattb.model.Goal;

import java.util.HashMap;

public interface DashboardDAO {
    /**
     * Sums the users non-hidden account balances
     *
     * @return Net worth of the user
     */
    double getNetWorth();

    /**
     * Gets list of all goals a user has
     *
     * @return Map of Goal database id to the goal object itself
     */
    HashMap<Integer, Goal> getGoals();
}
