package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import dao.ContactInfo;
import dao.Customer;
import dao.Discount;
import dao.Hotel;
import dao.Room;
import dao.RoomCategory;
import dao.Service;
import dao.ServiceType;
import dao.Staff;
import service.ManagerService;

/**
 * This class is designed to support all the fetch operations by the manager.
 * All the operations performed by the managers are categorized on the basis of
 * title passed in the constructor.
 * 
 * @author kshittiz
 *
 */
@SuppressWarnings("serial")
public class FetchOperations extends JDialog implements ActionListener {
    JTable table;
    DefaultTableModel tableModel = new DefaultTableModel();
    JComboBox<String> extra;
    JComboBox<String> occup;
    JCheckBox role;
    String city = "";

    public FetchOperations(Manager manager, String title, String msg) {
        super(manager, title, true);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        JPanel panel = new JPanel(new BorderLayout());
        JLabel heading = new JLabel(msg);
        table = new JTable(tableModel);

        // All the actions performed by manager are reflected in a separate UI
        // controlled by this switch case
        switch (title) {
        case "See your hotel details":
            tableModel.setDataVector(ManagerService.getHotelDetails(LoginHMS.hid), Hotel.COLUMNS);
            setSize(dim.width / 2, dim.height / 5);
            break;
        case "See all hotels in chain":
            tableModel.setDataVector(ManagerService.getHotelDetails(0), Hotel.COLUMNS);
            setSize(dim.width / 2, dim.height / 3);
            break;
        case "See your staff details":
            extra = new JComboBox<String>(new String[] { "complete staff", "manager", "catering",
                    "cleaning", "front_desk" });
            panel.add(extra, BorderLayout.SOUTH);
            role = new JCheckBox("Group by Department/Role ");
            panel.add(role, BorderLayout.EAST);
            extra.addActionListener(this);
            tableModel.setDataVector(ManagerService.getStaffDetails(null), Staff.STAFF_COLUMNS);
            setSize((dim.width + 500) / 2, dim.height / 3);
            break;
        case "See all customers in your hotel":
            tableModel.setDataVector(ManagerService.getCustomerDetails(), Customer.COLUMNS);
            setSize(dim.width / 2, dim.height / 3);
            break;

        case "See all room categories in your hotel":
            tableModel.setDataVector(ManagerService.getRoomCategoryDetails(), RoomCategory.COLUMNS);
            setSize(dim.width / 2, dim.height / 3);
            break;
        case "See all rooms in your hotel":
            tableModel.setDataVector(ManagerService.getRoomDetails(), Room.COLUMNS);
            setSize(dim.width / 2, dim.height / 3);
            break;
        case "See all types of services in hotel chain":
            tableModel.setDataVector(ManagerService.getServiceTypeDetails(), ServiceType.COLUMNS);
            setSize(dim.width / 3, dim.height / 3);
            break;
        case "See all services offered by your hotel":
            tableModel.setDataVector(ManagerService.getServiceDetails(), Service.COLUMNS);
            setSize(dim.width / 3, dim.height / 3);
            break;
        case "See discounts offered":
            tableModel.setDataVector(ManagerService.getDiscountDetails(), Discount.COLUMNS);
            setSize(dim.width / 3, dim.height / 3);
            break;
        case "See occupancy statistics":
            city = "";
            occup = new JComboBox<String>(new String[] { "Occupancy group by all hotels",
                    "Occupancy by room type", "Occupancy by city", "Occupancy by dates",
                    "Total Occupancy", "% of rooms occupied" });
            panel.add(occup, BorderLayout.SOUTH);
            occup.addActionListener(this);
            tableModel.setDataVector(ManagerService.getOccupancyStats(
                    "Occupancy group by all hotels", null), Room.GRP_HOTELS_COLUMNS);
            setSize(dim.width / 3, dim.height / 3);
            break;
        case "See contact details":
            tableModel.setDataVector(manager.contactData, ContactInfo.COLUMNS);
            setSize(dim.width / 4, dim.height / 3);
            break;
        }

        panel.add(heading, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        add(panel);
        setLocation(manager.getLocation());
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == extra) {
            String action = (String) extra.getSelectedItem();

            // If group by role/department check box is selected than a separate
            // data is loaded in the table
            if (role.isSelected())
                tableModel.setDataVector(ManagerService.getStaffDetailsGroupedByRole(),
                        Staff.COLUMNS);
            // loading data in table based on the department of staff
            else {
                if ("complete staff".equals(action))
                    tableModel.setDataVector(ManagerService.getStaffDetails(null),
                            Staff.STAFF_COLUMNS);
                if ("manager".equals(action))
                    tableModel.setDataVector(ManagerService.getStaffDetails(action),
                            Staff.MANAGER_STAFF_COLUMNS);
                if ("cleaning".equals(action))
                    tableModel.setDataVector(ManagerService.getStaffDetails(action),
                            Staff.CLEANING_STAFF_COLUMNS);
                if ("catering".equals(action))
                    tableModel.setDataVector(ManagerService.getStaffDetails(action),
                            Staff.CLEANING_STAFF_COLUMNS);
                if ("front_desk".equals(action))
                    tableModel.setDataVector(ManagerService.getStaffDetails(action),
                            Staff.FRONT_DESK_Staff_COLUMNS);
            }
        }
        // Loading data in table based on different occupancy operations
        if (e.getSource() == occup) {
            String action = (String) occup.getSelectedItem();
            if ("Occupancy group by all hotels".equals(action))
                tableModel.setDataVector(ManagerService.getOccupancyStats(action, null),
                        Room.GRP_HOTELS_COLUMNS);
            if ("Occupancy by room type".equals(action))
                tableModel.setDataVector(ManagerService.getOccupancyStats(action, null),
                        Room.GRP_RTYPE_COLUMNS);
            if ("Occupancy by city".equals(action)) {
                new Query(this);
                tableModel.setDataVector(ManagerService.getOccupancyStats(action, city),
                        Room.GRP_RCITY_COLUMNS);
            }
            if ("Occupancy by dates".equals(action)) {
                new DateQueryOccup(this);
                tableModel.setDataVector(ManagerService.getOccupancyStats(action, null),
                        Room.TOT_OCCUP_COLUMNS);
            }
            if ("Total Occupancy".equals(action))
                tableModel.setDataVector(ManagerService.getOccupancyStats(action, null),
                        Room.TOT_OCCUP_COLUMNS);
            if ("% of rooms occupied".equals(action))
                tableModel.setDataVector(ManagerService.getOccupancyStats(action, null),
                        Room.PER_OCCUP_COLUMNS);
        }
    }

}

