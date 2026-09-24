package hotelmanagement.controller;

import hotelmanagement.model.Staff;
import hotelmanagement.util.Session;

public class LoginController {

    private final StaffController staffController = new StaffController();

    /** Returns null if fields are blank or credentials are wrong. */
    public Staff login(String username, String password) {
        if (username == null || username.trim().isEmpty()) return null;
        if (password == null || password.trim().isEmpty()) return null;

        Staff s = staffController.authenticate(username.trim(), password.trim());
        if (s != null) Session.setCurrentStaff(s);
        return s;
    }

    public void logout() {
        Session.logout();
    }
}