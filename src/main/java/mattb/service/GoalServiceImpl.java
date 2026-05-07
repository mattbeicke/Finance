package mattb.service;

import javafx.collections.ObservableList;
import mattb.dao.GoalDAO;
import mattb.model.Goal;
import mattb.model.GoalResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for {@link Goal Goals}
 *
 * @author Matthew Beicke
 */
@Service
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
    @Transactional
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
    @Transactional
    public boolean updateGoal(String name, String target, int id) {
        if (name.isBlank() || target.isBlank() || id < 1) return false;

        goalDAO.updateGoal(name, Double.parseDouble(target), id);

        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void deleteGoal(int goalId) {
        goalDAO.deleteGoal(goalId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ObservableList<Goal> getGoals() {
        return goalDAO.getGoals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getGoalId(Goal goal) {
        int accId = accountService.getAccId(goal.account());

        if (accId == -1) return -1;

        return goalDAO.getGoalId(accId, goal.target(), goal.initial(), goal.name());
    }
}
