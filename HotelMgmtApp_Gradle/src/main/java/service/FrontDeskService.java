package service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.json.JSONObject;

import dao.Billing;
import dao.CheckIn;
import dao.CheckInAttributes;
import dao.ContactInfo;
import dao.ContactLinks;
import dao.Customer;
import dao.Database;
import dao.Discount;
import dao.FrontDeskCheckInLinks;
import dao.HotelCheckInLinks;
import dao.HotelPeopleLinks;
import dao.People;
import dao.Room;
import dao.RoomCategory;
import dao.RoomServiceLinks;
import dao.Service;
import dao.ServiceType;
import view.LoginHMS;
import view.UpdateCustomer;

/**
 * This class is the service layer that interacts with the view and model
 * 
 * @author vidhisha
 *
 */
public class FrontDeskService {
    public static int room_alloted;

    public static int DEFAULT_TAX = 5; // Setting default tax value as 5

    /**
     * 
     * @param ssn
     * @return string
     */
    public static String getNameLinkedwithSSN(String ssn) {
        String name = null;
        Connection c = Database.getConnection();
        try {
            PreparedStatement exe = c.prepareStatement(
                    "SELECT name from people natural join front_desk where ssn = ?");
            exe.setString(1, ssn);
            ResultSet result = exe.executeQuery();
            if (result.next())
                name = result.getString(1);
            c.close();
        } catch (Exception e) {
            System.out.println(e);
        }
        return name;
    }

    /**
     * 
     * @param hotel_id
     * @return
     */
    public static HashMap<Integer, String> getAllPeople(int hotel_id) {

        HashMap<Integer, String> map = new HashMap<>();

        Connection c = Database.getConnection();
        try {
            PreparedStatement exe = c.prepareStatement(
                    "SELECT pid,name from people natural join hotel_people_links where hotel_id = ?");
            exe.setInt(1, hotel_id);
            ResultSet result = exe.executeQuery();
            while (result.next()) {
                map.put(result.getInt(1), result.getString(2));
            }

            c.close();
        } catch (Exception e) {
            System.out.println(e);
        }

        return map;
    }

    /**
     * This method is used to get lists of payment from Discount DAO class
     * 
     * @return List<String>
     */

    public static List<String> getListOfPayment() { // Obtaining
                                                    // billingTypesList from
                                                    // Discount
                                                    // table
        Connection c = Database.getConnection();
        Discount.setConnnection(c);
        List<String> returnPaymentList = Discount.getBillingTypes();
        return returnPaymentList;
    }

