package mattb.dao;

import mattb.FinanceException;
import mattb.model.Transaction;

import java.sql.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashMap;

import static mattb.FinanceError.*;

/**
 * DAO Implementation for {@link Transaction Transactions}
 *
 * @author Matthew Beicke
 */
public class TransactionDAOImpl implements TransactionDAO {
    private final Connection conn;

    /**
     * Sets database connection
     *
     * @param conn Connection to the SQLite database
     */
    public TransactionDAOImpl(Connection conn) {
        this.conn = conn;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int insertTransaction(LocalDate date, int fromAccId, int toAccId, double amount, String memo) {
        String sql = "insert into \"transaction\" (date, from_acc, to_acc, amount, memo) values (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            if (date == null) {
                date = LocalDate.now();
            }
            pstmt.setInt(1, (int) date.atStartOfDay(ZoneId.systemDefault()).toEpochSecond());
            pstmt.setInt(2, fromAccId);
            pstmt.setInt(3, toAccId);
            pstmt.setDouble(4, amount);
            pstmt.setString(5, memo);

            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException ignored) {
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
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            if (date == null) {
                date = LocalDate.now();
            }
            pstmt.setInt(1, (int) date.atStartOfDay(ZoneId.systemDefault()).toEpochSecond());
            pstmt.setInt(2, fromAccId);
            pstmt.setInt(3, toAccId);
            pstmt.setDouble(4, amount);
            pstmt.setString(5, memo);
            pstmt.setInt(6, id);

            pstmt.executeUpdate();
        } catch (SQLException ignored) {
            new FinanceException(SAVE_TRANSACTION_FAIL).displayAndLog();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateTransactionVisibility(int transactionId, boolean hidden) {
        String sql = hidden ? "delete from hidden_transactions where t_id = ?" : "insert or ignore into hidden_transactions (t_id) values (?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, transactionId);
            pstmt.executeUpdate();
        } catch (SQLException ignored) {
            new FinanceException(UPDATE_HIDDEN_TRANSACTION_LIST_FAIL).displayAndLog();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void linkTransactionCategory(int t_id, int cat_id) {
        String sql = "insert into tcat(trans, cat) values (?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, t_id);
            pstmt.setInt(2, cat_id);

            pstmt.executeUpdate();
        } catch (SQLException ignored) {
            new FinanceException(SAVE_TRANSACTION_CATEGORY_FAIL).displayAndLog();
        }
    }

    /**
     * {@inheritDoc}
     */
    public int findOrCreateCategory(String cat) {
        if (cat == null || cat.isBlank()) return -1;

        String sql = "select cat_id from category where cat_name = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, cat);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("cat_id");
            }
        } catch (SQLException ignored) {
            new FinanceException(GET_CATEGORY_ID_FAIL).displayAndLog();
        }

        sql = "insert into category(cat_name) values (?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, cat);

            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException ignored) {
            new FinanceException(SAVE_CATEGORY_FAIL).displayAndLog();
        }

        return -1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clearCategoriesForTransaction(int t_id) {
        String sql = "delete from tcat where trans = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, t_id);

            pstmt.executeUpdate();
        } catch (SQLException ignored) {
            new FinanceException(CLEAR_CATEGORIES_FAIL).displayAndLog();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HashMap<Integer, Transaction> getAllTransactions(boolean hidden, int perPage, int page) {
        HashMap<Integer, Transaction> map = new HashMap<>();

        String sql = """
                select t.t_id, t.date, fa.name as from_acc_name, ta.name as to_acc_name, t.amount, t.memo, c.cat_name
                from (select t_id from "transaction" where t_id %s (select t_id from hidden_transactions) order by t_id
                limit ? offset ?) page join "transaction" t on t.t_id = page.t_id left join tcat on t.t_id = tcat.trans
                left join category c on tcat.cat = c.cat_id left join account ta on t.to_acc = ta.acc_id
                left join account fa on t.from_acc = fa.acc_id order by t.t_id
                """.formatted(hidden ? "in" : "not in");

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, perPage);
            pstmt.setInt(2, ((page - 1) * perPage));

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                if (map.containsKey(rs.getInt("t_id"))) {
                    Transaction existing = map.get(rs.getInt("t_id"));
                    map.put(rs.getInt("t_id"), new Transaction(existing.toAccountName(), existing.fromAccountName(), existing.amount(), existing.category() + ", " + rs.getString("cat_name"), existing.memo(), existing.date()));
                } else {
                    map.put(rs.getInt("t_id"), new Transaction(rs.getString("to_acc_name"), rs.getString("from_acc_name"), rs.getDouble("amount"), rs.getString("cat_name"), rs.getString("memo"), new java.util.Date(1000L * rs.getInt("date"))));
                }
            }
        } catch (SQLException ignored) {
            new FinanceException(LOAD_TRANSACTIONS_FAIL).displayAndLog();
        }
        return map;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getTransactionCount(boolean hidden) {
        String sql = "select count(t_id) as num from \"transaction\" where t_id" + (hidden ? " in (select t_id from hidden_transactions)" : " not in (select t_id from hidden_transactions)");

        try (PreparedStatement pstmt = conn.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("num");
            }
        } catch (SQLException ignored) {
            new FinanceException(GET_TRANSACTION_COUNT_FAIL).displayAndLog();
        }
        return -1;
    }
}
