package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

import dao.ContactInfo;
import dao.Hotel;
import dao.Room;
import dao.Staff;
import service.ManagerService;

/**
 * This class is designed to support all the update operations by the manager.
 * All the operations performed by the managers are categorized on the basis of
 * title passed in the constructor.
 * 
 * @author kshittiz
 *
 */
@SuppressWarnings("serial")
public class UpdateOperations extends JDialog implements ActionListener, TableModelListener {
    JTable table;
    DefaultTableModel tableModel = new DefaultTableModel();
    JButton update = new JButton("Update");
    HashMap<String, String> valuesSelected = new HashMap<String, String>();
    static String title;

    public UpdateOperations(Manager manager, String titled, String msg) {
        super(manager, titled, true);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        JPanel panel = new JPanel(new BorderLayout());
        JLabel heading = new JLabel(msg);
        table = new JTable(tableModel) {
            @Override
            public boolean isCellEditable(int row, int column) {
                boolean result = true;
                switch (UpdateOperations.title) {
                case "Update staff member details":
                    result = (column == 0 || column == 2 || column == 6) ? false : true;
                    break;
                case "Update hotel details":
                    result = column == 0 ? false : true;
                    break;
                case "Update room details":
                    result = (column == 0 || column == 1 || column == 2) ? false : true;
                    break;
                case "Update contact details":
                    result = column == 0 ? false : true;
                    break;
                }
                return result;
            }
        };
        title = titled;

        // All the actions performed by manager are reflected in a separate UI
        // controlled by this switch case
        switch (titled) {
        case "Update staff member details":
            tableModel.setDataVector(ManagerService.getStaffDetails(null), Staff.STAFF_COLUMNS);
            setSize((dim.width + 500) / 2, dim.height / 3);
            break;
        case "Update hotel details":
            tableModel.setDataVector(ManagerService.getHotelDetails(LoginHMS.hid), Hotel.COLUMNS);
            setSize(dim.width / 2, dim.height / 5);
            break;
        case "Update room details":
            tableModel.setDataVector(ManagerService.getRoomDetails(), Room.COLUMNS);
            setSize(dim.width / 2, dim.height / 3);
            break;
        case "Update contact details":
            tableModel.setDataVector(manager.contactData, ContactInfo.COLUMNS);
            setSize(dim.width / 4, dim.height / 3);
            break;
        }

        tableModel.addTableModelListener(this);
        panel.add(heading, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        ImageIcon saveIcon = new ImageIcon(new ImageIcon("images/submit.png").getImage()
                .getScaledInstance(30, 22, Image.SCALE_SMOOTH));
        update.setBackground(Color.DARK_GRAY);
        update.setForeground(Color.GREEN);
        update.setIcon(saveIcon);
        update.addActionListener(this);
        add(update, BorderLayout.SOUTH);
        setSize(dim.width / 2, dim.height / 3);
        add(panel);
        setLocation(manager.getLocation());
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        boolean result = false;

        // All the actions performed by manager are reflected in a separate UI
        // controlled by this switch case
        switch (title) {
        case "Update staff member details":
            result = ManagerService.updateStaff(valuesSelected, Integer.parseInt(valuesSelected.get(
                    "pid")));
            break;
        case "Update hotel details":
            result = ManagerService.updateHotel(valuesSelected);
            break;
        case "Update room details":
            result = ManagerService.updateRoom(valuesSelected, Integer.parseInt(valuesSelected.get(
                    "room_num")));
            break;
        case "Update contact details":
            result = ManagerService.updateContact(valuesSelected, Integer.parseInt(valuesSelected
                    .get("contact_id")));
            break;
        }

        if (!result) {
            Manager.opLabel.setText("Record not updated, error in input!");
            Manager.opLabel.setForeground(Color.RED);
        } else {
            Manager.opLabel.setText("Record udated successfully!");
            Manager.opLabel.setForeground(Color.GREEN);
        }
        this.dispose();
    }

    @Override
    public void tableChanged(TableModelEvent e) {
        // Event trapped on any changes in table data based on the user
        // operations
        if ("Update staff member details".equals(title)) {
            if (valuesSelected.get("pid") != null && !valuesSelected.get("pid").equals(table
                    .getValueAt(e.getLastRow(), 0).toString()))
                valuesSelected.clear();
            valuesSelected.put("pid", table.getValueAt(e.getLastRow(), 0).toString());
            if ("Name".equals(table.getColumnName(e.getColumn())))
                valuesSelected.put("name", table.getValueAt(e.getLastRow(), 1).toString());
            if ("Address".equals(table.getColumnName(e.getColumn())))
                valuesSelected.put("address", table.getValueAt(e.getLastRow(), 3).toString());
            if ("Job Title".equals(table.getColumnName(e.getColumn())))
                valuesSelected.put("job_title", table.getValueAt(e.getLastRow(), 4).toString());
            if ("Age".equals(table.getColumnName(e.getColumn())))
                valuesSelected.put("age", table.getValueAt(e.getLastRow(), 5).toString());
        }

        if ("Update hotel details".equals(title)) {
            if (valuesSelected.get("hotel_id") != null && !valuesSelected.get("hotel_id").equals(
                    table.getValueAt(e.getLastRow(), 0).toString()))
                valuesSelected.clear();
            valuesSelected.put("hotel_id", table.getValueAt(e.getLastRow(), 0).toString());
            valuesSelected.put("hotel_id", table.getValueAt(e.getLastRow(), 0).toString());
            if ("Hotel Name".equals(table.getColumnName(e.getColumn())))
                valuesSelected.put("hotel_name", table.getValueAt(e.getLastRow(), 1).toString());
            if ("Hotel Address".equals(table.getColumnName(e.getColumn())))
                valuesSelected.put("hotel_address", table.getValueAt(e.getLastRow(), 2).toString());
        }

        if ("Update room details".equals(title)) {
            if (valuesSelected.get("room_num") != null && !valuesSelected.get("room_num").equals(
                    table.getValueAt(e.getLastRow(), 0).toString()))
                valuesSelected.clear();
            valuesSelected.put("room_num", table.getValueAt(e.getLastRow(), 0).toString());
            if ("Availability".equals(table.getColumnName(e.getColumn())))
                valuesSelected.put("availability", table.getValueAt(e.getLastRow(), 3).toString());
        }

        if ("Update contact details".equals(title)) {
            if (valuesSelected.get("contact_id") != null && !valuesSelected.get("contact_id")
                    .equals(table.getValueAt(e.getLastRow(), 0).toString()))
                valuesSelected.clear();
            valuesSelected.put("contact_id", table.getValueAt(e.getLastRow(), 0).toString());
            if ("Phone Number".equals(table.getColumnName(e.getColumn())))
                valuesSelected.put("phone_number", table.getValueAt(e.getLastRow(), 1).toString());
            if ("Email".equals(table.getColumnName(e.getColumn())))
                valuesSelected.put("email", table.getValueAt(e.getLastRow(), 2).toString());
        }
    }

}
