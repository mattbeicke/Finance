package mattb.service;

import mattb.model.Account;
import mattb.model.Goal;

import java.util.Map;

/**
 * Service Interface for {@link Goal Goals}
 *
 * @author Matthew Beicke
 */
public interface GoalService {
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
     * @return {@code true} if successful, {@code false} if not
     */
    boolean updateGoal(String goalName, String target, int goalId);

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
    Map<Integer, Goal> getGoals();

    /**
     * Checks if a {@link Goal} is in the local {@link Map} cache
     *
     * @param goal {@link Goal} to look for
     * @param map  {@link Map} to check through
     * @return The database id of the {@link Goal} or -1 if it cannot be found
     */
    int getGoalIdFromMap(Goal goal, Map<Integer, Goal> map);
}
