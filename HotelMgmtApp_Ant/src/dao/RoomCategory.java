package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;

public class RoomCategory {

    private int hid;
    private String room_category;
    private int occupancy;
    private int nightly_rate;
    private static Connection c = null;
    private static String[] strings = { "Room Category (*)", "Occupancy (*)", "Nightly Rate [$]" };
    public static Vector<String> COLUMNS = new Vector<String>(Arrays.asList(strings));

    public int getHid() {
        return hid;
    }

    public void setHid(int hid) {
        this.hid = hid;
    }

    public String getRoom_category() {
        return room_category;
    }

    public void setRoom_category(String room_category) {
        this.room_category = room_category;
    }

    public int getOccupancy() {
        return occupancy;
    }

    public void setOccupancy(int occupancy) {
        this.occupancy = occupancy;
    }

    public int getNightly_rate() {
        return nightly_rate;
    }

    public void setNightly_rate(int nightly_rate) {
        this.nightly_rate = nightly_rate;
    }

    public static void setConnnection(Connection conn) {
        c = conn;
    }

    public boolean createRoomCategory(int hid, String room_category, int occupancy,
            int nightly_rate) {

        try {
            PreparedStatement exe = c.prepareStatement(
                    "insert into room_category(hotel_id, room_category, occupancy,nightly_rate) values(?,?,?,?)");
            exe.setInt(1, hid);
            exe.setString(2, room_category);
            exe.setInt(3, occupancy);
            exe.setInt(4, nightly_rate);
            exe.executeQuery();
        } catch (Exception e) {
            System.out.println(e);
            return false;
        }
        return true;
    }

    public static ArrayList<RoomCategory> getRoomCategories(int hid) {
        ArrayList<RoomCategory> list = new ArrayList<RoomCategory>();
        try {
            RoomCategory rc;
            PreparedStatement exe = c.prepareStatement(
                    "Select room_category, occupancy from room_category where hotel_id = ?");
            exe.setInt(1, hid);
            ResultSet result = exe.executeQuery();
            while (result.next()) {
                rc = new RoomCategory();
                rc.setRoom_category(result.getString(1));
                rc.setOccupancy(result.getInt(2));
                list.add(rc);
            }
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
        return list;
    }
    
    public Vector<Vector<Object>> getRoomCategoriesForTable(int hid) {
        Vector<Vector<Object>> data = null;
        try {

            PreparedStatement exe = c.prepareStatement(
                    "Select room_category,occupancy,nightly_rate from room_category where hotel_id = ?");
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

    public boolean deleteRoomCategory(String category, int hid, int occup) {

        try {
            PreparedStatement exe = c.prepareStatement(
                    " Delete from room_category where room_category=? and hotel_id=? and occupancy=?");
            exe.setString(1, category);
            exe.setInt(2, hid);
            exe.setInt(3, occup);
            exe.executeQuery();

        } catch (Exception e) {
            System.out.println(e);
            return false;
        }
        return true;
    }
    /**
     * The function is used to generate the nightly rate 
     * @param temphid
     * @param temproom_category
     * @param tempoccupancy
     * @return
     */
    public static int nightlyRate(int temphid,String temproom_category,int tempoccupancy)
    {
        try

        {
            PreparedStatement exe = c.prepareStatement(
                    "select nightly_rate from room_category where hotel_id =? and room_category=? and occupancy =?");
            exe.setInt(1, (temphid));
            exe.setString(2, (temproom_category));
            exe.setInt(3, tempoccupancy);
           
            ResultSet result = exe.executeQuery();
            if (result.next()) {
                return result.getInt("nightly_rate");

            }
           

            

        }catch(
        Exception e)
        {
            System.out.println(e);
        }
        return 0;
    }
    public  Vector<Vector<Object>> getCategory(int hid)
    {
        Vector<Vector<Object>> data=null;
        try

        {
            PreparedStatement exe = c.prepareStatement(
                    "select room_category from room_category where hotel_id =?");
            exe.setInt(1, (hid));
            
            ResultSet result = exe.executeQuery();
            ResultSetMetaData metaData = result.getMetaData();
            int columnCount = metaData.getColumnCount();

            data = new Vector<Vector<Object>>();
            while (result.next()) {
                Vector<Object> vector = new Vector<Object>();
                for (int i = 1; i <= columnCount; i++) {
                    vector.add(result.getObject(i));
                }
                data.add(vector);
            }
           

            

        }catch(
        Exception e)
        {
            System.out.println(e);
        }
        return data;
    }
}
