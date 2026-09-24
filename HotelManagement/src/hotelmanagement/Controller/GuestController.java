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

    // ---------- Validation + save ----------

    public static class GuestResult {
        public final boolean success;
        public final String message;
        public final int guestId;
        public GuestResult(boolean success, String message, int guestId) {
            this.success = success;
            this.message = message;
            this.guestId = guestId;
        }
    }

    public GuestResult saveGuest(String firstName, String lastName, String nic,
                                 String phone, String email, String address,
                                 String nationality) {

        // --- Trim inputs ---
        firstName   = firstName   == null ? "" : firstName.trim();
        lastName    = lastName    == null ? "" : lastName.trim();
        nic         = nic         == null ? "" : nic.trim();
        phone       = phone       == null ? "" : phone.trim();
        email       = email       == null ? "" : email.trim();
        address     = address     == null ? "" : address.trim();
        nationality = nationality == null ? "" : nationality.trim();

        // --- Name: required, letters/spaces/dots/hyphens/apostrophes only ---
        if (firstName.isEmpty() || lastName.isEmpty())
            return new GuestResult(false, "First and last name are required.", -1);

        if (!firstName.matches("[A-Za-z .'-]+") || !lastName.matches("[A-Za-z .'-]+"))
            return new GuestResult(false,
                    "Names can only contain letters, spaces, dots, hyphens and apostrophes.", -1);

        if (firstName.length() > 50 || lastName.length() > 50)
            return new GuestResult(false, "Name is too long (max 50 chars).", -1);

        // --- NIC / Passport: required, 5–30 chars, letters/digits/dashes ---
        if (nic.isEmpty())
            return new GuestResult(false, "NIC / Passport number is required.", -1);
        if (!nic.matches("[A-Za-z0-9-]{5,30}"))
            return new GuestResult(false,
                    "NIC / Passport must be 5–30 letters, digits or dashes.", -1);

        // --- Phone: optional, 7–20 chars, digits + spaces + +-() ---
        if (!phone.isEmpty() && !phone.matches("[0-9+()\\- ]{7,20}"))
            return new GuestResult(false,
                    "Phone must be 7–20 digits (may include +, -, space, parentheses).", -1);

        // --- Email: optional, but must look like an email if present ---
        if (!email.isEmpty() &&
            !email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"))
            return new GuestResult(false, "Email format is invalid.", -1);

        // --- Address: optional, cap length ---
        if (address.length() > 255)
            return new GuestResult(false, "Address too long (max 255 chars).", -1);

        // --- Nationality: optional, letters + spaces only ---
        if (!nationality.isEmpty() && !nationality.matches("[A-Za-z ]+"))
            return new GuestResult(false,
                    "Nationality can only contain letters and spaces.", -1);

        // --- Duplicate NIC check ---
        if (nicExists(nic))
            return new GuestResult(false,
                    "A guest with this NIC / Passport already exists.", -1);

        // --- Save ---
        Guest g = new Guest();
        g.setFirstName(firstName);
        g.setLastName(lastName);
        g.setNic(nic);
        g.setPhone(phone);
        g.setEmail(email);
        g.setAddress(address);
        g.setNationality(nationality);

        int id = addGuest(g);
        return id > 0
            ? new GuestResult(true, "Guest saved (ID " + id + ")", id)
            : new GuestResult(false, "Failed to save guest.", -1);
    }

    private boolean nicExists(String nic) {
        String sql = "SELECT COUNT(*) FROM guests WHERE nic_or_passport = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nic);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }
}