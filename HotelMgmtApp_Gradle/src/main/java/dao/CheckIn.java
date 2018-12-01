package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

public class CheckIn {
    private static Connection c = null;

    public static void setConnnection(Connection conn) {
        c = conn;
    }
/**
 * The function is used to generate checkin id 
 * @param pid
 * @param guests
 * @param checkin
 * @param checkout
 * @return
 * @throws SQLException
 */
    public int checkIn(int pid, int guests, Timestamp checkin, Timestamp checkout) throws SQLException {
        int cid = 0;
        PreparedStatement exe = c.prepareStatement("insert into checkin(pid,guests,checkin,checkout) values(?, ?,?,?)",
                Statement.RETURN_GENERATED_KEYS);
        exe.setInt(1, pid);
        exe.setInt(2, guests);
        exe.setTimestamp(3, checkin);
        exe.setTimestamp(4, checkout);

        exe.executeQuery();
        ResultSet result = exe.getGeneratedKeys();
        if (result.next())
            cid = result.getInt(1);

        return cid;
    }
/**
 * The function is used to update room parameters
 * @param hid
 * @param room_num
 * @return
 */
    public boolean updateRoomAfterCheckIn(int hid, int room_num) {
        try {
            PreparedStatement exe = c
                    .prepareStatement("update room set availability='unavailable' where room_num=? and hotel_id=?");
            exe.setInt(1, room_num);
            exe.setInt(2, hid);
            exe.executeQuery();

        } catch (Exception e) {
            System.out.println(e);
            return false;
        }
        return true;
    }
/**
 * The function is used to update checkout time when the customer checks out a particular room
 * @param cid
 * @return
 */
    public static boolean updateCheckOutTime(int cid) {
        try {
            PreparedStatement exe = c.prepareStatement("update checkin set checkout =? where cid=?");
            java.util.Date today = new java.util.Date();

            exe.setTimestamp(1, new java.sql.Timestamp(today.getTime()));
            exe.setInt(2, cid);
            exe.executeQuery();
        } catch (Exception e) {
            System.out.println(e);
            return false;
        }
        return true;
    }
/**
 * The duration of the stay is generated based on Checkin id
 * @param tempCID
 * @return
 */
    public static int durationUsingCID(int tempCID) {
        try {
            PreparedStatement exe = c.prepareStatement("select * from checkin where cid=? ");
            exe.setInt(1, (tempCID));
            ResultSet result = exe.executeQuery();
            if (result.next()) {

                java.sql.Timestamp tempcheckin = result.getTimestamp("checkin");
                java.util.Date today = new java.util.Date();
                java.sql.Timestamp tempcheckout = new java.sql.Timestamp(today.getTime());

                return (int) ((tempcheckout.getTime() - tempcheckin.getTime()) / (1000 * 24 * 60 * 60));
            }

        } catch (Exception e) {
            System.out.println(e);
        }
        return 0;
    }
}
