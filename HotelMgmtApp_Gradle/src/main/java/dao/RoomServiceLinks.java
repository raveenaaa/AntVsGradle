package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Vector;

public class RoomServiceLinks {
    private static Connection c = null;
    private static String[] services = { "Hotel ID", "Room num", " Staff member", "Job title",
            "Service provided" };
    public static Vector<String> ROOM_SERVICE_COLUMNS = new Vector<String>(Arrays.asList(services));

    public static void setConnnection(Connection conn) {
        c = conn;
    }

    public int addRoomServiceLinks(int room_num, int service_id, int hid, int pid)
            throws SQLException {
        int id = 0;
        PreparedStatement exe = c.prepareStatement(
                "insert into room_service_links(room_num,service_num,hotel_id_room, hotel_id_service,staff_id ) values(?, ?,?,?,?)",
                Statement.RETURN_GENERATED_KEYS);
        exe.setInt(1, room_num);
        exe.setInt(2, service_id);
        exe.setInt(3, hid);
        exe.setInt(4, hid);
        exe.setInt(5, pid);

        exe.executeQuery();
        ResultSet result = exe.getGeneratedKeys();
        if (result.next())
            id = result.getInt(1);

        return id;
    }

    public static int getServiceNumber(int temphid, int tempRoomNo) {
        try {
            PreparedStatement exe = c.prepareStatement(
                    "select service_num from room_service_links where hotel_id_room=? and room_num=?");
            exe.setInt(1, (temphid));
            exe.setInt(2, (tempRoomNo));
            ResultSet result = exe.executeQuery();
            if (result.next()) {
                return result.getInt("service_num");

            }

        } catch (Exception e) {
            System.out.println(e);
        }
        return 0;
    }

    public static boolean deleteServiceLinks(int hotel_id_room, int room_num) {
        try {
            PreparedStatement exe = c.prepareStatement(
                    "delete from room_service_links where hotel_id_room=? and  room_num=? ");
            exe.setInt(1, (hotel_id_room));
            exe.setInt(2, (room_num));
            ResultSet result = exe.executeQuery();
            if (result.next()) {
                return true;

            }

        } catch (Exception e) {
            System.out.println(e);
        }
        return false;

    }

    public Vector<Vector<Object>> getRoomServicesOfferedByStaff(int room_num, int hid) {
        Vector<Vector<Object>> data = null;
        try {
            PreparedStatement exe = c.prepareStatement(
                    "select hotel_id, room_num,name,job_title,service.type from room_service_links natural join service join people on staff_id=pid natural join staff where hotel_id_room = ? and room_num=? and hotel_id=?");
            exe.setInt(1, hid);
            exe.setInt(2, room_num);
            exe.setInt(3, hid);

            ResultSet result = exe.executeQuery();
            ResultSetMetaData metaData = result.getMetaData();
            int columnCount = metaData.getColumnCount();
            // Data of the table
            data = new Vector<Vector<Object>>();
            while (result.next()) {
                Vector<Object> vector = new Vector<Object>();
                for (int i = 1; i <= columnCount; i++) {
                    vector.add(result.getObject(i));
                }
                data.add(vector);
            }

        } catch (Exception e) {
            System.out.println(e);
        }

        return data;
    }
}
