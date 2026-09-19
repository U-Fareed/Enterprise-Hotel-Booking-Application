package hotelmanagement.controller;

import hotelmanagement.model.Room;
import hotelmanagement.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoomController {

    public List<Room> getAllRooms() {
        List<Room> list = new ArrayList<>();
        String sql = "SELECT * FROM rooms ORDER BY room_number";
        try (Connection conn = DBConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public Room getRoomByNumber(String roomNumber) {
        String sql = "SELECT * FROM rooms WHERE room_number = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, roomNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public boolean addRoom(Room r) {
        String sql = "INSERT INTO rooms (room_number, room_type, capacity, rate, status) VALUES (?,?,?,?,?)";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, r.getRoomNumber());
            ps.setString(2, r.getRoomType());
            ps.setInt(3, r.getCapacity());
            ps.setDouble(4, r.getRate());
            ps.setString(5, r.getStatus());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean updateRoom(Room r) {
        String sql = "UPDATE rooms SET room_number=?, room_type=?, capacity=?, rate=?, status=? WHERE room_id=?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, r.getRoomNumber());
            ps.setString(2, r.getRoomType());
            ps.setInt(3, r.getCapacity());
            ps.setDouble(4, r.getRate());
            ps.setString(5, r.getStatus());
            ps.setInt(6, r.getRoomId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean deleteRoom(int roomId) {
        String sql = "DELETE FROM rooms WHERE room_id=?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, roomId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    private Room mapRow(ResultSet rs) throws SQLException {
        Room r = new Room();
        r.setRoomId(rs.getInt("room_id"));
        r.setRoomNumber(rs.getString("room_number"));
        r.setRoomType(rs.getString("room_type"));
        r.setCapacity(rs.getInt("capacity"));
        r.setRate(rs.getDouble("rate"));
        r.setStatus(rs.getString("status"));
        return r;
    }
}