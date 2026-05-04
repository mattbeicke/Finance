package mattb.service;

import mattb.model.Goal;

import java.util.Map;

public interface GoalService {
    Map<Integer, Goal> getGoals();

    int getGoalIdFromMap(Goal goal, Map<Integer, Goal> map);

    void saveGoal(int accId, String name, double target);

    void deleteGoal(int goalId);

    boolean updateGoal(String name, String target, int id);
}
