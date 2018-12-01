package service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Vector;

import org.json.JSONObject;

import dao.Catering;
import dao.Cleaning;
import dao.ContactInfo;
import dao.ContactLinks;
import dao.Customer;
import dao.Database;
import dao.Discount;
import dao.FrontDesk;
import dao.Hotel;
import dao.HotelPeopleLinks;
import dao.Manager;
import dao.People;
import dao.Room;
import dao.RoomCategory;
import dao.Service;
import dao.ServiceType;
import dao.Staff;
import view.LoginHMS;

/**
 * Service class for Manager operations
 * 
 * @author kshittiz
 *
 */
public class ManagerService {

    /**
     * This function returns name linked with SSN
     * 
     * @param ssn
     * @return
     */
    public static String getNameLinkedwithSSN(String ssn) {
        String name = null;
        Connection c = Database.getConnection();
        try {
            PreparedStatement exe = c.prepareStatement(
                    "SELECT name from people natural join manager where ssn = ?");
            exe.setString(1, ssn);
            ResultSet result = exe.executeQuery();
            if (result.next())

                name = result.getString(1);
            c.close();
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            Database.endConnnection(c);
        }
        return name;
    }

    // ADD Operations

    /**
     * Adding staff member in people hierarchy using transaction
     * 
     * @param JSONOBject
     *            obj
     * @return boolean
     */
    public static boolean addNewStaff(JSONObject obj) {
        Connection c = Database.getConnection();
        try {
            // staring a transaction to add values in people hierarchy
            c.setAutoCommit(false);
            // setting connection in people table
            People.setConnnection(c);
            People p = new People();
            // adding person in people table
            int pid = p.addPerson(obj);
            obj.put("pid", pid);
            // setting connection in staff table
            Staff.setConnnection(c);
            p = new Staff();
            // adding person in staff table
            p.addPerson(obj);

            // Selecting person department, setting connection with
            // corresponding class to add entry of person in the respective
            // table
            if ("manager".equals(obj.getString("department"))) {
                Manager.setConnnection(c);
                p = new Manager();
                p.addPerson(obj);
            } else if ("front_desk".equals(obj.getString("department"))) {
                FrontDesk.setConnnection(c);
                p = new FrontDesk();
                p.addPerson(obj);
            } else if ("catering".equals(obj.getString("department"))) {
                Catering.setConnnection(c);
                p = new Catering();
                p.addPerson(obj);
            } else {
                Cleaning.setConnnection(c);
                p = new Cleaning();
                p.addPerson(obj);
            }
            // setting hotel_people_links
            HotelPeopleLinks.setConnnection(c);
            HotelPeopleLinks hpl = new HotelPeopleLinks();
            hpl.addHotelPeopleLinks(obj.getInt("hotel_serving"), pid);

            // setting contact_info
            ContactInfo.setConnnection(c);
            ContactInfo info = new ContactInfo();
            String phone = obj.optString("phone").isEmpty() ? null : obj.optString("phone");
            String email = obj.optString("email").isEmpty() ? null : obj.optString("email");
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
            // Ending connection
            Database.endConnnection(c);
        }
    }

