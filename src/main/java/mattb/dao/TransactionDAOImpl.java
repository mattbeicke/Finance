package mattb.dao;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import mattb.FinanceException;
import mattb.model.Transaction;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;

import static mattb.FinanceError.*;

/**
 * DAO Implementation for {@link Transaction Transactions}
 *
 * @author Matthew Beicke
 */
@Repository
public class TransactionDAOImpl implements TransactionDAO {
    private final JdbcTemplate jdbcTemplate;

    /**
     * Sets database connection
     *
     * @param jdbcTemplate Connection to the SQLite database
     */
    public TransactionDAOImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int insertTransaction(LocalDate date, int fromAccId, int toAccId, double amount, String memo) {
        String sql = "insert into \"transaction\" (date, from_acc, to_acc, amount, memo) values (?, ?, ?, ?, ?)";

        try {
            return jdbcTemplate.execute(conn -> {
                LocalDate finalDate = (date == null) ? LocalDate.now() : date;
                PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                pstmt.setInt(1, (int) finalDate.atStartOfDay(ZoneId.systemDefault()).toEpochSecond());
                pstmt.setInt(2, fromAccId);
                pstmt.setInt(3, toAccId);
                pstmt.setDouble(4, amount);
                pstmt.setString(5, memo);
                return pstmt;
            }, (PreparedStatement pstmt) -> {
                pstmt.executeUpdate();
                ResultSet rs = pstmt.getGeneratedKeys();
                return rs.next() ? rs.getInt(1) : -1;
            });
        } catch (DataAccessException e) {
            new FinanceException(SAVE_TRANSACTION_FAIL).displayAndLog();
        }

        return -1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateTransaction(LocalDate date, int fromAccId, int toAccId, double amount, String memo, int id) {
        String sql = "update \"transaction\" set date = ?, from_acc = ?, to_acc = ?, amount = ?, memo = ? where t_id = ?";

        try {
            if (date == null) {
                date = LocalDate.now();
            }
            jdbcTemplate.update(sql, (int) date.atStartOfDay(ZoneId.systemDefault()).toEpochSecond(), fromAccId, toAccId, amount, memo, id);
        } catch (DataAccessException ignored) {
            new FinanceException(SAVE_TRANSACTION_FAIL).displayAndLog();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateTransactionVisibility(int transactionId, boolean hidden) {
        String sql = hidden ? "delete from hidden_transactions where t_id = ?" : "insert or ignore into hidden_transactions (t_id) values (?)";

        try {
            jdbcTemplate.update(sql, transactionId);
        } catch (DataAccessException ignored) {
            new FinanceException(UPDATE_HIDDEN_TRANSACTION_LIST_FAIL).displayAndLog();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void linkTransactionCategory(int t_id, int cat_id) {
        String sql = "insert into tcat(trans, cat) values (?, ?)";

        try {
            jdbcTemplate.update(sql, t_id, cat_id);
        } catch (DataAccessException ignored) {
            new FinanceException(SAVE_TRANSACTION_CATEGORY_FAIL).displayAndLog();
        }
    }

    /**
     * {@inheritDoc}
     */
    public int findOrCreateCategory(String cat) {
        String selectSql = "select cat_id from category where cat_name = ?";

        try {
            Integer catId = jdbcTemplate.queryForObject(selectSql, Integer.class, cat);
            if (catId != null) {
                return catId;
            }
        } catch (EmptyResultDataAccessException ignored) {
            try {
                String insertSql = "insert into category (cat_name) values (?)";

                return jdbcTemplate.execute(conn -> {
                    PreparedStatement pstmt = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);
                    pstmt.setString(1, cat);
                    return pstmt;
                }, (PreparedStatement pstmt) -> {
                    pstmt.executeUpdate();
                    ResultSet rs = pstmt.getGeneratedKeys();
                    return rs.next() ? rs.getInt(1) : -1;
                });
            } catch (DataAccessException ignoredE) {
                new FinanceException(SAVE_CATEGORY_FAIL).displayAndLog();
            }
        } catch (DataAccessException ignored) {
            new FinanceException(GET_CATEGORY_ID_FAIL).displayAndLog();
        }

        return -1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clearCategoriesForTransaction(int t_id) {
        String sql = "delete from tcat where trans = ?";

        try {
            jdbcTemplate.update(sql, t_id);
        } catch (DataAccessException ignored) {
            new FinanceException(CLEAR_CATEGORIES_FAIL).displayAndLog();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ObservableList<Transaction> getAllTransactions(boolean hidden, int perPage, int page) {
        HashMap<Integer, Transaction> map = new HashMap<>();

        String sql = """
                select t.t_id, t.date, fa.name as from_acc_name, ta.name as to_acc_name, t.amount, t.memo, c.cat_name
                from (select t_id from "transaction" where t_id %s (select t_id from hidden_transactions) order by date desc
                limit ? offset ?) page join "transaction" t on t.t_id = page.t_id left join tcat on t.t_id = tcat.trans
                left join category c on tcat.cat = c.cat_id left join account ta on t.to_acc = ta.acc_id
                left join account fa on t.from_acc = fa.acc_id order by t.date desc
                """.formatted(hidden ? "in" : "not in");

        try {
            jdbcTemplate.query(sql, rs -> {
                while (rs.next()) {
                    if (map.containsKey(rs.getInt("t_id"))) {
                        Transaction existing = map.get(rs.getInt("t_id"));
                        map.put(rs.getInt("t_id"), new Transaction(existing.toAccountName(), existing.fromAccountName(), existing.amount(), existing.category() + ", " + rs.getString("cat_name"), existing.memo(), existing.date()));
                    } else {
                        map.put(rs.getInt("t_id"), new Transaction(rs.getString("to_acc_name"), rs.getString("from_acc_name"), rs.getDouble("amount"), rs.getString("cat_name"), rs.getString("memo"), new java.util.Date(1000L * rs.getInt("date"))));
                    }
                }
            }, perPage, ((page - 1) * perPage));
        } catch (DataAccessException ignored) {
            new FinanceException(LOAD_TRANSACTIONS_FAIL).displayAndLog();
        }

        return FXCollections.observableArrayList(map.values());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getTID(Date date, int fromAccId, int toAccId, double amount, String memo) {
        String sql = "select t_id from \"transaction\" where date = ? and from_acc = ? and to_acc = ? and amount = ? and memo = ?";

        try {
            Integer t_id = jdbcTemplate.queryForObject(sql, Integer.class, date.getTime() / 1000L, fromAccId, toAccId, amount, memo);
            if (t_id != null) return t_id;
        } catch (DataAccessException ignored) {
            new FinanceException(GET_TRANSACTION_ID_FAIL).displayAndLog();
        }

        return -1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getTransactionCount(boolean hidden) {
        String sql = "select count(t_id) as num from \"transaction\" where t_id %s (select t_id from hidden_transactions)".formatted(hidden ? "in" : "not in");

        try {
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class);
            return (count != null) ? count : -1;
        } catch (DataAccessException ignored) {
            new FinanceException(GET_TRANSACTION_COUNT_FAIL).displayAndLog();
        }

        return -1;
    }
}
