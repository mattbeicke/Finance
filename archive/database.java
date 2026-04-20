package mattb.controllers;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class database {
    public static boolean checkProvider(String provider, Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("select name from provider where name=" + provider);
        return !rs.next();
    }
}
