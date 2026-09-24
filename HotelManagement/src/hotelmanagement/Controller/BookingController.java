package hotelmanagement.controller;

import hotelmanagement.exception.RoomNotAvailableException;
import hotelmanagement.util.DBConnection;
import java.sql.*;

public class BookingController {

    public int createBooking(int guestId, int roomId, String checkIn, String checkOut,
                          int numAdults, int numChildren,
                          String paymentMethod, double totalAmount)
        throws RoomNotAvailableException {

    String checkSql = "SELECT COUNT(*) FROM bookings WHERE room_id=? AND status IN ('CONFIRMED','CHECKED_IN') " +
                       "AND check_in_date < ? AND check_out_date > ?";
    String insertSql = "INSERT INTO bookings (guest_id, room_id, check_in_date, check_out_date, " +
                       "num_adults, num_children, payment_method, total_amount) VALUES (?,?,?,?,?,?,?,?)";

    try (Connection conn = DBConnection.getInstance().getConnection()) {

        try (PreparedStatement ps = conn.prepareStatement(checkSql)) {
            ps.setInt(1, roomId);
            ps.setString(2, checkOut);
            ps.setString(3, checkIn);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    throw new RoomNotAvailableException("This room is already booked for those dates.");
                }
            }
        }

        try (PreparedStatement ps = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, guestId);
            ps.setInt(2, roomId);
            ps.setString(3, checkIn);
            ps.setString(4, checkOut);
            ps.setInt(5, numAdults);
            ps.setInt(6, numChildren);
            ps.setString(7, paymentMethod);
            ps.setDouble(8, totalAmount);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
    } catch (SQLException e) { e.printStackTrace(); }
    return -1;
}

    public java.util.List<String> getTodaysArrivals() {
        return runNameQuery("SELECT g.first_name, g.last_name FROM bookings b JOIN guests g ON b.guest_id=g.guest_id " +
                "WHERE b.check_in_date = CURDATE() AND b.status='CONFIRMED'");
    }

    public java.util.List<String> getTodaysDepartures() {
        return runNameQuery("SELECT g.first_name, g.last_name FROM bookings b JOIN guests g ON b.guest_id=g.guest_id " +
                "WHERE b.check_out_date = CURDATE() AND b.status='CHECKED_IN'");
    }

    public double getTotalRevenue() {
        String sql = "SELECT COALESCE(SUM(amount),0) FROM payments";
        try (Connection conn = DBConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getDouble(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    public java.util.List<Object[]> getBookingsForDate(String date) {
        java.util.List<Object[]> rows = new java.util.ArrayList<>();
        String sql = "SELECT b.booking_id, g.first_name, g.last_name, r.room_number, " +
                     "b.check_in_date, b.check_out_date, b.status " +
                     "FROM bookings b " +
                     "JOIN guests g ON b.guest_id = g.guest_id " +
                     "JOIN rooms  r ON b.room_id  = r.room_id " +
                     "WHERE b.check_in_date = ? OR b.check_out_date = ? " +
                     "ORDER BY b.check_in_date";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, date);
            ps.setString(2, date);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    rows.add(new Object[]{
                        rs.getInt("booking_id"),
                        rs.getString("first_name") + " " + rs.getString("last_name"),
                        rs.getString("room_number"),
                        rs.getString("check_in_date"),
                        rs.getString("check_out_date"),
                        rs.getString("status")
                    });
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return rows;
    }

    public boolean updateBookingStatus(int bookingId, String newStatus) {
        String sql = "UPDATE bookings SET status=? WHERE booking_id=?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setInt(2, bookingId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public java.util.List<hotelmanagement.model.Room> getAvailableRooms() {
    java.util.List<hotelmanagement.model.Room> list = new java.util.ArrayList<>();
    String sql = "SELECT * FROM rooms WHERE status='AVAILABLE' ORDER BY room_number";
    try (Connection conn = DBConnection.getInstance().getConnection();
         Statement st = conn.createStatement();
         ResultSet rs = st.executeQuery(sql)) {
        while (rs.next()) {
            hotelmanagement.model.Room r = new hotelmanagement.model.Room();
            r.setRoomId(rs.getInt("room_id"));
            r.setRoomNumber(rs.getString("room_number"));
            r.setRoomType(rs.getString("room_type"));
            r.setCapacity(rs.getInt("capacity"));
            r.setAdultRate(rs.getDouble("adult_rate"));
            r.setChildRate(rs.getDouble("child_rate"));
            r.setStatus(rs.getString("status"));
            list.add(r);
        }
    } catch (SQLException e) { e.printStackTrace(); }
    return list;
}

    public boolean setRoomStatus(int roomId, String status) {
        String sql = "UPDATE rooms SET status=? WHERE room_id=?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, roomId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    private java.util.List<String> runNameQuery(String sql) {
        java.util.List<String> names = new java.util.ArrayList<>();
        try (Connection conn = DBConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) names.add(rs.getString("first_name") + " " + rs.getString("last_name"));
        } catch (SQLException e) { e.printStackTrace(); }
        return names;
    }
    public static class BookingResult {
    public final boolean success;
    public final String message;
    public final int bookingId;
    public BookingResult(boolean success, String message, int bookingId) {
        this.success = success; this.message = message; this.bookingId = bookingId;
    }
}

public double calculateTotal(int nights, int adults, int children,
                             double adultRate, double childRate) {
    if (nights <= 0 || adults < 1) return 0;
    return nights * (adults * adultRate + children * childRate);
}

public BookingResult bookRoom(int guestId, int roomId,
                              java.util.Date inDate, java.util.Date outDate,
                              int adults, int children,
                              String paymentMethod) {

    if (inDate == null || outDate == null)
        return new BookingResult(false, "Please pick both dates.", -1);
    if (!outDate.after(inDate))
        return new BookingResult(false, "Check-out must be after check-in.", -1);

    hotelmanagement.model.Room room =
            new RoomController().getRoomById(roomId);
    if (room == null)
        return new BookingResult(false, "Room not found.", -1);
    if (adults + children > room.getCapacity())
        return new BookingResult(false,
                "Room capacity is " + room.getCapacity() +
                ". You selected " + (adults + children) + ".", -1);

    long diff = (outDate.getTime() - inDate.getTime()) / (1000L * 60 * 60 * 24);
    int nights = (int) diff;
    if (nights <= 0)
        return new BookingResult(false, "Invalid date range.", -1);

    double total = calculateTotal(nights, adults, children,
            room.getAdultRate(), room.getChildRate());

    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
    try {
        int id = createBooking(guestId, roomId,
                sdf.format(inDate), sdf.format(outDate),
                adults, children, paymentMethod, total);
        return id > 0
            ? new BookingResult(true, "Booking created (ID " + id + ")", id)
            : new BookingResult(false, "Failed to save booking.", -1);
    } catch (RoomNotAvailableException ex) {
        return new BookingResult(false, ex.getMessage(), -1);
    }
}
}