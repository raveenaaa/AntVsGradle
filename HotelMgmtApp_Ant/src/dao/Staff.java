package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Vector;

import org.json.JSONObject;

//import com.sun.xml.internal.ws.org.objectweb.asm.Type;

public class Staff extends People {
    private static Connection c = null;
    private static String[] staff = { "ID (*)", "Name", "SSN", "Address", "Job Title", "Age",
            "Department" };
    public static Vector<String> STAFF_COLUMNS = new Vector<String>(Arrays.asList(staff));

    private static String[] colums = { "Count", "Department" };
    public static Vector<String> COLUMNS = new Vector<String>(Arrays.asList(colums));

    private static String[] managerStaff = { "ID(*)", "Name", "SSN", "Address", "Job Title", "Age",
            "Department", "Privilege" };
    public static Vector<String> MANAGER_STAFF_COLUMNS = new Vector<String>(Arrays.asList(
            managerStaff));

    private static String[] cateringStaff = { "ID(*)", "Name", "SSN", "Address", "Job Title", "Age",
            "Department", "Skill" };
    public static Vector<String> CATERING_STAFF_COLUMNS = new Vector<String>(Arrays.asList(
            cateringStaff));

    private static String[] cleaningStaff = { "ID(*)", "Name", "SSN", "Address", "Job Title", "Age",
            "Department", "Speciality" };

    public static Vector<String> CLEANING_STAFF_COLUMNS = new Vector<String>(Arrays.asList(
            cleaningStaff));

    private static String[] frontDeskStaff = { "ID(*)", "Name", "SSN", "Address", "Job Title",
            "Age", "Department", "Gender" };
    public static Vector<String> FRONT_DESK_Staff_COLUMNS = new Vector<String>(Arrays.asList(
            frontDeskStaff));

    public static void setConnnection(Connection conn) {
        c = conn;
    }

    public int addPerson(JSONObject obj1) throws Exception {
        PreparedStatement exe = c.prepareStatement(
                "insert into staff(pid, job_title, hotel_serving, age, department) values(?,?,?,?,?)",
                Statement.RETURN_GENERATED_KEYS);
        exe.setInt(1, obj1.getInt("pid"));
        exe.setString(2, obj1.getString("job_title"));
        exe.setInt(3, obj1.getInt("hotel_serving"));
        Integer age = (obj1.optInt("age") == 0) ? null : obj1.optInt("age");
        if (age != null)
            exe.setInt(4, age);
        else
            exe.setNull(4, 0);
        exe.setString(5, obj1.getString("department"));
        exe.executeQuery();

        return obj1.getInt("pid");
    }

    public boolean deletePerson(int pid) {

        try {
            PreparedStatement exe = c.prepareStatement(" Delete from staff where pid=?");
            exe.setInt(1, pid);

            exe.executeQuery();

        } catch (Exception e) {
            System.out.println(e);
            return false;
        }
        return true;
    }

    public void updatePerson(HashMap<String, String> values, int pid) throws Exception {
        String staffQuery = "UPDATE staff set";
        boolean check = false;
        if (values.containsKey("job_title")) {
            staffQuery = staffQuery + " job_title='" + values.get("job_title").toString() + "',";
            check = true;
        }

        if (values.containsKey("age")) {
            staffQuery = staffQuery + " age=" + values.get("age").toString() + ",";
            check = true;
        }

        String finalQueryStaff = staffQuery.subSequence(0, staffQuery.length() - 1).toString();

        finalQueryStaff = finalQueryStaff + " where pid =" + pid;
        if (check) {
            Statement exe = c.createStatement();
            exe.executeQuery(finalQueryStaff);
        }
    }

    public Vector<Vector<Object>> getStaffDetails(String staffType, int hid) {
        Vector<Vector<Object>> data = null;
        try {
            PreparedStatement exe = null;
            if ("manager".equals(staffType))
                exe = c.prepareStatement(
                        "Select pid,name,ssn, address,job_title,age,department,privilege from people natural join staff natural join manager where hotel_serving = ?");
            else if ("catering".equals(staffType))
                exe = c.prepareStatement(
                        "Select pid,name,ssn, address,job_title,age,department,skill from people natural join staff natural join catering_staff where hotel_serving = ?");
            else if ("cleaning".equals(staffType))
                exe = c.prepareStatement(
                        "Select pid,name,ssn, address,job_title,age,department,speciality from people natural join staff natural join cleaning_staff where hotel_serving = ?");
            else if ("front_desk".equals(staffType))
                exe = c.prepareStatement(
                        "Select pid,name,ssn, address,job_title,age,department,gender from people natural join staff natural join front_desk where hotel_serving = ?");
            else
                exe = c.prepareStatement(
                        "Select pid,name,ssn, address,job_title,age,department from people natural join staff where hotel_serving = ?");

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

    public Vector<Vector<Object>> getStaffDetailsGroupedByRole(int hid) {
        Vector<Vector<Object>> data = null;
        try {
            PreparedStatement exe = c.prepareStatement(
                    "Select count(pid),department from people natural join staff where hotel_serving = ? group by department ");
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

}
