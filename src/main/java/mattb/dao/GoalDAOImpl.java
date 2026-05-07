package mattb.dao;

import mattb.FinanceException;
import mattb.model.Goal;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;

import static mattb.FinanceError.*;

/**
 * DAO Implementation for {@link Goal Goals}
 *
 * @author Matthew Beicke
 */
@Repository
public class GoalDAOImpl implements GoalDAO {
    private final JdbcTemplate jdbcTemplate;

    /**
     * Sets database connection
     *
     * @param jdbcTemplate Connection to the SQLite database
     */
    public GoalDAOImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveGoal(int accId, String goalName, double target) {
        String sql = "insert into goal (acc_id, target, initial, name) select acc_id, ?, balance, ? from account where acc_id = ?";

        try {
            jdbcTemplate.update(sql, target, goalName, accId);
        } catch (DataAccessException ignored) {
            new FinanceException(SAVE_GOAL_FAIL).displayAndLog();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateGoal(String goalName, double target, int goalId) {
        String sql = "update goal set name = ?, target = ? where goal_id = ?";

        try {
            jdbcTemplate.update(sql, goalName, target, goalId);
        } catch (DataAccessException ignored) {
            new FinanceException(UPDATE_GOAL_FAIL).displayAndLog();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteGoal(int goalId) {
        String sql = "delete from goal where goal_id = ?";

        try {
            jdbcTemplate.update(sql, goalId);
        } catch (DataAccessException ignored) {
            new FinanceException(DELETE_GOAL_FAIL).displayAndLog();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HashMap<Integer, Goal> getGoals() {
        HashMap<Integer, Goal> goals = new HashMap<>();

        String sql = """
                select goal_id, account.name as acc_name, target, initial, account.balance as current, goal.name as goal_name from goal
                left join account on goal.acc_id = account.acc_id
                """;

        try {
            jdbcTemplate.query(sql, rs -> {
                while (rs.next()) {
                    goals.put(rs.getInt("goal_id"), new Goal(rs.getDouble("current"), rs.getDouble("initial"), rs.getDouble("target"), rs.getString("acc_name"), rs.getString("goal_name")));
                }
            });
        } catch (DataAccessException ignored) {
            new FinanceException(LOAD_GOALS_FAIL).displayAndLog();
        }

        return goals;
    }
}
