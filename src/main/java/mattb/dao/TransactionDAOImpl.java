package mattb.dao;

import mattb.FinanceException;
import mattb.model.Transaction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.HashMap;

import static mattb.FinanceError.*;

/**
 * DAO Implementation for Transactions
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
    public HashMap<Integer, Transaction> getAllTransactions(boolean hidden, int perPage, int page) {
        HashMap<Integer, Transaction> map = new HashMap<>();

        try (PreparedStatement pstmt = conn.prepareStatement(getTableQuery(hidden, perPage, page)); ResultSet rs = pstmt.executeQuery()) {
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
     * Builds the database query for the {@link #getAllTransactions(boolean, int, int)} method
     *
     * @param hidden Whether to get the hidden {@link Transaction Transactions} or unhidden ones
     * @return The SQL query for the requesting method
     */
    private static String getTableQuery(boolean hidden, int perPage, int page) {
        String sql = """
                select t_id, date, fa.name as from_acc_name, ta.name as to_acc_name, amount, memo, cat_name from "transaction"
                left join tcat on t_id = trans
                left join category on cat = cat_id
                left join account ta on to_acc = ta.acc_id
                left join account fa on from_acc = fa.acc_id
                where t_id
                """;
        sql = sql + (hidden ? " in (select t_id from hidden_transactions)" : " not in (select t_id from hidden_transactions)");
        return sql + " limit " + perPage + " offset " + ((page - 1) * perPage);
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


    /**
     * {@inheritDoc}
     */
    @Override
    public void saveTransaction(LocalDate date, int fromAccId, int toAccId, double amount, String memo, int id, boolean editing) {
        String sql;
        if (editing) {
            sql = "update \"transaction\" set date = ?, from_acc = ?, to_acc = ?, amount = ?, memo = ? where t_id = ?";
        } else {
            sql = "insert into \"transaction\" (date, from_acc, to_acc, amount, memo) values (?, ?, ?, ?, ?)";
        }
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            if (date == null) {
                date = LocalDate.now();
            }
            pstmt.setInt(1, (int) date.atStartOfDay(ZoneId.systemDefault()).toEpochSecond());
            pstmt.setInt(2, fromAccId);
            pstmt.setInt(3, toAccId);
            pstmt.setDouble(4, amount);
            if (!memo.isBlank()) {
                pstmt.setString(5, memo);
            } else {
                pstmt.setString(5, "");
            }
            if (editing) {
                pstmt.setInt(6, id);
            }

            pstmt.executeUpdate();
        } catch (SQLException ignored) {
            new FinanceException(SAVE_TRANSACTION_FAIL).displayAndLog();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveCategories(String input) {
        // Get all categories the user entered
        String[] categories = input.split(",\\s*");
        if (categories.length == 0) return;
        if (categories.length == 1 && categories[0].isBlank()) return;

        // Find transaction id
        int t_id = 0;
        String sql = "select max(t_id) as t_id from \"transaction\"";
        try (PreparedStatement pstmt = conn.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {
            rs.next();
            t_id = rs.getInt("t_id");
        } catch (SQLException ignored) {
            new FinanceException(GET_TRANSACTION_ID_FAIL).displayAndLog();
        }

        if (t_id <= 0) return;

        int[] cats = new int[categories.length];

        for (int i = 0; i < categories.length; i++) {
            String category = categories[i];
            createCatIfNeeded(category);

            sql = "select cat_id from category where cat_name = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, category);

                ResultSet rs = pstmt.executeQuery();
                rs.next();
                cats[i] = rs.getInt("cat_id");
            } catch (SQLException ignored) {
                new FinanceException(GET_CATEGORY_ID_FAIL).displayAndLog();
            }
        }

        // Update the tcat table
        String placeholders = String.join(",", Collections.nCopies(cats.length, "(?, ?)"));
        sql = "insert into tcat(trans, cat) values " + placeholders;
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (int i = 0; i < cats.length; i++) {
                pstmt.setInt(i * 2 + 1, t_id);
                pstmt.setInt(i * 2 + 2, cats[i]);
            }

            pstmt.executeUpdate();
        } catch (SQLException ignored) {
            new FinanceException(SAVE_TRANSACTION_CATEGORY_FAIL).displayAndLog();
        }
    }

    /**
     * Creates a {@code Category} if it does not exist
     *
     * @param cat Name of {@code Category} to add if needed
     */
    private void createCatIfNeeded(String cat) {
        if (cat == null || cat.isBlank()) return;

        String sql = "insert or ignore into category(cat_name) values (?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, cat);

            pstmt.executeUpdate();
        } catch (SQLException ignored) {
            new FinanceException(SAVE_CATEGORY_FAIL).displayAndLog();
        }
    }
}