    /**
     * This function is used to return the string to be displayed on the dialog
     * box, it calculates the total amount based on the room number
     * 
     * @param room_num
     * @param Discount
     * @param Billing_Type
     * @param tax
     * @param billingadress
     * @return amount
     * @throws NumberFormatException
     * @throws SQLException
     */
    public static String calculateAmount(String room_num, String Discount, String Billing_Type,
            String tax, String billingadress) throws NumberFormatException, SQLException {
        // Initializing the connection
        Connection c = Database.getConnection();

        try {
            // Initializing auto commit for transaction
            c.setAutoCommit(false);

            // Initializing variables from input data
            int amount = 0;
            int temphid = LoginHMS.hid;
            int tempRoomNo = Integer.parseInt(room_num);
            int finalDiscount = 0;
            if (!Discount.isEmpty()) {
                finalDiscount = Integer.parseInt(Discount);
            }
            if (!tax.isEmpty()) { // Changing default value of tax if the state
                                  // has a different tax
                DEFAULT_TAX = Integer.parseInt(tax);
            }

            // Setting the variable values obtained from different classes
            Billing.setConnnection(c);
            int intialDiscount = Billing.discountOnBillType(Billing_Type); // Get
                                                                           // discount
                                                                           // based
                                                                           // on
                                                                           // payment
                                                                           // method
            CheckInAttributes.setConnnection(c);
            int tempCID = CheckInAttributes.cidUsingHidRoom_Num(temphid, tempRoomNo); // Get
                                                                                      // checkInAttributes
            CheckIn.setConnnection(c);
            int duration = CheckIn.durationUsingCID(tempCID);// Get total
                                                             // duration spend
                                                             // by customer
                                                             // in particular
                                                             // room
            if (duration == 0)
                duration = 1;
            Room.setConnnection(c);
            String temproom_category = Room.roomCat(tempRoomNo, temphid);// Get
                                                                         // room_category
            int tempoccupancy = Room.roomOccupancy(tempRoomNo, temphid);// Get
                                                                        // room
                                                                        // occupancy
            RoomCategory.setConnnection(c);
            int tempNightlyRate = RoomCategory.nightlyRate(temphid, temproom_category,
                    tempoccupancy);// Get nightly rate for the room at the given
                                   // hotel

            ServiceType.setConnnection(c);
            int serviceAmount = ServiceType.getServiceAmount(temphid, tempRoomNo); // Obtain
                                                                                   // total
                                                                                   // service
                                                                                   // charges
                                                                                   // levied
                                                                                   // on
                                                                                   // the
                                                                                   // room

            List<String> servicesUsed = new ArrayList<String>();
            servicesUsed = ServiceType.getServicesUsed(temphid, tempRoomNo); // obtain
                                                                             // the
                                                                             // list
                                                                             // of
                                                                             // services
                                                                             // used
                                                                             // by
                                                                             // the
                                                                             // room

            RoomServiceLinks.setConnnection(c);

            Service.setConnnection(c);

            // Calculating the total amount
            amount = amount + tempNightlyRate * duration;
            amount = amount + serviceAmount;
            amount = amount - amount * ((finalDiscount + intialDiscount) / 100);
            amount = amount + amount * DEFAULT_TAX / 100;

            // Makes changes in databases affected by room checkout
            Billing.setConnnection(c);

            Billing.addBilling(tempCID, finalDiscount, amount, DEFAULT_TAX, billingadress,
                    Billing_Type); // adding entry for the particular room to
                                   // billing
                                   // table
            Room.setConnnection(c);
            Room.updateRoomAvailbility("available", tempRoomNo); // changing
                                                                 // availability
                                                                 // of room on
                                                                 // checkout
            CheckIn.setConnnection(c);
            CheckIn.updateCheckOutTime(tempCID); // updating checkout time
            RoomServiceLinks.setConnnection(c);
            RoomServiceLinks.deleteServiceLinks(temphid, tempRoomNo); // Removing
                                                                      // services
            // records used by customer
            String finalString = "The Total number of days occupied by the customer is : "
                    + duration;
            finalString = finalString + "\nThe category of room occupied by the customer is : "
                    + temproom_category;
            finalString = finalString + "\n";
            if (serviceAmount != 0) {
                finalString = finalString + "\nThe services used by the room is :-" + "\n";
                for (int i = 0; i < servicesUsed.size(); i++) {
                    finalString = finalString + servicesUsed.get(i) + " ";

                }
            }
            finalString = finalString + "\n\n\nThe total amount levied on the room is: " + Integer
                    .toString(amount);

            // Committing transaction
            c.commit();

            return finalString;
        } catch (Exception e) {
            try {
                // In case of any exception in entering data in tables above,
                // transaction is rolled back
                c.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
            return "";
        } finally {
            Database.endConnnection(c);
        }

    }

    /**
     * function where we check if a person with a particular ssn is present in
     * the database or not
     * 
     * @param ssn
     * @return boolean
     */
    public static boolean checkIfPersonPresent(String ssn) {
        int count = 0;
        Connection c = Database.getConnection();
        try {
            PreparedStatement exe = c.prepareStatement("SELECT count(*) from people where ssn = ?");
            exe.setString(1, ssn);
            ResultSet result = exe.executeQuery();
            if (result.next())
                count = result.getInt(1);
            c.close();
        } catch (Exception e) {
            System.out.println(e);
        }
        if (count == 0)
            return false;
        else
            return true;
    }

    /**
     * function to add new customers via a transaction
     * 
     * @param obj
     * @return boolean
     */
    public static boolean addNewCustomer(JSONObject obj) {
        Connection c = Database.getConnection();
        try {
            // staring a transaction to add values in people hierarchy
            c.setAutoCommit(false);
            People.setConnnection(c);
            People p = new People();
            int pid = p.addPerson(obj);
            obj.put("pid", pid);

            Customer.setConnnection(c);
            p = new Customer();
            p.addPerson(obj);

            // setting hotel_people_links
            HotelPeopleLinks.setConnnection(c);
            HotelPeopleLinks hpl = new HotelPeopleLinks();
            hpl.addHotelPeopleLinks(obj.getInt("hotel_serving"), pid);

            // setting contact_info
            ContactInfo.setConnnection(c);
            ContactInfo info = new ContactInfo();
            String phone = obj.optString("phone");
            String email = obj.optString("email");
            int contact_id = info.addContactInfo(phone, email);

            // setting contact links
            ContactLinks.setConnnection(c);
            ContactLinks link = new ContactLinks();
            link.CreateContactLinks(pid, contact_id, "people");

            // Committing transaction
            c.commit();
            // transaction ends
            return true;

        } catch (Exception e) {
            try {
                // In case of any exception in entering data in tables above,
                // transaction is rolled back
                c.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
            return false;
        } finally {
            Database.endConnnection(c);
        }
    }

    /**
     * function to check if a room is available or not
     * 
     * @param hid
     * @param numguest
     * @param category
     * @return HashMap<Integer, String>
     */
    public static HashMap<Integer, String> checkRoomAvailable(int hid, int numguest,
            String category) {
        String availability = "unavailable";
        HashMap<Integer, String> map = new HashMap<Integer, String>();

        Connection c = Database.getConnection();
        try {
            PreparedStatement exe = c.prepareStatement(
                    "SELECT room_num,availability from room where hotel_id=? and occupancy=? and room_category=? and availability='available'");
            exe.setInt(1, hid);
            exe.setInt(2, numguest);
            exe.setString(3, category);
            ResultSet result = exe.executeQuery();
            if (result.next()) {
                availability = result.getString(2);
                map.put(result.getInt(1), result.getString(2));
            }
            c.close();
        } catch (Exception e) {
            System.out.println(e);
        }

        if (availability.equals("available"))
            return map;
        else
            return null;

    }

    /**
     * Check in customer using transaction mechanism
     * 
     * @param obj
     * @return boolean
     */
    public static boolean addNewCheckIn(JSONObject obj) {
        Connection c = Database.getConnection();
        try {
            // staring a transaction to add values in people hierarchy
            c.setAutoCommit(false);
            // setting connection with check in
            CheckIn.setConnnection(c);
            CheckIn checkin = new CheckIn();
            int cid = checkin.checkIn(obj.getInt("pid"), obj.getInt("guests"), (Timestamp) obj.get(
                    "checkin"), (Timestamp) obj.get("checkout"));

            checkin.updateRoomAfterCheckIn(LoginHMS.hid, obj.getInt("room_num"));

            // setting connection with hotel_checkin_links
            HotelCheckInLinks.setConnnection(c);
            HotelCheckInLinks h = new HotelCheckInLinks();
            // adding entry in hotel_checkin_links
            h.addHotelCheckInLinks(LoginHMS.hid, cid);

            // setting connection with frontdesk_checkin_links
            FrontDeskCheckInLinks.setConnnection(c);
            FrontDeskCheckInLinks f = new FrontDeskCheckInLinks();
            // adding entry in frontdesk_checkin_links
            f.addFrontDeskCheckInLinks(LoginHMS.pid, cid);

            // setting connection with checkin_attributes
            CheckInAttributes.setConnnection(c);
            CheckInAttributes checkin_attr = new CheckInAttributes();
            // adding entry in checkin_attributes
            checkin_attr.addCheckInAttributes(cid, LoginHMS.hid, obj.getInt("room_num"));

            // setting connection with service
            Service.setConnnection(c);
            // setting connection with room_service_links
            RoomServiceLinks.setConnnection(c);
            Service s = new Service();
            RoomServiceLinks r = new RoomServiceLinks();

            if (obj.getString("category").contains("Presidential")) {
                int room_service = s.getservicenum("room service", LoginHMS.hid);
                int catering = s.getservicenum("catering", LoginHMS.hid);

                int staff_id = s.getStaffServing(LoginHMS.hid, "catering");
                // adding services based on Presidential suite
                r.addRoomServiceLinks(obj.getInt("room_num"), room_service, LoginHMS.hid,
                        LoginHMS.pid);
                r.addRoomServiceLinks(obj.getInt("room_num"), catering, LoginHMS.hid, staff_id);

            }

            // Committing transaction
            c.commit();

            room_alloted = obj.getInt("room_num");
            // transaction ends
            return true;

        } catch (Exception e) {
            try {
                // In case of any exception in entering data in tables above,
                // transaction is rolled back
                c.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
            return false;
        } finally {
            // ending connection with database
            Database.endConnnection(c);
        }
    }

    /**
     * function to request a new service for a room
     * 
     * @param obj
     * @return boolean
     */
    public static boolean requestNewService(JSONObject obj) {
        Connection c = Database.getConnection();

        try {
            // staring a transaction to add values in people hierarchy
            c.setAutoCommit(false);

            RoomServiceLinks.setConnnection(c);
            RoomServiceLinks roomservice = new RoomServiceLinks();
            roomservice.addRoomServiceLinks(obj.getInt("room_num"), obj.getInt("service_num"), obj
                    .getInt("hotel_id"), obj.getInt("staff_id"));

            // Committing transaction
            c.commit();
            // transaction ends
            return true;

        } catch (Exception e) {
            try {
                c.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
            return false;

        } finally {
            Database.endConnnection(c);
        }

    }

    /**
     * this function is used to update the customer
     * 
     * @param obj
     * @return boolean
     */
    public static boolean updateCustomer(JSONObject obj) {
        Connection c = Database.getConnection();
        try {
            // staring a transaction to add values in people hierarchy
            c.setAutoCommit(false);
            People.setConnnection(c);
            People p = new People();
            int pid = People.getPIDbySSN(obj.getString("original_ssn"));

            // if (!obj.getString("name").isEmpty())
            // p.fdupdatePerson(obj.getString("name"), pid);

            HashMap<String, String> map_people = new HashMap<String, String>();

            if (!obj.getString("name").isEmpty())
                map_people.put("name", obj.getString("name"));

            if (!obj.getString("address").isEmpty())
                map_people.put("address", obj.getString("address"));

            p.updatePerson(map_people, pid);

            Customer.setConnnection(c);
            Customer cust = new Customer();

            if (!obj.getString("date_of_birth").isEmpty()) {
                int count = cust.checkIfCustomerExists(pid);
                if (count == 0) {
                    JSONObject obj1 = new JSONObject();
                    obj1.put("pid", pid);
                    obj1.put("date_of_birth", obj.getString("date_of_birth"));
                    cust.addPerson(obj1);
                } else {
                    cust.updatePerson(obj.getString("date_of_birth"), pid);
                }
            }

            ContactInfo.setConnnection(c);
            ContactInfo info = new ContactInfo();
            int contactid = Integer.parseInt(UpdateCustomer.contactId.getText());

            String phone = obj.optString("phone");
            String email = obj.optString("email");

            if (contactid == 0) {
                contactid = info.addContactInfo(phone, email);
                ContactLinks.setConnnection(c);
                ContactLinks contact = new ContactLinks();
                contact.CreateContactLinks(pid, contactid, "people");

            } else {
                HashMap<String, String> map = new HashMap<String, String>();

                map.put("phone_number", phone);
                map.put("email", email);
                info.updateContactDetails(map, contactid);
            }

            // Committing transaction
            c.commit();
            // transaction ends
            return true;

        } catch (Exception e) {
            try {
                c.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
            return false;
        } finally {
            Database.endConnnection(c);
        }
    }

    /**
     * this function is used to get the contact details of a particular person
     * 
     * @param ssn
     * @return Vector<Vector<Object>>
     */
    public static Vector<Vector<Object>> getContactDetails(String ssn) {
        Vector<Vector<Object>> contacts;
        Connection c = Database.getConnection();
        People.setConnnection(c);
        int pid = People.getPIDbySSN(ssn);
        ContactInfo.setConnnection(c);
        ContactInfo ci = new ContactInfo();
        try {
            contacts = ci.getContactDetails(pid, "people");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return contacts;

    }

    /**
     * this function calls into the DAO layer to get the staff who served what
     * services to which room
     * 
     * @param room_num
     * @return Vector<Vector<Object>>
     */
    public static Vector<Vector<Object>> getRoomServicesOfferedByStaff(String room_num) {
        Connection c = Database.getConnection();
        RoomServiceLinks.setConnnection(c);
        RoomServiceLinks r = new RoomServiceLinks();
        Vector<Vector<Object>> data = r.getRoomServicesOfferedByStaff(Integer.parseInt(room_num),
                LoginHMS.hid);
        Database.endConnnection(c);
        return data;
    }

    public static Vector<Vector<Object>> getCategory(int hid) {
        Connection c = Database.getConnection();
        RoomCategory.setConnnection(c);
        RoomCategory r = new RoomCategory();
        Vector<Vector<Object>> data = r.getCategory(hid);
        Database.endConnnection(c);
        return data;
    }

}