/**
 * Class that creates dialog box that allow user to enter city
 * 
 * @author kshittiz
 *
 */
@SuppressWarnings("serial")
class Query extends JDialog implements ActionListener {
    JTextField city = new JTextField();
    JButton submit = new JButton("submit");
    FetchOperations ops;

    Query(FetchOperations ops) {
        super(ops, "Enter city", true);
        this.ops = ops;
        add(city, BorderLayout.CENTER);
        add(submit, BorderLayout.SOUTH);
        submit.addActionListener(this);
        submit.setBackground(Color.ORANGE);
        setSize(300, 100);
        setLocation(ops.getLocation());
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        ops.city = city.getText();
        this.dispose();
    }
}

/**
 * Class that creates dialog box for allowing using to enter date
 * 
 * @author kshittiz
 *
 */
@SuppressWarnings("serial")
class DateQueryOccup extends JDialog implements ActionListener {
    JLabel entry = new JLabel("Start Date");
    JLabel exit = new JLabel("End Date");

    JTextField entryDate = new JTextField("yyyy-mm-dd HH:mm:ss");
    JTextField exitDate = new JTextField("yyyy-mm-dd HH:mm:ss");

    JButton submit = new JButton("submit");
    FetchOperations ops;

    DateQueryOccup(FetchOperations ops) {
        super(ops, "Specify date range", true);
        this.ops = ops;
        JPanel panel = new JPanel(new GridLayout(2, 2, 0, 3));
        panel.add(entry);
        panel.add(entryDate);
        panel.add(exit);
        panel.add(exitDate);
        add(panel, BorderLayout.CENTER);
        add(submit, BorderLayout.SOUTH);
        submit.addActionListener(this);
        submit.setBackground(Color.ORANGE);
        setSize(350, 150);
        setLocation(ops.getLocation());
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");
        try {
            Date startDate = myFormat.parse(entryDate.getText());
            Date endDate = myFormat.parse(exitDate.getText());
            ManagerService.setRoomTimeStamp(new Timestamp(startDate.getTime()), new Timestamp(
                    endDate.getTime()));

        } catch (ParseException e1) {
            entryDate.setText("Wrong date format used");
        }
        this.dispose();
    }
}
