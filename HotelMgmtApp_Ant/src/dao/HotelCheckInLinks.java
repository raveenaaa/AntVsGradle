package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
/**
 * The class is used to perform the operations on the hotelcheckinlinks tables
 * hotelcheckinlinks maintain the link between hotel and checkinid of a customer
 * @author kshittiz
 *
 */
public class HotelCheckInLinks {
    private static Connection c = null;

    public static void setConnnection(Connection conn) {
        c = conn;
    }

    public int addHotelCheckInLinks(int hotel_id, int cid) throws SQLException {
        int id = 0;
        PreparedStatement exe = c.prepareStatement(
                "insert into hotel_checkin_links(hotel_id,cid) values(?, ?)",
                Statement.RETURN_GENERATED_KEYS);
        exe.setInt(1, hotel_id);
        exe.setInt(2, cid);

        exe.executeQuery();
        ResultSet result = exe.getGeneratedKeys();
        if (result.next())
            id = result.getInt(1);

        return id;
    }

}
