package mattb.service;

import javafx.collections.ObservableList;
import mattb.model.Account;
import mattb.model.Goal;
import mattb.model.GoalResponse;

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
     * @param accName  Database id of the {@link Account} the {@link Goal} is tracking
     * @param goalName Name of {@link Goal}
     * @param target   Target {@link Account} balance
     * @return A {@link GoalResponse} object containing data based on if the {@link Goal} processed correctly or not
     */
    GoalResponse saveGoal(String accName, String goalName, String target);

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
     * @return A list of all requested {@link Goal Goals}
     */
    ObservableList<Goal> getGoals();

    /**
     * Checks if a {@link Goal} is in the local {@link Map} cache
     *
     * @param goal {@link Goal} to look for
     * @return The database id of the {@link Goal} or -1 if it cannot be found
     */
    int getGoalId(Goal goal);
}
