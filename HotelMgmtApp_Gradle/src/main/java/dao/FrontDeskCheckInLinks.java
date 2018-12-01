package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class FrontDeskCheckInLinks {
    private static Connection c = null;

    public static void setConnnection(Connection conn) {
        c = conn;
    }
/**
 * The function is used to add an entry to the frontdesk_check_links which maintains a link between the 
 * id of the front desk representative and the checked in customers id
 * @param pid
 * @param cid
 * @return
 * @throws SQLException
 */
    public int addFrontDeskCheckInLinks(int pid, int cid) throws SQLException {
        int id = 0;
        PreparedStatement exe = c.prepareStatement(
                "insert into frontdesk_checkin_links(pid,cid) values(?, ?)",
                Statement.RETURN_GENERATED_KEYS);
        exe.setInt(1, pid);
        exe.setInt(2, cid);

        exe.executeQuery();
        ResultSet result = exe.getGeneratedKeys();
        if (result.next())
            id = result.getInt(1);

        return id;
    }

}
