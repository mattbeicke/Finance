package mattb.dao;

import mattb.model.Account;
import mattb.model.Goal;

import java.util.HashMap;

/**
 * DAO Interface for the DashboardController
 *
 * @author Matthew Beicke
 */
public interface DashboardDAO {
    /**
     * Sums the users non-hidden {@link Account} balances
     *
     * @return Net worth of the user
     */
    double getNetWorth();

    /**
     * Gets list of all {@link Goal goals} a user has
     *
     * @return A map of all requested {@link Goal Goals}. The {@link Goal} id as key and the object itself as value
     */
    HashMap<Integer, Goal> getGoals();
}
