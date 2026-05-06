package mattb.service;

import mattb.dao.GoalDAO;
import mattb.model.Goal;
import mattb.model.GoalResponse;

import java.util.Map;

/**
 * Service Implementation for {@link Goal Goals}
 *
 * @author Matthew Beicke
 */
public class GoalServiceImpl implements GoalService {
    private final GoalDAO goalDAO;
    private final AccountService accountService;

    /**
     * Sets up DAO connection
     *
     * @param goalDAO        Connection to the {@link GoalDAO}
     * @param accountService Connection to the {@link AccountService}
     */
    public GoalServiceImpl(GoalDAO goalDAO, AccountService accountService) {
        this.goalDAO = goalDAO;
        this.accountService = accountService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GoalResponse saveGoal(String accName, String goalName, String target) {
        if (accName == null || goalName == null || target == null) {
            return new GoalResponse(false, "Please fill all required fields");
        }

        int accId = accountService.getAccId(accName);

        if (accId == -1 || goalName.isBlank() || target.isBlank()) {
            return new GoalResponse(false, "Please fill all required fields");
        }

        goalDAO.saveGoal(accId, goalName, Double.parseDouble(target));

        return new GoalResponse(true, "");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean updateGoal(String name, String target, int id) {
        if (name.isBlank() || target.isBlank() || id < 1) return false;

        goalDAO.updateGoal(name, Double.parseDouble(target), id);

        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteGoal(int goalId) {
        goalDAO.deleteGoal(goalId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<Integer, Goal> getGoals() {
        return goalDAO.getGoals();
    }

    /**
     * {@inheritDoc}
     */
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
}
