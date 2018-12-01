package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;

import org.json.JSONObject;
/**
 * The class 
 * @author kshittiz
 *
 */
public class Manager extends Staff {
    private static Connection c = null;

    public static void setConnnection(Connection conn) {
        c = conn;
    }

    public int addPerson(JSONObject obj1) throws Exception {
        PreparedStatement exe = c.prepareStatement(
                "insert into manager(pid, privilege) values(?,?)");
        exe.setInt(1, obj1.getInt("pid"));
        exe.setString(2, obj1.getString("privilege"));
        exe.executeQuery();
        return obj1.getInt("pid");
    }

    public boolean deletePerson(int pid) {
        try {
            PreparedStatement exe = c.prepareStatement(" Delete from manager where pid=?");
            exe.setInt(1, pid);

            exe.executeQuery();

        } catch (Exception e) {
            System.out.println(e);
            return false;
        }
        return true;

    }

    public boolean updatePerson(JSONObject obj2, int pid) {

        try {

            PreparedStatement exe = c.prepareStatement(
                    "update manager set privilege=? where pid =?");

            exe.setString(1, obj2.getString("privilege"));

            exe.setInt(2, pid);

            exe.executeQuery();

        } catch (Exception e) {
            System.out.println(e);
            return false;
        }
        return true;
    }
}
