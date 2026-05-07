package mattb.dao;

import javafx.collections.ObservableList;
import mattb.model.Account;
import mattb.model.Goal;

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
     * @return A list of all requested {@link Goal Goals}
     */
    ObservableList<Goal> getGoals();

    /**
     * Gets a {@link Goal Goals} database id
     *
     * @param accId   Database id of the {@link Account} the {@link Goal} is tracking
     * @param target  Target {@link Account} balance
     * @param initial Initial {@link Account} balance
     * @param name    Name of {@link Goal}
     * @return Database id of the {@link Goal} or -1 if it cannot be found
     */
    int getGoalId(int accId, double target, double initial, String name);
}
