package mattb.dao;

import mattb.model.Goal;

/**
 * DAO Interface for the ViewGoalDetailsController
 *
 * @author Matthew Beicke
 */
public interface UpdateGoalDAO {
    /**
     * Updates a {@link Goal} with given {@code name} and {@code target} balance
     *
     * @param goalName Name of {@link Goal}
     * @param target   Target balance
     * @param goalId   Database id of {@link Goal} to update
     */
    void updateGoal(String goalName, double target, int goalId);

    /**
     * Deletes {@link Goal} with given {@code id}
     *
     * @param goalId Database id of a {@link Goal} to delete
     */
    void deleteGoal(int goalId);
}
