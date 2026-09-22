package hotelmanagement.util;

import hotelmanagement.model.Staff;

public class Session {
    private static Staff currentStaff;

    public static void setCurrentStaff(Staff s) { currentStaff = s; }
    public static Staff getCurrentStaff() { return currentStaff; }
    public static boolean isAdmin() {
        return currentStaff != null && "ADMIN".equals(currentStaff.getRole());
    }
    public static void logout() { currentStaff = null; }
}