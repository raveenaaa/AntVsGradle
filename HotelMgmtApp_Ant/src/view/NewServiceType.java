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

import service.ManagerService;

/**
 * Class that creates UI for adding new service type in hotel chain
 * 
 * @author kshittiz
 *
 */
@SuppressWarnings("serial")
public class NewServiceType extends JDialog implements ActionListener {

    JLabel type, price;
    JTextField typeT, priceT;

    JButton save = new JButton("Save");

    public NewServiceType(Manager manager) {
        super(manager, "New Service Type", true);
        JPanel panel = new JPanel(new GridLayout(2, 2, 0, 3));
        type = new JLabel(" Service Type (*)");
        price = new JLabel(" Price (*) [$]");

        typeT = new JTextField();
        priceT = new JTextField();

        panel.add(type);
        panel.add(typeT);

        panel.add(price);
        panel.add(priceT);

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
        if (!ManagerService.addNewServiceType(typeT.getText(), priceT.getText())) {
            Manager.opLabel.setText("Service type not saved, error in input!");
            Manager.opLabel.setForeground(Color.RED);
        } else {
            Manager.opLabel.setText("Service type saved successfully!");
            Manager.opLabel.setForeground(Color.GREEN);
        }
        this.dispose();
    }

}
