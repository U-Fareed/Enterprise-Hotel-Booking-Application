package hotelmanagement.controller;

import hotelmanagement.exception.RoomNotAvailableException;
import hotelmanagement.util.DBConnection;
import java.sql.*;

public class BookingController {

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
                r.setRate(rs.getDouble("rate"));
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
}