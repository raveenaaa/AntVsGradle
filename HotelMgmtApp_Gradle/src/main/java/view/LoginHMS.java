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
import java.sql.SQLException;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.json.JSONObject;

import dao.ContactInfo;
import dao.ContactLinks;
import dao.Database;
import dao.Hotel;
import dao.HotelPeopleLinks;
import dao.Manager;
import dao.People;
import dao.Staff;
import service.FrontDeskService;
import service.ManagerService;

/**
 * First class of UI that allow user to login on the basis of their role
 * 
 * @author kshittiz
 *
 */
public class LoginHMS extends JFrame implements ActionListener {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    JTextField ssnText;
    JComboBox<String> viewList;
    String user;
    public static int hid; // After login (staff has only one hid), person's
                           // hotel id
    // will be stored here for further reference
    public static int pid; // After login, people id will be stored here for
                           // further
    // reference

    public LoginHMS() {
        // create frame
        super("Wolf-Inn");
        setSize(350, 220);

        // create panel
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(2, 2));

        // ssn label
        JLabel ssnLabel = new JLabel("Enter SSN");
        panel.add(ssnLabel);

        // ssn text field
        ssnText = new JTextField("771310931");

        panel.add(ssnText);

        // view label
        JLabel viewLabel = new JLabel("Select duty");
        panel.add(viewLabel);

        // view combo box
        String[] privileges = { "Front Desk Representative", "Manager", "Chairman" };
        viewList = new JComboBox<String>(privileges);
        viewList.setSelectedIndex(0);
        panel.add(viewList);

        // add login button to panel
        JButton loginButton = new JButton("Login");
        loginButton.setBackground(Color.GREEN);
        loginButton.addActionListener(this);
        // set location of frame such that it always appears in middle of screen
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(dim.width / 2 - getSize().width / 2, dim.height / 2 - getSize().height / 2);

        // add panel to frame
        add(panel, BorderLayout.CENTER);
        add(loginButton, BorderLayout.SOUTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getRootPane().setDefaultButton(loginButton);
        setSize(400, 150);
        setVisible(true);

    }

    public static void main(String[] args) {
        new LoginHMS();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String ssn = ssnText.getText();
        String duty = (String) viewList.getSelectedItem();
        // Getting connection
        Connection conn = Database.getConnection();
        // Setting connection in people class
        People.setConnnection(conn);
        // Setting connection in HotelPeopleLinks
        HotelPeopleLinks.setConnnection(conn);
        // Setting pid for logged in person in class variable for future use
        pid = People.getPIDbySSN(ssn);

        if (pid == 0)
            new Error(this);
        else {
            if (duty.equals("Front Desk Representative")) {
                hid = HotelPeopleLinks.getHotelIdsByPeopleId(pid).get(0);
                user = FrontDeskService.getNameLinkedwithSSN(ssn);
                if (user == null)
                    new Error(this);
                else
                    new FrontDesk(user);
            } else if (duty.equals("Manager")) {
                hid = HotelPeopleLinks.getHotelIdsByPeopleId(pid).get(0);
                user = ManagerService.getNameLinkedwithSSN(ssn);
                if (user == null)
                    new Error(this);
                else
                    new view.Manager(user);
            } else {
                if ((duty.toLowerCase()).equals(People.getTypeBySSN(ssn)))
                    new Chairman(this);
                else
                    new Error(this);
            }
        }

        // Ending connection, Always use this function for closing connection
        Database.endConnnection(conn);

    }

}

/**
 * Class that creates Error dialog box
 * 
 * @author kshittiz
 *
 */
@SuppressWarnings("serial")
class Error extends JDialog {
    JLabel error = new JLabel("Sorry! SSN not registered for this duty");

    Error(LoginHMS login) {
        super(login, "Error", true);
        error.setForeground(Color.RED);
        add(error, BorderLayout.CENTER);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(250, 100);
        setLocation(login.getLocationOnScreen());
        setVisible(true);
    }
}

/**
 * Class that creates UI for chairman functionalities i.e. adding hotel and
 * manager
 * 
 * @author kshittiz
 *
 */
@SuppressWarnings("serial")
class Chairman extends JDialog implements ActionListener {
    JTextField hotelName;
    JTextField hotelAddress;
    JTextField hotelPhone;
    JTextField hotelEmail;
    JTextField managerName;
    JTextField managerSSN;
    JTextField managerAge;
    JTextField jobTitle;
    JTextField phone;
    JTextField email;
    JTextField managerAddress;
    JButton submit = new JButton("submit");

