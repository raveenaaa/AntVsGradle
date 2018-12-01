package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import dao.Discount;
import dao.Room;
import dao.RoomCategory;
import dao.Service;
import dao.ServiceType;
import dao.Staff;
import service.ManagerService;

/**
 * This class is designed to support all the delete operations by the manager.
 * All the operations performed by the managers are categorized on the basis of
 * title passed in the constructor.
 * 
 * @author kshittiz
 *
 */
@SuppressWarnings("serial")
public class DeleteOperations extends JDialog implements ActionListener {
    JTable table;
    JButton delete = new JButton("delete");
    DefaultTableModel tableModel = new DefaultTableModel();

    HashMap<String, String> valuesSelected = new HashMap<String, String>();
    String title;

    public DeleteOperations(Manager manager, String title, String msg) {
        super(manager, title, true);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        JPanel panel = new JPanel(new BorderLayout());
        JLabel heading = new JLabel(msg);
        table = new JTable(tableModel);
        this.title = title; // maintaining title at class level for future use

        // All the actions performed by manager are reflected in a separate UI
        // controlled by this switch case
        switch (title) {
        case "Remove staff member":
            tableModel.setDataVector(ManagerService.getStaffDetails(null), Staff.STAFF_COLUMNS);
            setSize((dim.width) / 2, dim.height / 3);
            break;
        case "Remove room category":
            tableModel.setDataVector(ManagerService.getRoomCategoryDetails(), RoomCategory.COLUMNS);
            setSize(dim.width / 2, dim.height / 3);
            break;
        case "Remove room":
            tableModel.setDataVector(ManagerService.getRoomDetails(), Room.COLUMNS);
            setSize(dim.width / 2, dim.height / 3);

            break;
        case "Remove service type":
            tableModel.setDataVector(ManagerService.getServiceTypeDetails(), ServiceType.COLUMNS);
            setSize(dim.width / 3, dim.height / 3);

            break;
        case "Remove Services offered":
            tableModel.setDataVector(ManagerService.getServiceDetails(), Service.COLUMNS);
            setSize(dim.width / 3, dim.height / 3);

            break;
        case "Remove discount":
            tableModel.setDataVector(ManagerService.getDiscountDetails(), Discount.COLUMNS);
            setSize(dim.width / 3, dim.height / 3);

            break;
        }
        table.addMouseListener(new TableListener(this));
        panel.add(heading, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        ImageIcon saveIcon = new ImageIcon(new ImageIcon("images/remove.png").getImage()
                .getScaledInstance(25, 22, Image.SCALE_SMOOTH));
        delete.setIcon(saveIcon);
        delete.setBackground(Color.DARK_GRAY);
        delete.setForeground(Color.RED);
        delete.addActionListener(this);
        panel.add(delete, BorderLayout.SOUTH);
        getRootPane().setDefaultButton(delete);

        add(panel);
        setLocation(manager.getLocation());
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    @Override

    public void actionPerformed(ActionEvent e) {
        // All the actions performed by manager are controlled
        // controlled by this switch case
        switch (title) {
        case "Remove staff member":
            if (ManagerService.deleteStaff(valuesSelected)) {
                Manager.opLabel.setText("Record deleted Successfully!");
                Manager.opLabel.setForeground(Color.GREEN);
            } else {
                Manager.opLabel.setText("Record not deleted (possibly in use)!");
                Manager.opLabel.setForeground(Color.RED);
            }
            break;
        case "Remove room category":
            if (ManagerService.deleteRoomCategory(valuesSelected)) {
                Manager.opLabel.setText("Record deleted Successfully!");
                Manager.opLabel.setForeground(Color.GREEN);
            } else {
                Manager.opLabel.setText("Record not deleted (possibly in use)!");
                Manager.opLabel.setForeground(Color.RED);
            }
            break;
        case "Remove room":
            if (ManagerService.deleteRoom(valuesSelected)) {
                Manager.opLabel.setText("Record deleted Successfully!");
                Manager.opLabel.setForeground(Color.GREEN);
            } else {
                Manager.opLabel.setText("Record not deleted (possibly in use)!");
                Manager.opLabel.setForeground(Color.RED);
            }
            break;
        case "Remove service type":
            if (ManagerService.deleteServiceType(valuesSelected)) {
                Manager.opLabel.setText("Record deleted Successfully!");
                Manager.opLabel.setForeground(Color.GREEN);
            } else {
                Manager.opLabel.setText("Record not deleted (possibly in use)!");
                Manager.opLabel.setForeground(Color.RED);
            }
            break;
        case "Remove Services offered":
            if (ManagerService.deleteService(valuesSelected)) {
                Manager.opLabel.setText("Record deleted Successfully!");
                Manager.opLabel.setForeground(Color.GREEN);
            } else {
                Manager.opLabel.setText("Record not deleted (possibly in use)!");
                Manager.opLabel.setForeground(Color.RED);
            }
            break;
        case "Remove discount":
            if (ManagerService.deleteDiscount(valuesSelected)) {
                Manager.opLabel.setText("Record deleted Successfully!");
                Manager.opLabel.setForeground(Color.GREEN);
            } else {
                Manager.opLabel.setText("Record not deleted!");
                Manager.opLabel.setForeground(Color.RED);
            }
            break;
        }
        this.dispose();
    }
}

/**
 * Class specifically designed to listen to any updates in table loaded on UI
 * for view
 * 
 * @author kshittiz
 *
 */
class TableListener extends MouseAdapter {
    DeleteOperations dp;

    TableListener(DeleteOperations dp) {
        this.dp = dp;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        JTable table = (JTable) e.getSource();
        int row = table.getSelectedRow();
        int column = table.getSelectedColumn();
        Object o = table.getValueAt(row, column);
        if (o != null)
            dp.valuesSelected.put(table.getColumnName(column), o.toString());

    }
}
