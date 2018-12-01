package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;

import org.json.JSONObject;

public class Catering extends Staff {
    private static Connection c = null;

    public static void setConnnection(Connection conn) {
        c = conn;
    }
/**
 * The function is used to add person to the People tables using json packet
 */
    public int addPerson(JSONObject obj1) throws Exception {
        PreparedStatement exe = c.prepareStatement(
                "insert into  catering_staff(pid, skill) values(?, ?)");
        exe.setInt(1, obj1.getInt("pid"));
        exe.setString(2, obj1.getString("skill"));
        exe.executeQuery();
        return obj1.getInt("pid");
    }
/**
 * The function is used to delete person from the people table
 */
    public boolean deletePerson(int pid) {

        try {
            PreparedStatement exe = c.prepareStatement(" Delete from catering_staff where pid=?");
            exe.setInt(1, pid);

            exe.executeQuery();

        } catch (Exception e) {
            System.out.println(e);
            return false;
        }
        return true;

    }
/**
 * The function is used the update person object in people table
 * @param obj2
 * @param pid
 * @return
 */
    public boolean updatePerson(JSONObject obj2, int pid) {
        try {

            PreparedStatement exe = c.prepareStatement(
                    "update catering_staff set skill=? where pid =?");

            exe.setString(1, obj2.getString("skill"));

            exe.setInt(2, pid);

            exe.executeQuery();

        } catch (Exception e) {
            System.out.println(e);
            return false;
        }
        return true;
    }

}
