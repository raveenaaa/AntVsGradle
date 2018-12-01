package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import dao.Database;

import dao.RoomCategory;
import service.ManagerService;

/**
 * Class that creates dialog box / UI for creating new room in manager's hotel
 * 
 * @author kshittiz
 *
 */
@SuppressWarnings("serial")
public class NewRoom extends JDialog implements ActionListener {

    JLabel room, category, occupancy, availability;
    JComboBox<String> categoryB;
    JComboBox<String> availabilityB = new JComboBox<String>(new String[] { "available",
            "unavailable" });
    JTextField roomN, occupancyT;
    HashMap<String, String> category_occupancy = new HashMap<String, String>();
    JButton save = new JButton("Save");

    public NewRoom(Manager manager) {
        super(manager, "New Room", true);
        JPanel panel = new JPanel(new GridLayout(4, 2, 0, 3));
        room = new JLabel(" Room (*)");
        category = new JLabel(" Category (*)");
        occupancy = new JLabel(" Occupancy");
        occupancy.setEnabled(false);
        availability = new JLabel(" Availability");

        roomN = new JTextField();
        occupancyT = new JTextField();
        occupancyT.setEnabled(false);

        setRoomCategoriesAndOccupancies();
        categoryB.addActionListener(this);

        panel.add(room);
        panel.add(roomN);

        panel.add(category);
        panel.add(categoryB);

        panel.add(occupancy);
        panel.add(occupancyT);

        panel.add(availability);
        panel.add(availabilityB);

        add(panel, BorderLayout.CENTER);

        ImageIcon saveIcon = new ImageIcon(new ImageIcon("images/submit.png").getImage()
                .getScaledInstance(30, 22, Image.SCALE_SMOOTH));
        save.setIcon(saveIcon);
        save.setBackground(Color.DARK_GRAY);
        save.setForeground(Color.GREEN);
        save.addActionListener(this);
        add(save, BorderLayout.SOUTH);
        getRootPane().setDefaultButton(save);

        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(dim.width / 4, dim.height / 6);
        setLocation(manager.getLocation());
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == categoryB) {
            occupancyT.setText(category_occupancy.get(categoryB.getSelectedItem()));
        } else {
            if (!ManagerService.addNewRoom(roomN.getText(), LoginHMS.hid, (String) categoryB
                    .getSelectedItem(), occupancyT.getText(), (String) availabilityB
                            .getSelectedItem())) {
                Manager.opLabel.setText("Room not saved, error in input!");
                Manager.opLabel.setForeground(Color.RED);
            } else {
                Manager.opLabel.setText("Room saved successfully!");
                Manager.opLabel.setForeground(Color.GREEN);
            }
            this.dispose();
        }
    }

    /**
     * Function to dynamically set room categories and occupancy in combo box of
     * rooms
     */
    private void setRoomCategoriesAndOccupancies() {
        int count = 0;
        Connection conn = Database.getConnection();
        RoomCategory.setConnnection(conn);
        ArrayList<RoomCategory> list = RoomCategory.getRoomCategories(LoginHMS.hid);
        String categories[] = new String[list.size()];
        Iterator<RoomCategory> i = list.iterator();
        while (i.hasNext()) {
            RoomCategory temp = i.next();
            categories[count] = temp.getRoom_category();
            category_occupancy.put(temp.getRoom_category(), "" + temp.getOccupancy());
            count++;
        }
        categoryB = new JComboBox<String>(categories);
        Database.endConnnection(conn);
    }

}
