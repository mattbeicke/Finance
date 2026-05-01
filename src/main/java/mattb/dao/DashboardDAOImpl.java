package mattb.dao;

import mattb.FinanceException;
import mattb.model.Goal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import static mattb.FinanceError.LOAD_GOALS_FAIL;
import static mattb.FinanceError.NET_WORTH_FAIL;

/**
 * DAO Implementation for the DashboardController
 *
 * @author Matthew Beicke
 */
public class DashboardDAOImpl implements DashboardDAO {
    private final Connection conn;

    /**
     * Sets database connection
     *
     * @param conn Connection to the SQLite database
     */
    public DashboardDAOImpl(Connection conn) {
        this.conn = conn;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getNetWorth() {
        String sql = "select sum(balance) as networth from account where acc_id not in (select acc_id from hidden_accounts)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {
            if (!rs.next()) {
                return 0;
            } else {
                return rs.getDouble("networth");
            }
        } catch (SQLException ignored) {
            new FinanceException(NET_WORTH_FAIL).displayAndLog();
        }
        return 0;
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
