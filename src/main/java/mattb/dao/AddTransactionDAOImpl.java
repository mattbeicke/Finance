package mattb.dao;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import mattb.FinanceException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;

import static mattb.FinanceError.*;

public class AddTransactionDAOImpl implements AddTransactionDAO {
    private final Connection conn;

    /**
     * Sets database connection
     *
     * @param conn Connection to the SQLite database
     */
    public AddTransactionDAOImpl(Connection conn) {
        this.conn = conn;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ObservableList<String> loadAccountNames() {
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
     * Creates a category if it does not exist
     *
     * @param cat Category to add if needed
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