    /**
     * Adding contact details using transaction
     * 
     * @param id
     * @param phone_number
     * @param email
     * @param type
     * @return boolean
     */
    public static boolean addContactInfo(String id, String phone_number, String email,
            String type) {
        Connection c = Database.getConnection();
        try {
            // staring a transaction to add contact
            c.setAutoCommit(false);
            // setting connection
            ContactInfo.setConnnection(c);
            ContactInfo ci = new ContactInfo();
            // adding contact info
            int contact_id = ci.addContactInfo(phone_number, email);

            // setting connection with contact links
            ContactLinks.setConnnection(c);
            ContactLinks cl = new ContactLinks();

            // adding entry in contact links based on contact id generated above
            // and type of contact i.e. people/hotel
            if ("people".equals(type))
                cl.CreateContactLinks(Integer.parseInt(id), contact_id, "people");
            else
                cl.CreateContactLinks(Integer.parseInt(id), contact_id, "hotel");

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
     * This function add new room in the manager's hotel
     * 
     * @param roomN
     * @param hid
     * @param category
     * @param occup
     * @param avail
     * @return boolean
     */
    public static boolean addNewRoom(String roomN, int hid, String category, String occup,
            String avail) {
        boolean result = false;
        Connection conn = Database.getConnection();
        Room.setConnnection(conn);
        Room r = new Room();
        try {
            if (r.createRoom(Integer.parseInt(roomN), hid, category, Integer.parseInt(occup),
                    avail)) {
                result = true;
            } else {
                result = false;
            }
        } catch (Exception ex) {
            System.out.println(ex);
            result = false;
        } finally {
            Database.endConnnection(conn);
        }

        return result;

    }

    /**
     * This function add new room category in manager's hotel
     * 
     * @param hid
     * @param category
     * @param occup
     * @param rate
     * @return boolean
     */
    public static boolean addNewCategory(int hid, String category, int occup, String rate) {
        boolean result = false;
        Connection conn = Database.getConnection();
        RoomCategory.setConnnection(conn);
        RoomCategory rc = new RoomCategory();
        try {
            if (rc.createRoomCategory(hid, category, occup, Integer.parseInt(rate))) {
                result = true;
            } else {
                result = false;
            }
        } catch (Exception ex) {
            System.out.println(ex);
            result = false;
        } finally {
            Database.endConnnection(conn);
        }
        return result;

    }

    /**
     * This function add new service in the hotel
     * 
     * @param serviceN
     * @param hid
     * @param type
     * @return
     */
    public static boolean addNewService(String serviceN, int hid, String type) {
        boolean result = false;
        Connection conn = Database.getConnection();
        Service.setConnnection(conn);
        Service s = new Service();
        try {
            if (s.addService(Integer.parseInt(serviceN), hid, type)) {
                result = true;
            } else {
                result = false;
            }
        } catch (Exception ex) {
            System.out.println(ex);
            result = false;
        } finally {
            Database.endConnnection(conn);
        }
        return result;

    }

    /**
     * This function add new service type in hotel
     * 
     * @param type
     * @param price
     * @return boolean
     */
    public static boolean addNewServiceType(String type, String price) {
        boolean result = false;
        Connection conn = Database.getConnection();
        ServiceType.setConnnection(conn);
        ServiceType st = new ServiceType();
        try {
            if (st.addServiceType(type, Integer.parseInt(price))) {
                result = true;
            } else {
                result = false;
            }
        } catch (Exception ex) {
            System.out.println(ex);
            result = false;
        } finally {
            Database.endConnnection(conn);
        }

        return result;

    }

    /**
     * This function add new discount based on payment type in hotel chain
     * 
     * @param billing_type
     * @param disc
     * @return boolean
     */
    public static boolean addNewDiscount(String billing_type, String disc) {
        boolean result = false;
        Connection conn = Database.getConnection();
        Discount.setConnnection(conn);
        Discount d = new Discount();
        try {
            if (d.addDiscount(billing_type, Integer.parseInt(disc))) {
                result = true;
            } else {
                result = false;
            }
        } catch (Exception ex) {
            System.out.println(ex);
            result = false;
        } finally {
            Database.endConnnection(conn);
        }

        return result;

    }

    // FETCH Operations

    /**
     * This function return hotel details
     * 
     * @param hid
     * @return Vector<Vector<Object>>
     */
    public static Vector<Vector<Object>> getHotelDetails(int hid) {
        Connection c = Database.getConnection();
        Hotel.setConnnection(c);
        Hotel rc = new Hotel();
        Vector<Vector<Object>> data = rc.getHotelDetails(hid);
        Database.endConnnection(c);
        return data;
    }

    /**
     * This function return staff details of manager's hotel
     * 
     * @param hid
     * @return Vector<Vector<Object>>
     */
    public static Vector<Vector<Object>> getStaffDetails(String type) {
        Connection c = Database.getConnection();
        Staff.setConnnection(c);
        Staff s = new Staff();
        Vector<Vector<Object>> data = s.getStaffDetails(type, LoginHMS.hid);
        Database.endConnnection(c);
        return data;
    }

    /**
     * This function return staff details grouped by role/department
     * 
     * @param hid
     * @return Vector<Vector<Object>>
     */
    public static Vector<Vector<Object>> getStaffDetailsGroupedByRole() {
        Connection c = Database.getConnection();
        Staff.setConnnection(c);
        Staff s = new Staff();
        Vector<Vector<Object>> data = s.getStaffDetailsGroupedByRole(LoginHMS.hid);
        Database.endConnnection(c);
        return data;
    }

    /**
     * This function return customer details linked to the manager's hotel
     * 
     * @param hid
     * @return Vector<Vector<Object>>
     */
    public static Vector<Vector<Object>> getCustomerDetails() {
        Connection c = Database.getConnection();
        Customer.setConnnection(c);
        Customer s = new Customer();
        Vector<Vector<Object>> data = s.getCustomerDetails(LoginHMS.hid);
        Database.endConnnection(c);
        return data;
    }

    /**
     * This function return room details of manager's hotel
     * 
     * @param hid
     * @return Vector<Vector<Object>>
     */
    public static Vector<Vector<Object>> getRoomDetails() {
        Connection c = Database.getConnection();
        Room.setConnnection(c);
        Room r = new Room();
        Vector<Vector<Object>> data = r.getRoomDetails(LoginHMS.hid);
        Database.endConnnection(c);
        return data;
    }

    /**
     * This function return room category details of manager's hotel
     * 
     * @param hid
     * @return Vector<Vector<Object>>
     */
    public static Vector<Vector<Object>> getRoomCategoryDetails() {
        Connection c = Database.getConnection();
        RoomCategory.setConnnection(c);
        RoomCategory rc = new RoomCategory();
        Vector<Vector<Object>> data = rc.getRoomCategoriesForTable(LoginHMS.hid);
        Database.endConnnection(c);
        return data;
    }

    /**
     * This function return service details of manager's hotel
     * 
     * @param hid
     * @return Vector<Vector<Object>>
     */
    public static Vector<Vector<Object>> getServiceDetails() {
        Connection c = Database.getConnection();
        Service.setConnnection(c);
        Service s = new Service();
        Vector<Vector<Object>> data = s.getServiceDetails(LoginHMS.hid);
        Database.endConnnection(c);
        return data;
    }

    /**
     * This function return service type details of manager's hotel
     * 
     * @param hid
     * @return Vector<Vector<Object>>
     */
    public static Vector<Vector<Object>> getServiceTypeDetails() {
        Connection c = Database.getConnection();
        ServiceType.setConnnection(c);
        ServiceType s = new ServiceType();
        Vector<Vector<Object>> data = s.getServiceTypeDetails();
        Database.endConnnection(c);
        return data;
    }

    /**
     * This function return discount details in manager's hotel
     * 
     * @param hid
     * @return Vector<Vector<Object>>
     */
    public static Vector<Vector<Object>> getDiscountDetails() {
        Connection c = Database.getConnection();
        Discount.setConnnection(c);
        Discount d = new Discount();
        Vector<Vector<Object>> data = d.getDiscountDetails();
        Database.endConnnection(c);
        return data;
    }

    /**
     * This function return occupancy statistics
     * 
     * @param type
     * @param city
     * @return Vector<Vector<Object>>
     */
    public static Vector<Vector<Object>> getOccupancyStats(String type, String city) {
        Connection c = Database.getConnection();
        Room.setConnnection(c);
        Room r = new Room();
        Vector<Vector<Object>> data = r.getOccupancyStats(type, city, LoginHMS.hid);
        Database.endConnnection(c);
        return data;
    }

    /**
     * This function return contact details
     * 
     * @param type
     * @param id
     * @return Vector<Vector<Object>>
     */
    public static Vector<Vector<Object>> getContactDetails(String type, String id) {
        Connection c = Database.getConnection();
        Vector<Vector<Object>> data;
        try {
            ContactInfo.setConnnection(c);
            ContactInfo ci = new ContactInfo();
            data = ci.getContactDetails(Integer.parseInt(id), type);
        } catch (Exception e) {
            System.out.println(e);
            return null;
        } finally {
            Database.endConnnection(c);
        }
        return data;
    }

    /**
     * This function return revenue of manager's hotel
     * 
     * @param startDate
     * @param endDate
     * @return double
     */
    public static double getRevenue(Timestamp startDate, Timestamp endDate) {
        Connection c = Database.getConnection();
        Hotel.setConnnection(c);
        Hotel h = new Hotel();
        double revenue = h.getRevenue(startDate, endDate, LoginHMS.hid);
        Database.endConnnection(c);
        return revenue;
    }

    // UPDATE OPERATIONS
    /**
     * This function update staff details
     * 
     * @param values
     * @param pid
     * @return boolean
     */
    public static boolean updateStaff(HashMap<String, String> values, int pid) {
        Connection c = Database.getConnection();
        try {
            People.setConnnection(c); // updating fields in people
            Staff.setConnnection(c); // updating fields in staff

            People p = new People();
            p.updatePerson(values, pid);
            p = new Staff();
            p.updatePerson(values, pid);

        } catch (Exception e) {
            return false;
        } finally {
            Database.endConnnection(c);
        }
        return true;
    }

    /**
     * This function update room in manager's hotel
     * 
     * @param values
     * @param room_num
     * @return boolean
     */
    public static boolean updateRoom(HashMap<String, String> values, int room_num) {
        Connection c = Database.getConnection();
        try {
            Room.setConnnection(c);
            Room r = new Room();
            r.updateRoom(values, room_num);
        } catch (Exception e) {
            return false;
        } finally {
            Database.endConnnection(c);
        }
        return true;
    }

    /**
     * This function update manager's hotel details
     * 
     * @param values
     * @return boolean
     */
    public static boolean updateHotel(HashMap<String, String> values) {
        Connection c = Database.getConnection();
        try {
            Hotel.setConnnection(c);
            Hotel h = new Hotel();
            h.updateHotel(values);
        } catch (Exception e) {
            return false;
        } finally {
            Database.endConnnection(c);
        }
        return true;
    }

    /**
     * This function update contact details based on contact id
     * 
     * @param values
     * @param cid
     * @return boolean
     */
    public static boolean updateContact(HashMap<String, String> values, int cid) {
        Connection c = Database.getConnection();
        try {
            ContactInfo.setConnnection(c);
            ContactInfo ci = new ContactInfo();
            ci.updateContactDetails(values, cid);
        } catch (Exception e) {
            return false;
        } finally {
            Database.endConnnection(c);
        }
        return true;
    }

    // DELETE OPERATIONS
    /**
     * This function delete staff from manager's hotel
     * 
     * @param values
     * @return boolean
     */
    public static boolean deleteStaff(HashMap<String, String> values) {
        boolean result = false;
        Connection c = Database.getConnection();
        Staff.setConnnection(c);
        Staff p = new Staff();
        int pid = Integer.parseInt(values.get("ID (*)"));
        if (pid != LoginHMS.pid)
            result = p.deletePerson(pid);
        Database.endConnnection(c);
        return result;
    }

    /**
     * This function delete room in hotel
     * 
     * @param values
     * @return boolean
     */
    public static boolean deleteRoom(HashMap<String, String> values) {
        boolean result = false;
        Connection c = Database.getConnection();
        Room.setConnnection(c);
        Room r = new Room();
        int rid = Integer.parseInt(values.get("Room Number (*)"));
        if (r.getRoomAvailability(rid, LoginHMS.hid))
            result = r.deleteRoom(rid, LoginHMS.hid);
        Database.endConnnection(c);
        return result;
    }

    /**
     * This function delete room category in hotel
     * 
     * @param values
     * @return boolean
     */
    public static boolean deleteRoomCategory(HashMap<String, String> values) {
        Connection c = Database.getConnection();
        RoomCategory.setConnnection(c);
        RoomCategory r = new RoomCategory();
        String category = values.get("Room Category (*)");
        int occup = Integer.parseInt(values.get("Occupancy (*)"));
        boolean result = r.deleteRoomCategory(category, LoginHMS.hid, occup);
        Database.endConnnection(c);
        return result;
    }

    /**
     * This function delete service type in hotel chain only if not linked with
     * other hotels
     * 
     * @param values
     * @return boolean
     */
    public static boolean deleteServiceType(HashMap<String, String> values) {
        Connection c = Database.getConnection();
        ServiceType.setConnnection(c);
        ServiceType st = new ServiceType();
        String type = values.get("Service Type (*)");
        boolean result = st.deleteServiceType(type);
        Database.endConnnection(c);
        return result;
    }

    /**
     * This function delete service linked with manager hotel
     * 
     * @param values
     * @return
     */
    public static boolean deleteService(HashMap<String, String> values) {
        Connection c = Database.getConnection();
        Service.setConnnection(c);
        Service st = new Service();
        int service_num = Integer.parseInt(values.get("Service Number (*)"));
        boolean result = st.deleteService(service_num, LoginHMS.hid);
        Database.endConnnection(c);
        return result;
    }

    /**
     * This function delete discount only if not used in any invoice receipt
     * 
     * @param values
     * @return boolean
     */
    public static boolean deleteDiscount(HashMap<String, String> values) {
        Connection c = Database.getConnection();
        Discount.setConnnection(c);
        Discount dis = new Discount();
        String billing_type = values.get("Billing Type (*)");
        boolean result = dis.deleteDiscount(billing_type);
        Database.endConnnection(c);
        return result;
    }

    /**
     * This function delete contact
     * 
     * @param values
     * @return boolean
     */
    public static boolean deleteContact(String id) {
        boolean result = false;
        Connection c = Database.getConnection();
        try {
            ContactInfo.setConnnection(c);
            ContactInfo ci = new ContactInfo();
            result = ci.deleteContact(Integer.parseInt(id));
            Database.endConnnection(c);
        } catch (Exception e) {
            System.out.println(e);
            result = false;
        }
        return result;
    }

    /**
     * Helper method to set Room timestamp instance variables for occupancy
     * query
     * 
     */
    public static void setRoomTimeStamp(Timestamp start, Timestamp end) {
        Room.start = start;
        Room.end = end;
    }
}
