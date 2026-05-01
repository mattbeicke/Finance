package mattb.dao;

public interface UpdateGoalDAO {
    /**
     * Updates a goal with given goal name and target balance
     *
     * @param goalName Name of goal
     * @param target   Target balance
     * @param goalId   Database id of goal to update
     */
    void updateGoal(String goalName, double target, int goalId);

    /**
     * Deletes goal with given goal id
     *
     * @param goalId Database id of a Goal to delete
     */
    void deleteGoal(int goalId);
}
