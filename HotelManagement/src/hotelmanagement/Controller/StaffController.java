package hotelmanagement.controller;

import hotelmanagement.model.Staff;
import hotelmanagement.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StaffController {

    public Staff authenticate(String username, String password) {
        String sql = "SELECT * FROM staff WHERE username=? AND password=? AND is_active=TRUE";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public List<Staff> getAllStaff() {
        List<Staff> list = new ArrayList<>();
        String sql = "SELECT * FROM staff ORDER BY full_name";
        try (Connection conn = DBConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public boolean addStaff(Staff s) {
        String sql = "INSERT INTO staff (username, password, full_name, role, is_active) VALUES (?,?,?,?,?)";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, s.getUsername());
            ps.setString(2, s.getPassword());
            ps.setString(3, s.getFullName());
            ps.setString(4, s.getRole());
            ps.setBoolean(5, s.isActive());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean updateStaff(Staff s) {
        String sql = "UPDATE staff SET username=?, password=?, full_name=?, role=?, is_active=? WHERE staff_id=?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, s.getUsername());
            ps.setString(2, s.getPassword());
            ps.setString(3, s.getFullName());
            ps.setString(4, s.getRole());
            ps.setBoolean(5, s.isActive());
            ps.setInt(6, s.getStaffId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean deleteStaff(int staffId) {
        String sql = "DELETE FROM staff WHERE staff_id=?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, staffId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    private Staff mapRow(ResultSet rs) throws SQLException {
        Staff s = new Staff();
        s.setStaffId(rs.getInt("staff_id"));
        s.setUsername(rs.getString("username"));
        s.setPassword(rs.getString("password"));
        s.setFullName(rs.getString("full_name"));
        s.setRole(rs.getString("role"));
        s.setActive(rs.getBoolean("is_active"));
        return s;
    }
}