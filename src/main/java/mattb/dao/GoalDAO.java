package mattb.dao;

import mattb.model.Account;
import mattb.model.Goal;

import java.util.HashMap;

/**
 * DAO Interface for {@link Goal Goals}
 *
 * @author Matthew Beicke
 */
public interface GoalDAO {
    /**
     * Saves {@link Goal} to database
     *
     * @param accId    Database id of the {@link Account} the {@link Goal} is tracking
     * @param goalName Name of {@link Goal}
     * @param target   Target {@link Account} balance
     */
    void saveGoal(int accId, String goalName, double target);

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

    /**
     * Gets list of all {@link Goal goals} a user has
     *
     * @return A map of all requested {@link Goal Goals}. The {@link Goal} id as key and the object itself as value
     */
    HashMap<Integer, Goal> getGoals();
}