    Chairman(LoginHMS login) {
        super(login, "Chairman", true);
        // create panel for hotel
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 2, 0, 3));
        panel.setBorder(BorderFactory.createTitledBorder("Hotel Details"));
        // create panel for manager
        JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayout(7, 2, 0, 3));
        panel1.setBorder(BorderFactory.createTitledBorder("Assign Manager"));

        // hotel label
        JLabel hotel = new JLabel(" Hotel Name (*)");
        panel.add(hotel);

        // hotel text field
        hotelName = new JTextField();
        panel.add(hotelName);

        // hotel address
        JLabel address = new JLabel(" Hotel Address (*)");
        panel.add(address);

        // hotel address field
        hotelAddress = new JTextField();
        panel.add(hotelAddress);

        // hotel phone
        JLabel hPhone = new JLabel(" Hotel Phone Number");
        panel.add(hPhone);

        // hotel phone field
        hotelPhone = new JTextField();
        panel.add(hotelPhone);

        // hotel email
        JLabel hEmail = new JLabel(" Hotel Email ID");
        panel.add(hEmail);

        // hotel email field
        hotelEmail = new JTextField();
        panel.add(hotelEmail);

        // Manager name label
        JLabel manager = new JLabel(" Manager Name (*)");
        panel1.add(manager);

        // Manager name text field
        managerName = new JTextField();
        panel1.add(managerName);

        // Manager SSN label
        JLabel SSN = new JLabel(" SSN (*)");
        panel1.add(SSN);

        // Manager SSN field
        managerSSN = new JTextField();
        panel1.add(managerSSN);

        // Manager job label
        JLabel job = new JLabel(" Job Title (*)");
        panel1.add(job);

        // Manager job text field
        jobTitle = new JTextField();
        panel1.add(jobTitle);

        // Manager age label
        JLabel age = new JLabel(" Age");
        panel1.add(age);

        // Manager age field
        managerAge = new JTextField();
        panel1.add(managerAge);

        // Manager phone number label
        JLabel ph = new JLabel(" Phone Number");
        panel1.add(ph);

        // Manager phone number text field
        phone = new JTextField();
        panel1.add(phone);

        // Manager email label
        JLabel eID = new JLabel(" Email ID");
        panel1.add(eID);

        // Manager email field
        email = new JTextField();
        panel1.add(email);

        // Manager address label
        JLabel addr = new JLabel(" Address");
        panel1.add(addr);

        // Manager address field
        managerAddress = new JTextField();
        panel1.add(managerAddress);

        submit.setBackground(Color.ORANGE);
        submit.addActionListener(this);
        add(panel, BorderLayout.NORTH);
        add(panel1, BorderLayout.CENTER);
        add(submit, BorderLayout.SOUTH);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        ImageIcon submitIcon = new ImageIcon(new ImageIcon("images/submit.png").getImage()
                .getScaledInstance(30, 22, Image.SCALE_SMOOTH));
        submit.setIcon(submitIcon);
        submit.setBackground(Color.DARK_GRAY);
        submit.setForeground(Color.GREEN);
        getRootPane().setDefaultButton(submit);
        setSize(500, 400);
        setLocation(login.getLocationOnScreen());
        setVisible(true);
    }

    /**
     * Creating connection and executing chairman functionality to initialize
     * database with hotel and general manager. Using transaction mechanism
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        JSONObject input = new JSONObject();
        if (!managerName.getText().isEmpty())
            input.put("name", managerName.getText());

        input.put("SSN", managerSSN.getText());
        input.put("address", managerAddress.getText());
        input.put("job_title", jobTitle.getText());
        if (!managerAge.getText().isEmpty())
            input.put("age", managerAge.getText());

        input.put("department", "manager");
        input.put("privilege", "full_access");

        input.put("phone", phone.getText());
        input.put("email", email.getText());
        input.put("peopleType", "staff");

        // building connection
        Connection c = Database.getConnection();
        try {
            // starting transaction
            c.setAutoCommit(false);

            Hotel.setConnnection(c);
            People.setConnnection(c);
            HotelPeopleLinks.setConnnection(c);

            Hotel h = new Hotel();
            int hid = h.addHotel(hotelName.getText(), hotelAddress.getText());
            input.put("hotel_serving", hid);

            People p = new People();
            int pid = p.addPerson(input);
            input.put("pid", pid);
            HotelPeopleLinks hpl = new HotelPeopleLinks();
            hpl.addHotelPeopleLinks(hid, pid);

            Staff.setConnnection(c);
            p = new Staff();
            p.addPerson(input);
            Manager.setConnnection(c);
            p = new Manager();
            p.addPerson(input);

            // setting contact info of hotel and manager
            ContactInfo.setConnnection(c);
            ContactLinks.setConnnection(c);
            ContactInfo cInfo = new ContactInfo();
            ContactLinks clink = new ContactLinks();
            int contact_id_hotel = cInfo.addContactInfo(hotelPhone.getText(), hotelEmail.getText());
            clink.CreateContactLinks(hid, contact_id_hotel, "hotel");
            int contact_id_people = cInfo.addContactInfo(phone.getText(), email.getText());
            clink.CreateContactLinks(pid, contact_id_people, "people");
            // committing and ending transaction
            c.commit();

        } catch (Exception ex) {
            System.out.println(ex);
            try {
                c.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        } finally {
            Database.endConnnection(c);
        }
        this.dispose();
    }
}