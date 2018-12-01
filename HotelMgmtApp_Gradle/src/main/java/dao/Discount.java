package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

public class Discount {
    private static Connection c = null;
    private static String[] strings = { "Billing Type (*)", "Discount [%]" };
    public static Vector<String> COLUMNS = new Vector<String>(Arrays.asList(strings));

    public static void setConnnection(Connection conn) {
        c = conn;
    }
/**
 * The method is used to add discount for  corresponding billing_type 
 * @param billing_type
 * @param discount
 * @return
 */
    public boolean addDiscount(String billing_type, int discount) {
        try {
            PreparedStatement exe = c.prepareStatement(
                    "insert into discount(billing_type,discount) values(?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            exe.setString(1, billing_type);
            exe.setInt(2, discount);
            exe.executeQuery();

        } catch (Exception e) {
            System.out.println(e);
            return false;
        }
        return true;
    }

    public Vector<Vector<Object>> getDiscountDetails() {
        Vector<Vector<Object>> data = null;
        try {

            PreparedStatement exe = c.prepareStatement("Select * from discount");
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
/**
 * The function is to used to delete discount entry from discount table
 * @param billing_type
 * @return
 */
    public boolean deleteDiscount(String billing_type) {
        try {
            PreparedStatement exe = c.prepareStatement(
                    " Delete from discount where billing_type=?");
            exe.setString(1, billing_type);
            exe.executeQuery();

        } catch (Exception e) {
            System.out.println(e);
            return false;
        }
        return true;
    }
    /**
     * The method is used to generate billing types for front desk view
     * @return
     */
    public static List<String> getBillingTypes(){
        List<String> billTypes = new ArrayList<String>();

        try {
            PreparedStatement exe = c.prepareStatement(
                    " Select billing_type from discount");
            
            ResultSet result =exe.executeQuery();
            while(result.next()) {
                billTypes.add(result.getString(1));
            }
           

        } catch (Exception e) {
            System.out.println(e);
            
        }
        
        return billTypes;
        
    }
  
   
}
