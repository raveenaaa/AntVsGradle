package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import org.json.JSONObject;

import dao.ContactInfo;
import service.FrontDeskService;
/**
 * This class opens the UI for updating the customer details
 * @author vidhisha
 *
 */
@SuppressWarnings("serial")
public class UpdateCustomer extends JDialog implements ActionListener {

    JLabel nameL, dobL, phoneL, emailL, addressL, ssnL, message;
    JTextField nameT, dobT, addressT, ssnT;
    DefaultTableModel tableModel = new DefaultTableModel();
    JTable table = new JTable(tableModel);
    JButton save = new JButton("Update");
    JButton fetchContacts = new JButton("Fetch contacts");
    JLabel contactIdLabel = new JLabel("Contact Id");
    public static JTextField contactId = new JTextField("0");
    JButton update = new JButton("Update Contact Details");
    JPanel panel;
    public static JTextField phoneT, emailT;
    String original_ssn;

    UpdateCustomer(FrontDesk f, String ssn) {
        panel = new JPanel(new GridLayout(13, 1, 0, 3));

        // add labels and textfields to panel
        original_ssn = ssn;
        nameL = new JLabel("Name");
        dobL = new JLabel("Date of Birth");
        phoneL = new JLabel("Phone Number");
        emailL = new JLabel("E-Mail Address");
        addressL = new JLabel(" Address");

        nameT = new JTextField();
        dobT = new JTextField();
        phoneT = new JTextField();
        emailT = new JTextField();
        addressT = new JTextField();

        panel.add(nameL);
        panel.add(nameT);

        panel.add(dobL);
        panel.add(dobT);

        panel.add(addressL);
        panel.add(addressT);

        panel.add(fetchContacts);
        fetchContacts.addActionListener(this);
        panel.add(contactIdLabel);
        panel.add(contactId);

        panel.add(phoneL);
        panel.add(phoneT);

        panel.add(emailL);
        panel.add(emailT);

        add(panel, BorderLayout.CENTER);

        // creating image icon for save button
        ImageIcon saveIcon = new ImageIcon(new ImageIcon("images/submit.png").getImage()
                .getScaledInstance(30, 22, Image.SCALE_SMOOTH));
        save.setIcon(saveIcon);
        save.setBackground(Color.DARK_GRAY);
        save.setForeground(Color.GREEN);
        save.addActionListener(this);
        add(save, BorderLayout.SOUTH);
        getRootPane().setDefaultButton(save);

        // setting size of frame
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        setSize((dim.width - 20) / 3, (dim.height - 40) / 2);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setVisible(true);

    }

    public void actionPerformed(ActionEvent e) {

        // open UI to show contacts
        if (e.getSource() == fetchContacts) {

            customerContactDetails();
        }

        // send all data as JSONObject when save is pressed
        if (e.getSource() == save) {
            JSONObject input = new JSONObject();
            input.put("name", nameT.getText());

            input.put("original_ssn", original_ssn);

            input.put("address", addressT.getText());

            input.put("phone", phoneT.getText());
            input.put("email", emailT.getText());

            input.put("date_of_birth", dobT.getText());

            if (!FrontDeskService.updateCustomer(input)) {
                message = new JLabel("Customer not updated, error in input!");
                message.setForeground(Color.RED);

                mydialog(message);
            } else {
                message = new JLabel("Customer details updated successfully!");
                message.setForeground(Color.GREEN);
                mydialog(message);
            }
            this.dispose();

        }

    }

    // function to show a dialog with pop-up message
    public void mydialog(JLabel label) {
        JDialog dialog = new JDialog();
        dialog.add(label, BorderLayout.CENTER);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setSize(250, 100);
        dialog.setVisible(true);
    }

    // UI to show contact details for a person
    public void customerContactDetails() {
        JDialog dialog = new JDialog();
        Vector<Vector<Object>> vector = FrontDeskService.getContactDetails(original_ssn);
        tableModel.setDataVector(vector, ContactInfo.COLUMNS);
        System.out.println("vector is" + vector);
        if (vector.size() == 0 || vector == null) {
            contactId.setText("0");
            contactId.setEnabled(false);
        }
        dialog.add(new JScrollPane(table), BorderLayout.CENTER);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setSize(350, 200);
        dialog.setVisible(true);
    }

}
