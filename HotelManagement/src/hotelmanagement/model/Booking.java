package hotelmanagement.model;

public class Booking {
    private int bookingId, guestId, roomId;
    private String checkInDate, checkOutDate, status, paymentMethod, paymentStatus;
    private double totalAmount;

    public Booking() {}

    public int getBookingId() { return bookingId; }
    public void setBookingId(int v) { this.bookingId = v; }
    public int getGuestId() { return guestId; }
    public void setGuestId(int v) { this.guestId = v; }
    public int getRoomId() { return roomId; }
    public void setRoomId(int v) { this.roomId = v; }
    public String getCheckInDate() { return checkInDate; }
    public void setCheckInDate(String v) { this.checkInDate = v; }
    public String getCheckOutDate() { return checkOutDate; }
    public void setCheckOutDate(String v) { this.checkOutDate = v; }
    public String getStatus() { return status; }
    public void setStatus(String v) { this.status = v; }
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String v) { this.paymentMethod = v; }
    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double v) { this.totalAmount = v; }
    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String v) { this.paymentStatus = v; }
}