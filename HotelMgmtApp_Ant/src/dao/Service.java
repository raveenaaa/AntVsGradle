package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Vector;

public class Service {
    private static Connection c = null;
    private static String[] strings = { "Service Number (*)", "Service Type" };
    public static Vector<String> COLUMNS = new Vector<String>(Arrays.asList(strings));

    public static void setConnnection(Connection conn) {
        c = conn;
    }

    public boolean addService(int service_num, int hotel_id, String type) {

        try {
            PreparedStatement exe = c.prepareStatement(
                    "insert into service(service_num, hotel_id,type) values(?, ?,?)");
            exe.setInt(1, service_num);
            exe.setInt(2, hotel_id);
            exe.setString(3, type);

            exe.executeQuery();

        } catch (Exception e) {
            System.out.println(e);
            return false;
        }
        return true;
    }

    public Vector<Vector<Object>> getServiceDetails(int hid) {
        Vector<Vector<Object>> data = null;
        try {

            PreparedStatement exe = c.prepareStatement(

                    "Select service_num, type from service where hotel_id = ?");
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
            //System.out.println(e);
        }

        return data;
    }

    public boolean deleteService(int service_num, int hid) {
        try {
            PreparedStatement exe = c.prepareStatement(
                    " Delete from service where service_num = ? and hotel_id=?");
            exe.setInt(1, service_num);
            exe.setInt(2, hid);
            exe.executeQuery();

        } catch (Exception e) {
            //System.out.println(e);
            return false;
        }
        return true;
    }

    public int getservicenum(String service, int hid) {
        int service_num = 0;
        try {
            PreparedStatement exe = c.prepareStatement(
                    "SELECT service_num from service where type=? and hotel_id=?");
            exe.setString(1, service);
            exe.setInt(2, hid);

            ResultSet result = exe.executeQuery();
            if (result.next())
                service_num = result.getInt(1);

        } catch (Exception e) {
            System.out.println(e);
        }
        return service_num;
    }

    public int getStaffServing(int hid, String title) {
        int pid = 0;
        try {
            PreparedStatement exe = c.prepareStatement(
                    "SELECT pid from staff where hotel_serving=? and department=?");
            exe.setInt(1, hid);
            exe.setString(2, title);
            // exe.executeQuery();

            ResultSet result = exe.executeQuery();
            if (result.next())
                pid = result.getInt(1);

        } catch (Exception e) {
            System.out.println(e);
        }
        return pid;
    }

    public static String getServiceType(int temphid, int tempServiceNum) {
        try {
            PreparedStatement exe = c.prepareStatement(
                    "select type from service where hotel_id=? and service_num=?");
            exe.setInt(1, (temphid));
            exe.setInt(2, (tempServiceNum));
            // System.out.println(temphid);
            ResultSet result = exe.executeQuery();
            if (result.next()) {
                return result.getString("type");
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return "";
    }

    public HashMap<String, Integer> getStaffIDs(int hid) {
        HashMap<String, Integer> map = new HashMap<String, Integer>();
        try {
            PreparedStatement exe = c.prepareStatement(
                    "SELECT pid,name,job_title,department from people natural join staff where hotel_serving=?");
            exe.setInt(1, hid);
            ResultSet result = exe.executeQuery();
            while (result.next()) {
                map.put(result.getString(2) + " | " + result.getString(3) + " | " + result
                        .getString(4), result.getInt(1));
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return map;
    }
}
