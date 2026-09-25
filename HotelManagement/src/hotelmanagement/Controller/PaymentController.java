/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package hotelmanagement.Controller;


import hotelmanagement.model.Payment;
import hotelmanagement.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author USER
 */
public class PaymentController {
   
    
    public List<Object[]> getUnpaidBookings() {
        List<Object[]> rows = new ArrayList<>();
        String sql = "SELECT b.booking_id, g.first_name, g.last_name, r.room_number, " +
                     "b.check_in_date, b.check_out_date, b.total_amount " +
                     "FROM bookings b " +
                     "JOIN guests g ON b.guest_id = g.guest_id " +
                     "JOIN rooms  r ON b.room_id  = r.room_id " +
                     "WHERE b.status IN ('CONFIRMED','CHECKED_IN') " +
                     "AND (b.payment_status IS NULL OR b.payment_status <> 'PAID') " +
                     "ORDER BY b.booking_id DESC";
        try (Connection conn = DBConnection.getInstance().getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                rows.add(new Object[]{
                    rs.getInt("booking_id"),
                    rs.getString("first_name") + " " + rs.getString("last_name"),
                    rs.getString("room_number"),
                    rs.getString("check_in_date"),
                    rs.getString("check_out_date"),
                    rs.getDouble("total_amount")
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return rows;
    }

  
    public List<Payment> getPaymentsForBooking(int bookingId) {
        List<Payment> list = new ArrayList<>();
        String sql = "SELECT * FROM payments WHERE booking_id=? ORDER BY payment_id DESC";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookingId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public PaymentResult processPayment(int bookingId, double amount,
                                        String method, String reference) {
        if (amount <= 0)
            return new PaymentResult(false, "Amount must be positive.", -1);
        if (method == null || method.trim().isEmpty())
            return new PaymentResult(false, "Pick a payment method.", -1);

        String insertSql = "INSERT INTO payments (booking_id, amount, method, status, reference, paid_at) " +
                           "VALUES (?,?,?,?,?,?)";
        String updateSql = "UPDATE bookings SET payment_status='PAID' WHERE booking_id=?";

        try (Connection conn = DBConnection.getInstance().getConnection()) {
            conn.setAutoCommit(false);
            try {
                int paymentId;
                try (PreparedStatement ps = conn.prepareStatement(insertSql,
                        Statement.RETURN_GENERATED_KEYS)) {
                    ps.setInt(1, bookingId);
                    ps.setDouble(2, amount);
                    ps.setString(3, method);
                    ps.setString(4, "PAID");
                    ps.setString(5, (reference == null || reference.trim().isEmpty())
                                    ? "N/A" : reference.trim());
                    ps.setString(6, new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                                       .format(new java.util.Date()));
                    ps.executeUpdate();
                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        if (!keys.next()) throw new SQLException("No payment id returned.");
                        paymentId = keys.getInt(1);
                    }
                }
                try (PreparedStatement ps = conn.prepareStatement(updateSql)) {
                    ps.setInt(1, bookingId);
                    ps.executeUpdate();
                }
                conn.commit();
                return new PaymentResult(true, "Payment recorded.", paymentId);
            } catch (SQLException ex) {
                conn.rollback();
                ex.printStackTrace();
                return new PaymentResult(false, "Payment failed: " + ex.getMessage(), -1);
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new PaymentResult(false, "Database error.", -1);
        }
    }

    private Payment mapRow(ResultSet rs) throws SQLException {
        Payment p = new Payment();
        p.setPaymentId(rs.getInt("payment_id"));
        p.setBookingId(rs.getInt("booking_id"));
        p.setAmount(rs.getDouble("amount"));
        p.setMethod(rs.getString("method"));
        p.setStatus(rs.getString("status"));
        p.setReference(rs.getString("reference"));
        p.setPaidAt(rs.getString("paid_at"));
        return p;
    }

    public static class PaymentResult {
        public final boolean success;
        public final String message;
        public final int paymentId;
        public PaymentResult(boolean success, String message, int paymentId) {
            this.success = success;
            this.message = message;
            this.paymentId = paymentId;
        }
    }
    
    public Object[] getBookingDetails(int bookingId) {
    String sql = "SELECT g.first_name, g.last_name, r.room_number, r.room_type, " +
                 "b.check_in_date, b.check_out_date, b.total_amount " +
                 "FROM bookings b " +
                 "JOIN guests g ON b.guest_id = g.guest_id " +
                 "JOIN rooms  r ON b.room_id  = r.room_id " +
                 "WHERE b.booking_id = ?";
    try (Connection conn = DBConnection.getInstance().getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setInt(1, bookingId);
        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return new Object[]{
                    rs.getString("first_name") + " " + rs.getString("last_name"),
                    rs.getString("room_number"),
                    rs.getString("room_type"),
                    rs.getString("check_in_date"),
                    rs.getString("check_out_date"),
                    rs.getDouble("total_amount")
                };
            }
        }
    } catch (SQLException e) { e.printStackTrace(); }
    return null;
    }

}
