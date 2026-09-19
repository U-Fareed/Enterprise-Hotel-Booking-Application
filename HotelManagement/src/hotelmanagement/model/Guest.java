package hotelmanagement.model;

public class Guest {
    private int guestId;
    private String firstName, lastName, nic, phone, email, address, nationality;

    public Guest() {}

    public int getGuestId() { return guestId; }
    public void setGuestId(int guestId) { this.guestId = guestId; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String v) { this.firstName = v; }
    public String getLastName() { return lastName; }
    public void setLastName(String v) { this.lastName = v; }
    public String getNic() { return nic; }
    public void setNic(String v) { this.nic = v; }
    public String getPhone() { return phone; }
    public void setPhone(String v) { this.phone = v; }
    public String getEmail() { return email; }
    public void setEmail(String v) { this.email = v; }
    public String getAddress() { return address; }
    public void setAddress(String v) { this.address = v; }
    public String getNationality() { return nationality; }
    public void setNationality(String v) { this.nationality = v; }
}