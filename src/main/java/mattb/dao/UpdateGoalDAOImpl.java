package mattb.dao;

import mattb.FinanceException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static mattb.FinanceError.DELETE_GOAL_FAIL;
import static mattb.FinanceError.UPDATE_GOAL_FAIL;

public class UpdateGoalDAOImpl implements UpdateGoalDAO {
    private final Connection conn;

    /**
     * Sets database connection
     *
     * @param conn Connection to the SQLite database
     */
    public UpdateGoalDAOImpl(Connection conn) {
        this.conn = conn;
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
}
