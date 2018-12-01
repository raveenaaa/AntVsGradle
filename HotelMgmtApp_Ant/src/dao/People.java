package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

import org.json.JSONObject;

public class People {

    private static Connection c = null;

    public static void setConnnection(Connection conn) {
        c = conn;
    }

    public int addPerson(JSONObject obj1) throws Exception {
        int peopleId = 0;
        PreparedStatement exe = c.prepareStatement(
                "insert into people(name,ssn, address, type) values(?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS);
        exe.setString(1, obj1.getString("name"));
        exe.setInt(2, obj1.getInt("SSN"));
        exe.setString(3, obj1.getString("address"));
        exe.setString(4, obj1.getString("peopleType"));
        exe.executeQuery();
        ResultSet result = exe.getGeneratedKeys();
        if (result.next())
            peopleId = result.getInt(1);
        return peopleId;
    }

    public static String getTypeBySSN(String ssn) {
        String type = null;
        try {
            PreparedStatement exe = c.prepareStatement("SELECT type from people where ssn = ?");
            exe.setString(1, ssn);
            ResultSet result = exe.executeQuery();
            if (result.next())
                type = result.getString(1);
        } catch (Exception e) {
            System.out.println(e);
        }
        return type;
    }

    public static boolean checkSSNAvailability(String ssn) {
        boolean check = false;
        try {
            PreparedStatement exe = c.prepareStatement("SELECT name from people where ssn = ?");
            exe.setString(1, ssn);
            ResultSet result = exe.executeQuery();
            check = result.next();
        } catch (Exception e) {
            System.out.println(e);
        }
        return check;
    }

    public boolean deletePerson(int pid) {

        try {
            PreparedStatement exe = c.prepareStatement(" Delete from people where pid=?");
            exe.setInt(1, pid);

            exe.executeQuery();

        } catch (Exception e) {
            System.out.println(e);
            return false;
        }
        return true;
    }

    public void updatePerson(HashMap<String, String> values, int pid) throws Exception {
        String peopleQuery = "UPDATE people set";
        boolean check = false;
        if (values.containsKey("name")) {
            peopleQuery = peopleQuery + " name='" + values.get("name").toString() + "',";
            check = true;
        }
        if (values.containsKey("address")) {
            peopleQuery = peopleQuery + " address='" + values.get("address").toString() + "',";
            check = true;
        }

        String finalQueryPeople = peopleQuery.subSequence(0, peopleQuery.length() - 1).toString();

        finalQueryPeople = finalQueryPeople + " where pid =" + pid;
        if (check) {
            Statement exe = c.createStatement();
            exe.executeQuery(finalQueryPeople);
        }
    }
    public boolean fdupdatePerson(String name,int pid)
    {
        String peopleQuery = "UPDATE people set name='"+name+"' where pid="+pid;
        try {
            Statement exe = c.createStatement();
			exe.executeQuery(peopleQuery);
			return true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

    }

    public static int getPIDbySSN(String ssn) {
        int pid = 0;
        try {
            PreparedStatement exe = c.prepareStatement("SELECT pid from people where ssn =?");
            exe.setString(1, ssn);
            ResultSet result = exe.executeQuery();
            if(result.next())
                pid = result.getInt("pid");
        } catch (Exception e) {
            System.out.println(e);
        }

        return pid;
    }
   
   
   
}
