package hotelmanagement;

import hotelmanagement.util.DBConnection;
import java.sql.Connection;

public class HotelManagement {

    public static void main(String[] args) {

        Connection connection = DBConnection.getInstance().getConnection();

        if (connection != null) {
            System.out.println("Database connection successful!");
        } else {
            System.out.println("Database connection failed!");
        }
    }
}