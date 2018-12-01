package view;

import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Date;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import service.FrontDeskService;
/**This class is used to create UI for a checkin field
 * 
 * @author vidhisha
 *
 */
public class NewCheckIn {

    JLabel ssnL, guestsL, checkinL, categoryL;
    public static JTextField ssnT, checkinT;
    @SuppressWarnings("rawtypes")
    JComboBox guestT, categoryT;
    JButton check;
    JPanel end;
    String[] num = { "1", "2", "3", "4" };
    //String[] category = { "Deluxe", "Economy", "Executive suite", "Presidential Suite" };
    ArrayList<String> category=new ArrayList<String>();
    JPanel panel = new JPanel(new GridLayout(12, 2));
    Date date1;

    NewCheckIn(FrontDesk f) {
        ssnL = new JLabel("SSN");
        guestsL = new JLabel("Number of Guests");
        checkinL = new JLabel("Check In Date");
        categoryL = new JLabel("Category");

        ssnT = new JTextField();
        guestT = new JComboBox<>(num);
        
        Vector<Vector<Object>> data=FrontDeskService.getCategory(LoginHMS.hid);
        int k;
        for (k = 0; k < data.size(); k++) {
            // System.out.println(data_service);
            category.add(data.get(k).get(0).toString());

        }

        categoryT = new JComboBox(category.toArray());
        
        //categoryT = new JComboBox<>(category);
        panel.add(ssnL);
        panel.add(ssnT);

        panel.add(guestsL);
        panel.add(guestT);

        panel.add(categoryL);
        panel.add(categoryT);

    }

    public JPanel createcheckin() {
        return panel;
    }

}
