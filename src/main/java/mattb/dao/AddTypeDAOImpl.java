package mattb.dao;

import mattb.FinanceException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static mattb.FinanceError.SAVE_ACCOUNT_TYPE_FAIL;

public class AddTypeDAOImpl implements AddTypeDAO {
    private final Connection conn;

    /**
     * Sets database connection
     *
     * @param conn Connection to the SQLite database
     */
    public AddTypeDAOImpl(Connection conn) {
        this.conn = conn;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveType(String type) {
        String sql = "insert or ignore into account_type(type) values (?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, type);

            pstmt.executeUpdate();
        } catch (SQLException ignored) {
            new FinanceException(SAVE_ACCOUNT_TYPE_FAIL).displayAndLog();
        }
    }
}
