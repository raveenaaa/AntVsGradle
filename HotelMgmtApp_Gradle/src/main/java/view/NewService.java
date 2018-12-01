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
import dao.ServiceType;
import service.ManagerService;

/**
 * Class that creates dialog box / UI for creating new services in hotel
 * 
 * @author kshittiz
 *
 */
@SuppressWarnings("serial")
public class NewService extends JDialog implements ActionListener {

    JLabel service, type;
    JComboBox<String> typeB;

    JTextField serviceN;
    JButton save = new JButton("Save");

    public NewService(Manager manager) {
        super(manager, "New Service", true);
        JPanel panel = new JPanel(new GridLayout(2, 2, 0, 3));
        service = new JLabel(" Service Number (*)");
        type = new JLabel(" Type (*)");

        serviceN = new JTextField();

        panel.add(service);
        panel.add(serviceN);

        setServices();
        panel.add(type);
        panel.add(typeB);

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
        setSize(dim.width / 4, dim.height / 7);
        setLocation(manager.getLocation());
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!ManagerService.addNewService(serviceN.getText(), LoginHMS.hid, (String) typeB
                .getSelectedItem())) {
            Manager.opLabel.setText("Service not saved, error in input!");
            Manager.opLabel.setForeground(Color.RED);
        } else {
            Manager.opLabel.setText("Service saved successfully!");
            Manager.opLabel.setForeground(Color.GREEN);
        }
        this.dispose();
    }

    /**
     * Function to dynamically set room categories and occupancy in combo box of
     * rooms
     */
    private void setServices() {
        Connection conn = Database.getConnection();
        ServiceType.setConnnection(conn);
        typeB = new JComboBox<String>(ServiceType.getServices());
        Database.endConnnection(conn);
    }

}
