package hotelmanagement;

import hotelmanagement.view.LoginForm;
import java.awt.EventQueue;

public class HotelManagement {

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            LoginForm login = new LoginForm();
            login.setLocationRelativeTo(null);
            login.setVisible(true);
        });
    }
}