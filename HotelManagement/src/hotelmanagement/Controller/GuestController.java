package hotelmanagement.controller;

import hotelmanagement.model.Guest;
import hotelmanagement.util.DBConnection;
import java.sql.*;

public class GuestController {

    /** Inserts the guest and returns the new guest_id, or -1 on failure. */
    public int addGuest(Guest g) {
        String sql = "INSERT INTO guests (first_name, last_name, nic_or_passport, phone, email, address, nationality) VALUES (?,?,?,?,?,?,?)";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, g.getFirstName());
            ps.setString(2, g.getLastName());
            ps.setString(3, g.getNic());
            ps.setString(4, g.getPhone());
            ps.setString(5, g.getEmail());
            ps.setString(6, g.getAddress());
            ps.setString(7, g.getNationality());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return -1;
    }
    
        public java.util.List<hotelmanagement.model.Guest> getAllGuests() {
        java.util.List<hotelmanagement.model.Guest> list = new java.util.ArrayList<>();
        String sql = "SELECT * FROM guests ORDER BY first_name, last_name";
        try (Connection conn = DBConnection.getInstance().getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                hotelmanagement.model.Guest g = new hotelmanagement.model.Guest();
                g.setGuestId(rs.getInt("guest_id"));
                g.setFirstName(rs.getString("first_name"));
                g.setLastName(rs.getString("last_name"));
                g.setNic(rs.getString("nic_or_passport"));
                g.setPhone(rs.getString("phone"));
                g.setEmail(rs.getString("email"));
                g.setAddress(rs.getString("address"));
                g.setNationality(rs.getString("nationality"));
                list.add(g);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
}