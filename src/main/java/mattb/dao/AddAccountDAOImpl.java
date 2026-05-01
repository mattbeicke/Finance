package mattb.dao;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import mattb.FinanceException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static mattb.FinanceError.*;

/**
 * DAO Implementation for the AddAccountController
 *
 * @author Matthew Beicke
 */
public class AddAccountDAOImpl implements AddAccountDAO {
    private final Connection conn;

    /**
     * Sets database connection
     *
     * @param conn Connection to the SQLite database
     */
    public AddAccountDAOImpl(Connection conn) {
        this.conn = conn;
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
}
