package hotelmanagement.model;

public class Room {
    private int roomId;
    private String roomNumber;
    private String roomType;
    private int capacity;
    private double adultRate;
    private double childRate;
    private String status;

    public Room() {}

    public int getRoomId() { return roomId; }
    public void setRoomId(int roomId) { this.roomId = roomId; }
    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }
    public String getRoomType() { return roomType; }
    public void setRoomType(String roomType) { this.roomType = roomType; }
    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
    public double getAdultRate() { return adultRate; }
    public void setAdultRate(double adultRate) { this.adultRate = adultRate; }
    public double getChildRate() { return childRate; }
    public void setChildRate(double childRate) { this.childRate = childRate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}