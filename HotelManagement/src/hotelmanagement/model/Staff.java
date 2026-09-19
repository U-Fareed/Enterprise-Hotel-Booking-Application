package hotelmanagement.model;

public class Staff {
    private int staffId;
    private String username, password, fullName, role;
    private boolean active;

    public Staff() {}

    public int getStaffId() { return staffId; }
    public void setStaffId(int staffId) { this.staffId = staffId; }
    public String getUsername() { return username; }
    public void setUsername(String v) { this.username = v; }
    public String getPassword() { return password; }
    public void setPassword(String v) { this.password = v; }
    public String getFullName() { return fullName; }
    public void setFullName(String v) { this.fullName = v; }
    public String getRole() { return role; }
    public void setRole(String v) { this.role = v; }
    public boolean isActive() { return active; }
    public void setActive(boolean v) { this.active = v; }
}