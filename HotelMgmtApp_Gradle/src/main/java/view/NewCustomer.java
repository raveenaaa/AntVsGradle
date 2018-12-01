package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.json.JSONObject;

import service.FrontDeskService;
/**This class is used to create dialog box box for adding new customer
 * 
 * @author vidhisha
 *
 */
@SuppressWarnings("serial")
public class NewCustomer extends JDialog implements ActionListener {

    JLabel nameL, dobL, phoneL, emailL, addressL, ssnL, message;
    JTextField nameT, dobT, phoneT, emailT, addressT, ssnT;
    JButton save = new JButton("Save");
    JPanel panel;

    public NewCustomer(FrontDesk f, String ssn) {
        panel = new JPanel(new GridLayout(6, 2, 0, 3));

        nameL = new JLabel("Name");
        ssnL = new JLabel("SSN");
        dobL = new JLabel("Date of Birth");
        phoneL = new JLabel("Phone Number");
        emailL = new JLabel("E-Mail Address");
        addressL = new JLabel(" Address");

        nameT = new JTextField();
        ssnT = new JTextField(9);
        dobT = new JTextField();
        phoneT = new JTextField();
        emailT = new JTextField();
        addressT = new JTextField();

        panel.add(nameL);
        panel.add(nameT);
        panel.add(ssnL);
        panel.add(ssnT);
        panel.add(dobL);
        panel.add(dobT);
        panel.add(phoneL);
        panel.add(phoneT);
        panel.add(emailL);
        panel.add(emailT);
        panel.add(addressL);
        panel.add(addressT);

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
        setSize((dim.width - 20) / 3, (dim.height - 30) / 4);
        setLocation(FrontDesk.p);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == save) {
            JSONObject input = new JSONObject();
            if (!nameT.getText().isEmpty())
                input.put("name", nameT.getText());

            input.put("SSN", Integer.parseInt(ssnT.getText()));

            input.put("address", addressT.getText());

            input.put("phone", phoneT.getText());
            input.put("email", emailT.getText());

            input.put("date_of_birth", dobT.getText());

            input.put("peopleType", "customer");
            input.put("hotel_serving", LoginHMS.hid);

            if (!FrontDeskService.addNewCustomer(input)) {
                message = new JLabel("Customer not added, error in input!");
                message.setForeground(Color.RED);
                mydialog(message);
            } else {
                message = new JLabel("Customer added successfully!");
                message.setForeground(Color.GREEN);
                mydialog(message);
            }
            this.dispose();
        }
    }

    public void mydialog(JLabel label) {
        JDialog dialog = new JDialog();
        dialog.add(label, BorderLayout.CENTER);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setSize(250, 100);
        dialog.setVisible(true);
    }

}
