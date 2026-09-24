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
        String sql = "INSERT INTO rooms (room_number, room_type, capacity, adult_rate, child_rate, status) VALUES (?,?,?,?,?,?)";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, r.getRoomNumber());
            ps.setString(2, r.getRoomType());
            ps.setInt(3, r.getCapacity());
            ps.setDouble(4, r.getAdultRate());
            ps.setDouble(5, r.getChildRate());
            ps.setString(6, r.getStatus());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean updateRoom(Room r) {
        String sql = "UPDATE rooms SET room_number=?, room_type=?, capacity=?, adult_rate=?, child_rate=?, status=? WHERE room_id=?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, r.getRoomNumber());
            ps.setString(2, r.getRoomType());
            ps.setInt(3, r.getCapacity());
            ps.setDouble(4, r.getAdultRate());
            ps.setDouble(5, r.getChildRate());
            ps.setString(6, r.getStatus());
            ps.setInt(7, r.getRoomId());
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
        r.setAdultRate(rs.getDouble("adult_rate"));
        r.setChildRate(rs.getDouble("child_rate"));
        r.setStatus(rs.getString("status"));
        return r;
    }

    public Room getRoomById(int roomId) {
        String sql = "SELECT * FROM rooms WHERE room_id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, roomId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public static class RoomResult {
        public final boolean success;
        public final String message;
        public RoomResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
    }

    public RoomResult saveRoom(Integer roomId, String roomNo, String type,
                               String capStr, String adultStr, String childStr,
                               String status) {
        if (roomNo == null || roomNo.trim().isEmpty() ||
            capStr == null || capStr.trim().isEmpty() ||
            adultStr == null || adultStr.trim().isEmpty() ||
            childStr == null || childStr.trim().isEmpty())
            return new RoomResult(false, "Please fill in all fields.");

        int capacity; double adultRate, childRate;
        try {
            capacity  = Integer.parseInt(capStr.trim());
            adultRate = Double.parseDouble(adultStr.trim());
            childRate = Double.parseDouble(childStr.trim());
        } catch (NumberFormatException e) {
            return new RoomResult(false, "Capacity and rates must be numbers.");
        }

        if (capacity <= 0 || adultRate <= 0 || childRate < 0)
            return new RoomResult(false,
                    "Capacity and adult rate must be positive. Child rate can be 0.");

        Room r = new Room();
        r.setRoomNumber(roomNo.trim());
        r.setRoomType(type);
        r.setCapacity(capacity);
        r.setAdultRate(adultRate);
        r.setChildRate(childRate);
        r.setStatus(status);

        boolean ok;
        if (roomId != null) {
            r.setRoomId(roomId);
            ok = updateRoom(r);
        } else {
            ok = addRoom(r);
        }
        return ok
            ? new RoomResult(true, roomId != null ? "Room updated." : "Room added.")
            : new RoomResult(false, "Failed. Room number may already exist.");
    }

    public boolean deleteByNumber(String roomNumber) {
        Room r = getRoomByNumber(roomNumber);
        return r != null && deleteRoom(r.getRoomId());
    }
}