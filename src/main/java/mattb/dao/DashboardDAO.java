package mattb.dao;

import javafx.collections.ObservableList;
import mattb.model.Goal;

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
     * @return List of goals
     */
    ObservableList<Goal> getGoals();
}
