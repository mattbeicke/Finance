package mattb.dao;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import mattb.FinanceException;
import mattb.model.Account;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;

import static mattb.FinanceError.*;

/**
 * DAO Implementation for {@link Account Accounts}
 *
 * @author Matthew Beicke
 */
@Service
public class AccountDAOImpl implements AccountDAO {
    private final JdbcTemplate jdbcTemplate;

    /**
     * Sets database connection
     *
     * @param jdbcTemplate Connection to the SQLite database
     */
    public AccountDAOImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void insertAccount(int typeId, double balance, String name) {
        String sql = "insert or ignore into account (acc_type, balance, name) VALUES (?, ?, ?)";

        try {
            jdbcTemplate.update(sql, typeId, balance, name);
        } catch (DataAccessException ignored) {
            new FinanceException(SAVE_ACCOUNT_FAIL).displayAndLog();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateAccount(int typeId, double balance, String name, int id) {
        String sql = "update account set acc_type = ?, balance = ?, name = ? where acc_id = ?";

        try {
            jdbcTemplate.update(sql, typeId, balance, name, id);
        } catch (DataAccessException ignored) {
            new FinanceException(SAVE_ACCOUNT_FAIL).displayAndLog();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateAccountVisibility(int accountId, boolean hidden) {
        String sql = hidden ? "delete from hidden_accounts where acc_id = ?" : "insert or ignore into hidden_accounts (acc_id) values (?)";

        try {
            jdbcTemplate.update(sql, accountId);
        } catch (DataAccessException ignored) {
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

            try {
                jdbcTemplate.update(sql, amount, fromAccId);
            } catch (DataAccessException ignored) {
                new FinanceException(UPDATE_ACCOUNT_BALANCE_FAIL).displayAndLog();
            }
        }

        // Update to account's balance
        if (toAccId != 0) {
            String sql = "update account set balance = balance + ? where acc_id = ?";

            try {
                jdbcTemplate.update(sql, amount, fromAccId);
            } catch (DataAccessException ignored) {
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

        try {
            Double d = jdbcTemplate.queryForObject(sql, Double.class);
            if (d != null) return d;
        } catch (DataAccessException ignored) {
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

        String sql = """
                select acc_id, name, balance, type from account left join account_type on acc_type = type_id
                where acc_id %s limit ? offset ?
                """.formatted(hidden ? "in (select acc_id from hidden_accounts)" : "not in (select acc_id from hidden_accounts union select 0)");

        try {
            jdbcTemplate.query(sql, rs -> {
                while (rs.next()) {
                    map.put(rs.getInt("acc_id"), new Account(rs.getDouble("balance"), rs.getString("type"), rs.getString("name")));
                }
            }, perPage, ((page - 1) * perPage));
        } catch (DataAccessException ignored) {
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

        String sql = "select name from account where acc_id not in (select acc_id from hidden_accounts union select 0)";

        try {
            jdbcTemplate.query(sql, rs -> {
                while (rs.next()) {
                    accountNames.add(rs.getString("name"));
                }
            });
        } catch (DataAccessException ignored) {
            new FinanceException(LOAD_ACCOUNTS_FAIL).displayAndLog();
        }

        return accountNames;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ObservableList<String> getAccountNamesExternal() {
        ObservableList<String> accountNames = FXCollections.observableArrayList();

        String sql = "select name from account where acc_id not in (select acc_id from hidden_accounts)";

        try {
            jdbcTemplate.query(sql, rs -> {
                while (rs.next()) {
                    accountNames.add(rs.getString("name"));
                }
            });
        } catch (DataAccessException ignored) {
            new FinanceException(LOAD_ACCOUNTS_FAIL).displayAndLog();
        }

        accountNames.add("Add more via Accounts tab");
        return accountNames;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getAccountCount(boolean hidden) {
        String sql = "select count(acc_id) as num from account where acc_id" + (hidden ? " in (select acc_id from hidden_accounts)" : " not in (select acc_id from hidden_accounts union select 0)");

        try {
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class);
            if (count != null) return count;
        } catch (DataAccessException ignored) {
            new FinanceException(GET_ACCOUNT_COUNT_FAIL).displayAndLog();
        }

        return -1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getAccId(String accName) {
        String sql = "select acc_id from account where name = ?";

        try {
            Integer id = jdbcTemplate.queryForObject(sql, Integer.class, accName);
            if (id != null) return id;
        } catch (DataAccessException ignored) {
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

        try {
            jdbcTemplate.update(sql, type);
        } catch (DataAccessException ignored) {
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

        try {
            jdbcTemplate.query(sql, rs -> {
                while (rs.next()) {
                    types.add(rs.getString("type"));
                }
            });
        } catch (DataAccessException ignored) {
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

        try {
            Integer id = jdbcTemplate.queryForObject(sql, Integer.class, type);
            if (id != null) return id;
        } catch (DataAccessException ignored) {
            new FinanceException(GET_ACCOUNT_TYPE_ID_FAIL).displayAndLog();
        }

        return -1;
    }
}
