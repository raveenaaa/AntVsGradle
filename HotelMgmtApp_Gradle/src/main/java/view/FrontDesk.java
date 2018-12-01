package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import org.json.JSONObject;

import dao.Database;
import dao.People;
import dao.Room;
import dao.RoomServiceLinks;
import dao.Service;
import service.FrontDeskService;

/**
 * This class is used to design UI for FrontDesk
 * 
 * @author vidhisha
 *
 */
public class FrontDesk extends JFrame implements ActionListener {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    JTabbedPane tabbedPane;
    JPanel register, checkin, checkout, billing, regpanel, billingpanel, end, checkinP, services,
            end1, report, myreport, end_rep;
    JLabel ssnL, room_numL;
    JTextField ssnT;
    JComboBox room_numC;
    JButton check, checkOut, addPerson, checkB, add_service, update, check_rep;
    NewCheckIn newcheckin;
    DefaultTableModel tableModel = new DefaultTableModel();
    JTable table = new JTable(tableModel);
    JComboBox<String> associateStaff;
    ArrayList<String> roomnums = new ArrayList<String>();

    JLabel roomL;
    JTextField roomT;
    JLabel extraDiscountLabel;
    JTextField extraDiscountText;
    JLabel taxLabel;
    JTextField taxText;
    JLabel billingAdressLabel;
    JTextField billingAdressText;
    JLabel billingTypeLabel;
    JTextField billingTypeText;
    JComboBox<String> payment;
    static Point p;

