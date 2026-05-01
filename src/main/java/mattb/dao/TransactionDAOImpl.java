package mattb.dao;

import mattb.FinanceException;
import mattb.model.Transaction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import static mattb.FinanceError.LOAD_TRANSACTIONS_FAIL;
import static mattb.FinanceError.UPDATE_HIDDEN_TRANSACTION_LIST_FAIL;

/**
 * DAO Implementation for the TransactionController
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
    public HashMap<Integer, Transaction> getAllTransactions(boolean hidden) {
        HashMap<Integer, Transaction> map = new HashMap<>();

        try (PreparedStatement pstmt = conn.prepareStatement(getTableQuery(hidden)); ResultSet rs = pstmt.executeQuery()) {
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
     * Builds the database query for the {@link #getAllTransactions(boolean)} method
     *
     * @param hidden Whether to get the hidden {@link Transaction Transactions} or unhidden ones
     * @return The SQL query for the requesting method
     */
    private static String getTableQuery(boolean hidden) {
        String sql = """
                select t_id, date, fa.name as from_acc_name, ta.name as to_acc_name, amount, memo, cat_name from "transaction"
                left join tcat on t_id = trans
                left join category on cat = cat_id
                left join account ta on to_acc = ta.acc_id
                left join account fa on from_acc = fa.acc_id
                """;
        return sql + (hidden ? "where t_id in (select t_id from hidden_transactions)" : "where t_id not in (select t_id from hidden_transactions)");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateTransactionVisibility(int transactionId, boolean hidden) {
        String sql = hidden ? "delete from hidden_transactions where t_id=?" : "insert or ignore into hidden_transactions (t_id) values (?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, transactionId);
            pstmt.executeUpdate();
        } catch (SQLException ignored) {
            new FinanceException(UPDATE_HIDDEN_TRANSACTION_LIST_FAIL).displayAndLog();
        }
    }
}
