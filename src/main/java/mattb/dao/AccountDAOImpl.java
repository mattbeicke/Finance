package mattb.dao;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import mattb.FinanceException;
import mattb.model.Account;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import static mattb.FinanceError.*;

/**
 * DAO Implementation for {@link Account Accounts}
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
    public void saveAccount(int typeId, double balance, String name, int id, boolean editing) {
        String sql;
        if (editing) {
            sql = "update account set acc_type = ?, balance = ?, name = ? where acc_id = ?";
        } else {
            sql = "insert or ignore into account (acc_type, balance, name) VALUES (?, ?, ?)";
        }
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, typeId);
            pstmt.setDouble(2, balance);
            pstmt.setString(3, name);
            if (editing) {
                pstmt.setInt(4, id);
            }

            pstmt.executeUpdate();
        } catch (SQLException ignored) {
            new FinanceException(SAVE_ACCOUNT_FAIL).displayAndLog();
        }
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
    public void updateBalances(double amount, int fromAccId, int toAccId) {
        // Update from account's balance
        if (fromAccId != 0) {
            String sql = "update account set balance = balance - ? where acc_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setDouble(1, amount);
                pstmt.setInt(2, fromAccId);

                pstmt.executeUpdate();
            } catch (SQLException ignored) {
                new FinanceException(UPDATE_ACCOUNT_BALANCE_FAIL).displayAndLog();
            }
        }

        // Update to account's balance
        if (toAccId != 0) {
            String sql = "update account set balance = balance + ? where acc_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setDouble(1, amount);
                pstmt.setInt(2, toAccId);

                pstmt.executeUpdate();
            } catch (SQLException ignored) {
                new FinanceException(UPDATE_ACCOUNT_BALANCE_FAIL).displayAndLog();
            }
        }
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
     * {@inheritDoc}
     */
    @Override
    public ObservableList<String> getAccountNames() {
        ObservableList<String> accountNames = FXCollections.observableArrayList();

        String sql = "select name from account where acc_id not in (select acc_id from hidden_accounts)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                accountNames.add(rs.getString("name"));
            }

            accountNames.add("Add more via Accounts tab");
        } catch (SQLException ignored) {
            new FinanceException(LOAD_ACCOUNTS_FAIL).displayAndLog();
        }

        return accountNames;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getAccountCount(boolean hidden) {
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

    /**
     * {@inheritDoc}
     */
    @Override
    public int getAccId(String accName) {
        if (accName == null || accName.isBlank()) return -1;

        String sql = "select acc_id from account where name=?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, accName);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("acc_id");
            } else {
                return -1;
            }
        } catch (SQLException ignored) {
            new FinanceException(GET_ACCOUNT_ID_FAIL).displayAndLog();
        }
        return -1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveAccountType(String type) {
        String sql = "insert or ignore into account_type(type) values (?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, type);

            pstmt.executeUpdate();
        } catch (SQLException ignored) {
            new FinanceException(SAVE_ACCOUNT_TYPE_FAIL).displayAndLog();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ObservableList<String> getAllTypes() {
        ObservableList<String> types = FXCollections.observableArrayList();

        String sql = "select type_id, type from account_type where type_id <> 0";
        try (PreparedStatement pstmt = conn.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                types.add(rs.getString("type"));
            }
        } catch (SQLException ignored) {
            new FinanceException(LOAD_ACCOUNT_TYPES_FAIL).displayAndLog();
        }

        types.add("Add more via Accounts tab");
        return types;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getTypeId(String type) {
        String sql = "select type_id from account_type where type = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, type);
            ResultSet rs = pstmt.executeQuery();

            if (!rs.next()) {
                return -1;
            }

            return rs.getInt("type_id");
        } catch (SQLException ignored) {
            new FinanceException(GET_ACCOUNT_TYPE_ID_FAIL).displayAndLog();
        }
        return -1;
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
}