    public FrontDesk(String name) {
        super("Front Desk View - " + name);
        tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(Color.DARK_GRAY);
        tabbedPane.setForeground(Color.WHITE);

        register = new JPanel(new BorderLayout());

        ImageIcon registerIcon = new ImageIcon(new ImageIcon("images/add.png").getImage()
                .getScaledInstance(30, 30, Image.SCALE_SMOOTH));

        tabbedPane.addTab("Register Customer", registerIcon, register);

        checkin = new JPanel(new BorderLayout());
        ImageIcon checkinIcon = new ImageIcon(new ImageIcon("images/checkin.png").getImage()
                .getScaledInstance(30, 30, Image.SCALE_SMOOTH));
        tabbedPane.addTab("Check-In Customer", checkinIcon, checkin);

        checkout = new JPanel(new BorderLayout());
        ImageIcon checkoutIcon = new ImageIcon(new ImageIcon("images/checkout.png").getImage()
                .getScaledInstance(30, 30, Image.SCALE_SMOOTH));
        tabbedPane.addTab("Check-Out Customer", checkoutIcon, checkout);

        services = new JPanel(new BorderLayout());
        ImageIcon servicesIcon = new ImageIcon(new ImageIcon("images/billing.png").getImage()
                .getScaledInstance(30, 30, Image.SCALE_SMOOTH));
        tabbedPane.addTab("Request Services", servicesIcon, services);

        report = new JPanel(new BorderLayout());
        ImageIcon reportIcon = new ImageIcon(new ImageIcon("images/fetch.png").getImage()
                .getScaledInstance(30, 30, Image.SCALE_SMOOTH));
        tabbedPane.addTab("Request Reports", reportIcon, report);

        add(tabbedPane, BorderLayout.CENTER);

        // UI for register
        regpanel = new JPanel(new GridLayout(12, 2, 0, 3));
        ssnL = new JLabel("SSN");

        ssnT = new JTextField();

        regpanel.add(ssnL);
        regpanel.add(ssnT);

        register.add(regpanel);
        // register.add(new JScrollPane(regpanel), BorderLayout.CENTER);

        end = new JPanel(new GridLayout(3, 1));
        ImageIcon searchIcon = new ImageIcon(new ImageIcon("images/search.png").getImage()
                .getScaledInstance(30, 30, Image.SCALE_SMOOTH));
        check = new JButton("Check if customer detail's present", searchIcon);
        check.setBackground(Color.DARK_GRAY);
        check.setForeground(Color.ORANGE);
        end.add(check);
        check.addActionListener(this);

        ImageIcon addIcon = new ImageIcon(new ImageIcon("images/submit.png").getImage()
                .getScaledInstance(30, 30, Image.SCALE_SMOOTH));
        addPerson = new JButton("Add this person", addIcon);
        addPerson.setBackground(Color.DARK_GRAY);
        addPerson.setForeground(Color.GREEN);
        end.add(addPerson);
        addPerson.addActionListener(this);

        ImageIcon upIcon = new ImageIcon(new ImageIcon("images/update.png").getImage()
                .getScaledInstance(30, 30, Image.SCALE_SMOOTH));
        update = new JButton("Update this Person's Record", upIcon);
        update.setBackground(Color.DARK_GRAY);
        update.setForeground(Color.GREEN);
        end.add(update);
        update.addActionListener(this);
        register.add(new JScrollPane(end), BorderLayout.SOUTH);

        // register.add(end, BorderLayout.SOUTH);
        register.add(new JScrollPane(end), BorderLayout.SOUTH);

        // UI for billing

        // UI for check-out
        billingpanel = new JPanel(new GridLayout(12, 2, 0, 3));
        roomL = new JLabel("Enter the room number to be checked out");
        roomT = new JTextField();

        billingpanel.add(roomL);
        billingpanel.add(roomT);

        extraDiscountLabel = new JLabel("Enter extra discount");
        extraDiscountText = new JTextField();

        billingpanel.add(extraDiscountLabel);
        billingpanel.add(extraDiscountText);

        taxLabel = new JLabel("Enter tax to be levied");
        taxText = new JTextField();

        billingpanel.add(taxLabel);
        billingpanel.add(taxText);

        billingAdressLabel = new JLabel("Enter the billing adress");
        billingAdressText = new JTextField();

        billingpanel.add(billingAdressLabel);
        billingpanel.add(billingAdressText);

        billingTypeLabel = new JLabel("Enter the billing type");
        List<String> paymentTypesList = FrontDeskService.getListOfPayment();// getting
                                                                            // all
                                                                            // billing
                                                                            // types
                                                                            // from
                                                                            // database
        String[] paymentTypes = new String[paymentTypesList.size()]; // converting
                                                                     // the list
                                                                     // obtained
                                                                     // to array
                                                                     // format
        paymentTypesList.toArray(paymentTypes);
        payment = new JComboBox<String>(paymentTypes); // creating a dropdown
                                                       // menu for billing types
        // payment.setSelectedIndex(0);

        billingpanel.add(billingTypeLabel);
        billingpanel.add(payment);

        checkout.add(billingpanel);

        end1 = new JPanel(new GridLayout(1, 1));
        checkOut = new JButton("Check out customer & generate bill");
        checkOut.setBackground(Color.DARK_GRAY);
        checkOut.setForeground(Color.ORANGE);
        end1.add(checkOut);
        checkOut.addActionListener(this);
        checkout.add(new JScrollPane(end1), BorderLayout.SOUTH);

        // UI for check-in
        newcheckin = new NewCheckIn(this);
        checkin.add(newcheckin.createcheckin());
        checkinP = new JPanel(new GridLayout(1, 1));

        checkB = new JButton("Check In");
        checkB.setBackground(Color.DARK_GRAY);
        checkB.setForeground(Color.GREEN);
        checkinP.add(checkB);
        checkB.addActionListener(this);
        checkin.add(new JScrollPane(checkinP), BorderLayout.SOUTH);

        // UI for services
        RequestService reqservice = new RequestService(this);
        services.add(reqservice.getview());

        end1 = new JPanel(new GridLayout(1, 1));
        add_service = new JButton("Add Service");
        add_service.setBackground(Color.DARK_GRAY);
        add_service.setForeground(Color.GREEN);
        end1.add(add_service);
        add_service.addActionListener(this);
        end1.add(add_service);
        services.add(end1, BorderLayout.SOUTH);

        // UI for report
        myreport = new JPanel(new GridLayout(12, 2, 0, 3));
        room_numL = new JLabel("Room Number");

        end_rep = new JPanel(new GridLayout(1, 1));

        check_rep = new JButton("Get Report");
        check_rep.setBackground(Color.DARK_GRAY);
        check_rep.setForeground(Color.GREEN);
        end_rep.add(check_rep);
        report.add(end_rep, BorderLayout.SOUTH);
        check_rep.addActionListener(this);
        Connection conn = Database.getConnection();
        Room.setConnnection(conn);
        Room room = new Room();

        Vector<Vector<Object>> data = room.getRoomDetails(LoginHMS.hid);
        int i;
        for (i = 0; i < data.size(); i++) {

            if (data.get(i).get(3).toString().equals("unavailable")) {

                roomnums.add(data.get(i).get(0).toString());
            }
        }
        room_numC = new JComboBox(roomnums.toArray());
        myreport.add(room_numL);
        myreport.add(room_numC);
        report.add(myreport);

        // set this view to full screen size
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(dim.width / 2, dim.height / 2);
        setLocation(100, 100);
        p = getLocation();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);

    }

    /**
     * this method is used to show which staff is serving which customer
     * 
     * @param room_num
     */
    public void showStaffServing(String room_num) {
        JDialog dialog = new JDialog();
        Vector<Vector<Object>> vector = FrontDeskService.getRoomServicesOfferedByStaff(room_num);
        tableModel.setDataVector(vector, RoomServiceLinks.ROOM_SERVICE_COLUMNS);
        if (vector.size() == 0 || vector == null) {

        }
        dialog.add(new JScrollPane(table), BorderLayout.CENTER);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setSize(500, 200);
        dialog.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent action) {
        // TODO Auto-generated method stub

        if (action.getSource() == check) {
            boolean check = FrontDeskService.checkIfPersonPresent(ssnT.getText());
            if (check == false) {
                new MyDialog("Sorry! No Data present for this Person");
            } else {
                new MyDialog2("Data present for this Person!");
            }
        }
        if (action.getSource() == check_rep) {

            showStaffServing(room_numC.getSelectedItem().toString());
        }

        if (action.getSource() == update) {

            boolean check = FrontDeskService.checkIfPersonPresent(ssnT.getText());
            if (check == false) {
                new MyDialog("Sorry! No Data present for this Person");
            } else {
                new UpdateCustomer(this, ssnT.getText());
            }

        }

        if (action.getSource() == addPerson) {
            new NewCustomer(this, ssnT.getText());
        }
        if (action.getSource() == add_service) {

            int my_room = Integer.parseInt(RequestService.room_numC.getSelectedItem().toString());
            String my_service = RequestService.typeC.getSelectedItem().toString();
            int staffId = RequestService.staffIds.get(RequestService.staffC.getSelectedItem());
            Connection c = Database.getConnection();
            Service.setConnnection(c);
            Service s = new Service();
            int room_service = s.getservicenum(my_service, LoginHMS.hid);
            JSONObject input = new JSONObject();
            input.put("room_num", my_room);
            input.put("service_type", my_service);
            input.put("service_num", room_service);
            input.put("hotel_id", LoginHMS.hid);
            input.put("staff_id", staffId);

            if (!FrontDeskService.requestNewService(input)) {
                new MyDialog("Service Requested was not successful");
            } else {
                new MyDialog2("Service Request completed successfully!");

            }

        }
        if (action.getSource() == checkB) {
            int numguests = Integer.parseInt(newcheckin.guestT.getSelectedItem().toString());
            String category = newcheckin.categoryT.getSelectedItem().toString();

            Timestamp date1 = null;

            try {
                date1 = new Timestamp(System.currentTimeMillis());

            } catch (Exception e) {
                e.printStackTrace();
            }

            int room_num = 0;
            Map<Integer, String> map = new LinkedHashMap<>();
            map = FrontDeskService.checkRoomAvailable(LoginHMS.hid, numguests, category);

            if (map != null) {
                JSONObject input = new JSONObject();
                Connection conn = Database.getConnection();

                People.setConnnection(conn);
                String ssn = newcheckin.ssnT.getText();

                int peopleid = People.getPIDbySSN(ssn);

                Database.endConnnection(conn);

                Map.Entry<Integer, String> entry = map.entrySet().iterator().next();
                room_num = entry.getKey();

                if (peopleid == 0)
                    new MyDialog("Invalid SSN");
                else {
                    input.put("pid", peopleid);
                    input.put("guests", numguests);
                    input.put("checkin", date1);
                    input.put("checkout", date1);
                    input.put("room_num", room_num);
                    input.put("category", category);

                    if (!FrontDeskService.addNewCheckIn(input)) {
                        new MyDialog("Check-In was not successful. ");
                    } else {
                        new MyDialog2("Check-In successful! Room number alloted is"
                                + FrontDeskService.room_alloted);
                        NewCheckIn.ssnT.setText("");
                    }

                    // this.dispose();
                }
            } else
                new MyDialog("Sorry! No Room Available");

        }

        // action to be perfomed when customer checks out
        if (action.getSource() == checkOut) {
            String finalString = "";
            String paymentTypeTemp = (String) payment.getSelectedItem();

            try {
                finalString = FrontDeskService.calculateAmount(roomT.getText(), extraDiscountText
                        .getText(), paymentTypeTemp, taxText.getText(), billingAdressText
                                .getText());
                if (finalString != "") {

                    new MyDialog3("The customer has successfully checked out " + "\n"
                            + finalString);
                } else {
                    new MyDialog3("The room needs to be corrected");
                }
            } catch (NumberFormatException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();

            }

        }

    }
}

@SuppressWarnings("serial")
class MyDialog extends JDialog {
    JLabel error;

    MyDialog(String text) {
        error = new JLabel(text);
        error.setForeground(Color.RED);
        add(error, BorderLayout.CENTER);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(250, 100);
        setLocation(FrontDesk.p);
        setVisible(true);
    }
}

@SuppressWarnings("serial")
class MyDialog2 extends JDialog {
    JLabel error;

    MyDialog2(String text) {
        error = new JLabel(text);
        error.setForeground(Color.GREEN);
        add(error, BorderLayout.CENTER);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(400, 200);
        setLocation(FrontDesk.p);
        setVisible(true);
    }

}

@SuppressWarnings("serial")
class MyDialog3 extends JDialog {
    JTextArea result;

    MyDialog3(String text) {
        this.setTitle("Itemized receipt");
        result = new JTextArea(text);
        result.setEditable(false);
        add(result, BorderLayout.CENTER);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(500, 300);
        setLocation(FrontDesk.p);
        setVisible(true);
    }
}
