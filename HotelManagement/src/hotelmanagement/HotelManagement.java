package hotelmanagement;

import hotelmanagement.view.LoginForm;
import javax.swing.UIManager;
import java.awt.EventQueue;

public class HotelManagement {

    public static void main(String[] args) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        EventQueue.invokeLater(() -> new LoginForm().setVisible(true));
    }
}