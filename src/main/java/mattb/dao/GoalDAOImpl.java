package mattb.dao;

import mattb.FinanceException;
import mattb.model.Goal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import static mattb.FinanceError.*;

/**
 * DAO Implementation for {@link Goal Goals}
 *
 * @author Matthew Beicke
 */
public class GoalDAOImpl implements GoalDAO {
    private final Connection conn;

    /**
     * Sets database connection
     *
     * @param conn Connection to the SQLite database
     */
    public GoalDAOImpl(Connection conn) {
        this.conn = conn;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveGoal(int accId, String goalName, double target) {
        String sql = "insert into goal (acc_id, target, initial, name) select acc_id, ?, balance, ? from account where acc_id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, target);
            pstmt.setString(2, goalName);
            pstmt.setInt(3, accId);

            pstmt.executeUpdate();
        } catch (SQLException e) {
            new FinanceException(SAVE_GOAL_FAIL).displayAndLog();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateGoal(String goalName, double target, int goalId) {
        String sql = "update goal set name = ?, target = ? where goal_id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, goalName);
            pstmt.setDouble(2, target);
            pstmt.setInt(3, goalId);

            pstmt.executeUpdate();
        } catch (SQLException ignored) {
            new FinanceException(UPDATE_GOAL_FAIL).displayAndLog();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteGoal(int goalId) {
        String sql = "delete from goal where goal_id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, goalId);

            pstmt.executeUpdate();
        } catch (SQLException ignored) {
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
        try (PreparedStatement pstmt = conn.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                goals.put(rs.getInt("goal_id"), new Goal(rs.getDouble("current"), rs.getDouble("initial"), rs.getDouble("target"), rs.getString("acc_name"), rs.getString("goal_name")));
            }
        } catch (SQLException ignored) {
            new FinanceException(LOAD_GOALS_FAIL).displayAndLog();
        }

        return goals;
    }
}
