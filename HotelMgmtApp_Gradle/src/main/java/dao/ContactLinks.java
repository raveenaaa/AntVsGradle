package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ContactLinks {
    private static Connection c = null;

    public static void setConnnection(Connection conn) {
        c = conn;
    }
/**
 * The method is used to create contact links in contact_links
 * @param link_id
 * @param contact_id
 * @param type
 * @return
 * @throws SQLException
 */
    public int CreateContactLinks(int link_id, int contact_id, String type) throws SQLException {
        int id = 0;
        PreparedStatement exe = c.prepareStatement(
                "insert into contact_links(link_id,contact_id, type) values(?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS);
        exe.setInt(1, link_id);
        exe.setInt(2, contact_id);
        exe.setString(3, type);
        exe.executeQuery();
        ResultSet result = exe.getGeneratedKeys();
        if (result.next())
            id = result.getInt(1);
        return id;
    }

}
