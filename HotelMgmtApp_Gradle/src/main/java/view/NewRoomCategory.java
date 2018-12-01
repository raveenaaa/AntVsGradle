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

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import dao.Database;
import dao.RoomCategory;

/**
 * Class that creates dialog box /UI that for adding new room category in
 * manager's hotel
 * 
 * @author kshittiz
 *
 */
@SuppressWarnings("serial")
public class NewRoomCategory extends JDialog implements ActionListener {

    JLabel category, occupancy, rate;
    JComboBox<Integer> occupancyB = new JComboBox<Integer>(new Integer[] { 1, 2, 3, 4 });
    JTextField categoryT, rateT;

    JButton save = new JButton("Save");

    public NewRoomCategory(Manager manager) {
        super(manager, "New Room Category", true);
        JPanel panel = new JPanel(new GridLayout(3, 2, 0, 3));
        category = new JLabel(" Category (*)");
        occupancy = new JLabel(" Occupancy");
        rate = new JLabel(" Nightly Rate (*) [$]");

        categoryT = new JTextField();
        rateT = new JTextField();

        panel.add(category);
        panel.add(categoryT);

        panel.add(occupancy);
        panel.add(occupancyB);

        panel.add(rate);
        panel.add(rateT);

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
        Connection conn = Database.getConnection();
        RoomCategory.setConnnection(conn);
        RoomCategory rc = new RoomCategory();
        try {
            if (!rc.createRoomCategory(LoginHMS.hid, categoryT.getText(), (int) occupancyB
                    .getSelectedItem(), Integer.parseInt(rateT.getText()))) {
                Manager.opLabel.setText("Room category not saved, error in input!");
                Manager.opLabel.setForeground(Color.RED);
            } else {
                Manager.opLabel.setText("Room category saved successfully!");
                Manager.opLabel.setForeground(Color.GREEN);
            }
        } catch (Exception ex) {
            System.out.println(ex);
            Manager.opLabel.setText("Room category not saved, error in input!");
            Manager.opLabel.setForeground(Color.RED);
        }

        this.dispose();
        Database.endConnnection(conn);
    }

}
