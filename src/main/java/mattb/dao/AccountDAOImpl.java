package mattb.dao;

import mattb.FinanceException;
import mattb.model.Account;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import static mattb.FinanceError.*;

/**
 * DAO Implementation for the AccountController
 *
 * @author Matthew Beicke
 */
public class AccountDAOImpl implements AccountDAO {
    private final Connection conn;

    /**
     * Sets database connection
     *
     * @param conn Connection to the SQLite database
     */
    public AccountDAOImpl(Connection conn) {
        this.conn = conn;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HashMap<Integer, Account> getAllAccounts(boolean hidden, int perPage, int page) {
        HashMap<Integer, Account> map = new HashMap<>();

        try (PreparedStatement pstmt = conn.prepareStatement(getTableQuery(hidden, perPage, page)); ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                map.put(rs.getInt("acc_id"), new Account(
                        rs.getDouble("balance"),
                        rs.getString("type"),
                        rs.getString("name")
                ));
            }
        } catch (SQLException ignored) {
            new FinanceException(LOAD_ACCOUNTS_FAIL).displayAndLog();
        }
        return map;
    }

    /**
     * Builds the database query for the {@link #getAllAccounts(boolean, int, int)} method
     *
     * @param hidden  Whether to get the hidden {@link Account Accounts} or non-hidden ones
     * @param perPage Number of {@link Account Accounts} to get
     * @param page    Offset of {@link Account Accounts} request
     * @return The SQL query for the requesting method
     */
    private String getTableQuery(boolean hidden, int perPage, int page) {
        String sql = """
                select acc_id, name, balance, type from account
                left join account_type on acc_type = type_id
                where acc_id
                """;
        sql = sql + (hidden ? " in (select acc_id from hidden_accounts)" : " not in (select acc_id from hidden_accounts union select 0)");
        return sql + " limit " + perPage + " offset " + ((page - 1) * perPage);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateAccountVisibility(int accountId, boolean hidden) {
        String sql = hidden ? "delete from hidden_accounts where acc_id = ?" : "insert or ignore into hidden_accounts (acc_id) values (?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, accountId);

            pstmt.executeUpdate();
        } catch (SQLException ignored) {
            new FinanceException(UPDATE_HIDDEN_ACCOUNT_LIST_FAIL).displayAndLog();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getNumAccounts(boolean hidden) {
        String sql = "select count(acc_id) as num from account where acc_id" + (hidden ? " in (select acc_id from hidden_accounts)" : " not in (select acc_id from hidden_accounts union select 0)");

        try (PreparedStatement pstmt = conn.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("num");
            }
        } catch (SQLException ignored) {
            new FinanceException(GET_ACCOUNT_COUNT_FAIL).displayAndLog();
        }
        return -1;
    }
}
