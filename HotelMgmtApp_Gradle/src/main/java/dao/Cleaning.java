package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;

import org.json.JSONObject;

public class Cleaning extends Staff {
    private static Connection c = null;

    public static void setConnnection(Connection conn) {
        c = conn;
    }
/**
 * The method is used to add cleaning staff member 
 */
    public int addPerson(JSONObject obj1) throws Exception {
        PreparedStatement exe = c.prepareStatement(
                "insert into cleaning_staff(pid, speciality) values(?, ?)");
        exe.setInt(1, obj1.getInt("pid"));
        exe.setString(2, obj1.getString("speciality"));
        exe.executeQuery();

        return obj1.getInt("pid");
    }
/**
 * The method is used to delete from the cleaning staff table
 */
    public boolean deletePerson(int pid) {

        try {
            PreparedStatement exe = c.prepareStatement(" Delete from cleaning_staff where pid=?");
            exe.setInt(1, pid);

            exe.executeQuery();

        } catch (Exception e) {
            System.out.println(e);
            return false;
        }
        return true;
    }
/**
 * The method is used to update person parameter in cleaning table
 * @param obj2
 * @param pid
 * @return
 */
    public boolean updatePerson(JSONObject obj2, int pid) {

        try {

            PreparedStatement exe = c.prepareStatement(
                    "update cleaning_staff set speciality=? where pid =?");

            exe.setString(1, obj2.getString("speciality"));

            exe.setInt(2, pid);

            exe.executeQuery();

        } catch (Exception e) {
            System.out.println(e);
            return false;
        }
        return true;
    }

}
