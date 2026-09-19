package hotelmanagement.controller;

import hotelmanagement.exception.RoomNotAvailableException;
import hotelmanagement.util.DBConnection;
import java.sql.*;

public class BookingController {

    /**
     * Checks for date overlap, then inserts the booking.
     * Returns the new booking_id.
     */
    public int createBooking(int guestId, int roomId, String checkIn, String checkOut,
                              String paymentMethod, double totalAmount) throws RoomNotAvailableException {

        String checkSql = "SELECT COUNT(*) FROM bookings WHERE room_id=? AND status IN ('CONFIRMED','CHECKED_IN') " +
                           "AND check_in_date < ? AND check_out_date > ?";
        String insertSql = "INSERT INTO bookings (guest_id, room_id, check_in_date, check_out_date, payment_method, total_amount) " +
                            "VALUES (?,?,?,?,?,?)";

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
                ps.setString(5, paymentMethod);
                ps.setDouble(6, totalAmount);
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) return keys.getInt(1);
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return -1;
    }

    /** Guest full names checking in today. */
    public java.util.List<String> getTodaysArrivals() {
        return runNameQuery("SELECT g.first_name, g.last_name FROM bookings b JOIN guests g ON b.guest_id=g.guest_id " +
                "WHERE b.check_in_date = CURDATE() AND b.status='CONFIRMED'");
    }

    /** Guest full names checking out today. */
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

    private java.util.List<String> runNameQuery(String sql) {
        java.util.List<String> names = new java.util.ArrayList<>();
        try (Connection conn = DBConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) names.add(rs.getString("first_name") + " " + rs.getString("last_name"));
        } catch (SQLException e) { e.printStackTrace(); }
        return names;
    }
}