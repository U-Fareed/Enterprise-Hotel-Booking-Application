package hotelmanagement.util;

import hotelmanagement.model.Staff;

public class Session {
    public static Staff currentStaff;
}

/*Since DashboardFrame currently has a no-arg 
constructor, we need somewhere to stash the 
logged-in staff member so other screens 
(and the dashboard's welcome message) can read it*/