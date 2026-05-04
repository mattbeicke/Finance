package mattb.service;

import mattb.dao.GoalDAO;
import mattb.model.Goal;

import java.util.Map;

public class GoalServiceImpl implements GoalService {
    private final GoalDAO goalDAO;

    public GoalServiceImpl(GoalDAO goalDAO) {
        this.goalDAO = goalDAO;
    }

    @Override
    public Map<Integer, Goal> getGoals() {
        return goalDAO.getGoals();
    }

    @Override
    public int getGoalIdFromMap(Goal goal, Map<Integer, Goal> map) {
        if (goal == null || map == null) return -1;
        for (Map.Entry<Integer, Goal> entry : map.entrySet()) {
            if (entry.getValue().equals(goal)) {
                return entry.getKey();
            }
        }
        return -1;
    }

    @Override
    public void saveGoal(int accId, String name, double target) {
        goalDAO.saveGoal(accId, name, target);
    }

    @Override
    public void deleteGoal(int goalId) {
        goalDAO.deleteGoal(goalId);
    }

    @Override
    public boolean updateGoal(String name, String target, int id) {
        if (name.isBlank() || target.isBlank() || id < 1) return false;
        goalDAO.updateGoal(name, Double.parseDouble(target), id);
        return true;
    }
}
