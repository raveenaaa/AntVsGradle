package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

public class ServiceType {
    private static Connection c = null;
    private static String[] strings = { "Service Type (*)", "Price [$]" };
    public static Vector<String> COLUMNS = new Vector<String>(Arrays.asList(strings));

    public static void setConnnection(Connection conn) {
        c = conn;
    }

    public boolean addServiceType(String type, int price) {

        try {
            PreparedStatement exe = c
                    .prepareStatement("insert into service_type(type, price) values(?, ?)");
            exe.setString(1, type);
            exe.setInt(2, price);

            exe.executeQuery();

        } catch (Exception e) {
            System.out.println(e);
            return false;
        }
        return true;
    }

    public static String[] getServices() {
        ArrayList<String> list = new ArrayList<String>();
        try {
            PreparedStatement exe = c.prepareStatement("Select type from service_type");
            ResultSet result = exe.executeQuery();
            while (result.next())
                list.add(result.getString(1));
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
        return list.toArray(new String[list.size()]);
    }

    public Vector<Vector<Object>> getServiceTypeDetails() {
        Vector<Vector<Object>> data = null;
        try {

            PreparedStatement exe = c.prepareStatement("Select * from service_type");
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

    public boolean deleteServiceType(String type) {
        try {
            PreparedStatement exe = c.prepareStatement(" Delete from service_type where type=?");
            exe.setString(1, type);
            exe.executeQuery();

        } catch (Exception e) {
            System.out.println(e);
            return false;
        }
        return true;
    }

    public static int getServiceAmount(int hotel_id, int room_num) {
        try

        {

            PreparedStatement exe = c.prepareStatement(
                    "select SUM(price) from room_service_links natural join service natural join service_type where room_num=? and hotel_id=?");
            exe.setInt(1, (room_num));
            exe.setInt(2, (hotel_id));
            ResultSet result = exe.executeQuery();
            if (result.next()) {
                int tempServiceAmount = result.getInt(1); //Obtaining total service cost levied on customer
                
                return tempServiceAmount;           
                // check if the amount is null or has a value

            }

        } catch (Exception e) {
            System.out.println(e);
        }

        return 0;
    }
/**
 * The method is used to generate the services used by the room
 * @param temphid
 * @param tempRoomNo
 * @return
 */
    public static List<String> getServicesUsed(int temphid, int tempRoomNo) {
        List<String> tempServicesUsed = new ArrayList<String>();
        try

        {

            PreparedStatement exe = c.prepareStatement(
                    "select type,price from room_service_links natural join service natural join service_type where room_num=? and hotel_id=?");
            exe.setInt(1, (tempRoomNo));
            exe.setInt(2, (temphid));
            ResultSet result = exe.executeQuery();
            while (result.next()) {
                tempServicesUsed.add((result.getString(1)+" : "));// add services used by the room during
                                                          // stay
                tempServicesUsed.add("The price levied for this service is  " +(Integer.toString((result.getInt(2)))));// add prices for each
                                                                            //service
            }

            return tempServicesUsed;

        } catch (Exception e) {
            System.out.println(e);
        }

        return tempServicesUsed;
    }

}
