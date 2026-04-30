package mattb.dao;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import mattb.FinanceException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static mattb.FinanceError.*;

public class AddGoalDAOImpl implements AddGoalDAO {
    private final Connection conn;

    /**
     * Sets database connection
     *
     * @param conn Connection to the SQLite database
     */
    public AddGoalDAOImpl(Connection conn) {
        this.conn = conn;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ObservableList<String> getAccounts() {
        ObservableList<String> accountNames = FXCollections.observableArrayList();

        String sql = "select name from account where acc_id <> 0";
        try (PreparedStatement pstmt = conn.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                accountNames.add(rs.getString("name"));
            }
        } catch (SQLException ignored) {
            new FinanceException(LOAD_ACCOUNTS_FAIL).displayAndLog();
        }

        return accountNames;
    }

    /**
     * {@inheritDoc
     */
    @Override
    public int getAccId(String accName) {
        String sql = "select acc_id from account where name = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, accName);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("acc_id");
            }
            return -1;
        } catch (SQLException ignored) {
            new FinanceException(GET_ACCOUNT_ID_FAIL).displayAndLog();
        }
        return -1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveGoal(int accId, String goalName, double target) {
        Result r = getInitialBalance(accId);
        if (!r.success()) return;

        String sql = "insert into goal (acc_id, target, initial, name) values (?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, accId);
            pstmt.setDouble(2, target);
            pstmt.setDouble(3, r.balance());
            pstmt.setString(4, goalName);
            pstmt.executeUpdate();
        } catch (SQLException ignored) {
            new FinanceException(SAVE_GOAL_FAIL).displayAndLog();
        }
    }

    private record Result(boolean success, double balance) {
    }

    /**
     * Gets the current (initial) balance of account
     *
     * @param accId Account to lookup balance for
     * @return A {@link Result} object that contains both the success value (true for success, false for failure) and what the initial balance is (if successful)
     */
    private Result getInitialBalance(int accId) {
        String sql = "select balance from account where acc_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, accId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new Result(true, rs.getInt("balance"));
            }
        } catch (SQLException ignored) {
            new FinanceException(GET_ACCOUNT_BALANCE_FAIL).displayAndLog();
        }
        return new Result(false, 0);
    }
}
