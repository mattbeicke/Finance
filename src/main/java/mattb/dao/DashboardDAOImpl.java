package mattb.dao;

import mattb.FinanceException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static mattb.FinanceError.NET_WORTH_FAIL;

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
}
