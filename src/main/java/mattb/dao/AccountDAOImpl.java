package mattb.dao;

import mattb.FinanceException;
import mattb.model.Account;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import static mattb.FinanceError.LOAD_ACCOUNTS_FAIL;
import static mattb.FinanceError.UPDATE_HIDDEN_ACCOUNT_LIST_FAIL;

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
    public HashMap<Integer, Account> getAllAccounts(boolean hidden) {
        HashMap<Integer, Account> map = new HashMap<>();

        try (PreparedStatement pstmt = conn.prepareStatement(getTableQuery(hidden)); ResultSet rs = pstmt.executeQuery()) {
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
     * Builds the database query for the {@link #getAllAccounts(boolean)} method
     *
     * @param hidden Whether to get the hidden {@link Account Accounts} or unhidden ones
     * @return The SQL query for the requesting method
     */
    private String getTableQuery(boolean hidden) {
        String sql = """
                select acc_id, name, balance, type from account
                left join account_type on acc_type = type_id
                where acc_id
                """;
        return sql + (hidden ? " in (select acc_id from hidden_accounts)" : " not in (select acc_id from hidden_accounts union select 0)");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateAccountVisibility(int accountId, boolean hidden) {
        String sql = hidden ? "delete from hidden_accounts where acc_id=?" : "insert or ignore into hidden_accounts (acc_id) values (?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, accountId);

            pstmt.executeUpdate();
        } catch (SQLException ignored) {
            new FinanceException(UPDATE_HIDDEN_ACCOUNT_LIST_FAIL).displayAndLog();
        }
    }
}
