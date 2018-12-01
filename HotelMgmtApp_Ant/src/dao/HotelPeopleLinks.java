package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
/**
 * The HotelPeopleLinks class is used to maintain a link between hotel and people
 * @author kshittiz
 *
 */
public class HotelPeopleLinks {
    private static Connection c = null;

    public static void setConnnection(Connection conn) {
        c = conn;
    }

    public int addHotelPeopleLinks(int hotel_id, int pid) throws SQLException {
        int id = 0;
        PreparedStatement exe = c.prepareStatement(
                "insert into hotel_people_links(hotel_id,pid) values(?, ?)",
                Statement.RETURN_GENERATED_KEYS);
        exe.setInt(1, hotel_id);
        exe.setInt(2, pid);

        exe.executeQuery();
        ResultSet result = exe.getGeneratedKeys();
        if (result.next())
            id = result.getInt(1);

        return id;
    }
/**
 * The function is used to get list of hotel ids based on people id
 * @param pid
 * @return
 */
    public static ArrayList<Integer> getHotelIdsByPeopleId(int pid) {
        ArrayList<Integer> list = new ArrayList<Integer>();
        try {
            PreparedStatement exe = c.prepareStatement(
                    "select hotel_id from hotel_people_links where pid=?");
            exe.setInt(1, pid);
            ResultSet result = exe.executeQuery();
            if (result.next())
                list.add(result.getInt(1));
        } catch (Exception e) {
            System.out.println(e);
        }
        return list;
    }
/**
 * The method is used to get a list of all the people associated by the hotel 
 * @param hotel_id
 * @return
 */
    public static ArrayList<Integer> getPeopleIdsByHotel(int hotel_id) {
        ArrayList<Integer> list = new ArrayList<Integer>();
        try {
            PreparedStatement exe = c.prepareStatement(
                    "select pid from hotel_people_links where hotel_id=?");
            exe.setInt(1, hotel_id);
            ResultSet result = exe.executeQuery();
            if (result.next())
                list.add(result.getInt(1));
        } catch (Exception e) {
            System.out.println(e);
        }
        return list;
    }

}
