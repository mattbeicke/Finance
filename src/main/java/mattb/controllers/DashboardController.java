package mattb.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import mattb.FinanceException;
import mattb.Main;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static mattb.FinanceError.NET_WORTH_FAIL;

public class DashboardController {
    @FXML
    private Label netWorth;

    @FXML
    private void initialize() {
        Connection conn = Main.getConn();

        String sql = "select sum(balance) as networth from account where acc_id not in(select acc_id from hidden_accounts)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {
            if (!rs.next()) {
                netWorth.setText("0");
            } else {
                netWorth.setText("$" + rs.getDouble("networth"));
            }
        } catch (SQLException ignored) {
            throw new FinanceException(NET_WORTH_FAIL);
        }
    }
}
