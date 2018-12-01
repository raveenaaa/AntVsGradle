package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Vector;

import view.LoginHMS;

public class Room {
    private static Connection c = null;
    private static String[] strings = { "Room Number (*)", "Category", "Occupancy",
            "Availability" };
    public static Vector<String> COLUMNS = new Vector<String>(Arrays.asList(strings));

    private static String[] GRP_HOTELS = { "Room Count ", "Hotel Name", "Availability" };
    public static Vector<String> GRP_HOTELS_COLUMNS = new Vector<String>(Arrays.asList(GRP_HOTELS));

    private static String[] GRP_RTYPE = { "Room Count ", "Room Category", "Availability" };
    public static Vector<String> GRP_RTYPE_COLUMNS = new Vector<String>(Arrays.asList(GRP_RTYPE));

    private static String[] GRP_RCITY = { "Room Count ", "Address/City", "Availability" };
    public static Vector<String> GRP_RCITY_COLUMNS = new Vector<String>(Arrays.asList(GRP_RCITY));

    private static String[] GRP_RDATES = { "Room Count ", "Hotel Name", "Availability" };
    public static Vector<String> GRP_RDATES_COLUMNS = new Vector<String>(Arrays.asList(GRP_RDATES));

    private static String[] TOT_OCCUP = { "Room Count ", "Availability" };
    public static Vector<String> TOT_OCCUP_COLUMNS = new Vector<String>(Arrays.asList(TOT_OCCUP));

    private static String[] PER_OCCUP = { "Hotel Name", "Occupancy Percentage" };
    public static Vector<String> PER_OCCUP_COLUMNS = new Vector<String>(Arrays.asList(PER_OCCUP));

    public static Timestamp start, end;

    public static void setConnnection(Connection conn) {
        c = conn;
    }
    public static boolean updateRoomAvailbility(String newAvailability,int room_num) {
        try {
            PreparedStatement exe = c.prepareStatement(
                    "Update room set availability=? where room_num=?");
                  
            exe.setString(1, newAvailability);
            exe.setInt(2,room_num);
            exe.executeQuery();

        } catch (Exception e) {
            System.out.println(e);
            return false;
        }
        return true;
        
        
    }
    public boolean createRoom(int room_num, int hotel_id, String room_category, int occupancy,
            String availability) {

        try {
            PreparedStatement exe = c.prepareStatement(
                    "insert into room(room_num, hotel_id,room_category,occupancy,availability) values(?, ?,?,?,?)");
            exe.setInt(1, room_num);
            exe.setInt(2, hotel_id);
            exe.setString(3, room_category);
            exe.setInt(4, occupancy);
            exe.setString(5, availability);

            exe.executeQuery();

        } catch (Exception e) {
            System.out.println(e);
            return false;
        }
        return true;
    }

    public Vector<Vector<Object>> getRoomDetails(int hid) {
        Vector<Vector<Object>> data = null;
        try {

            PreparedStatement exe = c.prepareStatement(

                    "Select room_num, room_category,occupancy,availability from room where hotel_id = ?");
            exe.setInt(1, hid);
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

    public Vector<Vector<Object>> getOccupancyStats(String type, String city, int hid) {
        Vector<Vector<Object>> data = null;
        try {

            PreparedStatement exe = null;
            switch (type) {
            case "Occupancy group by all hotels":
                exe = c.prepareStatement(
                        " select count(hotel_id), hotel_name, availability from hotel natural join room where availability = 'available' group by hotel_id");
                break;
            case "Occupancy by room type":
                exe = c.prepareStatement(
                        " select count(hotel_id), room_category, availability from hotel natural join room where availability = 'available' and hotel_id=? group by room_category");

                exe.setInt(1, hid);
                break;
            case "Occupancy by city":
                exe = c.prepareStatement(
                        "select count(*), hotel_address, availability from hotel nautral join room where availability = 'available' and hotel_address like ? group by hotel_address like ?");
                exe.setString(1, "%" + city + "%");
                exe.setString(2, "%" + city + "%");
                break;
            case "Occupancy by dates":
                exe = c.prepareStatement(
                        "select count(*),availability from checkin natural join checkin_attributes natural join room where hotel_id=? and checkin BETWEEN ? and ? group by availability");
                exe.setInt(1, hid);
                exe.setTimestamp(2, start);
                exe.setTimestamp(3, end);
                break;
            case "Total Occupancy":
                exe = c.prepareStatement(
                        "select count(hotel_id), availability from hotel natural join room where hotel_id = ? group by availability");
                exe.setInt(1, hid);
                break;
            case "% of rooms occupied":
                exe = c.prepareStatement(
                        "select hotel_name, (count(*) * 100/(select count(*) from room where hotel_id = ?)) as percentage from room natural join hotel where hotel_id=? and availability = 'unavailable'");
                exe.setInt(1, hid);
                exe.setLong(2, hid);
                break;
            }

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

    public boolean getRoomAvailability(int room_num, int hid) {
        boolean result = false;
        try {
            String avail = "";
            PreparedStatement exe = c.prepareStatement(
                    " Select availability from room where room_num=? and hotel_id = ?");
            exe.setInt(1, room_num);
            exe.setInt(2, hid);
            ResultSet resultSet = exe.executeQuery();
            if (resultSet.next())
                avail = resultSet.getString(1);
            if ("available".equals(avail))
                result = true;

        } catch (Exception e) {
            System.out.println(e);
            return false;
        }
        return result;
    }

    public void updateRoom(HashMap<String, String> values, int room_num) throws Exception {
        String query = "UPDATE room set";
        if (values.containsKey("availability"))
            query = query + " availability= ?";

        query = query + " where room_num = ? and hotel_id= ?";
        PreparedStatement exe = c.prepareStatement(query);
        exe.setString(1, values.get("availability").toString());
        exe.setInt(2, room_num);
        exe.setInt(3, LoginHMS.hid);
        exe.executeQuery();
    }

    public boolean deleteRoom(int room_num, int hid) {

        try {
            PreparedStatement exe = c.prepareStatement(
                    " Delete from room where room_num=? and hotel_id = ?");
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
     * The room category is generated  using Room number and hotel id 
     * @param tempRoomNo
     * @param temphid
     * @return
     */
    public static String roomCat(int tempRoomNo,int temphid) {
    try {
        PreparedStatement exe = c
                .prepareStatement("select * from room where room_num=? and hotel_id=?");
        exe.setInt(1, (tempRoomNo));
        exe.setInt(2, (temphid));
     
        ResultSet result = exe.executeQuery();
        if (result.next()) {
            return result.getString("room_category");
           // tempoccupancy = result.getInt("occupancy");
        }
        

    } catch (Exception e) {
        System.out.println(e);
    
    }
    return "";
}
    /**
     * The function is used to generate the room occupancy based on room number and hotel id
     * @param tempRoomNo
     * @param temphid
     * @return
     */
    public static int roomOccupancy(int tempRoomNo,int temphid) {
        try {
            PreparedStatement exe = c
                    .prepareStatement("select * from room where room_num=? and hotel_id=?");
            exe.setInt(1, (tempRoomNo));
            exe.setInt(2, (temphid));
         
            ResultSet result = exe.executeQuery();
            if (result.next()) {
               return result.getInt("occupancy");
            }
            

        } catch (Exception e) {
            System.out.println(e);
        
        }
        return 0;
    }
    
}