package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.json.JSONException;
import org.json.JSONObject;

public class FrontDesk extends Staff {
    private static Connection c = null;

    public static void setConnnection(Connection conn) {
        c = conn;
    }
/**
 * The function is used to add person to the front_desk table
 */
    public int addPerson(JSONObject obj1) throws JSONException, SQLException {
        PreparedStatement exe = c.prepareStatement(
                "insert into front_desk(pid, gender) values(?, ?)");
        exe.setInt(1, obj1.getInt("pid"));
        exe.setString(2, obj1.getString("gender"));
        exe.executeQuery();
        return obj1.getInt("pid");

    }
/**
 * The function is used to delete the entry from front desk table
 */
    public boolean deletePerson(int pid) {

        try {
            PreparedStatement exe = c.prepareStatement(" Delete from front_desk where pid=?");
            exe.setInt(1, pid);

            exe.executeQuery();

        } catch (Exception e) {
            System.out.println(e);
            return false;
        }
        return true;
    }
/**
 * The function is used to update the front desk representative parameters
 * @param obj2
 * @param pid
 * @return
 */
    public boolean updatePerson(JSONObject obj2, int pid) {

        try {

            PreparedStatement exe = c.prepareStatement(
                    "update front_desk set gender=? where pid =?");

            exe.setString(1, obj2.getString("gender"));

            exe.setInt(2, pid);

            exe.executeQuery();

        } catch (Exception e) {
            System.out.println(e);
            return false;
        }
        return true;
    }

}
