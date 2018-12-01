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
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.json.JSONObject;

import service.ManagerService;

/**
 * Class that creates dialog box / UI for adding new staff in manager's hotel
 * 
 * @author kshittiz
 *
 */
@SuppressWarnings("serial")
public class NewStaff extends JDialog implements ActionListener {

    JLabel name, ssn, jobTitle, age, department, gender, skill, privilege, speciality, phone, email,
            address;
    JTextField nameT, ssnT, titleT, ageT, skillT, specialityT, phoneT, emailT, addressT;
    JComboBox<String> departmentB = new JComboBox<String>(new String[] { "Manager", "Catering",
            "Cleaning", "Front_Desk" });
    JComboBox<String> privilegeB = new JComboBox<String>(new String[] { "Cleaning", "Catering",
            "Front_Desk" });
    JComboBox<String> genderB = new JComboBox<String>(new String[] { "Female", "Male", "Other" });
    JButton save = new JButton("Save");

    public NewStaff(Manager manager) {
        super(manager, "New staff Member", true);
        JPanel panel = new JPanel(new GridLayout(12, 2, 0, 3));
        name = new JLabel(" Name (*)");
        ssn = new JLabel(" SSN (*)");
        jobTitle = new JLabel(" Job Title (*)");
        age = new JLabel(" Age");
        department = new JLabel(" Department");
        gender = new JLabel(" Gender");
        skill = new JLabel(" Skill");
        privilege = new JLabel(" Privilege");
        speciality = new JLabel(" speciality");
        phone = new JLabel(" Phone");

        email = new JLabel(" Email");
        address = new JLabel(" Address");

        nameT = new JTextField();
        ssnT = new JTextField();
        titleT = new JTextField();
        ageT = new JTextField();
        skillT = new JTextField();
        specialityT = new JTextField();
        phoneT = new JTextField();
        emailT = new JTextField();
        addressT = new JTextField();

        panel.add(name);
        panel.add(nameT);

        panel.add(ssn);
        panel.add(ssnT);

        panel.add(jobTitle);
        panel.add(titleT);

        panel.add(age);
        panel.add(ageT);

        panel.add(department);
        panel.add(departmentB);
        departmentB.addActionListener(this);

        panel.add(privilege);
        panel.add(privilegeB);

        panel.add(gender);
        panel.add(genderB);
        genderB.setEnabled(false);
        gender.setEnabled(false);

        panel.add(skill);
        panel.add(skillT);
        skillT.setEnabled(false);
        skill.setEnabled(false);

        panel.add(speciality);
        panel.add(specialityT);
        specialityT.setEnabled(false);
        speciality.setEnabled(false);

        panel.add(phone);
        panel.add(phoneT);

        panel.add(email);
        panel.add(emailT);

        panel.add(address);
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
        setSize((dim.width - 20) / 3, (dim.height - 30) / 2);
        setLocation(manager.getLocation());
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == departmentB) {
            if ("Manager".equals(departmentB.getSelectedItem())) {
                privilegeB.setEnabled(true);
                genderB.setEnabled(false);
                skillT.setEnabled(false);
                specialityT.setEnabled(false);
                privilege.setEnabled(true);
                gender.setEnabled(false);
                skill.setEnabled(false);
                speciality.setEnabled(false);

            } else if ("Catering".equals(departmentB.getSelectedItem())) {
                privilegeB.setEnabled(false);
                genderB.setEnabled(false);
                skillT.setEnabled(true);
                specialityT.setEnabled(false);
                privilege.setEnabled(false);
                gender.setEnabled(false);
                skill.setEnabled(true);
                speciality.setEnabled(false);

            } else if ("Cleaning".equals(departmentB.getSelectedItem())) {
                privilegeB.setEnabled(false);
                genderB.setEnabled(false);
                skillT.setEnabled(false);
                specialityT.setEnabled(true);
                privilege.setEnabled(false);
                gender.setEnabled(false);
                skill.setEnabled(false);
                speciality.setEnabled(true);
            } else {
                privilegeB.setEnabled(false);
                genderB.setEnabled(true);
                skillT.setEnabled(false);
                specialityT.setEnabled(false);
                privilege.setEnabled(false);
                gender.setEnabled(true);
                skill.setEnabled(false);
                speciality.setEnabled(false);
            }
        }

        if (e.getSource() == save) {
            JSONObject input = new JSONObject();
            if (!nameT.getText().isEmpty())
                input.put("name", nameT.getText());

            input.put("SSN", ssnT.getText());
            input.put("address", addressT.getText());
            input.put("job_title", titleT.getText());
            if (!ageT.getText().isEmpty())
                input.put("age", ageT.getText());

            input.put("department", departmentB.getSelectedItem().toString().toLowerCase());
            input.put("privilege", privilegeB.getSelectedItem().toString().toLowerCase());
            input.put("gender", genderB.getSelectedItem().toString().toLowerCase());
            input.put("skill", skillT.getText());
            input.put("speciality", specialityT.getText());
            input.put("phone", phoneT.getText());
            input.put("email", emailT.getText());
            input.put("peopleType", "staff");
            input.put("hotel_serving", LoginHMS.hid);
            if (!ManagerService.addNewStaff(input)) {
                Manager.opLabel.setText("Staff member not added, error in input!");
                Manager.opLabel.setForeground(Color.RED);
            } else {
                Manager.opLabel.setText("Staff member added successfully!");
                Manager.opLabel.setForeground(Color.GREEN);
            }
            this.dispose();
        }
    }

}
