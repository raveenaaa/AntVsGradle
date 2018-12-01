package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Billing {
    

    private static Connection c = null;

    public static void setConnnection(Connection conn) {
       c = conn;
    }
/**
 * The function is used to add billing record to the billing table
 * @param cid
 * @param amount
 * @param extra_discount
 * @param tax
 * @param billing_address
 * @param billing_type
 * @return
 * @throws SQLException
 */
    public static  int addBilling(int cid, int amount, int extra_discount, int tax, String billing_address,
            String billing_type) throws SQLException {
        
        int invoice_id = 0;
        PreparedStatement exe = c.prepareStatement(
                "insert into billing(cid,amount,extra_discount,tax,billing_address,billing_type) values(?,?,?,?,?,?)",
                Statement.RETURN_GENERATED_KEYS);
        exe.setInt(1, cid);
        exe.setInt(2, amount);
        exe.setInt(3, extra_discount);
        exe.setInt(4, tax);
        exe.setString(5, billing_address);
        exe.setString(6, billing_type);

        exe.executeQuery();
        
        ResultSet result = exe.getGeneratedKeys();
        if (result.next())
            invoice_id = result.getInt(1);

        return invoice_id;
    }
    /**
     * The function is used to generate based on billing type
     * @param Billing_Type
     * @return
     */
    public static int discountOnBillType(String Billing_Type) {
        try {
            PreparedStatement exe = c
                    .prepareStatement("select discount from discount where billing_type=? ");
            exe.setString(1, Billing_Type);
            ResultSet result = exe.executeQuery();
            if (result.next()) {
                return result.getInt("discount");

            }

        } catch (Exception e) {
            System.out.println(e);
        }
        return 0;
    }
   

}
